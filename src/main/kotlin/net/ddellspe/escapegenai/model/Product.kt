package net.ddellspe.escapegenai.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlin.random.Random
import net.ddellspe.escapegenai.util.generateProductName

@Entity
@Table(name = "product")
class Product(
  @Id @GeneratedValue var id: Long?,
  var name: String = generateProductName(),
  var price: Int = Random.nextInt(10, 300),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Product

    if (name != other.name) return false
    if (price != other.price) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + price
    return result
  }

  override fun toString(): String {
    return "Product(id=$id, name='$name', price=$price)"
  }
}
