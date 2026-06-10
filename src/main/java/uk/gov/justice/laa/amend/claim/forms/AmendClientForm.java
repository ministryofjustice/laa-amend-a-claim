package uk.gov.justice.laa.amend.claim.forms;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientView;

@Setter
@Getter
public class AmendClientForm implements Serializable {

  private Map<String, String> inputs;

  public AmendClientForm() {
    this.inputs = new LinkedHashMap<>();
  }

  public AmendClientForm(ClaimClientView view) {
    this.inputs =
        view.client1Rows().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> String.valueOf(entry.getValue()),
                    (a, b) -> b,
                    LinkedHashMap::new));
  }
}
