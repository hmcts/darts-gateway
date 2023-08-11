package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;

public interface CasesRoute {
    GetCasesResponse route(GetCases getCasesRequest);

    void route(AddCase addCase);


}
