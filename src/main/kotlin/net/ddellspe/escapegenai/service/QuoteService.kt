package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.Quote
import net.ddellspe.escapegenai.model.QuoteContainer
import net.ddellspe.escapegenai.repository.QuoteRepository
import net.ddellspe.escapegenai.util.generateParts
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class QuoteService(var quoteRepository: QuoteRepository) {
  fun createQuote(quoteContainer: QuoteContainer): QuoteContainer {
    if (quoteContainer.id != null) {
      if (quoteRepository.findByIdOrNull(quoteContainer.id) != null) {
        throw IllegalArgumentException(
            "Quote with id=${quoteContainer.id} already exists, please update.")
      }
    }
    val quote =
        Quote(
            id = quoteContainer.id ?: UUID.randomUUID(),
            quote = quoteContainer.quote,
            quoteParts = generateParts(quoteContainer.quote),
        )
    return quoteRepository.save(quote).toQuoteContainer()
  }

  fun updateQuote(quoteContainer: QuoteContainer): QuoteContainer {
    if (quoteContainer.id == null) {
      throw IllegalArgumentException("Quote does not exist, please create instead.")
    }
    val quote =
        quoteRepository.findByIdOrNull(quoteContainer.id)
            ?: throw IllegalArgumentException(
                "Quote with id=${quoteContainer.id} does not exist, please create instead.")
    if (quote.quote != quoteContainer.quote) {
      quote.quote = quoteContainer.quote
      quote.quoteParts = generateParts(quote.quote)
    }
    return quoteRepository.save(quote).toQuoteContainer()
  }

  fun getQuote(id: UUID): Quote {
    return quoteRepository.findByIdOrNull(id)
        ?: throw IllegalArgumentException("Quote with id=${id} does not exist.")
  }

  fun getAllQuotes(): List<Quote> {
    return quoteRepository.findAll()
  }
}
