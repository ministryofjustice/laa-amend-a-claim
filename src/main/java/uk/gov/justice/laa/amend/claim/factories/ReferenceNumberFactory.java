package uk.gov.justice.laa.amend.claim.factories;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.ReferenceNumber;

@Component
public class ReferenceNumberFactory {

    public ReferenceNumber create() {
        String referenceNumber = RandomStringUtils.secure().next(6, true, true).toUpperCase();
        return new ReferenceNumber(referenceNumber);
    }
}
