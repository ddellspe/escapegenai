package net.ddellspe.escapegenai.service

import io.mockk.*
import java.time.OffsetDateTime
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.repository.TeamRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class TeamServiceTest {
  private val teamRepository: TeamRepository = mockk()
  private val invoiceService: InvoiceService = mockk()
  private val teamService = TeamService(teamRepository, invoiceService)
  private val team: Team = mockk()
  private val invoiceMinus: Invoice = mockk()
  private val invoiceNoDiff: Invoice = mockk()
  private val invoicePlus: Invoice = mockk()
  private val teamInvoiceMinus: TeamInvoice = mockk()
  private val teamInvoiceNoDiff: TeamInvoice = mockk()
  private val teamInvoicePlus: TeamInvoice = mockk()
  private val product1: Product = mockk()
  private val product2: Product = mockk()
  private val invoiceProduct1: InvoiceProduct = mockk()
  private val invoiceProduct2: InvoiceProduct = mockk()
  private val teamInvoices: MutableList<TeamInvoice> = mockk()
  private val teamInvoiceCaptures = mutableListOf<TeamInvoice>()
  private val dateSlot = slot<OffsetDateTime>()
  private val id = UUID.randomUUID()

  @Test
  fun dumbCoverageTest() {
    teamService.teamRepository = teamRepository
    teamService.invoiceService = invoiceService
  }

  @Test
  fun whenCreateTeam_hasNoId_thenReturnTeam() {
    val teamContainer = TeamContainer(name = "test")
    every { teamRepository.save(match { it.name == "test" }) } returns team
    every { teamRepository.save(team) } returns team
    every { team.teamInvoices } returns teamInvoices
    every { invoiceService.createNewInvoice(difference = match { it < 0 }) } returns invoiceMinus
    every { invoiceService.createNewInvoice(difference = match { it == 0 }) } returns invoiceNoDiff
    every { invoiceService.createNewInvoice(difference = match { it > 0 }) } returns invoicePlus
    every { teamInvoices.add(capture(teamInvoiceCaptures)) } returns true

    val result: Team = teamService.createTeam(teamContainer)

    verify(exactly = 2) { teamRepository.save(any()) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 3) { team.teamInvoices }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it < 0 }) }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it == 0 }) }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it > 0 }) }
    verify(exactly = 3) { teamInvoices.add(any()) }
    assertEquals(team, result)
    assertEquals(3, teamInvoiceCaptures.size)
    assertEquals(1, teamInvoiceCaptures.filter { inv -> inv.firstTask }.size)
    val invoicesFound = teamInvoiceCaptures.stream().map { teamInv -> teamInv.invoice }.toList()
    assertTrue(invoicesFound.contains(invoicePlus))
    assertTrue(invoicesFound.contains(invoiceMinus))
    assertTrue(invoicesFound.contains(invoiceNoDiff))
  }

  @Test
  fun whenCreateTeam_hasIdInRepository_thenExpectException() {
    val teamContainer = TeamContainer(id = id, name = "test")
    every { teamRepository.findByIdOrNull(id) } returns team

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.createTeam(teamContainer) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} already exists, use update instead.", exception.message)
  }

  @Test
  fun whenCreateTeam_hasIdNotInRepository_thenReturnTeam() {
    val teamContainer = TeamContainer(id = id, name = "test")
    every { teamRepository.findByIdOrNull(id) } returns null
    every { teamRepository.save(match { it.name == "test" }) } returns team
    every { teamRepository.save(team) } returns team
    every { team.teamInvoices } returns teamInvoices
    every { invoiceService.createNewInvoice(difference = match { it < 0 }) } returns invoiceMinus
    every { invoiceService.createNewInvoice(difference = match { it == 0 }) } returns invoiceNoDiff
    every { invoiceService.createNewInvoice(difference = match { it > 0 }) } returns invoicePlus
    every { teamInvoices.add(capture(teamInvoiceCaptures)) } returns true

    val result: Team = teamService.createTeam(teamContainer)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { teamRepository.save(any()) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 3) { team.teamInvoices }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it < 0 }) }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it == 0 }) }
    verify(exactly = 1) { invoiceService.createNewInvoice(difference = match { it > 0 }) }
    verify(exactly = 3) { teamInvoices.add(any()) }
    assertEquals(team, result)
    assertEquals(3, teamInvoiceCaptures.size)
    assertEquals(1, teamInvoiceCaptures.filter { inv -> inv.firstTask }.size)
    val invoicesFound = teamInvoiceCaptures.stream().map { teamInv -> teamInv.invoice }.toList()
    assertTrue(invoicesFound.contains(invoicePlus))
    assertTrue(invoicesFound.contains(invoiceMinus))
    assertTrue(invoicesFound.contains(invoiceNoDiff))
  }

  @Test
  fun whenUpdateTeam_hasNoTeam_thenThrowException() {
    every { teamRepository.findByIdOrNull(id) } returns null
    every { team.id } returns id

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.updateTeam(team) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.id }
    assertEquals("Team with id=${id} does not exist, please create it first.", exception.message)
  }

  @Test
  fun whenUpdateTeam_hasTeam_thenSaves() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.id } returns id

    val result: Team = teamService.updateTeam(team)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.id }
    assertEquals(team, result)
  }

  @Test
  fun whenGetTeam_hasTeam_thenReturnTeam() {
    every { teamRepository.findByIdOrNull(id) } returns team

    val result = teamService.getTeam(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals(team, result)
  }

  @Test
  fun whenGetTeam_hasNoTeam_thenThrowException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.getTeam(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenGetAllTeams_hasNoTeams_thenExpectEmptyList() {
    every { teamRepository.findAll() } returns emptyList()

    val result: List<Team> = teamService.getAllTeams()

    verify(exactly = 1) { teamRepository.findAll() }
    assertEquals(emptyList<Team>(), result)
  }

  @Test
  fun whenGetAllTeams_hasTeams_thenExpectListWithTeam() {
    val team2: Team = mockk()
    every { teamRepository.findAll() } returns listOf(team, team2)
    every { team.name } returns "Two"
    every { team2.name } returns "One"

    val result: List<Team> = teamService.getAllTeams()

    verify(exactly = 1) { teamRepository.findAll() }
    verify(exactly = 1) { team.name }
    verify(exactly = 1) { team2.name }
    assertEquals(listOf(team2, team), result)
  }

  @Test
  fun whenDeleteTeam_hasNoTeam_thenExpectError() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.deleteTeam(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenDeleteTeam_hasTeam_thenExpectNoError() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.delete(team) } just runs

    teamService.deleteTeam(id)
  }

  @Test
  fun whenVerifyTeamPassword_hasNoTeam_thenExpectException() {
    every { teamRepository.findByIdOrNull(id) } returns null

    val exception: IllegalArgumentException =
      assertThrows<IllegalArgumentException> { teamService.verifyTeamOpened(id) }

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    assertEquals("Team with id=${id} does not exist.", exception.message)
  }

  @Test
  fun whenVerifyTeamOpened_hasTeam_thenExpectAppropriateMockCalls() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.firstSelected } returns null
    every { team.firstSelected = capture(dateSlot) } just runs

    teamService.verifyTeamOpened(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.firstSelected }
    verify(exactly = 1) { team.firstSelected = any<OffsetDateTime>() }
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyTeamOpened_hasTeamDateAlreadySet_thenExpectAppropriateMockCalls() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { teamRepository.save(team) } returns team
    every { team.firstSelected } returns dt
    every { team.firstSelected = dt } just runs

    teamService.verifyTeamOpened(id)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { teamRepository.save(team) }
    verify(exactly = 1) { team.firstSelected }
    verify(exactly = 1) { team.firstSelected = dt }
  }

  @Test
  fun whenVerifyProductsIdentified_hasIncorrectHighQuantity_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns mutableListOf(teamInvoicePlus, teamInvoiceNoDiff)
    every { teamInvoicePlus.firstTask } returns false
    every { teamInvoiceNoDiff.firstTask } returns true
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { invoiceNoDiff.invoiceProducts } returns mutableListOf(invoiceProduct1, invoiceProduct2)
    every { invoiceProduct1.quantity } returns 10
    every { invoiceProduct2.quantity } returns 5
    every { invoiceProduct1.product } returns product1
    every { invoiceProduct2.product } returns product2
    every { product1.price } returns 5
    every { product2.price } returns 20
    every { product1.name } returns "product1"
    every { product2.name } returns "product2"

    val result = teamService.verifyProductsIdentified(id, "product2", "product2")
    assertFalse(result.verified)
    assertEquals("Incorrect Product with Highest Count.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.teamInvoices }
    verify(exactly = 1) { teamInvoicePlus.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.invoice }
    verify(exactly = 2) { invoiceNoDiff.invoiceProducts }
    verify(exactly = 2) { invoiceProduct1.quantity }
    verify(exactly = 2) { invoiceProduct2.quantity }
    verify(exactly = 2) { invoiceProduct1.product }
    verify(exactly = 2) { invoiceProduct2.product }
    verify(exactly = 1) { product1.price }
    verify(exactly = 1) { product2.price }
    verify(exactly = 1) { product1.name }
    verify(exactly = 1) { product2.name }
  }

  @Test
  fun whenVerifyProductsIdentified_hasIncorrectHighCost_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns mutableListOf(teamInvoicePlus, teamInvoiceNoDiff)
    every { teamInvoicePlus.firstTask } returns false
    every { teamInvoiceNoDiff.firstTask } returns true
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { invoiceNoDiff.invoiceProducts } returns mutableListOf(invoiceProduct1, invoiceProduct2)
    every { invoiceProduct1.quantity } returns 10
    every { invoiceProduct2.quantity } returns 5
    every { invoiceProduct1.product } returns product1
    every { invoiceProduct2.product } returns product2
    every { product1.price } returns 5
    every { product2.price } returns 20
    every { product1.name } returns "product1"
    every { product2.name } returns "product2"

    val result = teamService.verifyProductsIdentified(id, "product1", "product1")
    assertFalse(result.verified)
    assertEquals("Incorrect Product with Highest Cost.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.teamInvoices }
    verify(exactly = 1) { teamInvoicePlus.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.invoice }
    verify(exactly = 2) { invoiceNoDiff.invoiceProducts }
    verify(exactly = 2) { invoiceProduct1.quantity }
    verify(exactly = 2) { invoiceProduct2.quantity }
    verify(exactly = 2) { invoiceProduct1.product }
    verify(exactly = 2) { invoiceProduct2.product }
    verify(exactly = 1) { product1.price }
    verify(exactly = 1) { product2.price }
    verify(exactly = 1) { product1.name }
    verify(exactly = 1) { product2.name }
  }

  @Test
  fun whenVerifyProductsIdentified_hasIncorrectBoth_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns mutableListOf(teamInvoicePlus, teamInvoiceNoDiff)
    every { teamInvoicePlus.firstTask } returns false
    every { teamInvoiceNoDiff.firstTask } returns true
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { invoiceNoDiff.invoiceProducts } returns mutableListOf(invoiceProduct1, invoiceProduct2)
    every { invoiceProduct1.quantity } returns 10
    every { invoiceProduct2.quantity } returns 5
    every { invoiceProduct1.product } returns product1
    every { invoiceProduct2.product } returns product2
    every { product1.price } returns 5
    every { product2.price } returns 20
    every { product1.name } returns "product1"
    every { product2.name } returns "product2"

    val result = teamService.verifyProductsIdentified(id, "product1", "product2")
    assertFalse(result.verified)
    assertEquals("Incorrect Product with Highest Cost.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.teamInvoices }
    verify(exactly = 1) { teamInvoicePlus.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.invoice }
    verify(exactly = 2) { invoiceNoDiff.invoiceProducts }
    verify(exactly = 2) { invoiceProduct1.quantity }
    verify(exactly = 2) { invoiceProduct2.quantity }
    verify(exactly = 2) { invoiceProduct1.product }
    verify(exactly = 2) { invoiceProduct2.product }
    verify(exactly = 1) { product1.price }
    verify(exactly = 1) { product2.price }
    verify(exactly = 1) { product1.name }
    verify(exactly = 1) { product2.name }
  }

  @Test
  fun whenVerifyProductsIdentified_hasBothCorrect_noPreviousValue_thenReturnTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns mutableListOf(teamInvoicePlus, teamInvoiceNoDiff)
    every { teamInvoicePlus.firstTask } returns false
    every { teamInvoiceNoDiff.firstTask } returns true
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { invoiceNoDiff.invoiceProducts } returns mutableListOf(invoiceProduct1, invoiceProduct2)
    every { invoiceProduct1.quantity } returns 10
    every { invoiceProduct2.quantity } returns 5
    every { invoiceProduct1.product } returns product1
    every { invoiceProduct2.product } returns product2
    every { product1.price } returns 5
    every { product2.price } returns 20
    every { product1.name } returns "product1"
    every { product2.name } returns "product2"
    every { team.productsIdentified } returns null
    every { team.productsIdentified = capture(dateSlot) } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyProductsIdentified(id, "product2", "product1")
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.teamInvoices }
    verify(exactly = 1) { teamInvoicePlus.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.invoice }
    verify(exactly = 2) { invoiceNoDiff.invoiceProducts }
    verify(exactly = 2) { invoiceProduct1.quantity }
    verify(exactly = 2) { invoiceProduct2.quantity }
    verify(exactly = 2) { invoiceProduct1.product }
    verify(exactly = 2) { invoiceProduct2.product }
    verify(exactly = 1) { product1.price }
    verify(exactly = 1) { product2.price }
    verify(exactly = 1) { product1.name }
    verify(exactly = 1) { product2.name }
    verify(exactly = 1) { team.productsIdentified }
    verify(exactly = 1) { team.productsIdentified = any<OffsetDateTime>() }
    verify(exactly = 1) { teamRepository.save(team) }
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyProductsIdentified_hasBothCorrect_hasPreviousValue_thenReturnTrue() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns mutableListOf(teamInvoicePlus, teamInvoiceNoDiff)
    every { teamInvoicePlus.firstTask } returns false
    every { teamInvoiceNoDiff.firstTask } returns true
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { invoiceNoDiff.invoiceProducts } returns mutableListOf(invoiceProduct1, invoiceProduct2)
    every { invoiceProduct1.quantity } returns 10
    every { invoiceProduct2.quantity } returns 5
    every { invoiceProduct1.product } returns product1
    every { invoiceProduct2.product } returns product2
    every { product1.price } returns 5
    every { product2.price } returns 20
    every { product1.name } returns "product1"
    every { product2.name } returns "product2"
    every { team.productsIdentified } returns dt
    every { team.productsIdentified = dt } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyProductsIdentified(id, "product2", "product1")
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 1) { team.teamInvoices }
    verify(exactly = 1) { teamInvoicePlus.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.firstTask }
    verify(exactly = 1) { teamInvoiceNoDiff.invoice }
    verify(exactly = 2) { invoiceNoDiff.invoiceProducts }
    verify(exactly = 2) { invoiceProduct1.quantity }
    verify(exactly = 2) { invoiceProduct2.quantity }
    verify(exactly = 2) { invoiceProduct1.product }
    verify(exactly = 2) { invoiceProduct2.product }
    verify(exactly = 1) { product1.price }
    verify(exactly = 1) { product2.price }
    verify(exactly = 1) { product1.name }
    verify(exactly = 1) { product2.name }
    verify(exactly = 1) { team.productsIdentified }
    verify(exactly = 1) { team.productsIdentified = dt }
    verify(exactly = 1) { teamRepository.save(team) }
  }

  @Test
  fun whenVerifyLeakageIdentified_hasUnderpaidIncorrect_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1
    every { invoiceMinus.id } returns 1L
    every { invoicePlus.difference } returns 1
    every { invoicePlus.id } returns 2L

    val result = teamService.verifyLeakageIdentified(id, "2", "2")
    assertFalse(result.verified)
    assertEquals("Incorrect Underpaid Invoice ID.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifyLeakageIdentified_hasOverpaidIncorrect_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1
    every { invoiceMinus.id } returns 1L
    every { invoicePlus.difference } returns 1
    every { invoicePlus.id } returns 2L

    val result = teamService.verifyLeakageIdentified(id, "1", "1")
    assertFalse(result.verified)
    assertEquals("Incorrect Overpaid Invoice ID.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifyLeakageIdentified_hasBothIncorrect_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1
    every { invoiceMinus.id } returns 1L
    every { invoicePlus.difference } returns 1
    every { invoicePlus.id } returns 2L

    val result = teamService.verifyLeakageIdentified(id, "2", "1")
    assertFalse(result.verified)
    assertEquals("Incorrect Underpaid Invoice ID.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifyLeakageIdentified_hasBothCorrect_noPreviousValue_thenReturnTrue() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1
    every { invoiceMinus.id } returns 1L
    every { invoicePlus.difference } returns 1
    every { invoicePlus.id } returns 2L
    every { team.leakageIdentified } returns null
    every { team.leakageIdentified = capture(dateSlot) } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyLeakageIdentified(id, "1", "2")
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoicePlus.id }
    verify(exactly = 1) { team.leakageIdentified }
    verify(exactly = 1) { team.leakageIdentified = any<OffsetDateTime>() }
    verify(exactly = 1) { teamRepository.save(team) }
    assertEquals(true, dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifyLeakageIdentified_hasBothCorrect_hasPreviousValue_thenReturnTrue() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1
    every { invoiceMinus.id } returns 1L
    every { invoicePlus.difference } returns 1
    every { invoicePlus.id } returns 2L
    every { team.leakageIdentified } returns dt
    every { team.leakageIdentified = dt } just runs
    every { teamRepository.save(team) } returns team

    val result = teamService.verifyLeakageIdentified(id, "1", "2")
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoicePlus.id }
    verify(exactly = 1) { team.leakageIdentified }
    verify(exactly = 1) { team.leakageIdentified = dt }
    verify(exactly = 1) { teamRepository.save(team) }
  }

  @Test
  fun whenVerifySupplierEmails_missingUnderpaidCompany_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"

    val result = teamService.verifySupplierEmails(id, "company 2", "2")
    assertFalse(result.verified)
    assertEquals("We're not sure if you've contacted the right company.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
  }

  @Test
  fun whenVerifySupplierEmails_missingUnderpaidId_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L

    val result = teamService.verifySupplierEmails(id, "company 1 11", "2")
    assertFalse(result.verified)
    assertEquals(
      "company 1 isn't sure which invoice you are talking about, they have multiple with you.",
      result.incorrectItem,
    )

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 2) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
  }

  @Test
  fun whenVerifySupplierEmails_missingUnderpaidWord_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L

    val result = teamService.verifySupplierEmails(id, "company 1 10 overpaid", "2")
    assertFalse(result.verified)
    assertEquals("company 1 isn't sure what was incorrect about the invoice.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 2) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 2) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
  }

  @Test
  fun whenVerifySupplierEmails_missingUnderpaidDifferenceNoComma_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L

    val result = teamService.verifySupplierEmails(id, "company 1 10 underpaid 160", "2")
    assertFalse(result.verified)
    assertEquals("company 1 is unsure how much you still owe.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 2) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
  }

  @Test
  fun whenVerifySupplierEmails_underPaidDifferenceNoComma_missingOverpaidCompany_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"

    val result = teamService.verifySupplierEmails(id, "company 1 10 underpaid 1500", "2")
    assertFalse(result.verified)
    assertEquals("We're not sure if you've contacted the right company.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 3) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.company }
  }

  @Test
  fun whenVerifySupplierEmails_underPaidDifferenceComma_missingOverpaidCompany_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"

    val result = teamService.verifySupplierEmails(id, "company 1 10 underpaid 1,500", "2")
    assertFalse(result.verified)
    assertEquals("We're not sure if you've contacted the right company.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.company }
  }

  @Test
  fun whenVerifySupplierEmails_missingOverpaidId_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"
    every { invoicePlus.id } returns 33L

    val result =
      teamService.verifySupplierEmails(id, "company 1 10 underpaid 1,500", "company 2 32")
    assertFalse(result.verified)
    assertEquals(
      "company 2 isn't sure which invoice you're talking about, they have multiple with you.",
      result.incorrectItem,
    )

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 2) { invoicePlus.company }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifySupplierEmails_missingOverpaidWord_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"
    every { invoicePlus.id } returns 33L

    val result =
      teamService.verifySupplierEmails(id, "company 1 10 underpaid 1,500", "company 2 33 underpaid")
    assertFalse(result.verified)
    assertEquals("company 2 isn't sure what was incorrect about the invoice.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 1) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 2) { invoicePlus.company }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifySupplierEmails_missingOverpaidDifference_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"
    every { invoicePlus.id } returns 33L

    val result =
      teamService.verifySupplierEmails(
        id,
        "company 1 10 underpaid 1,500",
        "company 2 33 overpaid 1234",
      )
    assertFalse(result.verified)
    assertEquals("company 2 is unsure how much they owe you.", result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 3) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 2) { invoicePlus.company }
    verify(exactly = 1) { invoicePlus.id }
  }

  @Test
  fun whenVerifySupplierEmails_overpaidDifferenceNoComma_noPreviousContact_thenReturnFalse() {
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"
    every { invoicePlus.id } returns 33L
    every { team.suppliersContacted } returns null
    every { team.suppliersContacted = capture(dateSlot) } just runs
    every { team.underpaidEmail = "company 1 10 underpaid 1,500" } just runs
    every { team.overpaidEmail = "company 2 33 overpaid 1500" } just runs
    every { teamRepository.save(team) } returns team

    val result =
      teamService.verifySupplierEmails(
        id,
        "company 1 10 underpaid 1,500",
        "company 2 33 overpaid 1500",
      )
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 2) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.company }
    verify(exactly = 1) { invoicePlus.id }
    verify(exactly = 1) { team.suppliersContacted }
    verify(exactly = 1) { team.suppliersContacted = any<OffsetDateTime>() }
    verify(exactly = 1) { team.underpaidEmail = "company 1 10 underpaid 1,500" }
    verify(exactly = 1) { team.overpaidEmail = "company 2 33 overpaid 1500" }
    verify(exactly = 1) { teamRepository.save(team) }
    assertTrue(dateSlot.captured.isBefore(OffsetDateTime.now()))
  }

  @Test
  fun whenVerifySupplierEmails_overpaidDifferenceWithComma_hasPreviousContact_thenReturnFalse() {
    val dt = OffsetDateTime.now()
    every { teamRepository.findByIdOrNull(id) } returns team
    every { team.teamInvoices } returns
      mutableListOf(teamInvoiceNoDiff, teamInvoiceMinus, teamInvoicePlus)
    every { teamInvoiceNoDiff.invoice } returns invoiceNoDiff
    every { teamInvoiceMinus.invoice } returns invoiceMinus
    every { teamInvoicePlus.invoice } returns invoicePlus
    every { invoiceNoDiff.difference } returns 0
    every { invoiceMinus.difference } returns -1500
    every { invoicePlus.difference } returns 1500
    every { invoiceMinus.company } returns "company 1"
    every { invoiceMinus.id } returns 10L
    every { invoicePlus.company } returns "company 2"
    every { invoicePlus.id } returns 33L
    every { team.suppliersContacted } returns dt
    every { team.suppliersContacted = dt } just runs
    every { team.underpaidEmail = "company 1 10 underpaid 1,500" } just runs
    every { team.overpaidEmail = "company 2 33 overpaid 1,500" } just runs
    every { teamRepository.save(team) } returns team

    val result =
      teamService.verifySupplierEmails(
        id,
        "company 1 10 underpaid 1,500",
        "company 2 33 overpaid 1,500",
      )
    assertTrue(result.verified)
    assertNull(result.incorrectItem)

    verify(exactly = 1) { teamRepository.findByIdOrNull(id) }
    verify(exactly = 2) { team.teamInvoices }
    verify(exactly = 2) { teamInvoiceNoDiff.invoice }
    verify(exactly = 3) { teamInvoiceMinus.invoice }
    verify(exactly = 2) { teamInvoicePlus.invoice }
    verify(exactly = 2) { invoiceNoDiff.difference }
    verify(exactly = 4) { invoiceMinus.difference }
    verify(exactly = 3) { invoicePlus.difference }
    verify(exactly = 1) { invoiceMinus.company }
    verify(exactly = 1) { invoiceMinus.id }
    verify(exactly = 1) { invoicePlus.company }
    verify(exactly = 1) { invoicePlus.id }
    verify(exactly = 1) { team.suppliersContacted }
    verify(exactly = 1) { team.suppliersContacted = dt }
    verify(exactly = 1) { team.underpaidEmail = "company 1 10 underpaid 1,500" }
    verify(exactly = 1) { team.overpaidEmail = "company 2 33 overpaid 1,500" }
    verify(exactly = 1) { teamRepository.save(team) }
  }
}
