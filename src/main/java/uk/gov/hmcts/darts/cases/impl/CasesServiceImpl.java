package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CasesService;
import uk.gov.hmcts.darts.cases.mapper.GetCasesMapper;
import uk.gov.hmcts.darts.common.client.ModernisedDartsFeignClient;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasesServiceImpl implements CasesService {

    private final ModernisedDartsFeignClient modernisedDartsFeignClient;

    @Override
    public GetCasesResponse getCases(GetCases getCasesRequest) {

        List<ScheduledCase> modernisedDartsResponse = modernisedDartsFeignClient.getCases(
            getCasesRequest.getCourthouse(),
            getCasesRequest.getCourtroom(),
            getCasesRequest.getDate()
        );
        return GetCasesMapper.mapToMojDartsResponse(getCasesRequest, modernisedDartsResponse);
    }
}
