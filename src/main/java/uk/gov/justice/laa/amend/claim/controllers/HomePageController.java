package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.SearchQuery;
import uk.gov.justice.laa.amend.claim.models.Sort;
import uk.gov.justice.laa.amend.claim.models.SortField;
import uk.gov.justice.laa.amend.claim.models.Sorts;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomePageController {

    private final ClaimService claimService;
    private final ClaimResultMapper claimResultMapper;
    private final ClaimMapper claimMapper;
    private final SearchProperties searchProperties;
    private final Validator validator;

    @GetMapping("/")
    public String onPageLoad(
            Model model,
            SearchQuery query,
            HttpSession session,
            HttpServletRequest request,
            Errors errors,
            HttpServletResponse response) {
        log.error("Test error log");
        log.warn("Test warn log");
        query.rejectUnknownParams(request);

        SearchForm form = new SearchForm(query);

        model.addAttribute("form", form);
        model.addAttribute("query", query);
        model.addAttribute("SortField", SortField.class);

        Sorts sorts;
        Sort sort = query.getSort();
        int page = query.getPage();
        if (searchProperties.isSortEnabled()) {
            if (sort == null) {
                sort = Sort.defaults();
            }
            sorts = new Sorts(sort);
        } else {
            sort = null;
            sorts = Sorts.disabled();
        }
        model.addAttribute("sorts", sorts);

        if (form.anyNonEmpty()) {
            ValidationUtils.invokeValidator(validator, form, errors);
            if (errors.hasErrors()) {
                model.addAttribute("org.springframework.validation.BindingResult.form", errors);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return "index";
            }
            ClaimResultSet result = claimService.searchClaims(
                    form.getProviderAccountNumber(),
                    Optional.ofNullable(form.getUniqueFileNumber()),
                    Optional.ofNullable(form.getCaseReferenceNumber()),
                    Optional.ofNullable(form.getSubmissionPeriod()),
                    page,
                    DEFAULT_PAGE_SIZE,
                    sort);
            String redirectUrl = query.getRedirectUrl(sort);
            SearchResultView viewModel = claimResultMapper.toDto(result, redirectUrl, claimMapper);
            model.addAttribute("viewModel", viewModel);
            session.setAttribute("searchUrl", redirectUrl);
        }

        return "index";
    }

    @PostMapping("/")
    public String onSubmit(
            @Valid @ModelAttribute("form") SearchForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "index";
        }

        Sort sort = searchProperties.isSortEnabled() ? Sort.defaults() : null;
        SearchQuery query = new SearchQuery(form, sort);
        String redirectUrl = query.getRedirectUrl();
        return "redirect:" + redirectUrl;
    }
}
