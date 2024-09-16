package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*
import net.ddellspe.escapegenai.util.generateFunFactType
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity(name = "team")
@Table(name = "team")
data class Team(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var name: String = "",
  @Column(name = "first_selected") var firstSelected: OffsetDateTime? = null
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(
      this.id,
      this.name,
      this.firstSelected,
    )
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(
      this.id,
      this.name,
      this.firstSelected,
    )
  }
}
