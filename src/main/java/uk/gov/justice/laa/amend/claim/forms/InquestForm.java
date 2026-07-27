package uk.gov.justice.laa.amend.claim.forms;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestData;

/**
 * Backs the Inquest tab's single-page form, staged in {@code HttpSession} between viewing and
 * saving.
 */
@Getter
@Setter
public class InquestForm implements Serializable {

  private String deceasedForename;
  private String deceasedSurname;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate deceasedDateOfBirth;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate deceasedDateOfDeath;

  private String coronersInquestReference;
  private Set<String> interestedDepartmentCodes = new LinkedHashSet<>();
  private List<String> interestedPublicAuthorities = new ArrayList<>(List.of(""));

  public static InquestForm from(ClaimInquestData data) {
    var form = new InquestForm();
    form.setDeceasedForename(data.getDeceasedForename());
    form.setDeceasedSurname(data.getDeceasedSurname());
    form.setDeceasedDateOfBirth(data.getDeceasedDateOfBirth());
    form.setDeceasedDateOfDeath(data.getDeceasedDateOfDeath());
    form.setCoronersInquestReference(data.getCoronersInquestReference());
    form.setInterestedDepartmentCodes(
        data.getInterestedDepartmentCodes() == null
            ? new LinkedHashSet<>()
            : new LinkedHashSet<>(data.getInterestedDepartmentCodes()));
    var authorities =
        data.getInterestedPublicAuthorities() == null
            ? new ArrayList<String>()
            : new ArrayList<>(data.getInterestedPublicAuthorities());
    if (authorities.isEmpty()) {
      authorities.add("");
    }
    form.setInterestedPublicAuthorities(authorities);
    return form;
  }
}
