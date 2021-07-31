package com.company;

import java.io.*;
import java.util.*;
import java.net.*;

public class Server {
    static ArrayList<ClientHandler> ar = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);
        Socket s;
        while (true) {
            s = ss.accept();
            System.out.println("New client request received : " + s);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            System.out.println("Creating a new handler for this client...");
            ClientHandler client = new ClientHandler(s, dis, dos);
            Thread t = new Thread(client);
            System.out.println("Adding this client to active client list");
            ar.add(client);
            t.start();
        }
    }
}

class ClientHandler implements Runnable
{
    String username, password;
    DataInputStream dis;
    DataOutputStream dos;
    Socket s;
    boolean isLoggedIn;
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.isLoggedIn=true;
    }
    @Override
    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                StringTokenizer st = new StringTokenizer(received, "#");
                String command = st.nextToken();
                System.out.println(received);
                if(command.equals("LogOut")){
                    dos.writeUTF("SuccessfulLogOut#");
                    this.isLoggedIn=false;
                }
                else if(command.equals("LogIn")){
                    String username = st.nextToken();
                    String password = st.nextToken();
                    System.out.println("Logging In user : " + username + ", " + password);
                    Scanner scanner = new Scanner(new FileReader("Clients.txt"));
                    boolean found = false;
                    ArrayList<String> usernames = new ArrayList<>();
                    while (scanner.hasNext())
                        usernames.add(scanner.nextLine());
                    scanner.close();
                    for(String s : usernames)
                        if((username + " " + password).equals(s)){
                            this.username = username;
                            this.password = password;
                            this.isLoggedIn = true;
                            found = true;
                            break;
                        }
                    if(found)
                        dos.writeUTF("SuccessfulLogIn#" + username);
                    if(!found)
                        dos.writeUTF("UsernameNotFound#");
                }
                else if(command.equals("search")){
                    String username = st.nextToken(), messageToSearch = st.nextToken(), temp, messages = "";
                    Scanner scanner = new Scanner(new FileReader("Messages.txt"));
                    while (scanner.hasNext()) {
                        temp = scanner.nextLine();
                        if (temp.contains("#" + username + "#") && temp.contains(messageToSearch))
                            messages = messages + temp + "%";
                    }
                    scanner.close();
                    dos.writeUTF("searched#" + messages);
                }
                else if(command.equals("getPreviousMessages")){
                    String client1 = st.nextToken(), client2 = st.nextToken(), totalMessages="";
                    Scanner scanner = new Scanner(new FileReader("Messages.txt"));
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        if (line.contains("#"+client1+"#") && line.contains("#"+client2+"#"))
                            totalMessages = totalMessages + "#" + line;
                    }
                    scanner.close();
                    System.out.println("total messages : \n" + totalMessages);
                    for(ClientHandler mc : Server.ar)
                        if(mc.username.equals(client1))
                            mc.dos.writeUTF("PreviousMessages" + totalMessages);
                }
                else if(command.equals("simpleChat")){
                    String messageToSend = st.nextToken();
                    String receiver = st.nextToken();
                    String time = st.nextToken();
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Messages.txt", true)));
                    out.println("pvChat#" + username + "#" + receiver + "#" + messageToSend + "#" + time);
                    out.close();
                    for(ClientHandler mc : Server.ar)
                        if(mc.username.equals(receiver)){
                            mc.dos.writeUTF("simpleChat#" + username + "#" + messageToSend);
                            break;
                        }
                }
                else if(command.equals("file")){
                    String fileName = st.nextToken(), fileReceiver = st.nextToken(), time = st.nextToken(), lengthString = st.nextToken();
                    byte[] bytes = new byte[(int)Long.parseLong(lengthString)];
                    dis.read(bytes, 0, bytes.length);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Messages.txt", true)));
                    out.println("file#" + username + "#" + fileReceiver + "#" + fileName + "#" + time);
                    out.close();
                    for(ClientHandler mc : Server.ar)
                        if(mc.username.equals(fileReceiver)){
                            mc.dos.writeUTF("file#" + username + "#" + mc.username + "#" + fileName + "#" + lengthString);
                            mc.dos.write(bytes, 0, bytes.length);
                            break;
                        }
                }
                else if(command.equals("SignUp")){
                    String username = st.nextToken();
                    String password = st.nextToken();
                    System.out.println(username + " " + password);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Clients.txt", true)));
                    out.println(username + " " + password);
                    out.close();
                    this.dos.writeUTF("SuccessfulSignUp");
                }
                else if(command.equals("getChatActivity")){
                    Scanner scanner = new Scanner(new FileReader("Messages.txt"));
                    ArrayList<String> users = new ArrayList<>();
                    String temp;
                    while (scanner.hasNext())
                        if((temp = scanner.nextLine()).contains("#" + username + "#")){
                            String[] strings = temp.split("#");
                            users.add(strings[1]);
                            users.add(strings[2]);
                        }
                    scanner.close();
                    users = removeDuplicates(users);
                    users.remove(this.username);
                    temp="";
                    for(String s : users)
                        temp = temp + s + "#";
                    dos.writeUTF("previousChatActivity#" + temp);
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
    private static ArrayList<String> removeDuplicates(ArrayList<String> list)
    {
        ArrayList<String> newList = new ArrayList<>();
        for (String element : list)
            if (!newList.contains(element))
                newList.add(element);
        return newList;
    }
}
