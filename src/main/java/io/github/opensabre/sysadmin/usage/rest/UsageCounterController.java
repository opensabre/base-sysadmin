package io.github.opensabre.sysadmin.usage.rest;

import io.github.opensabre.sysadmin.usage.enums.UsageEvent;
import io.github.opensabre.sysadmin.usage.enums.UsageGranularity;
import io.github.opensabre.sysadmin.usage.enums.UsageObjectType;
import io.github.opensabre.sysadmin.usage.model.form.UsageTrendQuery;
import io.github.opensabre.sysadmin.usage.model.UsageCounterRequest;
import io.github.opensabre.sysadmin.usage.model.form.UsageCounterEventForm;
import io.github.opensabre.sysadmin.usage.model.vo.UsageTrendVo;
import io.github.opensabre.sysadmin.usage.service.IUsageCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通用对象使用量统计接口。
 */
@Tag(name = "对象使用量")
@RestController
@RequestMapping("/usage-counters")
public class UsageCounterController {

    @Resource
    private IUsageCounterService usageCounterService;

    /**
     * 接收其他应用的对象使用计次上报，并异步写入分钟统计桶。
     *
     * @param form 对象使用事件
     * @return 是否已受理
     */
    @PostMapping("/events")
    @Operation(summary = "上报对象使用计次事件", description = "事件将异步写入使用量统计，不接受敏感业务参数")
    public boolean record(@Valid @RequestBody UsageCounterEventForm form) {
        usageCounterService.record(UsageCounterRequest.builder()
                .objectType(form.getObjectType())
                .objectId(form.getObjectId())
                .usageEvent(form.getUsageEvent())
                .outcome(form.getOutcome())
                .build());
        return true;
    }

    @GetMapping("/trend")
    @Operation(summary = "查询对象使用量趋势")
    public List<UsageTrendVo> trend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "HOUR") UsageGranularity granularity,
            @RequestParam(required = false) UsageObjectType objectType,
            @RequestParam(required = false) String objectId,
            @RequestParam(required = false) UsageEvent usageEvent) {
        UsageTrendQuery query = new UsageTrendQuery();
        query.setFrom(from);
        query.setTo(to);
        query.setGranularity(granularity);
        query.setObjectType(objectType);
        query.setObjectId(objectId);
        query.setUsageEvent(usageEvent);
        return usageCounterService.trend(query);
    }
}
