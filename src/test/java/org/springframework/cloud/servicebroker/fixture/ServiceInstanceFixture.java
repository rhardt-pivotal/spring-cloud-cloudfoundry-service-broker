package org.springframework.cloud.servicebroker.fixture;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.ServiceInstance;

/**
 * Created by rhardt on 11/23/16.
 */
public class ServiceInstanceFixture {

    private static final String SVC_DEF_ID = "serviceDefinitionId";
    private static final String SVC_PLAN_ID = "servicePlanId";

    public static ServiceInstance getServiceInstance() {
        return new ServiceInstance(buildCreateRequest());
    }

    private static CreateServiceInstanceRequest buildCreateRequest() {
        return new CreateServiceInstanceRequest(SVC_DEF_ID, SVC_PLAN_ID, "organizationGuid", "spaceGuid")
                .withServiceInstanceId("8675309");
    }
}