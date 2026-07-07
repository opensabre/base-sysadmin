package io.github.opensabre.sysadmin.notification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.notification.model.po.NotificationScene;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface NotificationSceneMapper extends BaseMapper<NotificationScene> {
}
