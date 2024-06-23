package net.ddellspe.escapegenai.util

import net.datafaker.Faker
import net.ddellspe.escapegenai.model.QuotePart

private val FAKER = Faker()
private val POSSIBLE_CHARACTERS: String =
  (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString("") + "!@#$%^&*()-=+_{}[]"

fun generateContent(originalWord: String, paragraphs: Int = 100): String {
  val content: StringBuilder = StringBuilder()
  (1..paragraphs).forEach { paragraph: Int ->
    content.append("<p>")
    (1..20).forEach { sentence: Int ->
      val sentenceWords: MutableList<String> =
        FAKER.lorem().sentence(9, 0).split(" ").toMutableList()
      sentenceWords.add((1..8).random(), originalWord)
      content.append(sentenceWords.joinToString(" "))
      if (sentence != 20) {
        content.append(" ")
      }
    }
    content.append("</p>")
    if (paragraph != paragraphs) {
      content.append("\n")
    }
  }
  return content.toString()
}

fun generateRandomBs(): String {
  return FAKER.company().bs().split(" ")[0].lowercase()
}

fun generateParts(quote: String): List<QuotePart> {
  return quote.split(" ").map { v -> QuotePart(part = v.lowercase()) }
}

fun passwordGenerator(): String {
  return 1.rangeTo((15..20).random())
    .asSequence()
    .map { POSSIBLE_CHARACTERS[POSSIBLE_CHARACTERS.indices.random()] }
    .joinToString("")
}

fun passwordPageGenerator(password: String): String {
  return "<a href=\"your password is: $password\">Another link</a>"
}
