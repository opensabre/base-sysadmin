package io.github.opensabre.sysadmin.captcha.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.opensabre.sysadmin.captcha.model.po.CaptchaScene;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaptchaSceneMapper extends BaseMapper<CaptchaScene> {
}
