package com.yanwanfu.mysocketclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MyService extends Service {

    private Socket socket = null;
    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder{

       public MyService getService(){
           return MyService.this;
       }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        connect();
    }

    public void connect() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //配置连接
                    socket = new Socket("192.168.1.28", 8000);
                    //写出
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    //读入
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
