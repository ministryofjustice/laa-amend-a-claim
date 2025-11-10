package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.context.MessageSource;
import uk.gov.justice.laa.amend.claim.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ClaimValuesTableRow {
    String item;
    String calculated;
    String submitted;
    String assessed;
    @Getter
    String changeUrl;
    @Builder.Default boolean totalRow = false;

    private static final String NOT_APPLICABLE = "claimValuesTable.notApplicable";

    MessageSource messageSource;

    /**
     * Returns the header labels for the table columns.
     *
     * @return list of header labels in the correct order
     */
    public static List<String> getHeaders() {
        return List.of("claimValuesTable.item", "claimValuesTable.calculated", "claimValuesTable.submitted", "claimValuesTable.amended", "claimValuesTable.emptyHeader");
    }


    @SuppressWarnings("unused")
    public static class ClaimValuesTableRowBuilder {
        /**
         * Sets the calculated field by formatting the BigDecimal amount as currency.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder calculatedAmount(BigDecimal amount) {
            this.calculated = amount != null ? CurrencyUtils.formatCurrency(amount) : null;
            return this;
        }

        /**
         * Sets the calculated field by formatting the Integer count as a plain number.
         *
         * @param count the count to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder calculatedCount(Integer count) {
            this.calculated = count != null ? count.toString() : null;
            return this;
        }

        /**
         * Sets the submitted field by formatting the BigDecimal amount as currency.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder submittedAmount(BigDecimal amount) {
            this.submitted = amount != null ? CurrencyUtils.formatCurrency(amount) : null;
            return this;
        }

        /**
         * Sets the submitted field by formatting the Integer count as a plain number.
         *
         * @param count the count to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder submittedCount(Integer count) {
            this.submitted = count != null ? count.toString() : null;
            return this;
        }

        /**
         * Sets the assessed field by formatting the BigDecimal amount as currency.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder assessedAmount(BigDecimal amount) {
            this.assessed = amount != null ? CurrencyUtils.formatCurrency(amount) : null;
            return this;
        }

        /**
         * Sets the assessed field by formatting the Integer count as a plain number.
         *
         * @param count the count to format
         * @return this builder
         */
        public ClaimValuesTableRowBuilder assessedCount(Integer count) {
            this.assessed = count != null ? count.toString() : null;
            return this;
        }

        /**
         * Sets the calculated field to Yes or No based on the boolean value.
         *
         * @param value the boolean value
         * @return this builder
         */
        public ClaimValuesTableRowBuilder calculatedYesNo(Boolean value) {
            this.calculated = value != null ? (value ? "Yes" : "No") : null;
            return this;
        }

        /**
         * Sets the submitted field to Yes or No based on the boolean value.
         *
         * @param value the boolean value
         * @return this builder
         */
        public ClaimValuesTableRowBuilder submittedYesNo(Boolean value) {
            this.submitted = value != null ? (value ? "Yes" : "No") : null;
            return this;
        }

        /**
         * Sets the assessed field to Yes or No based on the boolean value.
         *
         * @param value the boolean value
         * @return this builder
         */
        public ClaimValuesTableRowBuilder assessedYesNo(Boolean value) {
            this.assessed = value != null ? (value ? "Yes" : "No") : null;
            return this;
        }

        /**
         * Sets the calculated field to "N/A" (not applicable).
         *
         * @return this builder
         */
        public ClaimValuesTableRowBuilder calculatedNA() {
            this.calculated = NOT_APPLICABLE;
            return this;
        }

        /**
         * Sets the submitted field to "N/A" (not applicable).
         *
         * @return this builder
         */
        public ClaimValuesTableRowBuilder submittedNA() {
            this.submitted = NOT_APPLICABLE;
            return this;
        }

        /**
         * Sets the assessed field to "N/A" (not applicable).
         *
         * @return this builder
         */
        public ClaimValuesTableRowBuilder assessedNA() {
            this.assessed = NOT_APPLICABLE;
            return this;
        }
    }
}
