package pl.rafalpaprota.deposits.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Table(name = "calculations")
@Entity
public class Calculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double amount;

    @Column
    private LocalDate calculationDate;

    @Column
    private String algorithmName;

    @Column
    private Double profit;

    @OneToOne
    @JoinColumn(name = "investment_id", referencedColumnName = "id")
    private Investment investment;
}
