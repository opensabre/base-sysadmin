package io.github.opensabre.sysadmin.usage.event;

import io.github.opensabre.eda.api.EdaEvent;
import io.github.opensabre.governance.usage.UsageEventTypes;
import io.github.opensabre.governance.usage.UsageOutcome;
import io.github.opensabre.governance.usage.UsageRecord;
import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import io.github.opensabre.sysadmin.usage.service.IUsageSceneService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UsageCounterEdaEventHandlerTest {

    @Test
    void shouldAggregateRemoteUsageRecord() {
        UsageCounterMapper mapper = mock(UsageCounterMapper.class);
        IUsageSceneService sceneService = mock(IUsageSceneService.class);
        when(sceneService.isEnabled("CAPTCHA_SCENE", "login", "CAPTCHA_GENERATE")).thenReturn(true);
        UsageCounterEdaEventHandler handler = new UsageCounterEdaEventHandler(mapper, sceneService);
        UsageRecord record = new UsageRecord("record-1", Instant.parse("2026-07-19T00:00:00Z"), "authorization",
                "CAPTCHA_SCENE", "login", "CAPTCHA_GENERATE", UsageOutcome.SUCCESS);

        handler.handle(EdaEvent.of(UsageEventTypes.EDA_EVENT_TYPE, "authorization", record));

        verify(mapper).increment(anyString(), any(), eq("CAPTCHA_SCENE"), eq("login"),
                eq("CAPTCHA_GENERATE"), eq(0L), eq(1L), eq(0L));
    }

    @Test
    void shouldIgnoreUnregisteredUsageRecord() {
        UsageCounterMapper mapper = mock(UsageCounterMapper.class);
        IUsageSceneService sceneService = mock(IUsageSceneService.class);
        UsageCounterEdaEventHandler handler = new UsageCounterEdaEventHandler(mapper, sceneService);

        handler.record(new UsageRecord("record-2", Instant.now(), "authorization", "REPORT", "daily", "EXPORT", UsageOutcome.SUCCESS));

        verifyNoInteractions(mapper);
    }
}
