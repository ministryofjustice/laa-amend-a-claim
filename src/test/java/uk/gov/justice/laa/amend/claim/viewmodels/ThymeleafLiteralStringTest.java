package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.expression.Messages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class ThymeleafLiteralStringTest {

    private Messages messages;

    @BeforeEach
    void setUp() {
        messages = mock(Messages.class);
    }

    @Test
    void resolve_shouldReturnValue() {
        ThymeleafLiteralString tls =
            new ThymeleafLiteralString("value");

        String result = tls.resolve(messages);

        assertThat(result).isEqualTo("value");

        verifyNoInteractions(messages);
    }
}
