package uk.gov.hmcts.darts.authentication.exception;

public class UnsupportedIdentitesException  extends ServiceInvocationException {
    public static final String TYPE = "dfs.authentication.exception";

    public UnsupportedIdentitesException(String type) {
        super("E_SERVICE_AUTHORIZATION_FAILED_UNSUPPORTED_IDENTITY_TYPE", type);
        this.setExceptionType("dfs.authentication.exception");
    }
}
