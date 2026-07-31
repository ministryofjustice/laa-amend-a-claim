package uk.gov.justice.laa.amend.claim.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.forms.errors.AmendmentFormError;

class AmendmentFormRedirectsTest {

  @Test
  void flashesPlainErrorListUnderGivenAttributeNameNotTheRawBindingResult() {
    var redirectAttributes = mock(RedirectAttributes.class);
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors())
        .thenReturn(
            List.of(new FieldError("caseDetailsForm", "inputs[FEE_CODE]", "Value is required")));

    var result =
        AmendmentFormRedirects.redirectWithErrors(
            redirectAttributes, bindingResult, "caseDetailsFormErrors", "/some/redirect/url");

    verify(redirectAttributes)
        .addFlashAttribute(
            "caseDetailsFormErrors",
            List.of(new AmendmentFormError("FEE_CODE", "Value is required")));
    assertThat(result).isEqualTo("redirect:/some/redirect/url");
  }

  @Test
  void usesGivenAttributeNameForFlashKey() {
    var redirectAttributes = mock(RedirectAttributes.class);
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(List.of());

    AmendmentFormRedirects.redirectWithErrors(
        redirectAttributes, bindingResult, "caseTypeFormErrors", "/other/url");

    verify(redirectAttributes).addFlashAttribute("caseTypeFormErrors", List.of());
  }
}
