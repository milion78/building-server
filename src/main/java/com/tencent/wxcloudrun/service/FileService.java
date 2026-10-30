package com.tencent.wxcloudrun.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * 对象存储文件服务。
 * fileId 即对象键 ObjectKey，访问域名格式为 BucketName-APPID.cos.Region.myqcloud.com。
 *
 * @author building-service
 */
@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private static final String SOURCE_URL_PREFIX = "https://sinorelic.oss-cn-hangzhou.aliyuncs.com/";

    private static final String FILE_ID_PREFIX =
            "cloud://prod-8gmyadj42ce90ec4.7072-prod-8gmyadj42ce90ec4-1387541623/pic/";

    private final String secretId;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final long expireSeconds;

    private COSClient cosClient;

    public FileService(@Value("${cos.secret-id}") String secretId,
                       @Value("${cos.secret-key}") String secretKey,
                       @Value("${cos.region}") String region,
                       @Value("${cos.bucket}") String bucket,
                       @Value("${cos.expire-seconds:86400}") long expireSeconds) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 按文件 ID 换取带签名的临时访问链接，有效期 24 小时。
     *
     * @param fileId 对象键，例如 relic/1003/cover.jpeg
     * @return 可访问的 URL，fileId 为空时返回 null
     */
    public String getUrlByFileId(String fileId) {
        if (fileId == null || fileId.trim().isEmpty()) {
            logger.warn("fileId 为空，无法生成访问地址");
            return null;
        }
        String objectKey = toObjectKey(fileId);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                bucket, objectKey, HttpMethodName.GET);
        request.setExpiration(new Date(System.currentTimeMillis() + expireSeconds * 1000L));
        String url = client().generatePresignedUrl(request).toString();
        logger.info("FileService.getUrlByFileId - fileId: {}", objectKey);
        return url;
    }

    /**
     * 把外部图片地址换成 fileId。
     * 只保留 URL 最后一段文件名，再拼上云存储前缀。
     *
     * @param url 例如 https://sinorelic.oss-cn-hangzhou.aliyuncs.com/upload/202209/308b5572-b314-4025-8e0e-0cc431838ca9.jpg
     * @return cloud://prod-8gmyadj42ce90ec4.7072-prod-8gmyadj42ce90ec4-1387541623/pic/308b5572-b314-4025-8e0e-0cc431838ca9.jpg
     */
    public String getFileIdByUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.warn("url 为空，无法换成 fileId");
            return null;
        }
        if (!url.trim().startsWith(SOURCE_URL_PREFIX)) {
            return url;
        }
        String path = url.trim();
        int query = path.indexOf('?');
        if (query >= 0) {
            path = path.substring(0, query);
        }
        int fragment = path.indexOf('#');
        if (fragment >= 0) {
            path = path.substring(0, fragment);
        }
        int slash = path.lastIndexOf('/');
        String fileName = slash >= 0 ? path.substring(slash + 1) : path;
        if (fileName.isEmpty()) {
            logger.warn("url 中没有文件名: {}", url);
            return null;
        }
        String fileId = FILE_ID_PREFIX + fileName;
        logger.info("FileService.getFileIdByUrl - fileId: {}", fileId);
        return fileId;
    }

    /**
     * cloud://环境.存储桶/pic/文件名 里，对象键是 pic/文件名。
     */
    private static String toObjectKey(String fileId) {
        String objectKey = fileId.trim();
        if (objectKey.startsWith("cloud://")) {
            int slash = objectKey.indexOf('/', "cloud://".length());
            if (slash >= 0 && slash + 1 < objectKey.length()) {
                objectKey = objectKey.substring(slash + 1);
            }
        }
        while (objectKey.startsWith("/")) {
            objectKey = objectKey.substring(1);
        }
        return objectKey;
    }

    private COSClient client() {
        if (cosClient != null) {
            return cosClient;
        }
        if (isBlank(secretId) || isBlank(secretKey) || isBlank(region) || isBlank(bucket)) {
            throw new IllegalStateException("对象存储未配置，请设置 COS_SECRET_ID、COS_SECRET_KEY、COS_REGION、COS_BUCKET");
        }
        synchronized (this) {
            if (cosClient == null) {
                COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
                ClientConfig clientConfig = new ClientConfig(new Region(region));
                cosClient = new COSClient(credentials, clientConfig);
            }
        }
        return cosClient;
    }

    @PreDestroy
    public void shutdown() {
        if (cosClient != null) {
            cosClient.shutdown();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
