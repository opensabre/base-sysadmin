package io.github.opensabre.sysadmin.ratelimit.algorithm;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.storage.RateLimitStorage;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingWindowAlgorithmTest {

    @Test
    void deniesRequestWhenCountExceedsLimitWithinWindow() {
        AtomicLong now = new AtomicLong(1_000L);
        SlidingWindowAlgorithm algorithm = new SlidingWindowAlgorithm(new InMemorySlidingWindowStorage(now));

        RateLimitResult first = algorithm.checkLimit("login:user-1", 2, 60);
        RateLimitResult second = algorithm.checkLimit("login:user-1", 2, 60);
        RateLimitResult third = algorithm.checkLimit("login:user-1", 2, 60);

        assertTrue(first.isAllowed());
        assertEquals(1, first.getRemaining());
        assertTrue(second.isAllowed());
        assertEquals(0, second.getRemaining());
        assertFalse(third.isAllowed());
        assertEquals(3, third.getCurrentCount());
        assertEquals(0, third.getRemaining());
    }

    @Test
    void allowsRequestAfterOldEntriesExpireFromWindow() {
        AtomicLong now = new AtomicLong(1_000L);
        SlidingWindowAlgorithm algorithm = new SlidingWindowAlgorithm(new InMemorySlidingWindowStorage(now));

        algorithm.checkLimit("login:user-2", 2, 60);
        algorithm.checkLimit("login:user-2", 2, 60);

        now.addAndGet(60_001L);
        RateLimitResult result = algorithm.checkLimit("login:user-2", 2, 60);

        assertTrue(result.isAllowed());
        assertEquals(1, result.getCurrentCount());
        assertEquals(1, result.getRemaining());
    }

    private static class InMemorySlidingWindowStorage implements RateLimitStorage {

        private final AtomicLong now;
        private final Map<String, Deque<Long>> requests = new HashMap<>();

        private InMemorySlidingWindowStorage(AtomicLong now) {
            this.now = now;
        }

        @Override
        public RateLimitResult checkSlidingWindow(String key, int maxCount, int period) {
            long currentTime = now.get();
            long windowStart = currentTime - period * 1000L;
            Deque<Long> timestamps = requests.computeIfAbsent(key, ignored -> new ArrayDeque<>());
            while (!timestamps.isEmpty() && timestamps.peekFirst() <= windowStart) {
                timestamps.removeFirst();
            }
            timestamps.addLast(currentTime);
            long currentCount = timestamps.size();
            long resetTime = timestamps.peekFirst() + period * 1000L;
            if (currentCount > maxCount) {
                return RateLimitResult.denied(key, currentCount, maxCount, "当前已超过限次，请稍后再试", resetTime);
            }
            return RateLimitResult.allowed(key, currentCount, maxCount, Math.max(0, maxCount - (int) currentCount), resetTime);
        }

        @Override
        public Long getCount(String key) {
            return null;
        }

        @Override
        public Long increment(String key, long delta) {
            return null;
        }

        @Override
        public Long incrementAndExpire(String key, long delta, long expire) {
            return null;
        }

        @Override
        public void expire(String key, long expire) {
        }

        @Override
        public void set(String key, Object value, long expire) {
        }

        @Override
        public void delete(String key) {
        }

        @Override
        public void delete(String[] keys) {
        }

        @Override
        public Boolean exists(String key) {
            return false;
        }
    }
}
