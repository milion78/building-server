package com.tencent.wxcloudrun.service;

import java.util.List;
import java.util.Optional;

/**
 * 数据库实体的基础增删改查。
 *
 * @param <T> 与数据表一一对应的实体
 */
public interface DtoSerrvice<T> {

  Optional<T> selectById(Long id);

  List<T> selectList();

  int insert(T entity);

  int update(T entity);

  int deleteById(Long id);
}
