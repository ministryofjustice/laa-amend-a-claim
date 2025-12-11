package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;


/**
 * Represents the assessment status of a claim.
 * <p>
 * This enum defines the possible states for claim assessment:
 * <ul>
 *   <li>{@link #ASSESSABLE} - The claim can be assessed. Status helps to display a Change link on UI.</li>
 *   <li>{@link #NOT_ASSESSABLE} - The claim cannot be assessed. Helps to display a Change/Add link on UI.</li>
 *   <li>{@link #NEEDS_ASSESSING} - The claim requires assessment. Helps to display an Add link on UI</li>
 *   <li>{@link #DO_NOT_DISPLAY} - The status should not be shown in the UI.</li>
 * </ul>
 */

@Getter
public enum AssessStatus {
    ASSESSABLE,
    NOT_ASSESSABLE,
    NEEDS_ASSESSING,
    DO_NOT_DISPLAY
}
