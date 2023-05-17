package uk.gov.hmcts.darts.cases;

import java.util.List;

public record AddCaseRequest(
    String courthouse,
    String courtroom,
    List<String> defendants,
    List<String> judges,
    List<String> prosecutors,
    List<String> defenders,
    String type,
    String id) {}

