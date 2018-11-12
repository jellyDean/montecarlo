package com.finance.montecarlo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Functional tests for testing the application
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MonteCarloApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ElasticSearchFunctionalTests {

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
    public void runAwsEsQueryWithInvalidSponsorState() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans?sponsorState=KG")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("SponsorState is not supported")));
    }

    @Test
    public void runAwsEsQueryWithTwoManyQueryParams() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans?sponsorState=KG&planName=test")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Only one query parameter")));
    }

    @Test
    public void runAwsEsQueryWithValidSponsorState() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans?sponsorState=OH")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"total\":8535")));
    }

    @Test
    public void runAwsEsQueryWithValidPlanName() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans?planName=apple")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"total\":69")));
    }

    @Test
    public void runAwsEsQueryWithSponsorName() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans?sponsorName=test")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"total\":28")));
    }

    @Test
    public void runAwsEsQueryWithNoParams() throws Exception {

        mockMvc.perform(get("/v1/finance/retirement/plans")
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"total\":203082")));
    }
}
