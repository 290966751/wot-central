package com.wot.common.redis.lock;

import com.wot.common.lock.DistributedLock;
import com.wot.common.lock.ZLock;
import com.wot.exception.BusinessException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.concurrent.TimeUnit;

import static com.wot.common.redis.constant.RedisToolsConstant.LOCK_KEY_PREFIX;
import static com.wot.exception.EnumErrorDesc.PARAM_ERROR;

/**
 * redisson分布式锁实现，基本锁功能的抽象实现
 * 本接口能满足绝大部分的需求，高级的锁功能，请自行扩展或直接使用原生api
 */
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "wot.lock", name = "lockerType", havingValue = "REDIS", matchIfMissing = true)
public class RedissonDistributedLock implements DistributedLock {

    @Autowired
    private RedissonClient redisson;

    private ZLock getLock(String key, boolean isFair) {
        RLock lock;
        if (isFair) {
            lock = redisson.getFairLock(LOCK_KEY_PREFIX + key);
        } else {
            lock = redisson.getLock(LOCK_KEY_PREFIX + key);
        }
        return new ZLock(lock, this);
    }

    @Override
    public ZLock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
        ZLock zLock = getLock(key, isFair);
        RLock lock = (RLock) zLock.getLock();
        lock.lock(leaseTime, unit);
        return zLock;
    }

    @Override
    public ZLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws InterruptedException {
        ZLock zLock = getLock(key, isFair);
        RLock lock = (RLock) zLock.getLock();
        if (lock.tryLock(waitTime, leaseTime, unit)) {
            return zLock;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) {
        if (lock != null) {
            if (lock instanceof RLock) {
                RLock rLock = (RLock) lock;
                if (rLock.isLocked()) {
                    rLock.unlock();
                }
            } else {
                // "requires RLock type"
                throw new BusinessException(PARAM_ERROR);
            }
        }
    }
}
