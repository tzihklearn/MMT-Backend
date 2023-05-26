package com.sipc.mmtbackend.utils.PictureUtil;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 对需要用到的 Minio 的方法进行适用性封装（请勿直接调用）
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MinioUtil {
    // 图片扩展名
    private static final String PictureURLEndStr = ".png";
    // 被删除的图片的扩展名
    private static final String DropedPictureURLEndStr = ".png.d";
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 根据 pictureId 获取图片访问链接
     *
     * @param pictureId 图片唯一ID
     * @return 一个字符串，为公网访问图片的链接
     * @author DoudiNCer
     */
    public String getPictureURL(String pictureId) {
        return minioConfig.getPublicAddress() + "/" + minioConfig.getBucketName() + "/" + pictureId + PictureURLEndStr;
    }

    /**
     * 构造一个唯一的图片 ID
     *
     * @return 图片 ID 或 null
     */
    private String createPicId() {
        String v = "MINIOPIC" + (new Date()) + "MINIOTIME";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Get MD5 Algorithm Error When Create Avatar ID: " + e.getMessage());
            return null;
        }
        return Base64.encodeBase64String(md5.digest(v.getBytes()));
    }

    /**
     * 上传图片
     *
     * @param picture 图片文件
     * @return 图片ID，null 表示系统错误
     */
    public String uploadPicture(MultipartFile picture) {
        String pictureId = createPicId();
        if (pictureId == null) return null;
        try (InputStream is = picture.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(pictureId + PictureURLEndStr)
                            .stream(is, picture.getSize(), -1)
                            .build());
        } catch (Throwable e) {
            log.warn("Upload New Picture Error: " + e.getMessage());
            return null;
        }
        return pictureId;
    }

    /**
     * 软删除图片
     *
     * @param pictureId 图片在 Minio 的ID
     * @return true 表示一切正常，false 表示操作失败。null 表示系统错误
     */
    public Boolean softDeletePicture(String pictureId) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(pictureId + DropedPictureURLEndStr)
                            .source(
                                    CopySource.builder()
                                            .bucket(minioConfig.getBucketName())
                                            .object(pictureId + PictureURLEndStr)
                                            .build()
                            )
                            .build()
            );
        } catch (Throwable e) {
            log.warn("软删除时复制图片 " + pictureId + " 失败：" + e.getMessage());
            return null;
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(pictureId + PictureURLEndStr)
                            .build()
            );
        } catch (Throwable e) {
            log.warn("软删除时删除图片 " + pictureId + " 失败：" + e.getMessage());
            return null;
        }
        return true;
    }
}
