package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.GetCases;
import com.synapps.moj.dfs.response.DARTSResponse;
import com.synapps.moj.dfs.response.GetCasesResponse;

public interface CasesRoute {
    GetCasesResponse route(GetCases getCasesRequest);

    DARTSResponse route(AddCase addCase);
}
