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

        // TODO - get results and add to model
        return "index";
    }

}
