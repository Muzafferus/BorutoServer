package com.muzafferus.plugins

import com.muzafferus.repository.root
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        root()
    }
}
