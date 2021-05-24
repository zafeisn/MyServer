package com.example.myserver;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.example.myserver.MainActivity.tv_conn_state;

public class SocketServer {
    private String ip;
    private int port;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SocketServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 进行socket通信
                    Socket socket = new Socket(ip, port);
                    // 获取输入输出流
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    tv_conn_state.setText(" 服务器连接成功");
                    Log.i("连接" , "开始连接服务器... " + ip + " " + port);

                } catch (IOException e) {
                    Log.i("连接" , "服务器连接失败... " + e.getMessage());
                    e.printStackTrace();
                    return;  // 异常结束，跳出线程
                }
            }
        }).start();

    }

    // 发送内容到服务器
    public void sendMsg(final String MSG) {

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
                    String recevieMsg = inputStream.toString();
                    Log.v("接收：","接收成功：" + recevieMsg);
                } catch (IOException e) {
                    Log.v("接收：","接收失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

