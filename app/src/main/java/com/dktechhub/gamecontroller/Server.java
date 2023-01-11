package com.dktechhub.gamecontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args)
    {
        new ServerThread().start();
    }

    public static class ServerThread extends Thread{
        int port=2004;
        ServerSocket ss;
        public boolean cancelled=false;
        @Override
        public void run() {
            super.run();
            try {
                ss=new ServerSocket(2004);
                System.out.println("Listening on port "+port);
                process(ss.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void process(Socket s)
        {   try {
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String s1 = bufferedReader.readLine();
            if(s1.length()>0&&s1.equals("INITIATE"))
            {

                      os.write("OK\r\n".getBytes());
                while(!cancelled&&!s1.equals("CLOSE"))
                {



                      s1 = bufferedReader.readLine();
                    System.out.println("Received :"+s1);
                    os.write("NEXT\r\n".getBytes());
                }

                os.write("CLOSED\r\n".getBytes());
            }
        }catch (Exception e)
            {

                e.printStackTrace();
            }

            System.out.println("Finished connection");
        }


    }
}
