package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.GetCases;
import com.synapps.moj.dfs.response.GetCasesResponse;
import com.synapps.moj.dfs.response.DARTSResponse;

public interface CasesRoute {

    GetCasesResponse route(GetCases getCasesRequest);

    DARTSResponse route(AddCase addCase);


}
