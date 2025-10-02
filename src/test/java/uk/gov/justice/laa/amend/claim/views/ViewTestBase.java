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
    Assertions.assertNotNull(heading, String.format("Expected page to have heading '%s'", expectedText));
    Assertions.assertEquals(expectedText, heading.text());
  }

  protected void assertPageHasTitle(Document doc, String expectedText) {
    String title = doc.title();
    Assertions.assertEquals(String.format("%s - Amend a claim - GOV.UK", expectedText), title);
  }

  protected void assertPageHasHint(Document doc, String id, String expectedText) {
    Element hint = doc.getElementById(id);
    Assertions.assertNotNull(hint, String.format("Expected page to have hint with id '%s'", id));
    Assertions.assertEquals(expectedText, hint.text());
  }

  protected void assertPageHasTextInput(Document doc, String id, String expectedLabel) {
    Element input = doc.getElementById(id);
    Assertions.assertNotNull(input, String.format("Expected page to have input with id '%s'", id));
    Element label = doc.selectFirst(String.format("label[for=%s]", id));
    Assertions.assertNotNull(label, String.format("Expected page to have label for '%s'", id));
    Assertions.assertEquals(expectedLabel, label.text());
  }

  protected void assertPageHasDateInput(Document doc) {
    Element input = doc.selectFirst(".govuk-date-input");
    Assertions.assertNotNull(input, "Expected page to have date input");
  }
}
