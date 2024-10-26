package net.ddellspe.escapegenai.service

import java.time.OffsetDateTime
import java.util.*
import kotlin.math.abs
import net.ddellspe.escapegenai.model.*
import net.ddellspe.escapegenai.repository.TeamRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TeamService(var teamRepository: TeamRepository, var invoiceService: InvoiceService) {
  fun createTeam(teamContainer: TeamContainer): Team {
    if (teamContainer.id != null) {
      if (teamRepository.findByIdOrNull(teamContainer.id) != null) {
        throw IllegalArgumentException(
          "Team with id=${teamContainer.id} already exists, use update instead."
        )
      }
    }
    val invoices: MutableList<Invoice> = ArrayList()
    val noDiff = (1..3).random()
    val minus = (1..3).filter { v -> v != noDiff }.random()
    val plus = (1..3).filter { v -> v != noDiff && v != minus }.random()
    (1..3).forEach { i ->
      var diff = 0
      if (i == minus) {
        diff = (-3000..-1000).random()
      } else if (i == plus) {
        diff = (1000..3000).random()
      }
      invoices.add(invoiceService.createNewInvoice(difference = diff))
    }
    val team = teamRepository.save(Team(name = teamContainer.name))
    var first = false
    invoices.forEach { invoice ->
      team.teamInvoices.add(TeamInvoice(team = team, invoice = invoice, firstTask = !first))
      first = true
    }
    return teamRepository.save(team)
  }

  fun updateTeam(team: Team): Team {
    teamRepository.findByIdOrNull(team.id)
      ?: throw IllegalArgumentException(
        "Team with id=${team.id} does not exist, please create it first."
      )
    return teamRepository.save(team)
  }

  fun getTeam(id: UUID): Team {
    return teamRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("Team with id=${id} does not exist.")
  }

  fun getAllTeams(): List<Team> {
    return teamRepository.findAll().sortedBy { team: Team? -> team?.name }
  }

  fun deleteTeam(id: UUID) {
    val team = getTeam(id)
    teamRepository.delete(team)
  }

  fun verifyTeamOpened(id: UUID) {
    val team = getTeam(id)
    team.firstSelected = team.firstSelected ?: OffsetDateTime.now()
    teamRepository.save(team)
  }

  fun verifyProductsIdentified(id: UUID, highCost: String, highQuantity: String): VerifyResponse {
    val team = getTeam(id)
    val invoice =
      team.teamInvoices.stream().filter { inv -> inv.firstTask }.findFirst().get().invoice
    val highQuantityName =
      invoice.invoiceProducts
        .stream()
        .sorted { o1, o2 -> o2.quantity.compareTo(o1.quantity) }
        .findFirst()
        .get()
        .product
        .name
    val highCostName =
      invoice.invoiceProducts
        .stream()
        .sorted { o1, o2 ->
          (o2.quantity * o2.product.price).compareTo((o1.quantity * o1.product.price))
        }
        .findFirst()
        .get()
        .product
        .name
    if (highCost.trim() == highCostName) {
      if (highQuantity.trim() == highQuantityName) {
        team.productsIdentified = team.productsIdentified ?: OffsetDateTime.now()
        teamRepository.save(team)
        return VerifyResponse(true)
      } else {
        return VerifyResponse(false, "Incorrect Product with Highest Count.")
      }
    } else {
      return VerifyResponse(false, "Incorrect Product with Highest Cost.")
    }
  }

  fun verifyLeakageIdentified(
    id: UUID,
    underpaidInvoiceId: String,
    overpaidInvoiceId: String,
  ): VerifyResponse {
    val team = getTeam(id)
    val overpaidInvoiceIdVal =
      team.teamInvoices
        .stream()
        .filter { inv -> inv.invoice.difference > 0 }
        .findFirst()
        .get()
        .invoice
        .id!!
    val underpaidInvoiceIdVal =
      team.teamInvoices
        .stream()
        .filter { inv -> inv.invoice.difference < 0 }
        .findFirst()
        .get()
        .invoice
        .id!!
    if (underpaidInvoiceId.trim() == underpaidInvoiceIdVal.toString()) {
      if (overpaidInvoiceId.trim() == overpaidInvoiceIdVal.toString()) {
        team.leakageIdentified = team.leakageIdentified ?: OffsetDateTime.now()
        teamRepository.save(team)
        return VerifyResponse(true)
      } else {
        return VerifyResponse(false, "Incorrect Overpaid Invoice ID.")
      }
    } else {
      return VerifyResponse(false, "Incorrect Underpaid Invoice ID.")
    }
  }

  fun verifySupplierEmails(
    id: UUID,
    underpaidEmail: String,
    overpaidEmail: String,
  ): VerifyResponse {
    val team = getTeam(id)
    val overpaidInvoice =
      team.teamInvoices
        .stream()
        .filter { inv -> inv.invoice.difference > 0 }
        .findFirst()
        .get()
        .invoice
    val underpaidInvoice =
      team.teamInvoices
        .stream()
        .filter { inv -> inv.invoice.difference < 0 }
        .findFirst()
        .get()
        .invoice
    if (underpaidEmail.lowercase().contains(underpaidInvoice.company.lowercase())) {
      if (underpaidEmail.contains(underpaidInvoice.id.toString())) {
        if (underpaidEmail.lowercase().contains("under")) {
          if (
            (underpaidEmail.contains(abs(underpaidInvoice.difference).toString()) ||
              underpaidEmail.contains("%,d".format(abs(underpaidInvoice.difference))))
          ) {
            if (overpaidEmail.lowercase().contains(overpaidInvoice.company.lowercase())) {
              if (overpaidEmail.contains(overpaidInvoice.id.toString())) {
                if (overpaidEmail.lowercase().contains("over")) {
                  if (
                    (overpaidEmail.contains(abs(overpaidInvoice.difference).toString()) ||
                      overpaidEmail.contains("%,d".format(abs(overpaidInvoice.difference))))
                  ) {
                    team.suppliersContacted = team.suppliersContacted ?: OffsetDateTime.now()
                    team.underpaidEmail = underpaidEmail
                    team.overpaidEmail = overpaidEmail
                    teamRepository.save(team)
                    return VerifyResponse(true)
                  } else {
                    return VerifyResponse(
                      false,
                      "${overpaidInvoice.company} is unsure how much they owe you.",
                    )
                  }
                } else {
                  return VerifyResponse(
                    false,
                    "${overpaidInvoice.company} isn't sure what was incorrect about the invoice.",
                  )
                }
              } else {
                return VerifyResponse(
                  false,
                  "${overpaidInvoice.company} isn't sure which invoice you're talking about, they have multiple with you.",
                )
              }
            } else {
              return VerifyResponse(false, "We're not sure if you've contacted the right company.")
            }
          } else {
            return VerifyResponse(
              false,
              "${underpaidInvoice.company} is unsure how much you still owe.",
            )
          }
        } else {
          return VerifyResponse(
            false,
            "${underpaidInvoice.company} isn't sure what was incorrect about the invoice.",
          )
        }
      } else {
        return VerifyResponse(
          false,
          "${underpaidInvoice.company} isn't sure which invoice you are talking about, they have multiple with you.",
        )
      }
    } else {
      return VerifyResponse(false, "We're not sure if you've contacted the right company.")
    }
  }
}
