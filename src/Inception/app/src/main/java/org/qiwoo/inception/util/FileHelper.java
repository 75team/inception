package org.qiwoo.inception.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liupengke on 15/4/9.
 */
public class FileHelper {
    /**
     * Reads in text from a resource file and returns a String containing the
     * text.
     */
    public static String readFile(Context context,
                                  String fileName) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream =
                    context.getResources().getAssets().open(fileName);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open file: " + fileName, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("file not found: " + fileName, nfe);
        }

        return body.toString();
    }
}