package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.dto.RelicDetail;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.service.FileService;
import com.tencent.wxcloudrun.service.entity.RelicInfoService;
import com.tencent.wxcloudrun.typehandler.ImageUrlsTypeHandler;
import com.tencent.wxcloudrun.util.IdCodeUtil;
import org.springframework.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
     * 按 code 查询文保详情
     * 接口地址: POST /relic/getById
     * coverImage、imageUrls 在服务层已转成 fileId，供前端 getTempFileURL 换链
     *
     * @param code 文保 code
     * @return 文保详情；code 无效或查不到时返回空对象
     */
    @PostMapping("/getById")
    public ResponseResult<RelicDetail> getRelicInfoById(@RequestParam(required = false) String code) {
        logger.info("/relic/getById post request, code: {}", code);
        Long id = IdCodeUtil.toId(code);
        if (id == null) {
            return ResponseResult.success(new RelicDetail());
        }
        relicInfoService.fillImage(id, 1);
        return relicInfoService.getRelicInfo(id)
                .map(relic -> ResponseResult.success(toRelicDetail(relic)))
                .orElseGet(() -> ResponseResult.success(new RelicDetail()));
    }

    private RelicDetail toRelicDetail(RelicInfo relic) {
        RelicDetail detail = new RelicDetail();
        if (relic == null) {
            return detail;
        }
        BeanUtils.copyProperties(relic, detail);
        detail.setImageUrls(toImageFileIds(relic.getImageUrls()));
        return detail;
    }

    private List<String> toImageFileIds(String imageUrls) {
        List<String> fileIds = new ArrayList<>();
        for (String url : ImageUrlsTypeHandler.parse(imageUrls)) {
            String fileId = fileService.getFileIdByUrl(url);
            if (fileId != null && !fileId.trim().isEmpty()) {
                fileIds.add(fileId);
            }
        }
        return fileIds;
    }

    /**
     * 按 id 顺序补齐 imageUrls。
     * 用同名其他记录的封面图更新当前记录。
     * 接口地址: POST /relic/fillImage
     *
     * @param id 起始 id，从这个 id 开始向后处理
     * @param count 向后处理的文保条数
     * @return 实际更新的记录数
     */
    @PostMapping("/fillImage")
    public ResponseResult<Integer> fillImage(@RequestParam(required = false) Long id,
                                             @RequestParam(required = false) Integer count) {
        logger.info("/relic/fillImage post request, id: {}, count: {}", id, count);
        int size = count == null ? 0 : count;
        return ResponseResult.success(relicInfoService.fillImage(id, size));
    }
}
