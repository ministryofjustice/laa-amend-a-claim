package uk.gov.justice.laa.payments.amend.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
    "hasRole(T(enums.models.uk.gov.justice.laa.payments.amend.Role).ROLE_ESCAPE_CASE_BULK_UPLOADER)")
public @interface HasRoleEscapeCaseBulkUploader {}
