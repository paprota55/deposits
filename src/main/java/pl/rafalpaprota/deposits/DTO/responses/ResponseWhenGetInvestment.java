package pl.rafalpaprota.deposits.DTO.responses;

import lombok.Data;

@Data
public class ResponseWhenGetInvestment {
    private Long id;
    private String name;

    public ResponseWhenGetInvestment() {

    }

    public ResponseWhenGetInvestment(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
