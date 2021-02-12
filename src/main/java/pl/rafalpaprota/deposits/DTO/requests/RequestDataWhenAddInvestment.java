package pl.rafalpaprota.deposits.DTO.requests;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class RequestDataWhenAddInvestment {

    private String name;

    private Float rates;

    private Integer capitalizationPeriod;

    @NotNull
    @DateTimeFormat
    private LocalDate dateFrom;

    @NotNull
    @DateTimeFormat
    private LocalDate dateTo;
}
