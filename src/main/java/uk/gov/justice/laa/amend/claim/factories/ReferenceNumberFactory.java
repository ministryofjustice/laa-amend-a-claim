package uk.gov.justice.laa.amend.claim.factories;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class ReferenceNumberFactory {

    public String create() {
        return RandomStringUtils.secure().next(6, CHARS).toUpperCase();
    }

    private static final String CHARS = "ABCDEFHJKLMNPRSTUVWXYZ2345789";
}
