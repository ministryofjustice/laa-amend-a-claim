package uk.gov.justice.laa.payments.amend.service;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;

public interface MaintenanceService {

  boolean maintenanceApplies(HttpServletRequest request);

  boolean maintenanceEnabled();

  ThymeleafString getMessage();

  ThymeleafString getTitle();
}
