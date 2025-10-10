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
import uk.gov.justice.laa.amend.claim.service.ClaimService;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ClaimService claimService;

    @GetMapping("/")
    public String onPageLoad(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "index";
    }

    @PostMapping("/")
    public String onSubmit(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "3") Integer size,
        @Valid @ModelAttribute("searchForm") SearchForm searchForm,
        BindingResult bindingResult,
        Model model,
        HttpServletResponse response
    ) {
        if (searchForm.allEmpty()) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "index";
        }
        var result = claimService.searchClaims(searchForm.getProviderAccountNumber(), page, size);
        model.addAttribute("viewModel", result);
        return "index";
    }
}
