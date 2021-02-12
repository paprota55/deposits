package pl.rafalpaprota.deposits.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.rafalpaprota.deposits.model.Investment;

@Repository
public interface InvestmentRepository extends CrudRepository<Investment, Long> {

}
