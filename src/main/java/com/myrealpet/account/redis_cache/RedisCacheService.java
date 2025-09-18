package com.myrealpet.account.redis_cache;

import java.time.Duration;
import java.util.Set;

public interface RedisCacheService {
    <K, V> void setKeyAndValue(K key, V value);
    <K, V> void setKeyAndValue(K key, V value, Duration timeout);
    <T> T getValueByKey(String key, Class<T> clazz);
    void deleteByKey(String token);

    void setValueWithExpiration(String key, String value, Duration duration);
    String getValue(String key);
    void deleteValue(String key);
    void setExpiration(String key, Duration duration);
    void addToSet(String key, String value);
    void removeFromSet(String key, String value);
    Set<String> getSetMembers(String key);
}
