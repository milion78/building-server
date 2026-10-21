package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RelicInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RelicInfoServiceLocationTest {

  private static final double LONGITUDE = 116.42;
  private static final double LATITUDE = 39.9028;
  private static final double DELTA = 0.05;

  @Autowired
  private RelicInfoService relicInfoService;

  @Test
  void listRelicInfoAroundPoint() {
    double leftLongitude = LONGITUDE - DELTA;
    double rightLongitude = LONGITUDE + DELTA;
    double topLatitude = LATITUDE + DELTA;
    double bottomLatitude = LATITUDE - DELTA;

    List<RelicInfo> list = relicInfoService.listRelicInfoByLocation(
        leftLongitude, topLatitude, rightLongitude, bottomLatitude);

    System.out.println("count=" + list.size());
    for (RelicInfo item : list) {
      System.out.printf("%d\t%s\t%s\t%s%n",
          item.getId(), item.getRelicName(), item.getLongitude(), item.getLatitude());
      assertTrue(item.getLongitude() >= leftLongitude && item.getLongitude() <= rightLongitude);
      assertTrue(item.getLatitude() >= bottomLatitude && item.getLatitude() <= topLatitude);
    }
    assertFalse(list.isEmpty());
  }
}
