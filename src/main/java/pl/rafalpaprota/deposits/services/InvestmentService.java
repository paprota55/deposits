package pl.rafalpaprota.deposits.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.rafalpaprota.deposits.DTO.requests.RequestDataToCalculation;
import pl.rafalpaprota.deposits.DTO.requests.RequestDataWhenAddInvestment;
import pl.rafalpaprota.deposits.DTO.responses.ResponseCalculation;
import pl.rafalpaprota.deposits.DTO.responses.ResponseWhenGetInvestment;
import pl.rafalpaprota.deposits.DTO.responses.ResponseWhenGetInvestmentAndCalculations;
import pl.rafalpaprota.deposits.DTO.responses.ResponseWhenInvestmentAdded;
import pl.rafalpaprota.deposits.exceptions.IncorrectDataException;
import pl.rafalpaprota.deposits.model.Calculation;
import pl.rafalpaprota.deposits.model.Investment;
import pl.rafalpaprota.deposits.repositories.CalculationRepository;
import pl.rafalpaprota.deposits.repositories.InvestmentRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvestmentService {
    private final CalculationRepository calculationRepository;
    private final InvestmentRepository investmentRepository;

    @Autowired
    public InvestmentService(CalculationRepository calculationRepository, InvestmentRepository investmentRepository) {
        this.calculationRepository = calculationRepository;
        this.investmentRepository = investmentRepository;
    }

    public ResponseWhenInvestmentAdded addInvestment(RequestDataWhenAddInvestment requestDataWhenAddInvestment) {

        Long id = investmentRepository.save(mapRequestDataWhenAddInvestmentToInvestment(requestDataWhenAddInvestment)).getId();

        Optional<Investment> foundInvestment = investmentRepository.findById(id);

        return foundInvestment.map(this::mapInvestmentToResponseWhenInvestmentAdded).orElse(null);
    }

    public List<ResponseWhenGetInvestment> getInvestmentListDTO() {
        return convertInvestmentListToResponseWhenGetInvestmentList((List<Investment>) investmentRepository.findAll());
    }

    public ResponseWhenGetInvestmentAndCalculations getInvestmentAndCalculations(final Long id) {

        Optional<Investment> investment = investmentRepository.findById(id);
        if (investment.isPresent()) {
            return new ResponseWhenGetInvestmentAndCalculations(investment.get(), convertCalculationListToCalculationResponseDTOList(calculationRepository.findAllByInvestment(investment.get())));
        } else {
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = IncorrectDataException.class)
    public ResponseCalculation calculateTheInterest(Long id, RequestDataToCalculation requestDataToCalculation) throws IncorrectDataException {
        Calculation newResponse = new Calculation();
        newResponse.setAlgorithmName(requestDataToCalculation.getAlgorithmName());
        newResponse.setAmount(requestDataToCalculation.getAmount());
        newResponse.setCalculationDate(LocalDate.now());

        Optional<Investment> investment = investmentRepository.findById(id);
        if (investment.isPresent()) {
            newResponse.setInvestment(investment.get());
            investment.get().setCounter(investment.get().getCounter() + 1);
            investmentRepository.save(investment.get());

            if (requestDataToCalculation.getAlgorithmName().equals("end")) {
                double profit = calculateEndAlgorithm(requestDataToCalculation.getAmount(), investment.get(), investment.get().getDateFrom(), investment.get().getDateTo());
                //po odliczeniu podatku Belki
                //profit = profit * 0.81;
                newResponse.setProfit(profit);

            } else if (requestDataToCalculation.getAlgorithmName().equals("today")) {
                double profit = calculateTodayAlgorithm(requestDataToCalculation.getAmount(), investment.get(), investment.get().getDateFrom(), LocalDate.now());
                newResponse.setProfit(profit);
            }

            calculationRepository.save(newResponse);
            return mapCalculationToCalculationResponseDTO(newResponse);
        }

        return null;
    }

    //Compound interest
    public double calculateEndAlgorithm(Double amount, Investment investment, LocalDate dateFrom, LocalDate dateTo) throws IncorrectDataException {
        if (LocalDate.now().isBefore(dateFrom)) {
            throw new IncorrectDataException();
        } else {
            long investmentTime = calculateDays(dateFrom, dateTo);
            double k = 12 / investment.getCapitalizationPeriod();
            double n = investmentTime / 365.0;
            double x = 1 + (investment.getRates() / (100 * k));
            double profit = amount * Math.pow(x, n * k);
            return profit - amount;
        }
    }

    //Probably wrong but no more time to solve the problem
    //TODO when end other come back here
    public double calculateTodayAlgorithm(Double amount, Investment investment, LocalDate dateFrom, LocalDate dateTo) throws IncorrectDataException {
        if (dateTo.isBefore(dateFrom)) {
            throw new IncorrectDataException();
        } else {
            long investmentTime = calculateDays(dateFrom, dateTo);
            double k = 12 / investment.getCapitalizationPeriod();
            double n = investmentTime / 365.0;
            double x = 1 + (investment.getRates() / (100 * k));
            double profit = amount * Math.pow(x, n * k);
            return profit - amount;
        }
    }

    public List<ResponseWhenGetInvestment> convertInvestmentListToResponseWhenGetInvestmentList(List<Investment> investmentList) {

        ArrayList<ResponseWhenGetInvestment> responseWhenGetInvestments = new ArrayList<>();
        for (Investment current : investmentList) {
            responseWhenGetInvestments.add(new ResponseWhenGetInvestment(current.getId(), current.getName()));
        }
        return responseWhenGetInvestments;
    }

    public List<ResponseCalculation> convertCalculationListToCalculationResponseDTOList(List<Calculation> calculationList) {
        ArrayList<ResponseCalculation> responseCalculations = new ArrayList<>();
        for (Calculation current : calculationList) {
            responseCalculations.add(new ResponseCalculation(current.getAmount(), current.getCalculationDate(), current.getInvestment(), current.getAlgorithmName(), current.getProfit()));
        }
        return responseCalculations;
    }

    public Investment mapRequestDataWhenAddInvestmentToInvestment(RequestDataWhenAddInvestment requestDataWhenAddInvestment) {
        Investment investment = new Investment();
        investment.setName(requestDataWhenAddInvestment.getName());
        investment.setCapitalizationPeriod(requestDataWhenAddInvestment.getCapitalizationPeriod());
        investment.setRates(requestDataWhenAddInvestment.getRates());
        investment.setId(null);
        investment.setCounter(0L);
        investment.setDateFrom(requestDataWhenAddInvestment.getDateFrom());
        investment.setDateTo(requestDataWhenAddInvestment.getDateTo());
        return investment;
    }

    public ResponseCalculation mapCalculationToCalculationResponseDTO(Calculation calculation) {
        return new ResponseCalculation(calculation.getAmount(), calculation.getCalculationDate(), calculation.getInvestment(), calculation.getAlgorithmName(), calculation.getProfit());
    }

    public ResponseWhenInvestmentAdded mapInvestmentToResponseWhenInvestmentAdded(Investment investment) {
        ResponseWhenInvestmentAdded responseWhenInvestmentAdded = new ResponseWhenInvestmentAdded();
        responseWhenInvestmentAdded.setId(investment.getId());
        responseWhenInvestmentAdded.setRates(investment.getRates());
        responseWhenInvestmentAdded.setName(investment.getName());
        responseWhenInvestmentAdded.setDays(calculateDays(investment.getDateFrom(), investment.getDateTo()));
        return responseWhenInvestmentAdded;
    }

    public Long calculateDays(LocalDate dateFrom, LocalDate dateTo) {
        return ChronoUnit.DAYS.between(dateFrom, dateTo);
    }
}
