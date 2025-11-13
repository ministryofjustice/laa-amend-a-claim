package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_DATE_FORMAT;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PERIOD_FORMAT;
import static uk.gov.justice.laa.amend.claim.utils.FormUtils.getClientFullName;

@Data
public class Claim implements Serializable {
    private String uniqueFileNumber;
    private String submissionId;
    private String claimId;
    private String caseReferenceNumber;
    private String clientSurname;
    private String clientForename;

    @JsonSerialize(using = YearMonthSerializer.class)
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth submissionPeriod;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate caseStartDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate caseEndDate;

    private String feeScheme;
    private String categoryOfLaw;
    private String matterTypeCode;
    private String scheduleReference;
    private Boolean escaped;
    private String providerAccountNumber;

    @JsonIgnore
    public String getAccountNumber() {
        return scheduleReference != null ? scheduleReference.split("/")[0] : null;
    }

    @JsonIgnore
    public String getCaseStartDateForDisplay() {
        return caseStartDate != null ? caseStartDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    @JsonIgnore
    public String getCaseEndDateForDisplay() {
        return caseEndDate != null ? caseEndDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    @JsonIgnore
    public String getSubmissionPeriodForDisplay() {
        return submissionPeriod != null ? submissionPeriod.format(DateTimeFormatter.ofPattern(DEFAULT_PERIOD_FORMAT)) : null;
    }

    @JsonIgnore
    public long getSubmissionPeriodForSorting() {
        return submissionPeriod != null ? submissionPeriod.atDay(1).toEpochDay() : 0;
    }

    @JsonIgnore
    public String getEscapedForDisplay() {
        return (escaped != null && escaped) ? "index.results.escaped.yes" : "index.results.escaped.no";
    }

    @JsonIgnore
    public String getClientName() {
        return getClientFullName(clientForename, clientSurname);
    }

    public void parseAndSetSubmissionPeriod(String submissionPeriod) {
        if (submissionPeriod != null) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("MMM-yyyy").toFormatter(Locale.ENGLISH);
                this.submissionPeriod = YearMonth.parse(submissionPeriod, formatter);
            } catch (DateTimeParseException e) {
                this.submissionPeriod = null;
            }
        } else {
            this.submissionPeriod = null;
        }
    }
}
