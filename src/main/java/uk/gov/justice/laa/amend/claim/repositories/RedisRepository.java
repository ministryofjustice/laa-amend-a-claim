package uk.gov.justice.laa.amend.claim.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public abstract class RedisRepository<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    protected abstract long getTimeToLiveInSeconds();

    public T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(value, clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to parse value as %s", clazz.getSimpleName()), e);
        }
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, getTimeToLiveInSeconds(), TimeUnit.SECONDS);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
