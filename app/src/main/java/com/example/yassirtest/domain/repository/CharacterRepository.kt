package com.example.yassirtest.domain.repository


import com.example.yassirtest.domain.model.Character

interface CharacterRepository {
    suspend fun getCharacters(page: Int, name: String?, status: String?, species: String?): List<Character>
}
