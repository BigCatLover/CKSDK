package com.game.sdk.http;

import com.game.sdk.domain.NotProguard;
import com.game.sdk.log.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by liu hong liang on 2016/11/9.
 * 对IO进行读写
 */
@NotProguard
public class IoUtil {
    public static String readString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            L.e("hongliangsdk", "data=" + buffer.toString());
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String readString1(InputStream inputStream) {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }


    public static void writeString(OutputStream outputStream, String data) {
        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
