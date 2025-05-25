package uk.gov.hmcts.darts.testutils;

import com.emc.documentum.fs.rt.DfsAttributeHolder;
import com.emc.documentum.fs.rt.DfsExceptionHolder;
import com.emc.documentum.fs.rt.ServiceContextLookupException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.authentication.exception.InvalidIdentitiesFoundException;
import uk.gov.hmcts.darts.authentication.exception.NoIdentitiesFoundException;
import uk.gov.hmcts.darts.authentication.exception.RegisterNullServiceContextException;
import uk.gov.hmcts.darts.cache.AuthSupport;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapTestClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.ContextRegistryParent;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@TestComponent
public class AuthenticationAssertion {

    private static final String EXPECTED_DOCUMENTUM_LOOKUP_CLASS = "com.emc.documentum.fs.rt.ServiceContextLookupException";
    @Setter
    private AuthSupport authSupport;

    void runBlock(GeneralRunnableOperationWithException runnable, Class<?> ex,
                  FaultErrorCodes expectedFaultCode, FaultErrorCodes expectedFaultCodeCause, String invalidToken)
        throws IOException, TransformerException, InterruptedException {
        try {
            runnable.run();
            Assertions.fail("Never expect to get here");
        } catch (SoapFaultClientException e) {

            // THIS CHECK IS TO ENSURE THE LOOKUP EXCEPTION NEVER LEAVES THE DOCUMENTUM NAMESPACE.
            // THE USE OF THE DOCUMENTUM NAMESPACE IS REQUIRED FOR CPP TO CONTINUE FUNCTIONING CORRECTLY
            if (ex.getCanonicalName().equals(ServiceContextLookupException.class.getCanonicalName())) {
                Assertions.assertEquals(EXPECTED_DOCUMENTUM_LOOKUP_CLASS, ServiceContextLookupException.class.getCanonicalName());
            }

            assertErrorResponse(e, ex, expectedFaultCode, expectedFaultCodeCause, invalidToken);
        } catch (JAXBException e) {
            Assertions.fail("JAXBException, never expect to get here");
        }
    }

    public String assertWithTokenHeader(SoapTestClient client, GeneralRunnableOperationWithException runnable, ContextRegistryClient contextClient,
                                        URL baseUrl, String userName, String password) throws IOException, JAXBException, InterruptedException {

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", userName);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);

        contextClient.setHeaderBlock(soapHeaderServiceContextStr);

        String token = ContextRegistryParent.registerToken(baseUrl, contextClient, userName, password);

        lenient().doNothing().when(authSupport).validateToken(token);

        soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderSecurityToken.xml");

        String headerWithToken = soapHeaderServiceContextStr.replace("${TOKEN}", token);
        client.setHeaderBlock(headerWithToken);
        Mockito.clearInvocations(authSupport);
        runnable.run();

        return token;
    }

