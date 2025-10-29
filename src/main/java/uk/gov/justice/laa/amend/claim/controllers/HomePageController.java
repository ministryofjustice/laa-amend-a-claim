package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.utils.RedirectUrlUtils;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;

    @GetMapping("/")
    public String onPageLoad(
        Model model,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = DEFAULT_SORT) Sort sort,
        @RequestParam(required = false) String providerAccountNumber,
        @RequestParam(required = false) String submissionDateMonth,
        @RequestParam(required = false) String submissionDateYear,
        @RequestParam(required = false) String uniqueFileNumber,
        @RequestParam(required = false) String caseReferenceNumber
    ) {
        SearchForm form = new SearchForm();
        form.setProviderAccountNumber(providerAccountNumber);
        form.setSubmissionDateMonth(submissionDateMonth);
        form.setSubmissionDateYear(submissionDateYear);
        form.setUniqueFileNumber(uniqueFileNumber);
        form.setCaseReferenceNumber(caseReferenceNumber);

        model.addAttribute("sorts", getSorts(sort));
        model.addAttribute("form", form);

        if (form.anyNonEmpty()) {
            ClaimResultSet result = claimService.searchClaims(
                form.getProviderAccountNumber(),
                Optional.ofNullable(form.getUniqueFileNumber()),
                Optional.ofNullable(form.getCaseReferenceNumber()),
                page,
                DEFAULT_PAGE_SIZE,
                sort.toString()
            );
            String redirectUrl = RedirectUrlUtils.getRedirectUrl(form, page, sort);
            SearchResultViewModel viewModel = claimResultMapper.toDto(result, redirectUrl);
            model.addAttribute("viewModel", viewModel);
        }

        return "index";
    }

    @PostMapping("/")
    public String onSubmit(
        @Valid @ModelAttribute("form") SearchForm form,
        BindingResult bindingResult,
        HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "index";
        }

        String redirectUrl = RedirectUrlUtils.getRedirectUrl(form);
        return "redirect:" + redirectUrl;
    }

    private Map<String, SortDirection> getSorts(Sort currentSort) {
        Map<String, SortDirection> sorts = new HashMap<>(Map.of(
            "uniqueFileNumber", SortDirection.NONE,
            "caseReferenceNumber", SortDirection.NONE,
            "scheduleReference", SortDirection.NONE
        ));
        sorts.put(currentSort.getField(), currentSort.getDirection());
        return sorts;
    }
}
