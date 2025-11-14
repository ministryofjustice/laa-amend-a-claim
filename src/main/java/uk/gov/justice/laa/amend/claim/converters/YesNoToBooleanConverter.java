package uk.gov.justice.laa.amend.claim.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class YesNoToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return "yes".equalsIgnoreCase(source);
    }
}