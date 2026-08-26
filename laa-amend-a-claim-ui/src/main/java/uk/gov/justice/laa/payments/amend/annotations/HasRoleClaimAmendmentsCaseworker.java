package uk.gov.justice.laa.payments.amend.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
    "hasRole(T(uk.gov.justice.laa.payments.amend.models.enums.Role).ROLE_CLAIM_AMENDMENTS_CASEWORKER)")
public @interface HasRoleClaimAmendmentsCaseworker {}
