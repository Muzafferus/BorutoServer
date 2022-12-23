package com.muzafferus.plugins

import com.muzafferus.routes.getAllHeroes
import com.muzafferus.routes.getAllHeroesAlternative
import com.muzafferus.routes.root
import com.muzafferus.routes.searchHeroes
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import javax.naming.AuthenticationException

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        getAllHeroesAlternative()
        searchHeroes()

        get("/test2") {
            throw AuthenticationException()
        }

        static("/images") {
            resources("images")
        }
    }
}
