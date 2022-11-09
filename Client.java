import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
    public static void leMenu(BufferedReader in) throws IOException
    {
        System.out.println(in.readLine());
        System.out.println(in.readLine());
        System.out.println(in.readLine());
        System.out.println(in.readLine());
        System.out.println(in.readLine());
        System.out.println(in.readLine());
    }

    public static void main(String[] args) 
    {
        // TO-DO MUDAR HOST
        try (Socket socket = new Socket("192.168.1.236", 5555))
        {
            // #region STREAMS
            // capturar output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);      

            // capturar input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // scanner p/ E/S 
            Scanner scanner = new Scanner(System.in);
            String linha = null;
            // #endregion

            // #region IDENTIFICACAO
            // 2. recebe solicitacao de identificacao do servidor
            System.out.println(in.readLine());

            // 3. envia resposta
            linha = scanner.nextLine();
            out.println(linha);
            // #endregion

            switch (linha)
            {
                // #region ATENDENTE
                case "s":
                    String opcao = null;
                    Boolean conectado = true;
                    while (conectado)
                    {
                        // 2. ler menu
                        leMenu(in);

                        // 3. envia opcao
                        opcao = scanner.nextLine();
                        out.println(opcao);
                        int tamanho = -1;

                        switch (opcao)
                        {
                            case "1":
                                tamanho = Integer.parseInt(in.readLine());
                                if (tamanho != 0)
                                {
                                    System.out.print("\nIniciando atendimento...\n");

                                    String msgatendente = null;
                                    String msgatendido = null;
                                    do
                                    {
                                        // 1. pega input de E/S e joga na stream out
                                        msgatendente = scanner.nextLine();
                                        out.println(msgatendente);
                                        
                                        if (msgatendido != null)
                                        {
                                            // 5. captura msg da stream e joga no display
                                            msgatendido = in.readLine();
                                            System.out.println(msgatendido);
                                        }

                                    } while (msgatendido != "-d");
                                }
                                else 
                                {
                                    System.out.println("\nFila zerada!\n");
                                }
                                break;

                            case "2":
                                System.out.println(in.readLine());
                                tamanho = Integer.parseInt(in.readLine());
                                if (tamanho == 0)
                                {
                                    System.out.println("\nFila zerada!\n");
                                    break;
                                }
                                for (int i = 0; i < tamanho; i++)
                                {
                                    System.out.println(in.readLine());
                                }
                                break;
                            
                            case "3":
                                System.out.println("\nDesconectando...");
                                conectado = false;
                                break;
                        }
                    }
                    break;
                // #endregion

                // #region ATENDIDO
                case "n":
                    System.out.println(in.readLine());

                    String msgatendente = null;
                    String msgatendido = null;
                    do
                    {
                        if (in.readLine() != null)
                        {
                            // 3. pega da stream in e joga em E/S
                            msgatendente = in.readLine();
                            System.out.println(msgatendente);

                            // 4. pega resposta e joga na stream out
                            msgatendido = scanner.nextLine();
                            out.println(msgatendido);
                        }

                    } while (msgatendente != "-d");

                    break;
                // #endregion
                
                default:
                    out.println("Caractere não reconhecido");
                    break;
            }

            scanner.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}