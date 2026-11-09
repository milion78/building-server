package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.PicInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PicInfoMapper {

  PicInfo getPicInfo(@Param("id") Long id);

  List<PicInfo> listPicInfo();

  List<PicInfo> listPicInfoByRelicId(@Param("relicId") Long relicId);

  List<PicInfo> listPicInfoByRelicIdAndType(@Param("relicId") Long relicId, @Param("type") String type);

  List<PicInfo> listPicInfoByRelicTypeAfter(@Param("relicId") Long relicId,
                                            @Param("type") String type,
                                            @Param("updateTime") Date updateTime);

  List<PicInfo> listPicInfoByLocation(@Param("minLongitude") double minLongitude,
                                      @Param("maxLongitude") double maxLongitude,
                                      @Param("minLatitude") double minLatitude,
                                      @Param("maxLatitude") double maxLatitude);

  int insert(PicInfo entity);

  int update(PicInfo entity);

  int deleteById(@Param("id") Long id);
}
