package org.springframework.cloud.servicebroker.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of ServiceInstanceBindingService for service brokers that do not support bindable services.
 *
 * See http://docs.cloudfoundry.org/services/api.html#binding
 *
 * @author Scott Frederick, Vinicius Carvalho, Rob Hardt
 */
@Component
public class KafkaServiceInstanceBindingService implements ServiceInstanceBindingService {

	@Autowired
	private ZooClient client;

	@Autowired
	private Environment env;

	@Autowired
	private ObjectMapper mapper;


	public final static String BINDINGS_PATH = "/sb/bindings";


	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {

		String bindingId = request.getBindingId();
		String instanceId = request.getServiceInstanceId();

		if(client.exists(BINDINGS_PATH+"/"+bindingId)){
			throw new IllegalStateException("Binding Already exists");
		}

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("zookeeper", env.getProperty("kafka.host")+":2181");
		credentials.put("brokers",getBrokers());

		ServiceInstanceBinding sib = new ServiceInstanceBinding(
				bindingId,
				instanceId,
				credentials,
				null,
				request.getAppGuid()
		);

		byte[] body = null;
		try {
			body = mapper.writeValueAsBytes(sib);
		} catch (Exception e) {
			throw new RuntimeException("Could not create json payload");
		}
		client.create(BINDINGS_PATH+"/"+sib.getId(),body);

		return new CreateServiceInstanceAppBindingResponse().withCredentials(credentials);

	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {

		byte[] body = client.get(BINDINGS_PATH+"/"+request.getBindingId());
		if(body == null) {
			throw new ServiceInstanceBindingDoesNotExistException(request.getBindingId());
		}
		client.delete(BINDINGS_PATH+"/"+request.getBindingId());

	}


	private String[] getBrokers(){
		ArrayList<String> list = new ArrayList<>();
		List<String> brokerList = client.list("/brokers/ids");
		for(String brokerId : brokerList){
			try {
				Map<String,Object> broker = mapper.readValue(client.get("/brokers/ids/"+brokerId), Map.class);
				String b = broker.get("host")+":"+broker.get("port");
				list.add(b);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list.toArray(new String[list.size()]);
	}


}
