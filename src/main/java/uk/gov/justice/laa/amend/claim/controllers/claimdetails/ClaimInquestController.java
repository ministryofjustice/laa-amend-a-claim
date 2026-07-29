package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.InquestConfig;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.InquestForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.InquestDataService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimInquestDataWrite;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.InquestDepartmentReference;

@Controller
@RequiresFeatureFlag(Feature.INQUEST_TAB)
public class ClaimInquestController extends ClaimDetailsBaseController {

  private final InquestDataService inquestDataService;

  public ClaimInquestController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      FeatureFlagsConfig featureFlagsConfig,
      InquestConfig inquestConfig,
      InquestDataService inquestDataService) {
    super(assessmentService, userRetrievalService, featureFlagsConfig, inquestConfig);
    this.inquestDataService = inquestDataService;
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/inquest")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    ClaimDetails claim = requireInquestEligibleClaim(session, submissionId, claimId);

    InquestForm form =
        inquestDataService.get(claimId).map(InquestForm::from).orElseGet(InquestForm::new);

    return renderForm(request, session, model, claim, form);
  }

  @PostMapping("/submissions/{submissionId}/claims/{claimId}/inquest")
  public String onSubmit(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      HttpServletResponse response,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId,
      @Valid @ModelAttribute("form") InquestForm form,
      BindingResult bindingResult,
      @RequestParam(required = false) String addAuthority,
      @RequestParam(required = false) Integer removeAuthority,
      @AuthenticationPrincipal OidcUser user) {
    ClaimDetails claim = requireInquestEligibleClaim(session, submissionId, claimId);

    if (addAuthority != null) {
      form.getInterestedPublicAuthorities().add("");
      return renderForm(request, session, model, claim, form);
    }

    if (removeAuthority != null && form.getInterestedPublicAuthorities().size() > 1) {
      form.getInterestedPublicAuthorities().remove(removeAuthority.intValue());
      return renderForm(request, session, model, claim, form);
    }

    validateInterestedDepartmentCodes(form, bindingResult);

    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return renderForm(request, session, model, claim, form);
    }

    ClaimInquestDataWrite write =
        ClaimInquestDataWrite.builder()
            .deceasedForename(form.getDeceasedForename())
            .deceasedSurname(form.getDeceasedSurname())
            .deceasedDateOfBirth(form.getDeceasedDateOfBirth())
            .deceasedDateOfDeath(form.getDeceasedDateOfDeath())
            .coronersInquestReference(form.getCoronersInquestReference())
            .interestedDepartmentCodes(form.getInterestedDepartmentCodes())
            .interestedPublicAuthorities(nonBlank(form.getInterestedPublicAuthorities()))
            .actorUserId(user.getClaim("oid"))
            .build();

    inquestDataService.save(claimId, write);

    return "redirect:%s".formatted(inquestUrl(submissionId, claimId));
  }

  private String inquestUrl(UUID submissionId, UUID claimId) {
    return "/submissions/%s/claims/%s/inquest".formatted(submissionId, claimId);
  }

  private void validateInterestedDepartmentCodes(InquestForm form, BindingResult bindingResult) {
    Set<String> knownCodes =
        inquestDataService.getInquestDepartments().stream()
            .map(InquestDepartmentReference::getCode)
            .collect(Collectors.toSet());

    boolean hasUnknownCode =
        form.getInterestedDepartmentCodes().stream().anyMatch(code -> !knownCodes.contains(code));

    if (hasUnknownCode) {
      bindingResult.rejectValue(
          "interestedDepartmentCodes",
          "claimInquest.interestedDepartmentCodes.error.unknown",
          "Select a known interested department");
    }
  }

  private List<String> nonBlank(List<String> values) {
    return values.stream().filter(StringUtils::isNotBlank).toList();
  }

  private String renderForm(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      ClaimDetails claim,
      InquestForm form) {
    model.addAttribute("form", form);
    model.addAttribute("departments", inquestDataService.getInquestDepartments());

    var user = setLatestAssessment(claim);
    setCommonModelAttributes(model, session, request, claim, user);

    return "claimdetails/claim-inquest";
  }

  private ClaimDetails requireInquestEligibleClaim(
      HttpSession session, UUID submissionId, UUID claimId) {
    ClaimDetails claim = getClaim(session, submissionId, claimId);
    if (!isInquestEligible(claim)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim is not inquest-eligible");
    }
    return claim;
  }
}
