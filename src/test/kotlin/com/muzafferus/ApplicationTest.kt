package com.muzafferus

import com.muzafferus.models.ApiResponse
import com.muzafferus.repository.HeroRepositoryImpl
import com.muzafferus.repository.NEXT_PAGE_KEY
import com.muzafferus.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `access root endpoint, assert correct information`() =
        testApplication {
            val response = client.get("/")
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status
            )
            assertEquals(
                expected = "Welcome to Boruto API!",
                actual = response.bodyAsText()
            )
        }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint page default, assert correct information`() =
        testApplication {
            val heroRepository = HeroRepositoryImpl()

            val response = client.get("/boruto/heroes")

            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status
            )

            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())

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

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() =
        testApplication {
            val heroRepository = HeroRepositoryImpl()

            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                val response = client.get("/boruto/heroes?page=$page")
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status
                )
                val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())

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

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint with error NumberFormatException, assert correct information`() =
        testApplication {
            val response = client.get("/boruto/heroes?page=aa")
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = response.status
            )
            val expected = ApiResponse(
                success = false,
                message = "Only Numbers Allowed."
            )
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())

            assertEquals(
                expected = expected,
                actual = actual
            )

        }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint with error IllegalArgumentException, assert correct information`() =
        testApplication {
            val response = client.get("/boruto/heroes?page=0")
            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = response.status
            )
            assertEquals(
                expected = "Page not Found.",
                actual = response.bodyAsText()
            )
        }

    @Test
    fun `access calculatePage method, assert correct information`() =
        testApplication {
            val heroRepository = HeroRepositoryImpl()

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

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() =
        testApplication {
            val response = client.get("/boruto/heroes/search?name=sas")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).heroes.size
            assertEquals(expected = 1, actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert multiple hero result`() =
        testApplication {
            val response = client.get("/boruto/heroes/search?name=sa")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).heroes.size
            assertEquals(expected = 3, actual = actual)

        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query invalid name, assert empty list as a result`() =
        testApplication {
            val response = client.get("/boruto/heroes/search?name=muzaffar")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).heroes
            assertEquals(expected = emptyList(), actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() =
        testApplication {
            val response = client.get("/boruto/heroes/search?name=")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText()).heroes
            assertEquals(expected = emptyList(), actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint, assert not found`() =
        testApplication {
            val response = client.get("/unknown")
            assertEquals(expected = HttpStatusCode.NotFound, actual = response.status)
            assertEquals(expected = "Page not Found.", actual = response.bodyAsText())
        }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint 2, assert AuthenticationException`() =
        testApplication {
            val response = client.get("/test2")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            assertEquals(expected = "We caught an exception.", actual = response.bodyAsText())
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert correct information`() =
        testApplication {
            val heroRepository = HeroRepositoryImpl()

            val response = client.get("/boruto/heroes/search?name=awa")
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status
            )
            val expected = ApiResponse(
                success = true,
                message = "OK",
                prevPage = null,
                nextPage = null,
                heroes = arrayListOf(heroRepository.page3[0])
            )
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())

            assertEquals(
                expected = expected,
                actual = actual
            )
        }

}