package net.ddellspe.escapegenai.repository

import net.ddellspe.escapegenai.model.Product
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface ProductRepository : ListCrudRepository<Product, Int> {}
