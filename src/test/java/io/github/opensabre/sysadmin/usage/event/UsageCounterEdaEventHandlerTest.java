package io.github.opensabre.sysadmin.usage.event;

import io.github.opensabre.eda.api.EdaEvent;
import io.github.opensabre.governance.usage.UsageEventTypes;
import io.github.opensabre.governance.usage.UsageOutcome;
import io.github.opensabre.governance.usage.UsageRecord;
import io.github.opensabre.sysadmin.usage.dao.UsageCounterMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UsageCounterEdaEventHandlerTest {

    @Test
    void shouldAggregateRemoteUsageRecord() {
        UsageCounterMapper mapper = mock(UsageCounterMapper.class);
        UsageCounterEdaEventHandler handler = new UsageCounterEdaEventHandler(mapper);
        UsageRecord record = new UsageRecord("record-1", Instant.parse("2026-07-19T00:00:00Z"), "authorization",
                "CAPTCHA_SCENE", "login", "CAPTCHA_GENERATE", UsageOutcome.SUCCESS);

        handler.handle(EdaEvent.of(UsageEventTypes.EDA_EVENT_TYPE, "authorization", record));

        verify(mapper).increment(anyString(), any(), eq("CAPTCHA_SCENE"), eq("login"),
                eq("CAPTCHA_GENERATE"), eq(0L), eq(1L), eq(0L));
    }
}
