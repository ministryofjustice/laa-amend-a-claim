package uk.gov.justice.laa.amend.claim.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;

@Component
public class OutcomeTypeConverter implements Converter<String, OutcomeType> {

    @Override
    public OutcomeType convert(String source) {
        return OutcomeType.fromFormValue(source);
    }
}