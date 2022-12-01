package com.muzafferus.plugins

import com.muzafferus.routes.getAllHeroes
import com.muzafferus.routes.root
import com.muzafferus.routes.searchHeroes
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        searchHeroes()

        static("/images") {
            resources("images")
        }
    }
}
