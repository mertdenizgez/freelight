"""Localized first-run and privacy strings, merged by generate-translations.py."""
KEYS='about privacy privacy_body reader_start reader_stop setup_connect_bulb tv_begin_help tv_confirm tv_expired tv_failed tv_forget tv_forget_help tv_forget_title tv_intro tv_pair tv_pairing tv_pin_help tv_pin_hint tv_pin_invalid tv_repair tv_setup tv_status_auth tv_status_fresh tv_status_missing tv_status_wait tv_success tv_verifying'.split()
DATA={}
def add(code,lines):
 rows=lines.strip().split('\n');assert len(rows)==len(KEYS),(code,len(rows));DATA[code]=dict(zip(KEYS,rows))
add('en','''About
Privacy
Colors are processed locally. No analytics or cloud account is used. TV credentials are encrypted on this TV; bulb names, addresses and settings stay on the device. Forget TV removes its pairing. Clear Android app data to remove all settings. Contact: project issues on GitHub.
Start synchronization
Stop synchronization
Add a Bluetooth bulb
Only compatible Philips TVs with a local Ambilight API are supported. A PIN will appear on this TV. No computer, IP address or account is needed.
Confirm PIN
The PIN session expired. Cancel and pair again.
Connection or PIN verification failed. Check the PIN, cancel and retry if needed. Existing pairing is preserved.
Forget TV
Remove this TV pairing and stop synchronization? Bulb registrations remain. You can pair again later.
Forget this TV?
Connect this Philips TV to Freelight, then add a compatible Hue LCA012 RGB bulb. Other TVs, Hue models, Bridge and Matter are not supported in this release.
Pair this TV
Waiting for the TV pairing service…
Enter the four-digit PIN shown by this TV. If the TV dialog covers the app, return to Freelight without cancelling the TV pairing.
4-digit PIN
Enter exactly four digits.
Pair again
TV connection
TV authorization was rejected. Pair this TV again.
Connected · live TV colors available
Pair this TV to get started.
TV paired · synchronization stopped or waiting for fresh colors
TV paired successfully. Continue by adding a Bluetooth bulb.
Verifying PIN and Ambilight access…''')
add('tr','''Hakkında
Gizlilik
Renkler yerel işlenir. Analitik veya bulut hesabı kullanılmaz. TV kimlik bilgileri bu TV’de şifrelenir; ampul adları, adresleri ve ayarlar cihazda kalır. TV’yi unut eşleştirmeyi siler. Tüm ayarları silmek için Android uygulama verilerini temizleyin. İletişim: GitHub proje sorunları.
Aktarımı başlat
Aktarımı durdur
Bluetooth ampul ekle
Yalnızca yerel Ambilight API’si olan uyumlu Philips TV’ler desteklenir. Bu TV’de PIN görünecek. Bilgisayar, IP adresi veya hesap gerekmez.
PIN’i onayla
PIN oturumunun süresi doldu. Vazgeçip yeniden eşleştirin.
Bağlantı veya PIN doğrulaması başarısız. PIN’i kontrol edin; gerekirse vazgeçip yeniden deneyin. Mevcut eşleştirme korunur.
TV’yi unut
Bu TV eşleştirmesi kaldırılsın ve aktarım dursun mu? Ampul kayıtları kalır. Daha sonra yeniden eşleştirebilirsiniz.
Bu TV unutulsun mu?
Bu Philips TV’yi Freelight’a bağlayın, ardından uyumlu Hue LCA012 RGB ampul ekleyin. Diğer TV’ler, Hue modelleri, Bridge ve Matter bu sürümde desteklenmez.
Bu TV’yi eşleştir
TV eşleştirme servisi bekleniyor…
TV’nin gösterdiği dört haneli PIN’i girin. TV penceresi uygulamayı kapatıyorsa TV eşleştirmesini iptal etmeden Freelight’a dönün.
4 haneli PIN
Tam olarak dört rakam girin.
Yeniden eşleştir
TV bağlantısı
TV yetkilendirmeyi reddetti. Bu TV’yi yeniden eşleştirin.
Bağlı · canlı TV renkleri alınıyor
Başlamak için bu TV’yi eşleştirin.
TV eşleştirildi · aktarım kapalı veya güncel renkler bekleniyor
TV eşleştirildi. Bluetooth ampul ekleyerek devam edin.
PIN ve Ambilight erişimi doğrulanıyor…''')
add('de','''Über
Datenschutz
Farben werden lokal verarbeitet. Keine Analysen oder Cloud-Konten. TV-Zugangsdaten werden auf diesem TV verschlüsselt; Lampennamen, Adressen und Einstellungen bleiben lokal. TV vergessen löscht die Kopplung. Android-App-Daten löschen entfernt alle Einstellungen. Kontakt: GitHub-Projektissues.
Synchronisierung starten
Synchronisierung stoppen
Bluetooth-Lampe hinzufügen
Nur kompatible Philips-TVs mit lokaler Ambilight-API werden unterstützt. Auf diesem TV erscheint eine PIN. Kein Computer, keine IP-Adresse und kein Konto nötig.
PIN bestätigen
Die PIN-Sitzung ist abgelaufen. Abbrechen und erneut koppeln.
Verbindung oder PIN-Prüfung fehlgeschlagen. PIN prüfen; gegebenenfalls abbrechen und erneut versuchen. Bestehende Kopplung bleibt erhalten.
TV vergessen
TV-Kopplung entfernen und Synchronisierung stoppen? Lampeneinträge bleiben erhalten. Späteres Koppeln ist möglich.
Diesen TV vergessen?
Diesen Philips-TV mit Freelight verbinden und eine kompatible Hue LCA012 RGB-Lampe hinzufügen. Andere TVs, Hue-Modelle, Bridge und Matter werden hier nicht unterstützt.
Diesen TV koppeln
TV-Kopplungsdienst wird gesucht…
Die vierstellige PIN dieses TVs eingeben. Verdeckt der TV-Dialog die App, ohne Abbruch der TV-Kopplung zu Freelight zurückkehren.
Vierstellige PIN
Genau vier Ziffern eingeben.
Erneut koppeln
TV-Verbindung
TV-Autorisierung abgelehnt. Diesen TV erneut koppeln.
Verbunden · aktuelle TV-Farben verfügbar
Zum Start diesen TV koppeln.
TV gekoppelt · Synchronisierung gestoppt oder Warten auf Farben
TV erfolgreich gekoppelt. Jetzt eine Bluetooth-Lampe hinzufügen.
PIN und Ambilight-Zugriff werden geprüft…''')
add('fr','''À propos
Confidentialité
Les couleurs sont traitées localement. Aucune analyse ni compte cloud. Les identifiants TV sont chiffrés sur ce téléviseur ; noms, adresses et réglages des ampoules restent locaux. Oublier le TV supprime son association. Effacez les données Android pour tout supprimer. Contact : tickets GitHub du projet.
Démarrer la synchronisation
Arrêter la synchronisation
Ajouter une ampoule Bluetooth
Seuls les TV Philips compatibles avec une API Ambilight locale sont pris en charge. Un code apparaît sur ce TV. Aucun ordinateur, adresse IP ni compte requis.
Confirmer le code
La session a expiré. Annulez et recommencez.
Échec de connexion ou de vérification du code. Vérifiez le code ; annulez puis réessayez si nécessaire. L’association existante est conservée.
Oublier le TV
Supprimer cette association et arrêter la synchronisation ? Les ampoules restent enregistrées. Vous pourrez recommencer plus tard.
Oublier ce TV ?
Associez ce TV Philips à Freelight, puis ajoutez une ampoule RGB Hue LCA012 compatible. Autres TV, modèles Hue, Bridge et Matter non pris en charge ici.
Associer ce TV
Recherche du service d’association TV…
Entrez le code à quatre chiffres affiché par ce TV. Si sa fenêtre masque l’application, revenez à Freelight sans annuler l’association du TV.
Code à 4 chiffres
Entrez exactement quatre chiffres.
Associer à nouveau
Connexion TV
Autorisation TV refusée. Associez à nouveau ce TV.
Connecté · couleurs TV disponibles
Associez ce TV pour commencer.
TV associé · synchronisation arrêtée ou couleurs en attente
TV associé. Continuez en ajoutant une ampoule Bluetooth.
Vérification du code et de l’accès Ambilight…''')
add('es','''Acerca de
Privacidad
Los colores se procesan localmente. Sin analítica ni cuenta en la nube. Las credenciales del TV se cifran en este TV; nombres, direcciones y ajustes de bombillas permanecen locales. Olvidar TV elimina su vinculación. Borra los datos Android para eliminar todo. Contacto: incidencias del proyecto en GitHub.
Iniciar sincronización
Detener sincronización
Añadir bombilla Bluetooth
Solo se admiten TV Philips compatibles con API Ambilight local. Aparecerá un PIN en este TV. No se necesita ordenador, dirección IP ni cuenta.
Confirmar PIN
La sesión PIN caducó. Cancela y vuelve a vincular.
Falló la conexión o el PIN. Comprueba el PIN; cancela y reintenta si es necesario. Se conserva la vinculación anterior.
Olvidar TV
¿Eliminar esta vinculación y detener la sincronización? Se conservan las bombillas. Puedes vincular de nuevo después.
¿Olvidar este TV?
Conecta este TV Philips a Freelight y añade una bombilla RGB Hue LCA012 compatible. Otros TV, modelos Hue, Bridge y Matter no se admiten en esta versión.
Vincular este TV
Esperando al servicio de vinculación del TV…
Introduce el PIN de cuatro cifras mostrado por este TV. Si su ventana oculta la aplicación, vuelve a Freelight sin cancelar la vinculación del TV.
PIN de 4 cifras
Introduce exactamente cuatro cifras.
Vincular de nuevo
Conexión del TV
El TV rechazó la autorización. Vuelve a vincular este TV.
Conectado · colores del TV disponibles
Vincula este TV para comenzar.
TV vinculado · sincronización detenida o esperando colores
TV vinculado. Continúa añadiendo una bombilla Bluetooth.
Verificando PIN y acceso a Ambilight…''')
add('it','''Informazioni
Privacy
I colori sono elaborati localmente. Nessuna analisi o account cloud. Le credenziali TV sono cifrate su questo TV; nomi, indirizzi e impostazioni delle lampadine restano locali. Dimentica TV rimuove l’associazione. Cancella i dati Android per eliminare tutto. Contatto: segnalazioni GitHub del progetto.
Avvia sincronizzazione
Ferma sincronizzazione
Aggiungi lampadina Bluetooth
Sono supportati solo TV Philips compatibili con API Ambilight locale. Un PIN apparirà su questo TV. Non servono computer, indirizzo IP o account.
Conferma PIN
La sessione PIN è scaduta. Annulla e associa di nuovo.
Connessione o verifica PIN non riuscita. Controlla il PIN; se necessario annulla e riprova. L’associazione precedente è conservata.
Dimentica TV
Rimuovere questa associazione e fermare la sincronizzazione? Le lampadine rimangono registrate. Potrai associare di nuovo.
Dimenticare questo TV?
Collega questo TV Philips a Freelight e aggiungi una lampadina RGB Hue LCA012 compatibile. Altri TV, modelli Hue, Bridge e Matter non sono supportati qui.
Associa questo TV
In attesa del servizio di associazione TV…
Inserisci il PIN di quattro cifre mostrato da questo TV. Se la finestra nasconde l’app, torna a Freelight senza annullare l’associazione del TV.
PIN a 4 cifre
Inserisci esattamente quattro cifre.
Associa di nuovo
Connessione TV
Autorizzazione TV rifiutata. Associa di nuovo questo TV.
Connesso · colori TV disponibili
Associa questo TV per iniziare.
TV associato · sincronizzazione ferma o colori in attesa
TV associato. Continua aggiungendo una lampadina Bluetooth.
Verifica PIN e accesso Ambilight…''')
add('ru','''О приложении
Конфиденциальность
Цвета обрабатываются локально. Нет аналитики или облачного аккаунта. Данные доступа ТВ зашифрованы на этом ТВ; имена, адреса и настройки ламп остаются на устройстве. Забыть ТВ удаляет сопряжение. Очистка данных Android удаляет все настройки. Связь: задачи проекта на GitHub.
Начать синхронизацию
Остановить синхронизацию
Добавить Bluetooth-лампу
Поддерживаются только совместимые Philips ТВ с локальным API Ambilight. На этом ТВ появится PIN. Компьютер, IP-адрес и аккаунт не нужны.
Подтвердить PIN
Сеанс PIN истёк. Отмените и повторите сопряжение.
Ошибка соединения или PIN. Проверьте PIN; при необходимости отмените и повторите. Прежнее сопряжение сохранено.
Забыть ТВ
Удалить сопряжение и остановить синхронизацию? Лампы останутся зарегистрированы. Позже можно выполнить сопряжение снова.
Забыть этот ТВ?
Подключите Philips ТВ к Freelight, затем добавьте совместимую RGB-лампу Hue LCA012. Другие ТВ, модели Hue, Bridge и Matter здесь не поддерживаются.
Сопрячь этот ТВ
Ожидание службы сопряжения ТВ…
Введите четырёхзначный PIN с этого ТВ. Если окно ТВ скрывает приложение, вернитесь в Freelight, не отменяя сопряжение ТВ.
PIN из 4 цифр
Введите ровно четыре цифры.
Повторить сопряжение
Подключение ТВ
ТВ отклонил авторизацию. Повторите сопряжение.
Подключено · цвета ТВ доступны
Для начала выполните сопряжение ТВ.
ТВ сопряжён · синхронизация остановлена или ожидание цветов
ТВ сопряжён. Продолжите, добавив Bluetooth-лампу.
Проверка PIN и доступа к Ambilight…''')
add('ar','''حول التطبيق
الخصوصية
تُعالج الألوان محليًا دون تحليلات أو حساب سحابي. تُشفّر بيانات اعتماد التلفاز على هذا التلفاز؛ وتبقى أسماء المصابيح وعناوينها وإعداداتها على الجهاز. نسيان التلفاز يحذف الاقتران. امسح بيانات تطبيق Android لحذف كل الإعدادات. التواصل: مشكلات المشروع على GitHub.
بدء المزامنة
إيقاف المزامنة
إضافة مصباح Bluetooth
تُدعم فقط أجهزة Philips المتوافقة ذات واجهة Ambilight محلية. سيظهر رمز PIN على هذا التلفاز. لا حاجة لحاسوب أو عنوان IP أو حساب.
تأكيد الرمز
انتهت جلسة الرمز. ألغِ وأعد الاقتران.
فشل الاتصال أو التحقق من الرمز. تحقق منه وألغِ وأعد المحاولة عند الحاجة. يبقى الاقتران السابق محفوظًا.
نسيان التلفاز
حذف اقتران هذا التلفاز وإيقاف المزامنة؟ تبقى المصابيح مسجلة ويمكن إعادة الاقتران لاحقًا.
نسيان هذا التلفاز؟
اربط تلفاز Philips هذا بـ Freelight ثم أضف مصباح Hue LCA012 RGB متوافقًا. الأجهزة والطرازات الأخرى وBridge وMatter غير مدعومة هنا.
اقتران هذا التلفاز
بانتظار خدمة اقتران التلفاز…
أدخل رمز PIN المكوّن من أربعة أرقام الظاهر على هذا التلفاز. إذا حجبت نافذة التلفاز التطبيق فعد إلى Freelight دون إلغاء اقتران التلفاز.
رمز من 4 أرقام
أدخل أربعة أرقام بالضبط.
إعادة الاقتران
اتصال التلفاز
رفض التلفاز التفويض. أعد اقتران هذا التلفاز.
متصل · ألوان التلفاز متاحة
اقرن هذا التلفاز للبدء.
التلفاز مقترن · المزامنة متوقفة أو بانتظار الألوان
تم اقتران التلفاز. تابع بإضافة مصباح Bluetooth.
التحقق من الرمز والوصول إلى Ambilight…''')
add('pt','''Sobre
Privacidade
As cores são processadas localmente. Sem análise nem conta na nuvem. As credenciais do TV são cifradas neste TV; nomes, endereços e ajustes das lâmpadas ficam locais. Esquecer TV remove o emparelhamento. Limpe os dados Android para remover tudo. Contacto: problemas do projeto no GitHub.
Iniciar sincronização
Parar sincronização
Adicionar lâmpada Bluetooth
Apenas TVs Philips compatíveis com API Ambilight local são suportadas. Um PIN aparecerá neste TV. Não precisa de computador, endereço IP ou conta.
Confirmar PIN
A sessão PIN expirou. Cancele e emparelhe novamente.
Falha na ligação ou no PIN. Verifique o PIN; cancele e repita se necessário. O emparelhamento anterior é preservado.
Esquecer TV
Remover este emparelhamento e parar a sincronização? As lâmpadas ficam registadas. Pode emparelhar novamente depois.
Esquecer este TV?
Ligue este TV Philips ao Freelight e adicione uma lâmpada RGB Hue LCA012 compatível. Outros TVs, modelos Hue, Bridge e Matter não são suportados aqui.
Emparelhar este TV
A aguardar o serviço de emparelhamento do TV…
Introduza o PIN de quatro dígitos mostrado neste TV. Se a janela ocultar a aplicação, volte ao Freelight sem cancelar o emparelhamento do TV.
PIN de 4 dígitos
Introduza exatamente quatro dígitos.
Emparelhar novamente
Ligação ao TV
O TV rejeitou a autorização. Emparelhe este TV novamente.
Ligado · cores do TV disponíveis
Emparelhe este TV para começar.
TV emparelhado · sincronização parada ou cores em espera
TV emparelhado. Continue adicionando uma lâmpada Bluetooth.
A verificar o PIN e o acesso Ambilight…''')
add('nl','''Over
Privacy
Kleuren worden lokaal verwerkt. Geen analyses of cloudaccount. TV-inloggegevens zijn op deze TV versleuteld; lampnamen, adressen en instellingen blijven lokaal. TV vergeten verwijdert de koppeling. Wis Android-appgegevens om alles te verwijderen. Contact: GitHub-projectissues.
Synchronisatie starten
Synchronisatie stoppen
Bluetooth-lamp toevoegen
Alleen compatibele Philips-TVs met lokale Ambilight-API worden ondersteund. Op deze TV verschijnt een pincode. Geen computer, IP-adres of account nodig.
Pincode bevestigen
De pincodesessie is verlopen. Annuleer en koppel opnieuw.
Verbinding of pincodecontrole mislukt. Controleer de code; annuleer en probeer zo nodig opnieuw. De bestaande koppeling blijft behouden.
TV vergeten
Deze TV-koppeling verwijderen en synchronisatie stoppen? Lampregistraties blijven behouden. Later opnieuw koppelen is mogelijk.
Deze TV vergeten?
Koppel deze Philips-TV aan Freelight en voeg een compatibele Hue LCA012 RGB-lamp toe. Andere TVs, Hue-modellen, Bridge en Matter worden hier niet ondersteund.
Deze TV koppelen
Wachten op de TV-koppelservice…
Voer de viercijferige code van deze TV in. Bedekt het TV-venster de app, ga dan terug naar Freelight zonder de TV-koppeling te annuleren.
Viercijferige pincode
Voer precies vier cijfers in.
Opnieuw koppelen
TV-verbinding
TV-autorisatie geweigerd. Koppel deze TV opnieuw.
Verbonden · TV-kleuren beschikbaar
Koppel deze TV om te beginnen.
TV gekoppeld · synchronisatie gestopt of wachten op kleuren
TV gekoppeld. Voeg nu een Bluetooth-lamp toe.
Pincode en Ambilight-toegang controleren…''')
add('pl','''O aplikacji
Prywatność
Kolory są przetwarzane lokalnie. Bez analityki i konta w chmurze. Dane dostępu TV są szyfrowane na tym TV; nazwy, adresy i ustawienia żarówek pozostają lokalne. Zapomnij TV usuwa parowanie. Wyczyść dane Android, aby usunąć wszystko. Kontakt: zgłoszenia projektu GitHub.
Rozpocznij synchronizację
Zatrzymaj synchronizację
Dodaj żarówkę Bluetooth
Obsługiwane są tylko zgodne TV Philips z lokalnym API Ambilight. Na tym TV pojawi się PIN. Komputer, adres IP i konto nie są potrzebne.
Potwierdź PIN
Sesja PIN wygasła. Anuluj i sparuj ponownie.
Błąd połączenia lub PIN. Sprawdź PIN; w razie potrzeby anuluj i ponów. Poprzednie parowanie zostaje zachowane.
Zapomnij TV
Usunąć parowanie TV i zatrzymać synchronizację? Żarówki pozostaną zapisane. Później można sparować ponownie.
Zapomnieć ten TV?
Połącz TV Philips z Freelight, a następnie dodaj zgodną żarówkę RGB Hue LCA012. Inne TV, modele Hue, Bridge i Matter nie są tu obsługiwane.
Sparuj ten TV
Oczekiwanie na usługę parowania TV…
Wpisz czterocyfrowy PIN widoczny na tym TV. Jeśli okno TV zasłania aplikację, wróć do Freelight bez anulowania parowania TV.
4-cyfrowy PIN
Wpisz dokładnie cztery cyfry.
Sparuj ponownie
Połączenie TV
TV odrzucił autoryzację. Sparuj ten TV ponownie.
Połączono · kolory TV dostępne
Sparuj ten TV, aby rozpocząć.
TV sparowany · synchronizacja zatrzymana lub oczekiwanie na kolory
TV sparowany. Dodaj teraz żarówkę Bluetooth.
Sprawdzanie PIN i dostępu Ambilight…''')
add('uk','''Про застосунок
Конфіденційність
Кольори обробляються локально. Без аналітики та хмарного акаунта. Дані доступу ТВ зашифровані на цьому ТВ; імена, адреси й налаштування ламп лишаються на пристрої. Забути ТВ видаляє сполучення. Очищення даних Android видаляє все. Зв’язок: проблеми проєкту на GitHub.
Почати синхронізацію
Зупинити синхронізацію
Додати Bluetooth-лампу
Підтримуються лише сумісні ТВ Philips із локальним API Ambilight. На цьому ТВ з’явиться PIN. Комп’ютер, IP-адреса й акаунт не потрібні.
Підтвердити PIN
Сеанс PIN завершився. Скасуйте та сполучіть знову.
Помилка з’єднання або PIN. Перевірте PIN; за потреби скасуйте й повторіть. Попереднє сполучення збережено.
Забути ТВ
Видалити сполучення ТВ та зупинити синхронізацію? Лампи залишаться зареєстровані. Можна сполучити знову пізніше.
Забути цей ТВ?
Підключіть ТВ Philips до Freelight, потім додайте сумісну RGB-лампу Hue LCA012. Інші ТВ, моделі Hue, Bridge і Matter тут не підтримуються.
Сполучити цей ТВ
Очікування служби сполучення ТВ…
Введіть чотиризначний PIN із цього ТВ. Якщо вікно ТВ закриває застосунок, поверніться до Freelight без скасування сполучення ТВ.
PIN із 4 цифр
Введіть рівно чотири цифри.
Сполучити знову
З’єднання ТВ
ТВ відхилив авторизацію. Сполучіть цей ТВ знову.
Підключено · кольори ТВ доступні
Сполучіть цей ТВ для початку.
ТВ сполучено · синхронізацію зупинено або очікуються кольори
ТВ сполучено. Додайте Bluetooth-лампу.
Перевірка PIN і доступу Ambilight…''')
add('zh','''关于
隐私
颜色在本地处理，不使用分析服务或云账户。电视凭据在本机加密；灯泡名称、地址和设置保留在设备上。忘记电视会删除配对。清除 Android 应用数据可删除所有设置。联系：GitHub 项目问题页面。
开始同步
停止同步
添加蓝牙灯泡
仅支持具有本地 Ambilight API 的兼容 Philips 电视。本机会显示 PIN，无需电脑、IP 地址或账户。
确认 PIN
PIN 会话已过期。请取消并重新配对。
连接或 PIN 验证失败。请检查 PIN，必要时取消并重试。原有配对保持不变。
忘记电视
删除此电视配对并停止同步？灯泡记录将保留，以后可以重新配对。
忘记此电视？
将此 Philips 电视连接至 Freelight，再添加兼容的 Hue LCA012 RGB 灯泡。本版本不支持其他电视、Hue 型号、Bridge 或 Matter。
配对此电视
正在等待电视配对服务…
输入此电视显示的四位 PIN。如果电视窗口挡住应用，请返回 Freelight，但不要取消电视配对。
四位 PIN
请输入四位数字。
重新配对
电视连接
电视拒绝授权。请重新配对。
已连接 · 可读取电视颜色
请先配对此电视。
电视已配对 · 同步已停止或正在等待颜色
电视配对成功。请继续添加蓝牙灯泡。
正在验证 PIN 和 Ambilight 访问权限…''')
add('ja','''アプリについて
プライバシー
色は端末内で処理され、分析サービスやクラウドアカウントは使用しません。TVの認証情報はこのTVで暗号化され、電球名・アドレス・設定は端末に保存されます。TVを削除するとペアリングを解除します。全設定の削除にはAndroidのアプリデータを消去してください。連絡先：GitHubのプロジェクト課題。
同期を開始
同期を停止
Bluetooth電球を追加
ローカルAmbilight API対応のPhilips TVのみ対応します。このTVにPINが表示されます。パソコン、IPアドレス、アカウントは不要です。
PINを確認
PINセッションの期限切れです。キャンセルして再度ペアリングしてください。
接続またはPIN確認に失敗しました。PINを確認し、必要ならキャンセルして再試行してください。既存のペアリングは保持されます。
TVを削除
TVのペアリングを解除して同期を停止しますか？電球登録は保持されます。後で再度ペアリングできます。
このTVを削除しますか？
このPhilips TVをFreelightに接続し、対応するHue LCA012 RGB電球を追加します。他のTV、Hueモデル、Bridge、Matterは未対応です。
このTVをペアリング
TVペアリングサービスを待機中…
このTVに表示された4桁のPINを入力してください。TVの画面でアプリが隠れる場合、TVのペアリングを中止せずFreelightに戻ってください。
4桁のPIN
数字を4桁入力してください。
再度ペアリング
TV接続
TVが認証を拒否しました。再度ペアリングしてください。
接続済み・TVの色を取得可能
このTVをペアリングして開始してください。
TVペアリング済み・同期停止または色を待機中
TVをペアリングしました。Bluetooth電球を追加してください。
PINとAmbilightへのアクセスを確認中…''')
add('ko','''앱 정보
개인정보
색상은 기기에서 처리되며 분석 서비스나 클라우드 계정을 사용하지 않습니다. TV 인증 정보는 이 TV에 암호화되며 전구 이름, 주소와 설정은 기기에 저장됩니다. TV 삭제는 페어링을 제거합니다. 모든 설정은 Android 앱 데이터 삭제로 지울 수 있습니다. 문의: GitHub 프로젝트 이슈.
동기화 시작
동기화 중지
Bluetooth 전구 추가
로컬 Ambilight API가 있는 호환 Philips TV만 지원합니다. 이 TV에 PIN이 표시되며 컴퓨터, IP 주소나 계정이 필요하지 않습니다.
PIN 확인
PIN 세션이 만료되었습니다. 취소 후 다시 페어링하세요.
연결 또는 PIN 확인에 실패했습니다. PIN을 확인하고 필요하면 취소 후 다시 시도하세요. 기존 페어링은 유지됩니다.
TV 삭제
이 TV 페어링을 삭제하고 동기화를 중지할까요? 전구 등록은 유지되며 나중에 다시 페어링할 수 있습니다.
이 TV를 삭제할까요?
이 Philips TV를 Freelight에 연결하고 호환 Hue LCA012 RGB 전구를 추가하세요. 다른 TV, Hue 모델, Bridge와 Matter는 지원하지 않습니다.
이 TV 페어링
TV 페어링 서비스를 기다리는 중…
이 TV에 표시된 네 자리 PIN을 입력하세요. TV 창이 앱을 가리면 TV 페어링을 취소하지 않고 Freelight로 돌아오세요.
4자리 PIN
숫자 네 자리를 입력하세요.
다시 페어링
TV 연결
TV가 인증을 거부했습니다. 다시 페어링하세요.
연결됨 · TV 색상 사용 가능
시작하려면 이 TV를 페어링하세요.
TV 페어링됨 · 동기화 중지 또는 색상 대기 중
TV 페어링 완료. Bluetooth 전구를 추가하세요.
PIN 및 Ambilight 접근 확인 중…''')
add('hi','''परिचय
गोपनीयता
रंग स्थानीय रूप से संसाधित होते हैं। कोई विश्लेषण या क्लाउड खाता नहीं है। TV की पहचान जानकारी इसी TV पर एन्क्रिप्ट होती है; बल्ब के नाम, पते और सेटिंग डिवाइस पर रहते हैं। TV भूलें से पेयरिंग हटती है। सभी सेटिंग हटाने के लिए Android ऐप डेटा साफ करें। संपर्क: GitHub परियोजना के मुद्दे।
सिंक शुरू करें
सिंक रोकें
Bluetooth बल्ब जोड़ें
केवल स्थानीय Ambilight API वाले संगत Philips TV समर्थित हैं। इस TV पर PIN दिखेगा। कंप्यूटर, IP पता या खाता आवश्यक नहीं है।
PIN की पुष्टि करें
PIN सत्र समाप्त हो गया। रद्द करके दोबारा पेयर करें।
कनेक्शन या PIN सत्यापन विफल हुआ। PIN जांचें; जरूरत हो तो रद्द करके पुनः प्रयास करें। पुरानी पेयरिंग सुरक्षित रहती है।
TV भूलें
यह TV पेयरिंग हटाकर सिंक रोकें? बल्ब पंजीकरण बने रहेंगे। बाद में दोबारा पेयर कर सकते हैं।
इस TV को भूलें?
इस Philips TV को Freelight से जोड़ें, फिर संगत Hue LCA012 RGB बल्ब जोड़ें। अन्य TV, Hue मॉडल, Bridge और Matter इस संस्करण में समर्थित नहीं हैं।
इस TV को पेयर करें
TV पेयरिंग सेवा की प्रतीक्षा…
इस TV पर दिखा चार अंकों का PIN दर्ज करें। TV विंडो ऐप को ढक दे तो TV पेयरिंग रद्द किए बिना Freelight पर लौटें।
4 अंकों का PIN
ठीक चार अंक दर्ज करें।
दोबारा पेयर करें
TV कनेक्शन
TV ने अनुमति अस्वीकार की। दोबारा पेयर करें।
जुड़ा है · TV रंग उपलब्ध हैं
शुरू करने के लिए इस TV को पेयर करें।
TV पेयर है · सिंक बंद या रंगों की प्रतीक्षा
TV पेयर हो गया। अब Bluetooth बल्ब जोड़ें।
PIN और Ambilight पहुंच की जांच…''')
add('id','''Tentang
Privasi
Warna diproses secara lokal. Tanpa analitik atau akun cloud. Kredensial TV dienkripsi pada TV ini; nama, alamat, dan pengaturan lampu tetap di perangkat. Lupakan TV menghapus pemasangan. Hapus data aplikasi Android untuk menghapus semuanya. Kontak: isu proyek GitHub.
Mulai sinkronisasi
Hentikan sinkronisasi
Tambah lampu Bluetooth
Hanya TV Philips kompatibel dengan API Ambilight lokal yang didukung. PIN akan muncul pada TV ini. Tidak perlu komputer, alamat IP, atau akun.
Konfirmasi PIN
Sesi PIN berakhir. Batalkan dan pasangkan lagi.
Koneksi atau verifikasi PIN gagal. Periksa PIN; batalkan dan coba lagi bila perlu. Pemasangan lama tetap tersimpan.
Lupakan TV
Hapus pemasangan TV dan hentikan sinkronisasi? Pendaftaran lampu tetap tersimpan. Anda dapat memasangkan lagi nanti.
Lupakan TV ini?
Hubungkan TV Philips ini ke Freelight lalu tambahkan lampu RGB Hue LCA012 yang kompatibel. TV lain, model Hue lain, Bridge, dan Matter tidak didukung di versi ini.
Pasangkan TV ini
Menunggu layanan pemasangan TV…
Masukkan PIN empat digit yang ditampilkan TV ini. Jika jendela TV menutupi aplikasi, kembali ke Freelight tanpa membatalkan pemasangan TV.
PIN 4 digit
Masukkan tepat empat digit.
Pasangkan lagi
Koneksi TV
TV menolak otorisasi. Pasangkan TV ini lagi.
Terhubung · warna TV tersedia
Pasangkan TV ini untuk memulai.
TV dipasangkan · sinkronisasi berhenti atau menunggu warna
TV berhasil dipasangkan. Lanjutkan dengan menambah lampu Bluetooth.
Memverifikasi PIN dan akses Ambilight…''')
add('sv','''Om
Integritet
Färger behandlas lokalt. Ingen analys eller molnkonto används. TV-uppgifter krypteras på denna TV; lampnamn, adresser och inställningar stannar på enheten. Glöm TV tar bort parkopplingen. Rensa Android-appdata för att ta bort allt. Kontakt: projektets GitHub-ärenden.
Starta synkronisering
Stoppa synkronisering
Lägg till Bluetooth-lampa
Endast kompatibla Philips-TV med lokalt Ambilight-API stöds. En PIN visas på denna TV. Ingen dator, IP-adress eller konto behövs.
Bekräfta PIN
PIN-sessionen har gått ut. Avbryt och parkoppla igen.
Anslutning eller PIN-kontroll misslyckades. Kontrollera PIN; avbryt och försök igen vid behov. Befintlig parkoppling bevaras.
Glöm TV
Ta bort TV-parkoppling och stoppa synkronisering? Lampregistreringar behålls. Du kan parkoppla igen senare.
Glöm denna TV?
Anslut denna Philips-TV till Freelight och lägg till en kompatibel Hue LCA012 RGB-lampa. Andra TV, Hue-modeller, Bridge och Matter stöds inte här.
Parkoppla denna TV
Väntar på TV:ns parkopplingstjänst…
Ange den fyrsiffriga PIN som denna TV visar. Om TV-rutan täcker appen, återgå till Freelight utan att avbryta TV-parkopplingen.
Fyrsiffrig PIN
Ange exakt fyra siffror.
Parkoppla igen
TV-anslutning
TV:n nekade behörighet. Parkoppla denna TV igen.
Ansluten · TV-färger tillgängliga
Parkoppla denna TV för att börja.
TV parkopplad · synkronisering stoppad eller väntar på färger
TV:n är parkopplad. Lägg nu till en Bluetooth-lampa.
Kontrollerar PIN och Ambilight-åtkomst…''')
