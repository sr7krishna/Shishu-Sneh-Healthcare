# ShishuSneh Healthcare

A native Android application for tracking infant and toddler health, built with Jetpack Compose and a clean MVVM architecture. The app enables parents and guardians to manage baby profiles, monitor growth, track vaccinations, log feeding sessions, record health visits, and follow developmental milestones — all stored locally with optional Firebase synchronisation.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Firebase Setup](#firebase-setup)
- [Database Schema](#database-schema)
- [Known Issues and Fixes Applied](#known-issues-and-fixes-applied)
- [Build Variants](#build-variants)
- [Requirements](#requirements)

---

## Features

**Profile Management**
- Multi-step guided profile setup for guardian and baby details
- Date of birth parsing with real age calculation displayed on the dashboard
- Birth weight and birth height capture
- Support for multiple baby profiles under one account

**Dashboard**
- Real baby name and age shown dynamically from the database
- Summary cards for next vaccine, growth status, and last feeding time
- Quick-action shortcuts to growth logging and medication management
- Milestone celebration banner based on actual date of birth

**Vaccination Tracker**
- Automatically seeded immunisation schedule on baby creation (BCG, OPV, DTP, Hepatitis B, MMR, and others)
- Scheduled dates calculated from the baby's actual date of birth
- Status badges: Upcoming, Due Today, Overdue, Done
- Mark-as-done button updates the record and timestamps the administered date

**Growth Chart**
- Line chart rendered with MPAndroidChart showing weight progression over time
- Add Growth Entry dialog accepts weight (kg), height (cm), head circumference (cm), and optional notes
- Statistics panel showing current weight and total gain since birth

**Feeding Log**
- Add Feeding Log dialog with type selection (Breastfeed, Formula, Solid Food)
- Fields for start time, duration in minutes, amount in ml, food item, and notes
- Chronological list of all logged sessions with time and duration display

**Health Records**
- Add Health Record dialog with type selection (Doctor Visit, Lab Report, Prescription, Illness)
- Fields for date, doctor name, clinic, and notes
- Scrollable vault of all past records

**Developmental Milestones**
- Automatically seeded milestone checklist on baby creation (months 1 through 12)
- Checkbox toggle persists achieved status to the database with animated progress bar
- Overall development percentage calculated from actual records

**Medications**
- Schedule and track medication reminders
- Separate screen accessible from dashboard and bottom navigation

**Background Workers**
- WorkManager-based `VaccineReminderWorker` queries overdue vaccines for a given baby and surfaces notifications

---

## Architecture

The project follows a layered MVVM architecture with unidirectional data flow.

```
UI Layer (Compose Screens)
        |
        v
ViewModel (StateFlow / viewModelScope)
        |
        v
Domain Layer (Use Cases / Repository Interfaces)
        |
        v
Data Layer (Room DAOs / Firebase / DataStore)
```

- **Presentation** — Composable screens and Hilt-injected ViewModels. Each screen observes `StateFlow` from its ViewModel and sends one-shot events via lambdas.
- **Domain** — Repository interfaces and use cases that decouple the presentation layer from data sources.
- **Data** — Room database implementations, Firebase repository implementations, DataStore preference manager, and WorkManager workers.
- **DI** — Hilt modules (`AppModule`, `DatabaseModule`, `RepositoryModule`, `FirebaseModule`) wire every dependency at compile time.

---

## Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, StateFlow, Coroutines |
| Dependency Injection | Hilt (with KSP) |
| Local Database | Room (with SQLCipher encryption) |
| Navigation | Jetpack Navigation Compose |
| Image Loading | Coil |
| Charts | MPAndroidChart |
| Animations | Lottie Compose |
| Background Tasks | WorkManager (Hilt-integrated) |
| Preferences | DataStore Preferences |
| Remote / Auth | Firebase Auth, Firestore, Storage, Messaging, Crashlytics, Analytics |
| Build System | Gradle with Kotlin DSL, Version Catalog |

---

## Project Structure

```
app/src/main/java/com/example/shishu_sneh_healthcare/
|
+-- data/
|   +-- local/
|   |   +-- dao/          # Room DAOs for each entity
|   |   +-- db/           # AppDatabase definition
|   |   +-- entity/       # Room entity data classes
|   +-- preferences/      # DataStore PreferenceManager
|   +-- repository/       # Concrete repository implementations
|
+-- di/
|   +-- AppModule.kt
|   +-- DatabaseModule.kt
|   +-- FirebaseModule.kt
|   +-- RepositoryModule.kt
|
+-- domain/
|   +-- repository/       # Repository interfaces (contracts)
|   +-- use_case/         # Business logic use cases
|
+-- presentation/
|   +-- auth/             # Login screen and ViewModel
|   +-- dashboard/        # Dashboard screen and ViewModel
|   +-- feeding/          # Feeding log screen and ViewModel
|   +-- growth/           # Growth chart screen and ViewModel
|   +-- milestone/        # Milestone screen and ViewModel
|   +-- navigation/       # NavGraph and sealed Screen routes
|   +-- onboarding/       # Onboarding flow
|   +-- profile/          # Profile setup and profile view
|   +-- records/          # Health records screen and ViewModel
|   +-- settings/         # Settings screen
|   +-- splash/           # Splash screen and ViewModel
|   +-- vaccine/          # Vaccination screen and ViewModel
|   +-- medication/       # Medication screen and ViewModel
|
+-- ui/theme/             # Color, Typography, Theme definitions
+-- worker/               # VaccineReminderWorker
+-- MainActivity.kt
+-- ShishuSnehApp.kt
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11
- Android SDK with compile SDK 36

### Clone and Open

```bash
git clone https://github.com/your-org/ShishuSnehHealthcare.git
cd ShishuSnehHealthcare
```

Open the project root in Android Studio and allow Gradle to sync.

### Google Services

Firebase is integrated but the `google-services` plugin is currently commented out in `app/build.gradle.kts`. To enable Firebase features:

1. Complete the Firebase setup steps described below.
2. Uncomment the following lines in `app/build.gradle.kts`:

```kotlin
alias(libs.plugins.google.services)
alias(libs.plugins.firebase.crashlytics)
```

### Run

Select a device or emulator with API level 24 or above and press **Run**. The app will launch to the splash screen and route to onboarding or the dashboard depending on whether setup has been completed.

---

## Firebase Setup

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com).
2. Register an Android app with the package name `com.example.shishu_sneh_healthcare`.
3. Download `google-services.json` and place it in the `app/` directory.
4. Enable **Email/Password** authentication in the Firebase console.
5. Create a **Firestore** database in production mode and configure security rules as appropriate.
6. Enable **Firebase Storage** if photo attachments are required.
7. Uncomment the plugin aliases in `app/build.gradle.kts` as noted above.

---

## Database Schema

The local Room database (`AppDatabase`, version 1) contains the following tables. All child tables use `ForeignKey` with `CASCADE` delete tied to the `babies.id` column.

| Table | Key Columns |
|---|---|
| `babies` | id, name, dob (Long), gender, bloodGroup, birthWeight, birthHeight, motherName, userId |
| `vaccines` | id, babyId, name, disease, scheduledDate, givenDate, status, hospital, doctor |
| `milestones` | id, babyId, month, description, status (Yes / No), achievedDate, notes |
| `growth_entries` | id, babyId, date, weight, height, headCirc, notes |
| `feeding_logs` | id, babyId, type, startTime, duration, amount, foodItem, notes |
| `health_records` | id, babyId, type, date, doctorName, clinic, notes, attachmentUri |
| `medications` | id, babyId, ... |
| `users` | id, ... |
| `notifications` | id, ... |

The database is encrypted at rest using SQLCipher.

---

## Known Issues and Fixes Applied

The following bugs were identified and corrected during the most recent revision:

**ProfileSetupScreen**
- Date of birth was stored as the literal value `0L` instead of being parsed. Fixed by parsing the `DD/MM/YYYY` input string using `SimpleDateFormat` before constructing the `BabyEntity`.
- Birth weight and birth height fields were hardcoded to `3.2` and `50.0`. Fixed by adding input fields to the setup form and reading the entered values.
- The `userId` field was hardcoded to `"dummy_user_id"`. Fixed by reading the authenticated Firebase UID from `FirebaseAuth.getInstance().currentUser`.

**ProfileViewModel**
- After saving a new baby, vaccines and milestones were never seeded. Fixed by injecting `VaccineRepository` and `MilestoneRepository` and calling their `insertVaccines` and `insertMilestones` methods immediately after `insertBaby` returns, with scheduled dates calculated from the saved date of birth.

**DashboardViewModel / DashboardScreen**
- `loadBabies` was called with the hardcoded string `"dummy_user_id"`. Fixed to use the authenticated UID.
- The header banner ("Ananya turned 3 months today") was hardcoded. Fixed to compute the actual age in months from the selected baby's `dob` timestamp and display the real baby name.
- Summary card values (Next Vaccine, Growth, Feeding) were static strings. Fixed to derive real values from the database via the ViewModel.

**VaccinationScreen**
- No button existed to mark a vaccine as administered. Fixed by adding a "Mark as Done" action button to each `VaccineCard` that calls `viewModel.updateVaccineStatus`.

**GrowthChartScreen**
- No UI existed to add new growth entries. Fixed by adding a FAB that opens an `AlertDialog` with weight, height, head circumference, and notes fields, wired to `viewModel.addGrowthEntry`.

**FeedingScreen**
- The FAB had no `onClick` implementation. Fixed by adding an `AlertDialog` with type, duration, amount, food item, and notes fields, wired to `viewModel.addFeedingLog`.

**HealthRecordsScreen**
- The FAB had no `onClick` implementation. Fixed by adding an `AlertDialog` with type, doctor name, clinic, and notes fields, wired to `viewModel.addRecord`.

**MilestoneScreen / MilestoneViewModel**
- Milestones were never seeded from real data and only sample placeholders were shown. Fixed by seeding the checklist during baby creation (see ProfileViewModel above) so that `loadMilestones` returns actual records.

---

## Build Variants

| Variant | Minification | Notes |
|---|---|---|
| `debug` | Disabled | Full logging, UI tooling enabled |
| `release` | Enabled (R8) | ProGuard rules applied via `proguard-rules.pro` |

---

## Requirements

- Minimum SDK: API 24 (Android 7.0 Nougat)
- Target SDK: API 36
- Compile SDK: 36
- Java compatibility: VERSION_11

---

## License
This project was made by P B Srikrishna(1JS22CS102).
This project is private. All rights reserved.
