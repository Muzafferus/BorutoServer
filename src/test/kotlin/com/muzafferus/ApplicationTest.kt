package com.muzafferus

import com.muzafferus.models.ApiResponse
import com.muzafferus.repository.HeroRepository
import com.muzafferus.repository.NEXT_PAGE_KEY
import com.muzafferus.repository.PREVIOUS_PAGE_KEY
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Welcome to Boruto API!",
                    actual = response.content
                )
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint page default, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                val expected = ApiResponse(
                    success = true,
                    message = "OK",
                    prevPage = null,
                    nextPage = 2,
                    heroes = heroRepository.page1,
                    lastUpdated = actual.lastUpdated
                )

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=$page").apply {
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                    val expected = ApiResponse(
                        success = true,
                        message = "OK",
                        prevPage = heroRepository.calculatePage(page = page)[PREVIOUS_PAGE_KEY],
                        nextPage = heroRepository.calculatePage(page = page)[NEXT_PAGE_KEY],
                        heroes = heroes[page - 1],
                        lastUpdated = actual.lastUpdated
                    )

                    assertEquals(
                        expected = expected,
                        actual = actual
                    )
                }
            }

        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint with error NumberFormatException, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=aa").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Only Numbers Allowed."
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint with error IllegalArgumentException, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=0").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Heroes not Found."
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access calculatePage method, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            pages.forEach { page ->
                val expected = mapOf(
                    PREVIOUS_PAGE_KEY to if (page == 1) null else page - 1,
                    NEXT_PAGE_KEY to if (page == 5) null else page + 1
                )
                val actual = heroRepository.calculatePage(page)

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes.size
                assertEquals(expected = 1, actual = actual)
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert multiple hero result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes.size
                assertEquals(expected = 3, actual = actual)
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query invalid name, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=muzaffar").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint, assert not found`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(expected = HttpStatusCode.NotFound, actual = response.status())
                assertEquals(expected = "Page not Found.", actual = response.content)
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint 2, assert AuthenticationException`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/test2").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                assertEquals(expected = "We caught an exception.", actual = response.content)
            }
        }
    }


    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=awa").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "OK",
                    prevPage = null,
                    nextPage = null,
                    heroes = arrayListOf(heroRepository.page3[0])
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

}