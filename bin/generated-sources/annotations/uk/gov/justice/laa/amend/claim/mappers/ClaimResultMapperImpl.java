package uk.gov.justice.laa.amend.claim.mappers;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.BoltOnPatch;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-15T13:44:07+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ClaimResultMapperImpl implements ClaimResultMapper {

    @Override
    public SearchResultViewModel toDto(ClaimResultSet claimResultSet, String href) {
        if ( claimResultSet == null ) {
            return null;
        }

        SearchResultViewModel searchResultViewModel = new SearchResultViewModel();

        searchResultViewModel.setPagination( toPagination( claimResultSet, href ) );
        searchResultViewModel.setClaims( map( claimResultSet.getContent() ) );

        return searchResultViewModel;
    }

    @Override
    public Claim mapToClaim(ClaimResponse claimResponse) {
        if ( claimResponse == null ) {
            return null;
        }

        Claim claim = new Claim();

        claim.setUniqueFileNumber( getAccountNumber( claimResponse.getUniqueFileNumber() ) );
        claim.setCaseReferenceNumber( getAccountNumber( claimResponse.getCaseReferenceNumber() ) );
        claim.setClientSurname( getAccountNumber( claimResponse.getClientSurname() ) );
        if ( claimResponse.getCaseStartDate() != null ) {
            claim.setDateSubmitted( LocalDate.parse( claimResponse.getCaseStartDate() ) );
        }
        claim.setEscaped( claimResponseFeeCalculationResponseBoltOnDetailsEscapeCaseFlag( claimResponse ) );

        setExtraFields( claimResponse, claim );

        return claim;
    }

    private Boolean claimResponseFeeCalculationResponseBoltOnDetailsEscapeCaseFlag(ClaimResponse claimResponse) {
        FeeCalculationPatch feeCalculationResponse = claimResponse.getFeeCalculationResponse();
        if ( feeCalculationResponse == null ) {
            return null;
        }
        BoltOnPatch boltOnDetails = feeCalculationResponse.getBoltOnDetails();
        if ( boltOnDetails == null ) {
            return null;
        }
        return boltOnDetails.getEscapeCaseFlag();
    }
}
