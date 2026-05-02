package com.mindmatrix.karunadakala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.karunadakala.data.model.*
import com.mindmatrix.karunadakala.data.repository.GeminiRepository
import com.mindmatrix.karunadakala.data.repository.KarunadaRepository
import com.mindmatrix.karunadakala.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ══════════════════════════════════════════════════════
//  Home ViewModel
// ══════════════════════════════════════════════════════
data class HomeUiState(
    val events: List<Event> = emptyList(),
    val featuredArtForms: List<ArtForm> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val eventsResult    = repo.getEvents()
            val artFormsResult  = repo.getArtForms()
            _uiState.update {
                it.copy(
                    isLoading       = false,
                    events          = if (eventsResult   is Result.Success) eventsResult.data.take(6) else emptyList(),
                    featuredArtForms= if (artFormsResult is Result.Success) artFormsResult.data.take(4) else emptyList(),
                    error           = when {
                        eventsResult is Result.Error -> eventsResult.message
                        else -> null
                    }
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  Art Form Explorer ViewModel
// ══════════════════════════════════════════════════════
data class ExplorerUiState(
    val artForms: List<ArtForm> = emptyList(),
    val filteredArtForms: List<ArtForm> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()

    init { loadArtForms() }

    fun loadArtForms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repo.getArtForms()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, artForms = result.data, filteredArtForms = result.data)
                }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun onSearchQuery(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.artForms
            else state.artForms.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.district.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredArtForms = filtered)
        }
    }
}

// ══════════════════════════════════════════════════════
//  Art Form Detail ViewModel
// ══════════════════════════════════════════════════════
data class DetailUiState(
    val artForm: ArtForm? = null,
    val relatedArtisans: List<Artisan> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // GenAI state
    val aiDescription: String? = null,
    val isGeneratingAi: Boolean = false,
    val aiError: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: KarunadaRepository,
    private val gemini: GeminiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadArtForm(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val artFormResult  = repo.getArtFormById(id)
            val artisanResult  = repo.getArtisansByArtForm(id)
            _uiState.update {
                it.copy(
                    isLoading       = false,
                    artForm         = if (artFormResult  is Result.Success) artFormResult.data  else null,
                    relatedArtisans = if (artisanResult  is Result.Success) artisanResult.data  else emptyList(),
                    error           = if (artFormResult  is Result.Error)   artFormResult.message else null
                )
            }
        }
    }

    fun generateAiDescription() {
        val name = _uiState.value.artForm?.name ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAi = true, aiError = null) }
            when (val result = gemini.generateArtFormDescription(name)) {
                is Result.Success -> _uiState.update { it.copy(isGeneratingAi = false, aiDescription = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isGeneratingAi = false, aiError = result.message) }
                else -> {}
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  Artisan Map ViewModel
// ══════════════════════════════════════════════════════
data class MapUiState(
    val artisans: List<Artisan> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init { loadArtisans() }

    fun loadArtisans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repo.getArtisans()) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, artisans = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  Artisan Profile ViewModel
// ══════════════════════════════════════════════════════
data class ProfileUiState(
    val artisan: Artisan? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadArtisan(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repo.getArtisanById(id)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, artisan = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  Events ViewModel
// ══════════════════════════════════════════════════════
data class EventsUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repo.getEvents()) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, events = result.data) }
                is Result.Error   -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  Workshop Signup ViewModel
// ══════════════════════════════════════════════════════
data class SignupUiState(
    val name: String = "",
    val phone: String = "",
    val artFormInterest: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String)         { _uiState.update { it.copy(name = v) } }
    fun onPhoneChange(v: String)        { _uiState.update { it.copy(phone = v) } }
    fun onArtFormChange(v: String)      { _uiState.update { it.copy(artFormInterest = v) } }

    fun submit() {
        val state = _uiState.value
        if (state.name.isBlank() || state.phone.isBlank() || state.artFormInterest.isBlank()) {
            _uiState.update { it.copy(error = "Please fill all fields") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            val signup = com.mindmatrix.karunadakala.data.model.WorkshopSignup(
                name = state.name, phone = state.phone, artFormInterest = state.artFormInterest
            )
            when (val result = repo.submitSignup(signup)) {
                is Result.Success -> _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                is Result.Error   -> _uiState.update { it.copy(isSubmitting = false, error = result.message) }
                else -> {}
            }
        }
    }
}
