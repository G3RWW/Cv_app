# Publikavimas Google Play

Komandos – Windows PowerShell, vykdomos iš projekto aplanko.

## 0. Ką reikia pateikti (dėstytojo reikalavimai)

- Publikavimas sustabdomas ties **uždaru testavimu (Closed testing)** – viešai (Production) neleidžiama, nes Google
  reikalauja daug testuotojų.
- Sukuriamas leidimas, palaukiama **Google patvirtinimo**, tada į Moodle įkeliama **nuoroda į testavimą** ir **ekrano
  nuotrauka (print-screen)** iš Play Console.
- Paketo pavadinimas: `lt.vilniustech.gsk.mkdf_23_3.dovydas` (jau nustatytas `app/build.gradle.kts`).
- Patikrinimas gali užtrukti nuo kelių dienų iki kelių savaičių – pateikite anksti.

## Pasiruošimo sąrašas (prieš spaudžiant „Send for review“)

- [ ] Privatumo politikos nuoroda atsidaro naršyklėje **be prisijungimo**:
      <https://cv-app.dovydasgencevicius.workers.dev/privacy.html> (ta pati nuoroda yra programėlėje, pradžios ekrano apačioje)
- [ ] AAB pasirašytas **upload raktu**, ne debug (žr. 2–3 skyrius); `versionCode` didesnis nei ankstesnio įkelto
- [ ] Programėlė paleista iš **release** versijos telefone – visi ekranai veikia, paveikslėliai užsikrauna
- [ ] Ikona `store/icon-512.png`, grafika `store/feature-graphic-1024x500.png`
- [ ] Ekrano nuotraukos: telefono (≥ 2), 7" ir 10" planšetės (≥ 1) – iš emuliatoriaus *Pixel 8*, *Pixel Tablet*
- [ ] *Data safety*: „No data collected“ ir „No data shared“ – atitinka privatumo politiką
- [ ] *App access*: „All functionality is available without special access“
- [ ] *Ads*: No · *Target audience*: 18+ · *Content rating* anketa užpildyta
- [ ] Parduotuvės aprašymas be pažadų, kurių programėlė nedaro; kontaktinis el. paštas nurodytas

## 1. Google Play Developer paskyra


1. <https://play.google.com/console/signup> → **Yourself** (asmeninė paskyra).
2. Vienkartinis 25 USD mokestis, asmens tapatybės patvirtinimas (dokumentas) ir telefono numerio patvirtinimas.
   Patvirtinimas gali užtrukti iki kelių dienų – pradėkite anksti.

> **Kodėl tik uždaras testavimas.** Naujoms asmeninėms paskyroms Google reikalauja **uždaro testavimo (closed testing)
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
   - **Privacy policy** – `https://cv-app.dovydasgencevicius.workers.dev/privacy.html`
     (failas `docs/privacy.html`, publikuoja Cloudflare Worker). Prieš įklijuodami atidarykite nuorodą naršyklėje.
   - **Ads** – *No*.
   - **App access** – *All functionality is available without special access*.
   - **Content rating** – užpildykite anketą (kategorija *Reference / Utility*; smurto ir pan. nėra).
   - **Target audience** – 18+ (taip išvengiama „Families“ reikalavimų).
   - **Data safety** – *No data collected / No data shared* (nuotrauka ir įrašai lieka įrenginyje, atsarginės kopijos
     išjungtos `allowBackup="false"`, kontaktų forma tik atidaro el. pašto programėlę).
   - **Government app / Financial features / Health** – *No*.
4. **Store listing** (*Grow → Store presence → Main store listing*), LT ir pridėkite EN vertimą:
   - Programėlės ikona `store/icon-512.png` (512×512), *Feature graphic* `store/feature-graphic-1024x500.png`.
   - Telefono ekrano nuotraukos (bent 2), planšetės 7" ir 10" (bent po 1, nes UI pritaikytas planšetėms).
     Ekrano nuotraukas galima padaryti emuliatoriuje (*Pixel 8*, *Pixel Tablet*) mygtuku 📷 arba:
     ```powershell
     & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" exec-out screencap -p > screenshot.png
     ```
   - Kategorija: *Business* arba *Productivity*; kontaktinis el. paštas.
5. **Testing → Closed testing** → *Create track* (arba naudokite numatytąjį *Alpha*) → **Testers**: sukurkite el. pašto
   sąrašą (įtraukite save ir dėstytoją, jei jis prašo) → **Create new release** → įkelkite `app-release.aab`, įrašykite
   *Release notes* → *Save* → *Review release*.
6. **Publishing overview** → **Send changes for review**. Kol Google tikrina, būsena „In review“.
7. Po patvirtinimo *Closed testing → Testers* skiltyje nukopijuokite **„Join on the web“ / opt-in nuorodą** ir
   padarykite ekrano nuotrauką (būsena „Available to selected testers“ / leidimo puslapis).
8. Į Moodle įkelkite nuorodą ir ekrano nuotrauką. **Production** leidimo nekurkite.

Jei Google atmeta: Play Console → *Policy status* / el. laiške nurodoma priežastis. Pataisykite, padidinkite
`versionCode`, sukurkite naują AAB ir pateikite iš naujo.

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
