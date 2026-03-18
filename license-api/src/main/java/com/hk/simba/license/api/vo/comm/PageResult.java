package com.hk.simba.license.api.vo.comm;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengry
 * @description
 * @since 2020/3/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 8612222797016490510L;
    private List<T> results = Lists.newArrayList();
    private int pageNo;
    private int pageSize;
    private long count;
    private int totalPage;
}
