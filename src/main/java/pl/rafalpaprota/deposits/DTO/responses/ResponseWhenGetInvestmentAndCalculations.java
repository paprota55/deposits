package pl.rafalpaprota.deposits.DTO.responses;

import lombok.Data;
import pl.rafalpaprota.deposits.model.Investment;

import java.util.List;

@Data
public class ResponseWhenGetInvestmentAndCalculations {
    private Investment investment;
    private List<ResponseCalculation> responseCalculationList;

    public ResponseWhenGetInvestmentAndCalculations() {
    }

    public ResponseWhenGetInvestmentAndCalculations(Investment investment, List<ResponseCalculation> responseCalculations) {
        this.investment = investment;
        this.responseCalculationList = responseCalculations;
    }
}
