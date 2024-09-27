package net.ddellspe.escapegenai.controller

import net.ddellspe.escapegenai.model.Product
import net.ddellspe.escapegenai.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ProductController(var productService: ProductService) {
  @GetMapping("/products")
  fun getProducts(): ResponseEntity<List<Product>> {
    val products: List<Product> = productService.getAllProducts()
    return ResponseEntity.ok(products)
  }
}
