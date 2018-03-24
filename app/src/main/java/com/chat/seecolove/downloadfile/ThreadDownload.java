package com.chat.seecolove.downloadfile;

import android.app.DownloadManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.ThreadTool;

/**
 */
public class ThreadDownload {

    private int allLength,contentLength;
    private DownloadRunnable[] downloadRunnables;

    public  void download(final String URL,final File file,final int threadCount) {
        final long l = System.currentTimeMillis();
        downloadRunnables = new DownloadRunnable[threadCount];
        ThreadTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                    connection.setRequestMethod("GET");
                    contentLength  = connection.getContentLength();
                    int range = contentLength / threadCount;
                    for (int i = 0; i < threadCount; i++) {
                        int start = i * range;
                        int end = start + range;
                        if (i == (threadCount-1)) {
                            end = contentLength - 1;
                        }
                        downloadRunnables[i]  = new DownloadRunnable(URL, file, start, end, l,range);
                        ThreadTool.getInstance().execute(downloadRunnables[i]);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

     class DownloadRunnable implements Runnable {
        private String url;
        private File file;
        private int start;
        private int end;
        public int tempLength;
        public int range;
        private long startTime;

        public DownloadRunnable(String url, File file, int start, int end, long startTime,int range) {
            this.url = url;
            this.file = file;
            this.start = start;
            this.end = end;
            this.startTime = startTime;
            this.range = range;
        }

        @Override
        public void run() {
            RandomAccessFile accessFile=null;
            InputStream is=null;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range", "bytes=" + start + "-" + end+"");
                connection.connect();
//                LogTool.setLog("下载部分:","start:"+start+"--end:"+end);
                 accessFile = new RandomAccessFile(file, "rw");
                accessFile.seek(start);
                int code = connection.getResponseCode();
                if (code == 206) {
                    is= connection.getInputStream();
                    byte[] buffer = new byte[1024*500];
                    int length;
                    while (tempLength < range && (length = is.read(buffer)) != -1) {
                        accessFile.write(buffer, 0, length);
                        tempLength += length;
                    }

                    LogTool.setLog("下载完成时间:",(System.currentTimeMillis() - startTime));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(accessFile!=null){
                        accessFile.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

//    public int getAllLength(){
//        // 返回已经完成的百分比
//        int sumSize=0;
//        sumSize+=DownloadRunnable.tempLength;
//        double per =  allLength * 1.0 / fileSize;
//        LogTool.setLog("per:",per*100+"----allLength:"+allLength);
//       return allLength;
//    }

    /**
     * 获取下载完成的百分比
     *
     * @return
     */
    public double getCompleteRate() {
        // 统计多条线程已经下载的总大小
        int sumSize = 0;
        for (int i = 0; i < downloadRunnables.length; i++) {
            if(downloadRunnables[i]!=null){
                sumSize += downloadRunnables[i].tempLength;
            }
        }
//        LogTool.setLog("sumSize:",sumSize +"---contentLength:"+contentLength);
        // 返回已经完成的百分比
        return sumSize * 1.0 / contentLength;
    }


}