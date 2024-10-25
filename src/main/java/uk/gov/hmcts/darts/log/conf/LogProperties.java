package uk.gov.hmcts.darts.log.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Component
@ConfigurationProperties("darts-gateway.logging")
@Getter
@Setter
@ToString
@Validated
@Slf4j
public class LogProperties {
    private List<ExcludePayloadLogging> excludePayloadRequestLoggingBasedOnPayloadNamespaceAndTag;

    @SuppressWarnings("AbbreviationAsWordInName")
    public Optional<ExcludePayloadLogging> excludePayload(DOMSource xml) {
        Stream<ExcludePayloadLogging> excludeFromLogging = getExcludePayloadRequestLoggingBasedOnPayloadNamespaceAndTag()
            .stream().filter(payloadFilterDetails -> {
                NamespaceContext ctx = new NamespaceContext() {
                    @Override
                    public String getNamespaceURI(String prefix) {
                        return "docNamespace".equals(prefix) ? payloadFilterDetails.getNamespace() : null;
                    }

                    @Override
                    public Iterator<String> getPrefixes(String val) {
                        return null;
                    }

                    @Override
                    public String getPrefix(String uri) {
                        return null;
                    }
                };

                try {
                    XPathFactory pathFactory = XPathFactory.newInstance();
                    XPath xpath = pathFactory.newXPath();
                    xpath.setNamespaceContext(ctx);

                    if (payloadFilterDetails.getType() != null) {
                        return !xpath.evaluate(
                            "//docNamespace:" + payloadFilterDetails.getTag() + "/type[text()='" + payloadFilterDetails.getType() + "']",
                            xml.getNode()
                        ).isEmpty();
                    } else {
                        return !xpath.evaluate(
                            "//docNamespace:" + payloadFilterDetails.getTag(),
                            xml.getNode()
                        ).isEmpty();
                    }
                } catch (XPathExpressionException pathExpressionException) {
                    log.error("Error when filtering payload from logs", pathExpressionException);
                }

                // always exclude in the eventuality of an xpath error
                return true;
            });

        return excludeFromLogging.findAny();
    }
}
