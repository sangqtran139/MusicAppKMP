# MusicAppKMP

A **music streaming** app built with **Kotlin Multiplatform + Compose Multiplatform**, targeting
Android and iOS with shared UI and logic.

> **📐 Architecture & contribution guide → [`docs/`](docs/README.md).**
> Start there before writing code. AI agents: read [`CLAUDE.md`](CLAUDE.md) first.
> The architecture is Clean Architecture + MVI over feature/core modules (Koin, Ktor, SQLDelight,
> Coil, Media3/AVPlayer). The repo is currently the KMP wizard scaffold; the bootstrap order to
> reach the documented foundation is in [`docs/Setup/BuildGuide.md`](docs/Setup/BuildGuide.md) (Phase 0).
>
> **Backend:** catalog data comes from **Deezer** via the **RapidAPI** proxy `deezerdevs-deezer`
> (full API contract → [`docs/Api/DeezerApi.md`](docs/Api/DeezerApi.md)). It needs a RapidAPI key —
> put it in `local.properties` (git-ignored) as `RAPIDAPI_KEY=...`; never hardcode or commit it.

---

This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…