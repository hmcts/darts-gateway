package uk.gov.hmcts.darts.authentication.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationUserToJwt {
    private String userName;

    private String password;

    private String jwtUserName;

    private String jwtPassword;
}
