# BDE Event App - Room + MVVM + Jetpack Compose Architecture

## Overview
This Android app has been migrated from a SQLiteOpenHelper-based implementation to a modern architecture using:
- **Room** for database persistence
- **MVVM** (Model-View-ViewModel) architecture pattern
- **Jetpack Compose** for declarative UI
- **Kotlin Coroutines** and **Flow** for reactive programming

## Architecture Components

### 1. Data Layer (`com.example.bde_event.data`)

#### Entities (`data/entities/`)
Room database entities representing the database schema:
- **EventEntity**: Stores event information (name, date, duration, description, etc.)
- **TypeOfEventEntity**: Event categories (Concert, Conférence, Sport, etc.)
- **UserEntity**: User accounts
- **EmailEntity**: Newsletter email subscriptions

#### DAO (`data/dao/`)
- **EventDao**: Data Access Object providing Room queries
  - `getEventsFlow()`: Returns Flow<List<EventEntity>> with optional filters:
    - `minDate`: Filter out past events (nullable)
    - `query`: Text search on name/description (nullable)
  - `insertEvent()`, `insertType()`: Insert operations

#### Database (`data/`)
- **AppDatabase**: Room database singleton
  - Automatically prepopulates with sample data on first run
  - Contains 6 sample events (mix of past and future)
  - 3 event types (Concert, Conférence, Sport)

#### Repository (`data/`)
- **Repository**: Mediates between ViewModel and DAO
  - Converts UI parameters to DAO query parameters
  - Manages the showPast filter and search query

### 2. UI Layer (`com.example.bde_event.ui`)

#### ViewModel (`ui/`)
- **MainViewModel**: Manages UI state and business logic
  - `events`: StateFlow<List<EventEntity>> - automatically updates UI
  - `isAuthor`: StateFlow<Boolean> - authentication state
  - `setQuery()`, `setShowPast()`: Update filter parameters
  - `loginAsAuthor()`, `logout()`: Authentication actions
  
- **MainViewModelFactory**: Factory to create ViewModel with Repository dependency

#### Composables (`ui/`)
- **EventListScreen**: Main UI composable
  - Observes ViewModel state with `collectAsState()`
  - Automatically refreshes when data changes
  - Features:
    - Search field for real-time filtering
    - Switch to show/hide past events
    - Auth buttons (Connexion/Déconnexion/Espace administrateur)
    - Subscribe button
    - LazyColumn of event items

### 3. Legacy Models (Root package)
These remain for UI compatibility:
- **Event, TypeOfEvent, User, Email**: Domain models
- **EventItemComposable**: Reusable UI component for displaying events
- **EventEntity.toDomain()**: Mapper function to convert entities to domain models

### 4. Legacy SQLite Implementation
The old SQLite code is preserved but not used:
- **AppDatabaseHelper**: Old SQLiteOpenHelper (can be removed)
- **UserDatabaseHelper**: Old helper (can be removed)

## Key Features

### Reactive Data Flow
```
User Input → ViewModel State → Repository Query → DAO Flow → UI Update
```

1. User types in search box → ViewModel.setQuery()
2. Query StateFlow updates → triggers Room query via Flow
3. Room observes DB changes → emits new event list
4. UI observes StateFlow → automatically recomposes with new data

### Prepopulated Sample Data
On first app launch, the database is populated with:
- **Future Events**:
  - Concert: The Composers (tomorrow)
  - Basketball Tournament (in 3 days)
  - Conférence: Compose for Mobile (in 7 days)
  - Tech Talk: AI and Machine Learning (in 14 days)
  
- **Past Events**:
  - Retro Party (3 days ago)
  - Old Concert (10 days ago)

### Filtering Logic
- **Hide Past Events** (default): Only shows events where `dateMillis >= System.currentTimeMillis()`
- **Show Past Events**: Shows all events regardless of date
- **Search**: Filters by title or description using SQL LIKE

## Dependencies Added

```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Building and Running

### Prerequisites
- Android Studio Hedgehog or later
- Kotlin 1.9.20+
- Min SDK 34, Target SDK 36

### Build
```bash
./gradlew assembleDebug
```

### Run
1. Open project in Android Studio
2. Sync Gradle files
3. Run on emulator or device (API 34+)

### First Launch
- App will create database and prepopulate with sample events
- UI will display future events by default
- Toggle "Afficher les événements passés" to see past events
- Use search field to filter by title/description

## Testing the Architecture

### Verify Automatic UI Updates
1. **Search Functionality**: Type in search field → UI updates in real-time
2. **Toggle Past Events**: Switch on/off → Event list filters immediately
3. **Authentication**: Click Connexion → buttons change to Déconnexion + Admin

### Verify Data Persistence
1. **First Launch**: 6 events appear
2. **Close and Reopen**: Same events persist (not repopulated)
3. **Clear App Data**: Events are repopulated on next launch

## Migration Notes

### What Changed
- ✅ Replaced `AppDatabaseHelper` (SQLiteOpenHelper) with Room
- ✅ Replaced manual threading with Coroutines
- ✅ Replaced callbacks/listeners with StateFlow
- ✅ UI automatically observes data changes
- ✅ Type-safe database operations

### What Stayed
- ✅ Same UI layout and functionality
- ✅ Same database schema (tables and columns)
- ✅ Same filter and search behavior
- ✅ Domain models (Event, TypeOfEvent, etc.) for UI layer

### Future Enhancements
- [ ] Replace simulated auth with DataStore or secure storage
- [ ] Implement navigation to SubscribeActivity and AdminActivity
- [ ] Add event details screen
- [ ] Add CRUD operations for admin users
- [ ] Add unit and integration tests
- [ ] Add Hilt/Koin for dependency injection

## Troubleshooting

### Build Errors
- **Room schema export**: Set `exportSchema = false` in @Database (already done)
- **KSP not found**: Ensure KSP plugin is in root build.gradle.kts
- **Compose issues**: Check compose-compiler version matches Kotlin version

### Runtime Errors
- **Database not found**: Check `AppDatabase.getInstance(context)` is called
- **Flow not collecting**: Ensure `collectAsState()` is used in composables
- **Events not updating**: Verify DAO returns Flow, not suspend list

## Architecture Diagram

```
┌─────────────────────────────────────────────┐
│           MainActivity                       │
│  (Sets up Database, Repository, ViewModel)  │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│          EventListScreen                    │
│  (Composable observing ViewModel)          │
│  - collectAsState() for events              │
│  - Search field → setQuery()                │
│  - Switch → setShowPast()                   │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│           MainViewModel                     │
│  - events: StateFlow<List<EventEntity>>    │
│  - Combines query + showPast filters        │
│  - flatMapLatest → Repository               │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│            Repository                       │
│  - Converts UI params to DAO params         │
│  - getEventsFlow(showPast, query)           │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│             EventDao                        │
│  - getEventsFlow(minDate, query): Flow     │
│  - Room automatically observes DB           │
└─────────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│           AppDatabase (Room)                │
│  - EventEntity, TypeOfEventEntity, etc.     │
│  - Prepopulates on first run                │
└─────────────────────────────────────────────┘
```

## License
This project is for educational purposes.
