import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertNull

data class LoginRequest(val email: String, val password: String)

class ToolshopApiTest {

    val loginRequest = LoginRequest(
        "customer@practicesoftwaretesting.com",
        "welcome01"
    )

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            RestAssured.baseURI = "https://api-with-bugs.practicesoftwaretesting.com"
        }
    }

    @Test
    fun `API-1 TQ-107 Login with valid credentials`() {
        Given {
            accept("application/json")
            contentType("application/json")
            body(loginRequest)
        } When {
            post("/users/login")
        } Then {
            statusCode(200)
            body("token_type", equalToIgnoringCase("Bearer"))
            body("access_token", not(emptyOrNullString()))
            body("expires_in", greaterThan(0))
        }
    }

    @Test
    fun `API-4 TQ-110 Sort products by price ascending`() {
        val prices = Given {
            accept("application/json")
            queryParam("sort", "price,asc")
        } When {
            get("/products")
        } Then {
            statusCode(200)
            body("data.size()", greaterThan(1))
        } Extract {
            jsonPath().getList<Number>("data.price").map { it.toDouble() }
        }
        val outOfOrder = prices.zipWithNext().firstOrNull { (a, b) -> a > b }
        assertNull(outOfOrder, "Prices not sorted ascending, first out-of-order pair: $outOfOrder")
    }

    @Test
    fun `API-6 TQ-112 Get a brand that does not exist`() {
        Given {
            accept("application/json")
        } When {
            get("/brands/999999")
        } Then {
            statusCode(404)
            body("message", equalTo("Requested item not found"))
            body("size()", equalTo(1))
            body("$", not(hasKey("id")))
            body("$", not(hasKey("name")))
        }
    }
}