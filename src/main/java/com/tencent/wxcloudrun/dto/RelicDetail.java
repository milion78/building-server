package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Data
public class RelicDetail implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String relicName;

  private String complexName;

  private String relicCategory;

  private String relicRank;

  private String description;

  private String relicAddress;

  private String coverImage;

  private List<String> imageUrls = new ArrayList<>();

  private String logo;

  private String province;

  private String city;

  private Float longitude;

  private Float latitude;

  private String relic;

  private Date updateTime;

  private String era;
  
  private String batch;

  private List<Footprint> footprints = new ArrayList<>();
}
