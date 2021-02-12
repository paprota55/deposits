package pl.rafalpaprota.deposits.DTO.responses;

import lombok.Data;
import pl.rafalpaprota.deposits.model.Investment;

import java.time.LocalDate;

@Data
public class ResponseCalculation {
    private Double amount;
    private LocalDate calculationDate;
    private Investment investment;
    private String algorithmName;
    private Double profit;

    public ResponseCalculation() {
    }

    public ResponseCalculation(Double amount, LocalDate calculationDate, Investment investment, String algorithmName, Double profit) {
        this.amount = amount;
        this.calculationDate = calculationDate;
        this.investment = investment;
        this.algorithmName = algorithmName;
        this.profit = profit;
    }
}
