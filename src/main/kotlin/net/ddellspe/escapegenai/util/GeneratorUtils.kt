package net.ddellspe.escapegenai.util

import net.datafaker.Faker

private val FAKER = Faker()

fun generateProductName(): String {
  return FAKER.commerce().productName()
}
