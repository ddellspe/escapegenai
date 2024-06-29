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
  @Column(name = "first_selected") var firstSelected: OffsetDateTime? = null,
  @OneToOne(
    fetch = FetchType.EAGER,
    optional = false,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  @JoinColumn(name = "password_id")
  @Fetch(FetchMode.JOIN)
  var password: Password = Password(),
  @Column(name = "password_entered") var passwordEntered: OffsetDateTime? = null,
  @OneToOne(
    fetch = FetchType.EAGER,
    optional = false,
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
  )
  @JoinColumn(name = "word_id")
  @Fetch(FetchMode.JOIN)
  var word: TeamWord = TeamWord(),
  @Column(name = "word_entered") var wordEntered: OffsetDateTime? = null,
  @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = [CascadeType.DETACH])
  @JoinColumn(name = "quote_id")
  @Fetch(FetchMode.JOIN)
  var quote: Quote? = null,
  @Column(name = "quote_entered") var quoteEntered: OffsetDateTime? = null,
  @Column(name = "fun_fact_type") var funFactType: String = generateFunFactType(),
  @Column(name = "fun_fact_entered") var funFactEntered: OffsetDateTime? = null,
) {
  fun toMinimalTeam(): MinimalTeam {
    return MinimalTeam(
      this.id,
      this.name,
      this.firstSelected,
      this.passwordEntered,
      this.wordEntered,
      this.quoteEntered,
      this.funFactType,
      this.funFactEntered,
    )
  }

  fun toTeamContainer(): TeamContainer {
    return TeamContainer(
      this.id,
      this.name,
      this.firstSelected,
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
