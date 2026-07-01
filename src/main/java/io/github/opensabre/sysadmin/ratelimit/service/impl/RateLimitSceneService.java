package io.github.opensabre.sysadmin.ratelimit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.ratelimit.dao.RateLimitSceneMapper;
import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;
import io.github.opensabre.sysadmin.ratelimit.service.IRateLimitSceneService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateLimitSceneService extends ServiceImpl<RateLimitSceneMapper, RateLimitScene> implements IRateLimitSceneService {

    @Override
    public RateLimitScene getByCode(String sceneCode) {
        if (StringUtils.isBlank(sceneCode)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<RateLimitScene>()
                .eq(RateLimitScene::getSceneCode, sceneCode)
                .last("limit 1"));
    }

    @Override
    public List<RateLimitScene> list() {
        return super.list();
    }

    @Override
    public List<RateLimitScene> listEnabled() {
        return this.list(new LambdaQueryWrapper<RateLimitScene>()
                .eq(RateLimitScene::isEnabled, true));
    }

    @Override
    public boolean saveScene(RateLimitScene scene) {
        return this.save(scene);
    }

    @Override
    public boolean updateScene(RateLimitScene scene) {
        if (scene == null || StringUtils.isBlank(scene.getSceneCode())) {
            return false;
        }
        return this.update(scene, new LambdaUpdateWrapper<RateLimitScene>()
                .eq(RateLimitScene::getSceneCode, scene.getSceneCode()));
    }

    @Override
    public boolean deleteScene(String sceneCode) {
        return this.remove(new LambdaQueryWrapper<RateLimitScene>().eq(RateLimitScene::getSceneCode, sceneCode));
    }
}
