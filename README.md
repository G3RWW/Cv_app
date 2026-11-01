# Vizitinė kortelė (CV Card)

Android programėlė – mano vizitinė kortelė: nuotrauka, kontaktai, CV, portfolio ir „Susisiekti su manimi“ forma.
Kotlin + Jetpack Compose (Material 3), lietuvių ir anglų kalbomis.

## Užduoties reikalavimai ir kur jie įgyvendinti

| Reikalavimas | Kaip įgyvendinta | Failai |
|---|---|---|
| Vizitinė kortelė: el. paštas, nuotrauka, mygtukai CV ir portfolio | Pradžios ekranas su nuotrauka, vardu, el. paštu (paspaudus atidaro el. pašto programėlę), GitHub, vieta ir mygtukais *Peržiūrėti CV*, *Peržiūrėti portfolio*, *Susisiekti* | `ui/home/HomeScreen.kt` |
| Data Storage: vartotojas prideda CV / portfolio įrašus, jie išlieka po perkrovimo | **Room** (SQLite) duomenų bazė `cv.db`: lentelės `cv_entries` ir `portfolio_items`. Mygtukas *Pridėti* atidaro formą, savo įrašus galima ištrinti. Nuotraukos kelias saugomas **Preferences DataStore** | `data/AppDatabase.kt`, `data/Daos.kt`, `data/Entities.kt`, `data/ProfileRepository.kt` |
| Kontaktų forma – laiškas per kitą app'są (Intent) | Forma (vardas, el. paštas, telefonas, klausimas) su validacija; *Siųsti* – `Intent.ACTION_SENDTO` su `mailto:`, gavėju, tema ir tekstu. Nėra el. pašto programėlės – rodomas pranešimas | `ui/contact/ContactScreen.kt`, `ui/common/Intents.kt` |
| Lokalizavimas LT / EN + perjungiklis | Visi tekstai `res/values/strings.xml` (EN) ir `res/values-lt/strings.xml` (LT). Viršutinėje juostoje mygtukas **LT / EN** su meniu (Lietuvių / English / Sistemos kalba). Naudojamas `AppCompatDelegate.setApplicationLocales`, pasirinkimas išsaugomas; Android 13+ kalba matoma ir sistemos nustatymuose (`locales_config.xml`) | `ui/common/LanguageMenu.kt` |
| UI skirtingiems ekranams, vertikaliai ir horizontaliai | < 600dp pločio – apatinė navigacija, ≥ 600dp (gulsčias telefonas, planšetė) – šoninė *NavigationRail*. Pradžios ekranas plačiame ekrane išsidėsto dviem stulpeliais; CV ir portfolio – `LazyVerticalGrid` su `GridCells.Adaptive` (1 stulpelis telefone, 2–4 planšetėje); turinys slenkamas, edge-to-edge, klaviatūra neuždengia laukų | `ui/CvApp.kt`, `ui/home/HomeScreen.kt` |
| *(+1)* Kamera – nusifotografuoti ir pakeisti nuotrauką | *Nusifotografuoti* → `ActivityResultContracts.TakePicture` (kameros app'sas) į failą per `FileProvider`; failas `filesDir/photos`, kelias – DataStore. CAMERA leidimo nereikia | `ui/home/HomeScreen.kt`, `res/xml/file_paths.xml` |
| *(+1)* Portfolio vaizdai iš interneto su Coil, placeholder, klaida ir „bandyti dar kartą“ | **Coil 3** `SubcomposeAsyncImage`: kol kraunasi – placeholder paveikslėlis + indikatorius; nepavyko (nėra ryšio, bloga nuoroda) – klaidos paveikslėlis, tekstas ir mygtukas *Bandyti dar kartą* | `ui/portfolio/PortfolioImage.kt`, `res/drawable/image_*.xml` |
| Papildomai: dalijimasis per QR ir NFC | Ekranas *Dalintis*: pasirinkus **Kontaktas** – vCard (kitas telefonas siūlo išsaugoti kontaktą), **Portfolio** – ArtStation nuoroda. QR kodas generuojamas su ZXing; NFC – telefonas emuliuoja NFC Forum Type 4 žymą (*Host Card Emulation*), todėl kitame telefone programėlės nereikia. Žyma aktyvi tik kol ekranas atidarytas | `ui/share/ShareScreen.kt`, `share/` |
| Publikavimas Google Play | Release build su R8, pasirašymas per `keystore.properties`, AAB; žingsniai – [PUBLISHING.md](PUBLISHING.md), privatumo politika – [PRIVACY_POLICY.md](PRIVACY_POLICY.md) | `app/build.gradle.kts` |

## Dizainas

Savas „space fox“ stilius vietoj standartinio Material: visada tamsi kosmoso tema su žvaigždėmis ir ūkais,
lapės oranžinė, ūko violetinė ir žvaigždžių žydra spalvos, nukirsti kampai, „cel-shaded“ kortelės su kietu
spalvotu šešėliu, monospace etiketės ir mygtukai, gradientinis nuotraukos rėmelis, geometrinės lapės ikona.
Viskas – `ui/theme/Theme.kt` ir `ui/theme/Decor.kt`.

## Architektūra

```
app/src/main/java/lt/gencevicius/cv/
├── CvApplication.kt       AppContainer: Room DB, DataStore, ViewModel factory
├── MainActivity.kt        AppCompatActivity (reikia kalbos perjungimui) + Compose
├── data/                  Room entities/DAO, DataStore (nuotrauka), numatytas turinys
├── share/                 vCard / nuoroda, QR generavimas, NFC žymos emuliacija (HostApduService)
└── ui/
    ├── CvApp.kt           Scaffold, navigacija (Navigation Compose), bottom bar / rail
    ├── home/              Vizitinė kortelė + kamera
    ├── cv/                CV sąrašas, įrašo pridėjimas / trynimas
    ├── portfolio/         Portfolio tinklelis, Coil vaizdai su retry
    ├── contact/           Kontaktų forma → el. pašto Intent
    ├── share/             Dalijimosi ekranas (QR + NFC būsena)
    ├── common/            Kalbos meniu, Intent'ai, bendri komponentai
    └── theme/             „Space fox“ tema, žvaigždžių fonas, cel-shaded kortelės
```

Įdiegtas turinys (pavyzdiniai CV įrašai ir portfolio darbai) imamas iš `strings.xml`, todėl keičiasi kartu su kalba;
duomenų bazėje saugomi tik vartotojo pridėti įrašai (jie pažymėti „Pridėjote jūs“ ir gali būti ištrinti).

## Kaip pritaikyti sau

- Vardas, el. paštas, ArtStation, GitHub: `res/values/strings.xml` (`profile_*`, nekeičiami vertimuose).
- Pareigos, aprašymas, CV įrašai: `profile_*`, `cv_*` abiejuose `strings.xml` failuose.
- Portfolio vaizdai: `data/DefaultContent.kt` – pakeiskite `imageUrl` savo viešai pasiekiamų paveikslėlių nuorodomis
  (pvz. ArtStation projektų paveikslėlių adresai, GitHub Pages, Imgur). Dabar naudojami `picsum.photos` pavyzdiniai vaizdai.

## Paleidimas (Windows, Android Studio)

1. Android Studio → *File → Open* → projekto aplankas.
2. Palaukite Gradle sync, pasirinkite emuliatorių / telefoną → **Run**.

PowerShell, iš projekto aplanko:

```powershell
.\gradlew.bat assembleDebug
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
```

### Kaip patikrinti funkcijas

- **Saugojimas**: CV → *Pridėti* → išsaugokite → uždarykite app'są (*Recent apps* → nubraukite) → atidarykite: įrašas liko.
- **Kalba**: viršuje *EN/LT* → pasirinkite kalbą; visas UI persijungia.
- **Ekranai**: pasukite telefoną arba paleiskite planšetės emuliatorių (*Pixel Tablet*) – navigacija persikelia į šoną.
- **Kamera**: kortelėje *Nusifotografuoti* (emuliatoriuje veikia virtuali kamera).
- **QR**: *Dalintis* → kitu telefonu nuskenuokite kamera (kontaktas arba portfolio).
- **NFC**: abiejuose telefonuose įjunkite NFC, šiame atidarykite *Dalintis*, priglauskite telefonų nugarėles.
  Emuliatoriuje NFC neveikia – reikia tikrų telefonų.
  Kontakto režime pasirinkite **Kitas telefonas: Android / iPhone**. Android gauna vCard tiesiai (atsidaro
  „Pridėti kontaktą“), iPhone – nuorodą į `docs/dovydas.vcf` (iPhone per NFC reaguoja tik į nuorodas).

### GitHub Pages (reikia iPhone NFC režimui)

`docs/` aplanke yra `dovydas.vcf` ir paprastas `index.html`. Kad nuoroda
`https://g3rww.github.io/Cv_app/dovydas.vcf` veiktų: repozitorija turi būti vieša, o
*Settings → Pages → Deploy from a branch → `main` / `/docs`*. Pakeitus kontaktus, atnaujinkite ir `docs/dovydas.vcf`.
- **Vaizdai offline**: įjunkite lėktuvo režimą, atidarykite portfolio su naujais vaizdais → klaidos paveikslėlis ir
  *Bandyti dar kartą*; įjunkite ryšį ir spauskite mygtuką.
