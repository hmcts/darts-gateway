package uk.gov.hmcts.darts;

import documentum.contextreg.Lookup;
import documentum.contextreg.LookupResponse;
import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.Unregister;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapperProvider;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;

class ContextRegistryTest extends FunctionalTestBase {

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientWrapperProvider.class)
    void testRegister(ContextRegistryClientWrapper wrapper) throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();

        SoapAssertionUtil<RegisterResponse> soapAssertionUtil = wrapper.register(register);
        JAXBElement<RegisterResponse> context = soapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientWrapperProvider.class)
    void testLookup(ContextRegistryClientWrapper wrapper) throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();
        Lookup lookup = ContextRegistryClientWrapper.getLookupPayload();

        SoapAssertionUtil<RegisterResponse> soapAssertionUtil = wrapper.register(register);
        wrapper.setToken(soapAssertionUtil.getResponse().getValue().getReturn());

        SoapAssertionUtil<LookupResponse> lookupResponseSoapAssertionUtil = wrapper.lookup(lookup);
        JAXBElement<LookupResponse> context = lookupResponseSoapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue().getReturn());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientWrapperProvider.class)
    @Disabled("flaky test")
    void testUnregister(ContextRegistryClientWrapper wrapper) throws Exception {
        Register register = ContextRegistryClientWrapper.getRegisterPayload();
        Unregister unregister = ContextRegistryClientWrapper.getUnregisterPayload();

        SoapAssertionUtil<RegisterResponse> soapAssertionUtil = wrapper.register(register);
        wrapper.setToken(soapAssertionUtil.getResponse().getValue().getReturn());

        SoapAssertionUtil<UnregisterResponse> unregisterResponseSoapAssertionUtil = wrapper.unregister(unregister);
        JAXBElement<UnregisterResponse> context = unregisterResponseSoapAssertionUtil.getResponse();
        Assertions.assertNotNull(context.getValue());
    }
}
