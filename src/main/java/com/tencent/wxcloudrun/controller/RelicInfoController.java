package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.RelicInfoService;
import com.tencent.wxcloudrun.util.IdCodeUtil;
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

    /**
     * 按 code 查询文保详情
     * 接口地址: POST /relic/getById
     * coverImage、imageUrls 在服务层已转成 fileId，供前端 getTempFileURL 换链
     *
     * @param code 文保 code
     * @return 文保详情；code 无效或查不到时返回空对象
     */
    @PostMapping("/getById")
    public ResponseResult<RelicInfo> getRelicInfoById(@RequestParam(required = false) String code) {
        logger.info("/relic/getById post request, code: {}", code);
        Long id = IdCodeUtil.toId(code);
        if (id == null) {
            return ResponseResult.success(new RelicInfo());
        }
        return relicInfoService.getRelicInfo(id)
                .map(ResponseResult::success)
                .orElseGet(() -> ResponseResult.success(new RelicInfo()));
    }
}
