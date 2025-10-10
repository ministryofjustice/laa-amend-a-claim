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
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;
import static uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils.isEmpty;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;

    @GetMapping("/")
    public String onPageLoad(
        Model model,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false) String providerAccountNumber,
        @RequestParam(required = false) String submissionDateMonth,
        @RequestParam(required = false) String submissionDateYear,
        @RequestParam(required = false) String referenceNumber
    ) {
        SearchForm form = new SearchForm();
        form.setProviderAccountNumber(providerAccountNumber);
        form.setSubmissionDateMonth(submissionDateMonth);
        form.setSubmissionDateYear(submissionDateYear);
        form.setReferenceNumber(referenceNumber);
        model.addAttribute("searchForm", form);

        if (!form.allEmpty()) {
            ClaimResultSet result = claimService.searchClaims(form.getProviderAccountNumber(), page, DEFAULT_PAGE_SIZE);
            SearchResultViewModel viewModel = claimResultMapper.toDto(result, getRedirectUrl(page, form));
            model.addAttribute("viewModel", viewModel);
        }

        return "index";
    }

    @PostMapping("/")
    public String onSubmit(
        @Valid @ModelAttribute("searchForm") SearchForm form,
        BindingResult bindingResult,
        HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "index";
        }

        return "redirect:" + getRedirectUrl(1, form);
    }

    private String getRedirectUrl(int page, SearchForm form) {
        String redirectUrl = String.format("/?page=%d", page);
        if (!isEmpty(form.getProviderAccountNumber())) {
            redirectUrl += String.format("&providerAccountNumber=%s", form.getProviderAccountNumber());
        }
        if (!isEmpty(form.getSubmissionDateMonth())) {
            redirectUrl += String.format("&submissionDateMonth=%s", form.getSubmissionDateMonth());
        }
        if (!isEmpty(form.getSubmissionDateYear())) {
            redirectUrl += String.format("&submissionDateYear=%s", form.getSubmissionDateYear());
        }
        if (!isEmpty(form.getReferenceNumber())) {
            redirectUrl += String.format("&referenceNumber=%s", form.getReferenceNumber());
        }
        return redirectUrl;
    }
}
