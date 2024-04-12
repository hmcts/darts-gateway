package uk.gov.hmcts.darts.cache.token.service.value.impl;

import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;

class ServiceContextCacheValueTest {

    private static final String SERVICE_CONTEXT_UNDER_TEST = """
      <context xmlns:ns4="http://profiles.core.datamodel.fs.documentum.emc.com/"    xmlns:ns2="http://context.core.datamodel.fs.documentum.emc.com/" xmlns:ns3="http://properties.core.datamodel.fs.documentum.emc.com/" ><ns2:Identities
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns2:RepositoryIdentity" repositoryName="moj_darts" password="${PASSWORD}" userName="${USER}">
        </ns2:Identities>
        <ns2:Profiles
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns4:ContentTransferProfile" isProcessOLELinks="false" allowAsyncContentTransfer="false" allowCachedContentTransfer="false" transferMode="MTOM">
        </ns2:Profiles>
      </context>
        """;

    private  ServiceContext context;

    @BeforeEach
    void beforeTest() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(ServiceContext.class);
        Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
        StringSource ss = new StringSource(SERVICE_CONTEXT_UNDER_TEST);
        context = jaxbMarshaller.unmarshal(ss, ServiceContext.class).getValue();
    }

    @Test
    void testBasicParsing() throws Exception {
        ServiceContextCacheValue contextCacheValue = new ServiceContextCacheValue(context);
        Assertions.assertFalse(contextCacheValue.getContextString().isEmpty());
        Assertions.assertEquals(TokenRegisterable.CACHE_PREFIX + ":${USER}:${PASSWORD}", contextCacheValue.getSharedKey());
    }
}
