package com.chat.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by lastc on 27.10.2017.
 */
public class XMLPropertyLoader implements PropertyLoader {

    private final static Path PATH = Paths.get("resources\\properties.tld");

    private Properties properties = new Properties();

    public XMLPropertyLoader() throws IOException {
        properties = new Properties();
        properties.loadFromXML(Files.newInputStream(PATH));

    }

    @Override
    public String getByKey(String key) {
        return properties.getProperty(key);
    }

}
