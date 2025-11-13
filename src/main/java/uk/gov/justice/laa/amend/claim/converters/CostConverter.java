package uk.gov.justice.laa.amend.claim.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.models.Cost;

@Component
public class CostConverter implements Converter<String, Cost> {

    @Override
    public Cost convert(String source) {
        try {
            return Cost.fromPath(source);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
