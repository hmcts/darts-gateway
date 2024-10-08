package uk.gov.hmcts.darts.authentication.component;

import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import javax.xml.transform.dom.DOMSource;

public final class ContextRegistryPayload {

    private static final String CONTEXT_REGISTRY_NAMESPACE = "http://services.rt.fs.documentum.emc.com/";

    private ContextRegistryPayload() {

    }

    public enum ContextRegistryOperation {
        REGISTRY_OPERATION("register"), LOOKUP_OPERATION("lookup"), UNREGISTER_OPERATION("unregister");

        private final String soapOperationName;

        ContextRegistryOperation(String soapOperationName) {
            this.soapOperationName = soapOperationName;
        }

        public String getSoapOperationName() {
            return soapOperationName;
        }
    }

    public static boolean isApplicable(SaajSoapMessage message, ContextRegistryOperation contextRegistryOperation) {
        Node bodyNode = ((DOMSource) message.getSoapBody().getPayloadSource()).getNode();
        String messageEndpoint = bodyNode.getLocalName();
        String namespaceUri = bodyNode.getNamespaceURI();

        return messageEndpoint.equals(contextRegistryOperation.soapOperationName) && CONTEXT_REGISTRY_NAMESPACE.equals(namespaceUri);
    }

}
