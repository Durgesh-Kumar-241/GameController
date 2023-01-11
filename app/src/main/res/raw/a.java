


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class a {
        static int port=2004;
        static Robot r;
        static ServerSocket ss;
        public static boolean cancelled=false;
    public static void main(String[] args)
    {
        //new ServerThread().start();
         try{
            r=new Robot();
            }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
                ss=new ServerSocket(2004);
                System.out.println("Listening on port "+port);
                process(ss.accept());
                //r=new Robot();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


/*public static class ServerThread{
        
        ServerThread()
        {  

        }
        @Override
        public void run() {
            super.run();

            
        }

    } */
    
      static void process(Socket s)
        {   System.out.println("Received connection");
            try {
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String s1 = bufferedReader.readLine();
                //System.out.println(s1);
            if(s1.length()>0&&s1.equals("IN"))
            {
                os.write("OK\r\n".getBytes());
                while(!cancelled&&!s1.equals("CE"))
                {
                    s1 = bufferedReader.readLine();
                    simulate(s1);
                    System.out.println(s1);
                    os.write("NE\r\n".getBytes());
                }
                os.write("CD\r\n".getBytes());
            }
            is.close();
            os.close();
            bufferedReader.close();
            s.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

            System.out.println("Finished connection");
        }

    



    static void simulate(String sim)
        {
            if(sim==null||sim.length()==0)
                return;

            try{
                int k = Integer.parseInt(sim);
                if(k<0)
                    r.keyRelease(-k);

                else r.keyPress(k);
                     }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        
}
