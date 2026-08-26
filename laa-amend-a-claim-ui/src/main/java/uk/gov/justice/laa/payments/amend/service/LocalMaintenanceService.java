package uk.gov.justice.laa.payments.amend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;

@Service
@RequiredArgsConstructor
@Profile("local")
public class LocalMaintenanceService implements MaintenanceService {

  @Override
  public boolean maintenanceApplies(HttpServletRequest request) {
    return false;
  }

  @Override
  public boolean maintenanceEnabled() {
    return false;
  }

  @Override
  public ThymeleafString getMessage() {
    return new ThymeleafMessage("maintenance.default.message");
  }

  @Override
  public ThymeleafString getTitle() {
    return new ThymeleafMessage("maintenance.default.title");
  }
}
