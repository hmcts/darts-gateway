package uk.gov.hmcts.darts.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.cache.token.component.impl.OauthTokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.impl.TokenValidatorImpl;
import uk.gov.hmcts.darts.cache.token.config.impl.CachePropertiesImpl;
import uk.gov.hmcts.darts.cache.token.config.impl.SecurityPropertiesImpl;
import uk.gov.hmcts.darts.common.controllers.RootController;
import uk.gov.hmcts.darts.conf.ServiceTestConfiguration;
import uk.gov.hmcts.darts.config.CacheConfig;
import uk.gov.hmcts.darts.event.client.DarNotifyEventClient;
import uk.gov.hmcts.darts.event.config.DarNotifyEventConfiguration;
import uk.gov.hmcts.darts.event.service.impl.DarNotifyEventServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootController.class)
@Import({DarNotifyEventConfiguration.class, DarNotifyEventClient.class,
    DarNotifyEventServiceImpl.class, CachePropertiesImpl.class, CacheConfig.class,
    SecurityPropertiesImpl.class, TokenValidatorImpl.class, OauthTokenGenerator.class, RestTemplate.class,
    ServiceTestConfiguration.class
})
class GetWelcomeTest {

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should welcome upon root request with 200 response code")
    @Test
    void welcomeRootEndpoint() throws Exception {
        MvcResult response = mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn();

        assertThat(response.getResponse().getContentAsString()).startsWith("Welcome");
    }
}
