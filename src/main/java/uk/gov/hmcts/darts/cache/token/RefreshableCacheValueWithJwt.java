package uk.gov.hmcts.darts.cache.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import documentum.contextreg.ServiceContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;

@Slf4j
@JsonTypeName("RefreshableCacheValueWithJwt")
public class RefreshableCacheValueWithJwt extends ServiceContextCacheValue implements DownstreamTokenisable {

    @Getter
    private String token;

    @JsonIgnore
    private TokenGeneratable jwtCacheRegistrable;

    public RefreshableCacheValueWithJwt(ServiceContext context, TokenGeneratable registerable) throws CacheException {
        super(context);

        jwtCacheRegistrable = registerable;
        Optional<Token> newtoken = jwtCacheRegistrable.createToken(context);

        if (newtoken.isPresent()) {
            Optional<String> tokenStr = newtoken.get().getToken();
            if (tokenStr.isPresent()) {
                token = tokenStr.orElse(EMPTY_DOWN_STREAM_TOKEN);
            }
        } else {
            token = EMPTY_DOWN_STREAM_TOKEN;
        }
    }

    public RefreshableCacheValueWithJwt(RefreshableCacheValueWithJwt context, TokenGeneratable registerable) throws CacheException {
        super(context);
        jwtCacheRegistrable = registerable;
        this.token = context.token;
    }

    public RefreshableCacheValueWithJwt() {
    }

    @Override
    public boolean refresh() throws CacheException {
        Optional<Token> downstream = getValidatedToken();

        return EMPTY_DOWN_STREAM_TOKEN.equals(getValidatedToken()) || (!EMPTY_DOWN_STREAM_TOKEN.equals(getValidatedToken())
            && downstream.isPresent() && !downstream.get().valid());
    }

    @Override
    public void performRefresh() throws CacheException {
        Optional<Token> token = jwtCacheRegistrable.createToken(getServiceContext());
        setDownstreamToken(token.get().getToken().orElse(EMPTY_DOWN_STREAM_TOKEN));
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
