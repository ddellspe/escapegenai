package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.InvoiceProduct
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface InvoiceProductRepository : ListCrudRepository<InvoiceProduct, UUID> {}
