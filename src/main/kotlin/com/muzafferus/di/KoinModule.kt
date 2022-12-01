package com.muzafferus.di

import com.muzafferus.repository.HeroRepository
import com.muzafferus.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}