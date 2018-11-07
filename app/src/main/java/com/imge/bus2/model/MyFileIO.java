package com.imge.bus2.model;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MyFileIO {
    private static final String TAG = "MyFileIO";

    // 寫入一個檔案
    public void saveFile(Context context,String file_name, String data){

        try{
            Writer writer = null;

            // 設定寫入檔名，並設成 private
            OutputStream out = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(data);

            if (writer != null) {
                writer.close();
            }
        }catch (IOException e){
            Log.e(TAG, "saveFile() IO出錯");
            e.printStackTrace();
        }

    }

    // 讀取一個檔案
    public String readFile(Context context,String file_name){

        BufferedReader reader = null;
        try {
            // 設定讀取的檔名
            InputStream in = context.openFileInput(file_name);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            if (reader != null) {
                reader.close();
                return jsonString.toString();
            }

        }catch (FileNotFoundException e) {

        }catch (IOException e){
            Log.e(TAG, "readFile() IO出錯");
            e.printStackTrace();
        }

        return "";
    }

}
