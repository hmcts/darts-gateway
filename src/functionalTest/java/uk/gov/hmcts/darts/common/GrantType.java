package uk.gov.hmcts.darts.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GrantType {

    PASSWORD("password");

    private final String value;
}
