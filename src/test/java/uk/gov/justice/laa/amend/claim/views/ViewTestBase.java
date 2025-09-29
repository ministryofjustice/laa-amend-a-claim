package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.Jsoup;
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

  protected IServletWebExchange exchange;

  protected String view;

  protected ViewTestBase(String view) {
    this.view = view;
  }

  @BeforeEach
  void setup() {
    MockServletContext context = new MockServletContext();
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    JakartaServletWebApplication app = JakartaServletWebApplication.buildApplication(context);
    this.exchange = app.buildExchange(request, response);
  }

  protected Document renderDocument() {
    return renderDocument(Map.of());
  }

  protected Document renderDocument(Map<String, Object> variables) {
    WebContext context = new WebContext(exchange, Locale.UK, variables);
    return Jsoup.parse(templateEngine.process(view, context));
  }

  protected void assertPageHasHeading(Document doc, String expectedText) {
    Element heading = doc.selectFirst("h1");
    Assertions.assertNotNull(heading, String.format("Expected page to have heading '%s' but no heading was found", expectedText));
    Assertions.assertEquals(expectedText, heading.text());
  }
}
