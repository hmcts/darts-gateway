package uk.gov.hmcts.darts.utils;

import com.emc.documentum.fs.rt.DfsAttributeHolder;
import com.emc.documentum.fs.rt.DfsExceptionHolder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;
import uk.gov.hmcts.darts.utils.client.SoapTestClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.ContextRegistryParent;

import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class AuthenticationAssertion {

    public String assertWithTokenHeader(SoapTestClient client, GeneralRunnableOperationWithException runnable, ContextRegistryClient contextClient,
                                        URL baseUrl, String userName, String password) throws Exception {

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", userName);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);

        contextClient.setHeaderBlock(soapHeaderServiceContextStr);

        String token = ContextRegistryParent.registerToken(baseUrl, contextClient, userName, password);

        soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderSecurityToken.xml");

        String headerWithToken = soapHeaderServiceContextStr.replace("${TOKEN}", token);
        client.setHeaderBlock(headerWithToken);

        runnable.run();

        return token;
    }

    public void assertWithUserNameAndPasswordHeader(SoapTestClient client,
                                                    GeneralRunnableOperationWithException runnable, String username, String password) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", username);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);

        client.setHeaderBlock(soapHeaderServiceContextStr);

        runnable.run();
    }

    public void assertFailBasedOnNoIdentities(SoapTestClient client,
        GeneralRunnableOperationWithException runnable) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContextNoIdentities.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        try {
            runnable.run();
        } catch (SoapFaultClientException e) {
            assertErrorResponse(e, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_NO_IDENTITIES, "");
        }
    }

    public void assertFailBasedOnInvalidIdentities(SoapTestClient client,
                                              GeneralRunnableOperationWithException runnable) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContextInvalidIdentities.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        try {
            runnable.run();
        } catch (SoapFaultClientException e) {
            assertErrorResponse(e, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_INVALID_IDENTITIES, "");
        }
    }

    public void assertFailBasedOnNotAuthenticatedForUsernameAndPassword(SoapTestClient client,
        GeneralRunnableOperationWithException runnable, String username, String password) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", username);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);

        client.setHeaderBlock(soapHeaderServiceContextStr);

        try {
            runnable.run();
            Assertions.fail("Never expect to get here");
        } catch (SoapFaultClientException e) {
            assertErrorResponse(e, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, "");
        }
    }

    private void assertErrorResponse(SoapFaultClientException faultClientException, FaultErrorCodes code, String messageArgs) throws Exception {
        ServiceExceptionType type = getSoapFaultDetails(faultClientException);
        Assertions.assertEquals(faultClientException.getMessage(), type.getMessage());
        Assertions.assertEquals(SoapFaultServiceException.getMessage(code.name(), messageArgs), type.getMessage());
        Assertions.assertEquals(code, FaultErrorCodes.valueOf(type.getMessageId()));
        Assertions.assertEquals(1, type.getExceptionBean().size());
        Assertions.assertNotNull(type.getStackTraceAsString());

        // assert the exception block
        DfsExceptionHolder exceptionHolder = type.getExceptionBean().get(0);
        Assertions.assertEquals(type.getMessageId(), exceptionHolder.getMessageId());
        Assertions.assertEquals(faultClientException.getMessage(), exceptionHolder.getMessage());
        Assertions.assertEquals(Exception.class.getCanonicalName(), exceptionHolder.getGenericType());
        Assertions.assertEquals("com.emc.documentum.fs.rt.ServiceContextLookupException", exceptionHolder.getExceptionClass());

        // assert the three core attributes
        assertAttributeValue(ServiceExceptionType.ATTRIBUTE_MESSAGE_ID,
                             String.class.getCanonicalName(), type.getMessageId(), exceptionHolder.getAttribute());
        assertAttributeValue(ServiceExceptionType.ATTRIBUTE_MESSAGE_ARGS,
                             String.class.getCanonicalName(), type.getMessageArgs().get(0).toString(), exceptionHolder.getAttribute());
    }

    private void assertAttributeValue(String name, String assertType, String assertValue, List<DfsAttributeHolder> atts) {
        boolean found = false;
        for (DfsAttributeHolder att : atts) {
            if (att.getName().equals(name)) {
                Assertions.assertEquals(assertType, att.getType());
                Assertions.assertEquals(assertValue, att.getValue());
                found = true;
            }
        }

        Assertions.assertTrue(found);
    }

    public void assertFailBasedOnNotAuthenticatedToken(SoapTestClient client, GeneralRunnableOperationWithException runnable) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderSecurityToken.xml");

        String invalidToken = "invalidToken";
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${TOKEN}", invalidToken);
        client.setHeaderBlock(soapHeaderServiceContextStr);

        try {
            runnable.run();
            Assertions.fail("Never expect to get here");
        } catch (SoapFaultClientException e) {
            assertErrorResponse(e, FaultErrorCodes.E_UNKNOWN_TOKEN, invalidToken);
        }
    }

    public void assertFailsWithServiceAuthorisationFailedError(SoapTestClient client,
                                                               GeneralRunnableOperationWithException runnable,
                                                               String username, String password) throws Exception {

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile("payloads/soapHeaderServiceContext.xml");
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", username);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);
        client.setHeaderBlock(soapHeaderServiceContextStr);

        try {
            runnable.run();
            Assertions.fail("Never expect to get here");
        } catch (SoapFaultClientException e) {
            assertErrorResponse(e, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, username);
        }
    }

    public static ServiceExceptionType getSoapFaultDetails(SoapFaultClientException exception) throws Exception {
        SoapFaultDetailElement faultDetailElement = exception.getSoapFault().getFaultDetail().getDetailEntries().next();
        DOMResult result = (DOMResult) faultDetailElement.getResult();

        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(result.getNode()), new StreamResult(writer));

        JAXBContext context;
        ServiceExceptionType clazzInstance;
        try {
            context = JAXBContext.newInstance(ServiceExceptionType.class);
            StringSource ss = new StringSource(writer.toString());
            clazzInstance = context
                .createUnmarshaller()
                .unmarshal(ss, ServiceExceptionType.class).getValue();
        } catch (JAXBException jaxbException) {
            throw new DartsException(jaxbException, CodeAndMessage.INVALID_XML);
        }

        return clazzInstance;
    }

    @FunctionalInterface
    public interface GeneralRunnableOperationWithException {
        void run() throws Exception;
    }
}
