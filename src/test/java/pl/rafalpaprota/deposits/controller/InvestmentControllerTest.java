package pl.rafalpaprota.deposits.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rafalpaprota.deposits.DTO.responses.ResponseWhenGetInvestment;
import pl.rafalpaprota.deposits.DTO.responses.ResponseWhenGetInvestmentAndCalculations;
import pl.rafalpaprota.deposits.model.Investment;
import pl.rafalpaprota.deposits.repositories.InvestmentRepository;
import pl.rafalpaprota.deposits.services.InvestmentService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Test
    void shouldGetResponseWhenGetInvestmentAndOkStatus() throws Exception {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));
        
        Long id = investmentRepository.save(investment).getId();

        MvcResult result = this.mockMvc.perform(get("/api/investments"))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        List<ResponseWhenGetInvestment> list = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List>() {
        });

        assertNotNull(list);
        assertNotEquals(0, list.size());

    }

    @Test
    void shouldGetResponseWhenGetInvestmentAndCalculationsAndOkStatus() throws Exception {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long id = investmentRepository.save(investment).getId();

        MvcResult result = this.mockMvc.perform(get("/api/investments/" + id + "/calculations"))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        ResponseWhenGetInvestmentAndCalculations response = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWhenGetInvestmentAndCalculations>() {
        });

        assertNotNull(response);
        assertNotNull(response.getResponseCalculationList());
    }

    @Test
    void shouldAddNewInvestmentAndReturnNoContentStatus() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/api/investments")
                .contentType("application/json")
                .content("{\"name\" : \"test\" , \"rates\" : \"5\" , \"capitalizationPeriod\" : \"3\" , \"dateFrom\" : \"2020-08-10\" , \"dateTo\" : \"2021-08-10\"}"))
                .andDo(print())
                .andExpect(status().is(204))
                .andReturn();
    }

    @Test
    void shouldNotAddNewInvestmentAndReturnBadRequestStatus() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/api/investments")
                .contentType("application/json")
                .content("{\"name\" : \"test\" , \"rates\" : \"5\" , \"capitalizationPeriod\" : \"3\" , \"dateFrom\" : \"2020-ds-10\" , \"dateTo\" : \"\"}"))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void shouldAddNewCalculationAndReturnNoContentStatus() throws Exception {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().minusMonths(6));
        investment.setDateTo(LocalDate.now().plusMonths(6));

        Long id = investmentRepository.save(investment).getId();

        MvcResult result = this.mockMvc.perform(post("/api/investments/" + id + "/calculations")
                .contentType("application/json")
                .content("{\"amount\" : \"2000\" , \"algorithmName\" : \"end\"}"))
                .andDo(print())
                .andExpect(status().is(204))
                .andReturn();
    }

    @Test
    void shouldNotAddNewCalculationAndReturnBadRequestStatus() throws Exception {
        Investment investment = new Investment();
        investment.setCounter(0L);
        investment.setName("investmentTest");
        investment.setRates(7.2F);
        investment.setCapitalizationPeriod(3);
        investment.setDateFrom(LocalDate.now().plusYears(1));
        investment.setDateTo(LocalDate.now().plusYears(2));

        Long id = investmentRepository.save(investment).getId();

        MvcResult result = this.mockMvc.perform(post("/api/investments/" + id + "/calculations")
                .contentType("application/json")
                .content("{\"amount\" : \"2000\" , \"algorithmName\" : \"today\"}"))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();
    }


}