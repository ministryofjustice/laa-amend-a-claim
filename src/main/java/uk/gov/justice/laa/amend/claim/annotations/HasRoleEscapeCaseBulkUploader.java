package uk.gov.justice.laa.amend.claim.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
    "hasRole(T(uk.gov.justice.laa.amend.claim.models.Role).ROLE_ESCAPE_CASE_BULK_UPLOADER)")
public @interface HasRoleEscapeCaseBulkUploader {}
