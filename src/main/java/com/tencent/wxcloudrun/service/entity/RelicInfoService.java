package com.tencent.wxcloudrun.service.entity;

import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.DtoSerrvice;

import java.util.List;
import java.util.Optional;

public interface RelicInfoService extends DtoSerrvice<RelicInfo> {

  Optional<RelicInfo> getRelicInfo(Long id);

  List<RelicInfo> listRelicInfo();

  List<RelicInfo> listRelicInfoByLocation(double leftLongitude, double topLatitude, double rightLongitude, double bottomLatitude);

  int fillImage(Long id, int count);
}
