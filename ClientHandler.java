import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable
{
    private final Socket socket;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
}
