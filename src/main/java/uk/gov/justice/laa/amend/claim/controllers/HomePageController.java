package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

import java.util.List;

import static uk.gov.justice.laa.amend.claim.forms.helpers.StringUtils.isEmpty;

@Controller
public class HomePageController {

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
        model.addAttribute("page", page);

        if (!form.allEmpty()) {
            ClaimResultSet result = getResult(page);
            SearchResultViewModel viewModel = new SearchResultViewModel(result, getRedirectUrl(page, form));
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

    private ClaimResultSet getResult(int page) {
        ClaimResponse claim1 = new ClaimResponse();
        claim1.setUniqueFileNumber("290419/711");
        claim1.setCaseReferenceNumber("EF/4560/2018/4364683");
        claim1.setClientSurname("Doe");
        claim1.setCaseStartDate("2019-04-29");
        claim1.setScheduleReference("0X766A/2018/02");

        ClaimResponse claim2 = new ClaimResponse();
        claim2.setUniqueFileNumber("101117/712");
        claim2.setCaseReferenceNumber("EF/4439/2017/3078011");
        claim2.setClientSurname("White");
        claim2.setCaseStartDate("2017-11-10");
        claim2.setScheduleReference("0X766A/2018/02");

        ClaimResponse claim3 = new ClaimResponse();
        claim3.setUniqueFileNumber("120419/714");
        claim3.setCaseReferenceNumber("DM/4604/2019/4334501");
        claim3.setClientSurname("Stevens");
        claim3.setCaseStartDate("2019-04-12");
        claim3.setScheduleReference("0X766A/2018/02");

        ClaimResultSet result = new ClaimResultSet();
        result.setContent(List.of(claim1, claim2, claim3));
        result.setTotalPages(9);
        result.setTotalElements(27);
        result.setNumber(page);
        result.setSize(3);

        return result;
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
