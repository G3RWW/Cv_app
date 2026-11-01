# Publikavimas Google Play

Komandos – Windows PowerShell, vykdomos iš projekto aplanko.

## 1. Google Play Developer paskyra

1. <https://play.google.com/console/signup> → **Yourself** (asmeninė paskyra).
2. Vienkartinis 25 USD mokestis, asmens tapatybės patvirtinimas (dokumentas) ir telefono numerio patvirtinimas.
   Patvirtinimas gali užtrukti iki kelių dienų – pradėkite anksti.

> **Svarbu dėl terminų.** Naujoms asmeninėms paskyroms Google reikalauja **uždaro testavimo (closed testing)
> su ≥ 12 testuotojų, kurie būtų prisijungę 14 dienų iš eilės**, ir tik tada leidžia prašyti *Production* prieigos.
> Kol to nėra, programėlę galima išleisti **Internal testing** / **Closed testing** takeliu – ji pasiekiama per
> Google Play nuorodą prisijungusiems testuotojams.

## 2. Įkėlimo (upload) raktas

Vieną kartą sukurkite raktą (laikykite **saugiai** ir **ne** git'e – `.gitignore` jau ignoruoja `*.jks` ir `keystore.properties`):

```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -genkeypair -v `
  -keystore upload-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

Šalia `settings.gradle.kts` sukurkite `keystore.properties`:

```properties
storeFile=upload-keystore.jks
storePassword=JŪSŲ_SLAPTAŽODIS
keyAlias=upload
keyPassword=JŪSŲ_SLAPTAŽODIS
```

(Alternatyva: Android Studio → *Build → Generate Signed App Bundle / APK…* – vedlys sukuria raktą ir AAB.)

## 3. Release AAB

```powershell
.\gradlew.bat bundleRelease
explorer app\build\outputs\bundle\release
```

Gaunamas `app-release.aab` (R8 sumažintas, pasirašytas upload raktu). Prieš įkeliant verta išbandyti release versiją:

```powershell
.\gradlew.bat installRelease
```

Kiekvienam naujam leidimui padidinkite `versionCode` (ir `versionName`) faile `app/build.gradle.kts`.

## 4. Play Console

1. **Create app**: pavadinimas *Vizitinė kortelė*, numatytoji kalba *lietuvių – lt-LT*, *App*, *Free*.
2. **Play App Signing** – palikite įjungtą (Google saugo tikrąjį pasirašymo raktą, jūs – tik upload raktą).
3. **App content** (*Policy → App content*):
   - **Privacy policy** – viešas URL su [PRIVACY_POLICY.md](PRIVACY_POLICY.md) turiniu
     (pvz., vieša GitHub nuoroda, GitHub Pages arba Google Sites puslapis).
   - **Ads** – *No*.
   - **App access** – *All functionality is available without special access*.
   - **Content rating** – užpildykite anketą (kategorija *Reference / Utility*; smurto ir pan. nėra).
   - **Target audience** – 18+ (taip išvengiama „Families“ reikalavimų).
   - **Data safety** – *No data collected / No data shared* (nuotrauka ir įrašai lieka įrenginyje,
     kontaktų forma tik atidaro el. pašto programėlę).
   - **Government app / Financial features / Health** – *No*.
4. **Store listing** (*Grow → Store presence → Main store listing*), LT ir pridėkite EN vertimą:
   - Programėlės ikona 512×512 PNG, *Feature graphic* 1024×500 PNG.
   - Telefono ekrano nuotraukos (bent 2), planšetės 7" ir 10" (bent po 1, nes UI pritaikytas planšetėms).
     Ekrano nuotraukas galima padaryti emuliatoriuje (*Pixel 8*, *Pixel Tablet*) mygtuku 📷 arba:
     ```powershell
     & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" exec-out screencap -p > screenshot.png
     ```
   - Kategorija: *Business* arba *Productivity*; kontaktinis el. paštas.
5. **Testing → Internal testing** (arba *Closed testing*) → *Create new release* → įkelkite `app-release.aab`,
   įrašykite *Release notes* → *Save* → *Review release* → *Start rollout*. Pridėkite testuotojų el. paštus,
   nukopijuokite *opt-in* nuorodą.
6. Po 14 dienų closed testing su 12 testuotojų: *Dashboard → Apply for production* → *Production → Create new release*.

## Parduotuvės tekstai

**Trumpas aprašymas (≤ 80 simbolių)**
- LT: `Mano vizitinė kortelė: kontaktai, CV, portfolio ir laiško forma.`
- EN: `My business card: contacts, CV, portfolio and a contact form.`

**Pilnas aprašymas**

LT:
```
Vizitinė kortelė – Dovydo Gencevičiaus, 3D menininko ir animatoriaus, kortelė telefone.

• Nuotrauka ir kontaktai – el. laiškas vienu paspaudimu
• CV: patirtis, išsilavinimas, įgūdžiai; galite pridėti savo įrašų
• Portfolio: 3D personažai, žaidimai (Vireka, Terminų Detektyvas) ir kiti projektai
• Forma „Susisiekti su manimi“ – laiškas atsidaro jūsų el. pašto programėlėje
• Nusifotografuokite ir pakeiskite kortelės nuotrauką
• Lietuvių ir anglų kalbos, pritaikyta telefonams ir planšetėms

Programėlė nerenka jokių asmens duomenų.
```

EN:
```
CV Card – the business card of Dovydas Gencevičius, 3D artist and animator, on your phone.

• Photo and contacts – email with one tap
• CV: experience, education, skills; add your own entries
• Portfolio: 3D characters, games (Vireka, Terminų Detektyvas) and other projects
• "Contact me" form – the message opens in your email app
• Take a selfie to replace the card photo
• Lithuanian and English, built for phones and tablets

The app does not collect any personal data.
```
