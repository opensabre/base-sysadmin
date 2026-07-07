package io.github.opensabre.sysadmin.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.opensabre.sysadmin.notification.dao.NotificationTemplateMapper;
import io.github.opensabre.sysadmin.notification.enums.NotificationType;
import io.github.opensabre.sysadmin.notification.model.po.NotificationTemplateConfig;
import io.github.opensabre.sysadmin.notification.service.INotificationTemplateConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTemplateConfigService
        extends ServiceImpl<NotificationTemplateMapper, NotificationTemplateConfig>
        implements INotificationTemplateConfigService {

    @Override
    public NotificationTemplateConfig getFormData(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public List<NotificationTemplateConfig> listTemplates(String sceneCode, NotificationType channel, Boolean enabled) {
        return this.list(baseQuery(sceneCode, channel, enabled)
                .orderByAsc(NotificationTemplateConfig::getSceneCode)
                .orderByAsc(NotificationTemplateConfig::getSort));
    }

    @Override
    public NotificationTemplateConfig getEnabledTemplate(String sceneCode, NotificationType channel) {
        if (StringUtils.isBlank(sceneCode) || channel == null) {
            return null;
        }
        return this.getOne(baseQuery(sceneCode, channel, true)
                .orderByAsc(NotificationTemplateConfig::getSort)
                .last("limit 1"));
    }

    @Override
    public NotificationTemplateConfig getFirstEnabledTemplate(String sceneCode) {
        if (StringUtils.isBlank(sceneCode)) {
            return null;
        }
        return this.getOne(baseQuery(sceneCode, null, true)
                .orderByAsc(NotificationTemplateConfig::getSort)
                .last("limit 1"));
    }

    @Override
    public boolean saveTemplate(NotificationTemplateConfig template) {
        if (template == null) {
            return false;
        }
        fillDefaults(template);
        return this.save(template);
    }

    @Override
    public boolean updateTemplate(String id, NotificationTemplateConfig template) {
        if (StringUtils.isBlank(id) || template == null) {
            return false;
        }
        fillDefaults(template);
        template.setId(id);
        return this.update(template, new LambdaUpdateWrapper<NotificationTemplateConfig>()
                .eq(NotificationTemplateConfig::getId, id));
    }

    @Override
    public boolean deleteTemplate(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        return this.removeById(id);
    }

    private LambdaQueryWrapper<NotificationTemplateConfig> baseQuery(String sceneCode, NotificationType channel, Boolean enabled) {
        LambdaQueryWrapper<NotificationTemplateConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(sceneCode), NotificationTemplateConfig::getSceneCode, sceneCode);
        queryWrapper.eq(channel != null, NotificationTemplateConfig::getChannel, channel);
        queryWrapper.eq(enabled != null, NotificationTemplateConfig::getEnabled, enabled);
        return queryWrapper;
    }

    private void fillDefaults(NotificationTemplateConfig template) {
        if (template.getEnabled() == null) {
            template.setEnabled(true);
        }
        if (template.getSort() == null) {
            template.setSort(1);
        }
    }
}
