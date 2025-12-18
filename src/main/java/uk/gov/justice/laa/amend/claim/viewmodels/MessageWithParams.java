package uk.gov.justice.laa.amend.claim.viewmodels;

import org.thymeleaf.expression.Messages;

import java.util.Arrays;

public record MessageWithParams(String key, Object[] params) {

    public MessageWithParams(String key) {
        this(key, new Object[0]);
    }

    public String resolve(Messages messages) {
        return messages.msgWithParams(key, Arrays.stream(params).map(x -> resolve(messages, x)).toArray());
    }

    private String resolve(Messages messages, Object value) {
        return switch (value) {
            case MessageWithParams mwp -> mwp.resolve(messages);
            case String s -> s;
            default -> "";
        };
    }
}
