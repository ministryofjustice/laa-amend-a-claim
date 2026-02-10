package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;

public class AssessAssessedTotalsPage extends LaaTotalsPage {

    public AssessAssessedTotalsPage(Page page) {
        super(page, "Assess total claim value");

        this.totalVatInput = page.locator("#assessed-total-vat");

        this.totalInclVatInput = page.locator("#assessed-total-incl-vat");

        this.totalVatError = "Enter the total assessed VAT";

        this.totalInclVatError = "Enter the total assessed value of the claim";
    }
}
