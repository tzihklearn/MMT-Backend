package com.sipc.mmtbackend.utils.CheckinQRCodeUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sipc.mmtbackend.utils.CheckinQRCodeUtil.pojo.QRPayloadPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 签到二维码相关工具类
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CheckinQRCodeUtil {
    private final RedisUtil redisUtil;
    private static final String QRRedisKeyStart = "qrstart";
    private static final String QRRedisKeyEnd = "qre";
    private static final String QRRedisPref = "QR.";
    //CODE_WIDTH：二维码宽度，单位像素
    private static final int CODE_WIDTH = 100;
    //CODE_HEIGHT：二维码高度，单位像素
    private static final int CODE_HEIGHT = 100;
    //FRONT_COLOR：二维码前景色，0x000000 表示黑色
    private static final int FRONT_COLOR = 0x000000;
    //BACKGROUND_COLOR：二维码背景色，0xFFFFFF 表示白色
    private static final int BACKGROUND_COLOR = 0xFFFFFF;

    /**
     * 获取某个组织当天的签到二维码
     *
     * @param organizationId 组织ID
     * @param creatorId 生成该二维码的用户的 UserID
     * @return 二维码图片的 Base64 编码，出现异常返回 NULL
     */
    public String getCheckinQRCode(int organizationId, int creatorId){
        String qrKey = QRRedisKeyStart + organizationId + QRRedisKeyEnd;
        String hashed;
        try {
            MessageDigest md5MD = MessageDigest.getInstance("MD5");
            md5MD.update(qrKey.getBytes());
            hashed = new BigInteger(1, md5MD.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Get Algorithm Error When Process Password: " + e.getMessage());
            return null;
        }
        String redisKey = QRRedisPref + hashed;
        QRPayloadPo payload = redisUtil.getString(redisKey, QRPayloadPo.class);
        if (payload == null){
            payload = new QRPayloadPo(organizationId, creatorId);
            Date now = new Date();
            Date tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(0);
            tomorrow.setMinutes(0);
            tomorrow.setSeconds(0);
            boolean redisOP = redisUtil.setString(qrKey, payload, (tomorrow.getTime() - now.getTime()), TimeUnit.MILLISECONDS);
            if (!redisOP){
                log.warn("Redis SetString key = " + qrKey + ", value = " + payload + "Error\n");
                return null;
            }
        }
        BufferedImage qr = getBufferedImage(qrKey);
        if (qr == null){
            log.warn("QR Payload: " + payload + "\n");
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(qr, "png", baos);
        } catch (IOException e) {
            log.warn("Read QR Code " + payload + "Image Error: " + e.getMessage() + "]n");
            return null;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(baos.toByteArray()));
    }

    /**
     * 解析二维码
     *
     * @param QRKey 扫描二维码得到的内容
     * @return 二维码所属组织的组织 ID,二维码不存在返回 NULL
     */
    public Integer verifyQRCode(String QRKey){
        QRPayloadPo qr = redisUtil.getString(QRRedisPref + QRKey, QRPayloadPo.class);
        if (qr == null){
            log.info("Scanned Noe Exist QR Code: " + QRKey + "\n");
            return null;
        }
        return qr.getOrganizationId();
    }

    private BufferedImage getBufferedImage(String content) {

        //com.google.zxing.EncodeHintType：编码提示类型,枚举类型
        Map<EncodeHintType, Object> hints = new HashMap<>();

        //EncodeHintType.CHARACTER_SET：设置字符编码类型
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        //EncodeHintType.ERROR_CORRECTION：设置误差校正
        //ErrorCorrectionLevel：误差校正等级，L = ~7% correction、M = ~15% correction、Q = ~25% correction、H = ~30% correction
        //不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

        //EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
        hints.put(EncodeHintType.MARGIN, 1);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_HEIGHT, hints);
        } catch (WriterException e) {
            log.warn("Create QR Code Error: " + e.getMessage());
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(CODE_WIDTH, CODE_HEIGHT, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < CODE_WIDTH; x++) {
            for (int y = 0; y < CODE_HEIGHT; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
            }
        }
        return bufferedImage;
    }
}
