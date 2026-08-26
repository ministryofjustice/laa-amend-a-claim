package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.exceptions.FeeCodeNotFoundException;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.service.AvailableFeeCodesService;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOptions;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendCaseTypeController extends AbstractAmendController {

  private final AvailableFeeCodesService availableFeeCodesService;

  public AmendCaseTypeController(
      List<GenericAmendmentFieldValidator> genericAmendmentFieldValidators,
      List<FieldSpecificAmendmentValidator> fieldSpecificAmendmentValidators,
      AvailableFeeCodesService availableFeeCodesService) {
    super(genericAmendmentFieldValidators, fieldSpecificAmendmentValidators);
    this.availableFeeCodesService = availableFeeCodesService;
  }

  @InitBinder("caseTypeForm")
  public void initCaseTypeFormBinder(
      WebDataBinder binder,
      HttpSession session,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    initBinder(binder, session, submissionId, claimId);
  }

  @GetMapping("/amend-fee-code")
  public String amendFeeCode(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    requireEditable(ClaimDetailsViewField.FEE_CODE, claim);

    var availableFeeCodes = availableFeeCodesService.getAvailableFeeCodes(claim.getAreaOfLaw());
    if (!availableFeeCodes.containsKey(claim.getFeeCode())) {
      throw new FeeCodeNotFoundException(claim.getFeeCode());
    }

    var currentFeeCode = availableFeeCodes.get(claim.getFeeCode());
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("currentFeeCode", currentFeeCode);
    model.addAttribute("feeCodes", availableFeeCodes);
    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());
    return "pages/amendments/amend-fee-code";
  }

  @PostMapping("/amend-fee-code")
  public String postAmendFeeCode(
      HttpSession session,
      @Valid @ModelAttribute("caseTypeForm") AmendmentForm caseTypeForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    requireEditable(ClaimDetailsViewField.FEE_CODE, claim);

    var amendmentForms = getAmendmentForms(session, claimId);

    saveCaseTypeForm(session, claimId, amendmentForms, caseTypeForm, claim);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "caseTypeFormErrors",
          "/submissions/%s/claims/%s/amendments/amend-fee-code".formatted(submissionId, claimId));
    }

    if (claim.getAreaOfLaw() == AreaOfLaw.CRIME_LOWER) {
      return "redirect:/submissions/%s/claims/%s/amendments/amend-stage-reached"
          .formatted(submissionId, claimId);
    }

    return "redirect:/submissions/%s/claims/%s/amendments/amend-matter-type"
        .formatted(submissionId, claimId);
  }

  @GetMapping("/amend-stage-reached")
  public String getAmendStageReached(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (claim.getAreaOfLaw() != AreaOfLaw.CRIME_LOWER) {
      return "redirect:/submissions/%s/claims/%s/amendments/amend-matter-type"
          .formatted(submissionId, claimId);
    }
    requireEditable(CrimeClaimDetailsViewField.STAGE_REACHED, claim);

    var amendmentForms = getAmendmentForms(session, claimId);
    model.addAttribute("forms", amendmentForms);
    model.addAttribute("stageReachedOptions", FieldOptions.CRIME_STAGE_REACHED);
    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());
    return "pages/amendments/amend-stage-reached";
  }

  @PostMapping("/amend-stage-reached")
  public String postAmendStageReached(
      HttpSession session,
      @Valid @ModelAttribute("caseTypeForm") AmendmentForm caseTypeForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (claim.getAreaOfLaw() != AreaOfLaw.CRIME_LOWER) {
      return "redirect:/submissions/%s/claims/%s/amendments/amend-matter-type"
          .formatted(submissionId, claimId);
    }
    requireEditable(CrimeClaimDetailsViewField.STAGE_REACHED, claim);

    var amendmentForms = getAmendmentForms(session, claimId);

    saveCaseTypeForm(session, claimId, amendmentForms, caseTypeForm, claim);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "caseTypeFormErrors",
          "/submissions/%s/claims/%s/amendments/amend-stage-reached"
              .formatted(submissionId, claimId));
    }

    return "redirect:/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }

  @GetMapping("/amend-matter-type")
  public String getAmendMatterType(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    getValidClaim(session, submissionId, claimId);

    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("forms", amendmentForms);
    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());

    return "pages/amendments/amend-matter-type";
  }

  @PostMapping("/amend-matter-type")
  public String postAmendMatterType(
      HttpSession session,
      @Valid @ModelAttribute("caseTypeForm") AmendmentForm caseTypeForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    saveCaseTypeForm(session, claimId, amendmentForms, caseTypeForm, claim);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "caseTypeFormErrors",
          "/submissions/%s/claims/%s/amendments/amend-matter-type"
              .formatted(submissionId, claimId));
    }

    return "redirect:/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }

  private static void saveCaseTypeForm(
      HttpSession session,
      UUID claimId,
      AmendmentForms amendmentForms,
      AmendmentForm caseTypeForm,
      ClaimDetails claim) {
    var caseType = amendmentForms.getCaseTypeForm();
    var lockedFields =
        lockedFields(ClaimCaseViewFactory.create(claim).caseTypeRows().keySet(), claim);

    caseType.setCurrent(retainLockedInputs(caseTypeForm, caseType.getOriginal(), lockedFields));
    saveAmendmentForms(session, claimId, amendmentForms);
  }
}
