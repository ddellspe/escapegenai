package net.ddellspe.escapegenai.config

import java.nio.charset.StandardCharsets
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode

@Configuration
class ThymeleafConfiguration {
  @Bean
  fun springTemplateEngine(): SpringTemplateEngine {
    val templateEngine = SpringTemplateEngine()
    templateEngine.addTemplateResolver(templateResolver())
    return templateEngine
  }

  @Bean
  fun templateResolver(): SpringResourceTemplateResolver {
    val resolver = SpringResourceTemplateResolver()
    resolver.prefix = "classpath:/templates/"
    resolver.suffix = ".html"
    resolver.templateMode = TemplateMode.XML
    resolver.characterEncoding = StandardCharsets.UTF_8.name()
    return resolver
  }
}
