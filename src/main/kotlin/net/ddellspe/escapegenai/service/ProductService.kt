package net.ddellspe.escapegenai.service

import kotlin.random.Random
import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(var productRepository: ProductRepository) {
  fun getAllProducts(): List<Product> {
    val count = productRepository.count()
    if (count < 20) {
      for (i in 1..(20 - count)) {
        productRepository.save(Product(null))
      }
    }
    return productRepository.findAll()
  }

  fun getRandomProduct(): Product {
    var product: Product? = null
    while (product == null) {
      product = productRepository.findByIdOrNull(Random.nextInt(1, 20))
    }
    return product
  }

  fun getProduct(productId: Int): Product {
    return productRepository.findById(productId).get()
  }
}
