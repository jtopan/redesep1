import java.net.*;
import java.io.*;

public class Server 
{
    public static void main(String[] args) 
    {
        try
        {
            ServerSocket s = new ServerSocket(12345);

            while(true)
            {
                Socket c = s.accept();
                System.out.println("Jogador " + c.getInetAddress().getHostAddress() + " entrou no lobby");

            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
}
