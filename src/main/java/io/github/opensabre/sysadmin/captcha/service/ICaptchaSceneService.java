package io.github.opensabre.sysadmin.captcha.service;

import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;

import java.util.List;

public interface ICaptchaSceneService {

    CaptchaScene getByCode(String sceneCode);

    List<CaptchaScene> list();

    List<CaptchaScene> listEnabled();

    boolean saveScene(CaptchaScene scene);

    boolean updateScene(CaptchaScene scene);

    boolean deleteScene(String sceneCode);
}
