Home school application designed for parents

Planned features:
- Student setup, classes, GPAs, content creation, grading
- State registration and legal support

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop, Server.

[Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)

Foundation still in progress

[Designs](https://www.figma.com/design/d5sLjwLWIATF15Vwaf1hY8/homely-team-library?node-id=0-1&t=ct6DA2rb8WKBdMV0-1)

* Building instructions:
  - Wasm: ./gradlew wasmJsBrowserDevelopmentRun
  - Desktop: ./gradlew :composeApp:run
  - server: ./gradlew (currently [issue](https://youtrack.jetbrains.com/issue/KTOR-7054/NoSuchMethodError-when-using-coroutines-1.9.0-RC) with coroutines 1.9.0-RC)
  - android: install an emulator, either run in AS / fleet or ./gradlew installDebug
  - ios: install latest version of xcode, run using fleet, or configure in AS as noted [here](https://stackoverflow.com/questions/77851203/kotlin-multiplatform-library-iosapp-run-configuration)

* Development:
  - File Structure: featureGroup/di,data,domain,presentation,util/yourFile.kt
  - Architecture: MVVM, Repository Pattern
  - detekt (./gradlew detekt): static analysis / linting tool. https://github.com/detekt/detekt
  - Navigation: Voyager https://voyager.adriel.cafe/
  - UI: Material3 https://m3.material.io/components
  - DI: Koin https://github.com/InsertKoinIO/koin/tree/main
  - Networking: Ktor https://ktor.io/docs/client-create-new-application.html

* Contributing:
  - Pick up an issue documented in the issues panel!
  - Any help is appreciated

