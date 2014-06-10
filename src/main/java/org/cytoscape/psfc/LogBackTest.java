package org.cytoscape.psfc;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

/**
 * Created by User on 6/9/2014.
 */
public class LogBackTest {
    public static void main(String[] args) {
//        org.slf4j.Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld1");
//        logger.debug("Hello world.");
        org.apache.log4j.Logger logger1 = Logger.getLogger("");
        try {
            logger1.addAppender(new FileAppender(new PatternLayout(),"d:\\Workspace\\psf\\psfc\\fileapp.log",true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger1.debug("bzzzz");


    }
}
