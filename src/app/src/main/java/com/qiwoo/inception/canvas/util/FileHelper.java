package com.qiwoo.inception.canvas.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liupengke on 15/5/19.
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
}
