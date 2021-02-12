package pl.rafalpaprota.deposits.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvestmentServiceTest {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private CalculationRepository calculationRepository;

    @Test
    void whenGivenDataToCalculateAndAlgorithmEndReturnProfit() throws IncorrectDataException {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(5F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long investmentId = investmentRepository.save(investment).getId();
        investment = investmentRepository.findById(investmentId).get();

        Double profit = investmentService.calculateEndAlgorithm(1000D, investment, investment.getDateFrom(), investment.getDateTo());

        assertNotNull(profit);
        assertEquals(50.94533691406218, profit);
    }

    @Test
    void whenGivenDataToCalculateAndAlgorithmTodayReturnProfit() throws IncorrectDataException {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(5F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long investmentId = investmentRepository.save(investment).getId();
        investment = investmentRepository.findById(investmentId).get();

        Double profit = investmentService.calculateTodayAlgorithm(1000D, investment, investment.getDateFrom(), investment.getDateFrom().plusMonths(4));

        assertNotNull(profit);
        assertEquals(16.747430941740845, profit);
    }

    @Test
    void whenGivenDataToCalculateAndAlgorithmEndThrowException() throws IncorrectDataException {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(5F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long investmentId = investmentRepository.save(investment).getId();
        final Investment finalInvestment = investmentRepository.findById(investmentId).get();

        assertThrows(IncorrectDataException.class, () -> {
            investmentService.calculateEndAlgorithm(1000D, investment, investment.getDateFrom().plusYears(1), investment.getDateTo().plusYears(1));
        });
    }

    @Test
    void whenGivenDataToCalculateAndAlgorithmTodayThrowException() throws IncorrectDataException {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(5F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long investmentId = investmentRepository.save(investment).getId();
        final Investment finalInvestment = investmentRepository.findById(investmentId).get();

        assertThrows(IncorrectDataException.class, () -> {
            investmentService.calculateTodayAlgorithm(1000D, investment, investment.getDateFrom().plusYears(1), LocalDate.now());
        });
    }

    @Test
    void whenRequestToGetInvestmentAndCalculationsReturnResponseWhenGetInvestmentAndCalculations() {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now());
        investment.setDateTo(LocalDate.now().plusYears(1));

        Long investmentId = investmentRepository.save(investment).getId();

        investment = investmentRepository.findById(investmentId).get();

        Calculation calculation = new Calculation();
        calculation.setAmount(1000D);
        calculation.setAlgorithmName("end");
        calculation.setCalculationDate(LocalDate.now());
        calculation.setInvestment(investment);
        calculation.setProfit(200D);

        Long calculationId = calculationRepository.save(calculation).getId();

        ResponseWhenGetInvestmentAndCalculations response = investmentService.getInvestmentAndCalculations(investmentId);

        assertNotNull(response);
        assertEquals(investment, response.getInvestment());
        assertNotEquals(0, response.getResponseCalculationList().size());
    }

    @Test
    void whenRequestDataWhenAddInvestmentGivenAddEntityToDatabaseAndReturnResponseWhenInvestmentAdded() {
        RequestDataWhenAddInvestment requestDataWhenAddInvestment = new RequestDataWhenAddInvestment();
        requestDataWhenAddInvestment.setName("test");
        requestDataWhenAddInvestment.setRates(7.2F);
        requestDataWhenAddInvestment.setCapitalizationPeriod(6);
        requestDataWhenAddInvestment.setDateFrom(LocalDate.now());
        requestDataWhenAddInvestment.setDateTo(LocalDate.now().plusYears(1));

        ResponseWhenInvestmentAdded response = investmentService.addInvestment(requestDataWhenAddInvestment);

        Optional<Investment> investment = investmentRepository.findById(response.getId());
        assertNotNull(investment);
        Investment current = investment.get();
        assertEquals(requestDataWhenAddInvestment.getName(), current.getName());
        assertEquals(requestDataWhenAddInvestment.getRates(), current.getRates());
        assertEquals(requestDataWhenAddInvestment.getCapitalizationPeriod(), current.getCapitalizationPeriod());
        assertEquals(requestDataWhenAddInvestment.getDateFrom(), current.getDateFrom());
        assertEquals(requestDataWhenAddInvestment.getDateTo(), current.getDateTo());
    }

    @Test
    void whenInvestmentListGivenReturnResponseWhenGetInvestmentList() {
        List<Investment> investmentList = new ArrayList<>();
        List<ResponseWhenGetInvestment> responseWhenGetInvestmentList = investmentService.convertInvestmentListToResponseWhenGetInvestmentList(investmentList);

        Investment investment = new Investment();
        investment.setId(1L);
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now());
        investment.setDateTo(LocalDate.now().plusYears(1));

        assertEquals(0, responseWhenGetInvestmentList.size());

        investmentList.add(investment);
        responseWhenGetInvestmentList = investmentService.convertInvestmentListToResponseWhenGetInvestmentList(investmentList);

        assertEquals(1, responseWhenGetInvestmentList.size());

    }

    @Test
    void whenCalculationListGivenReturnResponseCalculationList() {
        List<Calculation> calculationList = new ArrayList<>();
        List<ResponseCalculation> responseCalculationList = investmentService.convertCalculationListToCalculationResponseDTOList(calculationList);

        Investment investment = new Investment();
        investment.setId(1L);
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now());
        investment.setDateTo(LocalDate.now().plusYears(1));

        Calculation calculation = new Calculation();
        calculation.setId(1L);
        calculation.setAmount(1000D);
        calculation.setAlgorithmName("end");
        calculation.setCalculationDate(LocalDate.now());
        calculation.setInvestment(investment);
        calculation.setProfit(200D);

        assertEquals(0, responseCalculationList.size());

        calculationList.add(calculation);

        responseCalculationList = investmentService.convertCalculationListToCalculationResponseDTOList(calculationList);

        assertEquals(1, responseCalculationList.size());
    }

    @Test
    void whenRequestDataWhenAddInvestmentGivenReturnInvestment() {
        RequestDataWhenAddInvestment requestDataWhenAddInvestment = new RequestDataWhenAddInvestment();
        requestDataWhenAddInvestment.setName("Investment");
        requestDataWhenAddInvestment.setCapitalizationPeriod(6);
        requestDataWhenAddInvestment.setDateFrom(LocalDate.now());
        requestDataWhenAddInvestment.setDateTo(LocalDate.now().plusYears(1));
        requestDataWhenAddInvestment.setRates(5.0F);

        Investment newInvestment = investmentService.mapRequestDataWhenAddInvestmentToInvestment(requestDataWhenAddInvestment);

        assertNull(newInvestment.getId());
        assertEquals(0L, newInvestment.getCounter());
        assertEquals(requestDataWhenAddInvestment.getRates(), newInvestment.getRates());
        assertEquals(requestDataWhenAddInvestment.getCapitalizationPeriod(), newInvestment.getCapitalizationPeriod());
        assertEquals(requestDataWhenAddInvestment.getName(), newInvestment.getName());
        assertEquals(requestDataWhenAddInvestment.getDateFrom(), newInvestment.getDateFrom());
        assertEquals(requestDataWhenAddInvestment.getDateTo(), newInvestment.getDateTo());
    }

    @Test
    void whenCalculationGivenReturnResponseCalculation() {
        Investment investment = new Investment();
        investment.setId(1L);
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now());
        investment.setDateTo(LocalDate.now().plusYears(1));

        Calculation calculation = new Calculation();
        calculation.setId(1L);
        calculation.setAmount(1000D);
        calculation.setAlgorithmName("end");
        calculation.setCalculationDate(LocalDate.now());
        calculation.setInvestment(investment);
        calculation.setProfit(200D);

        ResponseCalculation responseCalculation = investmentService.mapCalculationToCalculationResponseDTO(calculation);

        assertEquals(calculation.getAmount(), responseCalculation.getAmount());
        assertEquals(calculation.getCalculationDate(), responseCalculation.getCalculationDate());
        assertEquals(calculation.getInvestment(), investment);
        assertEquals(calculation.getAlgorithmName(), responseCalculation.getAlgorithmName());
        assertEquals(calculation.getProfit(), responseCalculation.getProfit());
    }

    @Test
    void whenInvestmentGivenReturnResponseWhenInvestmentAdded() {
        Investment investment = new Investment();
        investment.setId(1L);
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now());
        investment.setDateTo(LocalDate.now().plusYears(1));

        ResponseWhenInvestmentAdded newResponse = investmentService.mapInvestmentToResponseWhenInvestmentAdded(investment);
        assertEquals(investment.getId(), newResponse.getId());
        assertEquals(investment.getName(), newResponse.getName());
        assertEquals(investment.getRates(), newResponse.getRates());
        assertEquals(investmentService.calculateDays(investment.getDateFrom(), investment.getDateTo()), newResponse.getDays());
    }

    @Test
    void whenTwoDatesGivenReturnNumberOfDays() {
        long numberOfDays = 6L;
        LocalDate first = LocalDate.now();
        LocalDate second = LocalDate.now().plusDays(numberOfDays);

        Long days = investmentService.calculateDays(first, second);
        assertEquals(numberOfDays, days);

        days = investmentService.calculateDays(second, first);
        assertEquals(-numberOfDays, days);

        days = investmentService.calculateDays(first, first);
        assertEquals(0, days);
    }
}