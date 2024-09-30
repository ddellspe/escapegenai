package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.model.Product.ProductConstants.MAX_PRODUCT_COUNT
import net.ddellspe.escapegenai.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductServiceTest {
  private val productRepository: ProductRepository = mockk()
  private val productService = ProductService(productRepository)
  private val product: Product = mockk()

  @BeforeEach
  fun setup() {
    productService.productsInitialized = false
  }

  @Test
  fun dumbCoverageTests() {
    productService.productRepository = productRepository
  }

  @Test
  fun whenFindAllHasNoProducts_ReturnNoProducts() {
    productService.productsInitialized = true
    every { productRepository.findAll() } returns emptyList()

    val result = productService.getAllProducts()

    verify(exactly = 1) { productRepository.findAll() }
    assertEquals(0, result.size)
  }

  @Test
  fun whenFindAllHasProducts_ReturnNoProducts() {
    productService.productsInitialized = true
    every { productRepository.findAll() } returns listOf(product)

    val result = productService.getAllProducts()

    verify(exactly = 1) { productRepository.findAll() }
    assertEquals(1, result.size)
    assertEquals(product, result.get(0))
  }

  @Test
  fun whenNotInitialized_andCountZero_addProducts() {
    every { productRepository.count() } returns 0
    every { productRepository.save(any()) } returns product

    productService.initializeProductDatabase()

    verify(exactly = 1) { productRepository.count() }
    verify(exactly = MAX_PRODUCT_COUNT) { productRepository.save(any()) }
    assertTrue(productService.productsInitialized)
  }

  @Test
  fun whenNotFullyInitialized_andCountLessThanMax_addProducts() {
    every { productRepository.count() } returns 20
    every { productRepository.save(any()) } returns product

    productService.initializeProductDatabase()

    verify(exactly = 1) { productRepository.count() }
    verify(exactly = (MAX_PRODUCT_COUNT - 20)) { productRepository.save(any()) }
    assertTrue(productService.productsInitialized)
  }

  @Test
  fun whenNotFullyInitialized_andCountEqualToMax_addProducts() {
    every { productRepository.count() } returns MAX_PRODUCT_COUNT.toLong()
    every { productRepository.save(any()) } returns product

    productService.initializeProductDatabase()

    verify(exactly = 1) { productRepository.count() }
    verify(exactly = 0) { productRepository.save(any()) }
    assertTrue(productService.productsInitialized)
  }
}
