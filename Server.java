import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class Server 
{   
    public static void main(String[] args) 
    {
        ServerSocket server = null;

        try
        {
            server = new ServerSocket(5555);
            server.setReuseAddress(true);
            Queue<Socket> filaatendidos = new LinkedList<Socket>();
            Queue<Socket> filaatendentes = new LinkedList<Socket>();
            
            System.out.println("\n***Servidor Iniciado***\n");
            // loop infinito p/ capturar conexoes
            while (true)
            {
                Socket clientSocket = server.accept();

                // mostra em tela a notificacao de conexao
                System.out.println("Nova conexão: " + clientSocket.getInetAddress().getHostAddress());

                // cria uma nova thread
                ClientHandler client = new ClientHandler(clientSocket, filaatendidos, filaatendentes);
                new Thread(client).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (server != null)
            {
                try
                {
                    server.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable
    {
        public Socket socket;
        public Queue<Socket> filaatendidos;
        public Queue<Socket> filaatendentes;
        
        // socket do cliente
        public ClientHandler(Socket socket, Queue<Socket> filaatendidos, Queue<Socket> filaatendentes)
        {
            this.socket = socket;
            this.filaatendidos = filaatendidos;
            this.filaatendentes = filaatendentes;
        }

        public void enviaMenu(PrintWriter out)
        {   
            out.println("------------------------------");
            out.println("Escolha uma opção: ");
            out.println(" ");
            out.println("1. Iniciar atendimento");
            out.println("2. Consultar fila");
            out.println("3. Deslogar");
        }

        public void enviaFila(PrintWriter out, Queue<Socket> fila)
        {
            int i = 1;
            for (Socket socket : fila) 
            {
                out.println(i + ": " + socket.getInetAddress().getHostAddress());
                i++;
            }
        }

        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;

            try
            {
                // #region STREAMS
                // captura in e output stream
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //#endregion

                // #region IDENTIFICACAO
                // 1. solicita input de identificacao do usuario
                out.println("Bem vindo! Por favor, identifique-se. Você é um atendente? (s/n) ");

                // 4. recebe resposta
                String linha = in.readLine();
                // #endregion

                switch (linha)
                {
                    // #region ATENDENTE
                    case "s":

                        String opcao = null;
                        Boolean conectado = true;
                        while (conectado)
                        {
                            // 1. mostrar menu
                            enviaMenu(out);

                            // 4. recebe opcao
                            opcao = in.readLine();

                            switch (opcao)
                            {
                                case "1":
                                    this.filaatendentes.add(socket);

                                    out.println(filaatendidos.size());

                                    if (filaatendidos.size() != 0)
                                    {
                                        Socket atendido = filaatendidos.remove();
                                        PrintWriter out_atendido = new PrintWriter(atendido.getOutputStream(), true);

                                        String msgatendente = null;
                    
                                        // repassa a msg ao atendido e ouve pela resposta
                                        do
                                        {
                                            msgatendente = in.readLine();
                                            if (msgatendente != null)
                                            {
                                                // 2. pega da stream in e joga na stream out do atendido
                                                out_atendido.println(msgatendente);
                                            }
            
                                        } while (msgatendente != "-d");
                                    }
                                    break;

                                case "2":
                                    out.println("------------------------------");
                                    out.println(filaatendidos.size());
                                    if (filaatendidos.size() == 0) break;
                                    enviaFila(out, filaatendidos);
                                    break;
                                
                                case "3":
                                    conectado = false;
                                    break;
                            }
                        }
                        break;
                    // #endregion

                    // #region ATENDIDO
                    case "n":
                        this.filaatendidos.add(socket);
                        out.println("Aguarde enquanto achamos um atendente para você...");

                        Socket atendente = filaatendentes.remove();
                        PrintWriter out_atendente = new PrintWriter(atendente.getOutputStream(), true);

                        String msgatendido = null;
                        do
                        {   
                            // 5. pega da stream in e joga na stream out do atendente
                            msgatendido = in.readLine();
                            out_atendente.println(msgatendido);

                        } while (msgatendido != "-d");

                        break;
                    // #endregion

                    default:
                        out.println("Caractere não reconhecido");
                        break;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (out != null)
                    {
                        out.close();
                    }
                    if (in != null)
                    {
                        in.close();
                        socket.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    

    
}



