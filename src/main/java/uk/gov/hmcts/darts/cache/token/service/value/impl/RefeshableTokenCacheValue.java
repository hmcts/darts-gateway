package uk.gov.hmcts.darts.cache.token.service.value.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import documentum.contextreg.ServiceContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;

import java.util.Optional;

@Slf4j
@JsonTypeName("RefreshableCacheValueWithJwt")
public class RefeshableTokenCacheValue extends ServiceContextCacheValue implements DownstreamTokenisableValue {

    @Getter
    private String token;

    @JsonIgnore
    private TokenGeneratable jwtCacheRegistrable;

    public RefeshableTokenCacheValue(ServiceContext context, TokenGeneratable registerable) throws CacheException {
        super(context);

        jwtCacheRegistrable = registerable;
        Token newtoken = jwtCacheRegistrable.createToken(context);
        token = newtoken.getToken(false).orElse(EMPTY_DOWN_STREAM_TOKEN);
    }

    public RefeshableTokenCacheValue(RefeshableTokenCacheValue context, TokenGeneratable registerable) throws CacheException {
        super(context);
        jwtCacheRegistrable = registerable;
        this.token = context.token;
    }

    public RefeshableTokenCacheValue() {
    }

    @Override
    public boolean refresh() throws CacheException {
        Optional<Token> downstream = getValidatedToken();

        return EMPTY_DOWN_STREAM_TOKEN.equals(getDownstreamToken()) || (!EMPTY_DOWN_STREAM_TOKEN.equals(getDownstreamToken())
            && downstream.isPresent() && !downstream.get().valid());
    }

    @Override
    public void performRefresh() throws CacheException {
        try {
            Token token = jwtCacheRegistrable.createToken(getServiceContext());

            setDownstreamToken(token.getToken(false).orElse(EMPTY_DOWN_STREAM_TOKEN));
        } catch (CacheTokenCreationException e) {
            log.warn("Failure in refreshing a token continuing to use existing one");
        }
    }

    @Override
    @JsonIgnore
    public Optional<Token> getValidatedToken() {
        if (!token.equals(EMPTY_DOWN_STREAM_TOKEN)) {
            return Optional.of(jwtCacheRegistrable.getToken(token));
        }

        return Optional.empty();
    }

    public void setDownstreamToken(String token) {
        this.token = token;
    }

    @Override
    public String getDownstreamToken() {
        return token;
    }
}
