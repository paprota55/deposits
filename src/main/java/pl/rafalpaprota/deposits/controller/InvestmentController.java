package pl.rafalpaprota.deposits.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rafalpaprota.deposits.DTO.requests.RequestDataToCalculation;
import pl.rafalpaprota.deposits.DTO.requests.RequestDataWhenAddInvestment;
import pl.rafalpaprota.deposits.DTO.responses.ResponseCalculation;
import pl.rafalpaprota.deposits.exceptions.IncorrectDataException;
import pl.rafalpaprota.deposits.services.InvestmentService;

import javax.validation.Valid;

@RestController
public class InvestmentController {
    private final InvestmentService investmentService;

    @Autowired
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "api/investments")
    public ResponseEntity<?> addInvestment(@RequestBody @Valid final RequestDataWhenAddInvestment requestDataWhenAddInvestment) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(investmentService.addInvestment(requestDataWhenAddInvestment));
    }

    @RequestMapping(method = RequestMethod.GET, value = "api/investments")
    public ResponseEntity<?> getInvestments() {
        return ResponseEntity.ok(investmentService.getInvestmentListDTO());
    }

    @RequestMapping(method = RequestMethod.POST, value = "api/investments/{id}/calculations")
    public ResponseEntity<?> calculate(@PathVariable final Long id, @RequestBody RequestDataToCalculation requestDataToCalculation) {
        try {
            ResponseCalculation response = investmentService.calculateTheInterest(id, requestDataToCalculation);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (IncorrectDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "api/investments/{id}/calculations")
    public ResponseEntity<?> getCalculates(@PathVariable final Long id) {
        return ResponseEntity.ok(investmentService.getInvestmentAndCalculations(id));
    }
}
