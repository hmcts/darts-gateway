package uk.gov.hmcts.darts.noderegistration.mapper;

import com.synapps.moj.dfs.response.RegisterNodeResponse;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.model.noderegistration.PostNodeRegistrationResponse;

@Service
public class RegisterNodeMapper {

    public RegisterNodeResponse mapToDfsResponse(PostNodeRegistrationResponse modernizedResponse) {
        RegisterNodeResponse registerNodeResponse = new RegisterNodeResponse();
        registerNodeResponse.setNodeId(modernizedResponse.getNodeId().toString());
        registerNodeResponse.setCode("200");
        registerNodeResponse.setMessage("OK");
        return registerNodeResponse;
    }
}
