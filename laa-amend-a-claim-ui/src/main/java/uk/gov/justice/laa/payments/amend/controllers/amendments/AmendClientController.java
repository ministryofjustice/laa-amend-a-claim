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
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.payments.amend.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendClientController extends AbstractAmendController {

  public AmendClientController(
      List<GenericAmendmentFieldValidator> genericAmendmentFieldValidators,
      List<FieldSpecificAmendmentValidator> fieldSpecificAmendmentValidators) {
    super(genericAmendmentFieldValidators, fieldSpecificAmendmentValidators);
  }

  @InitBinder({"client1Form", "client2Form"})
  public void initClientFormBinder(
      WebDataBinder binder,
      HttpSession session,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    initBinder(binder, session, submissionId, claimId);
  }

  @GetMapping("/amend-client")
  public String viewAmendClient(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var clientView = ClaimClientViewFactory.create(claim);
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("clientView", clientView);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    model.addAttribute("forms", amendmentForms);

    return "pages/amendments/amend-client-1";
  }

  @PostMapping("/amend-client")
  public String amendClient1(
      HttpSession session,
      @Valid @ModelAttribute("client1Form") AmendmentForm client1Form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);

    amendmentForms.getClient1Form().setCurrent(client1Form);
    saveAmendmentForms(session, claimId, amendmentForms);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "/submissions/%s/claims/%s/amendments/amend-client".formatted(submissionId, claimId));
    }

    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }

  @GetMapping("/amend-client-two")
  public String viewAmendClientTwo(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var clientView = ClaimClientViewFactory.create(claim);
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("clientView", clientView);
    model.addAttribute("client2Form", amendmentForms.getClient2Form().getCurrent());
    model.addAttribute("forms", amendmentForms);

    return "pages/amendments/amend-client-2";
  }

  @PostMapping("/amend-client-two")
  public String postAmendClient2(
      HttpSession session,
      @Valid @ModelAttribute("client2Form") AmendmentForm client2Form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);

    amendmentForms.getClient2Form().setCurrent(client2Form);
    saveAmendmentForms(session, claimId, amendmentForms);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "/submissions/%s/claims/%s/amendments/amend-client-two".formatted(submissionId, claimId));
    }

    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