    public void assertWithUserNameAndPasswordHeader(SoapTestClient client,
                                                    GeneralRunnableOperationWithException runnable, String headerUsername,
                                                    String headerPassword) throws IOException, JAXBException, InterruptedException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", headerUsername);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", headerPassword);

        client.setHeaderBlock(soapHeaderServiceContextStr);

        runnable.run();
    }

    /*
    To be used with Register call as it doesn't use a header.
     */
    public void assertWithNoHeader(GeneralRunnableOperationWithException runnable) throws IOException, JAXBException, InterruptedException {
        runnable.run();
    }

    /*
    To be used with Register call as it doesn't use a header.
     */
    public void assertWithNoHeaderInvalidCredentials(GeneralRunnableOperationWithException runnable)
        throws TransformerException, IOException, InterruptedException {
        runBlock(runnable, AuthenticationFailedException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, FaultErrorCodes.E_UNKNOWN_TOKEN, null);
    }

    public void assertFailBasedOnNoIdentities(SoapTestClient client,
                                              GeneralRunnableOperationWithException runnable) throws IOException, TransformerException, InterruptedException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContextNoIdentities.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);
        runBlock(runnable, NoIdentitiesFoundException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_NO_IDENTITIES, null, null);
    }

    public String assertFailsWithRegisterNullServiceContextException(SoapTestClient client, GeneralRunnableOperationWithException runnable,
                                                                     ContextRegistryClient contextClient, URL baseUrl, String userName, String password)
        throws IOException, JAXBException, InterruptedException, TransformerException {

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

        runBlock(runnable, RegisterNullServiceContextException.class, FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES, null, null);

        return token;
    }

    public void assertFailBasedOnInvalidIdentities(SoapTestClient client,
                                                   GeneralRunnableOperationWithException runnable)
        throws IOException, TransformerException, InterruptedException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContextInvalidIdentities.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        runBlock(runnable, InvalidIdentitiesFoundException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_INVALID_IDENTITIES, null, null);
    }

    public void assertFailBasedOnNotAuthenticatedForUsernameAndPassword(SoapTestClient client,
                                                                        GeneralRunnableOperationWithException runnable, String username,
                                                                        String password) throws IOException, TransformerException, InterruptedException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", username);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);

        client.setHeaderBlock(soapHeaderServiceContextStr);

        runBlock(runnable, AuthenticationFailedException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, null);
    }

    private void assertErrorResponse(SoapFaultClientException faultClientException,
                                     Class<?> ex, FaultErrorCodes code, FaultErrorCodes cause, String messageArgs) throws TransformerException {
        ServiceExceptionType type = getSoapFaultDetails(faultClientException);
        Assertions.assertEquals(faultClientException.getMessage(), type.getMessage());
        Assertions.assertEquals(SoapFaultServiceException.getMessage(code.name(), messageArgs), type.getMessage());
        Assertions.assertEquals(code, FaultErrorCodes.valueOf(type.getMessageId()));

        if (cause != null) {
            DfsExceptionHolder exceptionHolder = type.getExceptionBean().get(0);
            Assertions.assertEquals(cause.name(), exceptionHolder.getMessageId());
            assertAttributeValue(ServiceExceptionType.ATTRIBUTE_MESSAGE_ID,
                                 String.class.getCanonicalName(), cause.name(), exceptionHolder.getAttribute()
            );
        }

        Assertions.assertNotNull(type.getStackTraceAsString());

        // assert the exception block
//TODO        DfsExceptionHolder exceptionHolder = type.getExceptionBean().get(cause == null ? 0 : 1);
//TODO        Assertions.assertEquals(type.getMessageId(), exceptionHolder.getMessageId());
//TODO        Assertions.assertEquals(faultClientException.getMessage(), exceptionHolder.getMessage());
//TODO        Assertions.assertEquals(Exception.class.getCanonicalName(), exceptionHolder.getGenericType());
//TODO        Assertions.assertEquals(ex.getCanonicalName(), exceptionHolder.getExceptionClass());

        // assert the three core attributes
//TODO        assertAttributeValue(ServiceExceptionType.ATTRIBUTE_MESSAGE_ID,
//TODO                             String.class.getCanonicalName(), type.getMessageId(), exceptionHolder.getAttribute()
//TODO        );
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

    public void assertFailBasedOnNotAuthenticatedToken(SoapTestClient client, GeneralRunnableOperationWithException runnable)
        throws IOException, TransformerException, InterruptedException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderSecurityToken.xml");

        String invalidToken = "invalidToken";
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${TOKEN}", invalidToken);
        client.setHeaderBlock(soapHeaderServiceContextStr);

        runBlock(runnable, ServiceContextLookupException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, null, invalidToken);
    }

    public void assertFailsWithServiceAuthorisationFailedError(SoapTestClient client,
                                                               GeneralRunnableOperationWithException runnable,
                                                               String username, String password, FaultErrorCodes cause)
        throws IOException, TransformerException, InterruptedException {

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile("payloads/soapHeaderServiceContext.xml");
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${USER}", username);
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}", password);
        client.setHeaderBlock(soapHeaderServiceContextStr);

        runBlock(runnable, AuthenticationFailedException.class, FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, cause, null);

    }

    public static ServiceExceptionType getSoapFaultDetails(SoapFaultClientException exception) throws TransformerException {
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
        @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
        void run() throws SoapFaultClientException, JAXBException, IOException, InterruptedException;
    }
}
