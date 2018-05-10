import java.io.Serializable;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.component.UIInput;

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
    private java.util.Date checkIn;
    private java.util.Date checkOut; 
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
    
    /*public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.created_date = created_date;
    }*/
    
    public List<Reservations> checkReservation() throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        /* grabs information from reservations table */ 
        String select = "select * from reservations r join rooms rms on "
                + "(r.roomnum = rms.roomnum) join bedinfo on (bedid = id)"
                + "where custlogin = ?";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, login.getLogin()); 
        ResultSet result = ps.executeQuery();
        
        List<Reservations> list = new ArrayList<Reservations>();
        
        while (result.next()) {
            
            Reservations res = new Reservations();
            
            res.setCheckIn(result.getDate("checkin"));
            res.setCheckOut(result.getDate("checkout"));
            res.setRoomNum(result.getInt("roomnum"));
            //res.setActualCheckout(result.getString("actualcheckout"));
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
        
        String insert = "insert into reservations (custlogin, checkin, checkout, roomnum) values (?,?,?,?)";
        
        if (findRoomNum() == 0) {
            return "badReservation";
        }
        PreparedStatement preparedStatement = con.prepareStatement(insert);
        preparedStatement.setString(1, mylogin); 
        preparedStatement.setDate(2, new java.sql.Date(checkIn.getTime()));
        preparedStatement.setDate(3, new java.sql.Date(checkOut.getTime()));
        preparedStatement.setInt(4, roomNum);
        //preparedStatement.setDate(8, new java.sql.Date(created_date.getTime()));
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "successful";
    }
    
    /* not iterating through each available room and idk why */ 
    public Integer findRoomNum() {
        try (Connection con = dbConnect.getConnection()) {
            con.setAutoCommit(false); 
            
            int bed; 
            if (bedType.equals("single king")) {
                bed = 1;
            } else {
                bed = 2; 
            }
            
            String select = "select * from rooms where bedid = ? and roomview = ?";
            PreparedStatement ps = con.prepareStatement(select); 
            ps.setInt(1, bed);
            ps.setString(2, view);
            ResultSet result = ps.executeQuery();
            
            result.next();
            setRoomNum(result.getInt("roomnum")); 
            if (!reserved()) {
                return 1; 
            }
            
            while (result.next()) {
                setRoomNum(result.getInt("roomnum")); 
                if (!reserved()) {
                    return 1; 
                }
            }
            
            con.commit();
            con.close(); 
            return 0; 
        } catch (SQLException e) {
            System.out.println("Can't get database connection"); 
            return 0; 
        } 
    }
    
    public boolean reserved() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();
        
        String select = "select count(*) as countrows from reservations where roomnum = ? and"
                + " checkin between ? and ? or checkout between ? and ?";
        
        PreparedStatement preparedStatement = con.prepareStatement(select);
        preparedStatement.setInt(1, roomNum); 
        preparedStatement.setDate(2, new java.sql.Date(checkIn.getTime()));
        preparedStatement.setDate(3, new java.sql.Date(checkOut.getTime()));
        preparedStatement.setDate(4, new java.sql.Date(checkIn.getTime()));
        preparedStatement.setDate(5, new java.sql.Date(checkOut.getTime()));
        ResultSet rs = preparedStatement.executeQuery();
        rs.next(); 
        int getRes = rs.getInt("countrows"); 
        if (getRes == 0) {
            return false; 
        }

        statement.close();
        con.commit();
        con.close();
        
        return true;
    }
    
    /*public void getNextRoom() {
        
    }*/
    
    public String deleteReservation() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        String delete = "delete from reservations where login = ? and checkin = ?"
                + " and checkout = ?";
        PreparedStatement ps = con.prepareStatement(delete);
        ps.setString(1, mylogin);
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime()));
        ps.executeUpdate();
        ps.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }
    
    public Reservations getReservation() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        Reservations res = new Reservations(); 
        String select = "select * from reservations r join rooms rm on "
                + "(r.roomnum = rm.roomnum) join bedinfo on (bedid = id)"
                + " where custlogin = ? and checkin = ? and checkout = ? "
                + "and r.roomnum = ?";
        PreparedStatement ps = con.prepareStatement(select);
        ps.setString(1, mylogin);
        ps.setDate(2, new java.sql.Date(checkIn.getTime()));
        ps.setDate(3, new java.sql.Date(checkOut.getTime()));
        ps.setInt(4, roomNum);
        ResultSet result = ps.executeQuery();

        result.next();

        res.setCheckIn(result.getDate("checkin"));
        res.setCheckOut(result.getDate("checkout"));
        res.setRoomNum(result.getInt("roomnum"));
        res.setBedType(result.getString("bedtype"));
        res.setView(result.getString("roomview"));
        return res;
    }
    
    public String goBack() {
        return "goBack";
    }
    
    /* this goes to facesmessage error even though it should be valid */ 
    public void validateDates(ComponentSystemEvent event) throws SQLException {
        //FacesContext fc = FacesContext.getCurrentInstance();

	UIComponent components = event.getComponent();
        
        // get checkIn date
        UIInput uiInputCheckIn = (UIInput) components.findComponent("checkInRemove");
        java.util.Date inputCheckIn = (java.util.Date)uiInputCheckIn.getLocalValue();

        // get checkOut Date
        UIInput uiInputCheckOut = (UIInput) components.findComponent("checkOutRemove");
        java.util.Date inputCheckOut = (java.util.Date)uiInputCheckOut.getLocalValue();

        if (!existsReservation(inputCheckIn, inputCheckOut)) {
            FacesMessage errorMessage = new FacesMessage("Dates range does not exist.");
            throw new ValidatorException(errorMessage);
        }

    }

    private boolean existsReservation(java.util.Date checkInDate, java.util.Date checkOutDate) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        PreparedStatement ps = con.prepareStatement(
                "select * from reservations where custlogin = ? and checkin = ? and checkout = ?");
        ps.setString(1, mylogin); 
        ps.setDate(2, new java.sql.Date(checkInDate.getTime()));
        ps.setDate(3, new java.sql.Date(checkOutDate.getTime())); 

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }
    
    // need functions that check if reservation is available for what the customer requested
    // then return whether available or not 
}
