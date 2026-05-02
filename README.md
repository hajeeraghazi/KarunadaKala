# 🎭 Karunada-Kala Source
### MindMatrix VTU Internship Programme — Project Title :90
**Android App Development using GenAI — National Pride**

---

## 📱 About the App

Karunada-Kala Source is a cultural discovery Android application that maps Karnataka's
traditional art forms, artisans, and performances. It serves as a "Directory of Pride"
for Karnataka's living heritage, enabling users to:

- **Explore** 5+ traditional art forms (Yakshagana, Bidriware, Kinnala Toys, Ilkal Saree, Dollu Kunitha)
- **Locate** artisans on an interactive Google Map with custom markers
- **Call** artisans directly with one tap
- **Discover** upcoming cultural events and performances
- **Register** interest in workshops
- **Get AI descriptions** of art forms powered by Google Gemini

---

## 🏗️ Architecture

```
MVVM + Repository Pattern
├── View Layer        → Jetpack Compose Composables (zero business logic)
├── ViewModel Layer   → StateFlow, Coroutines, Hilt injection
├── Repository Layer  → Firestore abstraction, Gemini API abstraction
└── Data Layer        → Firebase Firestore, Google Gemini API
```

---

## 🛠️ Tech Stack

| Component        | Technology                        |
|------------------|-----------------------------------|
| Language         | Kotlin                            |
| UI               | Jetpack Compose + Material 3      |
| Architecture     | MVVM + Repository                 |
| DI               | Hilt                              |
| Cloud DB         | Firebase Firestore                |
| Maps             | Google Maps SDK + Maps Compose    |
| Images           | Glide                             |
| GenAI            | Google Gemini API (gemini-1.5-flash)|
| Async            | Kotlin Coroutines + StateFlow     |
| Navigation       | Jetpack Navigation Compose        |
| Min SDK          | API 28 (Android 9.0)              |

---

## 🚀 Setup Instructions

### Step 1 — Clone & Open
```bash
git clone <your-repo-url>
# Open in Android Studio Hedgehog or newer
```

### Step 2 — Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create project: **KarunadaKala**
3. Add Android app with package: `com.mindmatrix.karunadakala`
4. Download `google-services.json` → place in `app/` folder
5. Enable **Firestore Database** (test mode is fine for demo)
6. Enable **Firebase Storage** (optional, for artisan photos)

### Step 3 — API Keys
Create `local.properties` in the project root (copy from `local.properties.template`):
```properties
MAPS_API_KEY=your_google_maps_api_key
GEMINI_API_KEY=your_gemini_api_key
sdk.dir=/path/to/your/Android/sdk
```

