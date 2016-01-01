/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.catais.av.av_avdpool_next;

import ch.ehi.ili2db.base.Ili2dbException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.sql.SQLException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stefan
 */
public class App {

    static final Logger logger = LogManager.getLogger(App.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting at: " + new Date());

        HashMap<String, String> params = new HashMap<>();

        // Create CLI options.
        Options options = new Options();
        options.addOption(null, "config", true, "config.properties file");
        options.addOption(null, "init", false, "create database schema");

        logger.debug(options);

        try {
            // Get the CLI options.
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            String config = null;
            if (!cmd.hasOption("config")) {
                throw new MissingOptionException("Missing option '--config'.");
            } else {
                config = cmd.getOptionValue("config");
            }

            boolean init = false;
            if (cmd.hasOption("init")) {
                init = true;
            }
            
            // Read config file and set all parameters.
            InputStream input = new FileInputStream(config);

            Properties prop = new Properties();
            prop.load(input);

            logger.debug("Properties from config: " + prop);

            params.put("dbhost", prop.getProperty("dbhost", "localhost"));
            params.put("dbport", prop.getProperty("dbport", "5432"));
            params.put("dbdatabase", prop.getProperty("dbdatabase", "xanadu2"));
            params.put("dbusr", prop.getProperty("dbusr", "stefan"));
            params.put("dbpwd", prop.getProperty("dbpwd", "ziegler12"));
            params.put("defaultSrsAuth", prop.getProperty("defaultSrsAuth", "EPSG"));
            params.put("defaultSrsCode", prop.getProperty("defaultSrsCode", "2056"));
            params.put("modeldir", prop.getProperty("modeldir", "http://www.catais.org/models/"));
            params.put("loglevel", prop.getProperty("loglevel", "info"));

            params.put("dbschemaTmp", prop.getProperty("dbschemaTmp", "av_avdpool_tmp"));
            params.put("modelsTmp", prop.getProperty("modelsTmp", "DM01AVSO24LV95"));
            
            params.put("dbschema", prop.getProperty("dbschema", "av_avdpool_next"));
            params.put("models", prop.getProperty("models", "DM01AVSO24LV95_AVDPOOL"));
            
            params.put("dbusrro", prop.getProperty("dbusrro", "mspublic"));
            params.put("dbpwdro", prop.getProperty("dbpwdro", "mspublic"));
            
            logger.debug("Params: " + params);

            // Now let the party begin...
            PostgresqlDatabase pgObj = new PostgresqlDatabase(params);

            // init temporary and final schema
            if (init) {
                pgObj.initSchema();
            }
  
        } catch (ParseException e) {
            logger.error(e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Ili2dbException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } 


        logger.info("Stopping at: " + new Date());
    }
}
