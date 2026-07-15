package io.github.opensabre.sysadmin.internalmessage.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.internalmessage.model.po.InternalMessageRecipient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InternalMessageRecipientMapper extends BaseMapper<InternalMessageRecipient> {
}
