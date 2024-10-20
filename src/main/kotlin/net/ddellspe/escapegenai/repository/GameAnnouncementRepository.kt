package net.ddellspe.escapegenai.repository

import java.util.*
import net.ddellspe.escapegenai.model.GameAnnouncement
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository interface GameAnnouncementRepository : ListCrudRepository<GameAnnouncement, UUID> {}
