package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

@UtilityClass
public class NodeUtil {
    public Optional<Node> findNode(String location, NodeList nodes) {
        String[] locationSplit = location.split("\\.", 2);
        for (int nodeCounter = 0; nodeCounter < nodes.getLength(); nodeCounter++) {
            Node node = nodes.item(nodeCounter);
            if (node.getLocalName().equals(locationSplit[0])) {
                if (locationSplit.length > 1) {
                    return findNode(locationSplit[1], node.getChildNodes());
                } else {
                    return Optional.of(node);
                }
            }
        }
        return Optional.empty();
    }

}
