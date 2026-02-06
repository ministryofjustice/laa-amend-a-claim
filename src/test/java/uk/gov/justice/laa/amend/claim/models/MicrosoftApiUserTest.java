package uk.gov.justice.laa.amend.claim.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MicrosoftApiUserTest {

    @Nested
    class GetNameTests {

        @Test
        void whenGivenNameAndSurnameAreNull() {
            MicrosoftApiUser user = new MicrosoftApiUser("test-id", "Bloggs, Joe", null, null);
            String result = user.getName();
            Assertions.assertEquals("Bloggs, Joe", result);
        }

        @Test
        void whenGivenNameAndSurnameAreNotNull() {
            MicrosoftApiUser user = new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs");
            String result = user.getName();
            Assertions.assertEquals("Joe Bloggs", result);
        }

        @Test
        void whenAllValuesAreNull() {
            MicrosoftApiUser user = new MicrosoftApiUser("test-id", null, null, null);
            String result = user.getName();
            Assertions.assertNull(result);
        }
    }
}
