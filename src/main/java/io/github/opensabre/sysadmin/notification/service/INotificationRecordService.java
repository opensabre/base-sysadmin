package io.github.opensabre.sysadmin.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;

public interface INotificationRecordService {

    IPage<NotificationRecord> pageRecords(long pageNum, long pageSize, String sceneCode,
                                          NotificationType channel, NotificationSendStatus status);

    NotificationRecord getRecord(String id);

    boolean saveRecord(NotificationRecord record);

    boolean updateRecord(NotificationRecord record);
}
