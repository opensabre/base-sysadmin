package io.github.opensabre.sysadmin.dict.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    private List<T> data;

    private PageMeta page;

    public static <T> PageData<T> from(IPage<T> page) {
        return PageData.<T>builder()
                .data(page.getRecords())
                .page(PageMeta.from(page))
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageMeta {

        private long pageNum;

        private long pageSize;

        private long total;

        public static PageMeta from(IPage<?> page) {
            return PageMeta.builder()
                    .pageNum(page.getCurrent())
                    .pageSize(page.getSize())
                    .total(page.getTotal())
                    .build();
        }
    }
}
