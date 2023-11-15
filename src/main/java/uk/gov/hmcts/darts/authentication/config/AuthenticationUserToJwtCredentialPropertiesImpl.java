package uk.gov.hmcts.darts.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConfigurationProperties("darts-gateway.authentication")
@Getter
@Setter
public class AuthenticationUserToJwtCredentialPropertiesImpl implements AuthenticationUserToJwtCredentialProperties {

    private List<AuthenticationUserToJwt> users;

    @Override
    public Optional<AuthenticationUserToJwt> getUserToJwtCredentials(String username, String password) {
        return users.stream().filter(user -> user.getUserName().equals(username)
            && user.getPassword().equals(password)).findFirst();
    }
}
