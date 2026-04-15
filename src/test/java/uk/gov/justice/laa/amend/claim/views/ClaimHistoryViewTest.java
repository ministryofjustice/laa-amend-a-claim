package uk.gov.justice.laa.amend.claim.views;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.amend.claim.models.ClaimHistoryEventType.CLAIM_VOIDED;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.controllers.ClaimHistoryController;
import uk.gov.justice.laa.amend.claim.models.ClaimHistory;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.ClaimHistoryService;

@WebMvcTest(ClaimHistoryController.class)
class ClaimHistoryViewTest extends ViewTestBase {

  private static final String USER = "Joe Bloggs";
  private static final OffsetDateTime CREATED_AT =
      OffsetDateTime.of(2026, 4, 14, 9, 30, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime ASSESSED_AT =
      OffsetDateTime.of(2026, 5, 15, 10, 40, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime VOIDED_AT =
      OffsetDateTime.of(2026, 5, 16, 10, 40, 0, 0, ZoneOffset.UTC);

  @MockitoBean private ClaimHistoryService claimHistoryService;

  private final String overviewUrl;

  ClaimHistoryViewTest() {
    this.overviewUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
    this.mapping = String.format("/submissions/%s/claims/%s/history", submissionId, claimId);
  }

  @BeforeEach
  void setUp() {
    when(featureFlagsConfig.getIsClaimHistoryEnabled()).thenReturn(true);
  }

  @Test
  void testPageWithAssessments() {
    var createdEvent =
        new ClaimHistoryEvent(CLAIM_CREATED_AND_ESCAPED, CREATED_AT, USER, Optional.empty());
    var assessedEvent =
        new ClaimHistoryEvent(
            CLAIM_ASSESSED_ESCAPE_CASE, ASSESSED_AT, USER, Optional.of(OutcomeType.PAID_IN_FULL));
    var voidedEvent = new ClaimHistoryEvent(CLAIM_VOIDED, VOIDED_AT, USER, Optional.empty());

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(
            new ClaimHistory(
                List.of(voidedEvent, assessedEvent, createdEvent),
                Optional.of(new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs"))));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timeline = selectFirst(doc, ".moj-timeline");
    var timelineItems = timeline.selectStream(".moj-timeline__item").toList();

    assertThat(timelineItems).hasSize(3);

    assertTimelineItemContent(
        timelineItems.getFirst(),
        "Claim voided",
        "by Joe Bloggs",
        "16 May 2026 at 11:40:00",
        "Claim voided");

    assertTimelineItemContent(
        timelineItems.get(1),
        "Claim assessed (Escape case assessment)",
        "by Joe Bloggs",
        "15 May 2026 at 11:40:00",
        "Escape case assessment submitted with outcome Assessed in full");

    assertTimelineItemContent(
        timelineItems.get(2),
        "Claim created",
        "by Joe Bloggs",
        "14 April 2026 at 10:30:00",
        List.of(
            "Claim uploaded to Submit a Bulk Claim",
            "Claim financial values calculated by Fee Scheme Platform",
            "Claim logged as an escape case by Fee Scheme Platform"));
  }

  @Test
  void testPageWithoutAssessments() {
    var createdEvent = new ClaimHistoryEvent(CLAIM_CREATED, CREATED_AT, null, Optional.empty());

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(createdEvent), Optional.empty()));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timeline = selectFirst(doc, ".moj-timeline");
    var timelineItems = timeline.selectStream(".moj-timeline__item").toList();

    assertThat(timelineItems).hasSize(1);

    assertTimelineItemContent(
        timelineItems.getFirst(),
        "Claim created",
        "User not currently available",
        "14 April 2026 at 10:30:00",
        List.of(
            "Claim uploaded to Submit a Bulk Claim",
            "Claim financial values calculated by Fee Scheme Platform"));
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasActiveSubNavigationItem(doc, "Claim history", mapping);

    assertH2Exists(doc, "Claim history");
  }

  private void assertTimelineItemContent(
      Element item,
      String typeText,
      String byUserText,
      String dateTimeText,
      String descriptionText) {
    assertTimelineItemContent(item, typeText, byUserText, dateTimeText, List.of(descriptionText));
  }

  private void assertTimelineItemContent(
      Element item,
      String typeText,
      String byUserText,
      String dateTimeText,
      List<String> descriptionText) {
    assertElementExists(
        item,
        ".moj-timeline__header",
        header -> {
          assertH2Exists(header, typeText);
          assertParagraphExists(header, byUserText);
        });
    assertElementExists(
        item, ".moj-timeline__date", date -> assertThat(date.text()).isEqualTo(dateTimeText));
    assertElementExists(
        item,
        ".moj-timeline__description",
        description -> descriptionText.forEach(text -> assertParagraphExists(description, text)));
  }
}
