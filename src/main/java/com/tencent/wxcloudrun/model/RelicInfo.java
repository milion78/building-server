package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RelicInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long sourceId;

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

  private Date updateTime;
}
