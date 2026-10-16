from pathlib import Path
from xml.sax.saxutils import escape
import json
KEYS='''home menu_open tagline bluetooth bridge close_menu start_all stop_all bulbs_count app_settings language system_default apply language_hint bulb_name name_hint position_title choose_position brightness factor brightness_hint bulb_settings new_bulb cancel save save_connect position_left position_upper_left position_top position_upper_right position_right room_accessibility connected connecting disconnected streaming paused waiting start stop settings disconnect empty_bulbs scan_title scan_steps scan_help scan_hint scan scan_select scan_first registered_bulbs manage_hint scanning scan_empty registered permission bluetooth_off scan_error bridge_title bridge_paired bridge_wait bridge_ip ip_hint find pair test bridge_pair_help entertainment_area forget forgotten bridge_note no_areas area_create area_saved pairing testing network_error invalid_ip pair_button bridge_connected discovering bridge_found bridge_not_found discovery_error channels notification_title notification_text'''.split()
DATA={}
def add(code,lines):
    values=lines.strip().split('\n')
    assert len(values)==len(KEYS),(code,len(values),len(KEYS))
    DATA[code]=dict(zip(KEYS,values))
def write():
    for code,strings in DATA.items():
        folder=(Path(__file__).resolve().parents[1]/'res')/('values' if code=='en' else 'values-'+code);folder.mkdir(exist_ok=True)
        body=['<resources>']
        for k,v in strings.items():body.append('    <string name="'+k+'">'+escape(v.replace("'","\\'").replace('"','\\"'))+'</string>')
        body.append('</resources>');(folder/'strings.xml').write_text('\n'.join(body)+'\n')
    (Path(__file__).resolve().parent/'catalog.json').write_text(json.dumps(DATA,ensure_ascii=False,indent=2)+'\n')
