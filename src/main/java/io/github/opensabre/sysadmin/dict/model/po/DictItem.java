package io.github.opensabre.sysadmin.dict.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字典项。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("base_sys_dict_item")
@EqualsAndHashCode(callSuper = true)
public class DictItem extends BasePo {

    private String dictCode;

    private String label;

    private String value;

    private Integer status;

    private Integer sort;

    private String tagType;
}
