package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(
    name = "ServiceContextLookupException",
    namespace = "http://rt.fs.documentum.emc.com/"
)
public class DocumentumUnknownTokenSOAPException extends ServiceInvocationException {
    public DocumentumUnknownTokenSOAPException(String token) {
        super("Token \"" + token + "\" not found in registry \"core\". Please make sure you are using the right registry and the token is not expired.\n", "E_UNKNOWN_TOKEN");
    }
}
