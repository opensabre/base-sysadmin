package io.github.opensabre.sysadmin.usage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.sysadmin.usage.model.UsageScene;

import java.util.List;

/** 计次场景登记管理服务。 */
public interface IUsageSceneService {
    UsageScene get(String objectType, String objectId, String usageEvent);
    IPage<UsageScene> page(long pageNum, long pageSize, String keywords, Boolean enabled);
    List<UsageScene> list();
    boolean saveScene(UsageScene scene);
    boolean updateScene(UsageScene scene);
    boolean deleteScene(String objectType, String objectId, String usageEvent);
    boolean isEnabled(String objectType, String objectId, String usageEvent);
}
