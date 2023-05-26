package com.sipc.mmtbackend.utils.PictureUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipc.mmtbackend.mapper.PictureMapper;
import com.sipc.mmtbackend.pojo.domain.Picture;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.PictureUsage;
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
 *
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
    private final PictureMapper pictureMapper;
    private final MinioUtil minioUtil;

    /**
     * 根据 avatar_id 获取图片访问链接
     *
     * @param avatar_id 图片唯一ID
     * @return 一个字符串，为公网访问图片的链接，若图片不存在返回 null
     * @author DoudiNCer
     */
    public String getPictureURL(String avatar_id) {
        Picture picture = pictureMapper.selectOne(new QueryWrapper<Picture>().eq("pic_id", avatar_id));
        if (picture == null) return null;
        return minioUtil.getPictureURL(avatar_id);
    }

    /**
     * 构造一个唯一的图片 ID
     *
     * @return 图片 ID 或 null
     */
    private String createAvatarId() {
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
     * @param picture 图片
     * @return 图片ID
     */
    @Deprecated
    public String uploadPicture(MultipartFile picture) {
        String pictureId = createAvatarId();
        if (pictureId == null) return null;
        try (InputStream is = picture.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(pictureId + PictureURLEndStr).stream(is, picture.getSize(), -1).build());
        } catch (Throwable e) {
            log.warn("Upload New Picture Error: " + e.getMessage());
        }
        return pictureId;
    }

    /**
     * 上传图片
     *
     * @param picFile MultipartFile 图片文件
     * @return 图片ID，null表示上传失败
     */
    public String uploadPicture(MultipartFile picFile, PictureUsage usage) {
        ObjectMapper objectMapper = new ObjectMapper();
        String usageJson;
        try {
            usageJson = objectMapper.writeValueAsString(usage);
        } catch (JsonProcessingException e) {
            log.warn("上传图片时转换图片用法" + usage + "时抛出异常：" + e.getMessage());
            return null;
        }
        String pictureId = minioUtil.uploadPicture(picFile);
        if (pictureId == null) return null;
        Picture picture = new Picture();
        picture.setPicId(pictureId);
        picture.setUsage(usageJson);
        int insertPic = pictureMapper.insert(picture);
        if (insertPic != 1) {
            log.warn("上传图片插入数据" + picture + "异常，受影响行数：" + insertPic);
            return null;
        }
        return pictureId;
    }

    /**
     * 删除图片
     *
     * @param pictureId 图片ID
     * @return true 表示一切正常，false 表示图片不存在，null 表示系统错误
     */
    public Boolean dropPicture(String pictureId) {
        Picture picture = pictureMapper.selectOne(new QueryWrapper<Picture>().eq("pic_id", pictureId));
        if (picture == null) {
            log.warn("尝试删除不存在的图片：" + pictureId);
            return false;
        }
        Boolean deletePicture = minioUtil.softDeletePicture(pictureId);
        if (deletePicture == null || !deletePicture)
            return deletePicture;
        int i = pictureMapper.deleteById(picture.getId());
        if (i != 0) {
            log.warn("从数据库删除图片 " + pictureId + " 失败，受影响行数：" + i);
            return null;
        }
        return true;
    }
}
