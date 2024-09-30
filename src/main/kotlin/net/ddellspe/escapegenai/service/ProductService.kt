package net.ddellspe.escapegenai.service

import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.model.Product.ProductConstants.MAX_PRODUCT_COUNT
import net.ddellspe.escapegenai.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(var productRepository: ProductRepository) {
  var productsInitialized = false

  fun getAllProducts(): List<Product> {
    initializeProductDatabase()
    return productRepository.findAll()
  }

  fun initializeProductDatabase() {
    if (!productsInitialized) {
      val count = productRepository.count()
      if (count < MAX_PRODUCT_COUNT) {
        for (i in 1..(MAX_PRODUCT_COUNT - count)) {
          productRepository.save(Product())
        }
      }
      productsInitialized = true
    }
  }
}
