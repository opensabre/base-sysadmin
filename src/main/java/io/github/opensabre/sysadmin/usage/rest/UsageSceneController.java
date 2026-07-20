package io.github.opensabre.sysadmin.usage.rest;

import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.sysadmin.dict.model.vo.PageData;
import io.github.opensabre.sysadmin.usage.model.UsageScene;
import io.github.opensabre.sysadmin.usage.service.IUsageSceneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/** 维护允许进入使用量统计的场景目录。 */
@Tag(name = "计次场景")
@RestController
@RequestMapping("/usage-scenes")
public class UsageSceneController {
    @Resource private IUsageSceneService usageSceneService;
    @GetMapping @Operation(summary = "分页查询计次场景")
    public PageData<UsageScene> page(@RequestParam(defaultValue = "1") long pageNum,
                                     @RequestParam(defaultValue = "10") long pageSize,
                                     @RequestParam(required = false) String keywords,
                                     @RequestParam(required = false) Boolean enabled) {
        return PageData.from(usageSceneService.page(pageNum, pageSize, keywords, enabled));
    }
    @PostMapping @Operation(summary = "创建计次场景") @Audit(operationType = OperationType.CREATE, description = "创建计次场景", module = "USAGE_SCENE", response = true)
    public boolean create(@RequestBody UsageScene scene) { return usageSceneService.saveScene(scene); }
    @PutMapping @Operation(summary = "更新计次场景") @Audit(operationType = OperationType.UPDATE, description = "更新计次场景", module = "USAGE_SCENE", response = true)
    public boolean update(@RequestBody UsageScene scene) { return usageSceneService.updateScene(scene); }
    @DeleteMapping @Operation(summary = "删除计次场景") @Audit(operationType = OperationType.DELETE, description = "删除计次场景", module = "USAGE_SCENE", response = true)
    public boolean delete(@RequestParam String objectType, @RequestParam String objectId, @RequestParam String usageEvent) { return usageSceneService.deleteScene(objectType, objectId, usageEvent); }
}
