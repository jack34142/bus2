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

    public void saveFile(Context context,String file_name, String data){

        try{
            Writer writer = null;

            OutputStream out = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(data);

            if (writer != null) {
                writer.close();
            }
        }catch (IOException e){
            Log.e("MyFileIO", "saveFile() IO出錯");
            e.printStackTrace();
        }

    }

    public String readFile(Context context,String file_name){

        BufferedReader reader = null;
        try {
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
            Log.e("MyFileIO", "readFile() IO出錯");
            e.printStackTrace();
        }

        return "";
    }

}
