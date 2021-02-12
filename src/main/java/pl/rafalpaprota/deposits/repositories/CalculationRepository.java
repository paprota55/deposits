package pl.rafalpaprota.deposits.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.rafalpaprota.deposits.model.Calculation;
import pl.rafalpaprota.deposits.model.Investment;

import java.util.List;

@Repository
public interface CalculationRepository extends CrudRepository<Calculation, Long> {
    List<Calculation> findAllByInvestment(Investment investment);
}
