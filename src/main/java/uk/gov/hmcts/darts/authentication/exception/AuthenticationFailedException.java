package uk.gov.hmcts.darts.authentication.exception;

public class AuthenticationFailedException extends ServiceInvocationException {
    public static final String TYPE = "dfs.authentication.exception";

    public AuthenticationFailedException() {
        super("E_SERVICE_AUTHORIZATION_FAILED", new String[0]);
        this.setExceptionType("dfs.authentication.exception");
    }
}
