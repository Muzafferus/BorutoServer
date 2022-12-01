package com.muzafferus.routes

import com.muzafferus.repository.HeroRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHeroes() {

    val heroesRepository: HeroRepository by inject()

    get("/boruto/heroes/search") {
        val name = call.request.queryParameters["name"]

        val apiResponse = heroesRepository.searchHeroes(name = name)

        call.respond(
            message = apiResponse,
            status = HttpStatusCode.OK
        )

    }


}