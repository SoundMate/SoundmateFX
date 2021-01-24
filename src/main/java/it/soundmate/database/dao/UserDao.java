/*
 * Copyright (c) 2021.
 * Created by Lorenzo Pantano on 08/01/21, 21:51
 * Last edited: 08/01/21, 20:19
 */

package it.soundmate.database.dao;

import it.soundmate.bean.LoggedBean;
import it.soundmate.bean.LoginBean;
import it.soundmate.bean.registerbeans.RegisterBean;
import it.soundmate.database.Connector;
import it.soundmate.database.dbexceptions.RepositoryException;
import it.soundmate.exceptions.UpdateException;
import it.soundmate.model.*;
import it.soundmate.utils.ImgBase64Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.sql.*;

public class UserDao implements Dao<User> {


    private final Connector connector = Connector.getInstance();
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);
    private static final String ERR_INSERT = "Error inserting user";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";


    @Override
    public int register(RegisterBean registerBean){
        ResultSet resultSet;
        String sql = "WITH ins1 AS (\n" +
                "     INSERT INTO registered_users (email, password, user_type, city)\n" +
                "         VALUES (?, ?, ?, ?)\n" +
                "         RETURNING id AS sample_id),\n" +
                "     ins2 AS (\n" +
                "     INSERT INTO users (id)\n" +
                "         SELECT sample_id FROM ins1)\n" +
                "SELECT sample_id FROM ins1;";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registerBean.getEmail());
            pstmt.setString(2, registerBean.getPassword());
            pstmt.setString(3, registerBean.getUserType().toString());
            pstmt.setString(4, registerBean.getCity());
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);
            else throw new RepositoryException("Error registering user");
        }
        catch (SQLException ex) {
            throw new RepositoryException(ERR_INSERT, ex);
        }
    }

    public LoggedBean login(LoginBean loginBean) throws LoginException {

        String sql = "SELECT id, email, password, user_type FROM registered_users WHERE email = ? AND password = ?";
        ResultSet resultSet;

        try(Connection conn = connector.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, loginBean.getEmail());
            preparedStatement.setString(2, loginBean.getPassword());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String mail = resultSet.getString(EMAIL);
                String psw = resultSet.getString(PASSWORD);
                String userType = resultSet.getString("user_type");


                if (mail.equals(loginBean.getEmail()) && psw.equals(loginBean.getPassword())){
                    LoggedBean loggedBean = new LoggedBean(userType, id);
                    loggedBean.setQueryResult(true);
                    return loggedBean;
                }
            }
        } catch (SQLException sqlException){
            throw new RepositoryException("Error: DB not responding! \n Check stacktrace for details");
        }
        throw new LoginException("User not found");
    }

    public void deleteAll() {
        String sql = "DELETE FROM registered_users";
        int delRecs;
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            delRecs = stmt.executeUpdate();
            if (delRecs >= 1) log.info("\t ***** user entries successfully cleaned! *****");
            resetID();

        } catch (SQLException ex) {
            throw new RepositoryException("Error Delete All", ex);
        }
    }

    private void resetID() {
        String sql = "ALTER SEQUENCE registered_users_id_seq RESTART WITH 1";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            log.info("\t ***** ID Values resetted successfully! *****");
        } catch (SQLException ex) {
            throw new RepositoryException("Error ResetID", ex);
        }
    }



    public boolean checkIfBanned(String mail) {
        String sql = "SELECT email FROM banned_users WHERE email = ? ";
        boolean res = false;
        ResultSet resultSet;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mail);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String queriedMail = resultSet.getString(EMAIL);
                if (queriedMail.equals(mail)) {
                    res = true;
                }
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error Querying", ex);
        } return res;
    }


    public boolean checkEmailBoolean(String email) {
        String sql = "SELECT email FROM registered_users WHERE email = ?";
        ResultSet resultSet;
        boolean result = false;


        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String mail = resultSet.getString(EMAIL);
                if (mail.equals(email)) result = true;

            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error Checking Email", ex);
        } return result;
    }



    public String getUserType(String email){
        String sql = "SELECT user_type FROM registered_users WHERE email = ?";
        ResultSet resultSet = null;
        String userType = "";

        try (Connection connection = connector.getConnection();
             PreparedStatement preparedStmt = connection.prepareStatement(sql)){

            preparedStmt.setString(1, email);

            resultSet = preparedStmt.executeQuery();

            while(resultSet.next()){
                userType = resultSet.getString("user_type");
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error Querying UserCode", ex);
        }
        return userType;
    }



    public void updatePassword(User user, String password) {
        String query = "update registered_users set password = ? where id = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            user.setPassword(password);
        } catch (SQLException e) {
            throw new UpdateException("Unable to update password, SQLException: "+e.getMessage());
        }
    }



    public void updateEmail(User user, String email) {
        String query = "update registered_users set email = ? where id = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            user.setEmail(email);
        } catch (SQLException e) {
            throw new UpdateException("Unable to update email, SQLException: "+e.getMessage());
        }
    }

    public boolean deleteUserByEmail(String email){
        String query = "delete from registered_users where email = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, email);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }


    public int updateProfilePic(int id, Path pathToImg){
        String query = "update users set encoded_profile_img = ? where id = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(query)) {
            String encodedImg = ImgBase64Repo.encode(pathToImg);
            preparedStatement.setString(1, encodedImg);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error Updating Image", e);
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    public boolean deleteProfilePic(User user){
        String query = "update users set encoded_profile_img = null where id = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, user.getId());
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public void updateCity(String city, User user) {
        String sql = "UPDATE registered_users SET city = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, city);
            preparedStatement.setInt(2, user.getId());
            if (preparedStatement.executeUpdate() == 1) user.setCity(city);
        } catch (SQLException sqlException) {
            throw new UpdateException("Error updating city, SQL Exception: "+sqlException.getMessage());
        }
    }


    @Override
    public int update(User user) {
        return 0;
    }

    @Override
    public int delete(User user) {
        return 0;
    }

    @Override
    public User get(int id) {
        return null;
    }

}
