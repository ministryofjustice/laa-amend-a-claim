package uk.gov.justice.laa.payments.amend.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.payments.amend.forms.errors.AmendmentFormError;

class AmendmentFormRedirectsTest {

  @Test
  void flashesPlainErrorListUnderFormErrorsNotTheRawBindingResult() {
    var redirectAttributes = mock(RedirectAttributes.class);
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors())
        .thenReturn(
            List.of(
                new FieldError(
                    "caseDetailsForm",
                    "inputs[FEE_CODE]",
                    null,
                    false,
                    new String[] {"Value is required"},
                    new Object[] {},
                    null)));

    var result =
        AmendmentFormRedirects.redirectWithErrors(
            redirectAttributes, bindingResult, "/some/redirect/url");

    verify(redirectAttributes)
        .addFlashAttribute(
            "formErrors", List.of(new AmendmentFormError("FEE_CODE", "Value is required")));
    assertThat(result).isEqualTo("redirect:/some/redirect/url");
  }

  @Test
  void flashesUnderFormErrorsKey() {
    var redirectAttributes = mock(RedirectAttributes.class);
    var bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(List.of());

    AmendmentFormRedirects.redirectWithErrors(redirectAttributes, bindingResult, "/other/url");

    verify(redirectAttributes).addFlashAttribute("formErrors", List.of());
  }

  @Test
  void flashesFormObjectWhenProvided() {
    var redirectAttributes = mock(RedirectAttributes.class);
    var bindingResult = mock(BindingResult.class);
    var form = new Object();
    when(bindingResult.getFieldErrors()).thenReturn(List.of());

    AmendmentFormRedirects.redirectWithErrors(
        redirectAttributes, bindingResult, "costsForm", form, "/other/url");

    verify(redirectAttributes).addFlashAttribute("formErrors", List.of());
    verify(redirectAttributes).addFlashAttribute("costsForm", form);
  }
}
