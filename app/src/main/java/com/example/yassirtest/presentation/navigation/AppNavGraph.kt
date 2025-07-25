package com.example.yassirtest.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yassirtest.presentation.ui.CharacterDetailScreen
import com.example.yassirtest.presentation.ui.CharacterListScreen
import com.example.yassirtest.presentation.viewModel.CharacterUiState
import com.example.yassirtest.presentation.viewModel.CharacterViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val viewModel: CharacterViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "characterList") {
        composable("characterList") {
            CharacterListScreen(
                viewModel = viewModel,
                onCharacterClick = { character ->
                    navController.navigate("characterDetail/${character.id}")
                }
            )
        }

        composable(
            "characterDetail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId")
            characterId?.let { id ->
                val character = viewModel.uiState.value
                    .let { state ->
                        if (state is CharacterUiState.Success) {
                            state.characters.find { it.id == id }
                        } else null
                    }

                character?.let {
                    CharacterDetailScreen(it,navController)
                }
            }
        }
    }
}
