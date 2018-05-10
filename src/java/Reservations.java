import java.io.Serializable;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;

@Named(value = "reservations")
@SessionScoped
@ManagedBean
public class Reservations implements Serializable {
    
    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }
    
    private DBConnect dbConnect = new DBConnect();
    private String mylogin;
    private String username; 
    private Integer resid; 
    private java.util.Date checkIn;
    private java.util.Date checkOut; 
    private java.util.Date actualCheckOut;
    private String view;
    private String bedType;
    private Integer roomNum; 

    public String getMylogin() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        return login.getLogin();
    }
    
    public void setMylogin(String mylogin) {
        this.mylogin = mylogin;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getResid() {
        return resid;
    }

    public void setResid(Integer resid) {
        this.resid = resid;
    }
    
    public java.util.Date getCheckIn() {
      return checkIn;
    }
    
    public void setCheckIn (java.util.Date checkIn) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.checkIn = checkIn; 
    }
    
    public java.util.Date getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut (java.util.Date checkOut) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.checkOut = checkOut; 
    }
    
    public java.util.Date getActualCheckOut() {
        return actualCheckOut;
    }

    public void setActualCheckOut(java.util.Date actualCheckOut) {
        this.actualCheckOut = actualCheckOut;
    }
    
    public String getView() {
        return view;
    }
    
    public void setView(String view) {
        this.view = view; 
    }
    
    public String getBedType() {
        return bedType;
    }
    
    public void setBedType(String bedType) {
        this.bedType = bedType;
    }
    
    public Integer getRoomNum() {
        return roomNum;
    }
    
    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum; 
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                            JUST FOR CUSTOMERS  
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public List<Reservations> checkReservation() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        /* grabs information from reservations table */ 
        String select = "select * from reservations r join rooms rms on "
                + "(r.roomnum = rms.roomnum) join bedinfo on (bedid = id) "
                + "where custlogin = ? order by rid";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, login.getLogin()); 
        ResultSet result = ps.executeQuery();
        
        List<Reservations> list = new ArrayList<Reservations>();
        
        while (result.next()) {
            
            Reservations res = new Reservations();
            
            res.setResid(result.getInt("rid"));
            res.setCheckIn(result.getDate("checkin"));
            res.setCheckOut(result.getDate("checkout"));
            res.setRoomNum(result.getInt("roomnum"));
            res.setActualCheckOut(result.getDate("actualcheckout"));
            res.setView(result.getString("roomview"));
            res.setBedType(result.getString("bedtype"));

            //store all data into a List
            list.add(res);
        }
        result.close();
        con.close();
        return list;
    }
 
    /* must check if reservation is valid before committing */ 
    public String createReservation() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        
        String insert = "insert into reservations (custlogin, checkin, checkout,"
                + " roomnum) values (?,?,?,?)";
        
        int findRoom = findRoomNum();
        if (findRoom == 0) {
            return "badReservation";
        }
        PreparedStatement preparedStatement = con.prepareStatement(insert);
        preparedStatement.setString(1, login.getLogin()); 
        preparedStatement.setDate(2, new java.sql.Date(checkIn.getTime()));
        preparedStatement.setDate(3, new java.sql.Date(checkOut.getTime()));
        preparedStatement.setInt(4, roomNum);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        return "successful";
    }

    private boolean existsReservation() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        String select = "select count(*) countrows from reservations where "
                + "custlogin = ? and checkin = ? and checkout = ?";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, login.getLogin()); 
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime())); 
        ResultSet result = ps.executeQuery();
        result.next();
        int getRes = result.getInt("countrows"); 
        if (getRes > 0) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }    
    
    public String deleteReservation() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        if (!existsReservation()) {
            return "tryAgain"; 
        }
        String delete = "delete from reservations where custlogin = ? and checkin = ?"
            + " and checkout = ?";
        PreparedStatement ps = con.prepareStatement(delete);
        ps.setString(1, login.getLogin());
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime()));
        ps.executeUpdate();
        ps.close();
        con.commit();
        con.close();
        return "deleted";
    }
    
    public Integer findRoomNum() {
        try (Connection con = dbConnect.getConnection()) {
            con.setAutoCommit(false); 
            
            int bed; 
            if (bedType.equals("single king")) {
                bed = 1;
            } else {
                bed = 2; 
            }
            
            String select = "select * from rooms where bedid = ? and roomview ="
                    + " ? and roomnum not in (select roomnum from reservations "
                    + "where checkin between ? and ? and checkout between ? and "
                    + "?)";
            PreparedStatement ps = con.prepareStatement(select); 
            ps.setInt(1, bed);
            ps.setString(2, view);
            ps.setDate(3, new java.sql.Date(checkIn.getTime()));
            ps.setDate(4, new java.sql.Date(checkOut.getTime()));
            ps.setDate(5, new java.sql.Date(checkIn.getTime()));
            ps.setDate(6, new java.sql.Date(checkOut.getTime()));
            ResultSet result = ps.executeQuery();
            
            select = "select count(*) countrows from rooms where bedid = ? and roomview ="
                    + " ? and roomnum not in (select roomnum from reservations "
                    + "where checkin between ? and ? and checkout between ? and "
                    + "?)";
            PreparedStatement ps2 = con.prepareStatement(select); 
            ps2.setInt(1, bed);
            ps2.setString(2, view);
            ps2.setDate(3, new java.sql.Date(checkIn.getTime()));
            ps2.setDate(4, new java.sql.Date(checkOut.getTime()));
            ps2.setDate(5, new java.sql.Date(checkIn.getTime()));
            ps2.setDate(6, new java.sql.Date(checkOut.getTime()));
            ResultSet result2 = ps2.executeQuery();
            result2.next(); 
            
            int roomsAvailable = result2.getInt("countrows"); 
            if (roomsAvailable > 0) {
                result.next(); 
                setRoomNum(result.getInt("roomnum"));
                con.commit();
                con.close(); 
                return 1;
            }
            
            con.commit();
            con.close(); 
            return 0; 
        } catch (SQLException e) {
            System.out.println("Can't get database connection"); 
            return 0; 
        } 
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
                            JUST FOR EMPLOYEES  
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */    
    public Reservations getReservation() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        Reservations res = new Reservations(); 
        String select = "select * from reservations r join rooms rm on "
                + "(r.roomnum = rm.roomnum) join bedinfo on (bedid = id)"
                + " where custlogin = ? and checkin = ? and checkout = ?"
                + " and r.roomnum = ?";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, username);
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime()));
        ps.setInt(4, roomNum);
        ResultSet result = ps.executeQuery();

        result.next();

        res.setResid(result.getInt("rid"));
        res.setUsername(result.getString("custlogin")); 
        res.setCheckIn(result.getDate("checkin"));
        res.setCheckOut(result.getDate("checkout"));
        res.setRoomNum(result.getInt("roomnum"));
        res.setBedType(result.getString("bedtype"));
        res.setView(result.getString("roomview"));
        return res;
    }
    
    public Reservations checkedRoomNumber() throws SQLException {
        Reservations res = new Reservations(); 
        res.setRoomNum(roomNum); 
        return res; 
    }
    
    public String checkInCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        } 
        String select = "select * from reservations r join rooms rm on (r.roomnum = rm.roomnum) where custlogin = ? and checkin = current_date";
        PreparedStatement ps = con.prepareStatement(select); 
        ps.setString(1, username); 
        ResultSet rs = ps.executeQuery(); 
        rs.next();
        setCheckIn(rs.getDate("checkin"));
        setCheckOut(rs.getDate("checkout"));
        setRoomNum(rs.getInt("roomnum"));
        rs.close();
        con.close();
        return "checkedIn";
    }
    
    /*public String checkOutCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        return "checkedOut";
    }*/
    
    public String createEReservation() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        
        String insert = "insert into reservations (custlogin, checkin, checkout,"
                + " roomnum) values (?,?,?,?)";
        
        int findRoom = findRoomNum();
        if (findRoom == 0) {
            return "badReservation";
        }
        PreparedStatement preparedStatement = con.prepareStatement(insert);
        preparedStatement.setString(1, username); 
        preparedStatement.setDate(2, new java.sql.Date(checkIn.getTime()));
        preparedStatement.setDate(3, new java.sql.Date(checkOut.getTime()));
        preparedStatement.setInt(4, roomNum);
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        return "successful";
    }

    private boolean existsEReservation() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        String select = "select count(*) countrows from reservations where "
                + "custlogin = ? and checkin = ? and checkout = ?";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, username); 
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime())); 
        ResultSet result = ps.executeQuery();
        result.next();
        int getRes = result.getInt("countrows"); 
        if (getRes > 0) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }    
    
    public String deleteEReservation() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);
        
        if (!existsEReservation()) {
            return "tryAgain"; 
        }
        String delete = "delete from reservations where custlogin = ? and checkin = ?"
            + " and checkout = ?";
        PreparedStatement ps = con.prepareStatement(delete);
        ps.setString(1, username);
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime()));
        ps.executeUpdate();
        ps.close();
        con.commit();
        con.close();
        return "deleted";
    }    
    
    public String goBack() {
        return "goBack";
    }
    
    public String goHome() {
        return "goHome"; 
    }
    
    public String tryAgain() {
        return "tryAgain"; 
    }
    
    public String showRes() {
        return "showRes"; 
    }
}
