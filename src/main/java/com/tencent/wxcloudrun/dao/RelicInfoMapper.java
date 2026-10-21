package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.RelicInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RelicInfoMapper {

  RelicInfo getRelicInfo(@Param("id") Long id);

  List<RelicInfo> listRelicInfo();

  List<RelicInfo> listRelicInfoByLocation(@Param("minLongitude") double minLongitude,
                                          @Param("maxLongitude") double maxLongitude,
                                          @Param("minLatitude") double minLatitude,
                                          @Param("maxLatitude") double maxLatitude);
}
