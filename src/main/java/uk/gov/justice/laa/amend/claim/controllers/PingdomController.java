package uk.gov.justice.laa.amend.claim.controllers;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@RestController
@AllArgsConstructor
public class PingdomController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> healthCheck() {
        if (maintenanceService.maintenanceEnabled()) {
            return ResponseEntity.status(503).body(Map.of("status", "MAINTENANCE"));
        }

        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
