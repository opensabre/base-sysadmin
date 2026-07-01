package io.github.opensabre.sysadmin.ratelimit.service;

import io.github.opensabre.sysadmin.ratelimit.model.RateLimitScene;

import java.util.List;

public interface IRateLimitSceneService {

    RateLimitScene getByCode(String sceneCode);

    List<RateLimitScene> list();

    List<RateLimitScene> listEnabled();

    boolean saveScene(RateLimitScene scene);

    boolean updateScene(RateLimitScene scene);

    boolean deleteScene(String sceneCode);
}
