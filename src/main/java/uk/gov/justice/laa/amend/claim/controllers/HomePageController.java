package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String onPageLoad(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "index";
    }

    @PostMapping("/")
    public String onSubmit(
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

        List<Claim> claims = List.of(
            new Claim(
                "01012025/123",
                "No data",
                "Doe",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            ),
            new Claim(
                "1203022025/123",
                "No data",
                "White",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            ),
            new Claim(
                "18042025/123",
                "No data",
                "Stevens",
                LocalDate.of(2025, 7, 25),
                "ABC123",
                "Family",
                "EC"
            )
        );

        SearchResultViewModel viewModel = new SearchResultViewModel(claims);

        model.addAttribute("viewModel", viewModel);
        return "index";
    }

}
