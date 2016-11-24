package org.springframework.cloud.servicebroker.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.fixture.ServiceInstanceFixture;
import org.springframework.cloud.servicebroker.integration.IntegrationTestBase;
import org.springframework.cloud.servicebroker.kafka.service.KafkaServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.kafka.service.KafkaServiceInstanceService;
import org.springframework.cloud.servicebroker.kafka.service.ZooClient;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import static org.junit.Assert.*;

/**
 * Created by rhardt on 11/23/16.
 */
@SpringBootTest
public class ServiceInstanceBindingServiceTest extends IntegrationTestBase{

    @Autowired
    private ServiceInstanceBindingService service;

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
            client.delete(KafkaServiceInstanceBindingService.BINDINGS_PATH + "/" + buildCreateRequest().getBindingId());
        } catch (Exception ex){};
    }

    private static final String SVC_DEF_ID = "serviceDefinitionId";
    private static final String SVC_PLAN_ID = "servicePlanId";
    private static final String SVC_BINDING_ID = "servicePlanId";

    @Test
    public void createServiceBindingInstance() throws Exception {



        CreateServiceInstanceBindingResponse response = service.createServiceInstanceBinding(buildCreateRequest());

        assertNotNull(response);

        assertTrue(client.exists(KafkaServiceInstanceBindingService.BINDINGS_PATH + "/" + buildCreateRequest().getBindingId()));

        exception.expect(Exception.class);
        service.createServiceInstanceBinding(buildCreateRequest());


    }

    @Test
    public void getLastOperation() throws Exception {
        assertTrue(true);
    }

    @Test
    public void deleteServiceInstance() throws Exception {

        try{
            client.create(KafkaServiceInstanceBindingService.BINDINGS_PATH+"/"+buildCreateRequest().getBindingId(),
                    mapper.writeValueAsBytes(buildCreateRequest()));
        } catch (Exception e){}

        service.deleteServiceInstanceBinding(buildDeleteRequest());

        assertFalse(client.exists(KafkaServiceInstanceBindingService.BINDINGS_PATH+"/"+buildDeleteRequest().getBindingId()));

        exception.expect(Exception.class);
        service.deleteServiceInstanceBinding(buildDeleteRequest());

    }


    private CreateServiceInstanceBindingRequest buildCreateRequest() {
        return new CreateServiceInstanceBindingRequest(SVC_DEF_ID, SVC_PLAN_ID, "8675309", null)
                .withBindingId(SVC_BINDING_ID);
    }

    private DeleteServiceInstanceBindingRequest buildDeleteRequest() {
        return new DeleteServiceInstanceBindingRequest(ServiceInstanceFixture.getServiceInstance().getServiceInstanceId(),
                SVC_BINDING_ID,SVC_DEF_ID,SVC_PLAN_ID,serviceDefinition);
    }
}