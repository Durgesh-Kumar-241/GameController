package com.dktechhub.gamecontroller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


public class Client {
    String remoteIp="127.0.0.1";
    int port = 2004;
    boolean running=false;
    BackgroundThread backgroundThread;
    OnConnectionStateChangeListener onConnectionStateChangeListener;
    public interface OnConnectionStateChangeListener{
        void onConnectionSuccess();
        void onConnectionBreak();
    }
    public interface OnConnectionStateChangeListener2{
        void onConnectionSuccess();
        void onConnectionBreak();
    }

    public void connect(String remoteIp,OnConnectionStateChangeListener monConnectionStateChangeListener)
    {
        this.remoteIp=remoteIp;
        onConnectionStateChangeListener=monConnectionStateChangeListener;
        Log.d("TAG","remote "+remoteIp);

            backgroundThread= new BackgroundThread(remoteIp,new OnConnectionStateChangeListener2() {
                @Override
                public void onConnectionSuccess() {
                    runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            onConnectionStateChangeListener.onConnectionSuccess();

                        }
                    });
                }

                @Override
                public void onConnectionBreak() {
                    runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            onConnectionStateChangeListener.onConnectionBreak();
                            running=false;
                        }
                    });
                }
            });
            backgroundThread.start();
            running=true;
            Log.d("TAG","started");
        }


    public void send(String text)
    {
        if(running&&backgroundThread!=null)
            backgroundThread.send(text);
    }

    public void close()
    {
        if(running&&backgroundThread!=null)
        {
            backgroundThread.send("CLOSE");
            backgroundThread.run=false;
        }
    }

    static class BackgroundThread extends Thread{
        String remoteIP;
        int port=2004;
        String sendBuffer="";
        boolean run=true;
        OnConnectionStateChangeListener2 onConnectionStateChangeListener2;

        BackgroundThread(String remote,OnConnectionStateChangeListener2 monConnectionStateChangeListener2)
        {
            onConnectionStateChangeListener2=monConnectionStateChangeListener2;
            remoteIP=remote;
        }

        public void send(String toSend)
        {
            sendBuffer+=toSend+"\r\n";
            //StringBuilder stringBuilder= new StringBuilder();

        }
        @Override
        public void run() {
            super.run();
            Log.d("TAG","Connection thread");
            try{
                Socket s = new Socket(remoteIP,port);
                InputStream is = s.getInputStream();
                OutputStream os = s.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                onConnectionStateChangeListener2.onConnectionSuccess();
                os.write("IN\r\n".getBytes());
                String s1=bufferedReader.readLine();
                //Log.d("TAG",s1);
                if(s1.length()>0&&s1.equals("OK"))
                {
                    while(run&&!s1.equals("CD")){
                        if(sendBuffer.length()>0)
                        {
                            os.write(sendBuffer.getBytes());
                            sendBuffer="";
                            s1=bufferedReader.readLine();
                            //System.out.println("received "+s1);
                        }
                    }
                }
                    is.close();
                os.close();bufferedReader.close();
                s.close();


            }catch (IOException e)
            {
                e.printStackTrace();
                onConnectionStateChangeListener2.onConnectionBreak();
            }
        }
    }

    public void runOnUIThread(Runnable r)
    {
        new Handler(Looper.getMainLooper()).post(r);
    }
}
