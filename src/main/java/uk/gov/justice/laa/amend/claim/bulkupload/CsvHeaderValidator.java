package uk.gov.justice.laa.amend.claim.bulkupload;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvHeaderValidator {
    public void validate(CsvSchema schema, List<String> actualHeaders) {

        List<String> missing = new ArrayList<>();

        for (CsvField field : schema.fields()) {
            if (field.required()) {
                boolean found = actualHeaders.stream()
                        .anyMatch(h -> h.equalsIgnoreCase(field.displayName()) || h.equalsIgnoreCase(field.key()));

                if (!found) {
                    missing.add(field.displayName());
                }
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required headers: " + String.join(", ", missing));
        }
    }
}
