# Migration Summary: SQLite to Room + MVVM

## Before and After Comparison

### Before (SQLiteOpenHelper approach)
```kotlin
// MainActivity.kt - Old approach
class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabaseHelper  // Direct DB access
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabaseHelper(this)
        
        setContent {
            EventListScreen(
                db = db,  // Pass DB directly to UI
                isAuthor = prefs.getBoolean("isAuthor", false),
                onLogin = { prefs.edit { putBoolean("isAuthor", true) } },
                ...
            )
        }
    }
}

// EventListScreen - Old approach
@Composable
fun EventListScreen(db: AppDatabaseHelper, ...) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    
    LaunchedEffect(query, showPast) {
        loading = true
        events = withContext(Dispatchers.IO) {
            db.getEvents(showPast, query)  // Manual thread switching
        }
        loading = false
    }
    // UI manually updates events state
}

// AppDatabaseHelper.kt - Old approach
class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(...) {
    fun getEvents(showPast: Boolean, query: String?): List<Event> {
        val db = readableDatabase
        val where = mutableListOf<String>()
        // Manual SQL string building
        val cursor = db.query(TABLE_EVENT, ..., selection, args, ...)
        // Manual cursor parsing
        return list
    }
}
```

### After (Room + MVVM approach)
```kotlin
// MainActivity.kt - New approach
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize with proper architecture
        val database = AppDatabase.getInstance(this)
        val repository = Repository(database.eventDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        
        setContent {
            EventListScreen(
                viewModel = viewModel,  // Pass ViewModel, not DB
                ...
            )
        }
    }
}

// EventListScreen - New approach
@Composable
fun EventListScreen(viewModel: MainViewModel, ...) {
    val events by viewModel.events.collectAsState()  // Automatic updates
    val isAuthor by viewModel.isAuthor.collectAsState()
    
    var query by rememberSaveable { mutableStateOf("") }
    var showPast by rememberSaveable { mutableStateOf(false) }
    
    // Propagate UI state to ViewModel
    LaunchedEffect(query) { viewModel.setQuery(query) }
    LaunchedEffect(showPast) { viewModel.setShowPast(showPast) }
    
    // UI automatically recomposes when StateFlow emits
}

// MainViewModel.kt - New approach
class MainViewModel(private val repo: Repository) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _showPast = MutableStateFlow(false)
    
    // Combine filters and react to changes
    private val eventsFlow = combine(_query, _showPast) { q, sp ->
        Pair(q, sp)
    }.flatMapLatest { (q, sp) ->
        repo.getEventsFlow(showPast = sp, query = q)
    }
    
    val events: StateFlow<List<EventEntity>> = eventsFlow.stateIn(
        viewModelScope, SharingStarted.Lazily, emptyList()
    )
    
    fun setQuery(q: String) { _query.value = q }
    fun setShowPast(show: Boolean) { _showPast.value = show }
}

// EventDao.kt - New approach
@Dao
interface EventDao {
    @Query("""
      SELECT * FROM event
      WHERE (:minDate IS NULL OR dateMillis >= :minDate)
        AND (:query IS NULL OR name LIKE :query OR description LIKE :query)
      ORDER BY dateMillis DESC
    """)
    fun getEventsFlow(minDate: Long?, query: String?): Flow<List<EventEntity>>
    // Type-safe, reactive, no manual cursor handling
}

// AppDatabase.kt - New approach
@Database(entities = [EventEntity::class, ...], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    
    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(...)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        CoroutineScope(Dispatchers.IO).launch {
                            prepopulate(getInstance(context).eventDao())
                        }
                    }
                })
                .build()
        }
    }
}
```

## Key Improvements

### 1. **Separation of Concerns**
- **Before**: UI directly accesses database
- **After**: UI → ViewModel → Repository → DAO → Database

### 2. **Reactive Programming**
- **Before**: Manual state updates with LaunchedEffect + withContext
- **After**: Flow-based reactive streams, automatic UI updates

### 3. **Type Safety**
- **Before**: Manual SQL strings, cursor parsing, potential runtime errors
- **After**: Compile-time checked queries, auto-generated code

### 4. **Threading**
- **Before**: Manual Dispatchers.IO switching, potential errors
- **After**: Room handles threading automatically

