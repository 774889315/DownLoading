package com.example.downloading.util;
import android.util.Log;

/**
 * Created by Unreal Lover on 2017/10/6.
 */
//一个处理类
public class CalculateUtil {
     public String unit = " ";
     public double rate = 0;
     public  CalculateUtil(double rate1){
         rate = rate1;
        int munit = 0;
         double token = 1024;
        for(;rate>token;rate/=token)
        {
            munit++;
        }
        switch (munit){
            case (0):{
                unit = "B";
                break;
            }
            case (1):{
                unit = "KB";
                break;
            }
            case (2):{
                unit = "MB";
                break;
            }
            case (3):{
                unit = "GB";
                break;
            }
            case (4):{
                unit = "TB";
                break;
            }
            case (5):{
                unit = "PB";
            }
        }
    }
    public double Rate(){
        Log.i("RRRRRate", rate+"");
        return rate;
    }
    public String Unit(){
        return unit;
    }
}
