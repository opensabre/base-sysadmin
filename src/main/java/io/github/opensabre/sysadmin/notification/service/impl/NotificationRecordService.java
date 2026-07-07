package io.github.opensabre.sysadmin.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.notification.dao.NotificationRecordMapper;
import io.github.opensabre.sysadmin.notification.enums.NotificationSendStatus;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationRecord;
import io.github.opensabre.sysadmin.notification.service.INotificationRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class NotificationRecordService extends ServiceImpl<NotificationRecordMapper, NotificationRecord>
        implements INotificationRecordService {

    @Override
    public IPage<NotificationRecord> pageRecords(long pageNum, long pageSize, String sceneCode,
                                                 NotificationType channel, NotificationSendStatus status) {
        LambdaQueryWrapper<NotificationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(sceneCode), NotificationRecord::getSceneCode, sceneCode);
        queryWrapper.eq(channel != null, NotificationRecord::getChannel, channel);
        queryWrapper.eq(status != null, NotificationRecord::getStatus, status);
        queryWrapper.orderByDesc(NotificationRecord::getCreatedTime);
        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public NotificationRecord getRecord(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public boolean saveRecord(NotificationRecord record) {
        if (record == null) {
            return false;
        }
        if (record.getRetryCount() == null) {
            record.setRetryCount(0);
        }
        return this.save(record);
    }

    @Override
    public boolean updateRecord(NotificationRecord record) {
        if (record == null || StringUtils.isBlank(record.getId())) {
            return false;
        }
        return this.updateById(record);
    }
}
