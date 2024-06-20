package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.Team
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface TeamRepository : ListCrudRepository<Team, UUID> {}
