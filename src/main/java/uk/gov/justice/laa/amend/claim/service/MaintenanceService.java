package uk.gov.justice.laa.amend.claim.service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

/**
 * Service for handling maintenance page data and defaults.
 */
public interface MaintenanceService {

    boolean maintenanceApplies(HttpServletRequest request) throws IOException;

    boolean maintenanceEnabled();

    ThymeleafString getMessage();

    ThymeleafString getTitle();
}
