package net.ddellspe.escapegenai.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GameDataControllerApplicationTest {

  @Autowired private val mockMvc: MockMvc? = null

  @Test
  @Sql(scripts = ["classpath:db/invoice_as_primary.sql"])
  fun testInvoiceAsPrimary() {
    mockMvc
      ?.perform(get("/game/invoice/00000000-0000-0000-0000-000000000002"))
      ?.andExpect(status().isOk())
      ?.andExpect(
        header()
          .string(CONTENT_DISPOSITION, "inline; filename=teamName First Invoice 123456789.pdf")
      )
  }

  @Test
  @Sql(scripts = ["classpath:db/invoice_as_nonprimary.sql"])
  fun testInvoiceAsNonPrimary() {
    mockMvc
      ?.perform(get("/game/invoice/00000000-0000-0000-0000-000000000005"))
      ?.andExpect(status().isOk())
      ?.andExpect(
        header().string(CONTENT_DISPOSITION, "inline; filename=teamName Invoice 123456790.pdf")
      )
  }
}
