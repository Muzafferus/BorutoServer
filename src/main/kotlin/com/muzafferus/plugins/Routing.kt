package com.muzafferus.plugins

import com.muzafferus.routes.getAllHeroes
import com.muzafferus.routes.getAllHeroesAlternative
import com.muzafferus.routes.root
import com.muzafferus.routes.searchHeroes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import javax.naming.AuthenticationException

//heroku ps:scale web=1  ===run app
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
