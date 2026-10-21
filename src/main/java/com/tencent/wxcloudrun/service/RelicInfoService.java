package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RelicInfo;

import java.util.List;
import java.util.Optional;

public interface RelicInfoService {

  Optional<RelicInfo> getRelicInfo(Long id);

  List<RelicInfo> listRelicInfo();

  List<RelicInfo> listRelicInfoByLocation(double leftLongitude, double topLatitude, double rightLongitude, double bottomLatitude);
}
