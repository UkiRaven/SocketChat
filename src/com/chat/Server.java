package com.chat;

import com.chat.db.UserLoader;
import com.chat.db.XMLUserLoader;
import com.chat.entity.Message;
import com.chat.misc.PropertyLoader;
import com.chat.misc.XMLPropertyLoader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;



/**
 * Created by lastc on 26.10.2017.
 */
public class Server {

    public static int PORT ;
    public static int FIXED_SIZE;
    public static int MAX_USERS;


    private static PropertyLoader loader;
    private static UserLoader userLoader;

    private static List<Connection> connections = new ArrayList<>();
    private static Set<String> connectedUsers = new HashSet<>();

    //Stack with FIXED_SIZE capacity
    private static Stack<Message> lastMessages = new Stack<Message>() {
        private static final long serialVersionUID = 1L;
        @Override
        public Message push(Message item) {
            if (this.size() == FIXED_SIZE) {
                System.out.println("Stack size: " + this.size());
                this.removeElementAt(0);
            }
            return super.push(item);
        }
    };

    static class Connection extends Thread {
        boolean isClosed;
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        String username;

        Connection(Socket socket) throws IOException {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }

        //When connection is established, last FIXED_SIZE messages are displayed
        private void writeLastMessages() throws IOException {
            for (Message m : lastMessages) {
                out.writeObject(m);
            }
        }

        private void notifyJoin() throws IOException {
            for (Connection c : connections) {
                c.out.writeObject(new Message("server", username + " has joined the chat"));
            }
        }

        private void notifyLeft() throws IOException {
            for (Connection c : connections) {
                c.out.writeObject(new Message("server", username + " has left the chat"));
            }
        }

        @Override
        public void run() {
            try {
                if (connections.size() < MAX_USERS) {
                    while (true) {
                        Message credentials = (Message) in.readObject();
                        if (credentials.getContent().equals("/exit")) {
                            close();
                            break;
                        }
                        if (connectedUsers.contains(credentials.getUserName())) {
                            out.writeObject(Message.ALREADY_CONNECTED_MESSAGE);
                            continue;
                        }
                        if (verifyUser(credentials.getUserName(), credentials.getContent())) {
                            out.writeObject(Message.LOGIN_SUCCESS_MESSAGE);
                            username = credentials.getUserName();
                            break;
                        } else {
                            out.writeObject(Message.INCORRECT_PASSWORD_MESSAGE);
                        }
                    }
                } else {
                    out.writeObject(Message.SERVER_IS_FULL_MESSAGE);
                }

                if (!isClosed) {
                    writeLastMessages();
                    connections.add(this);
                    connectedUsers.add(username);
                    notifyJoin();
                    Message message;
                    while (true) {
                        message = (Message) in.readObject();
                        if (message.getContent().equals("/exit")) {
                            close();
                            break;
                        }
                        lastMessages.push(message);
                        for (Connection connection : connections) {
                            connection.out.writeObject(message);
                            connection.out.flush();
                        }
                        System.out.println(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!isClosed) close();
            }
        }

        private void close() {
            connections.remove(this);
            connectedUsers.remove(username);
            isClosed = true;
            try {
                notifyLeft();
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Cannot close connection");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        init();
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            Connection connection = new Connection(socket);
            connection.start();
        }
    }

    private static void init() {
        try {
            userLoader = new XMLUserLoader();
            loader = new XMLPropertyLoader();
        } catch (IOException e) {
            System.out.println("Property file not found");
        }
        PORT = Integer.parseInt(loader.getByKey("port"));
        FIXED_SIZE = Integer.parseInt(loader.getByKey("last-messages-fixed"));
        MAX_USERS = Integer.parseInt(loader.getByKey("max-users"));
    }


    private static boolean verifyUser(String nickname, String password) {
        String pass = userLoader.getByName(nickname);
        if (pass == null) {
            try {
                userLoader.writeUser(nickname, password);
                return true;
            } catch (IOException e) {
                System.out.println("Error while saving user to file");
                return false;
            }
        }
        return pass.equals(password);
    }

}
