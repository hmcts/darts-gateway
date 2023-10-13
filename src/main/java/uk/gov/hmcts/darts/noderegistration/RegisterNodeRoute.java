package uk.gov.hmcts.darts.noderegistration;

import com.service.mojdarts.synapps.com.RegisterNode;
import com.service.mojdarts.synapps.com.registernode.Node;
import com.synapps.moj.dfs.response.RegisterNodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.client.RegisterNodeClient;
import uk.gov.hmcts.darts.model.noderegistration.PostNodeRegistrationResponse;
import uk.gov.hmcts.darts.noderegistration.mapper.RegisterNodeMapper;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;

@Service
@RequiredArgsConstructor
public class RegisterNodeRoute {
    @Value("${darts-gateway.register-node.schema}")
    private String addCaseSchemaPath;
    @Value("${darts-gateway.register-node.validate}")
    private boolean validateAddCase;

    private final XmlValidator xmlValidator;
    private final XmlParser xmlParser;

    private final RegisterNodeClient registerNodeClient;
    private final RegisterNodeMapper registerNodeMapper;

    public RegisterNodeResponse route(RegisterNode registerNode) {
        var caseDocumentXmlStr = registerNode.getDocument();
        if (validateAddCase) {
            xmlValidator.validate(caseDocumentXmlStr, addCaseSchemaPath);
        }

        var registerNodeObj = xmlParser.unmarshal(caseDocumentXmlStr, Node.class);

        ResponseEntity<PostNodeRegistrationResponse> registerNodeResponse =
            registerNodeClient.registerDevicesPost(registerNodeObj.getType(), registerNodeObj.getCourthouse(),
                                                                           registerNodeObj.getCourtroom(), registerNodeObj.getHostname(),
                                                                           registerNodeObj.getIpAddress(), registerNodeObj.getMacAddress());

        return registerNodeMapper.mapToDfsResponse(registerNodeResponse.getBody());
    }
}
