package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.TeamWord
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface TeamWordRepository : ListCrudRepository<TeamWord, UUID> {}
