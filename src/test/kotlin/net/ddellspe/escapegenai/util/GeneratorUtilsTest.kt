package net.ddellspe.escapegenai.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GeneratorUtilsTest {
  @Test
  fun testGenerateContent() {
    val content = generateContent("word")
    assertEquals(20000, content.split("\\s+".toRegex()).size)
    val noContent = generateContent("word", 0)
    assertEquals("", noContent)
    val smallContent = generateContent("word", 1)
    assertEquals(200, smallContent.split("\\s+".toRegex()).size)
    val negativeContent = generateContent("word", -1)
    assertEquals("", negativeContent)
  }

  @Test
  fun testWordCount() {
    val content = generateContent("word")
    assertEquals(20000, content.split("\\s+".toRegex()).size)
    val words =
      content
        .replace("<p>", "")
        .replace(".</p>\n", " ")
        .split(" ")
        .stream()
        .map(String::lowercase)
        .toList()
    val counts =
      words.distinct().associateWith { word ->
        words.stream().filter { w -> w.equals(word) }.count()
      }
    assertEquals(2000, counts["word"])
    assertEquals(2000, counts.values.max())
  }

  @Test
  fun testPasswordGenerator() {
    val password = passwordGenerator()
    assertEquals(true, password.length in 15..20)
    val pageContent = passwordPageGenerator(password)
    assertEquals(true, pageContent.contains(password))
  }

  @Test
  fun testGenerateFunFactType() {
    assertEquals("companyIndustry", generateFunFactType(7))
    assertEquals("companyIndustry", generateFunFactType(6))
    assertEquals("author", generateFunFactType(0))
    assertEquals("author", generateFunFactType(1))
    assertEquals(
      true,
      listOf(
          "author",
          "authorAddress",
          "authorTitle",
          "company",
          "companyAddress",
          "companyIndustry",
        )
        .contains(generateFunFactType()),
    )
  }
}
