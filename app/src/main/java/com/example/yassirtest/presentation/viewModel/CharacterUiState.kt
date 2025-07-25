package com.example.yassirtest.presentation.viewModel

import com.example.yassirtest.domain.model.Character

sealed class CharacterUiState {
    object Loading : CharacterUiState()
    data class Success(val characters: List<Character>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}