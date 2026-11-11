package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RelicInfoMapper;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.FileService;
import com.tencent.wxcloudrun.service.entity.RelicInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RelicInfoServiceImpl implements RelicInfoService {

  private static final Logger logger = LoggerFactory.getLogger(RelicInfoServiceImpl.class);

  final RelicInfoMapper relicInfoMapper;
  final FileService fileService;

  public RelicInfoServiceImpl(@Autowired RelicInfoMapper relicInfoMapper,
                              @Autowired FileService fileService) {
    this.relicInfoMapper = relicInfoMapper;
    this.fileService = fileService;
  }

  @Override
  public Optional<RelicInfo> getRelicInfo(Long id) {
    long started = System.nanoTime();
    RelicInfo relic = relicInfoMapper.getRelicInfo(id);
    logger.info("[timing] relic.getRelicInfo query, id={}, found={}, costMs={}",
        id, relic != null, elapsedMs(started));
    long coverStarted = System.nanoTime();
    fillImageFileIds(relic);
    logger.info("[timing] relic.getRelicInfo fillCover, id={}, costMs={}", id, elapsedMs(coverStarted));
    return Optional.ofNullable(relic);
  }

  @Override
  public List<RelicInfo> listRelicInfo() {
    List<RelicInfo> relics = relicInfoMapper.listRelicInfo();
    fillImageFileIds(relics);
    return relics;
  }

  @Override
  public List<RelicInfo> listRelicInfoByLocation(double leftLongitude, double topLatitude,
                                                 double rightLongitude, double bottomLatitude) {
    double minLongitude = Math.min(leftLongitude, rightLongitude);
    double maxLongitude = Math.max(leftLongitude, rightLongitude);
    double minLatitude = Math.min(topLatitude, bottomLatitude);
    double maxLatitude = Math.max(topLatitude, bottomLatitude);
    List<RelicInfo> relics = relicInfoMapper.listRelicInfoByLocation(
        minLongitude, maxLongitude, minLatitude, maxLatitude);
    fillImageFileIds(relics);
    return relics;
  }

  @Override
  public int fillImage(Long id, int count) {
    long started = System.nanoTime();
    if (id == null || count <= 0) {
      return 0;
    }
    long step = System.nanoTime();
    List<RelicInfo> relics = relicInfoMapper.listRelicInfoFromId(id, count);
    logger.info("[timing] relic.fillImage listFromId, id={}, count={}, size={}, costMs={}",
        id, count, relics == null ? 0 : relics.size(), elapsedMs(step));
    int updated = 0;
    if (relics == null) {
      logger.info("[timing] relic.fillImage done, id={}, updated=0, costMs={}", id, elapsedMs(started));
      return updated;
    }
    for (RelicInfo current : relics) {
      if (current == null || current.getId() == null) {
        continue;
      }
      String relicName = current.getRelicName();
      if (relicName == null || relicName.trim().isEmpty()) {
        continue;
      }
      step = System.nanoTime();
      List<RelicInfo> sameNameList = relicInfoMapper.listRelicInfoByName(relicName.trim());
      logger.info("[timing] relic.fillImage listByName, id={}, name={}, size={}, costMs={}",
          current.getId(), relicName, sameNameList == null ? 0 : sameNameList.size(), elapsedMs(step));
      List<String> imageUrls = new ArrayList<>();
      if (sameNameList != null) {
        for (RelicInfo other : sameNameList) {
          if (other == null || current.getId().equals(other.getId())) {
            continue;
          }
          String coverImage = other.getCoverImage();
          if (coverImage == null || coverImage.trim().isEmpty()) {
            continue;
          }
          imageUrls.add(coverImage.trim());
        }
      }
      if (imageUrls.isEmpty()) {
        continue;
      }
      step = System.nanoTime();
      relicInfoMapper.updateImageUrls(current.getId(), imageUrls);
      logger.info("[timing] relic.fillImage updateImageUrls, id={}, imageCount={}, costMs={}",
          current.getId(), imageUrls.size(), elapsedMs(step));
      updated++;
    }
    logger.info("[timing] relic.fillImage done, id={}, updated={}, costMs={}", id, updated, elapsedMs(started));
    return updated;
  }

  @Override
  public Optional<RelicInfo> selectById(Long id) {
    return getRelicInfo(id);
  }

  @Override
  public List<RelicInfo> selectList() {
    return listRelicInfo();
  }

  @Override
  public int insert(RelicInfo entity) {
    return relicInfoMapper.insert(entity);
  }

  @Override
  public int update(RelicInfo entity) {
    return relicInfoMapper.update(entity);
  }

  @Override
  public int deleteById(Long id) {
    return relicInfoMapper.deleteById(id);
  }

  private static long elapsedMs(long startedNanos) {
    return (System.nanoTime() - startedNanos) / 1000000L;
  }

  private void fillImageFileIds(List<RelicInfo> relics) {
    if (relics == null) {
      return;
    }
    for (RelicInfo relic : relics) {
      fillImageFileIds(relic);
    }
  }

  private void fillImageFileIds(RelicInfo relic) {
    if (relic == null) {
      return;
    }
    String coverImage = relic.getCoverImage();
    if (coverImage != null && !coverImage.trim().isEmpty()) {
      relic.setCoverImage(fileService.getThumbFileIdByUrl(coverImage));
    }
  }
}
