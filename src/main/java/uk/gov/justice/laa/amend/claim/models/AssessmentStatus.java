package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;


/**
 * Represents the assessment status of a claim field.
 * <p>
 * This enum defines the possible states for claim field assessment:
 * <ul>
 *   <li>{@link #ASSESSABLE} - The claim field with status can be assessed.</li>
 *   <li>{@link #NOT_ASSESSABLE} - The claim field with status cannot be assessed.</li>
 *   <li>{@link #DO_NOT_DISPLAY} - The claim field with status should not be shown in the UI.</li>
 * </ul>
 */

@Getter
public enum AssessmentStatus {
    ASSESSABLE,
    NOT_ASSESSABLE,
    DO_NOT_DISPLAY
}
