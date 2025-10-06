package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ViewTestBase {

  @Autowired
  private MockMvc mockMvc;

  protected String mapping;

  protected ViewTestBase(String mapping) {
    this.mapping = mapping;
  }

  protected Document renderDocument() throws Exception {
    return renderDocument(Map.of());
  }

  protected Document renderDocument(Map<String, Object> variables) throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get(mapping);

    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      requestBuilder = requestBuilder.flashAttr(entry.getKey(), entry.getValue());
    }

    String html = mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    return Jsoup.parse(html);
  }

  protected void assertPageHasHeading(Document doc, String expectedText) {
    Element heading = selectFirst(doc, "h1");
    Assertions.assertEquals(expectedText, heading.text());
  }

  protected void assertPageHasTitle(Document doc, String expectedText) {
    String title = doc.title();
    Assertions.assertEquals(String.format("%s - Amend a claim - GOV.UK", expectedText), title);
  }

  protected void assertPageHasHint(Document doc, String id, String expectedText) {
    Element hint = getElementById(doc, id);
    Assertions.assertEquals(expectedText, hint.text());
  }

  protected void assertPageHasTextInput(Document doc, String id, String expectedLabel) {
    Element label = selectFirst(doc, String.format("label[for=%s]", id));
    Assertions.assertEquals(expectedLabel, label.text());
  }

  protected void assertPageHasDateInput(Document doc, String expectedLegend) {
    Element input = selectFirst(doc, ".govuk-date-input");
    Element fieldset = input.parent();
    Assertions.assertNotNull(fieldset);
    Element legend = selectFirst(fieldset, "legend");
    Assertions.assertEquals(expectedLegend, legend.text());
  }

  protected void assertPageHasActiveServiceNavigationItem(Document doc, String expectedText) {
    Element element = selectFirst(doc, ".govuk-service-navigation__item--active");
    Assertions.assertEquals(expectedText, element.text());
  }

  protected void assertPageHasNoActiveServiceNavigationItems(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-service-navigation__item--active");
    Assertions.assertTrue(elements.isEmpty(), "Expected page to have no active service navigation items");
  }

  private Element selectFirst(Element element, String cssQuery) {
    Element result = element.selectFirst(cssQuery);
    Assertions.assertNotNull(element, String.format("Expected page to have element with CSS query '%s'", cssQuery));
    return result;
  }

  private Element getElementById(Element element, String id) {
    return selectFirst(element, String.format("#%s", id));
  }

  protected void assertPageHasH2(Document doc, String expectedText) {
    Element heading = selectFirst(doc, "h2");
    Assertions.assertEquals(expectedText, heading.text());
  }

  protected void assertPageHasTable(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-table");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasPagination(Document doc) {
    Elements elements = doc.getElementsByClass("moj-pagination");
    Assertions.assertFalse(elements.isEmpty());
  }
}