### 5. **Testability**
- **Before**: Hard to test, tight coupling to Android framework
- **After**: Easy to test, can mock Repository in ViewModel tests

### 6. **State Management**
- **Before**: SharedPreferences + mutable state in composables
- **After**: Centralized state in ViewModel using StateFlow

### 7. **Data Prepopulation**
- **Before**: No sample data
- **After**: Database prepopulated with 6 events on first launch

## File Structure

### New Files Created
```
app/src/main/java/com/example/bde_event/
├── data/
│   ├── AppDatabase.kt          (Room database singleton)
│   ├── Repository.kt            (Repository pattern)
│   ├── dao/
│   │   └── EventDao.kt         (Data access interface)
│   └── entities/
│       ├── EventEntity.kt      (Room entity)
│       ├── TypeOfEventEntity.kt (Room entity)
│       ├── UserEntity.kt        (Room entity)
│       └── EmailEntity.kt       (Room entity)
└── ui/
    ├── MainViewModel.kt         (ViewModel with StateFlow)
    ├── MainViewModelFactory.kt  (Factory for DI)
    └── EventListScreen.kt       (Observing composable)
```

### Modified Files
```
MainActivity.kt          - Now initializes ViewModel instead of DB
app/build.gradle.kts     - Added Room, KSP, ViewModel dependencies
build.gradle.kts         - Added KSP plugin, fixed AGP version
settings.gradle.kts      - Simplified repository configuration
.gitignore              - Added gradle wrapper files
```

### Legacy Files (Can be removed)
```
AppDatabaseHelper.kt     - Replaced by Room
UserDatabaseHelper.kt    - Not used in new architecture
```

## Dependencies Added

```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// ViewModel + Compose integration
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Data Flow Diagram

```
┌──────────────┐
│  User Input  │
│ (Search box, │
│   Switch)    │
└──────┬───────┘
       │
       ▼
┌─────────────────────────────┐
│     EventListScreen         │
│  var query by remember      │
│  LaunchedEffect(query) {    │
│    viewModel.setQuery(q)    │
│  }                          │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│      MainViewModel          │
│  _query = MutableStateFlow  │
│  combine(_query, _showPast) │
│  .flatMapLatest {           │
│    repo.getEventsFlow(...)  │
│  }                          │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│       Repository            │
│  fun getEventsFlow(...) {   │
│    val minDate = if(...)    │
│    dao.getEventsFlow(...)   │
│  }                          │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│        EventDao             │
│  @Query("SELECT * FROM ...  │
│    WHERE (:minDate IS NULL  │
│    OR dateMillis >= :...")  │
│  fun getEventsFlow():Flow   │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│      AppDatabase (Room)     │
│  - Observes DB changes      │
│  - Emits to Flow            │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│  UI Updates Automatically   │
│  (collectAsState() in       │
│   EventListScreen)          │
└─────────────────────────────┘
```

## Testing the Implementation

### Manual Testing Checklist
- [ ] App launches successfully
- [ ] 6 events are displayed on first launch
- [ ] Search box filters events in real-time
- [ ] "Afficher les événements passés" switch works
  - [ ] OFF: Only future events shown (4 events)
  - [ ] ON: All events shown (6 events)
- [ ] "Connexion" button changes to "Déconnexion" + "Espace administrateur"
- [ ] Events are sorted by date (most recent first)
- [ ] Closing and reopening app preserves data
- [ ] Clearing app data repopulates events

### Expected Behavior
1. **Initial State**: 4 future events displayed
2. **Toggle ON**: 6 total events (4 future + 2 past)
3. **Search "concert"**: Shows "Concert: The Composers" and "Old Concert"
4. **Search "basketball"**: Shows "Basketball Tournament"
5. **Search with toggle OFF**: Only searches future events

## Conclusion

The migration successfully:
- ✅ Eliminates boilerplate SQL code
- ✅ Provides type-safe database operations
- ✅ Enables reactive UI updates
- ✅ Follows MVVM best practices
- ✅ Separates concerns properly
- ✅ Makes code more testable
- ✅ Prepopulates with sample data

The app now follows modern Android development best practices and provides a solid foundation for future enhancements.
