package com.myrealpet.account.redis_cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    final private StringRedisTemplate redisTemplate;

    @Override
    public <K, V> void setKeyAndValue(K key, V value) {
        setKeyAndValue(key, value, Duration.ofMinutes(720));
    }

    @Override
    public <K, V> void setKeyAndValue(K key, V value, Duration timeout) {
        String keyAsString = String.valueOf(key);
        String valueAsString = String.valueOf(value);

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(keyAsString, valueAsString, timeout);
    }

    @Override
    public <T> T getValueByKey(String key, Class<T> clazz) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(key);

        if(value == null) {
            return null;
        }

        if(clazz == String.class) {
            return clazz.cast(value);
        }

        if(clazz == Integer.class) {
            return clazz.cast(Integer.valueOf(value));
        }

        if(clazz == Long.class) {
            return clazz.cast(Long.valueOf(value));
        }

        throw new IllegalArgumentException("Unsupported class: " + clazz);
    }

    @Override
    public void deleteByKey(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public void setValueWithExpiration(String key, String value, Duration duration) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(key, value, duration);
    }

    @Override
    public String getValue(String key) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(key);
    }

    @Override
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void setExpiration(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    @Override
    public void addToSet(String key, String value) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.add(key, value);
    }

    @Override
    public void removeFromSet(String key, String value) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        setOps.remove(key, value);
    }

    @Override
    public Set<String> getSetMembers(String key) {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        return setOps.members(key);
    }
}
