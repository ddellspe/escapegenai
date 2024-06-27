package net.ddellspe.escapegenai.service

import java.time.OffsetDateTime
import java.util.*
import net.ddellspe.escapegenai.model.Team
import net.ddellspe.escapegenai.model.TeamContainer
import net.ddellspe.escapegenai.repository.TeamRepository
import org.apache.commons.text.similarity.LevenshteinDistance
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TeamService(var teamRepository: TeamRepository) {
  fun createTeam(teamContainer: TeamContainer): Team {
    if (teamContainer.id != null) {
      if (teamRepository.findByIdOrNull(teamContainer.id) != null) {
        throw IllegalArgumentException(
          "Team with id=${teamContainer.id} already exists, use update instead."
        )
      }
    }
    return teamRepository.save(Team(name = teamContainer.name))
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

  fun verifyTeamPassword(id: UUID, password: String): Boolean {
    val team = getTeam(id)
    if (password == team.password.password) {
      team.passwordEntered = team.passwordEntered ?: OffsetDateTime.now()
      teamRepository.save(team)
      return true
    } else {
      return false
    }
  }

  fun verifyTeamWord(id: UUID, word: String): Boolean {
    val team = getTeam(id)
    if (word == team.word.word) {
      team.wordEntered = team.wordEntered ?: OffsetDateTime.now()
      teamRepository.save(team)
      return true
    } else {
      return false
    }
  }

  fun verifyTeamQuote(id: UUID, quote: String): Boolean {
    val team = getTeam(id)
    val actualQuote = team.quote?.quote ?: ""
    val levDistance = LevenshteinDistance()
    if (
      levDistance.apply(
        quote.lowercase().replace(".,!?".toRegex(), ""),
        actualQuote.lowercase().replace(".,!?".toRegex(), ""),
      ) <= (actualQuote.length.div(10))
    ) {
      team.quoteEntered = team.quoteEntered ?: OffsetDateTime.now()
      teamRepository.save(team)
      return true
    } else {
      return false
    }
  }

  fun verifyFunFact(id: UUID, funFact: String): Boolean {
    val team = getTeam(id)
    val quote = team.quote ?: return false
    val lookup: String? =
      when (team.funFactType) {
        "author" -> {
          quote.author
        }
        "authorAddress" -> {
          quote.authorAddress
        }
        "authorTitle" -> {
          quote.authorTitle
        }
        "company" -> {
          quote.company
        }
        "companyAddress" -> {
          quote.companyAddress
        }
        "companyIndustry" -> {
          quote.companyIndustry
        }
        else -> {
          null
        }
      }
    if (lookup != null && lookup == funFact) {
      team.funFactEntered = team.funFactEntered ?: OffsetDateTime.now()
      teamRepository.save(team)
      return true
    } else {
      return false
    }
  }
}
