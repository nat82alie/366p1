import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;  
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import java.time.LocalDate;


@Named(value = "roomprices")
@SessionScoped
@ManagedBean
public class RoomPrice implements Serializable {
    
    private DBConnect dbConnect = new DBConnect();
    private Integer id;
    private Integer roomnum;
    private Double price;
    private java.util.Date date;

    public Integer getId() {
        return id;
    }

    public Integer getRoomnum() {
        return roomnum;
    }

    public Double getPrice() {
        return price;
    }

    public  java.util.Date getDate() {
        return date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRoomnum(Integer roomnum) {
        this.roomnum = roomnum;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDate( java.util.Date date) {
        this.date = date;
    }
    
    public List<RoomPrice> getRoomPriceList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from RoomPrices order by date");

        //get roomprices data from database
        ResultSet result = ps.executeQuery();

        List<RoomPrice> list = new ArrayList<RoomPrice>();

        while (result.next()) {
            RoomPrice rp = new RoomPrice();

            rp.setId(result.getInt("Id"));
            rp.setRoomnum(result.getInt("Roomnum"));
            rp.setPrice(result.getDouble("Price"));
            rp.setDate(result.getDate("Date"));
            //store all data into a List
            list.add(rp);
        }
        result.close();
        con.close();
        return list;
    }
    
    public String createRoomPrice() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into RoomPrices values(?,?,?,?)");
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, roomnum);
        preparedStatement.setDouble(3, price);
        preparedStatement.setDate(4, new java.sql.Date(date.getTime()));
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }
    
    public void validateRoomPriceID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        Integer roomid = (Integer) value;
        if (existsRoomPriceId(roomid)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }
    
    private boolean existsRoomPriceId(Integer roomid) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from roomprices where id = '" + roomid + "'");

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
    
    public void validateRoomNum(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        Integer roomnum = (Integer) value;
        if (!existsRoomNum(roomnum)) {
            FacesMessage errorMessage = new FacesMessage("Room Number does not exist");
            throw new ValidatorException(errorMessage);
        }
    }
    
    private boolean existsRoomNum(Integer roomnum) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from rooms where roomnum = '" + roomnum + "'");

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
}
