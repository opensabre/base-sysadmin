package io.github.opensabre.sysadmin.captcha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.captcha.dao.CaptchaSceneMapper;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import io.github.opensabre.sysadmin.captcha.service.ICaptchaSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CaptchaSceneService extends ServiceImpl<CaptchaSceneMapper, CaptchaScene> implements ICaptchaSceneService {

    @Override
    public CaptchaScene getByCode(String sceneCode) {
        if (StringUtils.isBlank(sceneCode)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<CaptchaScene>()
                .eq(CaptchaScene::getSceneCode, sceneCode)
                .last("limit 1"));
    }

    @Override
    public List<CaptchaScene> list() {
        return super.list();
    }

    @Override
    public List<CaptchaScene> listEnabled() {
        return this.list(new LambdaQueryWrapper<CaptchaScene>()
                .eq(CaptchaScene::isEnabled, true));
    }

    @Override
    public boolean saveScene(CaptchaScene scene) {
        return this.save(scene);
    }

    @Override
    public boolean updateScene(CaptchaScene scene) {
        if (scene == null || StringUtils.isBlank(scene.getSceneCode())) {
            return false;
        }
        return this.update(scene, new LambdaUpdateWrapper<CaptchaScene>()
                .eq(CaptchaScene::getSceneCode, scene.getSceneCode()));
    }

    @Override
    public boolean deleteScene(String sceneCode) {
        return this.remove(new LambdaQueryWrapper<CaptchaScene>().eq(CaptchaScene::getSceneCode, sceneCode));
    }
}
