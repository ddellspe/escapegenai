package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.Quote
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface QuoteRepository : ListCrudRepository<Quote, UUID> {}
