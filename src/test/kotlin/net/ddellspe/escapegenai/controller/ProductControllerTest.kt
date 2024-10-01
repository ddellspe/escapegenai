package net.ddellspe.escapegenai.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.service.ProductService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ProductControllerTest {
  private val productService: ProductService = mockk()
  private val productController = ProductController(productService)
  private val product: Product = mockk()

  @Test
  fun dumbCoverageTest() {
    productController.productService = productService
  }

  @Test
  fun whenNoProductsAvailable_ReturnsNoProducts() {
    every { productService.getAllProducts() } returns emptyList()

    val result: ResponseEntity<List<Product>> = productController.getProducts()

    verify(exactly = 1) { productService.getAllProducts() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(0, result.body?.size)
  }

  @Test
  fun whenProductsAvailable_ReturnsProducts() {
    every { productService.getAllProducts() } returns listOf(product)

    val result: ResponseEntity<List<Product>> = productController.getProducts()

    verify(exactly = 1) { productService.getAllProducts() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(product, result.body?.get(0))
  }
}
