package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.FileService;
import com.tencent.wxcloudrun.service.RelicInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文保详情接口控制器
 * 一级路径：/relic
 *
 * @author building-service
 */
@RestController
@RequestMapping("/relic")
public class RelicInfoController {

    private static final Logger logger = LoggerFactory.getLogger(RelicInfoController.class);

    @Autowired
    private RelicInfoService relicInfoService;

    @Autowired
    private FileService fileService;

    /**
     * 按 id 查询文保详情
     * 接口地址: POST /relic/getById
     *
     * @param id 文保 id
     * @return 统一响应格式，包含文保详情
     */
    @PostMapping("/getById")
    public ResponseResult<RelicInfo> getRelicInfoById(@RequestParam(required = false) Long id) {
        logger.info("/relic/getById post request, id: {}", id);
        if (id == null) {
            return ResponseResult.error(1L, "id 不能为空");
        }
        return relicInfoService.getRelicInfo(id)
                .map(relic -> {
                    relic.setCoverImage(fileService.getUrlByFileId(fileService.getFileIdByUrl(relic.getCoverImage())));
                    return ResponseResult.success(relic);
                })
                .orElseGet(() -> ResponseResult.error(1L, "文保不存在"));
    }
}
