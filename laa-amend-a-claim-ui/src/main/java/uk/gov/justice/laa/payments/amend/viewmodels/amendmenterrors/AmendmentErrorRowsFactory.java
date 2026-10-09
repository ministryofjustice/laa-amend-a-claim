package uk.gov.justice.laa.payments.amend.viewmodels.amendmenterrors;

import static uk.gov.justice.laa.payments.amend.utils.MatterTypeUtils.MATTER_TYPE_CODE;
import static uk.gov.justice.laa.payments.amend.utils.MatterTypeUtils.MATTER_TYPE_CODE_1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.payments.amend.models.AmendmentError;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@UtilityClass
public class AmendmentErrorRowsFactory {

  private static final ThymeleafString SUBMISSION =
      new ThymeleafMessage("amendments.cannotBeSubmitted.submission");

  public static List<AmendmentErrorRow> create(ClaimDetails claim, List<AmendmentError> errors) {
    var fields = fieldsInTabOrder(claim);
    return errors.stream()
        .sorted(Comparator.comparingInt(error -> tabPosition(fields, error)))
        .map(error -> toRow(fields, error))
        .toList();
  }

  private static List<ClaimViewField<?>> fieldsInTabOrder(ClaimDetails claim) {
    var clientView = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    List<ClaimViewField<?>> fields = new ArrayList<>();
    fields.addAll(clientView.client1Rows().keySet());
    fields.addAll(clientView.client2Rows().keySet());
    fields.addAll(caseView.caseTypeRows().keySet());
    fields.addAll(caseView.caseDetailsRows().keySet());
    fields.addAll(ClaimCostsViewFactory.create(claim).costFields().keySet());
    return fields;
  }

  private static int tabPosition(List<ClaimViewField<?>> fields, AmendmentError error) {
    return findField(fields, error).map(fields::indexOf).orElse(Integer.MAX_VALUE);
  }

  private static AmendmentErrorRow toRow(List<ClaimViewField<?>> fields, AmendmentError error) {
    var item = findField(fields, error).map(field -> label(field, error)).orElse(SUBMISSION);
    return new AmendmentErrorRow(item, error.getMessage());
  }

  private static Optional<ClaimViewField<?>> findField(
      List<ClaimViewField<?>> fields, AmendmentError error) {
    if (error.getFieldName() == null) {
      return Optional.empty();
    }
    var fieldName = isWholeMatterType(error) ? MATTER_TYPE_CODE_1 : error.getFieldName();
    return fields.stream()
        .filter(field -> field.getAmendedFieldIdentifiers().contains(fieldName))
        .findFirst();
  }

  private static ThymeleafString label(ClaimViewField<?> field, AmendmentError error) {
    var key = isWholeMatterType(error) ? "MATTER_TYPE_CODE" : field.name();
    return new ThymeleafMessage("claimField." + key);
  }

  private static boolean isWholeMatterType(AmendmentError error) {
    return MATTER_TYPE_CODE.equals(error.getFieldName());
  }
}
