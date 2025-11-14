//package uk.gov.justice.laa.amend.claim.viewmodels;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import uk.gov.justice.laa.amend.claim.utils.CurrencyUtils;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.math.BigDecimal;
//
//@AllArgsConstructor
//@Data
//public class ClaimFieldRow implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//    private String label;
//    private Object submitted;
//    private Object calculated;
//    private Object amended;
//
//    /**
//     * Returns a formatted string representation of the submitted value.
//     *
//     * @return formatted submitted value, or null if the value is null
//     */
//    public String getFormattedSubmitted() {
//        return formatValue(submitted);
//    }
//
//    /**
//     * Returns a formatted string representation of the calculated value.
//     *
//     * @return formatted calculated value, or null if the value is null
//     */
//    public String getFormattedCalculated() {
//        return formatValue(calculated);
//    }
//
//    /**
//     * Returns a formatted string representation of the amended/assessed value.
//     *
//     * @return formatted amended value, or null if the value is null
//     */
//    public String getFormattedAmended() {
//        return formatValue(amended);
//    }
//
//    /**
//     * Formats a value based on its type.
//     *
//     * @param value the value to format
//     * @return formatted string, or null if value is null
//     */
//    private String formatValue(Object value) {
//        return switch (value) {
//            case null -> null;
//            case BigDecimal bigDecimal -> CurrencyUtils.formatCurrency(bigDecimal);
//            case Integer i -> i.toString();
//            case Boolean b -> b ? "Yes" : "No";
//            case String s -> s;
//            default -> value.toString();
//        };
//
//    }
//}
