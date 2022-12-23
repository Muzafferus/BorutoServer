package com.muzafferus.repository

import com.muzafferus.models.ApiResponse
import com.muzafferus.models.Hero

interface HeroRepositoryAlternative {

    val heroes: List<Hero>

    suspend fun getAllHeroes(page: Int = 1, limit: Int = 4): ApiResponse
    suspend fun searchHeroes(name: String?): ApiResponse
}