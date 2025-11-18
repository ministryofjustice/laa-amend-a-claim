package uk.gov.justice.laa.amend.claim.mappers;


import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.mapstruct.Qualifier;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.SubmissionResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MapperConfig(
        componentModel = "spring",
        uses = {ClaimMapperHelper.class}, // Add helper mappers if needed
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)

public interface ClaimMapperConfig {


}
