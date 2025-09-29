package uk.gov.justice.laa.amend.claim.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class})
@ActiveProfiles("test")
public class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @TestConfiguration
  static class MockSecurityBeans {

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
      return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    OAuth2AuthorizedClientRepository authorizedClientRepository() {
      return Mockito.mock(OAuth2AuthorizedClientRepository.class);
    }
  }

  @Test
  void actuatorEndpointsArePermittedWithoutAuth() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  void authenticatedEndpointsAccessibleWithValidRole() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk());
  }

  @Test
  void unauthenticatedUsersRedirectToLogin() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void incorrectRoleRedirectsToNotAuthorised() throws Exception {
    mockMvc.perform(get("/admin"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/not-authorised"));
  }
}
