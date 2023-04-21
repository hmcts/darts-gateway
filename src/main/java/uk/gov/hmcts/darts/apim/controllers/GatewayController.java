package uk.gov.hmcts.darts.apim.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.apim.validate.domain.ValidateResult;
import uk.gov.hmcts.darts.apim.validate.services.ThreateningContentService;
import uk.gov.hmcts.darts.apim.validate.services.ValidateService;
import uk.gov.hmcts.darts.apim.validate.services.XmlComplexityService;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@RestController
public class GatewayController {

    private final List<ValidateService> validateServices;

    @Autowired
    public GatewayController(ThreateningContentService threateningContentService,
                             XmlComplexityService xmlComplexityService) {
        validateServices = new ArrayList<>();
        validateServices.add(threateningContentService);
        validateServices.add(xmlComplexityService);
    }

    @PostMapping(
        value = "/validate",
        consumes = MediaType.TEXT_XML_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValidateResult> validate(@RequestBody String dartsRequest) {

        ValidateResult result = new ValidateResult(true, "");

        ListIterator<ValidateService> serviceListIterator = validateServices.listIterator();
        ValidateService service;

        while (serviceListIterator.hasNext() && result.isValid()) {
            service = serviceListIterator.next();
            result = service.validateContent(dartsRequest);
        }

        return ResponseEntity.ok().body(result);
    }
}
