package uk.gov.hmcts.darts.utils.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.JAXBElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

@Getter
public class DartsGatewayAssertionUtil<RESPONSE> {

    private JAXBElement<RESPONSE> response = null;

    DartsGatewayAssertionUtil(JAXBElement<RESPONSE> response) {
        this.response = response;
    }

    public void assertIdenticalResponse(RESPONSE assertion)
            throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(assertion);
        String jsonToCompare = mapper.writeValueAsString(response.getValue());

        System.out.println(json);
        System.out.println(jsonToCompare);

        Assertions.assertEquals(json, jsonToCompare, "Expected web service response to be equal");
    }

    public static void assertErrorResponse(String code, String message, DARTSResponse response)
            throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertEquals(code, response.getCode(), "Expected code to be equal");
        Assertions.assertEquals(message, response.getMessage(), "Expected message to be equal");
    }


}
