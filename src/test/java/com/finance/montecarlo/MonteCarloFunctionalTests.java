package com.finance.montecarlo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.montecarlo.models.MonteCarloRequest;
import com.finance.montecarlo.models.MonteCarloResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Locale;

import static com.finance.montecarlo.TestUtils.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MonteCarloApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class MonteCarloFunctionalTests {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void runMonteCarloSimulationWithOnePortfolio() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Resource res = new ClassPathResource("test-data/runMonteCarloWithOnePortfolio.json");
        Reader reader = new InputStreamReader(res.getInputStream());
        MonteCarloRequest request = mapper.readValue(reader, MonteCarloRequest.class);

        MvcResult result = mockMvc.perform(post("/v1/finance/montecarlo")
                .content(convertObjectToJsonString(request))
                .contentType(contentType))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();

        MonteCarloResponse baseResponse = convertJsonStringToObject(
                result.getResponse().getContentAsString(),
                MonteCarloResponse.class
        );

        assertEquals(1, baseResponse.getPortfolios().size());
        assertEquals(10000, baseResponse.getSimulationSize());
        assertEquals(100000, baseResponse.getInitialInvestmentAmount(), 0);
        assertEquals(.035, baseResponse.getInflationRate(), 0);
        assertEquals(20, baseResponse.getNumberOfYears(), 0);
        assertEquals(request.getPortfolios().get(0).getMean(), baseResponse.getPortfolios().get(0).getMean(), 0);
        assertEquals(request.getPortfolios().get(0).getStandardDeviation(), baseResponse.getPortfolios().get(0).getStandardDeviation(), 0);
        assertEquals(request.getPortfolios().get(0).getPortfolioType(), baseResponse.getPortfolios().get(0).getPortfolioType());

        BigDecimal bestCase = parse(baseResponse.getPortfolios().get(0).getBestCasePerformance(), Locale.US);
        BigDecimal worstCase = parse(baseResponse.getPortfolios().get(0).getWorstCasePerformance(), Locale.US);
        BigDecimal median = parse(baseResponse.getPortfolios().get(0).getMedian(), Locale.US);
        if (bestCase.doubleValue() < 100000){
            fail("best case is less than initial investment amount");
        }

        if (worstCase.doubleValue() < 100000){
            fail("worst case is less than initial investment amount");
        }

        if (median.doubleValue() < 100000){
            fail("median is less than initial investment amount");
        }
    }

    @Test
    public void runMonteCarloSimulationWithTwoPortfolios() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Resource res = new ClassPathResource("test-data/runMonteCarloWithTwoPortfolios.json");
        Reader reader = new InputStreamReader(res.getInputStream());
        MonteCarloRequest request = mapper.readValue(reader, MonteCarloRequest.class);

        MvcResult result = mockMvc.perform(post("/v1/finance/montecarlo")
                .content(convertObjectToJsonString(request))
                .contentType(contentType))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();

        MonteCarloResponse baseResponse = convertJsonStringToObject(
                result.getResponse().getContentAsString(),
                MonteCarloResponse.class
        );

        assertEquals(2, baseResponse.getPortfolios().size());
    }

    @Test
    public void runMonteCarloSimulationWithNoStdDev() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Resource res = new ClassPathResource("test-data/runMonteCarloWithNoStdDev.json");
        Reader reader = new InputStreamReader(res.getInputStream());
        MonteCarloRequest request = mapper.readValue(reader, MonteCarloRequest.class);

        mockMvc.perform(post("/v1/finance/montecarlo")
                .content(convertObjectToJsonString(request))
                .contentType(contentType))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("standardDeviation is a required field")));
    }

    @Test
    public void runMonteCarloSimulationWithNoMean() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Resource res = new ClassPathResource("test-data/runMonteCarloWithNoMean.json");
        Reader reader = new InputStreamReader(res.getInputStream());
        MonteCarloRequest request = mapper.readValue(reader, MonteCarloRequest.class);

        mockMvc.perform(post("/v1/finance/montecarlo")
                .content(convertObjectToJsonString(request))
                .contentType(contentType))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("mean is a required field")));
    }
}
