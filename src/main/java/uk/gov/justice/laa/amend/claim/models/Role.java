package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public enum Role {
    ROLE_CLAIM_AMENDMENTS_CASEWORKER("Amend a Claim - Claim Amendments Caseworker"),
    ROLE_ESCAPE_CASE_CASEWORKER("Amend a Claim - Escape Case Caseworker");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Optional<Role> fromRoleName(String roleName) {
        return Arrays.stream(values())
                .filter(sortField -> sortField.roleName.equals(roleName))
                .findFirst();
    }

    public static Set<Role> allRolesApartFrom(Role... roles) {
        var excluded = EnumSet.copyOf(Arrays.asList(roles));
        return EnumSet.complementOf(excluded);
    }
}
