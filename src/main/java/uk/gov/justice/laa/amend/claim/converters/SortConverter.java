package uk.gov.justice.laa.amend.claim.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.Sort;

@Component
public class SortConverter implements Converter<String, Sort> {

    @Override
    public Sort convert(String source) {
        return new Sort(source);
    }
}
