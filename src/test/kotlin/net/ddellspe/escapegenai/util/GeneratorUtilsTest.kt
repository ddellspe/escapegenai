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
  fun testPasswordGenerator() {
    val password = passwordGenerator()
    assertEquals(true, password.length in 15..20)
    val pageContent = passwordPageGenerator(password)
    assertEquals(true, pageContent.contains(password))
  }
}