**Get Maps API Key:**
1. [Google Cloud Console](https://console.cloud.google.com/)
2. Enable **Maps SDK for Android**
3. Create an API key, restrict to your package name

**Get Gemini API Key:**
1. [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create a free API key

### Step 4 — Seed Firestore Data
The app needs initial data in Firestore. Two ways to seed it:

**Option A (Recommended) — Temporary Admin Screen:**
Add this route temporarily to `NavGraph.kt`:
```kotlin
composable("admin/seed") {
    val vm: SeedViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    SeedDataScreen(
        onSeedClick    = vm::seedData,
        isSeedingDone  = state.isDone,
        isSeedingError = state.error,
        isSeeding      = state.isSeeding
    )
}
```
Run the app, navigate to `admin/seed`, tap **Seed Firestore Data**.
Remove the route after seeding.

**Option B — Firebase Console:**
Manually create collections `artforms`, `artisans`, `events` using the data
in `KarunadaRepository.kt` → `seedFirestore()` function.

### Step 5 — Build & Run
```bash
# Sync Gradle, then Run on emulator or device
```

---

## 📂 Project Structure

```
app/src/main/java/com/mindmatrix/karunadakala/
├── KarunadaKalaApp.kt              # Hilt Application class
├── MainActivity.kt                 # Single activity entry point
├── data/
│   ├── model/
│   │   └── Models.kt               # ArtForm, Artisan, Event, WorkshopSignup
│   └── repository/
│       ├── KarunadaRepository.kt   # Firestore operations + seed data
│       └── GeminiRepository.kt     # Gemini API calls
├── di/
│   └── AppModule.kt                # Hilt DI — Firestore + Gemini providers
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt             # Nav routes + bottom navigation
│   ├── screens/
│   │   ├── SharedComponents.kt     # Reusable composables
│   │   ├── home/HomeScreen.kt      # Home with events + featured arts
│   │   ├── explorer/               # Art Form grid with search
│   │   ├── detail/                 # Art form detail + Gemini AI
│   │   ├── map/                    # Google Maps with custom markers
│   │   ├── profile/                # Artisan profile + Tap to Call
│   │   ├── events/                 # Upcoming events list
│   │   ├── signup/                 # Workshop registration form
│   │   └── admin/                  # Debug seed screen
│   └── theme/
│       ├── Color.kt                # Karnataka flag color palette
│       └── Theme.kt                # Material 3 theme
└── viewmodel/
    ├── ViewModels.kt               # Home, Explorer, Detail, Map, Profile, Events, Signup
    └── SeedViewModel.kt            # Admin seed ViewModel
```

---

## ✅ Feature Checklist (Success Criteria)

| Criterion | Feature | Status |
|-----------|---------|--------|
| SC-1 | Map shows different icons for Workshops (blue) vs Performances (red) | ✅ |
| SC-2 | Artisan Profile has Tap to Call (Intent.ACTION_DIAL) | ✅ |
| SC-3 | UI uses Karnataka flag yellow (#FDD835) and red (#C62828) | ✅ |
| SC-4 | Art Form Explorer with catalog and history | ✅ |
| SC-5 | GenAI visibly integrated (Gemini API on Detail screen) | ✅ |
| SC-6 | Event feed for upcoming performances | ✅ |

---

## 🎨 Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Karnataka Yellow | `#FDD835` | CTA buttons, AI card |
| Karnataka Red | `#C62828` | Call button, event badges, performance markers |
| Primary Indigo | `#1A237E` | App bars, headings |
| Workshop Blue | `#1565C0` | Workshop map markers, badges |

---

## 📋 Firestore Collections

```
artforms/{id}     → name, district, category, history, imageUrl, thumbnailUrl
artisans/{id}     → name, artFormId, artFormName, district, type, phone, bio, photoUrl, lat, lng
events/{id}       → title, artFormId, artFormName, date, venue, district, imageUrl
signups/{id}      → name, phone, artFormInterest, timestamp
```

---

## 🤖 GenAI Integration

- **API:** Google Gemini (gemini-1.5-flash)
- **Screen:** Art Form Detail
- **Trigger:** "Generate AI Description" button
- **Prompt:** Generates a 3-sentence cultural description of the art form
- **UI:** Displays in a purple card labelled "AI-Generated · Powered by Gemini"
- **Caching:** Response cached in ViewModel for the session
- **Error Handling:** User-friendly error card if API fails

---

## 🔑 Key Files to Review for Evaluation

| What to Show | File |
|---|---|
| MVVM architecture | `ViewModels.kt` + `KarunadaRepository.kt` |
| GenAI integration | `GeminiRepository.kt` + `ArtFormDetailScreen.kt` |
| Google Maps + custom markers | `ArtisanMapScreen.kt` |
| Tap to Call | `ArtisanProfileScreen.kt` (Intent.ACTION_DIAL) |
| Karnataka colors | `Color.kt` + `Theme.kt` |
| Firestore schema | `KarunadaRepository.kt` → `seedFirestore()` |

---

## 📹 Demo Script (2-3 minutes)

1. **[0:00]** Launch app → Show home screen with events feed and Karnataka theme
2. **[0:20]** Tap "Explore Arts" → Show grid, search "Yaksha" → filter works
3. **[0:40]** Tap Yakshagana card → Detail screen, history text visible
4. **[1:00]** Tap "Generate AI Description" → Spinner → Gemini text appears (GenAI moment!)
5. **[1:20]** Tap Map tab → Karnataka map loads with blue + red markers
6. **[1:35]** Tap a red marker → Bottom sheet appears with artisan preview
7. **[1:45]** Tap "View Full Profile" → Artisan Profile screen
8. **[1:55]** Tap "Tap to Call" → Phone dialler opens (highlight this clearly!)
9. **[2:10]** Navigate to Events tab → Show dated event cards
10. **[2:25]** Go back → Tap "Register for Workshop" → Fill form → Submit → Success screen
11. **[2:45]** Point out Karnataka yellow/red colors throughout the UI

---

## 📄 License
MindMatrix VTU Internship Programme — Project 90 — For educational use only.
