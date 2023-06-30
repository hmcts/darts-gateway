package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;

public interface CasesService {
    GetCasesResponse getCases(GetCases getCasesRequest);
}
