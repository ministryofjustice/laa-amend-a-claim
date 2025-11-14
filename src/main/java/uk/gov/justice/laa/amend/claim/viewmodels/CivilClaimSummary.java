//package uk.gov.justice.laa.amend.claim.viewmodels;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import uk.gov.justice.laa.amend.claim.models.Cost;
//
//import java.util.List;
//
//@EqualsAndHashCode(callSuper = true)
//@Data
//public class CivilClaimSummary extends ClaimSummary {
//    private String matterTypeCodeOne;
//    private String matterTypeCodeTwo;
//    private ClaimFieldRow detentionTravelWaitingCosts;
//    private ClaimFieldRow jrFormFillingCost;
//    private ClaimFieldRow adjournedHearing;
//    private ClaimFieldRow cmrhTelephone;
//    private ClaimFieldRow cmrhOral;
//    private ClaimFieldRow hoInterview;
//    private ClaimFieldRow substantiveHearing;
//    private ClaimFieldRow counselsCost;
//
//    @Override
//    protected void addClaimTypeSpecificRows(List<ClaimFieldRow> rows) {
//        if (counselsCost != null) rows.add(counselsCost);
//        if (detentionTravelWaitingCosts != null) rows.add(detentionTravelWaitingCosts);
//        if (jrFormFillingCost != null) rows.add(jrFormFillingCost);
//        if (adjournedHearing != null) rows.add(adjournedHearing);
//        if (cmrhTelephone != null) rows.add(cmrhTelephone);
//        if (cmrhOral != null) rows.add(cmrhOral);
//        if (hoInterview != null) rows.add(hoInterview);
//        if (substantiveHearing != null) rows.add(substantiveHearing);
//    }
//
//    @Override
//    protected Cost getCostForRow(ClaimFieldRow row) {
//        if (row == null) return null;
//
//        if (row.equals(counselsCost)) return Cost.COUNSEL_COSTS;
//        if (row.equals(detentionTravelWaitingCosts)) return Cost.DETENTION_TRAVEL_AND_WAITING_COSTS;
//        if (row.equals(jrFormFillingCost)) return Cost.JR_FORM_FILLING_COSTS;
//
//        // Fall back to parent implementation
//        return super.getCostForRow(row);
//    }
//}
