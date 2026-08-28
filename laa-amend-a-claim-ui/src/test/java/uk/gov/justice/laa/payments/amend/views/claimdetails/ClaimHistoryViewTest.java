package uk.gov.justice.laa.payments.amend.views.claimdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;
import uk.gov.justice.laa.payments.amend.controllers.claimdetails.ClaimHistoryController;
import uk.gov.justice.laa.payments.amend.models.AssessmentInfo;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistory;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewFieldGetter;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewFieldPatcher;

@WebMvcTest(ClaimHistoryController.class)
class ClaimHistoryViewTest extends ClaimDetailsBaseTest {

  private static final String USER = "Joe Bloggs";
  private static final OffsetDateTime CREATED_AT =
      OffsetDateTime.of(2026, 4, 14, 9, 30, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime ASSESSED_AT =
      OffsetDateTime.of(2026, 5, 15, 10, 40, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime VOIDED_AT =
      OffsetDateTime.of(2026, 5, 16, 10, 40, 0, 0, ZoneOffset.UTC);
  private static final List<String> ALL_AMENDABLE_FIELD_NAMES =
      List.of(
          "INITIAL",
          "SURNAME",
          "GENDER",
          "ETHNICITY",
          "DISABILITY",
          "CASE_REFERENCE_NUMBER",
          "CASE_START_DATE",
          "UNIQUE_FILE_NUMBER",
          "CASE_CONCLUDED_DATE",
          "FEE_CODE",
          "PROFIT_COST",
          "DISBURSEMENTS",
          "DISBURSEMENTS_VAT",
          "VAT",
          "MATTER_TYPE_CODE",
          "STAGE_REACHED",
          "REPRESENTATION_ORDER_DATE",
          "STANDARD_FEE_CATEGORY",
          "OUTCOME_FOR_CLIENT",
          "SUSPECTS_DEFENDANTS_COUNT",
          "POLICE_STATION_COURT_ATTENDANCES_COUNT",
          "POLICE_STATION_COURT_PRISON_ID",
          "SCHEME_ID",
          "DSCC_NUMBER",
          "MAAT_ID",
          "PRISON_LAW_PRIOR_APPROVAL_NUMBER",
          "IS_DUTY_SOLICITOR",
          "IS_YOUTH_COURT",
          "TRAVEL_COSTS",
          "WAITING_COSTS",
          "FORENAME",
          "DATE_OF_BIRTH",
          "POSTCODE",
          "IS_ELIGIBLE_CLIENT",
          "CLIENT_TYPE",
          "UNIQUE_CLIENT_NUMBER",
          "HOME_OFFICE_CLIENT_NUMBER",
          "IS_POSTAL_APPLICATION_ACCEPTED",
          "MATTER_TYPE_CODE_1",
          "MATTER_TYPE_CODE_2",
          "SCHEDULE_REFERENCE_CIVIL",
          "CASE_ID",
          "CASE_CONCLUDED_CLAIMED_DATE",
          "CASE_STAGE",
          "VALUE_OF_COSTS",
          "PROCUREMENT_AREA",
          "ACCESS_POINT",
          "EXCEPTIONAL_CASE_FUNDING",
          "CIVIL_LEGAL_ADVICE_REFERENCE",
          "CIVIL_LEGAL_ADVICE_EXEMPTION",
          "DELIVERY_LOCATION",
          "COURT_LOCATION",
          "AIT_HEARING_CENTRE",
          "LOCAL_AUTHORITY_NUMBER",
          "DESIGNATED_ACCREDITED_REPRESENTATIVE",
          "ADVICE_TIME",
          "TRAVEL_TIME",
          "WAITING_TIME",
          "ADDITIONAL_TRAVEL_PAYMENT",
          "FOLLOW_ON_WORK",
          "TOLERANCE_INDICATOR",
          "LEGACY_CASE",
          "MEETINGS_ATTENDED",
          "ADVICE_TYPE",
          "TRANSFER_DATE",
          "MEDICAL_REPORTS_CLAIMED",
          "EXEMPTION_CRITERIA_SATISFIED",
          "IRC_SURGERY",
          "SURGERY_DATE",
          "SURGERY_CLIENTS_COUNT",
          "SURGERY_MATTERS_COUNT",
          "MENTAL_HEALTH_TRIBUNAL_REFERENCE",
          "IS_NRM_ADVICE",
          "COUNSELS_COST",
          "TRAVEL_AND_WAITING_COSTS",
          "DETENTION_TRAVEL",
          "JR_FORM_FILLING",
          "ADJOURNED_HEARING_FEE",
          "CMRH_TELEPHONE",
          "CMRH_ORAL",
          "HOME_OFFICE",
          "SUBSTANTIVE_HEARING",
          "IS_LONDON_RATE",
          "PRIOR_AUTHORITY_REFERENCE",
          "IS_LEGALLY_AIDED",
          "CLIENT_2_FORENAME",
          "CLIENT_2_SURNAME",
          "CLIENT_2_DATE_OF_BIRTH",
          "CLIENT_2_UCN",
          "CLIENT_2_POSTCODE",
          "CLIENT_2_GENDER",
          "CLIENT_2_ETHNICITY",
          "CLIENT_2_DISABILITY",
          "IS_CLIENT_2_LEGALLY_AIDED",
          "IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED",
          "CLAIM_ID",
          "UNIQUE_CASE_ID",
          "MEDIATION_SESSIONS_COUNT",
          "MEDIATION_TIME_MINUTES",
          "OUTCOME",
          "OUTREACH_LOCATION",
          "REFERRAL_SOURCE",
          "SCHEDULE_REFERENCE");

  @BeforeEach
  public void setup() {
    super.setup();
    mapping = historyUrl;
  }

  @Test
  void testPageWithAssessments() {
    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_AT, USER, true);
    var assessedEvent =
        new ClaimHistoryAssessedEvent(
            ASSESSED_AT, USER, AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT, OutcomeType.PAID_IN_FULL);
    var voidedEvent = new ClaimHistoryVoidedEvent(VOIDED_AT, USER);

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(
            new ClaimHistory(
                List.of(voidedEvent, assessedEvent, createdEvent),
                new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs"),
                VOIDED_AT));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timeline = selectFirst(doc, ".moj-timeline");
    var timelineItems = timeline.selectStream(".moj-timeline__item").toList();

    assertThat(timelineItems).hasSize(3);

    assertTimelineItemContent(
        timelineItems.getFirst(),
        "Claim voided",
        "by Joe Bloggs",
        "16 May 2026 at 11:40am",
        "Claim voided");

    assertTimelineItemContent(
        timelineItems.get(1),
        "Claim assessed (Escape case assessment)",
        "by Joe Bloggs",
        "15 May 2026 at 11:40am",
        "Escape case assessment submitted with outcome Assessed in full");

    assertTimelineItemContent(
        timelineItems.get(2),
        "Claim created",
        "by Joe Bloggs",
        "14 April 2026 at 10:30am",
        List.of(
            "Claim uploaded to Submit a Bulk Claim",
            "Claim financial values calculated by Fee Scheme Platform",
            "Claim logged as an escape case by Fee Scheme Platform"));
  }

  @Test
  void testPageWithAmendedBanner() {
    claim.setAmended(true);
    claim.setHasAssessment(false);
    claim.setDerivedClaimStatus(DerivedClaimStatus.AMENDED);
    claim.setStatus(ClaimStatus.VALID);

    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_AT, USER, true);
    var lastUpdatedUser = new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs");
    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(createdEvent), lastUpdatedUser, ASSESSED_AT));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    assertPageHasInformationAlert(
        doc, "This claim has been amended", "Last edited by Joe Bloggs on 15 May 2026 at 11:40am");
    assertThat(doc.select("#information-alert .moj-alert__content > div")).hasSize(1);
  }

  @Test
  void testPageWithAssessedBanner() {
    claim.setAmended(false);
    claim.setHasAssessment(true);
    claim.setDerivedClaimStatus(DerivedClaimStatus.ASSESSED);
    claim.setStatus(ClaimStatus.VALID);
    claim.setLastAssessment(
        AssessmentInfo.builder().lastAssessmentOutcome(OutcomeType.PAID_IN_FULL).build());

    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_AT, USER, true);
    var lastUpdatedUser = new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs");
    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(createdEvent), lastUpdatedUser, ASSESSED_AT));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    assertPageHasInformationAlert(
        doc,
        "This claim has been assessed",
        "Last edited by Joe Bloggs on 15 May 2026 at 11:40am Assessed in full");
  }

  @Test
  void testPageWithVoidedBanner() {
    claim.setStatus(ClaimStatus.VOID);
    claim.setDerivedClaimStatus(DerivedClaimStatus.VOIDED);

    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_AT, USER, true);
    var lastUpdatedUser = new MicrosoftApiUser("test-id", "Bloggs, Joe", "Joe", "Bloggs");
    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(createdEvent), lastUpdatedUser, VOIDED_AT));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var banner = doc.selectFirst(".moj-alert.moj-alert--error");
    assertThat(banner).isNotNull();
    assertThat(banner.text()).contains("This claim has been voided");
    assertThat(banner.text()).contains("Last edited by Joe Bloggs on 16 May 2026 at 11:40am");
    assertThat(banner.text()).contains("You can no longer make changes");
  }

  @Test
  void testPageWithoutAssessments() {
    var createdEvent = new ClaimHistoryCreatedEvent(CREATED_AT, null, false);

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(createdEvent), null, null));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timeline = selectFirst(doc, ".moj-timeline");
    var timelineItems = timeline.selectStream(".moj-timeline__item").toList();

    assertThat(timelineItems).hasSize(1);

    assertTimelineItemContent(
        timelineItems.getFirst(),
        "Claim created",
        "User not currently available",
        "14 April 2026 at 10:30am",
        List.of(
            "Claim uploaded to Submit a Bulk Claim",
            "Claim financial values calculated by Fee Scheme Platform"));
  }

  @Test
  void testPageWithAmendmentEventShowsRequestedByReasonAndBulletList() {
    var changes =
        ALL_AMENDABLE_FIELD_NAMES.stream()
            .sorted(Comparator.reverseOrder())
            .map(
                fieldName ->
                    new ClaimHistoryAmendmentChange(
                        testField(fieldName), "identifier." + fieldName, "before", "after"))
            .toList();

    var amendedEvent =
        new ClaimHistoryAmendedEvent(ASSESSED_AT, USER, changes, "PROVIDER", "CORRECTION");

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(amendedEvent), null, null));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timelineItem = selectFirst(doc, ".moj-timeline .moj-timeline__item");
    assertThat(timelineItem.selectFirst(".moj-timeline__title").text()).isEqualTo("Claim amended");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(1)").text())
        .isEqualTo("Requested by PROVIDER");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(2)").text())
        .isEqualTo("Reason CORRECTION");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(3)").text())
        .isEqualTo("The following fields were amended:");

    var bulletItems =
        timelineItem.select(".moj-timeline__description ul.govuk-list--bullet li").stream()
            .map(Element::text)
            .toList();
    assertThat(bulletItems)
        .containsExactly(
            "access point changed from before to after",
            "additional travel payment changed from before to after",
            "adjourned hearing fee changed from before to after",
            "advice time (minutes) changed from before to after",
            "Asylum and Immigration Tribunal (AIT) hearing centre changed from before to after",
            "case concluded date changed from before to after",
            "case concluded date or case claimed date changed from before to after",
            "case ID changed from before to after",
            "case management review hearing (CMRH)-oral changed from before to after",
            "case management review hearing (CMRH)-telephone changed from before to after",
            "case reference number (CRN) changed from before to after",
            "case stage or level changed from before to after",
            "case start date changed from before to after",
            "Civil Legal Advice (CLA) exemption code changed from before to after",
            "Civil Legal Advice (CLA) reference number changed from before to after",
            "claim ID changed from before to after",
            "client 2 date of birth changed from before to after",
            "client 2 disability changed from before to after",
            "client 2 ethnicity changed from before to after",
            "client 2 first name changed from before to after",
            "client 2 gender changed from before to after",
            "client 2 last name changed from before to after",
            "client 2 legally aided changed from before to after",
            "client 2 postal application accepted changed from before to after",
            "client 2 postcode changed from before to after",
            "client 2 unique client number (UCN) changed from before to after",
            "client type changed from before to after",
            "court location (Housing Possession Court Duty Scheme (HPCDS)) changed from before to after",
            "date of birth changed from before to after",
            "Defence Solicitor Call Centre (DSCC) number changed from before to after",
            "delivery location changed from before to after",
            "designated accredited representative changed from before to after",
            "detention, travel and waiting (DTW) costs changed from before to after",
            "disability changed from before to after",
            "disbursement VAT changed from before to after",
            "duty solicitor changed from before to after",
            "eligible client changed from before to after",
            "ethnicity changed from before to after",
            "exceptional case funding (ECF) reference changed from before to after",
            "exemption criteria satisfied changed from before to after",
            "fee code changed from before to after",
            "first name changed from before to after",
            "follow on work changed from before to after",
            "gender changed from before to after",
            "Home Office Interview changed from before to after",
            "Home Office unique client number (HO UCN) changed from before to after",
            "immigration removal centre (IRC) surgery changed from before to after",
            "initial changed from before to after",
            "JR and form filling changed from before to after",
            "last name changed from before to after",
            "legacy case changed from before to after",
            "legally aided changed from before to after",
            "local authority number changed from before to after",
            "London rate changed from before to after",
            "MAAT ID changed from before to after",
            "matter type changed from before to after",
            "matter type 1 changed from before to after",
            "matter type 2 changed from before to after",
            "mediation time (minutes) changed from before to after",
            "medical reports claimed changed from before to after",
            "meetings attended changed from before to after",
            "mental health tribunal reference changed from before to after",
            "National Immigration Asylum Team Disbursement prior authority number changed from before to after",
            "national referral mechanism (NRM) advice changed from before to after",
            "net cost of counsel changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "net travel costs changed from before to after",
            "net waiting costs changed from before to after",
            "number of clients resulting in legal help matter opened changed from before to after",
            "number of clients seen at surgery changed from before to after",
            "number of mediation sessions changed from before to after",
            "number of police station or court attendances changed from before to after",
            "number of suspects or defendants changed from before to after",
            "outcome changed from before to after",
            "outcome for client changed from before to after",
            "outreach location changed from before to after",
            "police station/court ID/prison ID changed from before to after",
            "postal application accepted changed from before to after",
            "postcode changed from before to after",
            "Prison Law Prior Approval number changed from before to after",
            "procurement area changed from before to after",
            "referral changed from before to after",
            "representation order date changed from before to after",
            "schedule reference changed from before to after",
            "schedule reference changed from before to after",
            "scheme ID changed from before to after",
            "stage reached changed from before to after",
            "standard fee category changed from before to after",
            "substantive hearing changed from before to after",
            "surgery date changed from before to after",
            "tolerance indicator changed from before to after",
            "transfer date changed from before to after",
            "travel and waiting costs changed from before to after",
            "travel time (minutes) changed from before to after",
            "type of advice changed from before to after",
            "unique case ID changed from before to after",
            "unique client number (UCN) changed from before to after",
            "unique file number (UFN) changed from before to after",
            "value of costs or damages recovered changed from before to after",
            "VAT indicator changed from before to after",
            "waiting time (minutes) changed from before to after",
            "youth court changed from before to after");
  }

  private void assertCommonPageContent(Document doc) {
    assertPageHasTitle(doc, "Claim details");
    assertPageHasHeading(doc, "Claim details");
    assertPageDoesNotHaveBackLink(doc);

    assertPageHasNoActiveServiceNavigationItems(doc);
    assertPageHasInactiveSubNavigationItem(doc, "Overview", overviewUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Client", clientUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Case", caseUrl);
    assertPageHasInactiveSubNavigationItem(doc, "Costs", costsUrl);
    assertPageHasActiveSubNavigationItem(doc, "Claim history", mapping);

    assertH2Exists(doc, "Claim history");
  }

  private static ClaimViewField<?> testField(String name) {
    return new TestClaimViewField(name);
  }

  private record TestClaimViewField(String name) implements ClaimViewField<ClaimDetails> {
    @Override
    public <V> ClaimViewFieldGetter<ClaimDetails, V> getGetter() {
      throw new UnsupportedOperationException("Not used in this test");
    }

    @Override
    public FieldType getFieldType() {
      return FieldType.TEXT;
    }

    @Override
    public String getClaimsApiFieldName() {
      return "";
    }

    @Override
    public ClaimViewFieldPatcher<?> getPatcher() {
      return null;
    }
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
