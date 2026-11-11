package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.PicInfoMapper;
import com.tencent.wxcloudrun.model.PicInfo;
import com.tencent.wxcloudrun.service.entity.PicInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PicInfoServiceImpl implements PicInfoService {

  private static final Logger logger = LoggerFactory.getLogger(PicInfoServiceImpl.class);

  final PicInfoMapper picInfoMapper;

  public PicInfoServiceImpl(@Autowired PicInfoMapper picInfoMapper) {
    this.picInfoMapper = picInfoMapper;
  }

  @Override
  public Optional<PicInfo> getPicInfo(Long id) {
    return Optional.ofNullable(picInfoMapper.getPicInfo(id));
  }

  @Override
  public List<PicInfo> listPicInfo() {
    return picInfoMapper.listPicInfo();
  }

  @Override
  public List<PicInfo> listPicInfoByRelicId(Long relicId) {
    return picInfoMapper.listPicInfoByRelicId(relicId);
  }

  @Override
  public List<PicInfo> listPicInfoByRelicIdAndType(Long relicId, String type) {
    long started = System.nanoTime();
    List<PicInfo> pics = picInfoMapper.listPicInfoByRelicIdAndType(relicId, type);
    logger.info("[timing] pic_info.listByRelicIdAndType, relicId={}, type={}, size={}, costMs={}",
        relicId, type, pics == null ? 0 : pics.size(), (System.nanoTime() - started) / 1000000L);
    return pics;
  }

  @Override
  public List<PicInfo> listPicInfoByRelicTypeAfter(Long relicId, String type, Date updateTime) {
    return picInfoMapper.listPicInfoByRelicTypeAfter(relicId, type, updateTime);
  }

  @Override
  public List<PicInfo> listPicInfoByLocation(double leftLongitude, double topLatitude,
                                             double rightLongitude, double bottomLatitude) {
    double minLongitude = Math.min(leftLongitude, rightLongitude);
    double maxLongitude = Math.max(leftLongitude, rightLongitude);
    double minLatitude = Math.min(topLatitude, bottomLatitude);
    double maxLatitude = Math.max(topLatitude, bottomLatitude);
    return picInfoMapper.listPicInfoByLocation(minLongitude, maxLongitude, minLatitude, maxLatitude);
  }

  @Override
  public Optional<PicInfo> selectById(Long id) {
    return getPicInfo(id);
  }

  @Override
  public List<PicInfo> selectList() {
    return listPicInfo();
  }

  @Override
  public int insert(PicInfo entity) {
    return picInfoMapper.insert(entity);
  }

  @Override
  public int update(PicInfo entity) {
    return picInfoMapper.update(entity);
  }

  @Override
  public int deleteById(Long id) {
    return picInfoMapper.deleteById(id);
  }
}
