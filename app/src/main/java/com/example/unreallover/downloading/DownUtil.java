package com.example.unreallover.downloading;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Unreal Lover on 2017/10/1.
 */

public class DownUtil {
    //下载资源的路径
    private String path;
    //下载资源的线程数
    private int threadNum;
    //下载文件的保存位置
    private String targetFile;
    //下载文件的大小
    private int fileSize;
    //下载的线程对象
    private DownThread[] threads;

    public DownUtil(String path,String targetFile,int threadNum)
    {
        this.path = path;
        this.threadNum = threadNum;
        threads = new DownThread[threadNum];
        this.targetFile = targetFile;
    }

    public void download() throws Exception
    {//path转为url格式
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg,"
                + "application/x-shockwave-flsh, application/xaml+xml,"
                + "application/vnd.ms-xpsdocument, application/x-ms-xbap,"
                + "application/x-ms-application, application/vnd.ms-excel,"
                + "application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language","zh-CN");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        //获取文件大小
        fileSize = conn.getContentLength();
        conn.disconnect();
        int currentPartSize = fileSize / threadNum + 1;
        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
        file.setLength(fileSize);
        file.close();
        for (int i = 0; i < threadNum; i++)
        {
            //每条线程开始时的位置
            int startPos = i * currentPartSize;
            //每条线程使用一个RandomAccess来进行下载
            RandomAccessFile currentPart = new RandomAccessFile(targetFile, "rw");
            //定位该线程的下载位置
            currentPart.seek(startPos);
            //创建下载线程
            threads[i] = new DownThread(startPos, currentPartSize, currentPart);
            //启动下载线程
            threads[i].start();
        }
    }
    //获取下载百分比
    public double getCompleteRate()
    {//统计多条线程已下载百分比
        int sumSize = 0;
        for (int i = 0; i < threadNum; i++)
        {
            sumSize += threads[i].length;
        }
        return sumSize * 1.0 / fileSize;
    }

    private class DownThread extends Thread
    {
        private int startPos;
        private int currentPartSize;
        private RandomAccessFile currentPart;
        public int length;

        public DownThread(int startPos, int currentPartSize, RandomAccessFile currentPart)
        {
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPart = currentPart;
        }

        @Override
        public void run()
        {
            try
            {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg,"
                        + "application/x-shockwave-flash, application/xaml+xml,"
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap,"
                        + "application/x-ms-application, application/vnd.ms-excel,"
                        + "application/vnd.ms-powerpoint, application/msword, */*"
                );
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Charset", "UTF-8");
                //QAQ
                InputStream inStream = conn.getInputStream();
                skipFully(inStream, this.startPos);
                byte[] buffer = new byte[1024];
                int hasRead = 0;
                while (length < currentPartSize && (hasRead = inStream.read(buffer)) >0)
                {
                    currentPart.write(buffer, 0, hasRead);
                    length += hasRead;
                }
                currentPart.close();
                inStream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    //定义一个为InputStream跳过bytes字节的方法
    public static void skipFully(InputStream in, long bytes) throws IOException
    {
        long remainning = bytes;
        long len = 0;
        while (remainning > 0)
        {
            len = in.skip(remainning);
            remainning -= len;
        }
    }

}
