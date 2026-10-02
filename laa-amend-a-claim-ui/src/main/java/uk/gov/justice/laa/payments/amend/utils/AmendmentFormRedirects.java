package uk.gov.justice.laa.payments.amend.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@UtilityClass
public class AmendmentFormRedirects {

  private static final ThymeleafUtils THYMELEAF_UTILS = new ThymeleafUtils();

  public static String redirectWithErrors(
      RedirectAttributes redirectAttributes, BindingResult bindingResult, String redirectUrl) {
    var errors =
        new ArrayList<>(THYMELEAF_UTILS.toAmendmentFormErrors(bindingResult.getFieldErrors()));
    redirectAttributes.addFlashAttribute("formErrors", errors);
    return "redirect:" + redirectUrl;
  }

  public static String redirectWithErrors(
      RedirectAttributes redirectAttributes,
      BindingResult bindingResult,
      String formAttributeName,
      Object form,
      String redirectUrl) {
    return redirectWithErrors(
        redirectAttributes, bindingResult, formAttributeName, form, List.of(), redirectUrl);
  }

  public static String redirectWithErrors(
      RedirectAttributes redirectAttributes,
      BindingResult bindingResult,
      String formAttributeName,
      Object form,
      Collection<? extends ClaimViewField<?>> pageFields,
      String redirectUrl) {
    List<String> fieldOrder = pageFields.stream().map(ClaimViewField::name).toList();
    var errors =
        new ArrayList<>(
            THYMELEAF_UTILS.toAmendmentFormErrors(bindingResult.getFieldErrors(), fieldOrder));
    redirectAttributes.addFlashAttribute("formErrors", errors);
    redirectAttributes.addFlashAttribute(formAttributeName, form);
    return "redirect:" + redirectUrl;
  }
}
