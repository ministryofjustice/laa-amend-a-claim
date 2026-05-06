package uk.gov.justice.laa.amend.claim.controllers;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.DEFAULT_PAGE_SIZE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.search.SearchQuery;
import uk.gov.justice.laa.amend.claim.models.search.SearchSortField;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@Controller
@RequiredArgsConstructor
public class HomePageController {

  public static final List<ClaimStatus> CLAIM_STATUSES =
      List.of(ClaimStatus.VALID, ClaimStatus.VOID);

  private final ClaimService claimService;
  private final ClaimResultMapper claimResultMapper;
  private final ClaimMapper claimMapper;
  private final Validator validator;

  @GetMapping("/")
  public String onPageLoad(
      Model model,
      SearchQuery query,
      HttpSession session,
      HttpServletRequest request,
      Errors errors,
      HttpServletResponse response) {
    query.rejectUnknownParams(request);

    SearchForm form = new SearchForm(query);

    model.addAttribute("form", form);
    model.addAttribute("query", query);
    model.addAttribute("SortField", SearchSortField.class);

    if (form.anyNonEmpty()) {
      ValidationUtils.invokeValidator(validator, form, errors);
      if (errors.hasErrors()) {
        model.addAttribute("org.springframework.validation.BindingResult.form", errors);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "index";
      }
      var result =
          claimService.searchClaims(
              form.getOfficeCode(),
              Optional.ofNullable(form.getUniqueFileNumber()),
              Optional.ofNullable(form.getCaseReferenceNumber()),
              Optional.ofNullable(form.getSubmissionPeriod()),
              Optional.ofNullable(form.getAreaOfLaw()),
              Optional.ofNullable(form.getEscapeCase()),
              CLAIM_STATUSES,
              query.getPage(),
              DEFAULT_PAGE_SIZE,
              query.getSort());
      String redirectUrl = query.getRedirectUrl(query.getSort());
      SearchResultView viewModel = claimResultMapper.toDto(result, redirectUrl, claimMapper);
      model.addAttribute("viewModel", viewModel);
      session.setAttribute("searchUrl", redirectUrl);
    }

    return "index";
  }

  @PostMapping("/")
  public String onSubmit(
      @Valid @ModelAttribute("form") SearchForm form,
      BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "index";
    }

    SearchQuery query = new SearchQuery(form);
    String redirectUrl = query.getRedirectUrl();
    return "redirect:" + redirectUrl;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public String handleSearchQueryBindingError(MethodArgumentNotValidException ex) {
    if (ex.getBindingResult().getTarget() instanceof SearchQuery query
        && ex.getBindingResult().getFieldErrors().stream()
            .anyMatch(fe -> "sort".equals(fe.getField()))) {
      return "redirect:" + query.getRedirectUrl(null);
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters");
  }
}
