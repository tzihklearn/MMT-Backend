package com.sipc.mmtbackend.utils.urlBase64;

import lombok.Cleanup;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 韦秉芮
 * @see "传入图片url获取base64编码后的字符串"
 */
public class UrlBase64 {
    public static byte[] getPng(String avatarUrl) throws IOException {
        URL url = new URL(avatarUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        @Cleanup DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
        @Cleanup ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = dataInputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        return outStream.toByteArray();
    }
}
