package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.TeamInvoice
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface TeamInvoiceRepository : ListCrudRepository<TeamInvoice, UUID> {}
