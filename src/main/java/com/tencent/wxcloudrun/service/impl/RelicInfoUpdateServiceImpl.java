package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RelicInfoUpdateMapper;
import com.tencent.wxcloudrun.model.RelicInfoUpdate;
import com.tencent.wxcloudrun.service.entity.RelicInfoUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelicInfoUpdateServiceImpl implements RelicInfoUpdateService {

  private static final Logger logger = LoggerFactory.getLogger(RelicInfoUpdateServiceImpl.class);

  final RelicInfoUpdateMapper relicInfoUpdateMapper;

  public RelicInfoUpdateServiceImpl(@Autowired RelicInfoUpdateMapper relicInfoUpdateMapper) {
    this.relicInfoUpdateMapper = relicInfoUpdateMapper;
  }

  @Override
  public Optional<RelicInfoUpdate> getRelicInfoUpdate(Long id) {
    return Optional.ofNullable(relicInfoUpdateMapper.getRelicInfoUpdate(id));
  }

  @Override
  public Optional<RelicInfoUpdate> getRelicInfoUpdateBySourceId(Long sourceId) {
    long started = System.nanoTime();
    RelicInfoUpdate update = relicInfoUpdateMapper.getRelicInfoUpdateBySourceId(sourceId);
    logger.info("[timing] relic_info_update.getBySourceId, sourceId={}, found={}, costMs={}",
        sourceId, update != null, (System.nanoTime() - started) / 1000000L);
    return Optional.ofNullable(update);
  }

  @Override
  public List<RelicInfoUpdate> listRelicInfoUpdate() {
    return relicInfoUpdateMapper.listRelicInfoUpdate();
  }

  @Override
  public List<RelicInfoUpdate> listRelicInfoUpdateByLocation(double leftLongitude, double topLatitude,
                                                             double rightLongitude, double bottomLatitude) {
    double minLongitude = Math.min(leftLongitude, rightLongitude);
    double maxLongitude = Math.max(leftLongitude, rightLongitude);
    double minLatitude = Math.min(topLatitude, bottomLatitude);
    double maxLatitude = Math.max(topLatitude, bottomLatitude);
    return relicInfoUpdateMapper.listRelicInfoUpdateByLocation(minLongitude, maxLongitude, minLatitude, maxLatitude);
  }

  @Override
  public Optional<RelicInfoUpdate> selectById(Long id) {
    return getRelicInfoUpdate(id);
  }

  @Override
  public List<RelicInfoUpdate> selectList() {
    return listRelicInfoUpdate();
  }

  @Override
  public int insert(RelicInfoUpdate entity) {
    return relicInfoUpdateMapper.insert(entity);
  }

  @Override
  public int update(RelicInfoUpdate entity) {
    return relicInfoUpdateMapper.update(entity);
  }

  @Override
  public int deleteById(Long id) {
    return relicInfoUpdateMapper.deleteById(id);
  }
}
