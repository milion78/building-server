package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PicInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String url;

  private String useeNickname;

  private Long userId;

  private String userHeadUrl;

  private Long relicId;

  private String relicName;

  private String type;

  private Float longitude;

  private Float latitude;

  private String picTime;

  private String detailExtra;

  private Date updateTime;
}
