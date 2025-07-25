package com.example.yassirtest.domain.usecase

import com.example.yassirtest.domain.model.Character
import com.example.yassirtest.domain.repository.CharacterRepository
import com.example.yassirtest.utli.Resource
import com.example.yassirtest.utli.safeApiCall
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(
        page: Int,
        name: String? = null,
        status: String? = null,
        species: String? = null
    ): Resource<List<Character>> {
        return safeApiCall {
            repository.getCharacters(page, name, status, species)
        }
    }
}

