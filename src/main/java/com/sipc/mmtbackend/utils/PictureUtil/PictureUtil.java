package com.sipc.mmtbackend.utils.PictureUtil;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
 * 图片工具类
 * @author DoudiNCer
 */

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PictureUtil {
    // 图片扩展名
    private static final String PictureURLEndStr = ".png";

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 根据 avatar_id 获取图片访问链接
     * @author DoudiNCer
     * @param avatar_id 图片唯一ID
     * @return 一个字符串，为公网访问图片的链接
     */
    public String getPictureURL(String avatar_id){
        return minioConfig.getPublicAddress() + "/" + minioConfig.getBucketName() + "/" + avatar_id + PictureURLEndStr;
    }

    /**
     * 获取一个唯一的图片 ID
     * @return 图片 ID 或 null
     */
    private String createAvatarId(){
        String v = "MINIOPIC" + (new Date()) + "MINIOTIME";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.warn("Get MD5 Algorithm Error When Create Avatar ID: " + e.getMessage());
            return null;
        }
        return  Base64.encodeBase64String(md5.digest(v.getBytes()));
    }

    /**
     * 上传图片
     * @param avatar 图片
     * @return 图片ID
     */
    public String uploadPicture(MultipartFile avatar){
        String avatarId = createAvatarId();
        if (avatarId == null)
            return null;
        try (InputStream is = avatar.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(avatarId + PictureURLEndStr)
                    .stream(is, avatar.getSize(), -1)
                    .build());
        } catch (Throwable e) {
            log.warn("Upload New Picture Error: " + e.getMessage());
        }
        return avatarId;
    }
}
