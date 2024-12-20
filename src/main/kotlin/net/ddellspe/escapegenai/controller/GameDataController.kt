package net.ddellspe.escapegenai.controller

import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.io.source.ByteArrayOutputStream
import java.util.*
import net.ddellspe.escapegenai.config.EscapeGenAIProperties
import net.ddellspe.escapegenai.config.ThymeleafConfiguration
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.service.GameAnnouncementService
import net.ddellspe.escapegenai.service.TeamInvoiceService
import net.ddellspe.escapegenai.service.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.thymeleaf.context.Context

@RestController
@RequestMapping("/game")
@EnableConfigurationProperties(EscapeGenAIProperties::class)
class GameDataController(
  var teamService: TeamService,
  var teamInvoiceService: TeamInvoiceService,
  var gameAnnouncementService: GameAnnouncementService,
) {

  @Autowired private var props: EscapeGenAIProperties = EscapeGenAIProperties()
  @Autowired private var thymeleafConfiguration: ThymeleafConfiguration = ThymeleafConfiguration()

  @GetMapping("/announcements")
  fun getAnnouncements(): ResponseEntity<List<GameAnnouncement>> {
    return ResponseEntity.ok(gameAnnouncementService.getAllGameAnnouncements())
  }

  @GetMapping("/teams")
  fun getTeams(): ResponseEntity<List<MinimalTeam>> {
    val teams: List<MinimalTeam> =
      teamService.getAllTeams().stream().map { t -> t.toMinimalTeam() }.toList()
    return ResponseEntity.ok(teams)
  }

  @PostMapping("/submit")
  fun submitGameState(
    @RequestBody gameSubmission: GameSubmission
  ): ResponseEntity<GameSubmissionResponse> {
    try {
      val team = teamService.getTeam(gameSubmission.id)
      teamService.verifyTeamOpened(team.id)
      val returnSubmission = GameSubmission(team.id)
      if (gameSubmission.highCost.isNullOrEmpty() || gameSubmission.highQuantity.isNullOrEmpty()) {
        return ResponseEntity(
          GameSubmissionResponse(returnSubmission, SubmissionFeedback(correct = "Team Selected")),
          HttpStatus.OK,
        )
      }
      val productVerification =
        teamService.verifyProductsIdentified(
          team.id,
          gameSubmission.highCost!!,
          gameSubmission.highQuantity!!,
        )
      if (!productVerification.verified) {
        return ResponseEntity(
          GameSubmissionResponse(
            returnSubmission,
            SubmissionFeedback(incorrect = productVerification.incorrectItem),
          ),
          HttpStatus.OK,
        )
      }
      returnSubmission.highCost = gameSubmission.highCost
      returnSubmission.highQuantity = gameSubmission.highQuantity
      if (
        gameSubmission.underpaidInvoiceId.isNullOrEmpty() ||
          gameSubmission.overpaidInvoiceId.isNullOrEmpty()
      ) {
        return ResponseEntity(
          GameSubmissionResponse(
            returnSubmission,
            SubmissionFeedback(correct = "Products Verified"),
          ),
          HttpStatus.OK,
        )
      }
      val leakageVerification =
        teamService.verifyLeakageIdentified(
          team.id,
          gameSubmission.underpaidInvoiceId!!,
          gameSubmission.overpaidInvoiceId!!,
        )
      if (!leakageVerification.verified) {
        return ResponseEntity(
          GameSubmissionResponse(
            returnSubmission,
            SubmissionFeedback(incorrect = leakageVerification.incorrectItem),
          ),
          HttpStatus.OK,
        )
      }
      returnSubmission.underpaidInvoiceId = gameSubmission.underpaidInvoiceId
      returnSubmission.overpaidInvoiceId = gameSubmission.overpaidInvoiceId
      if (
        gameSubmission.underpaidEmail.isNullOrEmpty() ||
          gameSubmission.overpaidEmail.isNullOrEmpty()
      ) {
        return ResponseEntity(
          GameSubmissionResponse(
            returnSubmission,
            SubmissionFeedback(correct = "Leakage Verified"),
          ),
          HttpStatus.OK,
        )
      }
      val emailVerified =
        teamService.verifySupplierEmails(
          team.id,
          gameSubmission.underpaidEmail!!,
          gameSubmission.overpaidEmail!!,
        )
      if (!emailVerified.verified) {
        return ResponseEntity(
          GameSubmissionResponse(
            returnSubmission,
            SubmissionFeedback(incorrect = emailVerified.incorrectItem),
          ),
          HttpStatus.OK,
        )
      }
      returnSubmission.underpaidEmail = gameSubmission.underpaidEmail
      returnSubmission.overpaidEmail = gameSubmission.overpaidEmail
      return ResponseEntity(
        GameSubmissionResponse(
          returnSubmission,
          SubmissionFeedback(correct = "All Tasks Completed!"),
        ),
        HttpStatus.OK,
      )
    } catch (e: IllegalArgumentException) {
      return ResponseEntity(
        GameSubmissionResponse(GameSubmission(gameSubmission.id), SubmissionFeedback()),
        HttpStatus.BAD_REQUEST,
      )
    }
  }

  @GetMapping("/invoice/{id}")
  fun getInvoice(@PathVariable id: UUID): ResponseEntity<out Any> {
    try {
      val teamInvoice = teamInvoiceService.getInvoice(id)
      val team = teamInvoice.team
      val invoice = teamInvoice.invoice

      val ctx = Context()
      ctx.setVariable("invoice", getInvoicePayload(team, invoice))
      val htmlContent = thymeleafConfiguration.springTemplateEngine().process("invoice_1", ctx)
      val targetOutputStream = ByteArrayOutputStream()
      HtmlConverter.convertToPdf(htmlContent, targetOutputStream)
      val headers = HttpHeaders()
      headers.add(
        CONTENT_DISPOSITION,
        "inline; filename=${team.name} ${if (teamInvoice.firstTask) "First " else ""}Invoice ${invoice.id}.pdf",
      )
      return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(targetOutputStream.toByteArray())
    } catch (e: IllegalArgumentException) {
      return ResponseEntity<String>(e.message!!, HttpStatus.NOT_FOUND)
    }
  }

  private fun getInvoicePayload(team: Team, invoice: Invoice): InvoiceDocument {
    val total =
      invoice.invoiceProducts
        .stream()
        .mapToLong { prod -> (prod.quantity * prod.product.price).toLong() }
        .sum() + invoice.difference
    return InvoiceDocument(
      id = invoice.id!!,
      teamName = team.name,
      company = invoice.company,
      address = invoice.address.replaceFirst(",", "<br />"),
      total = total,
      products =
        invoice.invoiceProducts
          .stream()
          .map { invoiceProduct ->
            InvoiceProductDocument(
              id = invoiceProduct.id,
              name = invoiceProduct.product.name,
              qty = invoiceProduct.quantity,
              price = invoiceProduct.product.price,
              total = invoiceProduct.quantity * invoiceProduct.product.price,
            )
          }
          .toList(),
    )
  }
}
