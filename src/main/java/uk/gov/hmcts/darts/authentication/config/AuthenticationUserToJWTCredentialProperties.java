package uk.gov.hmcts.darts.authentication.config;

import java.util.List;
import java.util.Optional;

public interface AuthenticationUserToJWTCredentialProperties {
    List<AuthenticationUserToJWT> getUsers();

    void setUsers(List<AuthenticationUserToJWT> users);

    Optional<AuthenticationUserToJWT> getUserToJWTCredentials(String username, String password);
}
