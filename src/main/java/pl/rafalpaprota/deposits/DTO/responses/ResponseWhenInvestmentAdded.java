package pl.rafalpaprota.deposits.DTO.responses;

import lombok.Data;

@Data
public class ResponseWhenInvestmentAdded {
    private Long id;

    private String name;

    private Float rates;

    private Long days;
}
