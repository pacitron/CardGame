import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public Client() {
            try {
                Scanner keyinput = null;
                Socket socket = null;
                while (true) {
                    try {
                        keyinput = new Scanner(System.in);
                        System.out.println("Enter the server ip address");
                        String ip = keyinput.nextLine();
                        System.out.println("connecting...");
                        socket = new Socket(ip, 1234);
                        break;
                    } catch (Exception e) {
                        System.out.println("Server doesn't exist...");
                    }
                }
                PrintWriter outputwriter = new PrintWriter(socket.getOutputStream());

                ListenThread lt = new ListenThread(socket);
                lt.start();

                while (true) {
                    String command = keyinput.nextLine();
                    if(command.length()>3){
                        System.out.println("Invalid move...\nTry again");
                        continue;
                    }
                    outputwriter.println(command);
                    outputwriter.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
    }

    public static void main(String[] args) {
        new Client();
    }

    private class ListenThread extends Thread {
        Socket socket = null;

        public ListenThread(Socket socket) {
            super("ListenThread");
            this.socket=socket;
        }

        public void run() {
            try {
                PrintWriter outputWriter = new PrintWriter(socket.getOutputStream());
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boolean end = false;
                while (!end) {
                    //wait for commands
                    String message = inputReader.readLine();
                    if (message != null)
                        System.out.println(message);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.print("----GAME OVER----");
                System.exit(1);
            }
        }
    }
}
