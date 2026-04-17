package uk.gov.justice.laa.amend.claim.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.FixedFeeClaimField;
import uk.gov.justice.laa.amend.claim.models.VatLiabilityClaimField;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

class SessionConfigTest {

  private final RedisSerializer<Object> serializer =
      new SessionConfig().springSessionDefaultRedisSerializer();

  @Test
  void roundTripsUsernamePasswordAuthenticationToken() {
    var token =
        new UsernamePasswordAuthenticationToken(
            "user", "credentials", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContext context = new SecurityContextImpl(token);

    byte[] serialized = serializer.serialize(context);
    Object deserialized = serializer.deserialize(serialized);

    assertThat(deserialized).isInstanceOf(SecurityContext.class);
  }

  @Test
  void roundTripsCivilClaimDetails() {
    CivilClaimDetails claim = new CivilClaimDetails();
    claim.setSubmissionId(UUID.randomUUID());
    claim.setClaimId(UUID.randomUUID());
    claim.setClaimSummaryFeeId(UUID.randomUUID());
    claim.setUniqueFileNumber("121019/001");
    claim.setCaseReferenceNumber("CRN-1");
    claim.setSubmissionPeriod(YearMonth.of(2024, 12));
    claim.setCaseStartDate(LocalDate.of(2024, 1, 1));
    claim.setCaseEndDate(LocalDate.of(2024, 6, 1));
    claim.setAreaOfLaw(AreaOfLaw.LEGAL_HELP);
    claim.setVatApplicable(true);
    claim.setStatus(ClaimStatus.VALID);
    claim.setSubmittedDate(OffsetDateTime.now());
    claim.setNetProfitCost(
        new CostClaimField(
            "netProfitCost", new BigDecimal("100.00"), new BigDecimal("90.00"), Cost.PROFIT_COSTS));
    claim.setNetDisbursementAmount(
        new CostClaimField(
            "netDisbursementAmount",
            new BigDecimal("50.00"),
            new BigDecimal("45.00"),
            Cost.DISBURSEMENTS));
    claim.setFixedFee(new FixedFeeClaimField(new BigDecimal("200.00")));
    claim.setVatClaimed(
        new VatLiabilityClaimField(new BigDecimal("20.00"), new BigDecimal("18.00")));
    claim.setAssessedTotalVat(new AssessedClaimField("assessedTotalVat"));
    claim.setAssessedTotalInclVat(new AssessedClaimField("assessedTotalInclVat"));
    claim.setAllowedTotalVat(new AssessedClaimField("allowedTotalVat"));
    claim.setAllowedTotalInclVat(new AssessedClaimField("allowedTotalInclVat"));

    byte[] serialized = serializer.serialize(claim);
    Object deserialized = serializer.deserialize(serialized);

    assertThat(deserialized).isInstanceOf(CivilClaimDetails.class);
    CivilClaimDetails restored = (CivilClaimDetails) deserialized;
    assertThat(restored).isNotNull();
    assertThat(restored.getClaimId()).isEqualTo(claim.getClaimId());
    assertThat(restored.getUniqueFileNumber()).isEqualTo("121019/001");
    assertThat(restored.getNetProfitCost()).isInstanceOf(CostClaimField.class);
    assertThat(restored.getFixedFee()).isInstanceOf(FixedFeeClaimField.class);
    assertThat(restored.getNetProfitCost().getSubmitted()).isInstanceOf(BigDecimal.class);
    assertThat(restored.getNetProfitCost().getCalculated()).isInstanceOf(BigDecimal.class);
    assertThat(restored.getNetProfitCost().getAssessed()).isInstanceOf(BigDecimal.class);
    assertThat(restored.getFixedFee().getCalculated()).isInstanceOf(BigDecimal.class);
  }

  @Test
  void roundTripsUuidAsObject() {
    UUID original = UUID.randomUUID();

    byte[] serialized = serializer.serialize(original);
    Object deserialized = serializer.deserialize(serialized);

    assertThat(deserialized).isInstanceOf(UUID.class);
    assertThat(deserialized).isEqualTo(original);
  }

  @Test
  void roundTripsCrimeClaimDetails() {
    CrimeClaimDetails claim = new CrimeClaimDetails();
    claim.setSubmissionId(UUID.randomUUID());
    claim.setClaimId(UUID.randomUUID());
    claim.setUniqueFileNumber("111018/001");
    claim.setAreaOfLaw(AreaOfLaw.CRIME_LOWER);
    claim.setVatApplicable(true);
    claim.setStatus(ClaimStatus.VALID);

    byte[] serialized = serializer.serialize(claim);
    Object deserialized = serializer.deserialize(serialized);

    assertThat(deserialized).isInstanceOf(CrimeClaimDetails.class);
  }

  @Test
  void roundTripsOauth2AuthenticationToken() {
    Map<String, Object> claims =
        Map.of(
            "oid", "00000000-0000-0000-0000-000000000000",
            "email", "test@example.com",
            "name", "Test");

    OidcIdToken idToken =
        new OidcIdToken("token", Instant.now(), Instant.now().plusSeconds(3600), claims);
    var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    DefaultOidcUser oidcUser = new DefaultOidcUser(authorities, idToken, "email");
    var token = new OAuth2AuthenticationToken(oidcUser, authorities, "test");
    SecurityContext context = new SecurityContextImpl(token);

    byte[] serialized = serializer.serialize(context);
    Object deserialized = serializer.deserialize(serialized);

    assertThat(deserialized).isInstanceOf(SecurityContext.class);
  }
}
