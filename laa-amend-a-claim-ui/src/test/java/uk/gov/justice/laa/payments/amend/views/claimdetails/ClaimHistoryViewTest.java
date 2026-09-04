package uk.gov.justice.laa.payments.amend.views.claimdetails;

import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.payments.amend.models.enums.Amendability.NEVER;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistory;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryFspEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewFieldGetter;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewFieldPatcher;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

@WebMvcTest(ClaimHistoryController.class)
class ClaimHistoryViewTest extends ClaimDetailsBaseTest {

  private static final String USER = "Joe Bloggs";
  private static final OffsetDateTime CREATED_AT =
      OffsetDateTime.of(2026, 4, 14, 9, 30, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime ASSESSED_AT =
      OffsetDateTime.of(2026, 5, 15, 10, 40, 0, 0, ZoneOffset.UTC);
  private static final OffsetDateTime VOIDED_AT =
      OffsetDateTime.of(2026, 5, 16, 10, 40, 0, 0, ZoneOffset.UTC);

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
  void testPageWithFspEventShowsExpectedCopyAndRecalculatedFields() {
    var fspEvent =
        new ClaimHistoryFspEvent(
            ASSESSED_AT,
            null,
            new BigDecimal("1000.00"),
            new BigDecimal("1200.50"),
            List.of(
                new ClaimHistoryAmendmentChange(
                    ClaimDetailsViewField.PROFIT_COST,
                    "fee.netProfitCostsAmount",
                    new BigDecimal("500.00"),
                    new BigDecimal("600.00"),
                    AreaOfLaw.LEGAL_HELP),
                new ClaimHistoryAmendmentChange(
                    ClaimDetailsViewField.DISBURSEMENTS,
                    "fee.disbursementAmount",
                    new BigDecimal("100.00"),
                    new BigDecimal("200.00"),
                    AreaOfLaw.LEGAL_HELP)));

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(fspEvent), null, null));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timelineItem = selectFirst(doc, ".moj-timeline .moj-timeline__item");
    assertThat(timelineItem.selectFirst(".moj-timeline__title").text())
        .isEqualTo("Total claim value recalculated");
    assertThat(timelineItem.selectFirst(".moj-timeline__byline").text())
        .isEqualTo("by Fee Scheme Platform");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(1)").text())
        .isEqualTo("Total claim value recalculated from £1,000.00 to £1,200.50");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(2)").text())
        .isEqualTo("The following fields were recalculated:");

