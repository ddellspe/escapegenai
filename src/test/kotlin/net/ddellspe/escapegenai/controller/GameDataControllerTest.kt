package net.ddellspe.escapegenai.controller

import io.mockk.*
import java.util.*
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.TeamInvoiceService
import net.ddellspe.escapegenai.service.TeamService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameDataControllerTest {
  private val teamService: TeamService = mockk()
  private val teamInvoiceService: TeamInvoiceService = mockk()
  private val team: Team = mockk()
  private val invoice: Invoice = mockk()
  private val invoiceProduct: InvoiceProduct = mockk()
  private val product: Product = mockk()
  private val teamInvoice: TeamInvoice = mockk()
  private val minimalTeam: MinimalTeam = mockk()
  private val gameSubmission: GameSubmission = mockk()
  private val id = UUID.randomUUID()
  private val gameDataController = GameDataController(teamService, teamInvoiceService)

  @Test
  fun dumbCoverageTests() {
    gameDataController.teamService = teamService
    gameDataController.teamInvoiceService = teamInvoiceService
  }

  @Test
  fun whenGetTeams_hasNoTeams_thenNoTeamsPresent() {
    every { teamService.getAllTeams() } returns emptyList()

    val result = gameDataController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(0, result.body?.size)
  }

  @Test
  fun whenGetTeams_hasTeams_thenTeamsPresent() {
    every { teamService.getAllTeams() } returns listOf(team)
    every { team.toMinimalTeam() } returns minimalTeam

    val result = gameDataController.getTeams()

    verify(exactly = 1) { teamService.getAllTeams() }
    verify(exactly = 1) { team.toMinimalTeam() }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(1, result.body?.size)
    assertEquals(minimalTeam, result.body?.get(0))
  }

  @Test
  fun whenSubmitGameState_hasNoTeam_thenExpectBadRequest() {
    every { teamService.getTeam(id) } throws IllegalArgumentException("No Team")
    every { gameSubmission.id } returns id

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 2) { gameSubmission.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_highCostNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { gameSubmission.highCost }
    verify(exactly = 2) { team.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_highQuantityNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 1) { gameSubmission.highCost }
    verify(exactly = 1) { gameSubmission.highQuantity }
    verify(exactly = 2) { team.id }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_verifyProductFails_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns false

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id)

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 2) { gameSubmission.highCost }
    verify(exactly = 2) { gameSubmission.highQuantity }
    verify(exactly = 3) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_verifyProductPasses_underpaidInvoiceNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 3) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 1) { gameSubmission.underpaidInvoiceId }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_overpaidInvoiceNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 3) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 1) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 1) { gameSubmission.overpaidInvoiceId }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_LeakageNotVerified_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns "2"
    every { teamService.verifyLeakageIdentified(id, "1", "2") } returns false

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 4) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 2) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 2) { gameSubmission.overpaidInvoiceId }
    verify(exactly = 1) { teamService.verifyLeakageIdentified(id, "1", "2") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_LeakageVerified_underpaidEmailNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns "2"
    every { teamService.verifyLeakageIdentified(id, "1", "2") } returns true
    every { gameSubmission.underpaidEmail } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost", "2", "1")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 4) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 3) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 3) { gameSubmission.overpaidInvoiceId }
    verify(exactly = 1) { teamService.verifyLeakageIdentified(id, "1", "2") }
    verify(exactly = 1) { gameSubmission.underpaidEmail }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_LeakageVerified_overpaidEmailNull_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns "2"
    every { teamService.verifyLeakageIdentified(id, "1", "2") } returns true
    every { gameSubmission.underpaidEmail } returns "underpaidEmail"
    every { gameSubmission.overpaidEmail } returns null

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost", "2", "1")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 4) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 3) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 3) { gameSubmission.overpaidInvoiceId }
    verify(exactly = 1) { teamService.verifyLeakageIdentified(id, "1", "2") }
    verify(exactly = 1) { gameSubmission.underpaidEmail }
    verify(exactly = 1) { gameSubmission.overpaidEmail }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_LeakageVerified_emailNotVerified_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns "2"
    every { teamService.verifyLeakageIdentified(id, "1", "2") } returns true
    every { gameSubmission.underpaidEmail } returns "underpaidEmail"
    every { gameSubmission.overpaidEmail } returns "overpaidEmail"
    every { teamService.verifySupplierEmails(id, "underpaidEmail", "overpaidEmail") } returns false

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody = GameSubmission(id, "highQuantity", "highCost", "2", "1")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 5) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 3) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 3) { gameSubmission.overpaidInvoiceId }
    verify(exactly = 1) { teamService.verifyLeakageIdentified(id, "1", "2") }
    verify(exactly = 2) { gameSubmission.underpaidEmail }
    verify(exactly = 2) { gameSubmission.overpaidEmail }
    verify(exactly = 1) { teamService.verifySupplierEmails(id, "underpaidEmail", "overpaidEmail") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenSubmitGameState_hasTeam_ProductVerified_LeakageVerified_emailVerified_thenExpectSuccessOnlyId() {
    every { teamService.getTeam(id) } returns team
    every { teamService.verifyTeamOpened(id) } just runs
    every { gameSubmission.id } returns id
    every { team.id } returns id
    every { gameSubmission.highCost } returns "highCost"
    every { gameSubmission.highQuantity } returns "highQuantity"
    every { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") } returns true
    every { gameSubmission.underpaidInvoiceId } returns "1"
    every { gameSubmission.overpaidInvoiceId } returns "2"
    every { teamService.verifyLeakageIdentified(id, "1", "2") } returns true
    every { gameSubmission.underpaidEmail } returns "underpaidEmail"
    every { gameSubmission.overpaidEmail } returns "overpaidEmail"
    every { teamService.verifySupplierEmails(id, "underpaidEmail", "overpaidEmail") } returns true

    val result = gameDataController.submitGameState(gameSubmission)
    val expectedBody =
      GameSubmission(id, "highQuantity", "highCost", "2", "1", "overpaidEmail", "underpaidEmail")

    verify(exactly = 1) { teamService.getTeam(id) }
    verify(exactly = 1) { teamService.verifyTeamOpened(id) }
    verify(exactly = 1) { gameSubmission.id }
    verify(exactly = 3) { gameSubmission.highCost }
    verify(exactly = 3) { gameSubmission.highQuantity }
    verify(exactly = 5) { team.id }
    verify(exactly = 1) { teamService.verifyProductsIdentified(id, "highCost", "highQuantity") }
    verify(exactly = 3) { gameSubmission.underpaidInvoiceId }
    verify(exactly = 3) { gameSubmission.overpaidInvoiceId }
    verify(exactly = 1) { teamService.verifyLeakageIdentified(id, "1", "2") }
    verify(exactly = 3) { gameSubmission.underpaidEmail }
    verify(exactly = 3) { gameSubmission.overpaidEmail }
    verify(exactly = 1) { teamService.verifySupplierEmails(id, "underpaidEmail", "overpaidEmail") }
    assertNotNull(result.body)
    assertEquals(HttpStatus.OK, result.statusCode)
    assertEquals(expectedBody, result.body)
  }

  @Test
  fun whenGetInvoice_hasNoInvoice_thenExpectBadRequest() {
    every { teamInvoiceService.getInvoice(id) } throws IllegalArgumentException("exception")

    val result = gameDataController.getInvoice(id)

    verify(exactly = 1) { teamInvoiceService.getInvoice(id) }
    assertNotNull(result.body)
    assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    assertEquals("exception", result.body)
  }
}
