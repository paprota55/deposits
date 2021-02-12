package pl.rafalpaprota.deposits.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Table(name = "investments")
@Entity
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long counter;

    @Column
    private String name;

    @Column
    private Float rates;

    @Column
    private Integer capitalizationPeriod;

    @Column
    private LocalDate dateFrom;

    @Column
    private LocalDate dateTo;
}
