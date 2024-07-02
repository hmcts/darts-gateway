package uk.gov.hmcts.darts;

import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;

public class TestContextRegistry extends FunctionalTest {

    @Test
    public void testRegisterForXhibit() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = xhibit.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @Test
    public void testLookupForXhibit() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = xhibit.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @Test
    public void testRegisterForViq() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = viq.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @Test
    public void testLookupForViq() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = viq.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @Test
    public void testRegisterForCpp() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = cpp.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @Test
    public void testLookupForCpp() throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse> soapAssertionUtil = cpp.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }
}
