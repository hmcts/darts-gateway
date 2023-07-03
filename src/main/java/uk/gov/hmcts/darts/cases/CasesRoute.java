package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;

public interface CasesRoute {
    GetCasesResponse getCases(GetCases getCasesRequest);
}
