package uk.gov.hmcts.darts.cache.token.service.value.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import documentum.contextreg.ServiceContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;

/**
 * A default implementation of the {@link DownstreamTokenisableValue} that uses {@link TokenGeneratable} to generate
 * tokens.
 */
@Slf4j
@JsonTypeName("RefreshableCacheValueWithJwt")
public class RefeshableTokenCacheValue extends ServiceContextCacheValue implements DownstreamTokenisableValue {

    @Getter
    private String tokenString;

    @JsonIgnore
    private TokenGeneratable jwtCacheRegisterable;

    public RefeshableTokenCacheValue(ServiceContext context, TokenGeneratable registerable) {
        super(context);

        jwtCacheRegisterable = registerable;
        Token newtoken = jwtCacheRegisterable.createToken(context);
        tokenString = newtoken.getTokenString();
    }

    public RefeshableTokenCacheValue(RefeshableTokenCacheValue context, TokenGeneratable registerable) {
        super(context);
        jwtCacheRegisterable = registerable;
        this.tokenString = context.tokenString;
    }

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public RefeshableTokenCacheValue() {
        //Empty constructor
    }

    @Override
    public boolean doesRequireRefresh() {
        Token downstream = getToken();

        boolean tokenValid = downstream.validate(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY);
        String downstreamToken = getDownstreamToken();
        return StringUtils.isEmpty(downstreamToken) || (StringUtils.isNotEmpty(downstreamToken)
                                                                               && !tokenValid);
    }

    @Override
    public void performRefresh() {
        try {
            Token token = jwtCacheRegisterable.createToken(getServiceContext());

            setDownstreamToken(token.getTokenString());
        } catch (CacheTokenCreationException e) {
            log.warn("Failure in refreshing a token continuing to use existing one");
        }
    }

    @JsonIgnore
    @Override
    public Token getToken() {
        return jwtCacheRegisterable.getToken(tokenString);
    }

    @Override
    public void setDownstreamToken(String token) {
        this.tokenString = token;
    }

    @Override
    public String getDownstreamToken() {
        return tokenString;
    }
}
