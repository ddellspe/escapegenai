package net.ddellspe.escapegenai.service

import java.util.*
import net.ddellspe.escapegenai.model.TeamWord
import net.ddellspe.escapegenai.repository.TeamWordRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TeamWordService(var teamWordRepository: TeamWordRepository) {
  fun getTeamWord(id: UUID): TeamWord {
    return teamWordRepository.findByIdOrNull(id)
      ?: throw IllegalArgumentException("TeamWord with id=${id} does not exist.")
  }
}
