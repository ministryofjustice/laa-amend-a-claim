package uk.gov.justice.laa.amend.claim.pages;

import com.microsoft.playwright.Page;

public class AssessDisbursementsPage extends LaaCostPage {

    public AssessDisbursementsPage(Page page) {
        super(page, "Assess disbursements");
    }
}