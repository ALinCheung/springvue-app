package com.springvue.app.dao.convert.support;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PO --> VO的增强支持类
 */
public interface ConvertEnricher<T, R, M extends Converter<T, R>> {

    /**
     * 获取Mapper实例
     */
    @SuppressWarnings("unchecked")
    default M loadMapper() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<M> mapperClass = (Class<M>) parameterizedType.getActualTypeArguments()[2];
        try {
            return (M) mapperClass.getDeclaredField("INSTANCE").get(mapperClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将source代表的实例转换并增强后返回R代表的类型实例
     *
     * @param source 源对象
     * @param arguments 其它参数
     * @return 返回转换增强后对像
     */
    default R convertAndEnrich(T source, Object... arguments) {
        return loadMapper().toVO(source);
    }

    /**
     * 批量转换
     *
     * @see #convertAndEnrich(Object, Object...)
     */
    default List<R> convertAndEnrich(List<T> sources) {
        final Converter<T, R> mapper = loadMapper();
        return sources.stream()
                .map(mapper::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 将source代表的实例转换并增强后返回T代表的类型实例
     *
     * @param source 源对象
     * @param arguments 其它参数
     * @return 返回转换增强后对像
     */
    default T convert(R source, Object... arguments) {
        return loadMapper().toPO(source);
    }

    /**
     * 批量转换
     *
     * @see #convert(Object, Object...)
     */
    default List<T> convert(List<R> sources) {
        return sources.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
