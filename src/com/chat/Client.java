package com.chat;

import com.chat.entity.Message;

import java.io.*;
import java.net.Socket;

/**
 * Created by lastc on 26.10.2017.
 */
public class Client {

    //TODO fix exception on socket close
    static class Receiver extends Thread {
        boolean isClosed;
        ObjectInputStream in;

        Receiver(ObjectInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (!isClosed) {
                    System.out.println(in.readObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                this.close();
            }
        }

        public void close() {
            isClosed = true;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 1488);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your nickname:");
        String username = reader.readLine();

        Message answer;
        do {
            System.out.println("Enter your password:");
            String password = reader.readLine();
            out.writeObject(new Message(username, password));
            answer = (Message) in.readObject();
            System.out.println(answer);
        } while (answer.equals(Message.INCORRECT_PASSWORD_MESSAGE) || answer.equals(Message.ALREADY_CONNECTED_MESSAGE));

        Receiver receiver = new Receiver(in);
        receiver.start();
        System.out.println("Type messages:");
        String message;
        do {
            message = reader.readLine();
            out.writeObject(new Message(username, message));
        }
        while (!message.equals("/exit"));
        receiver.close();
        out.close();
        in.close();
        socket.close();
    }
}
