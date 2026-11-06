package com.tencent.wxcloudrun.service.entity;

import com.tencent.wxcloudrun.model.RelicInfoUpdate;
import com.tencent.wxcloudrun.service.DtoSerrvice;

import java.util.List;
import java.util.Optional;

public interface RelicInfoUpdateService extends DtoSerrvice<RelicInfoUpdate> {

  Optional<RelicInfoUpdate> getRelicInfoUpdate(Long id);

  Optional<RelicInfoUpdate> getRelicInfoUpdateBySourceId(Long sourceId);

  List<RelicInfoUpdate> listRelicInfoUpdate();

  List<RelicInfoUpdate> listRelicInfoUpdateByLocation(double leftLongitude, double topLatitude, double rightLongitude, double bottomLatitude);
}
