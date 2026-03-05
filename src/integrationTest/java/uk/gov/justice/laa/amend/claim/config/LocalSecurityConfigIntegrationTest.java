package uk.gov.justice.laa.amend.claim.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.amend.claim.config.security.SecurityConstants.PERMISSIONS_POLICY;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.base.RedisSetup;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class LocalSecurityConfigIntegrationTest extends RedisSetup {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/js/app.js", "/css/styles.css"})
    void responseOnGetHasCorrectHeaders(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin"))
                .andExpect(header().string("Cross-Origin-Embedder-Policy", "require-corp"))
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Permissions-Policy", PERMISSIONS_POLICY));
    }

    @Test
    void responseOnPostHasCorrectHeaders() throws Exception {
        mockMvc.perform(post("/").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Cross-Origin-Opener-Policy", "same-origin"))
                .andExpect(header().string("Cross-Origin-Embedder-Policy", "require-corp"))
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Permissions-Policy", PERMISSIONS_POLICY));
    }
}
