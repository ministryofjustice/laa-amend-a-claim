package uk.gov.justice.laa.amend.claim.viewmodels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.expression.Messages;

class ThymeleafMessageTest {

    private Messages messages;

    @BeforeEach
    void setUp() {
        messages = mock(Messages.class);
    }

    @Test
    void constructor_shouldDefensivelyCopyParams() {
        Object[] originalParams = {"one", "two"};

        ThymeleafMessage message = new ThymeleafMessage("key", originalParams);

        originalParams[0] = "changed";

        assertThat(message.getParams()).containsExactly("one", "two");
    }

    @Test
    void constructor_shouldHandleNullParams() {
        ThymeleafMessage message = new ThymeleafMessage("key", (Object[]) null);

        assertThat(message.getParams()).isNotNull();
        assertThat(message.getParams()).isEmpty();
    }

    @Test
    void resolve_shouldResolvePlainStringParams() {
        when(messages.msgWithParams(eq("key"), any())).thenReturn("resolved");

        ThymeleafMessage message = new ThymeleafMessage("key", "param1", "param2");

        String result = message.resolve(messages);

        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(messages).msgWithParams(eq("key"), captor.capture());

        assertThat(captor.getValue()).containsExactly("param1", "param2");

        assertThat(result).isEqualTo("resolved");
    }

    @Test
    void resolve_shouldResolveNestedThymeleafMessages() {
        when(messages.msgWithParams(eq("inner.key"), any())).thenReturn("innerValue");

        when(messages.msgWithParams(eq("outer.key"), any())).thenReturn("outerValue");

        ThymeleafMessage inner = new ThymeleafMessage("inner.key");

        ThymeleafMessage outer = new ThymeleafMessage("outer.key", inner);

        String result = outer.resolve(messages);

        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(messages).msgWithParams(eq("outer.key"), captor.capture());

        assertThat(captor.getValue()).containsExactly("innerValue");

        assertThat(result).isEqualTo("outerValue");
    }

    @Test
    void resolve_shouldHandleMixedParamTypes() {
        when(messages.msgWithParams(eq("inner.key"), any())).thenReturn("inner");

        when(messages.msgWithParams(eq("key"), any())).thenReturn("resolved");

        ThymeleafMessage inner = new ThymeleafMessage("inner.key");

        ThymeleafMessage message = new ThymeleafMessage("key", "text", inner, 42, true, null);

        message.resolve(messages);

        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(messages).msgWithParams(eq("key"), captor.capture());

        assertThat(captor.getValue()).containsExactly("text", "inner", "42", "true", "");
    }
}