add('en','''Home
Open menu
Your light, your space
Bluetooth bulbs
Hue Bridge
Close menu
Start all
Stop all
Bulbs · %1$d / %2$d connected
App settings
App language
System default
Apply
Use the TV language automatically, or select a language. Unsupported system languages use English. Bulb connections stay active.
Bulb name
Example: Beside the sofa
Position · facing the TV
Choose a position…
Brightness · %1$.2f×
%1$.2f×
0× off · 1× normal · 2× original · 4× maximum. The bulb’s physical limit is respected. Save to apply.
Bulb settings
New bulb
Cancel
Save
Save and connect
Left
Upper left
Top
Upper right
Right
TV and bulbs. Use left/right to select a bulb and OK to open its settings.
Connected
Connecting
Disconnected
Sync on
Sync paused
Waiting for connection
Start
Stop
Settings
Disconnect
No bulbs yet. Open Bluetooth bulbs in the left menu to add one.
Add a Bluetooth bulb
Find bulb → Choose position → Connect
Make the new bulb discoverable in the Hue app, then close the phone app. Accept pairing on the TV if asked. Existing bulbs keep syncing.
Search for a bulb to begin.
Find bulbs
Add selected bulb
Find and select a bulb first.
Saved bulbs
Start, stop and configure bulbs on Home. Disconnected bulbs stay offline until you start them again.
Searching for Hue bulbs…
No bulbs found. Check power and discoverability.
saved
Nearby devices permission is required to connect a bulb.
TV Bluetooth is off.
Could not search for bulbs. Check Bluetooth and try again.
Connect Hue Bridge
Pairing saved. You can test the connection.
Add your Bridge when it arrives.
Bridge IP address
Example: 192.168.1.20
Find on network
Pair Bridge
Test connection
Press the physical button on your Bridge, then select Pair Bridge. No keys need to be typed.
Entertainment area
Forget pairing
Bridge pairing was removed from this TV.
Bridge pairing and area selection are available. Live Bridge streaming is not yet supported. IKEA Matter bulbs are not included.
No entertainment areas yet
Create an entertainment area in the Hue app, then test the connection.
Your area choice is saved on this TV.
Pairing with Bridge…
Checking Bridge connection and areas…
Could not connect. Check power, local network and Bridge address, then try again.
Enter a valid local IPv4 address, such as 192.168.1.20.
Press the Bridge button, then try pairing again.
Bridge connected
Searching the local network for a Bridge…
Bridge found. Press its button to pair.
Bridge not found. Enter its IP address from the Hue app.
Network discovery is unavailable. Enter the IP address manually.
%1$d light channels
Freelight is running
TV colors are read locally''')
add('tr','''Ana ekran
Menüyü aç
Işığın, yaşam alanın
Bluetooth ampuller
Hue Bridge
Menüyü kapat
Tümünü başlat
Tümünü durdur
Ampuller · %1$d / %2$d bağlı
Uygulama ayarları
Uygulama dili
Sistem dili
Uygula
TV’nin dilini otomatik kullanın veya bir dil seçin. Desteklenmeyen sistem dillerinde İngilizce kullanılır. Ampul bağlantıları devam eder.
Ampul adı
Örnek: Koltuk yanı
Konum · TV’ye karşıdan bakış
Konum seçin…
Parlaklık · %1$.2f×
%1$.2f×
0× kapalı · 1× normal · 2× eski ayar · 4× en yüksek. Ampulün fiziksel sınırı aşılmaz. Kaydet ile uygulanır.
Ampul ayarları
Yeni ampul
Vazgeç
Kaydet
Kaydet ve bağlan
Sol
Sol üst çapraz
Üst
Sağ üst çapraz
Sağ
TV ve ampuller. Sağ/sol ile ampul seçin, onay tuşuyla ayarlarını açın.
Bağlı
Bağlanıyor
Bağlantı kapalı
Aktarım açık
Aktarım durdu
Bağlantı bekleniyor
Başlat
Durdur
Ayarlar
Bağlantıyı kes
Henüz ampul yok. Eklemek için sol menüden Bluetooth ampuller’i açın.
Bluetooth ampul ekle
Ampulü bul → Konum seç → Bağlan
Yeni ampulü Hue uygulamasında görünür yapın, ardından telefon uygulamasını kapatın. TV’de istenirse eşleştirmeyi onaylayın. Diğer ampuller aktarımı sürdürür.
Başlamak için ampul arayın.
Ampul ara
Seçilen ampulü ekle
Önce bir ampul arayıp seçin.
Kayıtlı ampuller
Ampulleri ana ekrandan başlatın, durdurun ve ayarlayın. Bağlantısı kesilen ampuller siz başlatana kadar bağlanmaz.
Hue ampuller aranıyor…
Ampul bulunamadı. Elektriği ve görünürlüğünü kontrol edin.
kayıtlı
Ampule bağlanmak için Yakındaki cihazlar izni gerekiyor.
TV’nin Bluetooth’u kapalı.
Ampul aranamadı. Bluetooth’u kontrol edip tekrar deneyin.
Hue Bridge bağlantısı
Eşleştirme kaydedildi. Bağlantıyı test edebilirsiniz.
Bridge geldiğinde buradan ekleyebilirsiniz.
Bridge IP adresi
Örnek: 192.168.1.20
Ağda bul
Bridge ile eşleştir
Bağlantıyı test et
Bridge’in fiziksel düğmesine basıp Bridge ile eşleştir’i seçin. Anahtar girmeniz gerekmez.
Eğlence alanı
Eşleştirmeyi unut
Bridge eşleştirmesi bu TV’den kaldırıldı.
Bridge eşleştirme ve alan seçimi hazır. Canlı Bridge aktarımı henüz desteklenmiyor. IKEA Matter ampuller dahil değildir.
Henüz eğlence alanı yok
Hue uygulamasında bir eğlence alanı oluşturup bağlantıyı test edin.
Alan seçimi bu TV’de saklanır.
Bridge eşleştiriliyor…
Bridge bağlantısı ve alanlar kontrol ediliyor…
Bağlanılamadı. Elektriği, yerel ağı ve Bridge adresini kontrol edip tekrar deneyin.
192.168.1.20 gibi geçerli bir yerel IPv4 adresi girin.
Bridge’in düğmesine basıp tekrar eşleştirin.
Bridge bağlı
Yerel ağda Bridge aranıyor…
Bridge bulundu. Eşleştirmek için düğmesine basın.
Bridge bulunamadı. Hue uygulamasındaki IP adresini girin.
Ağda arama kullanılamıyor. IP adresini elle girin.
%1$d ışık kanalı
Freelight çalışıyor
TV renkleri yerel olarak okunuyor''')
add('de','''Startseite
Menü öffnen
Dein Licht, dein Zuhause
Bluetooth-Lampen
Hue Bridge
Menü schließen
Alle starten
Alle stoppen
Lampen · %1$d / %2$d verbunden
App-Einstellungen
App-Sprache
Systemsprache
Übernehmen
TV-Sprache automatisch nutzen oder eine Sprache wählen. Nicht unterstützte Systemsprachen verwenden Englisch. Lampen bleiben verbunden.
Lampenname
Beispiel: Neben dem Sofa
Position · Blick auf den TV
Position wählen…
Helligkeit · %1$.2f×
%1$.2f×
0× aus · 1× normal · 2× bisher · 4× maximal. Die physische Lampengrenze wird eingehalten. Mit Speichern übernehmen.
Lampeneinstellungen
Neue Lampe
Abbrechen
Speichern
Speichern und verbinden
Links
Links oben
Oben
Rechts oben
Rechts
TV und Lampen. Mit Links/Rechts eine Lampe wählen, mit OK ihre Einstellungen öffnen.
Verbunden
Verbindung wird hergestellt
Getrennt
Synchronisierung aktiv
Synchronisierung pausiert
Warte auf Verbindung
Starten
Stoppen
Einstellungen
Trennen
Noch keine Lampen. Im linken Menü unter Bluetooth-Lampen hinzufügen.
Bluetooth-Lampe hinzufügen
Lampe suchen → Position wählen → Verbinden
Neue Lampe in der Hue-App sichtbar machen, dann die Handy-App schließen. Kopplung am TV bestätigen. Andere Lampen synchronisieren weiter.
Zuerst nach Lampen suchen.
Lampen suchen
Ausgewählte hinzufügen
Zuerst eine Lampe suchen und wählen.
Gespeicherte Lampen
Lampen auf der Startseite starten, stoppen und einstellen. Getrennte Lampen bleiben bis zum nächsten Start offline.
Hue-Lampen werden gesucht…
Keine Lampen gefunden. Strom und Sichtbarkeit prüfen.
gespeichert
Zum Verbinden ist die Berechtigung für Geräte in der Nähe nötig.
TV-Bluetooth ist ausgeschaltet.
Lampensuche fehlgeschlagen. Bluetooth prüfen und erneut versuchen.
Hue Bridge verbinden
Kopplung gespeichert. Verbindung kann getestet werden.
Bridge hier hinzufügen, sobald sie verfügbar ist.
IP-Adresse der Bridge
Beispiel: 192.168.1.20
Im Netzwerk suchen
Bridge koppeln
Verbindung testen
Taste auf der Bridge drücken, dann Bridge koppeln wählen. Keine Schlüssel erforderlich.
Entertainment-Bereich
Kopplung vergessen
Bridge-Kopplung auf diesem TV entfernt.
Bridge-Kopplung und Bereichsauswahl sind verfügbar. Live-Übertragung über die Bridge wird noch nicht unterstützt. IKEA-Matter-Lampen sind nicht enthalten.
Noch keine Entertainment-Bereiche
In der Hue-App einen Entertainment-Bereich erstellen, dann Verbindung testen.
Bereichsauswahl wird auf diesem TV gespeichert.
Bridge wird gekoppelt…
Bridge-Verbindung und Bereiche werden geprüft…
Verbindung fehlgeschlagen. Strom, lokales Netzwerk und Bridge-Adresse prüfen und erneut versuchen.
Gültige lokale IPv4-Adresse eingeben, etwa 192.168.1.20.
Bridge-Taste drücken und erneut koppeln.
Bridge verbunden
Bridge wird im lokalen Netzwerk gesucht…
Bridge gefunden. Zum Koppeln ihre Taste drücken.
Keine Bridge gefunden. IP-Adresse aus der Hue-App eingeben.
Netzwerksuche nicht verfügbar. IP-Adresse manuell eingeben.
%1$d Lichtkanäle
Freelight läuft
TV-Farben werden lokal gelesen''')
add('fr','''Accueil
Ouvrir le menu
Votre lumière, votre espace
Ampoules Bluetooth
Hue Bridge
Fermer le menu
Tout démarrer
Tout arrêter
Ampoules · %1$d / %2$d connectées
Paramètres de l’application
Langue de l’application
Langue du système
Appliquer
Suivez la langue du téléviseur ou choisissez une langue. L’anglais est utilisé si la langue système n’est pas prise en charge. Les ampoules restent connectées.
Nom de l’ampoule
Exemple : Près du canapé
Position · face au téléviseur
Choisir une position…
Luminosité · %1$.2f×
%1$.2f×
0× éteint · 1× normal · 2× précédent · 4× maximum. La limite physique de l’ampoule est respectée. Enregistrez pour appliquer.
Paramètres de l’ampoule
Nouvelle ampoule
Annuler
Enregistrer
Enregistrer et connecter
Gauche
En haut à gauche
Haut
En haut à droite
Droite
Téléviseur et ampoules. Utilisez gauche/droite pour choisir une ampoule et OK pour ouvrir ses paramètres.
Connectée
Connexion en cours
Déconnectée
Synchronisation active
Synchronisation en pause
En attente de connexion
Démarrer
Arrêter
Paramètres
Déconnecter
Aucune ampoule. Ajoutez-en une via Ampoules Bluetooth dans le menu de gauche.
Ajouter une ampoule Bluetooth
Chercher → Choisir la position → Connecter
Rendez la nouvelle ampoule détectable dans l’application Hue, puis fermez l’application du téléphone. Acceptez l’appairage sur le téléviseur. Les autres ampoules continuent la synchronisation.
Recherchez une ampoule pour commencer.
Chercher des ampoules
Ajouter la sélection
Cherchez et sélectionnez d’abord une ampoule.
Ampoules enregistrées
Démarrez, arrêtez et configurez les ampoules depuis l’accueil. Les ampoules déconnectées restent hors ligne jusqu’au prochain démarrage.
Recherche d’ampoules Hue…
Aucune ampoule trouvée. Vérifiez son alimentation et sa visibilité.
enregistrée
L’autorisation Appareils à proximité est nécessaire pour connecter une ampoule.
Le Bluetooth du téléviseur est désactivé.
Recherche impossible. Vérifiez le Bluetooth et réessayez.
Connecter Hue Bridge
Appairage enregistré. Vous pouvez tester la connexion.
Ajoutez votre Bridge ici lorsqu’il sera disponible.
Adresse IP du Bridge
Exemple : 192.168.1.20
Chercher sur le réseau
Appairer le Bridge
Tester la connexion
Appuyez sur le bouton physique du Bridge, puis choisissez Appairer le Bridge. Aucune clé à saisir.
Espace de loisirs
Oublier l’appairage
L’appairage du Bridge a été supprimé de ce téléviseur.
L’appairage du Bridge et la sélection d’espace sont disponibles. La synchronisation en direct via Bridge n’est pas encore prise en charge. Les ampoules IKEA Matter ne sont pas incluses.
Aucun espace de loisirs
Créez un espace de loisirs dans l’application Hue, puis testez la connexion.
Votre choix d’espace est enregistré sur ce téléviseur.
Appairage du Bridge…
Vérification de la connexion et des espaces…
Connexion impossible. Vérifiez l’alimentation, le réseau local et l’adresse du Bridge, puis réessayez.
Saisissez une adresse IPv4 locale valide, par exemple 192.168.1.20.
Appuyez sur le bouton du Bridge, puis relancez l’appairage.
Bridge connecté
Recherche d’un Bridge sur le réseau local…
Bridge trouvé. Appuyez sur son bouton pour l’appairer.
Bridge introuvable. Saisissez son adresse IP indiquée dans l’application Hue.
Recherche réseau indisponible. Saisissez l’adresse IP manuellement.
%1$d canaux lumineux
Freelight est actif
Les couleurs du téléviseur sont lues localement''')
add('es','''Inicio
Abrir menú
Tu luz, tu espacio
Bombillas Bluetooth
Hue Bridge
Cerrar menú
Iniciar todas
Detener todas
Bombillas · %1$d / %2$d conectadas
Ajustes de la aplicación
Idioma de la aplicación
Idioma del sistema
Aplicar
Usa el idioma del televisor o elige uno. Si el idioma del sistema no está disponible, se usará inglés. Las bombillas seguirán conectadas.
Nombre de la bombilla
Ejemplo: Junto al sofá
Posición · mirando al televisor
Elegir posición…
Brillo · %1$.2f×
%1$.2f×
0× apagada · 1× normal · 2× anterior · 4× máximo. Se respeta el límite físico de la bombilla. Guarda para aplicar.
Ajustes de la bombilla
Nueva bombilla
Cancelar
Guardar
Guardar y conectar
Izquierda
Arriba a la izquierda
Arriba
Arriba a la derecha
Derecha
Televisor y bombillas. Usa izquierda/derecha para elegir una bombilla y OK para abrir sus ajustes.
Conectada
Conectando
Desconectada
Sincronización activa
Sincronización pausada
Esperando conexión
Iniciar
Detener
Ajustes
Desconectar
No hay bombillas. Añade una desde Bombillas Bluetooth en el menú izquierdo.
Añadir bombilla Bluetooth
Buscar → Elegir posición → Conectar
Haz visible la nueva bombilla en la aplicación Hue y cierra la aplicación del teléfono. Acepta el emparejamiento en el televisor. Las demás bombillas seguirán sincronizadas.
Busca una bombilla para empezar.
Buscar bombillas
Añadir seleccionada
Primero busca y selecciona una bombilla.
Bombillas guardadas
Inicia, detén y configura las bombillas desde Inicio. Las desconectadas no volverán a conectarse hasta que las inicies.
Buscando bombillas Hue…
No se encontraron bombillas. Revisa la alimentación y la visibilidad.
guardada
Se necesita permiso de dispositivos cercanos para conectar una bombilla.
El Bluetooth del televisor está apagado.
No se pudieron buscar bombillas. Revisa el Bluetooth e inténtalo de nuevo.
Conectar Hue Bridge
Emparejamiento guardado. Puedes probar la conexión.
Añade aquí tu Bridge cuando esté disponible.
Dirección IP del Bridge
Ejemplo: 192.168.1.20
Buscar en la red
Emparejar Bridge
Probar conexión
Pulsa el botón físico del Bridge y elige Emparejar Bridge. No es necesario introducir claves.
Área de entretenimiento
Olvidar emparejamiento
Se eliminó el emparejamiento del Bridge de este televisor.
Están disponibles el emparejamiento del Bridge y la selección de área. La sincronización en directo mediante Bridge aún no está disponible. No incluye bombillas IKEA Matter.
No hay áreas de entretenimiento
Crea un área de entretenimiento en la aplicación Hue y prueba la conexión.
La selección de área se guarda en este televisor.
Emparejando Bridge…
Comprobando conexión y áreas del Bridge…
No se pudo conectar. Revisa la alimentación, la red local y la dirección del Bridge e inténtalo de nuevo.
Introduce una dirección IPv4 local válida, como 192.168.1.20.
Pulsa el botón del Bridge y vuelve a emparejarlo.
Bridge conectado
Buscando un Bridge en la red local…
Bridge encontrado. Pulsa su botón para emparejarlo.
No se encontró el Bridge. Introduce su IP desde la aplicación Hue.
La búsqueda de red no está disponible. Introduce la IP manualmente.
%1$d canales de luz
Freelight está activo
Los colores del televisor se leen localmente''')
add('it','''Home
Apri menu
La tua luce, il tuo spazio
Lampadine Bluetooth
Hue Bridge
Chiudi menu
Avvia tutte
Ferma tutte
Lampadine · %1$d / %2$d connesse
Impostazioni app
Lingua dell’app
Lingua di sistema
Applica
Usa la lingua della TV o scegline una. Se la lingua di sistema non è supportata, verrà usato l’inglese. Le lampadine restano connesse.
Nome della lampadina
Esempio: Vicino al divano
Posizione · di fronte alla TV
Scegli posizione…
Luminosità · %1$.2f×
%1$.2f×
0× spenta · 1× normale · 2× precedente · 4× massimo. Il limite fisico della lampadina è rispettato. Salva per applicare.
Impostazioni lampadina
Nuova lampadina
Annulla
Salva
Salva e connetti
Sinistra
In alto a sinistra
Alto
In alto a destra
Destra
TV e lampadine. Usa sinistra/destra per scegliere una lampadina e OK per aprire le impostazioni.
Connessa
Connessione in corso
Disconnessa
Sincronizzazione attiva
Sincronizzazione in pausa
In attesa di connessione
Avvia
Ferma
Impostazioni
Disconnetti
Nessuna lampadina. Aggiungine una da Lampadine Bluetooth nel menu a sinistra.
Aggiungi lampadina Bluetooth
Cerca → Scegli posizione → Connetti
Rendi visibile la nuova lampadina nell’app Hue, poi chiudi l’app sul telefono. Conferma l’abbinamento sulla TV. Le altre lampadine continuano a sincronizzarsi.
Cerca una lampadina per iniziare.
Cerca lampadine
Aggiungi selezionata
Prima cerca e seleziona una lampadina.
Lampadine salvate
Avvia, ferma e configura le lampadine dalla Home. Quelle disconnesse restano offline finché non le avvii di nuovo.
Ricerca lampadine Hue…
Nessuna lampadina trovata. Controlla alimentazione e visibilità.
salvata
Per connettere una lampadina serve il permesso Dispositivi nelle vicinanze.
Il Bluetooth della TV è disattivato.
Ricerca non riuscita. Controlla il Bluetooth e riprova.
Connetti Hue Bridge
Abbinamento salvato. Puoi verificare la connessione.
Aggiungi qui il Bridge quando sarà disponibile.
Indirizzo IP del Bridge
Esempio: 192.168.1.20
Cerca nella rete
Abbina Bridge
Verifica connessione
Premi il pulsante fisico sul Bridge, poi scegli Abbina Bridge. Non servono chiavi da digitare.
Area di intrattenimento
Dimentica abbinamento
L’abbinamento del Bridge è stato rimosso da questa TV.
Sono disponibili l’abbinamento del Bridge e la scelta dell’area. La sincronizzazione live tramite Bridge non è ancora supportata. Le lampadine IKEA Matter non sono incluse.
Nessuna area di intrattenimento
Crea un’area di intrattenimento nell’app Hue, poi verifica la connessione.
La scelta dell’area viene salvata su questa TV.
Abbinamento Bridge…
Verifica della connessione e delle aree…
Connessione non riuscita. Controlla alimentazione, rete locale e indirizzo del Bridge, poi riprova.
Inserisci un indirizzo IPv4 locale valido, ad esempio 192.168.1.20.
Premi il pulsante del Bridge e riprova l’abbinamento.
Bridge connesso
Ricerca del Bridge nella rete locale…
Bridge trovato. Premi il suo pulsante per abbinarlo.
Bridge non trovato. Inserisci il suo IP dall’app Hue.
Ricerca di rete non disponibile. Inserisci l’IP manualmente.
%1$d canali luce
Freelight è in esecuzione
I colori della TV vengono letti localmente''')
add('ru','''Главная
Открыть меню
Ваш свет, ваше пространство
Лампы Bluetooth
Hue Bridge
Закрыть меню
Запустить все
Остановить все
Лампы · подключено %1$d / %2$d
Настройки приложения
Язык приложения
Язык системы
Применить
Используйте язык телевизора или выберите другой. Если язык системы не поддерживается, используется английский. Лампы остаются подключёнными.
Название лампы
Например: У дивана
Положение · лицом к телевизору
Выберите положение…
Яркость · %1$.2f×
%1$.2f×
0× выкл. · 1× обычно · 2× прежнее · 4× максимум. Физический предел лампы не превышается. Нажмите «Сохранить».
Настройки лампы
Новая лампа
Отмена
Сохранить
Сохранить и подключить
Слева
Слева вверху
Сверху
Справа вверху
Справа
Телевизор и лампы. Выберите лампу кнопками влево/вправо и нажмите OK, чтобы открыть настройки.
Подключена
Подключение
Отключена
Синхронизация включена
Синхронизация приостановлена
Ожидание подключения
Запустить
Остановить
Настройки
Отключить
Ламп пока нет. Добавьте лампу через раздел «Лампы Bluetooth» в левом меню.
Добавить лампу Bluetooth
Найти → Выбрать положение → Подключить
Сделайте новую лампу видимой в приложении Hue, затем закройте приложение на телефоне. Подтвердите сопряжение на телевизоре. Остальные лампы продолжат синхронизацию.
Для начала найдите лампу.
Найти лампы
Добавить выбранную
Сначала найдите и выберите лампу.
Сохранённые лампы
Запускайте, останавливайте и настраивайте лампы на главной странице. Отключённые лампы не подключатся до следующего запуска.
Поиск ламп Hue…
Лампы не найдены. Проверьте питание и видимость.
сохранена
Для подключения требуется разрешение на доступ к устройствам поблизости.
Bluetooth телевизора выключен.
Не удалось найти лампы. Проверьте Bluetooth и повторите попытку.
Подключение Hue Bridge
Сопряжение сохранено. Можно проверить подключение.
Добавьте Bridge здесь, когда он появится.
IP-адрес Bridge
Например: 192.168.1.20
Найти в сети
Сопрячь Bridge
Проверить подключение
Нажмите физическую кнопку на Bridge, затем выберите «Сопрячь Bridge». Вводить ключи не нужно.
Развлекательная зона
Забыть сопряжение
Сопряжение Bridge удалено с этого телевизора.
Доступны сопряжение Bridge и выбор зоны. Передача цветов в реальном времени через Bridge пока не поддерживается. Лампы IKEA Matter не включены.
Развлекательных зон пока нет
Создайте развлекательную зону в приложении Hue и проверьте подключение.
Выбор зоны сохраняется на этом телевизоре.
Сопряжение Bridge…
Проверка подключения и зон Bridge…
Не удалось подключиться. Проверьте питание, локальную сеть и адрес Bridge и повторите попытку.
Введите действительный локальный IPv4-адрес, например 192.168.1.20.
Нажмите кнопку Bridge и повторите сопряжение.
Bridge подключён
Поиск Bridge в локальной сети…
Bridge найден. Нажмите его кнопку для сопряжения.
Bridge не найден. Введите его IP-адрес из приложения Hue.
Поиск в сети недоступен. Введите IP-адрес вручную.
Световых каналов: %1$d
Freelight работает
Цвета телевизора считываются локально''')
add('ar','''الرئيسية
فتح القائمة
إضاءتك، مساحتك
مصابيح Bluetooth
Hue Bridge
إغلاق القائمة
تشغيل الكل
إيقاف الكل
المصابيح · %1$d / %2$d متصل
إعدادات التطبيق
لغة التطبيق
لغة النظام
تطبيق
استخدم لغة التلفاز تلقائياً أو اختر لغة. تُستخدم الإنجليزية إذا كانت لغة النظام غير مدعومة. تبقى المصابيح متصلة.
اسم المصباح
مثال: بجانب الأريكة
الموقع · عند مواجهة التلفاز
اختر الموقع…
السطوع · %1$.2f×
%1$.2f×
0× إيقاف · 1× عادي · 2× السابق · 4× الأقصى. لا يتم تجاوز الحد الفعلي للمصباح. احفظ لتطبيق التغييرات.
إعدادات المصباح
مصباح جديد
إلغاء
حفظ
حفظ واتصال
يسار
أعلى اليسار
أعلى
أعلى اليمين
يمين
التلفاز والمصابيح. اختر مصباحاً بزرّي اليسار واليمين واضغط موافق لفتح إعداداته.
متصل
جارٍ الاتصال
غير متصل
المزامنة مفعّلة
المزامنة متوقفة
بانتظار الاتصال
تشغيل
إيقاف
الإعدادات
قطع الاتصال
لا توجد مصابيح بعد. أضف مصباحاً من مصابيح Bluetooth في القائمة اليسرى.
إضافة مصباح Bluetooth
البحث → اختيار الموقع → الاتصال
اجعل المصباح الجديد قابلاً للاكتشاف في تطبيق Hue ثم أغلق التطبيق على الهاتف. وافق على الاقتران في التلفاز عند الطلب. تستمر مزامنة المصابيح الأخرى.
ابحث عن مصباح للبدء.
البحث عن مصابيح
إضافة المصباح المحدد
ابحث عن مصباح وحدده أولاً.
المصابيح المحفوظة
شغّل المصابيح وأوقفها واضبطها من الرئيسية. تبقى المصابيح المفصولة غير متصلة حتى تشغّلها مجدداً.
جارٍ البحث عن مصابيح Hue…
لم يتم العثور على مصابيح. تحقق من الطاقة وقابلية الاكتشاف.
محفوظ
يلزم إذن الأجهزة القريبة للاتصال بالمصباح.
Bluetooth التلفاز متوقف.
تعذر البحث عن مصابيح. تحقق من Bluetooth وحاول مجدداً.
توصيل Hue Bridge
تم حفظ الاقتران. يمكنك اختبار الاتصال.
أضف Bridge هنا عندما يصبح متاحاً.
عنوان IP لـ Bridge
مثال: 192.168.1.20
البحث في الشبكة
إقران Bridge
اختبار الاتصال
اضغط الزر الفعلي على Bridge ثم اختر إقران Bridge. لا حاجة لإدخال مفاتيح.
منطقة ترفيه
نسيان الاقتران
أُزيل اقتران Bridge من هذا التلفاز.
يتوفر اقتران Bridge واختيار المنطقة. بث الألوان المباشر عبر Bridge غير مدعوم بعد. مصابيح IKEA Matter غير مشمولة.
لا توجد مناطق ترفيه بعد
أنشئ منطقة ترفيه في تطبيق Hue ثم اختبر الاتصال.
يُحفظ اختيار المنطقة على هذا التلفاز.
جارٍ إقران Bridge…
جارٍ التحقق من اتصال Bridge والمناطق…
تعذر الاتصال. تحقق من الطاقة والشبكة المحلية وعنوان Bridge ثم حاول مجدداً.
أدخل عنوان IPv4 محلياً صالحاً، مثل 192.168.1.20.
اضغط زر Bridge ثم أعد محاولة الاقتران.
Bridge متصل
جارٍ البحث عن Bridge في الشبكة المحلية…
تم العثور على Bridge. اضغط زره للاقتران.
لم يتم العثور على Bridge. أدخل عنوان IP من تطبيق Hue.
البحث في الشبكة غير متاح. أدخل عنوان IP يدوياً.
%1$d قنوات إضاءة
Freelight قيد التشغيل
تُقرأ ألوان التلفاز محلياً''')
add('pt','''Início
Abrir menu
Sua luz, seu espaço
Lâmpadas Bluetooth
Hue Bridge
Fechar menu
Iniciar todas
Parar todas
Lâmpadas · %1$d / %2$d conectadas
Configurações do app
Idioma do app
Idioma do sistema
Aplicar
Use o idioma da TV ou escolha outro. Se o idioma do sistema não for compatível, será usado inglês. As lâmpadas permanecem conectadas.
Nome da lâmpada
Exemplo: Ao lado do sofá
Posição · de frente para a TV
Escolher posição…
Brilho · %1$.2f×
%1$.2f×
0× desligada · 1× normal · 2× anterior · 4× máximo. O limite físico da lâmpada é respeitado. Salve para aplicar.
Configurações da lâmpada
Nova lâmpada
Cancelar
Salvar
Salvar e conectar
Esquerda
Superior esquerda
Acima
Superior direita
Direita
TV e lâmpadas. Use esquerda/direita para escolher uma lâmpada e OK para abrir as configurações.
Conectada
Conectando
Desconectada
Sincronização ativa
Sincronização pausada
Aguardando conexão
Iniciar
Parar
Configurações
Desconectar
Nenhuma lâmpada ainda. Adicione uma em Lâmpadas Bluetooth no menu esquerdo.
Adicionar lâmpada Bluetooth
Buscar → Escolher posição → Conectar
Torne a nova lâmpada detectável no app Hue e feche o app no celular. Confirme o pareamento na TV. As outras lâmpadas continuam sincronizando.
Busque uma lâmpada para começar.
Buscar lâmpadas
Adicionar selecionada
Primeiro busque e selecione uma lâmpada.
Lâmpadas salvas
Inicie, pare e configure as lâmpadas no Início. As desconectadas permanecem offline até serem iniciadas novamente.
Buscando lâmpadas Hue…
Nenhuma lâmpada encontrada. Confira a energia e a visibilidade.
salva
A permissão de dispositivos próximos é necessária para conectar uma lâmpada.
O Bluetooth da TV está desligado.
Não foi possível buscar lâmpadas. Confira o Bluetooth e tente novamente.
Conectar Hue Bridge
Pareamento salvo. Você pode testar a conexão.
Adicione seu Bridge aqui quando estiver disponível.
Endereço IP do Bridge
Exemplo: 192.168.1.20
Buscar na rede
Parear Bridge
Testar conexão
Pressione o botão físico do Bridge e escolha Parear Bridge. Não é necessário digitar chaves.
Área de entretenimento
Esquecer pareamento
O pareamento do Bridge foi removido desta TV.
O pareamento do Bridge e a seleção de área estão disponíveis. A sincronização ao vivo pelo Bridge ainda não é compatível. Lâmpadas IKEA Matter não estão incluídas.
Nenhuma área de entretenimento
Crie uma área de entretenimento no app Hue e teste a conexão.
A seleção da área é salva nesta TV.
Pareando Bridge…
Verificando conexão e áreas do Bridge…
Não foi possível conectar. Confira a energia, a rede local e o endereço do Bridge e tente novamente.
Digite um endereço IPv4 local válido, como 192.168.1.20.
Pressione o botão do Bridge e tente parear novamente.
Bridge conectado
Buscando um Bridge na rede local…
Bridge encontrado. Pressione o botão para parear.
Bridge não encontrado. Digite o IP mostrado no app Hue.
Busca de rede indisponível. Digite o IP manualmente.
%1$d canais de luz
Freelight em execução
As cores da TV são lidas localmente''')
add('nl','''Start
Menu openen
Jouw licht, jouw ruimte
Bluetooth-lampen
Hue Bridge
Menu sluiten
Alles starten
Alles stoppen
Lampen · %1$d / %2$d verbonden
App-instellingen
App-taal
Systeemtaal
Toepassen
Gebruik automatisch de TV-taal of kies een taal. Niet-ondersteunde systeemtalen gebruiken Engels. Lampen blijven verbonden.
Lampnaam
Voorbeeld: Naast de bank
Positie · tegenover de TV
Kies een positie…
Helderheid · %1$.2f×
%1$.2f×
0× uit · 1× normaal · 2× eerder · 4× maximaal. De fysieke lamplimiet blijft gelden. Sla op om toe te passen.
Lampinstellingen
Nieuwe lamp
Annuleren
Opslaan
Opslaan en verbinden
Links
Linksboven
Boven
Rechtsboven
Rechts
TV en lampen. Kies een lamp met links/rechts en druk op OK voor de instellingen.
Verbonden
Verbinden
Niet verbonden
Synchronisatie aan
Synchronisatie gepauzeerd
Wachten op verbinding
Starten
Stoppen
Instellingen
Verbinding verbreken
Nog geen lampen. Voeg een lamp toe via Bluetooth-lampen in het linkermenu.
Bluetooth-lamp toevoegen
Zoeken → Positie kiezen → Verbinden
Maak de nieuwe lamp zichtbaar in de Hue-app en sluit de telefoonapp. Bevestig de koppeling op de TV. Andere lampen blijven synchroniseren.
Zoek eerst een lamp.
Lampen zoeken
Geselecteerde toevoegen
Zoek en selecteer eerst een lamp.
Opgeslagen lampen
Start, stop en configureer lampen op Start. Lampen zonder verbinding blijven offline tot je ze opnieuw start.
Hue-lampen zoeken…
Geen lampen gevonden. Controleer stroom en zichtbaarheid.
opgeslagen
Toestemming voor apparaten in de buurt is nodig om te verbinden.
TV-Bluetooth staat uit.
Zoeken mislukt. Controleer Bluetooth en probeer opnieuw.
Hue Bridge verbinden
Koppeling opgeslagen. Je kunt de verbinding testen.
Voeg je Bridge hier toe zodra deze beschikbaar is.
IP-adres van de Bridge
Voorbeeld: 192.168.1.20
Zoeken op netwerk
Bridge koppelen
Verbinding testen
Druk op de fysieke knop van de Bridge en kies Bridge koppelen. Je hoeft geen sleutels in te voeren.
Entertainmentruimte
Koppeling vergeten
Bridge-koppeling verwijderd van deze TV.
Bridge-koppeling en ruimteselectie zijn beschikbaar. Live synchronisatie via Bridge wordt nog niet ondersteund. IKEA Matter-lampen zijn niet inbegrepen.
Nog geen entertainmentruimtes
Maak een entertainmentruimte in de Hue-app en test de verbinding.
Je ruimtekeuze wordt op deze TV opgeslagen.
Bridge koppelen…
Bridge-verbinding en ruimtes controleren…
Verbinden mislukt. Controleer stroom, lokaal netwerk en Bridge-adres en probeer opnieuw.
Voer een geldig lokaal IPv4-adres in, zoals 192.168.1.20.
Druk op de Bridge-knop en probeer opnieuw te koppelen.
Bridge verbonden
Bridge zoeken op het lokale netwerk…
Bridge gevonden. Druk op de knop om te koppelen.
Bridge niet gevonden. Voer het IP-adres uit de Hue-app in.
Netwerkzoeken niet beschikbaar. Voer het IP-adres handmatig in.
%1$d lichtkanalen
Freelight is actief
TV-kleuren worden lokaal gelezen''')
add('pl','''Start
Otwórz menu
Twoje światło, twoja przestrzeń
Żarówki Bluetooth
Hue Bridge
Zamknij menu
Uruchom wszystkie
Zatrzymaj wszystkie
Żarówki · połączono %1$d / %2$d
Ustawienia aplikacji
Język aplikacji
Język systemu
Zastosuj
Użyj języka telewizora lub wybierz inny. Jeśli język systemu nie jest obsługiwany, zostanie użyty angielski. Żarówki pozostają połączone.
Nazwa żarówki
Przykład: Przy kanapie
Pozycja · patrząc na telewizor
Wybierz pozycję…
Jasność · %1$.2f×
%1$.2f×
0× wył. · 1× normalnie · 2× poprzednio · 4× maksimum. Fizyczny limit żarówki jest zachowany. Zapisz, aby zastosować.
Ustawienia żarówki
Nowa żarówka
Anuluj
Zapisz
Zapisz i połącz
Lewa
Lewy górny róg
Góra
Prawy górny róg
Prawa
Telewizor i żarówki. Wybierz żarówkę przyciskami lewo/prawo i naciśnij OK, aby otworzyć ustawienia.
Połączona
Łączenie
Rozłączona
Synchronizacja aktywna
Synchronizacja wstrzymana
Oczekiwanie na połączenie
Uruchom
Zatrzymaj
Ustawienia
Rozłącz
Brak żarówek. Dodaj żarówkę w sekcji Żarówki Bluetooth w lewym menu.
Dodaj żarówkę Bluetooth
Znajdź → Wybierz pozycję → Połącz
Włącz widoczność nowej żarówki w aplikacji Hue, a następnie zamknij aplikację na telefonie. Potwierdź parowanie na telewizorze. Pozostałe żarówki nadal się synchronizują.
Najpierw wyszukaj żarówkę.
Szukaj żarówek
Dodaj wybraną
Najpierw wyszukaj i wybierz żarówkę.
Zapisane żarówki
Uruchamiaj, zatrzymuj i konfiguruj żarówki na ekranie startowym. Rozłączone żarówki pozostają offline do ponownego uruchomienia.
Wyszukiwanie żarówek Hue…
Nie znaleziono żarówek. Sprawdź zasilanie i widoczność.
zapisana
Do połączenia z żarówką wymagane jest uprawnienie Urządzenia w pobliżu.
Bluetooth telewizora jest wyłączony.
Wyszukiwanie nie powiodło się. Sprawdź Bluetooth i spróbuj ponownie.
Połącz Hue Bridge
Parowanie zapisano. Możesz sprawdzić połączenie.
Dodaj Bridge tutaj, gdy będzie dostępny.
Adres IP Bridge
Przykład: 192.168.1.20
Szukaj w sieci
Sparuj Bridge
Sprawdź połączenie
Naciśnij fizyczny przycisk Bridge, a następnie wybierz Sparuj Bridge. Nie trzeba wpisywać kluczy.
Obszar rozrywki
Zapomnij parowanie
Parowanie Bridge usunięto z tego telewizora.
Dostępne są parowanie Bridge i wybór obszaru. Synchronizacja na żywo przez Bridge nie jest jeszcze obsługiwana. Żarówki IKEA Matter nie są uwzględnione.
Brak obszarów rozrywki
Utwórz obszar rozrywki w aplikacji Hue i sprawdź połączenie.
Wybór obszaru jest zapisywany na tym telewizorze.
Parowanie Bridge…
Sprawdzanie połączenia i obszarów Bridge…
Nie można połączyć. Sprawdź zasilanie, sieć lokalną i adres Bridge, a następnie spróbuj ponownie.
Wpisz prawidłowy lokalny adres IPv4, np. 192.168.1.20.
Naciśnij przycisk Bridge i ponów parowanie.
Bridge połączony
Wyszukiwanie Bridge w sieci lokalnej…
Znaleziono Bridge. Naciśnij jego przycisk, aby sparować.
Nie znaleziono Bridge. Wpisz adres IP z aplikacji Hue.
Wyszukiwanie sieciowe jest niedostępne. Wpisz adres IP ręcznie.
Kanały światła: %1$d
Freelight działa
Kolory telewizora są odczytywane lokalnie''')
add('uk','''Головна
Відкрити меню
Ваше світло, ваш простір
Лампи Bluetooth
Hue Bridge
Закрити меню
Запустити всі
Зупинити всі
Лампи · підключено %1$d / %2$d
Налаштування застосунку
Мова застосунку
Мова системи
Застосувати
Використовуйте мову телевізора або виберіть іншу. Якщо мова системи не підтримується, використовується англійська. Лампи залишаються підключеними.
Назва лампи
Наприклад: Біля дивана
Розташування · обличчям до телевізора
Виберіть розташування…
Яскравість · %1$.2f×
%1$.2f×
0× вимк. · 1× звичайно · 2× попереднє · 4× максимум. Фізична межа лампи зберігається. Натисніть «Зберегти».
Налаштування лампи
Нова лампа
Скасувати
Зберегти
Зберегти й підключити
Ліворуч
Угорі ліворуч
Угорі
Угорі праворуч
Праворуч
Телевізор і лампи. Виберіть лампу кнопками ліворуч/праворуч і натисніть OK, щоб відкрити налаштування.
Підключена
Підключення
Відключена
Синхронізацію ввімкнено
Синхронізацію призупинено
Очікування підключення
Запустити
Зупинити
Налаштування
Відключити
Ламп ще немає. Додайте лампу через розділ «Лампи Bluetooth» у лівому меню.
Додати лампу Bluetooth
Знайти → Вибрати розташування → Підключити
Зробіть нову лампу видимою в застосунку Hue, потім закрийте застосунок на телефоні. Підтвердьте сполучення на телевізорі. Інші лампи продовжать синхронізацію.
Спочатку знайдіть лампу.
Знайти лампи
Додати вибрану
Спочатку знайдіть і виберіть лампу.
Збережені лампи
Запускайте, зупиняйте й налаштовуйте лампи на головній сторінці. Відключені лампи залишаються офлайн до наступного запуску.
Пошук ламп Hue…
Лампи не знайдено. Перевірте живлення та видимість.
збережена
Для підключення потрібен дозвіл на доступ до пристроїв поблизу.
Bluetooth телевізора вимкнено.
Не вдалося знайти лампи. Перевірте Bluetooth і повторіть спробу.
Підключити Hue Bridge
Сполучення збережено. Можна перевірити підключення.
Додайте Bridge тут, коли він буде доступний.
IP-адреса Bridge
Наприклад: 192.168.1.20
Знайти в мережі
Сполучити Bridge
Перевірити підключення
Натисніть фізичну кнопку Bridge, потім виберіть «Сполучити Bridge». Вводити ключі не потрібно.
Розважальна зона
Забути сполучення
Сполучення Bridge видалено з цього телевізора.
Доступні сполучення Bridge і вибір зони. Синхронізація наживо через Bridge ще не підтримується. Лампи IKEA Matter не включені.
Розважальних зон ще немає
Створіть розважальну зону в застосунку Hue та перевірте підключення.
Вибір зони зберігається на цьому телевізорі.
Сполучення Bridge…
Перевірка підключення та зон Bridge…
Не вдалося підключитися. Перевірте живлення, локальну мережу й адресу Bridge та повторіть спробу.
Введіть дійсну локальну IPv4-адресу, наприклад 192.168.1.20.
Натисніть кнопку Bridge й повторіть сполучення.
Bridge підключено
Пошук Bridge у локальній мережі…
Bridge знайдено. Натисніть його кнопку для сполучення.
Bridge не знайдено. Введіть IP-адресу із застосунку Hue.
Пошук у мережі недоступний. Введіть IP-адресу вручну.
Світлових каналів: %1$d
Freelight працює
Кольори телевізора зчитуються локально''')
add('zh','''主页
打开菜单
你的灯光，你的空间
蓝牙灯泡
Hue Bridge
关闭菜单
全部启动
全部停止
灯泡 · %1$d / %2$d 已连接
应用设置
应用语言
跟随系统
应用
自动使用电视语言，或手动选择语言。系统语言不受支持时使用英语。灯泡连接不会中断。
灯泡名称
例如：沙发旁
位置 · 面向电视
选择位置…
亮度 · %1$.2f×
%1$.2f×
0× 关闭 · 1× 标准 · 2× 原设置 · 4× 最大。不超过灯泡的物理亮度限制。保存后生效。
灯泡设置
新灯泡
取消
保存
保存并连接
左侧
左上方
上方
右上方
右侧
电视和灯泡。使用左右键选择灯泡，按确定打开设置。
已连接
正在连接
已断开
同步已开启
同步已暂停
等待连接
启动
停止
设置
断开连接
尚未添加灯泡。请从左侧菜单的“蓝牙灯泡”中添加。
添加蓝牙灯泡
查找灯泡 → 选择位置 → 连接
在 Hue 应用中让新灯泡可被发现，然后关闭手机应用。如电视提示配对，请确认。其他灯泡将继续同步。
先搜索灯泡。
搜索灯泡
添加所选灯泡
请先搜索并选择一个灯泡。
已保存的灯泡
在主页启动、停止和设置灯泡。断开的灯泡在再次启动前不会重新连接。
正在搜索 Hue 灯泡…
未找到灯泡。请检查电源和可发现状态。
已保存
连接灯泡需要“附近设备”权限。
电视蓝牙已关闭。
无法搜索灯泡。请检查蓝牙后重试。
连接 Hue Bridge
配对已保存，可以测试连接。
Bridge 准备好后可在此添加。
Bridge IP 地址
例如：192.168.1.20
在网络中查找
配对 Bridge
测试连接
按下 Bridge 上的实体按钮，然后选择“配对 Bridge”。无需输入密钥。
娱乐区域
忘记配对
已从此电视移除 Bridge 配对。
已支持 Bridge 配对和区域选择，暂不支持通过 Bridge 实时同步。不包含宜家 Matter 灯泡。
尚无娱乐区域
在 Hue 应用中创建娱乐区域，然后测试连接。
区域选择将保存在此电视上。
正在配对 Bridge…
正在检查 Bridge 连接和区域…
无法连接。请检查电源、本地网络和 Bridge 地址后重试。
请输入有效的本地 IPv4 地址，例如 192.168.1.20。
请按下 Bridge 按钮后重新配对。
Bridge 已连接
正在本地网络中搜索 Bridge…
已找到 Bridge。请按下其按钮进行配对。
未找到 Bridge。请输入 Hue 应用中显示的 IP 地址。
网络搜索不可用。请手动输入 IP 地址。
%1$d 个灯光通道
Freelight 正在运行
正在本地读取电视颜色''')
add('ja','''ホーム
メニューを開く
あなたの光、あなたの空間
Bluetooth電球
Hue Bridge
メニューを閉じる
すべて開始
すべて停止
電球 · %1$d / %2$d 接続済み
アプリ設定
アプリの言語
システム言語
適用
テレビの言語を使うか、言語を選択してください。対応していないシステム言語では英語を使います。電球の接続は維持されます。
電球の名前
例：ソファの横
位置 · テレビに向かって
位置を選択…
明るさ · %1$.2f×
%1$.2f×
0× オフ · 1× 標準 · 2× 以前の設定 · 4× 最大。電球本体の上限は超えません。保存すると適用されます。
電球の設定
新しい電球
キャンセル
保存
保存して接続
左
左上
上
右上
右
テレビと電球。左右キーで電球を選び、決定キーで設定を開きます。
接続済み
接続中
未接続
同期中
同期を一時停止
接続待ち
開始
停止
設定
接続を解除
電球がありません。左メニューの「Bluetooth電球」から追加してください。
Bluetooth電球を追加
電球を検索 → 位置を選択 → 接続
Hueアプリで新しい電球を検出可能にしてから、スマートフォンのアプリを閉じます。テレビに表示されたらペア設定を許可してください。他の電球の同期は続きます。
まず電球を検索してください。
電球を検索
選択した電球を追加
先に電球を検索して選択してください。
保存済みの電球
ホームで電球の開始、停止、設定ができます。接続を解除した電球は再度開始するまで接続しません。
Hue電球を検索中…
電球が見つかりません。電源と検出可能な状態を確認してください。
保存済み
電球の接続には「付近のデバイス」の権限が必要です。
テレビのBluetoothがオフです。
電球を検索できません。Bluetoothを確認して再試行してください。
Hue Bridgeを接続
ペア設定を保存しました。接続をテストできます。
Bridgeの準備ができたら、ここで追加できます。
BridgeのIPアドレス
例：192.168.1.20
ネットワークで検索
Bridgeをペア設定
接続をテスト
Bridge本体のボタンを押してから「Bridgeをペア設定」を選びます。キーの入力は不要です。
エンターテインメントエリア
ペア設定を削除
このテレビからBridgeのペア設定を削除しました。
Bridgeのペア設定とエリア選択に対応しています。Bridge経由のリアルタイム同期には未対応です。IKEA Matter電球は含まれません。
エリアがありません
Hueアプリでエンターテインメントエリアを作成してから、接続をテストしてください。
エリアの選択はこのテレビに保存されます。
Bridgeをペア設定中…
Bridgeの接続とエリアを確認中…
接続できません。電源、ローカルネットワーク、Bridgeのアドレスを確認して再試行してください。
192.168.1.20など、有効なローカルIPv4アドレスを入力してください。
Bridgeのボタンを押して、もう一度ペア設定してください。
Bridge接続済み
ローカルネットワークでBridgeを検索中…
Bridgeが見つかりました。本体のボタンを押してペア設定してください。
Bridgeが見つかりません。Hueアプリに表示されたIPアドレスを入力してください。
ネットワーク検索を使用できません。IPアドレスを手動入力してください。
ライトチャンネル：%1$d
Freelight実行中
テレビの色をローカルで読み取り中''')
add('ko','''홈
메뉴 열기
나만의 빛, 나만의 공간
Bluetooth 전구
Hue Bridge
메뉴 닫기
모두 시작
모두 중지
전구 · %1$d / %2$d 연결됨
앱 설정
앱 언어
시스템 언어
적용
TV 언어를 자동으로 사용하거나 언어를 선택하세요. 지원하지 않는 시스템 언어는 영어로 표시됩니다. 전구 연결은 유지됩니다.
전구 이름
예: 소파 옆
위치 · TV를 바라볼 때
위치 선택…
밝기 · %1$.2f×
%1$.2f×
0× 끄기 · 1× 기본 · 2× 이전 설정 · 4× 최대. 전구의 물리적 한도는 넘지 않습니다. 저장하면 적용됩니다.
전구 설정
새 전구
취소
저장
저장 후 연결
왼쪽
왼쪽 위
위
오른쪽 위
오른쪽
TV와 전구입니다. 좌우 키로 전구를 선택하고 확인 키로 설정을 여세요.
연결됨
연결 중
연결 끊김
동기화 켜짐
동기화 일시 중지
연결 대기 중
시작
중지
설정
연결 해제
아직 전구가 없습니다. 왼쪽 메뉴의 Bluetooth 전구에서 추가하세요.
Bluetooth 전구 추가
전구 찾기 → 위치 선택 → 연결
Hue 앱에서 새 전구를 검색 가능하게 한 뒤 휴대폰 앱을 닫으세요. TV에 요청이 나타나면 페어링을 허용하세요. 다른 전구는 계속 동기화됩니다.
먼저 전구를 검색하세요.
전구 찾기
선택한 전구 추가
먼저 전구를 검색하고 선택하세요.
저장된 전구
홈에서 전구를 시작, 중지하고 설정하세요. 연결을 해제한 전구는 다시 시작할 때까지 연결되지 않습니다.
Hue 전구 검색 중…
전구를 찾지 못했습니다. 전원과 검색 가능 상태를 확인하세요.
저장됨
전구를 연결하려면 근처 기기 권한이 필요합니다.
TV Bluetooth가 꺼져 있습니다.
전구를 검색하지 못했습니다. Bluetooth를 확인하고 다시 시도하세요.
Hue Bridge 연결
페어링이 저장되었습니다. 연결을 테스트할 수 있습니다.
Bridge가 준비되면 여기서 추가하세요.
Bridge IP 주소
예: 192.168.1.20
네트워크에서 찾기
Bridge 페어링
연결 테스트
Bridge의 물리 버튼을 누른 뒤 Bridge 페어링을 선택하세요. 키를 입력할 필요는 없습니다.
엔터테인먼트 영역
페어링 삭제
이 TV에서 Bridge 페어링을 삭제했습니다.
Bridge 페어링과 영역 선택을 사용할 수 있습니다. Bridge 실시간 동기화는 아직 지원하지 않습니다. IKEA Matter 전구는 포함되지 않습니다.
아직 영역이 없습니다
Hue 앱에서 엔터테인먼트 영역을 만든 뒤 연결을 테스트하세요.
영역 선택은 이 TV에 저장됩니다.
Bridge 페어링 중…
Bridge 연결과 영역 확인 중…
연결할 수 없습니다. 전원, 로컬 네트워크와 Bridge 주소를 확인하고 다시 시도하세요.
192.168.1.20 같은 유효한 로컬 IPv4 주소를 입력하세요.
Bridge 버튼을 누르고 다시 페어링하세요.
Bridge 연결됨
로컬 네트워크에서 Bridge 검색 중…
Bridge를 찾았습니다. 버튼을 눌러 페어링하세요.
Bridge를 찾지 못했습니다. Hue 앱에 표시된 IP 주소를 입력하세요.
네트워크 검색을 사용할 수 없습니다. IP 주소를 직접 입력하세요.
조명 채널 %1$d개
Freelight 실행 중
TV 색상을 로컬에서 읽는 중''')
add('hi','''होम
मेन्यू खोलें
आपकी रोशनी, आपकी जगह
Bluetooth बल्ब
Hue Bridge
मेन्यू बंद करें
सभी शुरू करें
सभी रोकें
बल्ब · %1$d / %2$d कनेक्ट हैं
ऐप सेटिंग
ऐप की भाषा
सिस्टम की भाषा
लागू करें
टीवी की भाषा अपने आप इस्तेमाल करें या भाषा चुनें। सिस्टम की भाषा उपलब्ध न होने पर अंग्रेज़ी इस्तेमाल होगी। बल्ब कनेक्ट रहेंगे।
बल्ब का नाम
उदाहरण: सोफ़े के पास
स्थान · टीवी की ओर देखते हुए
स्थान चुनें…
चमक · %1$.2f×
%1$.2f×
0× बंद · 1× सामान्य · 2× पिछली सेटिंग · 4× अधिकतम। बल्ब की वास्तविक सीमा नहीं लांघी जाती। लागू करने के लिए सेव करें।
बल्ब की सेटिंग
नया बल्ब
रद्द करें
सेव करें
सेव करके कनेक्ट करें
बाएँ
ऊपर बाएँ
ऊपर
ऊपर दाएँ
दाएँ
टीवी और बल्ब। बाएँ/दाएँ बटन से बल्ब चुनें और OK से उसकी सेटिंग खोलें।
कनेक्ट है
कनेक्ट हो रहा है
डिसकनेक्ट है
सिंक चालू है
सिंक रुका है
कनेक्शन का इंतज़ार
शुरू करें
रोकें
सेटिंग
डिसकनेक्ट करें
अभी कोई बल्ब नहीं है। बाएँ मेन्यू में Bluetooth बल्ब से जोड़ें।
Bluetooth बल्ब जोड़ें
बल्ब खोजें → स्थान चुनें → कनेक्ट करें
Hue ऐप में नए बल्ब को खोजने योग्य बनाएँ और फ़ोन का ऐप बंद करें। टीवी पर अनुरोध आने पर पेयरिंग स्वीकार करें। अन्य बल्ब सिंक होते रहेंगे।
शुरू करने के लिए बल्ब खोजें।
बल्ब खोजें
चुना हुआ बल्ब जोड़ें
पहले बल्ब खोजें और चुनें।
सेव किए गए बल्ब
होम से बल्ब शुरू करें, रोकें और सेट करें। डिसकनेक्ट किए गए बल्ब दोबारा शुरू करने तक ऑफ़लाइन रहेंगे।
Hue बल्ब खोजे जा रहे हैं…
कोई बल्ब नहीं मिला। बिजली और खोजने योग्य स्थिति जाँचें।
सेव किया गया
बल्ब कनेक्ट करने के लिए आस-पास के डिवाइस की अनुमति चाहिए।
टीवी का Bluetooth बंद है।
बल्ब नहीं खोज सके। Bluetooth जाँचकर फिर कोशिश करें।
Hue Bridge कनेक्ट करें
पेयरिंग सेव हो गई। आप कनेक्शन जाँच सकते हैं।
Bridge उपलब्ध होने पर यहाँ जोड़ें।
Bridge का IP पता
उदाहरण: 192.168.1.20
नेटवर्क में खोजें
Bridge पेयर करें
कनेक्शन जाँचें
Bridge का बटन दबाएँ, फिर Bridge पेयर करें चुनें। कोई कुंजी टाइप नहीं करनी है।
मनोरंजन क्षेत्र
पेयरिंग भूलें
इस टीवी से Bridge की पेयरिंग हटा दी गई।
Bridge पेयरिंग और क्षेत्र चुनना उपलब्ध है। Bridge से लाइव सिंक अभी समर्थित नहीं है। IKEA Matter बल्ब शामिल नहीं हैं।
अभी कोई मनोरंजन क्षेत्र नहीं है
Hue ऐप में मनोरंजन क्षेत्र बनाकर कनेक्शन जाँचें।
क्षेत्र की पसंद इस टीवी पर सेव होती है।
Bridge पेयर हो रहा है…
Bridge कनेक्शन और क्षेत्र जाँचे जा रहे हैं…
कनेक्ट नहीं कर सके। बिजली, स्थानीय नेटवर्क और Bridge का पता जाँचकर फिर कोशिश करें।
सही स्थानीय IPv4 पता डालें, जैसे 192.168.1.20।
Bridge का बटन दबाकर फिर पेयर करें।
Bridge कनेक्ट है
स्थानीय नेटवर्क में Bridge खोज रहे हैं…
Bridge मिल गया। पेयर करने के लिए उसका बटन दबाएँ।
Bridge नहीं मिला। Hue ऐप से उसका IP पता डालें।
नेटवर्क खोज उपलब्ध नहीं है। IP पता खुद डालें।
%1$d लाइट चैनल
Freelight चल रहा है
टीवी के रंग स्थानीय रूप से पढ़े जा रहे हैं''')
add('id','''Beranda
Buka menu
Cahayamu, ruangmu
Bohlam Bluetooth
Hue Bridge
Tutup menu
Mulai semua
Hentikan semua
Bohlam · %1$d / %2$d terhubung
Pengaturan aplikasi
Bahasa aplikasi
Bahasa sistem
Terapkan
Gunakan bahasa TV secara otomatis atau pilih bahasa. Bahasa Inggris digunakan jika bahasa sistem tidak didukung. Bohlam tetap terhubung.
Nama bohlam
Contoh: Di samping sofa
Posisi · menghadap TV
Pilih posisi…
Kecerahan · %1$.2f×
%1$.2f×
0× mati · 1× normal · 2× sebelumnya · 4× maksimum. Batas fisik bohlam tetap berlaku. Simpan untuk menerapkan.
Pengaturan bohlam
Bohlam baru
Batal
Simpan
Simpan dan hubungkan
Kiri
Kiri atas
Atas
Kanan atas
Kanan
TV dan bohlam. Gunakan kiri/kanan untuk memilih bohlam dan OK untuk membuka pengaturannya.
Terhubung
Menghubungkan
Terputus
Sinkronisasi aktif
Sinkronisasi dijeda
Menunggu koneksi
Mulai
Hentikan
Pengaturan
Putuskan
Belum ada bohlam. Tambahkan melalui Bohlam Bluetooth di menu kiri.
Tambah bohlam Bluetooth
Cari → Pilih posisi → Hubungkan
Buat bohlam baru terlihat di aplikasi Hue, lalu tutup aplikasi ponsel. Setujui penyambungan di TV jika diminta. Bohlam lain tetap tersinkronisasi.
Cari bohlam untuk memulai.
Cari bohlam
Tambah yang dipilih
Cari dan pilih bohlam terlebih dahulu.
Bohlam tersimpan
Mulai, hentikan, dan atur bohlam dari Beranda. Bohlam yang diputus tetap offline sampai dimulai lagi.
Mencari bohlam Hue…
Bohlam tidak ditemukan. Periksa daya dan visibilitas.
tersimpan
Izin Perangkat di sekitar diperlukan untuk menghubungkan bohlam.
Bluetooth TV mati.
Tidak dapat mencari bohlam. Periksa Bluetooth lalu coba lagi.
Hubungkan Hue Bridge
Penyambungan tersimpan. Anda dapat menguji koneksi.
Tambahkan Bridge di sini saat tersedia.
Alamat IP Bridge
Contoh: 192.168.1.20
Cari di jaringan
Sambungkan Bridge
Uji koneksi
Tekan tombol fisik pada Bridge, lalu pilih Sambungkan Bridge. Tidak perlu mengetik kunci.
Area hiburan
Lupakan penyambungan
Penyambungan Bridge dihapus dari TV ini.
Penyambungan Bridge dan pemilihan area tersedia. Sinkronisasi langsung melalui Bridge belum didukung. Bohlam IKEA Matter tidak termasuk.
Belum ada area hiburan
Buat area hiburan di aplikasi Hue, lalu uji koneksi.
Pilihan area disimpan di TV ini.
Menyambungkan Bridge…
Memeriksa koneksi dan area Bridge…
Tidak dapat terhubung. Periksa daya, jaringan lokal, dan alamat Bridge lalu coba lagi.
Masukkan alamat IPv4 lokal yang valid, seperti 192.168.1.20.
Tekan tombol Bridge, lalu coba sambungkan lagi.
Bridge terhubung
Mencari Bridge di jaringan lokal…
Bridge ditemukan. Tekan tombolnya untuk menyambungkan.
Bridge tidak ditemukan. Masukkan alamat IP dari aplikasi Hue.
Pencarian jaringan tidak tersedia. Masukkan IP secara manual.
%1$d saluran cahaya
Freelight sedang berjalan
Warna TV dibaca secara lokal''')
add('sv','''Hem
Öppna menyn
Ditt ljus, ditt rum
Bluetooth-lampor
Hue Bridge
Stäng menyn
Starta alla
Stoppa alla
Lampor · %1$d / %2$d anslutna
Appinställningar
Appspråk
Systemspråk
Verkställ
Använd TV:ns språk automatiskt eller välj ett språk. Engelska används om systemspråket inte stöds. Lamporna förblir anslutna.
Lampans namn
Exempel: Bredvid soffan
Placering · vänd mot TV:n
Välj placering…
Ljusstyrka · %1$.2f×
%1$.2f×
0× av · 1× normalt · 2× tidigare · 4× högst. Lampans fysiska gräns respekteras. Spara för att tillämpa.
Lampinställningar
Ny lampa
Avbryt
Spara
Spara och anslut
Vänster
Uppe till vänster
Ovanför
Uppe till höger
Höger
TV och lampor. Välj lampa med vänster/höger och tryck OK för att öppna inställningarna.
Ansluten
Ansluter
Frånkopplad
Synkronisering på
Synkronisering pausad
Väntar på anslutning
Starta
Stoppa
Inställningar
Koppla från
Inga lampor än. Lägg till en via Bluetooth-lampor i vänstermenyn.
Lägg till Bluetooth-lampa
Sök → Välj placering → Anslut
Gör den nya lampan synlig i Hue-appen och stäng sedan telefonappen. Godkänn parkopplingen på TV:n om du blir tillfrågad. Övriga lampor fortsätter synkroniseras.
Sök efter en lampa för att börja.
Sök lampor
Lägg till vald
Sök och välj först en lampa.
Sparade lampor
Starta, stoppa och ställ in lampor från Hem. Frånkopplade lampor förblir offline tills du startar dem igen.
Söker efter Hue-lampor…
Inga lampor hittades. Kontrollera ström och synlighet.
sparad
Behörighet för enheter i närheten krävs för att ansluta en lampa.
TV:ns Bluetooth är avstängt.
Det gick inte att söka lampor. Kontrollera Bluetooth och försök igen.
Anslut Hue Bridge
Parkopplingen har sparats. Du kan testa anslutningen.
Lägg till din Bridge här när den är tillgänglig.
Bridge-enhetens IP-adress
Exempel: 192.168.1.20
Sök i nätverket
Parkoppla Bridge
Testa anslutning
Tryck på den fysiska knappen på Bridge och välj Parkoppla Bridge. Inga nycklar behöver skrivas in.
Underhållningsområde
Glöm parkoppling
Bridge-parkopplingen har tagits bort från den här TV:n.
Bridge-parkoppling och områdesval är tillgängliga. Livesynkronisering via Bridge stöds inte ännu. IKEA Matter-lampor ingår inte.
Inga underhållningsområden än
Skapa ett underhållningsområde i Hue-appen och testa sedan anslutningen.
Ditt områdesval sparas på den här TV:n.
Parkopplar Bridge…
Kontrollerar Bridge-anslutning och områden…
Kunde inte ansluta. Kontrollera ström, lokalt nätverk och Bridge-adress och försök igen.
Ange en giltig lokal IPv4-adress, till exempel 192.168.1.20.
Tryck på Bridge-knappen och försök parkoppla igen.
Bridge ansluten
Söker efter Bridge i det lokala nätverket…
Bridge hittades. Tryck på dess knapp för att parkoppla.
Bridge hittades inte. Ange IP-adressen från Hue-appen.
Nätverkssökning är inte tillgänglig. Ange IP-adressen manuellt.
%1$d ljuskanaler
Freelight körs
TV-färger läses lokalt''')
if __name__=='__main__':
    import runpy
    extra=runpy.run_path(str(Path(__file__).with_name('setup-translations.py')))['DATA']
    for code,strings in extra.items():DATA[code].update(strings)
    write();print('Wrote',len(DATA),'languages,',len(DATA['en']),'strings each')
