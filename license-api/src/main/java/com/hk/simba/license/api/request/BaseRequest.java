package com.hk.simba.license.api.request;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zengry
 * @description
 * @since 2020/3/11
 */
@Data
public class BaseRequest<T> implements Serializable {

    private static final long serialVersionUID = -5664595764737117894L;
    private T data;
}
