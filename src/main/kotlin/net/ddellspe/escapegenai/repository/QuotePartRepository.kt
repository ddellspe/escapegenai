package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.QuotePart
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface QuotePartRepository : ListCrudRepository<QuotePart, UUID> {}
