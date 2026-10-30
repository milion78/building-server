package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RelicInfoMapper;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.FileService;
import com.tencent.wxcloudrun.service.RelicInfoService;
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
    RelicInfo relic = relicInfoMapper.getRelicInfo(id);
    fillImageFileIds(relic);
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
    if (id == null || count <= 0) {
      return 0;
    }
    List<RelicInfo> relics = relicInfoMapper.listRelicInfoFromId(id, count);
    int updated = 0;
    if (relics == null) {
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
      List<RelicInfo> sameNameList = relicInfoMapper.listRelicInfoByName(relicName.trim());
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
      relicInfoMapper.updateImageUrls(current.getId(), imageUrls);
      updated++;
      logger.info("fillImage updated id={}, name={}, imageCount={}",
          current.getId(), relicName, imageUrls.size());
    }
    logger.info("fillImage finished, updated={}", updated);
    return updated;
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
      String coverFileId = fileService.getFileIdByUrl(coverImage);
      relic.setCoverImage(coverFileId);
    }
    List<String> urls = relic.getImageUrls();
    List<String> fileIds = new ArrayList<>();
    if (urls != null) {
      for (String url : urls) {
        if (url == null || url.trim().isEmpty()) {
          continue;
        }
        String fileId = fileService.getFileIdByUrl(url);
        if (fileId != null && !fileId.trim().isEmpty()) {
          fileIds.add(fileId);
        }
      }
    }
    relic.setImageUrls(fileIds);
  }
}
