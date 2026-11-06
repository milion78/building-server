package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Counter implements Serializable {

  private Integer id;

  private Integer count;

  private Date createdAt;

  private Date updatedAt;
}
