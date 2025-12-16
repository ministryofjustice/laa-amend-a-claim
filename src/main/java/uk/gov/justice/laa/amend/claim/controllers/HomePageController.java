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
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.Sorts;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.utils.RedirectUrlUtils;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.Objects;
import java.util.Optional;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_SORT;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;
    private final ClaimMapper claimMapper;
    private final SearchProperties searchProperties;

    @GetMapping("/")
    public String onPageLoad(
        Model model,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false) Sort sort,
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

        model.addAttribute("form", form);

        if (searchProperties.isSortEnabled()) {
            if (sort == null) {
                sort = Sort.defaults();
            }
            model.addAttribute("sorts", new Sorts(sort));
        } else {
            sort = null;
            model.addAttribute("sorts", Sorts.disabled());
        }

        if (form.anyNonEmpty()) {
            ClaimResultSet result = claimService.searchClaims(
                form.getProviderAccountNumber(),
                Optional.ofNullable(form.getUniqueFileNumber()),
                Optional.ofNullable(form.getCaseReferenceNumber()),
                Optional.ofNullable(form.getSubmissionPeriod()),
                page,
                DEFAULT_PAGE_SIZE,
                sort
            );
            String redirectUrl = RedirectUrlUtils.getRedirectUrl(form, page, sort);
            SearchResultView viewModel = claimResultMapper.toDto(result, redirectUrl, claimMapper);
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

        Sort sort = searchProperties.isSortEnabled() ? Sort.defaults() : null;
        String redirectUrl = RedirectUrlUtils.getRedirectUrl(form, sort);
        return "redirect:" + redirectUrl;
    }
}
