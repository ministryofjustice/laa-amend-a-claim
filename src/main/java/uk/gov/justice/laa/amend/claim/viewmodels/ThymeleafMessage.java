package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Getter;
import org.thymeleaf.expression.Messages;

import java.util.Arrays;

@Getter
public class ThymeleafMessage extends ThymeleafString {

    private final String key;
    private final Object[] params;

    public ThymeleafMessage(String key, Object... params) {
        this.key = key;
        this.params = params == null ? new Object[0] : params.clone();
    }

    @Override
    public String resolve(Messages messages) {
        return messages.msgWithParams(key, Arrays.stream(params).map(param -> resolve(param, messages)).toArray());
    }

    private String resolve(Object value, Messages messages) {
        return switch (value) {
            case ThymeleafMessage mwp -> mwp.resolve(messages);
            case String s -> s;
            default -> "";
        };
    }
}
