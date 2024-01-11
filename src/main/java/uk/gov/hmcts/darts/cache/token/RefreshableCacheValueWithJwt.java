package uk.gov.hmcts.darts.cache.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import documentum.contextreg.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;

@Slf4j
@JsonTypeName("RefreshableCacheValueWithJwt")
public class RefreshableCacheValueWithJwt extends ServiceContextCacheValue {

    @JsonIgnore
    private TokenGeneratable jwtCacheRegistrable;

    public RefreshableCacheValueWithJwt(ServiceContext context, TokenGeneratable registerable) throws CacheException {
        super(context);
        jwtCacheRegistrable = registerable;
        Optional<Token> token = jwtCacheRegistrable.createToken(context);

        if (token.isPresent()) {
            Optional<String> tokenStr = token.get().getToken();
            if (tokenStr.isPresent()) {
                setDownstreamToken(tokenStr.get());
            }
        }
    }

    public RefreshableCacheValueWithJwt(RefreshableCacheValueWithJwt context, TokenGeneratable registerable) throws CacheException {
        super(context);
        jwtCacheRegistrable = registerable;
    }

    public RefreshableCacheValueWithJwt() {
    }

    @Override
    public boolean refresh() throws CacheException {
        return EMPTY_DOWN_STREAM_TOKEN.equals(getDownstreamToken()) || (!EMPTY_DOWN_STREAM_TOKEN.equals(getDownstreamToken())
            && !jwtCacheRegistrable.getToken(getDownstreamToken()).valid());
    }

    @Override
    public void performRefresh() throws CacheException {
        Optional<Token> token = jwtCacheRegistrable.createToken(getServiceContext());
        if (token.isPresent()) {
            setDownstreamToken(token.get().getToken().orElse(""));
        }
    }
}
