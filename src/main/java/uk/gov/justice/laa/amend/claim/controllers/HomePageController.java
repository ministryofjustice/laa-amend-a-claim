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

import java.util.Optional;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

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
        @RequestParam(required = false) String uniqueFileNumber,
        @RequestParam(required = false) String caseReferenceNumber
    ) {
        SearchForm form = new SearchForm();
        form.setProviderAccountNumber(providerAccountNumber);
        form.setSubmissionDateMonth(submissionDateMonth);
        form.setSubmissionDateYear(submissionDateYear);
        form.setUniqueFileNumber(uniqueFileNumber);
        form.setCaseReferenceNumber(caseReferenceNumber);
        model.addAttribute("searchForm", form);

        if (form.anyNonEmpty()) {
            ClaimResultSet result = claimService.searchClaims(
                form.getProviderAccountNumber(),
                Optional.ofNullable(form.getUniqueFileNumber()),
                Optional.ofNullable(form.getCaseReferenceNumber()),
                page,
                DEFAULT_PAGE_SIZE
            );
            SearchResultViewModel viewModel = claimResultMapper.toDto(result, form.getRedirectUrl(page));
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

        return "redirect:" + form.getRedirectUrl(1);
    }
}
