package com.example.myserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static Button bt_connect,bt_send,bt_receive;
    EditText ipText,portText,ed_send_msg,ed_receive_msg;
    public static TextView tv_conn_state,tv_send_state,tv_receive_state;

    ConnServer connServer;
    int id;  // 控制发送和接收按钮
    boolean temp_conn=false, temp_send=false, temp_recevie=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        __initComponent__();

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipText.getText().toString();
                int port = Integer.parseInt(portText.getText().toString());
                connServer = new ConnServer(ip, port);
                connServer.getConn();
            }
        });
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connServer.sendMsg(ed_send_msg.getText().toString());
            }
        });

        bt_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connServer.receiveMsg();
            }
        });
    }

    // 初始化
    public void __initComponent__() {

        bt_connect = findViewById(R.id.bt_connect);
        bt_send = findViewById(R.id.bt_send);
        bt_receive = findViewById(R.id.bt_receive);
        ipText = findViewById(R.id.ipText);
        portText = findViewById(R.id.portText);
        ed_send_msg = findViewById(R.id.ed_send_msg);
        ed_receive_msg = findViewById(R.id.ed_receive_msg);
        tv_conn_state = findViewById(R.id.tv_conn_state);
        tv_send_state = findViewById(R.id.tv_send_state);
        tv_receive_state = findViewById(R.id.tv_receive_state);

    }


    // 开启线程
    class ConnServer extends Thread{

        private String ip;
        private int port;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnServer(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public void getConn() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 进行socket通信
                        Socket socket = new Socket(ip, port);
                        // 获取输入输出流
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        //tv_conn_state.setText(" 服务器连接成功");
                        Log.i("连接", "开始连接服务器... " + ip + " " + port);
                    } catch(IOException e) {
                        Log.i("连接", "服务器连接失败... " + e.getMessage());
                        e.printStackTrace();
                        return;  // 异常结束，跳出线程
                    }
                }
            }).start();
        }

        // 发送内容到服务器
        public void sendMsg(final String MSG){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String msg = MSG;
                    msg += "#";  // 以#结尾
                    try {
                        outputStream.write(msg.getBytes());
                        outputStream.flush(); // 关闭
                        Log.d("发送","发送成功：" + msg);

                    } catch (IOException e) {
                        Log.d("发送","发送失败：" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        // 接收服务器返回的数据
        public void receiveMsg() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 线程安全
                    StringBuffer stringBuffer = new StringBuffer();
                    try {
                        int assc = inputStream.read();
                        while (assc != '#') {
                            stringBuffer.append((char) assc);  // 存入
                            assc = inputStream.read();  // 继续读取
                        }
                        String recevieMsg = stringBuffer.toString();
                        String reMsg = new String(recevieMsg.getBytes("ISO-8859-1"),"utf-8");
                        Log.v("接收", "接收成功：" + reMsg);
                    } catch (IOException e) {
                        Log.v("接收", "接收失败：" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}