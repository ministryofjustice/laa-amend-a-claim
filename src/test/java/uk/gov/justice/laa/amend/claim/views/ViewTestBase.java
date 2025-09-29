package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.Locale;
import java.util.Map;

@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
public abstract class ViewTestBase {

  @Autowired
  protected SpringTemplateEngine templateEngine;

  protected WebContext context;

  @BeforeEach
  void setup() {
    MockServletContext context = new MockServletContext();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    JakartaServletWebApplication app = JakartaServletWebApplication.buildApplication(context);
    IServletWebExchange exchange = app.buildExchange(request, response);
    this.context = new WebContext(exchange, Locale.UK, Map.of());
  }

  protected void assertPageHasHeading(Document doc, String expectedText) {
    Element heading = doc.selectFirst("h1");
    Assertions.assertNotNull(heading, String.format("Expected page to have heading '%s' but no heading was found", expectedText));
    Assertions.assertEquals(expectedText, heading.text());
  }
}
