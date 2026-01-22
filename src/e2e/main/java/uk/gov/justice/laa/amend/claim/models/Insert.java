package uk.gov.justice.laa.amend.claim.models;

import java.util.List;

public interface Insert {

    String table();

    String id();

    List<Object> parameters();
}
