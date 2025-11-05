package uk.gov.justice.laa.amend.claim.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CacheRepository {

    private HttpSession session;

    public <T> T get(String key, Class<T> clazz) {
        Object value = session.getAttribute(key);
        if (value == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to parse value as %s", clazz.getSimpleName()), e);
        }
    }

    public void set(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }
}
