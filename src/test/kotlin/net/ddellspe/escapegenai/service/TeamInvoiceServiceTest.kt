package net.ddellspe.escapegenai.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.TeamInvoice
import net.ddellspe.escapegenai.repository.TeamInvoiceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class TeamInvoiceServiceTest {
  private val teamInvoiceRepository: TeamInvoiceRepository = mockk()
  private val teamInvoiceService = TeamInvoiceService(teamInvoiceRepository)
  private val id = UUID.randomUUID()
  private val invoice: Invoice = mockk()
  private val teamInvoice: TeamInvoice = mockk()

  @Test
  fun dumbCoverageTests() {
    teamInvoiceService.teamInvoiceRepository = teamInvoiceRepository
  }

  @Test
  fun whenGetInvoice_idNotFound_returnError() {
    every { teamInvoiceRepository.findByIdOrNull(id) } returns null

    val result = assertThrows<IllegalArgumentException> { teamInvoiceService.getInvoice(id) }

    verify(exactly = 1) { teamInvoiceRepository.findByIdOrNull(id) }
    assertEquals("Invoice with id=$id does not exist.", result.message)
  }

  @Test
  fun whenGetInvoice_idFound_returnInvoiceObject() {
    every { teamInvoiceRepository.findByIdOrNull(id) } returns teamInvoice

    val result = teamInvoiceService.getInvoice(id)

    verify(exactly = 1) { teamInvoiceRepository.findByIdOrNull(id) }
    assertEquals(teamInvoice, result)
  }
}
