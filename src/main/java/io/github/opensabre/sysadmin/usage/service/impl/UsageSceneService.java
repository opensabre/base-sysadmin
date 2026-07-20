package io.github.opensabre.sysadmin.usage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.usage.dao.UsageSceneMapper;
import io.github.opensabre.sysadmin.usage.model.UsageScene;
import io.github.opensabre.sysadmin.usage.service.IUsageSceneService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/** 计次场景登记实现。 */
@Service
public class UsageSceneService extends ServiceImpl<UsageSceneMapper, UsageScene> implements IUsageSceneService {
    @Override public UsageScene get(String type, String id, String event) {
        if (StringUtils.isAnyBlank(type, id, event)) return null;
        return getOne(wrapper(type, id, event).last("limit 1"));
    }
    @Override public List<UsageScene> list() { return super.list(); }
    @Override public boolean saveScene(UsageScene scene) { return scene != null && get(scene.getObjectType(), scene.getObjectId(), scene.getUsageEvent()) == null && save(scene); }
    @Override public boolean updateScene(UsageScene scene) { return scene != null && get(scene.getObjectType(), scene.getObjectId(), scene.getUsageEvent()) != null && update(scene, wrapper(scene.getObjectType(), scene.getObjectId(), scene.getUsageEvent())); }
    @Override public boolean deleteScene(String type, String id, String event) { return remove(wrapper(type, id, event)); }
    @Override public boolean isEnabled(String type, String id, String event) { UsageScene scene = get(type, id, event); return scene != null && scene.isEnabled(); }
    private LambdaQueryWrapper<UsageScene> wrapper(String type, String id, String event) { return new LambdaQueryWrapper<UsageScene>().eq(UsageScene::getObjectType, type).eq(UsageScene::getObjectId, id).eq(UsageScene::getUsageEvent, event); }
}
