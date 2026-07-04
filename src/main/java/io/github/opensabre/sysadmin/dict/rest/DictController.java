package io.github.opensabre.sysadmin.dict.rest;

import io.github.opensabre.sysadmin.dict.model.po.DictItem;
import io.github.opensabre.sysadmin.dict.model.po.DictType;
import io.github.opensabre.sysadmin.dict.model.vo.DictItemOption;
import io.github.opensabre.sysadmin.dict.model.vo.OptionItem;
import io.github.opensabre.sysadmin.dict.model.vo.PageData;
import io.github.opensabre.sysadmin.dict.service.IDictItemService;
import io.github.opensabre.sysadmin.dict.service.IDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "字典管理")
@RestController
@RequestMapping({"/dicts", "/api/v1/dicts", "/v1/dicts"})
public class DictController {

    @Resource
    private IDictTypeService dictTypeService;

    @Resource
    private IDictItemService dictItemService;

    @GetMapping
    @Operation(summary = "字典分页列表")
    public PageData<DictType> page(@RequestParam(defaultValue = "1") long pageNum,
                                   @RequestParam(defaultValue = "10") long pageSize,
                                   @RequestParam(required = false) String keywords,
                                   @RequestParam(required = false) Integer status) {
        return PageData.from(dictTypeService.pageDicts(pageNum, pageSize, keywords, status));
    }

    @GetMapping("/options")
    @Operation(summary = "启用字典选项")
    public List<OptionItem> options() {
        return dictTypeService.listOptions();
    }

    @GetMapping("/{id}/form")
    @Operation(summary = "字典表单数据")
    public DictType getFormData(@PathVariable String id) {
        return dictTypeService.getFormData(id);
    }

    @PostMapping
    @Operation(summary = "新增字典")
    public boolean create(@Valid @RequestBody DictType dictType) {
        return dictTypeService.saveDict(dictType);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改字典")
    public boolean update(@PathVariable String id, @Valid @RequestBody DictType dictType) {
        return dictTypeService.updateDict(id, dictType);
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典")
    public boolean delete(@PathVariable String ids) {
        return dictTypeService.deleteByIds(ids);
    }

    @GetMapping("/{dictCode}/items")
    @Operation(summary = "字典项分页列表")
    public PageData<DictItem> pageItems(@PathVariable String dictCode,
                                        @RequestParam(defaultValue = "1") long pageNum,
                                        @RequestParam(defaultValue = "10") long pageSize,
                                        @RequestParam(required = false) String keywords) {
        return PageData.from(dictItemService.pageItems(dictCode, pageNum, pageSize, keywords));
    }

    @GetMapping("/{dictCode}/items/options")
    @Operation(summary = "启用字典项选项")
    public List<DictItemOption> itemOptions(@PathVariable String dictCode) {
        return dictItemService.listOptions(dictCode);
    }

    @PostMapping("/{dictCode}/items")
    @Operation(summary = "新增字典项")
    public boolean createItem(@PathVariable String dictCode, @Valid @RequestBody DictItem item) {
        return dictItemService.saveItem(dictCode, item);
    }

    @GetMapping("/{dictCode}/items/{id}/form")
    @Operation(summary = "字典项表单数据")
    public DictItem getItemFormData(@PathVariable String dictCode, @PathVariable String id) {
        return dictItemService.getFormData(dictCode, id);
    }

    @PutMapping("/{dictCode}/items/{id}")
    @Operation(summary = "修改字典项")
    public boolean updateItem(@PathVariable String dictCode, @PathVariable String id, @Valid @RequestBody DictItem item) {
        return dictItemService.updateItem(dictCode, id, item);
    }

    @DeleteMapping("/{dictCode}/items/{ids}")
    @Operation(summary = "删除字典项")
    public boolean deleteItems(@PathVariable String dictCode, @PathVariable String ids) {
        return dictItemService.deleteByIds(dictCode, ids);
    }
}
