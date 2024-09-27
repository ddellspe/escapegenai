package net.ddellspe.escapegenai.repository

import net.ddellspe.escapegenai.model.Invoice
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface InvoiceRepository : ListCrudRepository<Invoice, Int> {}
