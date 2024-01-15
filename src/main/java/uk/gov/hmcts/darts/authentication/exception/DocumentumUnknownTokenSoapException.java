package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(
    name = "ServiceContextLookupException",
    namespace = "http://rt.fs.documentum.emc.com/"
)
public class DocumentumUnknownTokenSoapException extends ServiceInvocationException {

    public DocumentumUnknownTokenSoapException(String token) {
        super("E_UNKNOWN_TOKEN", new String[] {token, "central"});
    }
}
