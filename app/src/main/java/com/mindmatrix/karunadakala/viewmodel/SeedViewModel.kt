package com.mindmatrix.karunadakala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.karunadakala.data.repository.KarunadaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeedUiState(
    val isSeeding: Boolean  = false,
    val isDone: Boolean     = false,
    val error: String?      = null
)

@HiltViewModel
class SeedViewModel @Inject constructor(
    private val repo: KarunadaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeedUiState())
    val uiState: StateFlow<SeedUiState> = _uiState.asStateFlow()

    fun seedData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSeeding = true, error = null) }
            try {
                repo.seedFirestore()
                _uiState.update { it.copy(isSeeding = false, isDone = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSeeding = false, error = e.message ?: "Seeding failed") }
            }
        }
    }
}
