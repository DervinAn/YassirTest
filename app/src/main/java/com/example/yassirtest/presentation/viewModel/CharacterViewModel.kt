package com.example.yassirtest.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yassirtest.domain.usecase.GetCharactersUseCase
import com.example.yassirtest.utli.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.yassirtest.domain.model.Character

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val uiState: StateFlow<CharacterUiState> = _uiState
    private var allCharacters: List<Character> = emptyList()
    private var currentPage = 1

    private var query: String? = null
    private var lastStatus: String? = null
    private var lastSpecies: String? = null





    fun applyFilters(status: String?, species: String?) {
        lastStatus = status
        lastSpecies = species
        fetchCharacters(reset = true, status = status, species = species)
    }

    var isPaginating = false
        private set

    var isLastPage = false
        private set



fun searchCharacters(query: String) {
    if (query.isBlank()) {
        _uiState.value = CharacterUiState.Success(allCharacters)
        return
    }

    val normalizedQuery = query.trim().lowercase()

    val matchedCharacters = allCharacters.filter {
        it.name.trim().lowercase().contains(normalizedQuery)
    }

    _uiState.value = CharacterUiState.Success(matchedCharacters)
}


    private var isFetching = false

    fun fetchCharacters(
        reset: Boolean = false,
        status: String? = lastStatus,
        species: String? = lastSpecies
    ) {
        if (reset) {
            currentPage = 1
            isLastPage = false
            allCharacters = emptyList()
        }

        if (isPaginating || isLastPage || isFetching) return

        viewModelScope.launch {
            _uiState.value = if (reset) CharacterUiState.Loading else _uiState.value
            isPaginating = true
            isFetching = true

            val result = getCharactersUseCase(currentPage, query, status, species)

            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data.isEmpty()) {
                        // No more characters
                        isLastPage = true
                    } else {
                        allCharacters = if (reset) data else allCharacters + data
                        _uiState.value = CharacterUiState.Success(allCharacters)
                        currentPage++
                    }
                }

                is Resource.Error -> {
                    if (result.message.contains("404", ignoreCase = true)) {
                        isLastPage = true
                    } else {
                        _uiState.value = CharacterUiState.Error(result.message)
                    }
                }

                else -> Unit
            }

            isPaginating = false
            isFetching = false
        }
    }
}
