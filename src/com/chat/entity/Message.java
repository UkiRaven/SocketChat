package com.chat.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lastc on 27.10.2017.
 */
public class Message implements Serializable{

    public static final Message INCORRECT_PASSWORD_MESSAGE = new Message(new Date(0), "server", "Incorrect password, try again");
    public static final Message LOGIN_SUCCESS_MESSAGE = new Message(new Date(0), "server", "Welcome to the chat");
    public static final Message SERVER_IS_FULL_MESSAGE = new Message(new Date(0),"server", "Too many users, server is full. Beat somebody's face and get his place");
    public static final Message ALREADY_CONNECTED_MESSAGE = new Message(new Date(0), "server", "This user is already logged in");

    private Date date;
    private String userName;
    private String content;

    public Message(String userName, String content) {

        this.date = new Date();
        this.userName = userName;
        this.content = content;
    }

    public Message(Date date, String userName, String content) {

        this.date = date;
        this.userName = userName;
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("%tH:%<tM:%<tS %s: %s", date, userName, content);
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) return false;
        Message msg = (Message) obj;
        return this.content.equals(msg.content) && this.userName.equals(msg.userName) && this.date.equals(msg.date);
    }
}
