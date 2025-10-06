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
import uk.gov.justice.laa.amend.claim.viewmodels.SearchResultViewModel;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

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

        ClaimResponse claim1 = new ClaimResponse();
        claim1.setUniqueFileNumber("290419/711");
        claim1.setCaseReferenceNumber("EF/4560/2018/4364683");
        claim1.setClientSurname("Doe");
        claim1.setCaseStartDate("2019-04-29");
        claim1.setFeeCode("CAPA");
        claim1.setMatterTypeCode("IMCB:IRVL");
        claim1.setStatus(ClaimStatus.READY_TO_PROCESS);

        ClaimResponse claim2 = new ClaimResponse();
        claim2.setUniqueFileNumber("101117/712");
        claim2.setCaseReferenceNumber("EF/4439/2017/3078011");
        claim2.setClientSurname("White");
        claim2.setCaseStartDate("2017-11-10");
        claim2.setFeeCode("CAPA");
        claim2.setMatterTypeCode("IMLB:IOUT");
        claim2.setStatus(ClaimStatus.VALID);

        ClaimResponse claim3 = new ClaimResponse();
        claim3.setUniqueFileNumber("120419/714");
        claim3.setCaseReferenceNumber("DM/4604/2019/4334501");
        claim3.setClientSurname("Stevens");
        claim3.setCaseStartDate("2019-04-12");
        claim3.setFeeCode("CAPA");
        claim3.setMatterTypeCode("IMLB:IFME");
        claim3.setStatus(ClaimStatus.INVALID);

        ClaimResultSet result = new ClaimResultSet();
        result.setContent(List.of(claim1, claim2, claim3));
        SearchResultViewModel viewModel = new SearchResultViewModel(result);

        model.addAttribute("viewModel", viewModel);
        return "index";
    }

}