    var bulletItems =
        timelineItem.select(".moj-timeline__description ul.govuk-list--bullet li").stream()
            .map(Element::text)
            .toList();
    assertThat(bulletItems)
        .containsExactly(
            "net disbursements changed from £100.00 to £200.00",
            "net profit costs changed from £500.00 to £600.00");
  }

  @Test
  void testPageWithAmendmentEventShowsRequestedByReasonAndBulletListCrimeLower() {
    assertAmendmentBulletListForArea(
        AreaOfLaw.CRIME_LOWER,
        areaFieldNames(AreaOfLaw.CRIME_LOWER),
        List.of(
            "case concluded date changed from before to after",
            "case reference number (CRN) changed from before to after",
            "case start date changed from before to after",
            "client disability changed from before to after",
            "client ethnicity changed from before to after",
            "client gender changed from before to after",
            "client initial changed from before to after",
            "client last name changed from before to after",
            "Defence Solicitor Call Centre (DSCC) number changed from before to after",
            "disbursements VAT changed from before to after",
            "duty solicitor changed from before to after",
            "fee code changed from before to after",
            "MAAT ID changed from before to after",
            "matter type changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "net travel costs changed from before to after",
            "net waiting costs changed from before to after",
            "number of police station or court attendances changed from before to after",
            "number of suspects or defendants changed from before to after",
            "outcome for client changed from before to after",
            "police station/court ID/prison ID changed from before to after",
            "Prison Law Prior Approval number changed from before to after",
            "representation order date changed from before to after",
            "scheme ID changed from before to after",
            "stage reached changed from before to after",
            "standard fee category changed from before to after",
            "unique file number (UFN) changed from before to after",
            "VAT indicator changed from before to after",
            "youth court changed from before to after"));
  }

  @Test
  void testPageWithAmendmentEventShowsRequestedByReasonAndBulletListLegalHelp() {
    assertAmendmentBulletListForArea(
        AreaOfLaw.LEGAL_HELP,
        areaFieldNames(AreaOfLaw.LEGAL_HELP),
        List.of(
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
            "client date of birth changed from before to after",
            "client disability changed from before to after",
            "client ethnicity changed from before to after",
            "client first name changed from before to after",
            "client gender changed from before to after",
            "Home Office unique client number (HO UCN) changed from before to after",
            "client last name changed from before to after",
            "client postal application accepted changed from before to after",
            "client postcode changed from before to after",
            "client type changed from before to after",
            "unique client number (UCN) changed from before to after",
            "court location (Housing Possession Court Duty Scheme (HPCDS)) changed from before to after",
            "delivery location changed from before to after",
            "designated accredited representative changed from before to after",
            "detention, travel and waiting (DTW) costs changed from before to after",
            "disbursements VAT changed from before to after",
            "eligible client changed from before to after",
            "exceptional case funding (ECF) reference changed from before to after",
            "exemption criteria satisfied changed from before to after",
            "fee code changed from before to after",
            "follow on work changed from before to after",
            "Home Office interview changed from before to after",
            "immigration removal centre (IRC) surgery changed from before to after",
            "judicial review or form filling changed from before to after",
            "legacy case changed from before to after",
            "local authority number changed from before to after",
            "London rate changed from before to after",
            "matter type 1 changed from before to after",
            "matter type 2 changed from before to after",
            "medical reports claimed changed from before to after",
            "meetings attended changed from before to after",
            "mental health tribunal reference changed from before to after",
            "National Immigration Asylum Team Disbursement prior authority number changed from before to after",
            "National Referral Mechanism (NRM) advice changed from before to after",
            "net cost of counsel changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "number of clients resulting in legal help matter opened changed from before to after",
            "number of clients seen at surgery changed from before to after",
            "outcome for client changed from before to after",
            "procurement area changed from before to after",
            "schedule reference changed from before to after",
            "stage reached changed from before to after",
            "substantive hearing changed from before to after",
            "surgery date changed from before to after",
            "tolerance indicator changed from before to after",
            "transfer date changed from before to after",
            "travel and waiting costs changed from before to after",
            "travel time (minutes) changed from before to after",
            "type of advice changed from before to after",
            "unique case ID changed from before to after",
            "unique file number (UFN) changed from before to after",
            "value of costs or damages recovered changed from before to after",
            "VAT indicator changed from before to after",
            "waiting time (minutes) changed from before to after"));
  }

  @Test
  void testPageWithAmendmentEventShowsRequestedByReasonAndBulletListMediation() {
    assertAmendmentBulletListForArea(
        AreaOfLaw.MEDIATION,
        areaFieldNames(AreaOfLaw.MEDIATION),
        List.of(
            "case concluded date changed from before to after",
            "case reference number (CRN) changed from before to after",
            "case start date changed from before to after",
            "claim ID changed from before to after",
            "client 1 date of birth changed from before to after",
            "client 1 disability changed from before to after",
            "client 1 ethnicity changed from before to after",
            "client 1 first name changed from before to after",
            "client 1 gender changed from before to after",
            "client 1 last name changed from before to after",
            "client 1 legally aided changed from before to after",
            "client 1 postal application accepted changed from before to after",
            "client 1 postcode changed from before to after",
            "client 1 unique client number (UCN) changed from before to after",
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
            "disbursements VAT changed from before to after",
            "fee code changed from before to after",
            "matter type 1 changed from before to after",
            "matter type 2 changed from before to after",
            "mediation time (minutes) changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "number of mediation sessions changed from before to after",
            "outcome changed from before to after",
            "outreach location changed from before to after",
            "referral changed from before to after",
            "schedule reference changed from before to after",
            "unique case ID changed from before to after",
            "unique file number (UFN) changed from before to after",
            "VAT indicator changed from before to after"));
  }

  @Test
  void testPageWithFspEventShowsBulletListCrimeLower() {
    assertFspBulletListForArea(
        AreaOfLaw.CRIME_LOWER,
        fspHistoryFieldsForArea(AreaOfLaw.CRIME_LOWER),
        List.of(
            "calculated VAT amount changed from before to after",
            "category of law changed from before to after",
            "disbursements VAT changed from before to after",
            "escape case changed from before to after",
            "fee type changed from before to after",
            "fixed fee changed from before to after",
            "hourly total amount changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "net travel costs changed from before to after",
            "net waiting costs changed from before to after",
            "VAT rate applied changed from before to after"));
  }

  @Test
  void testPageWithFspEventShowsBulletListLegalHelp() {
    assertFspBulletListForArea(
        AreaOfLaw.LEGAL_HELP,
        fspHistoryFieldsForArea(AreaOfLaw.LEGAL_HELP),
        List.of(
            "adjourned hearing fee changed from before to after",
            "bolt-on total fee amount changed from before to after",
            "calculated VAT amount changed from before to after",
            "case management review hearing (CMRH)-oral changed from before to after",
            "case management review hearing (CMRH)-telephone changed from before to after",
            "category of law changed from before to after",
            "detention, travel and waiting (DTW) costs changed from before to after",
            "disbursements VAT changed from before to after",
            "escape case changed from before to after",
            "fee type changed from before to after",
            "fixed fee changed from before to after",
            "Home Office interview changed from before to after",
            "hourly total amount changed from before to after",
            "judicial review or form filling changed from before to after",
            "net cost of counsel changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "substantive hearing changed from before to after",
            "travel and waiting costs changed from before to after",
            "VAT rate applied changed from before to after"));
  }

  @Test
  void testPageWithFspEventShowsBulletListMediation() {
    assertFspBulletListForArea(
        AreaOfLaw.MEDIATION,
        fspHistoryFieldsForArea(AreaOfLaw.MEDIATION),
        List.of(
            "calculated VAT amount changed from before to after",
            "category of law changed from before to after",
            "disbursements VAT changed from before to after",
            "escape case changed from before to after",
            "fee type changed from before to after",
            "fixed fee changed from before to after",
            "hourly total amount changed from before to after",
            "net disbursements changed from before to after",
            "net profit costs changed from before to after",
            "VAT rate applied changed from before to after"));
  }

  @Test
  void testPageWithFspEventFormatsVatRateAppliedAsPercentage() {
    var fspEvent =
        new ClaimHistoryFspEvent(
            ASSESSED_AT,
            null,
            new BigDecimal("1000.00"),
            new BigDecimal("1200.50"),
            List.of(
                new ClaimHistoryAmendmentChange(
                    ClaimDetailsViewField.VAT_RATE_APPLIED,
                    "fee.vatRateApplied",
                    new BigDecimal("12.34"),
                    new BigDecimal("20"),
                    AreaOfLaw.LEGAL_HELP)));

    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(fspEvent), null, null));

    var doc = renderDocument();
    var bulletItems =
        doc.select(".moj-timeline__description ul.govuk-list--bullet li").stream()
            .map(Element::text)
            .toList();

    assertThat(bulletItems).containsExactly("VAT rate applied changed from 12.34% to 20%");
  }

  private void assertAmendmentBulletListForArea(
      AreaOfLaw areaOfLaw, List<String> fieldNames, List<String> expectedLines) {
    var changes =
        fieldNames.stream()
            .sorted(Comparator.reverseOrder())
            .map(
                fieldName ->
                    new ClaimHistoryAmendmentChange(
                        testField(fieldName),
                        "identifier." + fieldName,
                        "before",
                        "after",
                        areaOfLaw))
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
    assertThat(bulletItems).hasSize(fieldNames.size());

    var sortedBulletItems = new ArrayList<>(bulletItems);
    sortedBulletItems.sort(String.CASE_INSENSITIVE_ORDER);
    assertThat(bulletItems).containsExactlyElementsOf(sortedBulletItems);
    var sortedExpectedLines = new ArrayList<>(expectedLines);
    sortedExpectedLines.sort(String.CASE_INSENSITIVE_ORDER);
    assertThat(bulletItems).containsExactlyElementsOf(sortedExpectedLines);
  }

  private void assertFspBulletListForArea(
      AreaOfLaw areaOfLaw, List<ClaimViewField<?>> fields, List<String> expectedLines) {
    var changes =
        fields.stream()
            .map(
                field ->
                    new ClaimHistoryAmendmentChange(
                        field, field.getFeeApiFieldName(), "before", "after", areaOfLaw))
            .toList();

    var fspEvent =
        new ClaimHistoryFspEvent(
            ASSESSED_AT, null, new BigDecimal("1000.00"), new BigDecimal("1200.50"), changes);
    when(claimHistoryService.getClaimHistory(claim))
        .thenReturn(new ClaimHistory(List.of(fspEvent), null, null));

    var doc = renderDocument();
    assertCommonPageContent(doc);

    var timelineItem = selectFirst(doc, ".moj-timeline .moj-timeline__item");
    assertThat(timelineItem.selectFirst(".moj-timeline__title").text())
        .isEqualTo("Total claim value recalculated");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(1)").text())
        .isEqualTo("Total claim value recalculated from £1,000.00 to £1,200.50");
    assertThat(timelineItem.selectFirst(".moj-timeline__description p:nth-of-type(2)").text())
        .isEqualTo("The following fields were recalculated:");

    var bulletItems =
        timelineItem.select(".moj-timeline__description ul.govuk-list--bullet li").stream()
            .map(Element::text)
            .toList();
    assertThat(bulletItems).hasSize(fields.size());

    var sortedBulletItems = new ArrayList<>(bulletItems);
    sortedBulletItems.sort(String.CASE_INSENSITIVE_ORDER);
    assertThat(bulletItems).containsExactlyElementsOf(sortedBulletItems);
    var sortedExpectedLines = new ArrayList<>(expectedLines);
    sortedExpectedLines.sort(String.CASE_INSENSITIVE_ORDER);
    assertThat(bulletItems).containsExactlyElementsOf(sortedExpectedLines);
  }

  private static List<String> areaFieldNames(AreaOfLaw areaOfLaw) {
    var common = Arrays.stream(ClaimDetailsViewField.values());
    var areaSpecific =
        switch (areaOfLaw) {
          case CRIME_LOWER -> Arrays.stream(CrimeClaimDetailsViewField.values());
          case LEGAL_HELP -> Arrays.stream(CivilClaimDetailsViewField.values());
          case MEDIATION -> Arrays.stream(MediationClaimDetailsViewField.values());
        };

    return concat(common, areaSpecific)
        .filter(ClaimHistoryViewTest::isAmendableHistoryField)
        .map(ClaimViewField::name)
        .distinct()
        .toList();
  }

  private static boolean isAmendableHistoryField(ClaimViewField<?> field) {
    var identifier = field.getClaimsApiFieldName();
    return identifier != null && !identifier.isBlank() && field.getAmendability() != NEVER;
  }

  private static List<ClaimViewField<?>> fspHistoryFieldsForArea(AreaOfLaw areaOfLaw) {
    var fields =
        new LinkedHashSet<ClaimViewField<?>>(
            Arrays.stream(ClaimDetailsViewField.values())
                .filter(ClaimHistoryViewTest::isDisplayedFspHistoryField)
                .toList());

    switch (areaOfLaw) {
      case CRIME_LOWER -> {
        fields.addAll(
            Arrays.stream(CrimeClaimDetailsViewField.values())
                .filter(ClaimHistoryViewTest::isDisplayedFspHistoryField)
                .toList());
      }
      case LEGAL_HELP ->
          fields.addAll(
              Arrays.stream(CivilClaimDetailsViewField.values())
                  .filter(ClaimHistoryViewTest::isDisplayedFspHistoryField)
                  .toList());
      case MEDIATION ->
          fields.addAll(
              Arrays.stream(MediationClaimDetailsViewField.values())
                  .filter(ClaimHistoryViewTest::isDisplayedFspHistoryField)
                  .toList());
      default -> throw new IllegalStateException("Unexpected value: " + areaOfLaw);
    }
    return List.copyOf(fields);
  }

  private static final Set<String> IGNORED_FSP_FIELDS =
      Set.of(
          "boltOnAdjournedHearingCount",
          "boltOnCmrhTelephoneCount",
          "boltOnCmrhOralCount",
          "boltOnHomeOfficeInterviewCount",
          "feeCodeDescription",
          "feeCode",
          "schemeId",
          "vatIndicator",
          "requestedNetProfitCostsAmount",
          "requestedNetDisbursementAmount");

  private static boolean isDisplayedFspHistoryField(ClaimViewField<?> field) {
    var identifier = field.getFeeApiFieldName();
    return identifier != null && !identifier.isBlank() && !isIgnoredFspFieldIdentifier(identifier);
  }

  private static boolean isIgnoredFspFieldIdentifier(String fieldIdentifier) {
    if (IGNORED_FSP_FIELDS.contains(fieldIdentifier)) {
      return true;
    }
    return fieldIdentifier.startsWith("fee.")
        && IGNORED_FSP_FIELDS.contains(fieldIdentifier.substring("fee.".length()));
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
