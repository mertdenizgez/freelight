package local.ambilight.reader;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;

/** Setup only: never starts an entertainment stream or changes a light. */
public class HueClient {
    private final SharedPreferences prefs;
    public HueClient(Context context) { this(context, "hue"); }
    HueClient(Context context, String storage) { prefs = context.getSharedPreferences(storage, 0); }
    public String host() { return prefs.getString("host", ""); }
    public boolean paired() { return !prefs.getString("application_key", "").isEmpty(); }
    public String areaId() { return prefs.getString("area_id", ""); }
    public String areaName() { return prefs.getString("area_name", ""); }
    public String source() { return prefs.getString("source", "left"); }
    public void source(String value) { prefs.edit().putString("source", value).apply(); }
    public void selectArea(String id, String name) { prefs.edit().putString("area_id", id).putString("area_name", name).apply(); }
    public void forget() { prefs.edit().remove("application_key").remove("client_key").remove("area_id").remove("area_name").remove("areas").apply(); }
    public void saveHost(String address) throws Exception {
        address = validateHost(address);
        if (!address.equals(host())) { forget(); prefs.edit().putString("host", address).apply(); }
    }
    public static String validateHost(String value) throws Exception {
        String address = value.trim();
        if (!address.matches("[0-9]{1,3}(\\.[0-9]{1,3}){3}")) throw new IOException("Bridge’in yerel IPv4 adresini girin. Örnek: 192.168.1.20");
        for (String part : address.split("\\.")) if (Integer.parseInt(part) > 255) throw new IOException("IP adresi geçersiz.");
        InetAddress ip = InetAddress.getByName(address);
        if (!ip.isSiteLocalAddress()) throw new IOException("Bridge adresi ev ağındaki yerel bir IP olmalı.");
        return address;
    }
    protected String request(String method, String path, JSONObject body, boolean authorized) throws Exception {
        String address = validateHost(host());
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(null, new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String auth) {}
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String auth) {}
        }}, new SecureRandom());
        HttpsURLConnection connection = (HttpsURLConnection) new URL("https://" + address + path).openConnection();
        connection.setSSLSocketFactory(ssl.getSocketFactory());
        connection.setHostnameVerifier((hostname, session) -> hostname.equals(address));
        connection.setConnectTimeout(4000); connection.setReadTimeout(4000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method);
        if (authorized) {
            String key = prefs.getString("application_key", "");
            if (key.isEmpty()) throw new IOException("Önce Bridge ile eşleştirin.");
            connection.setRequestProperty("hue-application-key", key);
        }
        try {
            if (body != null) {
                connection.setDoOutput(true); connection.setRequestProperty("Content-Type", "application/json");
                try (OutputStream output = connection.getOutputStream()) { output.write(body.toString().getBytes("UTF-8")); }
            }
            int code = connection.getResponseCode();
            if (code == 401 || code == 403) throw new IOException("Bridge izni geçersiz. Yeniden eşleştirin.");
            if (code != 200) throw new IOException("Bridge HTTP " + code + " döndürdü.");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (InputStream input = connection.getInputStream()) {
                byte[] buffer = new byte[4096]; int n;
                while ((n = input.read(buffer)) != -1) {
                    output.write(buffer, 0, n);
                    if (output.size() > 262144) throw new IOException("Bridge yanıtı beklenenden büyük.");
                }
            }
            return output.toString("UTF-8");
        } catch (SocketTimeoutException e) { throw new IOException("Bridge yanıt vermedi. IP adresini ve ağı kontrol edin."); }
        catch (ConnectException e) { throw new IOException("Bridge’e bağlanılamadı. Adresi ve güç bağlantısını kontrol edin."); }
        finally { connection.disconnect(); }
    }
    public void pair() throws Exception {
        JSONArray response = new JSONArray(request("POST", "/api", new JSONObject().put("devicetype", "ambilight_reader#philips_tv").put("generateclientkey", true), false));
        for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.getJSONObject(i);
            if (item.has("error")) {
                int type = item.getJSONObject("error").optInt("type");
                if (type == 101) throw new IOException("Bridge’in üzerindeki düğmeye basın, ardından Eşleştir’i tekrar seçin.");
                throw new IOException("Bridge eşleştirme hatası: " + type);
            }
            if (item.has("success")) {
                JSONObject success = item.getJSONObject("success");
                String username = success.getString("username"), clientKey = success.getString("clientkey");
                if (username.isEmpty() || !clientKey.matches("[0-9a-fA-F]{32}")) throw new IOException("Bridge gerekli bağlantı anahtarlarını vermedi.");
                prefs.edit().putString("application_key", username).putString("client_key", clientKey).apply();
                return;
            }
        }
        throw new IOException("Bridge eşleştirme yanıtı alınamadı.");
    }
    public JSONArray testAndLoadAreas() throws Exception {
        JSONObject response = new JSONObject(request("GET", "/clip/v2/resource/entertainment_configuration", null, true));
        if (response.optJSONArray("errors") != null && response.getJSONArray("errors").length() > 0) throw new IOException("Bridge eğlence alanlarını okuyamadı.");
        JSONArray data = response.getJSONArray("data"), areas = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            JSONObject area = data.getJSONObject(i);
            areas.put(new JSONObject().put("id", area.getString("id")).put("name", area.optJSONObject("metadata") == null ? "Eğlence alanı" : area.getJSONObject("metadata").optString("name", "Eğlence alanı")).put("channels", area.optJSONArray("channels") == null ? 0 : area.getJSONArray("channels").length()));
        }
        // Clear a selection if it has been removed on the Bridge.
        boolean found = false;
        for (int i = 0; i < areas.length(); i++) if (areas.getJSONObject(i).getString("id").equals(areaId())) found = true;
        if (!found) selectArea("", "");
        prefs.edit().putString("areas", areas.toString()).apply();
        return areas;
    }
    public JSONArray cachedAreas() {
        try { return new JSONArray(prefs.getString("areas", "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }
}
