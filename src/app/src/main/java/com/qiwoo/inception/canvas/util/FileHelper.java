package com.qiwoo.inception.canvas.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liupengke on 15/5/19.
 * 从assets或res中读取文件，并返回文件内容
 */
public class FileHelper {
    public static String loadFromAssetsFile(Context context, String fname){
        StringBuilder body = new StringBuilder();
        try
        {
            InputStream in=context.getAssets().open(fname);

            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return body.toString();
    }
    public static String loadFromResourceFile(Context context,
                                              int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources()
                    .openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return body.toString();
    }
    public static InputStream readFromAsserts(Context context, String fname) {
        InputStream is = null;

        try {
            is = context.getAssets().open(fname);
        } catch (Exception e) {
            System.out.println(e);
            Log.i("debug", e.toString());
        }
        return is;
    }
}
