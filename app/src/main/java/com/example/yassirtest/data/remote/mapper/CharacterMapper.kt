package com.example.yassirtest.data.remote.mapper


import com.example.yassirtest.data.remote.dto.CharacterDto
import com.example.yassirtest.domain.model.Character

fun CharacterDto.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        image = image,
        species = species,
        status = status
    )
}
