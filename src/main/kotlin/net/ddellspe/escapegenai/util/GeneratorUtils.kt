package net.ddellspe.escapegenai.util

import java.util.*
import net.datafaker.Faker

private val FAKER = Faker(Locale.US)

fun generateProductName(): String {
  return FAKER.commerce().productName()
}

fun generateCompanyName(): String {
  return FAKER.company().name()
}

fun generateFormattedCompanyAddress(): String {
  val address = FAKER.address()
  return address.fullAddress()
}
