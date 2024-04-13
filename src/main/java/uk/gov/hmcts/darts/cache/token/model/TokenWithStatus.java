package uk.gov.hmcts.darts.cache.token.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.darts.cache.token.enums.TokenStatus;
import uk.gov.hmcts.darts.cache.token.service.Token;

@Getter
@Setter
@NoArgsConstructor
public class TokenWithStatus {

    Token token;
    TokenStatus status;

}
