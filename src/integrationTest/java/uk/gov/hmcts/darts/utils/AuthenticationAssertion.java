package uk.gov.hmcts.darts.utils;

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
            ServiceExceptionType type = getSoapFaultDetails(e);
            Assertions.assertEquals(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_INVALID_IDENTITIES, FaultErrorCodes.valueOf(type.getMessageId()));
            Assertions.assertEquals(
                SoapFaultServiceException.getMessage(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_INVALID_IDENTITIES.name()),
                type.getMessage()
            );
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
            ServiceExceptionType type = getSoapFaultDetails(e);
            Assertions.assertEquals(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, FaultErrorCodes.valueOf(type.getMessageId()));
            Assertions.assertEquals(SoapFaultServiceException.getMessage(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED.name()), type.getMessage());
        }
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
            ServiceExceptionType type = getSoapFaultDetails(e);
            Assertions.assertEquals(FaultErrorCodes.E_UNKNOWN_TOKEN, FaultErrorCodes.valueOf(type.getMessageId()));
            Assertions.assertEquals(SoapFaultServiceException.getMessage(FaultErrorCodes.E_UNKNOWN_TOKEN.name(), invalidToken), type.getMessage());
        }

    }

    public ServiceExceptionType getSoapFaultDetails(SoapFaultClientException exception) throws Exception {
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
