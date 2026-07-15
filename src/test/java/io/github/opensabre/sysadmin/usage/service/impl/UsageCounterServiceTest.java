package io.github.opensabre.sysadmin.usage.service.impl;

import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.event.UsageCounterEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.enums.UsageOutcome;
import io.github.opensabre.sysadmin.usage.model.UsageCounterRequest;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.vo.UsageTrendVo;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsageCounterServiceTest {

    private final UsageCounterMapper mapper = mock(UsageCounterMapper.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final UsageCounterService service = new UsageCounterService(mapper, eventPublisher);

    @Test
    void publishesTheRequestedOutcomeAsAnAsyncEvent() {
        service.record(UsageCounterRequest.builder()
                .objectType(UsageObjectType.CAPTCHA_SCENE)
                .objectId("LOGIN_SMS")
                .usageEvent(UsageEvent.CAPTCHA_GENERATE)
                .outcome(UsageOutcome.SUCCESS)
                .build());

        var event = forClass(UsageCounterEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue())
                .extracting(UsageCounterEvent::objectType, UsageCounterEvent::objectId,
                        UsageCounterEvent::usageEvent, UsageCounterEvent::outcome)
                .containsExactly(UsageObjectType.CAPTCHA_SCENE, "LOGIN_SMS", UsageEvent.CAPTCHA_GENERATE, UsageOutcome.SUCCESS);
    }

    @Test
    void calculatesSuccessRateFromAttemptsOnly() {
        UsageTrendVo trend = new UsageTrendVo();
        trend.setAttemptCount(4L);
        trend.setSuccessCount(3L);
        trend.setFailureCount(1L);
        when(mapper.trend(any())).thenReturn(List.of(trend));

        List<UsageTrendVo> result = service.trend(query(UsageGranularity.HOUR));

        assertThat(result).singleElement().extracting(UsageTrendVo::getSuccessRate).isEqualTo(new BigDecimal("0.7500"));
    }

    @Test
    void rejectsMinuteRangeLongerThanOneDay() {
        UsageTrendQuery query = query(UsageGranularity.MINUTE);
        query.setTo(query.getFrom().plusHours(25));

        assertThatIllegalArgumentException().isThrownBy(() -> service.trend(query));
    }

    private UsageTrendQuery query(UsageGranularity granularity) {
        UsageTrendQuery query = new UsageTrendQuery();
        query.setFrom(LocalDateTime.of(2026, 7, 15, 10, 0));
        query.setTo(LocalDateTime.of(2026, 7, 15, 11, 0));
        query.setGranularity(granularity);
        return query;
    }
}
