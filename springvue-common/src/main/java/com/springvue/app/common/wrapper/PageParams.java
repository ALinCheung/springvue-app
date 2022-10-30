package com.springvue.app.common.wrapper;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页查询参数
 */
@ApiModel(description = "分页查询入参")
@Data
public class PageParams<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页", example = "1")
    private int current = 1;

    @ApiModelProperty(value = "每页记录数", example = "20")
    private int size = 20;

    @ApiModelProperty("排序规则")
    private List<OrderItem> orders;

    private T param;

    public PageParams() {
    }

    public PageParams(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public Page page() {
        return this.page(true);
    }

    /**
     * 获取分页参数
     *
     * @param isSearchCount 是否计算总数
     * @return
     */
    public Page page(boolean isSearchCount) {
        Page page = new Page(this.getCurrent(), this.getSize(), isSearchCount);
        page.setOrders(this.getOrders());
        return page;
    }

    /**
     * 将数据按分页返回
     *
     * @param list
     * @return
     */
    public Page page(List list) {
        Page page = new Page<>(this.getCurrent(), this.getSize());
        if (list != null && !list.isEmpty()) {
            int total = list.size();
            List records = new ArrayList<>(0);
            int start = (this.current - 1) * this.size;
            int end = this.current * this.size - 1;
            if (start <= total) {
                for (int i = start; i <= (end >= total ? total - 1 : end); i++) {
                    records.add(list.get(i));
                }
            }
            page.setTotal(total);
            page.setRecords(records);
        }
        return page;
    }

    /**
     * 扩大limit的倍数, 用空间换时间, 提高查询速度
     *
     * @param times    倍数
     * @param page
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Page<T> expand(Long times, Page<T> page, Function<Page<T>, Page<T>> callable) {
        long size = page.getSize();
        long current = page.getCurrent();
        page.setSize(size * times);
        double v = Long.valueOf(page.getCurrent()).doubleValue() / Long.valueOf(times).doubleValue();
        page.setCurrent(v < 1 ? 1 : (long) Math.ceil(v));
        Page<T> dtoPage = callable.apply(page);
        int i = Long.valueOf((current - 1) * size - (page.getCurrent() - 1) * page.getSize()).intValue();
        int j = i + Long.valueOf(size).intValue();
        dtoPage.setRecords(CollectionUtil.sub(dtoPage.getRecords(), i, j));
        dtoPage.setSize(size);
        dtoPage.setCurrent(current);
        dtoPage.setPages((long) Math.ceil(dtoPage.getTotal() / size));
        return dtoPage;
    }
}

