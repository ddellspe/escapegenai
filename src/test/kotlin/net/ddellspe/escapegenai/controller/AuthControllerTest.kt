package net.ddellspe.escapegenai.controller

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class AuthControllerTest {
  private val authController = AuthController()

  @Test
  fun whenRequestMadeGetResponse() {
    val expected: Map<String, Any> = mapOf("authenticated" to true)

    val result: ResponseEntity<Map<String, Any>> = authController.getSession()

    Assertions.assertNotNull(result.body)
    Assertions.assertEquals(HttpStatus.OK, result.statusCode)
    Assertions.assertEquals(expected, result.body)
  }
}
