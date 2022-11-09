import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
    private static class ClientThread implements Runnable
    {
        private BufferedReader in;

        public ClientThread(BufferedReader in) throws IOException
        {
            this.in = in;
        }

        public void run()
        {
            try
            {
                String msgrecebida = null;
                do
                {
                    msgrecebida = in.readLine();
                    System.out.println(msgrecebida);

                } while (msgrecebida != null || msgrecebida != "desconectar");
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }

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

                                    ClientThread escutain = new ClientThread(in);
                                    new Thread(escutain).start();

                                    String msgatendente = null;
                                    do
                                    {
                                        msgatendente = scanner.nextLine();
                                        out.println(msgatendente);

                                    } while (msgatendente != null || msgatendente != "desconectar");
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

                    ClientThread escutainserver = new ClientThread(in);
                    new Thread(escutainserver).start();

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