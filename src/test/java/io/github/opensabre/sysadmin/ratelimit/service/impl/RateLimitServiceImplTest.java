package io.github.opensabre.sysadmin.ratelimit.service.impl;

import io.github.opensabre.sysadmin.ratelimit.algorithm.SlidingWindowAlgorithm;
import io.github.opensabre.sysadmin.ratelimit.config.RateLimitProperties;
import io.github.opensabre.sysadmin.ratelimit.enums.RateLimitAlgorithmType;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitConfig;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitResult;
import io.github.opensabre.sysadmin.ratelimit.storage.RateLimitStorage;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitServiceImplTest {

    @Test
    void routesSlidingWindowConfigToSlidingWindowAlgorithm() {
        RateLimitServiceImpl service = new RateLimitServiceImpl();
        ReflectionTestUtils.setField(service, "properties", new RateLimitProperties());
        ReflectionTestUtils.setField(service, "slidingWindowAlgorithm",
                new SlidingWindowAlgorithm(new MarkerSlidingWindowStorage()));

        RateLimitResult result = service.checkLimit(RateLimitConfig.builder()
                .key("login:user-1")
                .algorithm(RateLimitAlgorithmType.SLIDING_WINDOW)
                .maxCount(10)
                .period(60)
                .enabled(true)
                .build());

        assertTrue(result.isAllowed());
        assertEquals("ratelimit:login:user-1", result.getKey());
        assertEquals(7, result.getCurrentCount());
    }

    private static class MarkerSlidingWindowStorage implements RateLimitStorage {

        @Override
        public RateLimitResult checkSlidingWindow(String key, int maxCount, int period) {
            return RateLimitResult.allowed(key, 7L, maxCount, 3, 123L);
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
