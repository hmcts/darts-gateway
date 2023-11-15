package uk.gov.hmcts.darts.authentication.config;

import java.util.List;
import java.util.Optional;

public interface AuthenticationUserToJwtCredentialProperties {
    List<AuthenticationUserToJwt> getUsers();

    void setUsers(List<AuthenticationUserToJwt> users);

    Optional<AuthenticationUserToJwt> getUserToJwtCredentials(String username, String password);
}
