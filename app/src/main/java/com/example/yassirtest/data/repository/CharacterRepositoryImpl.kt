package com.example.yassirtest.data.repository


import com.example.yassirtest.data.remote.api.RickAndMortyApi
import com.example.yassirtest.data.remote.mapper.toDomain
import com.example.yassirtest.domain.model.Character
import com.example.yassirtest.domain.repository.CharacterRepository
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : CharacterRepository {

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?
    ): List<Character> {
        return api.getCharacters(page, name, status, species).results.map { it.toDomain() }
    }
}
