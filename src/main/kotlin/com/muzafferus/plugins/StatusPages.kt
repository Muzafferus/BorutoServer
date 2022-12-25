package com.muzafferus.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import javax.naming.AuthenticationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                message = "Page not Found.",
                status = HttpStatusCode.NotFound
            )
        }
        exception<AuthenticationException> { call, _ ->
            call.respond(
                message = "We caught an exception.",
                status = HttpStatusCode.OK
            )
        }
    }
}