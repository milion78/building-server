package com.tencent.wxcloudrun.service.entity;

import com.tencent.wxcloudrun.model.PicInfo;
import com.tencent.wxcloudrun.service.DtoSerrvice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PicInfoService extends DtoSerrvice<PicInfo> {

  Optional<PicInfo> getPicInfo(Long id);

  List<PicInfo> listPicInfo();

  List<PicInfo> listPicInfoByRelicId(Long relicId);

  List<PicInfo> listPicInfoByRelicIdAndType(Long relicId, String type);

  List<PicInfo> listPicInfoByRelicTypeAfter(Long relicId, String type, Date updateTime);

  List<PicInfo> listPicInfoByLocation(double leftLongitude, double topLatitude, double rightLongitude, double bottomLatitude);
}
