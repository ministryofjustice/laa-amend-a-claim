package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;
import uk.gov.justice.laa.payments.amend.support.TestMessageSources;

class ClaimViewFieldLabelTest {

  private static final MessageSource MESSAGE_SOURCE = TestMessageSources.real();

  static Stream<ClaimViewField<?>> allFields() {
    var fields = new ArrayList<ClaimViewField<?>>();
    fields.addAll(List.of(ClaimDetailsViewField.values()));
    fields.addAll(List.of(CivilClaimDetailsViewField.values()));
    fields.addAll(List.of(CrimeClaimDetailsViewField.values()));
    fields.addAll(List.of(MediationClaimDetailsViewField.values()));
    return fields.stream();
  }

  @ParameterizedTest
  @MethodSource("allFields")
  void labelResolvesWithoutError(ClaimViewField<?> field) {
    assertThatCode(() -> field.label(MESSAGE_SOURCE)).doesNotThrowAnyException();
    assertThat(field.label(MESSAGE_SOURCE)).isNotBlank();
  }
}
