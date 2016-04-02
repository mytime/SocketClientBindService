package com.yanwanfu.mysocketclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private EditText ip;
    private EditText editText;
    private TextView text;
    private Intent i;
    private MyService.MyBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ip = (EditText) findViewById(R.id.et_ip);
        editText = (EditText) findViewById(R.id.editText);
        text = (TextView) findViewById(R.id.text);

        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.bind).setOnClickListener(this);

        i = new Intent(MainActivity.this,MyService.class);

    }

//    private Socket socket = null;
//    private BufferedWriter writer = null;
//    private BufferedReader reader = null;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind:
                bindService(i,this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.connect:
                connet();
                break;
            case R.id.send:
                send();
                break;
        }

    }

    private void connet() {
        /**
         * 从网络读取数据
         *
         * Params 参数1启动任务执行的输入参数，比如HTTP请求的URL。
         * Progress 参数2 后台任务执行的百分比。
         * Result 参数3后台执行任务最终返回的结果，比如String。
         */
        AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {

            /**
             *后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。
             * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
             * 在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
             */
            @Override
            protected Void doInBackground(Void... params) {
                try {
//                    //配置连接
//                    socket = new Socket(ip.getText().toString(), 8000);
//                    //写出
//                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    //读入
//                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    //抛出连接成功信息

//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        publishProgress(line);
//                    }

                    String line;
                    while ((line = binder.getService().getReader().readLine()) != null){
                        publishProgress(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "无法建立连接", Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            /**
             * 相当于Handler 处理UI的方式，在这里面可以使用在doInBackground
             * 得到的结果处理操作UI。 此方法在主线程执行，任务执行的结果作
             * 为此方法的参数返回
             */
            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals("@success")) {
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }
                //更新UI显示
                text.append("别人说:" + values[0] + "\n");

                super.onProgressUpdate(values);
            }
        };
        //执行异步
        read.execute();

    }

    private void send() {


        try {
            text.append("我：" + editText.getText().toString() + "\n");
            binder.getService().getWriter().write(editText.getText().toString() + "\n");
            binder.getService().getWriter().flush();
            editText.setText("");

        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            //更新自身的UI显示
//            text.append("我：" + editText.getText().toString() + "\n");
//            //发送文本框里的内容到服务器，由服务器再分发出去
//            writer.write(editText.getText().toString() + "\n");
//            //强制输出
//            writer.flush();
//
//            //发送完清空文本框
//            editText.setText("");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
         binder = (MyService.MyBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        System.out.println("断开");
    }
}
