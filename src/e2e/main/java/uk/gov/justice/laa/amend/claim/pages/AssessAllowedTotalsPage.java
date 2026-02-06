package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;

public class AssessAllowedTotalsPage extends LaaTotalsPage {

    public AssessAllowedTotalsPage(Page page) {
        super(page, "Assess total allowed value");

        this.totalVatInput = page.locator("#allowed-total-vat");

        this.totalInclVatInput = page.locator("#allowed-total-incl-vat");

        this.totalVatError = "Enter the total allowed VAT";

        this.totalInclVatError = "Enter the total allowed value of the claim";
    }
}
