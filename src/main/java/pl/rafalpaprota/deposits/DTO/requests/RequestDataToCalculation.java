package pl.rafalpaprota.deposits.DTO.requests;

import lombok.Data;

@Data
public class RequestDataToCalculation {
    private Double amount;
    private String algorithmName;
}
