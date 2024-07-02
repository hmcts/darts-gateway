package uk.gov.hmcts.darts.common.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.darts.common.AccessTokenClient;
import uk.gov.hmcts.darts.properties.AzureAdB2CAuthenticationProperties;

public class AuthConfiguration {
    @Autowired
    private AzureAdB2CAuthenticationProperties azureAdB2CAuthenticationProperties;

    @Bean
    public AccessTokenClient getDartsToken() {
        return new AccessTokenClient(azureAdB2CAuthenticationProperties.getTokenUri(),
                                     azureAdB2CAuthenticationProperties.getScope(),
                                     azureAdB2CAuthenticationProperties.getUsername(),
                                     azureAdB2CAuthenticationProperties.getPassword(),
                                     azureAdB2CAuthenticationProperties.getClientId(),
                                     azureAdB2CAuthenticationProperties.getClientSecret());
    }
}
