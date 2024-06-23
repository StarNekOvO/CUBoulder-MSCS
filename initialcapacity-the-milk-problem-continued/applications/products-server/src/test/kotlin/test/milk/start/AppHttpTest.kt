package test.milk.start

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlin.test.*
import io.milk.products.ProductService
import io.milk.products.ProductInfo
import io.milk.products.PurchaseInfo
import io.milk.start.module
import io.milk.testsupport.testDbPassword
import io.milk.testsupport.testDbUsername
import io.milk.testsupport.testJdbcUrl
import org.junit.Before
import org.junit.Test
import test.milk.TestScenarioSupport
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppHttpTest {
    private val productService = mockk<ProductService>()
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(KotlinModule())

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    private fun Application.testModule() {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        routing {
            get("/products") {
                val products = productService.findAll()
                call.respond(products)
            }

            post("/purchase") {
                val purchase = call.receive<PurchaseInfo>()
                productService.decrementBy(purchase)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    @Test
    fun testGetProducts() {
        val product = ProductInfo(105442, "milk", 130)
        every { productService.findAll() } returns listOf(product)

        withTestApplication({ testModule() }) {
            handleRequest(HttpMethod.Get, "/products").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                // Convert response content to list of products and back to string to match the format
                val expectedJson = objectMapper.writeValueAsString(listOf(product))
                val actualJson = objectMapper.writeValueAsString(objectMapper.readValue(response.content, List::class.java))
                assertEquals(expectedJson, actualJson)
            }
        }

        verify { productService.findAll() }
    }

    @Test
    fun testSaferQuantity() {
        val product = ProductInfo(105442, "milk", 130)
        val purchase = PurchaseInfo(105442, "milk", 1)
        every { productService.findBy(105442) } returns product
        every { productService.decrementBy(purchase) } just Runs
        every { productService.findAll() } returns listOf(product.copy(quantity = 129))

        withTestApplication({ testModule() }) {
            handleRequest(HttpMethod.Post, "/purchase") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(objectMapper.writeValueAsString(purchase))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }

            handleRequest(HttpMethod.Get, "/products").apply {
                val content = response.content!!
                assertTrue(content.contains("129"), "Expected to find 129 in '$content'")
            }
        }

        verify { productService.findBy(105442) }
        verify { productService.decrementBy(purchase) }
        verify { productService.findAll() }
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }
}
