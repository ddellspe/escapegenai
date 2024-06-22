package net.ddellspe.escapegenai.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
data class Team(
  @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID = UUID.randomUUID(),
  var name: String = "",
  @OneToOne(
    fetch = FetchType.LAZY,
    optional = false,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  @JoinColumn(name = "password_id")
  var password: Password = Password(),
  var passwordEntered: OffsetDateTime? = null,
  @OneToOne(
    fetch = FetchType.LAZY,
    optional = false,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  @JoinColumn(name = "word_id")
  var word: TeamWord = TeamWord(),
  var wordEntered: OffsetDateTime? = null,
  @OneToOne(
    fetch = FetchType.LAZY,
    optional = true,
    cascade = [CascadeType.ALL],
    orphanRemoval = false,
  )
  @JoinColumn(name = "quote_id")
  var quote: Quote? = null,
  var quoteEntered: OffsetDateTime? = null,
  var funFactType: String? = null,
  var funFactEntered: OffsetDateTime? = null,
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(
      this.id,
      this.name,
      this.passwordEntered,
      this.wordEntered,
      this.quoteEntered,
      this.funFactEntered,
    )
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(
      this.id,
      this.name,
      this.password.id,
      this.passwordEntered,
      this.word.id,
      this.wordEntered,
      this.quote?.id,
      this.quoteEntered,
      this.funFactType,
      this.funFactEntered,
    )
  }
}
