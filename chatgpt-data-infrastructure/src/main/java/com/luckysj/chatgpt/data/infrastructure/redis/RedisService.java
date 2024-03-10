package com.luckysj.chatgpt.data.infrastructure.redis;

import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service("redissonService")
public class RedisService implements IRedisService{
    @Resource
    private RedissonClient redissonClient;

    public <T> void setValue(String key, T value) {
        redissonClient.<T>getBucket(key).set(value);
    }

    @Override
    public <T> void setValue(String key, T value, long expired) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, Duration.ofMillis(expired));
    }

    public <T> T getValue(String key) {
        return redissonClient.<T>getBucket(key).get();
    }

    @Override
    public <T> RQueue<T> getQueue(String key) {
        return redissonClient.getQueue(key);
    }

    @Override
    public <T> RBlockingQueue<T> getBlockingQueue(String key) {
        return redissonClient.getBlockingQueue(key);
    }

    @Override
    public <T> RDelayedQueue<T> getDelayedQueue(RBlockingQueue<T> rBlockingQueue) {
        return redissonClient.getDelayedQueue(rBlockingQueue);
    }

    @Override
    public long incr(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public long incrBy(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(delta);
    }

    @Override
    public long decr(String key) {
        return redissonClient.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public long decrBy(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(-delta);
    }

    @Override
    public void remove(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    public void addToSet(String key, String value) {
        RSet<String> set = redissonClient.getSet(key);
        set.add(value);
    }

    public boolean isSetMember(String key, String value) {
        RSet<String> set = redissonClient.getSet(key);
        return set.contains(value);
    }

    public void addToList(String key, String value) {
        RList<String> list = redissonClient.getList(key);
        list.add(value);
    }

    public String getFromList(String key, int index) {
        RList<String> list = redissonClient.getList(key);
        return list.get(index);
    }

    public void addToMap(String key, String field, String value) {
        RMap<String, String> map = redissonClient.getMap(key);
        map.put(field, value);
    }

    public String getFromMap(String key, String field) {
        RMap<String, String> map = redissonClient.getMap(key);
        return map.get(field);
    }

    public void addToSortedSet(String key, String value) {
        RSortedSet<String> sortedSet = redissonClient.getSortedSet(key);
        sortedSet.add(value);
    }

    @Override
    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    @Override
    public RLock getFairLock(String key) {
        return redissonClient.getFairLock(key);
    }

    @Override
    public RReadWriteLock getReadWriteLock(String key) {
        return redissonClient.getReadWriteLock(key);
    }

    @Override
    public RSemaphore getSemaphore(String key) {
        return redissonClient.getSemaphore(key);
    }

    @Override
    public RPermitExpirableSemaphore getPermitExpirableSemaphore(String key) {
        return redissonClient.getPermitExpirableSemaphore(key);
    }

    @Override
    public RCountDownLatch getCountDownLatch(String key) {
        return redissonClient.getCountDownLatch(key);
    }

    @Override
    public <T> RBloomFilter<T> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

}
