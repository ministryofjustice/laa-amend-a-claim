package uk.gov.justice.laa.amend.claim.views;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ThymeleafConfig.class)
public abstract class ViewTestBase {

  @Autowired
  public MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    session = new MockHttpSession();
    claim = MockClaimsFunctions.createMockCivilClaim();
  }

  protected String mapping;

  protected MockHttpSession session;
  protected ClaimDetails claim;

  protected final String claimId = "claimId";

  protected ViewTestBase(String mapping) {
    this.mapping = mapping;
  }

  protected Document renderDocument() throws Exception {
    return renderDocument(Map.of());
  }

  protected Document renderDocument(Map<String, Object> variables) throws Exception {
    session.setAttribute("claimId", claim);
    MockHttpServletRequestBuilder requestBuilder = get(mapping).session(session);

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

  protected Document renderDocumentWithErrors(MultiValueMap<String, String> params) throws Exception {
    session.setAttribute("claimId", claim);
    MockHttpServletRequestBuilder requestBuilder = post(mapping).session(session);

    String html = mockMvc.perform(requestBuilder.params(params))
        .andExpect(status().isBadRequest())
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

  protected void assertPageHasBackLink(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-back-link");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasPrimaryButton(Document doc, String expectedText) {
    Elements elements = doc.getElementsByClass("govuk-button");
    Assertions.assertFalse(elements.isEmpty());
    Assertions.assertEquals(expectedText, elements.getFirst().text());
  }

  protected void assertPageHasPrimaryButtonDisabled(Document doc, String expectedText) {
    Elements elements = doc.getElementsByClass("govuk-button");
    Assertions.assertFalse(elements.isEmpty());
    Element button = elements.getFirst();
    Assertions.assertEquals(expectedText, button.text());
    Assertions.assertTrue(button.hasAttr("disabled"));
  }

  protected void assertPageHasSecondaryButton(Document doc, String expectedText) {
    Elements elements = doc.getElementsByClass("govuk-button--secondary");
    Assertions.assertFalse(elements.isEmpty());
    Assertions.assertEquals(expectedText, elements.getFirst().text());
  }

  protected void assertPageHasLink(Document doc, String id, String expectedText) {
    Element element = getElementById(doc, id);
    Assertions.assertEquals(expectedText, element.text());
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

  protected void assertPageHasContent(Document doc, String expectedText) {
    Assertions.assertTrue(doc.text().contains(expectedText));
  }

  protected void assertPageHasTable(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-table");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasPagination(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-pagination");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasSuccessBanner(Document doc, String expectedText) {
    Element banner = selectFirst(doc, ".govuk-notification-banner--success");
    Element title = selectFirst(banner, ".govuk-notification-banner__title");
    Assertions.assertEquals("Success", title.text());
    Element content = selectFirst(banner, ".govuk-notification-banner__content");
    Assertions.assertEquals(expectedText, content.text());
  }

  protected void assertPageHasSummaryList(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-summary-list");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasRadioButtons(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-radios");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasInlineRadioButtons(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-radios--inline");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasNoSummaryList(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-summary-list");
    Assertions.assertTrue(elements.isEmpty());
  }

  protected void assertPageHasSummaryListRow(Document doc, String expectedKey, String expectedValue) {
    Elements rows = doc.getElementsByClass("govuk-summary-list__row");
    boolean rowFound = rows.stream().anyMatch(row -> {
      String keyText = row.select(".govuk-summary-list__key").text().trim();
      String valueText = row.select(".govuk-summary-list__value").text().trim();
      return keyText.equals(expectedKey) && valueText.equals(expectedValue);
    });
    Assertions.assertTrue(rowFound);
  }

  protected void assertPageHasValuesRow(Document doc, String expectedKey, ClaimField claimFieldRow, boolean checkAssessed) {
    Elements rows = doc.getElementsByClass("govuk-summary-list__row");
    boolean rowFound = rows.stream().anyMatch(row -> {
      String keyText = row.select(".govuk-summary-list__key").text().trim();
      Elements valueElements = row.select(".govuk-summary-list__value");
      if (valueElements.size() < 2) return false;
      String calculatedText = valueElements.get(0).text().trim();
      String submittedText = valueElements.get(1).text().trim();

      boolean value = keyText.equals(expectedKey) && calculatedText.equals(claimFieldRow.getCalculated().toString()) && submittedText.equals(claimFieldRow.getSubmitted().toString());
      if (checkAssessed) {
        Object expectedAssessedValue = claimFieldRow.getAssessed();
        if (expectedAssessedValue == null) {
          expectedAssessedValue = "Not applicable";
        }
        return value && expectedAssessedValue.toString().equals(valueElements.get(2).text().trim());
      }
      return value;
    });
    Assertions.assertTrue(rowFound);
  }


  protected void assertPageHasErrorSummary(Document doc, String... errorFields) {
    Element errorSummary = selectFirst(doc, ".govuk-error-summary");
    Element errorSummaryList = selectFirst(errorSummary, ".govuk-error-summary__list");

    List<String> actualErrorHrefs = errorSummaryList
        .select("li a")
        .stream()
        .map(element -> element.attr("href"))
        .toList();

    List<String> expectedErrorHrefs = Arrays.stream(errorFields)
        .map(field -> "#" + field)
        .toList();

    Assertions.assertEquals(expectedErrorHrefs, actualErrorHrefs);
  }

  protected void assertPageHasErrorAlert(Document doc) {
    Elements elements = doc.getElementsByClass("moj-alert moj-alert--error");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasPanel(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-panel");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected boolean pageHasLabel(Document doc, String label) {
    return !doc.select("dl.govuk-summary-list dt.govuk-summary-list__key:containsOwn(" + label + ")").isEmpty();
  }

  protected void assertPageHasInfoBanner(Document doc) {
    Elements elements = doc.getElementsByClass("moj-alert__content");
    Assertions.assertFalse(elements.isEmpty(), "Expected info banner but none found");

    String bannerText = elements.text();
    Assertions.assertTrue(bannerText.contains("Last edited by"),
        "Info banner does not contain expected text. Actual: " + bannerText);

  }

  protected void assertPageHasUpdateAssessmentButton(Document doc) {
    Elements elements = doc.select("div.govuk-button-group button.govuk-button");

    Assertions.assertFalse(elements.isEmpty(), "Expected Button not found");

    String buttonText = elements.text();
    Assertions.assertTrue(buttonText.contains("Update assessment outcome"),
            "Button does not contain expected label. Actual: " + buttonText);
  }
}
