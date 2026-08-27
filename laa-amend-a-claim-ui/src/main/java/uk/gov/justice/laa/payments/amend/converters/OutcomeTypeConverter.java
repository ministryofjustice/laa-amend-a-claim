package uk.gov.justice.laa.payments.amend.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;

@Component
public class OutcomeTypeConverter implements Converter<String, OutcomeType> {

  @Override
  public OutcomeType convert(String source) {
    return OutcomeType.fromFormValue(source);
  }
}
