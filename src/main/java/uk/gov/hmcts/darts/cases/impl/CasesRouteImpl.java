package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.cases.mapper.GetCasesMapper;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasesRouteImpl implements CasesRoute {

    private final DartsFeignClient dartsFeignClient;

    @Override
    public GetCasesResponse getCases(GetCases getCasesRequest) {

        List<ScheduledCase> modernisedDartsResponse = dartsFeignClient.getCases(
            getCasesRequest.getCourthouse(),
            getCasesRequest.getCourtroom(),
            getCasesRequest.getDate()
        );
        return GetCasesMapper.mapToMojDartsResponse(getCasesRequest, modernisedDartsResponse);
    }
}
