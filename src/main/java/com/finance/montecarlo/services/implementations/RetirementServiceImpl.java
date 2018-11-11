package com.finance.montecarlo.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.montecarlo.configuration.MonteCarloConfiguration;
import com.finance.montecarlo.models.ProfileDocument;
import com.finance.montecarlo.services.RetirementService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static javax.xml.crypto.dsig.SignatureProperties.TYPE;
import static org.elasticsearch.threadpool.ThreadPool.Names.INDEX;

@Service
public class RetirementServiceImpl implements RetirementService {

    private final MonteCarloConfiguration monteCarloConfiguration;
    private ObjectMapper objectMapper;
    private final Logger LOGGER;
    private RestHighLevelClient client;

    @Autowired
    public RetirementServiceImpl(MonteCarloConfiguration monteCarloConfiguration, RestHighLevelClient client, ObjectMapper objectMapper) {
        this.monteCarloConfiguration = monteCarloConfiguration;
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean createRetirementPlan(ProfileDocument document){

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);

        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, document.getId())
                .source(documentMapper);

        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

}