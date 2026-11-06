package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.RelicInfoUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RelicInfoUpdateMapper {

  RelicInfoUpdate getRelicInfoUpdate(@Param("id") Long id);

  RelicInfoUpdate getRelicInfoUpdateBySourceId(@Param("sourceId") Long sourceId);

  List<RelicInfoUpdate> listRelicInfoUpdate();

  List<RelicInfoUpdate> listRelicInfoUpdateByLocation(@Param("minLongitude") double minLongitude,
                                                      @Param("maxLongitude") double maxLongitude,
                                                      @Param("minLatitude") double minLatitude,
                                                      @Param("maxLatitude") double maxLatitude);

  int insert(RelicInfoUpdate entity);

  int update(RelicInfoUpdate entity);

  int deleteById(@Param("id") Long id);
}
