import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;

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

        private static class ServerThread implements Runnable
        {
            private Socket socket;
            private BufferedReader in;
            private String msg;

            public ServerThread(Socket s) throws IOException
            {
                this.socket = s;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }

            public String getMsg()
            {
                return this.msg;
            }

            public void run()
            {
                try
                {
                    String msgrecebida = null;
                    do
                    {
                        msgrecebida = in.readLine();
                        this.msg = msgrecebida;

                    } while (msgrecebida != null || msgrecebida != "desconectar");
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
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

                                    Socket atendido = filaatendidos.remove();
                                    PrintWriter out_atendido = new PrintWriter(atendido.getOutputStream(), true);

                                    out.println(filaatendidos.size());

                                    if (filaatendidos.size() != 0)
                                    {
                                        ServerThread escutain = new ServerThread(socket);
                                        new Thread(escutain).start();

                                        String msgatendente = null;
                                        do
                                        {
                                            msgatendente = escutain.getMsg();
                                            out_atendido.println(msgatendente);
                                        } while (msgatendente != null || msgatendente != "desconectar");
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
                        Scanner teste = new Scanner(System.in);
                        out.println("Aguarde enquanto achamos um atendente para você...");

                        while (teste.nextLine() != "x")
                        {
                            System.out.println("ok");
                        }
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



