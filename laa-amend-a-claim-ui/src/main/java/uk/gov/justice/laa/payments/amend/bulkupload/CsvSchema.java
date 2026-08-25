package uk.gov.justice.laa.payments.amend.bulkupload;

import java.util.List;

public record CsvSchema(List<CsvField> fields) {}
