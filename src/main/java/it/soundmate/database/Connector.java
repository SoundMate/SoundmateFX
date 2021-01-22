package it.soundmate.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;


public class Connector {

    private static String host;
    private static String user;
    private static String password;
    private static final Logger log = LoggerFactory.getLogger( Connector.class );
    private static Connector instance = null;
    private Connection connection;

    private Connector(){
        getConnectorProperties();
    }


    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(host, user, password);
            log.info("Connected to the Database! {}",connection.getClientInfo());
        }else if (connection.isClosed()){
            connection = DriverManager.getConnection(host, user, password);
        }
        return connection;
    }

    public static Connector getInstance(){
        if(instance == null){
            instance = new Connector();
        }
        return instance;
    }

    private static void getConnectorProperties(){
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("connector.properties")).getPath();
        Properties connProp = new Properties();

        try(FileInputStream inputStream = new FileInputStream(rootPath)){

            connProp.load(inputStream);
            host = connProp.getProperty("HOST");
            user = connProp.getProperty("USER");
            password = connProp.getProperty("PASSWORD");


        } catch (NullPointerException | FileNotFoundException ex){
            throw new NullPointerException();
        } catch (IOException e) {
            log.error("IOException error, check stacktrace", e);
        }
    }
    //For testing purposes
    public String getHOST() {
        return host;
    }

    public String getUSER() {
        return user;
    }

    public String getPASSWORD() {
        return password;
    }
}












