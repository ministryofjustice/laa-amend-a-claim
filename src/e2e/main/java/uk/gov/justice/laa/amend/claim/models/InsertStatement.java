package uk.gov.justice.laa.amend.claim.models;

import java.util.List;

public record InsertStatement (String table, List<String> columns, List<String> values) {}
