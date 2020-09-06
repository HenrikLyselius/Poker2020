package com.lyselius.connection;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Logging {





    public static Logger getLogger(String name)
    {
        final Logger logger = Logger.getLogger(name);
        Handler fileHandler = null;

        try { fileHandler = new FileHandler("./logs/" + logger.getName() + ".log"); }
        catch (IOException e) { e.printStackTrace(); }

        logger.addHandler(fileHandler);

        return logger;
    }
}
