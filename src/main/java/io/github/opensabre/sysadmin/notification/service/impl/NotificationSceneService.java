package io.github.opensabre.sysadmin.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.notification.dao.NotificationSceneMapper;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import io.github.opensabre.sysadmin.notification.service.INotificationSceneService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationSceneService extends ServiceImpl<NotificationSceneMapper, NotificationScene>
        implements INotificationSceneService {

    @Override
    public NotificationScene getByCode(String sceneCode) {
        if (StringUtils.isBlank(sceneCode)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<NotificationScene>()
                .eq(NotificationScene::getSceneCode, sceneCode)
                .last("limit 1"));
    }

    @Override
    public List<NotificationScene> list() {
        return super.list(new LambdaQueryWrapper<NotificationScene>().orderByDesc(NotificationScene::getUpdatedTime));
    }

    @Override
    public List<NotificationScene> listEnabled() {
        return super.list(new LambdaQueryWrapper<NotificationScene>()
                .eq(NotificationScene::getEnabled, true)
                .orderByAsc(NotificationScene::getSceneCode));
    }

    @Override
    public boolean saveScene(NotificationScene scene) {
        if (scene == null) {
            return false;
        }
        if (scene.getEnabled() == null) {
            scene.setEnabled(true);
        }
        return this.save(scene);
    }

    @Override
    public boolean updateScene(NotificationScene scene) {
        if (scene == null || StringUtils.isBlank(scene.getSceneCode())) {
            return false;
        }
        if (scene.getEnabled() == null) {
            scene.setEnabled(true);
        }
        return this.update(scene, new LambdaUpdateWrapper<NotificationScene>()
                .eq(NotificationScene::getSceneCode, scene.getSceneCode()));
    }

    @Override
    public boolean deleteScene(String sceneCode) {
        if (StringUtils.isBlank(sceneCode)) {
            return false;
        }
        return this.remove(new LambdaQueryWrapper<NotificationScene>()
                .eq(NotificationScene::getSceneCode, sceneCode));
    }
}
