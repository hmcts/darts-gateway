package uk.gov.hmcts.darts.cases;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.addcase.AddCaseResponse;
import com.service.mojdarts.synapps.com.addcase.NewDataSet;

public interface CasesRoute {
    GetCasesResponse getCases(GetCases getCasesRequest);

    AddCaseResponse addCase(NewDataSet addCaseRequest);


}
