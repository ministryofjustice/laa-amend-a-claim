package uk.gov.justice.laa.amend.claim.models;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.amend.claim.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TableRow {
    private String item;
    private String calculated;
    private String submitted;
    private String assessed;
    private String changeUrl;

    /**
     * Returns the header labels for the table columns.
     *
     * @return list of header labels in the correct order
     */
    public static List<String> getHeaders() {
        return List.of("Item", "Calculated", "Submitted", "Amended", "");
    }

    public static class TableRowBuilder {
        /**
         * Sets the calculated field by formatting the BigDecimal amount as currency.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public TableRowBuilder calculatedAmount(BigDecimal amount) {
            this.calculated = CurrencyUtils.formatCurrency(amount);
            return this;
        }

        /**
         * Sets the calculated field by formatting the Integer count as a plain number.
         *
         * @param count the count to format
         * @return this builder
         */
        public TableRowBuilder calculatedCount(Integer count) {
            this.calculated = count != null ? count.toString() : "0";
            return this;
        }

        /**
         * Sets the submitted field by formatting the BigDecimal amount as currency.
         * Returns "N/A" if amount is null.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public TableRowBuilder submittedAmount(BigDecimal amount) {
            this.submitted = CurrencyUtils.formatCurrency(amount, "N/A");
            return this;
        }

        /**
         * Sets the submitted field by formatting the Integer count as a plain number.
         * Returns "N/A" if count is null.
         *
         * @param count the count to format
         * @return this builder
         */
        public TableRowBuilder submittedCount(Integer count) {
            this.submitted = count != null ? count.toString() : "N/A";
            return this;
        }

        /**
         * Sets the assessed field by formatting the BigDecimal amount as currency.
         *
         * @param amount the amount to format
         * @return this builder
         */
        public TableRowBuilder assessedAmount(BigDecimal amount) {
            this.assessed = CurrencyUtils.formatCurrency(amount);
            return this;
        }

        /**
         * Sets the assessed field by formatting the Integer count as a plain number.
         *
         * @param count the count to format
         * @return this builder
         */
        public TableRowBuilder assessedCount(Integer count) {
            this.assessed = count != null ? count.toString() : "0";
            return this;
        }
    }
}
