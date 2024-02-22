package uk.gov.hmcts.darts.cache.token.config.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.darts.cache.token.config.ExternalUserToInternalUserMapping;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserToInternalUserMappingImpl implements ExternalUserToInternalUserMapping {
    private String userName;

    private String externalPassword;

    private String internalPassword;
}
