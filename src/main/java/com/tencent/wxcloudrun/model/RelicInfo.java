package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RelicInfo implements Serializable {

  private Long id;

  private String relicName;

  private String complexName;

  private String relicCategory;

  private String relicRank;

  private String description;

  private String relicAddress;

  private String coverImage;

  private String imageUrls;

  private String logo;

  private String province;

  private String city;

  private Float longitude;

  private Float latitude;

  private String relic;

  private LocalDateTime updateTime;
}
