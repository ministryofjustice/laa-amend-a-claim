package uk.gov.justice.laa.amend.claim.dto;

public record SearchForm(
    String providerAccountNumber,
    Integer submissionDateMonth,
    Integer submissionDateYear,
    String referenceNumber
) {}
