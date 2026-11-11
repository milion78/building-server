package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.dto.RelicDetail;
import com.tencent.wxcloudrun.dto.RelicFootprintInfo;
import com.tencent.wxcloudrun.service.RelicService;
import com.tencent.wxcloudrun.service.entity.RelicInfoService;
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
public class RelicController {

    private static final Logger logger = LoggerFactory.getLogger(RelicController.class);

    @Autowired
    private RelicInfoService relicInfoService;

    @Autowired
    private RelicService relicService;

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
        long started = System.nanoTime();
        logger.info("[timing] /relic/getById start, code={}", code);
        try {
            long step = System.nanoTime();
            Long id = IdCodeUtil.toId(code);
            logger.info("[timing] /relic/getById toId, code={}, id={}, costMs={}", code, id, elapsedMs(step));
            if (id == null) {
                return ResponseResult.success(new RelicDetail());
            }
            step = System.nanoTime();
            int filled = relicInfoService.fillImage(id, 1);
            logger.info("[timing] /relic/getById fillImage, id={}, updated={}, costMs={}",
                    id, filled, elapsedMs(step));
            step = System.nanoTime();
            RelicDetail detail = relicInfoService.getRelicInfo(id)
                    .map(relicService::toRelicDetail)
                    .orElseGet(RelicDetail::new);
            logger.info("[timing] /relic/getById getRelicInfo+toRelicDetail, id={}, footprintSize={}, imageSize={}, costMs={}",
                    id,
                    detail.getFootprints() == null ? 0 : detail.getFootprints().size(),
                    detail.getImageUrls() == null ? 0 : detail.getImageUrls().size(),
                    elapsedMs(step));
            return ResponseResult.success(detail);
        } finally {
            logger.info("[timing] /relic/getById end, code={}, costMs={}", code, elapsedMs(started));
        }
    }

    /**
     * 按 code 查询文保足迹汇总。
     * 接口地址: POST /relic/getRelicFootprintInfo
     * count：listFootprints 结果按 username 去重后的用户数
     * yearList：足迹 actionTime 年份，从大到小
     *
     * @param code 文保 code
     * @return 足迹汇总；code 无效时返回空汇总
     */
    @PostMapping("/getRelicFootprintInfo")
    public ResponseResult<RelicFootprintInfo> getRelicFootprintInfo(@RequestParam(required = false) String code) {
        long started = System.nanoTime();
        logger.info("[timing] /relic/getRelicFootprintInfo start, code={}", code);
        try {
            long step = System.nanoTime();
            Long id = IdCodeUtil.toId(code);
            logger.info("[timing] /relic/getRelicFootprintInfo toId, code={}, id={}, costMs={}",
                    code, id, elapsedMs(step));
            if (id == null) {
                return ResponseResult.success(new RelicFootprintInfo());
            }
            step = System.nanoTime();
            RelicFootprintInfo info = relicService.getFootprintInfo(id);
            logger.info("[timing] /relic/getRelicFootprintInfo getFootprintInfo, id={}, count={}, yearSize={}, costMs={}",
                    id, info.getCount(), info.getYearList() == null ? 0 : info.getYearList().size(), elapsedMs(step));
            return ResponseResult.success(info);
        } finally {
            logger.info("[timing] /relic/getRelicFootprintInfo end, code={}, costMs={}", code, elapsedMs(started));
        }
    }

    private static long elapsedMs(long startedNanos) {
        return (System.nanoTime() - startedNanos) / 1000000L;
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
