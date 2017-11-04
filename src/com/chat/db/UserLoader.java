package com.chat.db;

import java.io.IOException;

/**
 * Created by lastc on 04.11.2017.
 */
public interface UserLoader {

    String getByName(String name);
    void writeUser(String name, String pass) throws IOException;
}
