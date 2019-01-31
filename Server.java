import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private Hashtable<String, Server.ConnectionThread> connections= new Hashtable<String, ConnectionThread>();
    private Hashtable<Server.ConnectionThread, String> reverse= new Hashtable<ConnectionThread, String>();
    private String[][] main= {{"2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS","AS"},
                              {"2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH","AH"},
                              {"2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD","AD"},
                              {"2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC","AC"}};
    private String[] deck= {"2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS","AS",
            "2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH","AH",
            "2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD","AD",
            "2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC","AC"};
    private String[] player1=new String[13];
    private String[] player2=new String[13];
    private String[] player3=new String[13];
    private String[] player4=new String[13];
    private int round = 1;
    private int teamABid=0;
    private int teamBBid=0;
    private int teamAtricks=0;
    private int teamBtricks=0;
    private int teamAPoints=0;
    private int teamBPoints=0;
    private boolean gameOver=false;
    private int mainRound=1;
    private boolean teamAWon=false;
    private boolean teamBWon=false;
    private String[] thisTrick=new String[4];
    private int tcount=0;
    private int max;
    private int max1;
    private int max2;
    private  int trick=0;
    private char suit;

//----------------------------------------------------------------------------------------------------------------------

    public Server(){
        ServerSocket serversocket= null;
        try {
            serversocket= new ServerSocket(1234);
        } catch (IOException e) {
            System.err.println("Could not listen on port...");
            System.exit(-1);
        }
        System.out.println("Server listening...");
        int count=0;
        MainThread t1=new MainThread();
        t1.start();
        while (count<=4){
            try {
                Socket s1=serversocket.accept();
                System.out.println("Player-"+(count+1)+" joined");
                count++;

                ConnectionThread st1 = new ConnectionThread(s1,count);
                st1.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

//----------------------------------------------------------------------------------------------------------------------

    public class ConnectionThread extends Thread{
        ConnectionThread connection1=null;
        ConnectionThread connection2=null;
        ConnectionThread connection3=null;
        ConnectionThread connection4=null;
        private Socket socket = null;
        private OutputStream outputStream = null;
        private InputStream inputStream = null;
        private int number = 0;
        private int turn = 0;

        public ConnectionThread(Socket socket, int no){
            super("ConnectionThread");
            this.socket=socket;
            number=no;

        }

        public void run(){
            try {
                outputStream= socket.getOutputStream();
                inputStream = socket.getInputStream();
                BufferedReader inputreader= new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter outputwriter= new PrintWriter(outputStream);

                System.out.println("ListenThread for player " + number);
                connections.put(Integer.toString(number),this);
                reverse.put(this,Integer.toString(number));
                outputwriter.println("Waiting for other players to join....");
                outputwriter.flush();
                while (connections.get("4")==null){
                    continue;
                }
                connection1=connections.get("1");
                connection2=connections.get("2");
                connection3=connections.get("3");
                connection4=connections.get("4");
                outputwriter.println("Game begins...\nYou Are : "+number+"\n\t\tTEAM A: 1&3 \tTEAM B: 2&4\n");
                outputwriter.flush();
                while (true){
                    continue;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//----------------------------------------------------------------------------------------------------------------------

        private void trickWin() {
            PrintWriter outputwriter=new PrintWriter(outputStream);
            outputwriter.println("\tYou won this trick!!!");
            outputwriter.flush();

        }

        private void displayBids() {
            PrintWriter outputwriter= new PrintWriter(outputStream);
            outputwriter.println("TEAM A BID: "+teamABid+"\nTEAM B BID: "+teamBBid);
            outputwriter.flush();
        }

        private int yourBid() {
            int bid=0;
            int imp=0;
            try {
                BufferedReader inputreader= new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter outputwriter= new PrintWriter(outputStream);
                do {
                    imp=0;
                    outputwriter.println("Enter your bid:");
                    outputwriter.flush();
                    String input = inputreader.readLine();
                    try {
                        bid=Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        outputwriter.println("Impossible bid number... ");
                        outputwriter.flush();
                        imp=1;
                        continue;
                    }
                    if(bid>13 || bid<0){
                        outputwriter.println("Impossible bid number... ");
                        outputwriter.flush();
                    }
                } while (bid>13 || bid<0 || imp==1);
                outputwriter.println("Got it!");
                outputwriter.flush();
            } catch (IOException e) {
                System.out.println("-");
            }
            return bid;

        }

        private void yourTurn(int who,String[] player) {
            try {
                BufferedReader inputreader= new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter outputwriter= new PrintWriter(outputStream);
                outputwriter.println("your turn:");
                outputwriter.flush();
                while (true) {
                    String input = inputreader.readLine();
                    input=input.toUpperCase();
                    int check=0;
                    int check1=0;
                    for(int i=0;i<player.length;i++){
                        if(input.equals(player[i])) {
                            if (tcount > 0) {
                                if (input.charAt(input.length() - 1) != suit) {
                                    for (String value : player) {
                                        if (value.charAt(value.length() - 1) == suit) {
                                            outputwriter.println("You have a card of current suit. please use it");
                                            outputwriter.flush();
                                            check1 = 1;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (check1 == 1) {
                                continue;
                            }

                            player[i] = " ";
                            thisTrick[tcount] = input;
                            tcount++;
                            connection1.displayCard(who, input);
                            connection2.displayCard(who, input);
                            connection3.displayCard(who, input);
                            connection4.displayCard(who, input);
                            check = 1;
                        }
                        }

                    if(check==1){
                        outputwriter.println("Got it!");
                        outputwriter.flush();
                        break;
                    }
                    outputwriter.println("Invalid move... try again");
                    outputwriter.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void displayRound(){
            PrintWriter outputwriter=new PrintWriter(outputStream);
            outputwriter.println("\t\t\t----------Round:"+mainRound+"----------");
            outputwriter.flush();
        }

        private void displayDeck(String[] player){
            String display="";
            PrintWriter outputwriter=new PrintWriter(outputStream);
            for(int i=0;i<player.length;i++){
                display+=player[i]+", ";
            }
            outputwriter.println("------------------------------------------------------------\n"+"Your Cards: "+display);
            outputwriter.flush();
        }

        private void displayCard(int who,String what) {
            PrintWriter outputwriter=new PrintWriter(outputStream);
            outputwriter.println("Player "+who+" played: "+what);
            outputwriter.flush();
        }

        private void diaplayBidsWon(){
            PrintWriter outputwriter=new PrintWriter(outputStream);
            outputwriter.println("\tTEAM A: "+teamAtricks+"/"+teamABid+"\tTEAM B: "+teamBtricks+"/"+teamBBid);
            outputwriter.flush();
        }

        private void displayPoints(){
            PrintWriter outputwriter=new PrintWriter(outputStream);
            outputwriter.println("\tTEAM A POINTS: "+teamAPoints+"\tTEAM B POINTS: "+teamBPoints);
            outputwriter.flush();
        }

        public void displayWin(char a) {
            PrintWriter outputwriter= new PrintWriter(outputStream);
            outputwriter.println("-.-.-.-.-.-.-TEAM "+a+" WON-.-.-.-.-.-.-");
            outputwriter.flush();
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    private class MainThread extends Thread{
        ConnectionThread connection1=null;
        ConnectionThread connection2=null;
        ConnectionThread connection3=null;
        ConnectionThread connection4=null;
        private Socket socket = null;
        private OutputStream outputStream = null;
        private InputStream inputStream = null;
        private int number = 0;
        private int turn = 0;
        private String[] player=new String[13];
        int checkNum=0;
        boolean check=true;

        public MainThread(){
            super("MainThread");
        }

        public void shuffle(){
                Collections.shuffle(Arrays.asList(deck));
                for(int c=0;c<52;c++){
                    if(c<13){
                        player1[c]= deck[c];
                    } else if(c<26){
                        player2[c-13]= deck[c];
                    } else if(c<39){
                        player3[c-26]= deck[c];
                    } else{
                        player4[c-39]= deck[c];
                    }
                }
                Arrays.sort(player1);
            Arrays.sort(player1, (String a, String b) -> Integer.compare(b.charAt(b.length() - 1), a.charAt(a.length() - 1)));
                Arrays.sort(player2,Comparator.naturalOrder());
            Arrays.sort(player2, (String a, String b) -> Integer.compare(b.charAt(b.length() - 1), a.charAt(a.length() - 1)));
                Arrays.sort(player3,Comparator.reverseOrder());
            Arrays.sort(player3, (String a, String b) -> Integer.compare(b.charAt(b.length() - 1), a.charAt(a.length() - 1)));
                Arrays.sort(player4,Comparator.reverseOrder());
            Arrays.sort(player4, (String a, String b) -> Integer.compare(b.charAt(b.length() - 1), a.charAt(a.length() - 1)));
        }

        public void run(){

            while (connections.get("4")==null){
                continue;
            }
            connection1=connections.get("1");
            connection2=connections.get("2");
            connection3=connections.get("3");
            connection4=connections.get("4");

            while (!gameOver){
                connection1.displayRound();
                connection2.displayRound();
                connection3.displayRound();
                connection4.displayRound();

                //shuffle and sort cards
                shuffle();

                //display cards
                connection1.displayDeck(player1);
                connection2.displayDeck(player2);
                connection3.displayDeck(player3);
                connection4.displayDeck(player4);

                //enter and display bids
                int bidCount=1;
                while (bidCount<5) {
                    turn=round%4;
                    if (turn==1){
                        teamABid+=connection1.yourBid();
                    } else if(turn==2){
                        teamBBid+=connection2.yourBid();
                    } else if(turn==3){
                        teamABid+=connection3.yourBid();
                    } else {
                        teamBBid+=connection4.yourBid();
                    }
                    round++;
                    bidCount++;
                }
                connection1.displayBids();
                connection2.displayBids();
                connection3.displayBids();
                connection4.displayBids();

                //play
                turn=round%4;
                while (trick<13) {
                    if (turn==1){
                        connection1.yourTurn(1,player1);
                        suit= thisTrick[0].charAt(thisTrick[0].length() - 1);
                        connection2.yourTurn(2,player2);
                        connection3.yourTurn(3,player3);
                        connection4.yourTurn(4,player4);
                    } else if(turn==2){
                        connection2.yourTurn(2,player2);
                        suit= thisTrick[0].charAt(thisTrick[0].length() - 1);
                        connection3.yourTurn(3,player3);
                        connection4.yourTurn(4,player4);
                        connection1.yourTurn(1,player1);
                    } else if(turn==3){
                        connection3.yourTurn(3,player3);
                        suit= thisTrick[0].charAt(thisTrick[0].length() - 1);
                        connection4.yourTurn(4,player4);
                        connection1.yourTurn(1,player1);
                        connection2.yourTurn(2,player2);
                    } else {
                        connection4.yourTurn(4,player4);
                        suit= thisTrick[0].charAt(thisTrick[0].length() - 1);
                        connection1.yourTurn(1,player1);
                        connection2.yourTurn(2,player2);
                        connection3.yourTurn(3,player3);
                    }

                    for(int i=0;i<thisTrick.length;i++){
                        for (int j=0;j<4;j++){
                            for(int k=0;k<13;k++){
                                if(thisTrick[i].equals(main[j][k])){
                                    if(i==0){
                                        max1=j;
                                        max2=k;
                                        max=i;
                                    }
                                    if(j==max1){
                                        if(k>max2){
                                            max=i;
                                            max2=k;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    max++;
                    if(turn==1){
                        if(max==1){
                            connection1.trickWin();
                            teamAtricks++;
                            turn=1;
                        }else if(max==2){
                            connection2.trickWin();
                            teamBtricks++;
                            turn=2;
                        } else if(max==3){
                            connection3.trickWin();
                            teamAtricks++;
                            turn=3;
                        } else{
                            connection4.trickWin();
                            teamBtricks++;
                            turn=4;
                        }
                    }else if(turn==2){
                        if(max==1){
                            connection2.trickWin();
                            teamBtricks++;
                            turn=2;
                        }else if(max==2){
                            connection3.trickWin();
                            teamAtricks++;
                            turn=3;
                        } else if(max==3){
                            connection4.trickWin();
                            teamBtricks++;
                            turn=4;
                        } else{
                            connection1.trickWin();
                            teamAtricks++;
                            turn=1;
                        }
                    } else if(turn==3){
                        if(max==1){
                            connection3.trickWin();
                            teamAtricks++;
                            turn=3;
                        }else if(max==2){
                            connection4.trickWin();
                            teamBtricks++;
                            turn=4;
                        } else if(max==3){
                            connection1.trickWin();
                            teamAtricks++;
                            turn=1;
                        } else{
                            connection2.trickWin();
                            teamBtricks++;
                            turn=2;
                        }
                    } else{
                        if(max==1){
                            connection4.trickWin();
                            teamBtricks++;
                            turn=4;
                        }else if(max==2){
                            connection1.trickWin();
                            teamAtricks++;
                            turn=1;
                        } else if(max==3){
                            connection2.trickWin();
                            teamBtricks++;
                            turn=2;
                        } else{
                            connection3.trickWin();
                            teamAtricks++;
                            turn=3;
                        }
                    }
                    tcount=0;
                    trick++;
                    thisTrick=new String[4];

                    //display cards
                    connection1.displayDeck(player1);
                    connection2.displayDeck(player2);
                    connection3.displayDeck(player3);
                    connection4.displayDeck(player4);
                }

                if(teamAtricks<teamABid){
                    teamAPoints-=(teamABid*10);
                } else{
                    teamAPoints+=(teamABid*10);
                    teamAPoints+=(teamAtricks-teamABid);
                }
                if(teamBtricks<teamBBid){
                    teamBPoints-=(teamBBid*10);
                } else{
                    teamBPoints+=(teamBBid*10);
                    teamBPoints+=(teamBtricks-teamBBid);
                }
                connection1.diaplayBidsWon();
                connection1.displayPoints();
                connection2.diaplayBidsWon();
                connection2.displayPoints();
                connection3.diaplayBidsWon();
                connection3.displayPoints();
                connection4.diaplayBidsWon();
                connection4.displayPoints();

                if(teamAPoints==teamBPoints){
                } else if(teamAPoints>=250 && teamBPoints>=250 || teamAPoints<-100 && teamBPoints<-100){
                    if(teamAPoints>teamBPoints){
                        teamAWon=true;
                    } else{
                        teamBWon=true;
                    }
                } else if(teamAPoints>=250){
                    teamAWon=true;
                } else if(teamBPoints>=250){
                    teamBWon=true;
                } else if(teamAPoints<-100){
                    teamBWon=true;
                } else if(teamBPoints<-100){
                    teamAWon=true;
                }

                if (teamAWon) {
                    connection1.displayWin('A');
                    connection2.displayWin('A');
                    connection3.displayWin('A');
                    connection4.displayWin('A');
                    gameOver=true;
                } else if(teamBWon){
                    connection1.displayWin('B');
                    connection2.displayWin('B');
                    connection3.displayWin('B');
                    connection4.displayWin('B');
                    gameOver=true;
                }
                mainRound++;
                round++;
                trick=0;
                teamABid=0;
                teamBBid=0;
                player1=new String[13];
                player2=new String[13];
                player3=new String[13];
                player4=new String[13];
                teamAtricks=0;
                teamBtricks=0;
            }
            System.exit(1);
        }
    }
}