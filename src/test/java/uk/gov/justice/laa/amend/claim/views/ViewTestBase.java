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
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.util.ArrayList;
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

  protected void assertPageHasBackLinkWithHref(Document doc, String expectedHref) {
    Elements elements = doc.getElementsByClass("govuk-back-link");
    Assertions.assertFalse(elements.isEmpty(), "Expected page to have a back link");
    Element backLink = elements.first();
    Assertions.assertEquals(expectedHref, backLink.attr("href"), "Back link href does not match expected URL");
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

  protected void assertPageHasLink(Document doc, String id, String expectedText, String expectedHref) {
    Element element = getElementById(doc, id);
    Assertions.assertEquals(expectedText, element.text());
    Assertions.assertEquals(expectedHref, element.attr("href"));
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

  protected void assertPageHasSummaryCard(Document doc, String expectedText) {
    Elements cards = doc.getElementsByClass("govuk-summary-card");

    boolean cardFound = cards.stream().anyMatch(card -> {
      String text = card.select(".govuk-summary-card__title").text().trim();
      return text.equals(expectedText);
    });

    Assertions.assertTrue(cardFound);
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

  protected void assertPageHasRadioButtons(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-radios");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected void assertPageHasInlineRadioButtons(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-radios--inline");
    Assertions.assertFalse(elements.isEmpty());
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

  protected void assertPageHasInformationAlert(Document doc, String expectedTitle, String expectedText) {
    Element element = selectFirst(doc, ".moj-alert--information");

    Element title = selectFirst(element, ".moj-alert__heading");
    Assertions.assertEquals(expectedTitle, title.text());

    Element content = selectFirst(element, ".moj-alert__content > span");
    Assertions.assertEquals(expectedText, content.text());
  }

  protected void assertPageHasPanel(Document doc) {
    Elements elements = doc.getElementsByClass("govuk-panel");
    Assertions.assertFalse(elements.isEmpty());
  }

  protected List<List<Element>> getTable(Document doc, String summaryCardTitle) {
    Element summaryCard = getSummaryCard(doc, summaryCardTitle);
    Element table = selectFirst(summaryCard, "table.govuk-table");
    List<List<Element>> matrix = new ArrayList<>();
    for (Element row : table.select("tbody tr")) {
      List<Element> cells = row.select("td").stream().toList();
      matrix.add(cells);
    }
    return matrix;
  }

  protected List<List<Element>> getSummaryList(Document doc, String summaryCardTitle) {
    Element summaryCard = getSummaryCard(doc, summaryCardTitle);
    Element summaryList = selectFirst(summaryCard, "dl.govuk-summary-list");
    List<List<Element>> matrix = new ArrayList<>();
    for (Element row : summaryList.select(".govuk-summary-list__row")) {
      Element key =  selectFirst(row, ".govuk-summary-list__key");
      List<Element> values = row.select(".govuk-summary-list__value").stream().toList();
      List<Element> cells = new ArrayList<>();
      cells.add(key);
      cells.addAll(values);
      matrix.add(cells);
    }
    return matrix;
  }

  protected Element getSummaryCard(Document doc, String summaryCardTitle) {
      return selectFirst(
          doc,
          ".govuk-summary-card:has(h2.govuk-summary-card__title:matchesOwn(^" + summaryCardTitle + "$))"
      );
  }

  private void assertCellContainsText(Element cell, String expectedText) {
    Assertions.assertEquals(expectedText, cell.text(), "Cell does not contain expected text: " + expectedText);
  }

  private void assertCellIsEmpty(Element cell) {
    assertCellContainsText(cell, "");
  }

  private void assertCellContainsChangeLink(Element cell, String expectedHref, String expectedHiddenText) {
    assertCellContainsLink(cell, "Change", expectedHref, expectedHiddenText);
  }

  private void assertCellContainsAddLink(Element cell, String expectedHref, String expectedHiddenText) {
    assertCellContainsLink(cell, "Add", expectedHref, expectedHiddenText);
  }

  private void assertCellContainsLink(Element cell, String expectedText, String expectedHref, String expectedHiddenText) {
    Element link = selectFirst(cell, "a.govuk-link");
    Assertions.assertEquals(expectedText, link.ownText());
    Assertions.assertEquals(expectedHref, link.attr("href"));
    Element hiddenText = selectFirst(link, "span.govuk-visually-hidden");
    Assertions.assertEquals(String.format(" %s", expectedHiddenText), hiddenText.wholeOwnText());
  }

  protected void assertTableRowContainsValuesWithNoChangeLink(
      List<Element> row,
      String label,
      String calculated,
      String requested,
      String assessed
  ) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), calculated);
    assertCellContainsText(row.get(2), requested);
    assertCellContainsText(row.get(3), assessed);
    assertCellIsEmpty(row.get(4));
  }

  protected void assertTableRowContainsValuesWithChangeLink(
      List<Element> row,
      String label,
      String calculated,
      String requested,
      String assessed,
      String changeUrl
  ) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), calculated);
    assertCellContainsText(row.get(2), requested);
    assertCellContainsText(row.get(3), assessed);
    assertCellContainsChangeLink(row.get(4), changeUrl, label);
  }

    protected void assertTableRowContainsValuesWithChangeLink(
            List<Element> row,
            String label,
            String value,
            String changeUrl
    ) {
        assertCellContainsText(row.getFirst(), label);
        assertCellContainsText(row.get(1), value);
        assertCellContainsChangeLink(row.get(2), changeUrl, label);
    }

  protected void assertTableRowContainsValuesWithAddLink(
      List<Element> row,
      String label,
      String calculated,
      String requested,
      String addUrl
  ) {
    assertCellContainsText(row.getFirst(), label);
    assertCellContainsText(row.get(1), calculated);
    assertCellContainsText(row.get(2), requested);
    assertCellContainsAddLink(row.get(3), addUrl, label);
    assertCellIsEmpty(row.get(4));
  }

  protected void assertSummaryListRowContainsValues(
      List<Element> row,
      String label,
      String... values
  ) {
    assertCellContainsText(row.getFirst(), label);
    for (int i = 0; i < values.length; i++) {
      assertCellContainsText(row.get(i + 1), values[i]);
    }
  }

  protected Elements getTableHeaders(Document doc) {
    return doc.getElementsByClass("govuk-table__header");
  }

  protected void assertTableHeaderIsSortable(Element header, String ariaSort, String expectedText, String expectedLink) {
    Assertions.assertEquals(ariaSort, header.attr("aria-sort"));
    Element link = selectFirst(header, "a");
    Element span = selectFirst(link, "span");
    Assertions.assertEquals(expectedText, span.text());
    Assertions.assertEquals(expectedLink, link.attr("href"));
  }

  protected void assertTableHeaderIsNotSortable(Element header, String expectedText) {
    Assertions.assertEquals(expectedText, header.text());
  }
}
