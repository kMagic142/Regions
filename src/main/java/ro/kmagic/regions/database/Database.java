package ro.kmagic.regions.database;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {

    public static Connection connection;

    public static void connect(Logger logger, FileConfiguration config) {
        String host = config.getString("MySQL.host");
        String port = config.getString("MySQL.port");
        String database = config.getString("MySQL.database");
        String username = config.getString("MySQL.username");
        String password = config.getString("MySQL.password");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            logger.info("Connected to MySQL successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not connect to MySQL, check errors.");
        }
    }

    public static void disconnect(Logger logger){
        if(connection != null) {
            try {
                logger.info("Disconnected from MySQL database.");
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
                logger.severe("Could not disconnect from MySQL database.");
            }
        }
    }

    public static void reloadConnection(Logger logger, FileConfiguration config){
        disconnect(logger);
        connect(logger, config);
    }
}
