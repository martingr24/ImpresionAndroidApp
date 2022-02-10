package com.example.pruebaimpresion;

import android.content.Context;

import java.io.File;

public class Common {
    public static  String getAppPath(Context context){
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                 + File.separator
                    + "pruebasimpresion"
                    + File.separator
        );

        boolean result = false;

        if(!dir.exists()){
            result = dir.mkdir();
            if(!result){
                dir = new File(context.getFilesDir()
                        + File.separator
                        + "pruebasimpresion"
                        + File.separator
                );
                if(!dir.exists()){
                    result = dir.mkdir();
                }
            }
        }
        return dir.getPath() + File.separator;
    }
}
