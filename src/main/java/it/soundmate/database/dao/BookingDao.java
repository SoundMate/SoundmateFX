package it.soundmate.database.dao;

import it.soundmate.database.Connector;
import it.soundmate.database.dbexceptions.RepositoryException;
import it.soundmate.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {

    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String BOOKER_ID = "booker_id";
    public static final String IS_ACCEPTED = "is_accepted";
    public static final String ERROR_FETCHING_BOOKINGS = "Error fetching bookings. \n";
    public static final String ID_RENTER = "id_renter";
    private final Connector connector = Connector.getInstance();
    private static final Logger log = LoggerFactory.getLogger(BookingDao.class);


    //insert
    public Booking sendBookingRequest(Booking booking){
        String sql = "INSERT INTO booking (room_code, date, start_time, end_time, booker_id, is_accepted, id_renter) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Date date = Date.valueOf(booking.getDate());
        Time startTime = Time.valueOf(booking.getStartTime());
        Time endTime = Time.valueOf(booking.getEndTime());

        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, booking.getRoom().getCode());
            preparedStatement.setDate(2, date);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, endTime);
            preparedStatement.setInt(5, booking.getBookerUserId());
            preparedStatement.setBoolean(6, false);
            preparedStatement.setInt(7, booking.getIdRenter());

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected == 1) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return booking.withCode(resultSet.getInt("code"));
                }
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Error inserting entry: the error was: \n" + ex.getMessage(), ex);
        }
        return booking.withCode(-1); //invalid code, abort (must be > 0)
    }

    //fetch for all the request related to a specific @parameter room
    public List<Booking> getBookingRequestsByRoom(Room room) {
        String sql = "SELECT b.code, b.date, b.start_time, b.end_time, b.booker_id, b.is_accepted " +
                      "FROM room " +
                      "JOIN booking b ON room.room_code = b.room_code " +
                      "WHERE room.id = ?";
        ArrayList<Booking> bookings = new ArrayList<>();

        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, room.getRenterID());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Booking booking = buildBookingResult(resultSet);
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_FETCHING_BOOKINGS + ex.getMessage(), ex);
        }
    }

    public Booking buildBookingResult(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking();
        booking.setCode(resultSet.getInt("code"));
        booking.setDate(resultSet.getDate("date").toLocalDate());
        booking.setStartTime(resultSet.getTime(START_TIME).toLocalTime());
        booking.setEndTime(resultSet.getTime(END_TIME).toLocalTime());
        booking.setBookerUserId(resultSet.getInt(BOOKER_ID));
        booking.setAccepted(resultSet.getBoolean(IS_ACCEPTED));
        return booking;
    }

    //method to fetch all active bookings related to a RoomRenter user
    public List<Booking> getAllBookingsById(RoomRenter roomRenter) {
        String sql = "SELECT * FROM booking WHERE id_renter = ?";
        return getBookings(sql, roomRenter.getId());
    }

    public List<Booking> getBookings(String sql, int id) {
        ArrayList<Booking> bookings = new ArrayList<>();
        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Booking booking = buildBookingResult(resultSet);
                booking.setIdRenter(resultSet.getInt(ID_RENTER));
                bookings.add(booking);
            }
            return bookings;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_FETCHING_BOOKINGS + ex.getMessage(), ex);
        }
    }


    public boolean acceptBooking(Booking booking){
        String sql = "UPDATE booking SET is_accepted = ? WHERE code = ?";

        try(Connection conn = connector.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, booking.getCode());

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException ex){
            throw new RepositoryException("Error updating the field. The error was: \n" + ex.getMessage(), ex);
        }
    }

    public boolean updateTime(Booking booking){
        String sql = "UPDATE booking SET start_time = ?, end_time = ? WHERE code = ?";
        Time startTime = Time.valueOf(booking.getStartTime());
        Time endTime = Time.valueOf(booking.getEndTime());

        try(Connection conn = connector.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setTime(1, startTime);
            preparedStatement.setTime(2, endTime);
            preparedStatement.setInt(3, booking.getCode());

            return preparedStatement.executeUpdate() == 1;


        } catch (SQLException ex){
            throw new RepositoryException("Error updating the fields. The error was: \n" + ex.getMessage(), ex);
        }

    }


    public boolean updateDate(Booking booking) {
        Date date = Date.valueOf(booking.getDate());
        String sql = "UPDATE booking SET date = ? WHERE code = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setDate(1, date);
            preparedStatement.setInt(2, booking.getCode());

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException ex) {
            throw new RepositoryException("Error updating the fields. The error was: \n" + ex.getMessage(), ex);
        }

    }

    public List<Booking> getAllBookingsById(Solo solo) {
        String sql = "SELECT * FROM booking WHERE booker_id = ?";
        return getBookings(sql, solo.getId());
    }


    public boolean deleteBooking(Booking booking) {
        String sql = "DELETE FROM booking WHERE code = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, booking.getCode());

            return preparedStatement.executeUpdate() == 1;

        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_FETCHING_BOOKINGS + ex.getMessage(), ex);
        }
    }


    //testing purpose
    public void deleteBookingEntries() {
        String sql = "DELETE FROM booking";
        int delRecs;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            delRecs = stmt.executeUpdate();
            if (delRecs >= 1) log.info("\t ***** Booking Entries Successfully Cleaned! *****");
            resetCode();

        } catch (SQLException ex) {
            throw new RepositoryException("Error Deleting Booking entries \n" + ex.getMessage(), ex);
        }
    }

    private void resetCode() {
        String sql = "ALTER SEQUENCE booking_booking_code_seq RESTART WITH 1";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            log.info("\t ***** Code Values reset successfully! *****");
        } catch (SQLException ex) {
            throw new RepositoryException("Error ResetCode", ex);
        }
    }




}


