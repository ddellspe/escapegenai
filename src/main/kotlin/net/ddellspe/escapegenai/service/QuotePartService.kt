package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.QuotePart
import net.ddellspe.escapegenai.repository.QuotePartRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class QuotePartService(var quotePartRepository: QuotePartRepository) {
  fun getQuotePart(id: UUID): QuotePart {
    return quotePartRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("QuotePart with id=${id} does not exist.")
  }
}
