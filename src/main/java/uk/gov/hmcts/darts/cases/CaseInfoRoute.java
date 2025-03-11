package uk.gov.hmcts.darts.cases;

import com.synapps.moj.dfs.response.DARTSResponse;

public interface CaseInfoRoute {

    DARTSResponse handle(String document, String type);
}
