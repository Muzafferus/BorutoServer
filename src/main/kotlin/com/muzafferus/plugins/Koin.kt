package com.muzafferus.plugins

import com.muzafferus.di.koinModule
import io.ktor.application.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.modules
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin){
        //slf4jLogger()
        modules(koinModule)
    }
}