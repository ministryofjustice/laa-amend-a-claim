package uk.gov.justice.laa.payments.amend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;

class SystemReferenceServiceTest {

  @Mock private ClaimsApiClient claimsApiClient;

  private SystemReferenceService systemReferenceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    systemReferenceService = spy(new SystemReferenceService(claimsApiClient));
  }

  @Test
  void getAmendmentRequestReasonReturnsCodeToLabelMap() {
    setupAmendmentReasons(
        "PROVIDER",
        createReason("REASON1", "First Reason"),
        createReason("REASON2", "Second Reason"));

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result)
        .hasSize(2)
        .containsEntry("REASON1", "First Reason")
        .containsEntry("REASON2", "Second Reason");
  }

  @Test
  void getAmendmentRequestReasonReturnsEmptyMapForNullReasons() {
    doReturn(null).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestReasonReturnsEmptyMapForEmptyReasons() {
    doReturn(Collections.emptyList())
        .when(systemReferenceService)
        .getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestReasonFiltersOutNullReasons() {
    var reasons =
        java.util.Arrays.asList(
            createReason("REASON1", "First Reason"),
            null,
            createReason("REASON2", "Second Reason"));
    doReturn(reasons).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result)
        .hasSize(2)
        .containsEntry("REASON1", "First Reason")
        .containsEntry("REASON2", "Second Reason");
  }

  @Test
  void getAmendmentRequestReasonFiltersOutReasonsWithNullCode() {
    var reason = new AmendmentReasonReference();
    reason.setCode(null);
    reason.setDisplayLabel("Reason Label");

    doReturn(List.of(reason)).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestReasonFiltersOutReasonsWithNullDisplayLabel() {
    var reason = new AmendmentReasonReference();
    reason.setCode("REASON1");
    reason.setDisplayLabel(null);

    doReturn(List.of(reason)).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestReasonHandlesDuplicateCodesKeepingFirst() {
    var reasons =
        List.of(
            createReason("REASON1", "First Label"),
            createReason("REASON1", "Second Label"),
            createReason("REASON2", "Another Reason"));
    doReturn(reasons).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result).hasSize(2).containsEntry("REASON1", "First Label");
  }

  @Test
  void getAmendmentRequestReasonSortsByDisplayLabelAlphabetically() {
    var reasons =
        List.of(
            createReason("REASON_B", "Zulu"),
            createReason("REASON_A", "Alpha"),
            createReason("REASON_C", "Mike"));
    doReturn(reasons).when(systemReferenceService).getAmendmentReasonByProvider("PROVIDER");

    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER");

    assertThat(result.values()).containsExactly("Alpha", "Mike", "Zulu");
  }

  private void setupAmendmentReasons(String requestedBy, AmendmentReasonReference... reasons) {
    doReturn(List.of(reasons))
        .when(systemReferenceService)
        .getAmendmentReasonByProvider(requestedBy);
  }

  private AmendmentReasonReference createReason(String code, String displayLabel) {
    var reason = new AmendmentReasonReference();
    reason.setCode(code);
    reason.setDisplayLabel(displayLabel);
    return reason;
  }
}
