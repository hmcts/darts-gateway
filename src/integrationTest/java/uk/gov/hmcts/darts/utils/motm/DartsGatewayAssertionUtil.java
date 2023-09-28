package uk.gov.hmcts.darts.utils.motm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.Getter;
import org.bouncycastle.cert.ocsp.Req;
import org.junit.jupiter.api.Assertions;
import org.springframework.oxm.Marshaller;
import org.xmlunit.matchers.CompareMatcher;
import uk.gov.hmcts.darts.utils.TestUtils;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

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
