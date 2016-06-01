package com.example.administrator.terrificweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.administrator.terrificweather.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/2/24.
 */
public class DBHelper {
    private final int DB_SIZE = 400000;
    public static final String DB_NAME = "china_city.db";
    public static final String PACKAGE_NAME = "com.example.administrator.terrificweather";
    public static final String DB_PATH = "/data"+ Environment.getDataDirectory().getAbsolutePath()+"/"+PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context context;

    public DBHelper(Context context){
        this.context = context;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void openDatabase(){
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String file){

            try {
                if(!(new File(file).exists())){
                InputStream is = this.context.getResources().openRawResource(R.raw.china_city);
                FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer  = new byte[DB_SIZE];
                    int count = 0;
                    while ((count=is.read(buffer)) > 0){
                        fos.write(buffer,0,count);
                    }
                    fos.close();
                    is.close();
                }
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file,null);
                return  db;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        return null;
    }

    public  void closeDatabase(){
        this.database.close();
    }

}
