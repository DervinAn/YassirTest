package com.example.yassirtest

import app.cash.turbine.test
import com.example.yassirtest.domain.model.Character
import com.example.yassirtest.domain.usecase.GetCharactersUseCase
import com.example.yassirtest.presentation.viewModel.CharacterUiState
import com.example.yassirtest.presentation.viewModel.CharacterViewModel
import com.example.yassirtest.utli.Resource
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {


    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val mockUseCase = mockk<GetCharactersUseCase>()

    @Test
    fun `fetchCharacters emits Loading then Success`() = runTest {
        val characters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                species = "Human",
                image = "https://example.com/rick.png",
                status = "Alive"
            )
        )

//        this test simulate a successful api call
        // when use case is called, return a success with character list
        coEvery { mockUseCase(any(), any(), any(), any()) } returns Resource.Success(characters)

        val viewModel = CharacterViewModel(mockUseCase)

        viewModel.fetchCharacters(reset = true)

        viewModel.uiState.test {
            val first = awaitItem()
            Assert.assertTrue(first is CharacterUiState.Loading)

            val second = awaitItem()
            Assert.assertTrue(second is CharacterUiState.Success)

            val result = second as CharacterUiState.Success
            Assert.assertEquals("Rick Sanchez", result.characters.first().name)

            cancel()
        }
    }

    @Test
    fun `fetchCharacters emits Error on API failure`() = runTest {

        // given
//        this test simulates an api error
        coEvery { mockUseCase(any(), any(), any(), any()) } returns Resource.Error("Network error")

        val viewModel = CharacterViewModel(mockUseCase)

        // where
        viewModel.fetchCharacters(reset = true)

        // then
        viewModel.uiState.test {
            assert(awaitItem() is CharacterUiState.Loading)
            val second = awaitItem()
            assert(second is CharacterUiState.Error)
            assertEquals("Network error", (second as CharacterUiState.Error).message)
            cancel()
        }
    }
}