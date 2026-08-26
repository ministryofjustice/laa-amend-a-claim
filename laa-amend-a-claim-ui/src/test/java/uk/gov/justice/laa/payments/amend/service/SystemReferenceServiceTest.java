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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
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

  @Test
  void getAmendmentRequestedByOptionsSortsByDisplayLabelCaseInsensitively() {
    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(
        List.of(
            createProvider("C", "zebra"),
            createProvider("A", "Apple"),
            createProvider("B", "mango")));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result.values()).containsExactly("Apple", "mango", "zebra");
  }

  @Test
  void getAmendmentRequestedByOptionsReturnsEmptyMapForNullReferenceList() {
    Map<String, String> result = systemReferenceService.getAmendmentRequestedByOptions(null);

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestedByOptionsReturnsEmptyMapForNullRequestedByList() {
    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(null);

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestedByOptionsFiltersOutNullEntries() {
    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(
        java.util.Arrays.asList(createProvider("A", "Alpha"), null, createProvider("B", "Beta")));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result).hasSize(2).containsEntry("A", "Alpha").containsEntry("B", "Beta");
  }

  @Test
  void getAmendmentRequestedByOptionsFiltersOutEntriesWithNullCode() {
    var noCode = new AmendmentRequestedByReference();
    noCode.setCode(null);
    noCode.setDisplayLabel("No Code");

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(noCode, createProvider("A", "Alpha")));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result).hasSize(1).containsEntry("A", "Alpha");
  }

  @Test
  void getAmendmentRequestedByOptionsFiltersOutEntriesWithNullDisplayLabel() {
    var noLabel = new AmendmentRequestedByReference();
    noLabel.setCode("NO_LABEL");
    noLabel.setDisplayLabel(null);

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(noLabel, createProvider("A", "Alpha")));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result).hasSize(1).containsEntry("A", "Alpha");
  }

  @Test
  void getAmendmentRequestedByOptionsHandlesDuplicateCodesKeepingFirst() {
    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(
        List.of(
            createProvider("DUP", "First Label"),
            createProvider("DUP", "Second Label"),
            createProvider("OTHER", "Other")));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestedByOptions(referenceList);

    assertThat(result).hasSize(2).containsEntry("DUP", "First Label");
  }

  @Test
  void getAmendmentRequestReasonWithReferenceListSortsByDisplayLabelCaseInsensitively() {
    var provider = createProvider("PROVIDER", "Provider");
    provider.setReasons(
        List.of(
            createReason("C", "zebra"), createReason("A", "Apple"), createReason("B", "mango")));

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(provider));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestReason("PROVIDER", referenceList);

    assertThat(result.values()).containsExactly("Apple", "mango", "zebra");
  }

  @Test
  void getAmendmentRequestReasonWithNullReferenceListReturnsEmptyMap() {
    Map<String, String> result = systemReferenceService.getAmendmentRequestReason("PROVIDER", null);

    assertThat(result).isEmpty();
  }

  @Test
  void getAmendmentRequestReasonWithReferenceListFiltersOutNullReasons() {
    var provider = createProvider("PROVIDER", "Provider");
    provider.setReasons(
        java.util.Arrays.asList(createReason("R1", "First"), null, createReason("R2", "Second")));

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(provider));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestReason("PROVIDER", referenceList);

    assertThat(result).hasSize(2).containsEntry("R1", "First").containsEntry("R2", "Second");
  }

  @Test
  void getAmendmentRequestReasonWithReferenceListHandlesDuplicateCodesKeepingFirst() {
    var provider = createProvider("PROVIDER", "Provider");
    provider.setReasons(
        List.of(
            createReason("DUP", "First Label"),
            createReason("DUP", "Second Label"),
            createReason("OTHER", "Other")));

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(provider));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestReason("PROVIDER", referenceList);

    assertThat(result).hasSize(2).containsEntry("DUP", "First Label");
  }

  @Test
  void getAmendmentRequestReasonUsesProvidedReferenceList() {
    var reason1 = createReason("R1", "Reason One");
    var reason2 = createReason("R2", "Reason Two");

    var provider = createProvider("PROVIDER", "Provider");
    provider.setReasons(List.of(reason1, reason2));

    var referenceList = new AmendmentRequestedByReferenceList();
    referenceList.setRequestedBy(List.of(provider));

    Map<String, String> result =
        systemReferenceService.getAmendmentRequestReason("PROVIDER", referenceList);

    assertThat(result)
        .hasSize(2)
        .containsEntry("R1", "Reason One")
        .containsEntry("R2", "Reason Two");
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

  private AmendmentRequestedByReference createProvider(String code, String displayLabel) {
    var provider = new AmendmentRequestedByReference();
    provider.setCode(code);
    provider.setDisplayLabel(displayLabel);
    return provider;
  }
}
