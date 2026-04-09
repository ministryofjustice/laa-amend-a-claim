package uk.gov.justice.laa.amend.claim.service;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

/**
 * Service for handling maintenance page data and defaults.
 */
public interface MaintenanceService {

    boolean maintenanceApplies(HttpServletRequest request);

    boolean maintenanceEnabled();

    ThymeleafString getMessage();

    ThymeleafString getTitle();
}
