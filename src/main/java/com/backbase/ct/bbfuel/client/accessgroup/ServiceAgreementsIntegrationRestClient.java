package com.backbase.ct.bbfuel.client.accessgroup;

import static org.apache.http.HttpStatus.SC_OK;

import com.backbase.ct.bbfuel.client.common.AbstractRestClient;
import com.backbase.ct.bbfuel.data.CommonConstants;
import com.backbase.integration.accessgroup.rest.spec.v2.accessgroups.serviceagreements.ServiceAgreementGet;
import com.backbase.integration.accessgroup.rest.spec.v2.accessgroups.serviceagreements.ServiceAgreementPostRequestBody;
import com.backbase.integration.accessgroup.rest.spec.v2.accessgroups.serviceagreements.ServiceAgreementPutRequestBody;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class ServiceAgreementsIntegrationRestClient extends AbstractRestClient {

    private static final String ENTITLEMENTS = globalProperties
        .getString(CommonConstants.PROPERTY_ACCESS_CONTROL_BASE_URI);
    private static final String SERVICE_VERSION = "v2";
    private static final String ACCESS_GROUP_INTEGRATION_SERVICE = "accessgroup-integration-service";
    private static final String ENDPOINT_ACCESS_GROUPS = "/accessgroups";
    private static final String ENDPOINT_SERVICE_AGREEMENTS = ENDPOINT_ACCESS_GROUPS + "/serviceagreements";
    private static final String ENDPOINT_SERVICE_AGREEMENTS_BY_ID = ENDPOINT_SERVICE_AGREEMENTS + "/%s";

    public ServiceAgreementsIntegrationRestClient() {
        super(ENTITLEMENTS, SERVICE_VERSION);
        setInitialPath(composeInitialPath());
    }

    public Response ingestServiceAgreement(ServiceAgreementPostRequestBody body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .post(getPath(ENDPOINT_SERVICE_AGREEMENTS));
    }

    public Response updateServiceAgreement(String internalServiceAgreementId, ServiceAgreementPutRequestBody body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .put(getPath(String.format(ENDPOINT_SERVICE_AGREEMENTS_BY_ID, internalServiceAgreementId)));
    }

    public ServiceAgreementGet retrieveServiceAgreementByExternalId(String externalServiceAgreementId) {
        return requestSpec()
            .get(getPath(String.format(ENDPOINT_SERVICE_AGREEMENTS_BY_ID, externalServiceAgreementId)))
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(ServiceAgreementGet.class);
    }

    @Override
    protected String composeInitialPath() {
        return ACCESS_GROUP_INTEGRATION_SERVICE;
    }

}