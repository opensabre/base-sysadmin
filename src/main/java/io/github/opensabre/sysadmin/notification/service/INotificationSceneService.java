package io.github.opensabre.sysadmin.notification.service;

import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;

import java.util.List;

public interface INotificationSceneService {

    NotificationScene getByCode(String sceneCode);

    List<NotificationScene> list();

    List<NotificationScene> listEnabled();

    boolean saveScene(NotificationScene scene);

    boolean updateScene(NotificationScene scene);

    boolean deleteScene(String sceneCode);
}
