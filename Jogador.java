import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Jogador 
{
    public Socket cliente;
    public Scanner entrada;
    public PrintStream saida;
    public Tabuleiro tabuleiro;

    public Jogador(ServerSocket s) throws IOException
    {
        this.cliente = s.accept();
        this.entrada = new Scanner(cliente.getInputStream());
        this.saida = new PrintStream(cliente.getOutputStream());
        this.tabuleiro = new Tabuleiro();
    } 
}
