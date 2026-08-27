package uk.gov.justice.laa.payments.amend.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;

@AllArgsConstructor
@Service
@Slf4j
public class SystemReferenceService {

  private final ClaimsApiClient claimsApiClient;

  public AmendmentRequestedByReferenceList getAmendmentRequestedByReferenceList() {
    try {
      return claimsApiClient.getAmendmentRequestedByReferenceList().block();
    } catch (Exception e) {
      log.error("Error getting amendment requested by reference list", e);
      throw e;
    }
  }

  public List<AmendmentReasonReference> getAmendmentReasonByProvider(String providerCode) {
    return getAmendmentReasonByProvider(providerCode, getAmendmentRequestedByReferenceList());
  }

  public List<AmendmentReasonReference> getAmendmentReasonByProvider(
      String providerCode, AmendmentRequestedByReferenceList referenceList) {
    return Optional.ofNullable(referenceList)
        .map(AmendmentRequestedByReferenceList::getRequestedBy)
        .orElse(Collections.emptyList())
        .stream()
        .filter(
            requestedBy ->
                requestedBy != null
                    && providerCode != null
                    && providerCode.equalsIgnoreCase(requestedBy.getCode()))
        .findFirst()
        .map(
            requestedBy ->
                Optional.ofNullable(requestedBy.getReasons()).orElse(Collections.emptyList()))
        .orElse(Collections.emptyList());
  }

  public Map<String, String> getAmendmentRequestedByOptions() {
    return getAmendmentRequestedByOptions(getAmendmentRequestedByReferenceList());
  }

  public Map<String, String> getAmendmentRequestedByOptions(
      AmendmentRequestedByReferenceList referenceList) {
    return Optional.ofNullable(referenceList)
        .map(AmendmentRequestedByReferenceList::getRequestedBy)
        .orElse(Collections.emptyList())
        .stream()
        .filter(item -> item != null && item.getCode() != null && item.getDisplayLabel() != null)
        .collect(
            Collectors.toMap(
                AmendmentRequestedByReference::getCode,
                AmendmentRequestedByReference::getDisplayLabel,
                (existing, replacement) -> existing,
                LinkedHashMap::new))
        .entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(String.CASE_INSENSITIVE_ORDER))
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  public Map<String, String> getAmendmentRequestReason(String requestedBy) {
    return toAmendmentReasonMap(getAmendmentReasonByProvider(requestedBy));
  }

  public Map<String, String> getAmendmentRequestReason(
      String requestedBy, AmendmentRequestedByReferenceList referenceList) {
    return toAmendmentReasonMap(getAmendmentReasonByProvider(requestedBy, referenceList));
  }

  private Map<String, String> toAmendmentReasonMap(
      List<AmendmentReasonReference> amendmentReasons) {
    Map<String, String> codeToLabelMap = new LinkedHashMap<>();
    if (amendmentReasons != null && !amendmentReasons.isEmpty()) {
      codeToLabelMap =
          amendmentReasons.stream()
              .filter(
                  item -> item != null && item.getCode() != null && item.getDisplayLabel() != null)
              .collect(
                  Collectors.toMap(
                      AmendmentReasonReference::getCode,
                      AmendmentReasonReference::getDisplayLabel,
                      (existing, replacement) -> existing,
                      LinkedHashMap::new));
      codeToLabelMap =
          codeToLabelMap.entrySet().stream()
              .sorted(Map.Entry.comparingByValue(String.CASE_INSENSITIVE_ORDER))
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      Map.Entry::getValue,
                      (existing, replacement) -> existing,
                      LinkedHashMap::new));
    }
    return codeToLabelMap;
  }
}
