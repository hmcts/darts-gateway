package uk.gov.hmcts.darts.cases.impl;

import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.addcase.AddCaseResponse;
import com.service.mojdarts.synapps.com.addcase.NewDataSet;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.cases.mapper.AddCaseMapper;
import uk.gov.hmcts.darts.cases.mapper.GetCasesMapper;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.ws.DartsResponseUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CasesRouteImpl implements CasesRoute {

    private final DartsFeignClient dartsFeignClient;
    private final AddCaseMapper addCaseMapper;
    private final DartsResponseUtils responseUtils;

    @Override
    public GetCasesResponse getCases(GetCases getCasesRequest) {

        List<ScheduledCase> modernisedDartsResponse = dartsFeignClient.getCases(
            getCasesRequest.getCourthouse(),
            getCasesRequest.getCourtroom(),
            getCasesRequest.getDate()
        );
        return GetCasesMapper.mapToMojDartsResponse(getCasesRequest, modernisedDartsResponse);
    }

    @Override
    public AddCaseResponse addCase(NewDataSet addCaseRequest) {
        uk.gov.hmcts.darts.model.cases.AddCaseRequest mojDartsAddCaseRequest =
            addCaseMapper.mapToMojDartsAddCaseRequest(addCaseRequest);

        try {
            dartsFeignClient.addCase(mojDartsAddCaseRequest);
        } catch (FeignException.FeignClientException e) {
            return responseUtils.createErrorAddCaseResponse(e);
        }

        return responseUtils.createSuccessfulAddCaseResponse();
    }
}
