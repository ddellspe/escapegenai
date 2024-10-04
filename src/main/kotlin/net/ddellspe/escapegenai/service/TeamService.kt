package net.ddellspe.escapegenai.service

import java.time.OffsetDateTime
import java.util.*
import net.ddellspe.escapegenai.model.Invoice
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.model.TeamContainer
import net.ddellspe.escapegenai.model.TeamInvoice
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
    return teamRepository.findAll()
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
}
