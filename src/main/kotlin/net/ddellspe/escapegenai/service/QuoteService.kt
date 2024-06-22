package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.Quote
import net.ddellspe.escapegenai.repository.QuoteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class QuoteService(var quoteRepository: QuoteRepository) {
  fun getQuote(id: UUID): Quote {
    return quoteRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("Quote with id=${id} does not exist.")
  }
}
