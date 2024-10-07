package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.TeamInvoice
import net.ddellspe.escapegenai.repository.TeamInvoiceRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TeamInvoiceService(var teamInvoiceRepository: TeamInvoiceRepository) {
  fun getInvoice(uuid: UUID): TeamInvoice {
    return teamInvoiceRepository.findByIdOrNull(uuid)
      ?: throw IllegalArgumentException("Invoice with id=$uuid does not exist.")
  }
}
