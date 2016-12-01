package org.springframework.cloud.servicebroker.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Component;


/**
 * Created by rhardt on 11/20/16.
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaServiceInstanceService implements ServiceInstanceService {

    public final static String INSTANCES_PATH = "/sb/instances";


    private ZooClient client;


    private ObjectMapper mapper;



    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        String dashboard = "";
        ServiceInstance instance = new ServiceInstance(request);

        if (client.exists(INSTANCES_PATH + "/" + instance.getServiceInstanceId())) {
            throw new IllegalStateException("There's already an instance of this service");
        }
        byte[] body = null;
        try {
            body = mapper.writeValueAsBytes(instance);
        } catch (Exception e) {
            throw new RuntimeException("Could not create json payload");
        }
        client.create(INSTANCES_PATH + "/" + instance.getServiceInstanceId(), body);
        return new CreateServiceInstanceResponse();
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
        return new GetLastServiceOperationResponse().withOperationState(OperationState.SUCCEEDED);
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        byte[] existing = null;
        String instanceId = request.getServiceInstanceId();
        try{
            existing = client.get(INSTANCES_PATH+"/"+instanceId);
            if (existing == null) {
                log.info(String.format("No instance with id: %s", instanceId));
            }
            else {
                log.info("got instance");
                log.info(new String(existing));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            //don't rethrow, just log
            //throw new RuntimeException("Could not read/deserialize json payload");
        }

        if (existing == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        client.delete(INSTANCES_PATH+"/"+instanceId);
        return new DeleteServiceInstanceResponse();
    }

    private ServiceInstance getExisting(String instanceId) {
        ServiceInstance existing = null;
        try{
            byte[] body = client.get(INSTANCES_PATH+"/"+instanceId);
            existing = mapper.readValue(body, ServiceInstance.class);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not read/deserialize json payload");
        }

        if (existing == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }
        return existing;
    }

    @Override
    public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {

        String instanceId = request.getServiceInstanceId();
        ServiceInstance existing = getExisting(instanceId);
        ServiceInstance newInstance = new ServiceInstance(request);

        byte[] body = null;
        try {
            body = mapper.writeValueAsBytes(newInstance);
        } catch (Exception e) {
            throw new RuntimeException("Could not create json payload");
        }
        client.delete(INSTANCES_PATH+"/"+instanceId);
        client.create(INSTANCES_PATH+"/"+instanceId, body);


        return new UpdateServiceInstanceResponse();


    }
}
