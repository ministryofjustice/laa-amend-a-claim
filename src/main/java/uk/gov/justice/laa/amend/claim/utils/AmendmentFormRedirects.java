package uk.gov.justice.laa.amend.claim.utils;

import java.util.ArrayList;
import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@UtilityClass
public class AmendmentFormRedirects {

  private static final ThymeleafUtils THYMELEAF_UTILS = new ThymeleafUtils();

  public static String redirectWithErrors(
      RedirectAttributes redirectAttributes,
      BindingResult bindingResult,
      String errorsAttributeName,
      String redirectUrl) {
    var errors =
        new ArrayList<>(THYMELEAF_UTILS.toAmendmentFormErrors(bindingResult.getFieldErrors()));
    redirectAttributes.addFlashAttribute(errorsAttributeName, errors);
    return "redirect:" + redirectUrl;
  }
}
