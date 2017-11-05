package com.chat.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by lastc on 04.11.2017.
 */
public class XMLUserLoader implements UserLoader {
    private final static Path PATH = Paths.get("resources\\users.tld");
    private Properties users = new Properties();

    public XMLUserLoader() throws IOException {
        users = new Properties();
        users.loadFromXML(Files.newInputStream(PATH));
    }

    @Override
    public String getByName(String name) {
        return users.getProperty(name);
    }

    @Override
    public void writeUser(String name, String password) throws IOException {
        users.put(name, password);
        OutputStream usersXml = new FileOutputStream(PATH.toFile());
        users.storeToXML(usersXml, "user info");
        usersXml.close();
    }
}
