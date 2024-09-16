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

  fun verifyTeamOpened(id: UUID) {
    val team = getTeam(id)
    team.firstSelected = team.firstSelected ?: OffsetDateTime.now()
    teamRepository.save(team)
  }
}
