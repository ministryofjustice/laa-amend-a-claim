package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;


/**
 * Represents the assessment status of a claim field.
 * <p>
 * This enum defines the possible states for claim field assessment:
 * <ul>
 *   <li>{@link #MODIFIABLE} - The claim field with status can be modified.</li>
 *   <li>{@link #NOT_MODIFIABLE} - The claim field with status cannot be modified.</li>
 *   <li>{@link #DO_NOT_DISPLAY} - The claim field with status should not be shown in the UI.</li>
 * </ul>
 */

@Getter
public enum ClaimFieldStatus {
    MODIFIABLE,
    NOT_MODIFIABLE,
    DO_NOT_DISPLAY
}
