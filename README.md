<a name="readme-top"></a>

# 🧪 Yassir Android Interview Test – Rick and Morty Character Explorer

This is a test project developed as part of an Android interview process. It demonstrates clean architecture principles, modern Android development practices using **Jetpack Compose**, **Hilt**, and **Retrofit**, and integrates with the public [Rick and Morty API](https://rickandmortyapi.com/).

The application allows users to explore characters, filter them by **status** and **species**, and perform a **search by name**, all while supporting **pagination**.

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/screen1.png" width="30%" />
  <img src="screenshots/screen2.png" width="30%" />
  <img src="screenshots/screen3.png" width="30%" />
</p>

<p align="center">
  <img src="screenshots/screen4.png" width="45%" />
  <img src="screenshots/screen5.png" width="45%" />
</p>
---

## 📦 Features

- 🔍 **Search characters** by name
- 🧪 **Filter** characters by status and species
- ♻️ **Pagination** (infinite scroll)
- ⚙️ **Error handling** with meaningful messages
- 🧼 **Clean architecture** (Data, Domain, Presentation layers)
- 💉 **Dependency Injection** with Hilt
- 🧩 **Composable UI** with Material 3 support

---

## 🏗️ Tech Stack
-----------------------------------------------------------------
| Layer        | Technology                                     |
|--------------|------------------------------------------------|
| Language     | Kotlin                                         |
| UI           | Jetpack Compose + Material 3                   |
| Networking   | Retrofit + Gson                                |
| DI           | Hilt                                           |
| Async        | Kotlin Coroutines + Flow                       |
| Architecture | Clean Architecture (MVVM + UseCase separation) |
| API          |[RickandMorty API](https://rickandmortyapi.com/)|
-----------------------------------------------------------------

## 📂 Project Structure

com.example.yassirtest
├── data
│ ├── remote (API, DTOs, Mappers)
│ └── repository (CharacterRepositoryImpl)
├── domain
│ ├── model (Character)
│ ├── repository (CharacterRepository interface)
│ └── usecase (GetCharactersUseCase)
├── presentation
│ ├── ui (Compose screens)
│ ├── viewModel (CharacterViewModel + UI State)
│ └── navigation (NavGraph)
├── di (Hilt modules)
└── util (Resource wrapper, safeApiCall)


---

## 🚀 Getting Started

### ✅ Prerequisites

- Android Studio Hedgehog or newer
- Kotlin 1.9+
- Android Emulator or physical device
- Internet connection (API is online)

### 🛠️ How to Build & Run

1. **Clone the repo**
   ```bash
   git clone https://github.com/your-username/yassir-interview-test.git
   cd yassir-interview-test
Open in Android Studio
Sync Gradle and wait for dependencies to resolve
Run the app on an emulator or device
🧱 Architectural Decisions

✅ Clean Architecture
The project is structured in multiple layers:

data: Responsible for API interaction and DTO mapping.
domain: Contains business logic and use cases.
presentation: Handles UI state and rendering with Jetpack Compose.
✅ Separation of Concerns
ViewModels only handle UI logic.
UseCases execute business logic and data fetching.
Repositories abstract data sources and allow for easier testing.
✅ Modular & Testable
By decoupling domain logic from Android framework, components are more testable and reusable.
