package uk.gov.justice.laa.amend.claim.helpers;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;

public class PageHelper {

  public static Locator cardByTitle(String title, Page page) {
    return page.locator(".govuk-summary-card")
        .filter(
            new Locator.FilterOptions()
                .setHas(
                    page.locator("h2.govuk-summary-card__title")
                        .filter(new Locator.FilterOptions().setHasText(title))))
        .first();
  }

  public static Locator tableByCard(Locator card) {
    return card.locator("table.govuk-table");
  }

  public static Locator summaryListByCard(Locator card) {
    return card.locator("dl.govuk-summary-list");
  }

  public static Locator tableRowByLabel(Locator card, String label) {
    Locator table = tableByCard(card);
    return table.getByRole(AriaRole.ROW, new Locator.GetByRoleOptions().setName(label)).first();
  }

  public static Locator summaryListRowByLabel(Locator card, String label) {
    Locator summaryList = summaryListByCard(card);
    return summaryList
        .locator(
            String.format(
                ".govuk-summary-list__row:has(.govuk-summary-list__key:text-is('%s'))", label))
        .first();
  }

  public static void assertSummaryListValue(
      Locator rowSelector, int columnIndex, String expectedValue) {
    String actualValue = rowSelector.locator("dd").nth(columnIndex).textContent().trim();
    Assertions.assertEquals(expectedValue, actualValue, "Value mismatch in key");
  }

  public static void assertSummaryListRow(Page page, String title, String label, String... values) {
    Locator card = cardByTitle(title, page);
    Locator row = summaryListRowByLabel(card, label);

    for (int i = 0; i < values.length; ++i) {
      assertSummaryListValue(row, i, values[i]);
    }
  }
}
