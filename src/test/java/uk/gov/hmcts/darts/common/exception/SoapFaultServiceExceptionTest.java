package uk.gov.hmcts.darts.common.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

class SoapFaultServiceExceptionTest {

    @Test
    void testExceptionWithFaultCause() {
        FaultErrorCodes faultErrorCodesCause = FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_NO_IDENTITIES;
        SoapFaultServiceException exception = new SoapFaultServiceException(faultErrorCodesCause, null, "");

        FaultErrorCodes faultErrorCodes = FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES;
        SoapFaultServiceException exceptionWithCause = new SoapFaultServiceException(faultErrorCodes, exception, "test");

        Assertions.assertEquals(faultErrorCodes.name(), exceptionWithCause.getServiceExceptionType().getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null", exceptionWithCause.getServiceExceptionType().getMessage());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType().getMessageArgs().get(0));
        Assertions.assertEquals(2, exceptionWithCause.getServiceExceptionType()
            .getExceptionBean().size());
        Assertions.assertEquals(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_NO_IDENTITIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getMessageId());
        Assertions.assertEquals("Authorization failed, could not find identities in service context",
                                exceptionWithCause.getServiceExceptionType().getExceptionBean()
                                    .get(0).getMessage());
        Assertions.assertEquals(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_NO_IDENTITIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getAttribute().get(2).getValue());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType().getExceptionBean()
            .get(0).getAttribute().get(1).getValue());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(1).getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null",
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(1).getMessage());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(1).getAttribute().get(2).getValue());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType()
            .getExceptionBean().get(1).getAttribute().get(1).getValue());
    }

    @Test
    void testExceptionWithCause() {
        Exception exception = new Exception();

        FaultErrorCodes faultErrorCodes = FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES;
        SoapFaultServiceException exceptionWithCause = new SoapFaultServiceException(faultErrorCodes, exception, "test");

        Assertions.assertEquals(faultErrorCodes.name(), exceptionWithCause.getServiceExceptionType().getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null", exceptionWithCause.getServiceExceptionType().getMessage());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType().getMessageArgs().get(0));
        Assertions.assertEquals(2, exceptionWithCause.getServiceExceptionType().getExceptionBean().size());
        Assertions.assertEquals(FaultErrorCodes.E_UNKNOWN_TOKEN.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean()
                                    .get(0).getMessageId());
        Assertions.assertEquals("Token \"test\" not found in registry central. " +
                                    "Please make sure you are using the right registry and the token " +
                                    "is not expired.", exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getMessage());
        Assertions.assertEquals(FaultErrorCodes.E_UNKNOWN_TOKEN.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getAttribute().get(2).getValue());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getAttribute().get(1).getValue());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(1).getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null", exceptionWithCause.getServiceExceptionType().getExceptionBean().get(1).getMessage());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean()
                                    .get(1).getAttribute().get(2).getValue());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType()
            .getExceptionBean().get(1).getAttribute().get(1).getValue());
    }

    @Test
    void testException() {
        FaultErrorCodes faultErrorCodes = FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES;
        SoapFaultServiceException exceptionWithCause = new SoapFaultServiceException(faultErrorCodes, null, "test");

        Assertions.assertEquals(faultErrorCodes.name(), exceptionWithCause.getServiceExceptionType().getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null", exceptionWithCause.getServiceExceptionType().getMessage());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType()
            .getMessageArgs().get(0));
        Assertions.assertEquals(1, exceptionWithCause.getServiceExceptionType()
            .getExceptionBean().size());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getMessageId());
        Assertions.assertEquals("Provided ServiceContext is null", exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getMessage());
        Assertions.assertEquals(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES.name(),
                                exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getAttribute().get(2).getValue());
        Assertions.assertEquals("test", exceptionWithCause.getServiceExceptionType().getExceptionBean().get(0).getAttribute().get(1).getValue());
    }
}
