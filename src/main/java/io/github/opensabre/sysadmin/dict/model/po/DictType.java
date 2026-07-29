package io.github.opensabre.sysadmin.dict.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字典类型。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_dict_type")
@EqualsAndHashCode(callSuper = true)
public class DictType extends BasePo {

    private String name;

    private String dictCode;

    /**
     * 注册该字典定义的应用；为空表示由管理员维护的存量字典。
     */
    private String sourceApplication;

    private Integer status;

    private String remark;
}
