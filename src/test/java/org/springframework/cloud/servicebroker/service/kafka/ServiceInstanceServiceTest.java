package org.springframework.cloud.servicebroker.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.fixture.ServiceInstanceFixture;
import org.springframework.cloud.servicebroker.integration.IntegrationTestBase;
import org.springframework.cloud.servicebroker.kafka.service.KafkaServiceInstanceService;
import org.springframework.cloud.servicebroker.kafka.service.ZooClient;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rhardt on 11/23/16.
 */
@SpringBootTest
public class ServiceInstanceServiceTest extends IntegrationTestBase{

    @Autowired
    private ServiceInstanceService service;

    @Autowired
    private ZooClient client;

    @Autowired
    private ObjectMapper mapper;

    @Mock
    private ServiceDefinition serviceDefinition;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        try {
            client.delete(KafkaServiceInstanceService.INSTANCES_PATH + "/" + buildCreateRequest().getServiceInstanceId());
        } catch (Exception ex){};
    }

    private static final String SVC_DEF_ID = "serviceDefinitionId";
    private static final String SVC_PLAN_ID = "servicePlanId";

    @Test
    public void createServiceInstance() throws Exception {



        CreateServiceInstanceResponse response = service.createServiceInstance(buildCreateRequest());

        assertNotNull(response);
        assertNull(response.getDashboardUrl());
        assertFalse(response.isAsync());

        assertTrue(client.exists(KafkaServiceInstanceService.INSTANCES_PATH + "/" + buildCreateRequest().getServiceInstanceId()));

        exception.expect(Exception.class);
        service.createServiceInstance(buildCreateRequest());


    }

    @Test
    public void getLastOperation() throws Exception {
        assertTrue(true);
    }

    @Test
    public void deleteServiceInstance() throws Exception {

        try{
            client.create(KafkaServiceInstanceService.INSTANCES_PATH+"/"+buildCreateRequest().getServiceInstanceId(),
                    mapper.writeValueAsBytes(buildCreateRequest()));
        } catch (Exception e){}

        DeleteServiceInstanceResponse response = service.deleteServiceInstance(buildDeleteRequest());

        assertNotNull(response);
        assertFalse(response.isAsync());

        assertFalse(client.exists(KafkaServiceInstanceService.INSTANCES_PATH+"/"+buildDeleteRequest().getServiceInstanceId()));

        exception.expect(Exception.class);
        service.deleteServiceInstance(buildDeleteRequest());

    }

    @Test
    public void updateServiceInstance() throws Exception {
        assertTrue(true);
    }



    private CreateServiceInstanceRequest buildCreateRequest() {
        return new CreateServiceInstanceRequest(SVC_DEF_ID, SVC_PLAN_ID, "organizationGuid", "spaceGuid")
                .withServiceInstanceId(ServiceInstanceFixture.getServiceInstance().getServiceInstanceId());
    }

    private DeleteServiceInstanceRequest buildDeleteRequest() {
        return new DeleteServiceInstanceRequest(ServiceInstanceFixture.getServiceInstance().getServiceInstanceId(),
                SVC_DEF_ID, SVC_PLAN_ID, serviceDefinition);
    }
}