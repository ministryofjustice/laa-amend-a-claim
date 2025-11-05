package uk.gov.justice.laa.amend.claim.repositories;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class CacheRepositoryIntegrationTest {

    @Autowired
    private HttpSession session;

    private CacheRepository cacheRepository;

    @BeforeEach
    void setUp() {
        this.session = new MockHttpSession();
        cacheRepository = new CacheRepository(session);
    }

    @Test
    void testSetAndGetStringValue() {
        String key = "key";
        String value = "value";

        session.setAttribute(key, value);

        String result = cacheRepository.get(key, String.class);
        assertEquals(value, result);
    }

    @Test
    void testGetNonExistentKeyReturnsNull() {
        assertNull(cacheRepository.get("key", String.class));
    }

    @Test
    void testDeleteRemovesKey() {
        String key = "key";
        String value = "value";

        session.setAttribute(key, value);
        assertNotNull(session.getAttribute(key));

        cacheRepository.delete(key);
        assertNull(session.getAttribute(key));
    }

    @Test
    void testGetWithWrongTypeThrowsException() {
        String key = "key";
        String value = "value";

        session.setAttribute(key, value);
        assertThrows(RuntimeException.class, () -> cacheRepository.get(key, Integer.class));
    }
}
