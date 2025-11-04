package uk.gov.justice.laa.amend.claim.repositories;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.amend.claim.config.CacheProperties;
import uk.gov.justice.laa.amend.claim.models.Claim;

@Repository
public class CacheRepository extends RedisRepository<Claim> {

    private final CacheProperties properties;

    public CacheRepository(RedisTemplate<String, Object> redisTemplate, CacheProperties properties) {
        super(redisTemplate);
        this.properties = properties;
    }

    @Override
    protected long getTimeToLiveInSeconds() {
        return properties.getTimeToLiveInSeconds();
    }
}
