package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RelicInfoMapper;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.RelicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelicInfoServiceImpl implements RelicInfoService {

  final RelicInfoMapper relicInfoMapper;

  public RelicInfoServiceImpl(@Autowired RelicInfoMapper relicInfoMapper) {
    this.relicInfoMapper = relicInfoMapper;
  }

  @Override
  public Optional<RelicInfo> getRelicInfo(Long id) {
    return Optional.ofNullable(relicInfoMapper.getRelicInfo(id));
  }

  @Override
  public List<RelicInfo> listRelicInfo() {
    return relicInfoMapper.listRelicInfo();
  }

  @Override
  public List<RelicInfo> listRelicInfoByLocation(double leftLongitude, double topLatitude,
                                                 double rightLongitude, double bottomLatitude) {
    double minLongitude = Math.min(leftLongitude, rightLongitude);
    double maxLongitude = Math.max(leftLongitude, rightLongitude);
    double minLatitude = Math.min(topLatitude, bottomLatitude);
    double maxLatitude = Math.max(topLatitude, bottomLatitude);
    return relicInfoMapper.listRelicInfoByLocation(minLongitude, maxLongitude, minLatitude, maxLatitude);
  }
}
