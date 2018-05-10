import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

@Named(value = "customer")
@SessionScoped
@ManagedBean
public class Customer implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    private DBConnect dbConnect = new DBConnect();
    private String userLogin;
    private String userPwd; 
    private String FName;
    private String LName;
    private String email;
    private String address;
    private String ccn;
    private Date expdate;
    private Integer crccode; 
    private Date created_date;

    /*public String getLoginUser() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
    
        return login.getLogin();
    }*/
    
    public String getUserLogin() {
        return userLogin;
    }
    
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin; 
    }
    
    public String getUserPwd() {
        return userPwd;
    }
    
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd; 
    }
    
    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }
    
    public String getLName() {
        return LName;
    }
    
    public void setLName(String LName) {
        this.LName = LName;
    }
    
    public String getEmail() {
        return email; 
    }
    
    public void setEmail(String email) {
        this.email = email; 
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCcn() {
        return ccn;
    }
    
    public void setCcn(String ccn) {
        this.ccn = ccn; 
    }
    
    public java.util.Date getExpdate() {
        return expdate;
    }
    
    public void setExpdate(java.util.Date expdate) {
        this.expdate = expdate; 
    }
    
    public Integer getCrccode() {
        return crccode;
    }
    
    public void setCrccode(Integer code) {
        this.crccode = code; 
    }
    
    /* connect this to DB as well? */ 
    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.created_date = created_date;
    }

    public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Statement statement = con.createStatement();

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customers values(?,?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, userLogin); 
        preparedStatement.setString(2, userPwd);
        preparedStatement.setString(3, FName);
        preparedStatement.setString(4, LName);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, address);
        preparedStatement.setString(7, ccn); 
        preparedStatement.setDate(8, new java.sql.Date(expdate.getTime()));
        preparedStatement.setInt(9, crccode); 
        //preparedStatement.setDate(8, new java.sql.Date(created_date.getTime()));
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();
        //Util.invalidateUserSession();
        return "main";
    }
    
    public String deleteCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        String delete = "delete from customers where login = ?"; 
        PreparedStatement ps = con.prepareStatement(delete); 
        ps.setString(1, userLogin); 
        ps.executeUpdate();
        con.commit();
        con.close();
        return "done";
    }

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(
            "select login, FName, LName, email, address, expdate from customers order by LName");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            
            Customer cust = new Customer();

            cust.setUserLogin(result.getString("login"));
            cust.setFName(result.getString("FName"));
            cust.setLName(result.getString("LName"));
            cust.setEmail(result.getString("email"));
            cust.setAddress(result.getString("address"));
            cust.setExpdate(result.getDate("expdate"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }

    public void customerLoginExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsCustomerLogin((String) value)) {
            FacesMessage errorMessage = new FacesMessage("User does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateCustomerLogin(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        String loginUser = (String) value;
        if (existsCustomerLogin(loginUser)) {
            FacesMessage errorMessage = new FacesMessage("User already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsCustomerLogin(String userLogin) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        PreparedStatement ps = con.prepareStatement("select * from customers where login = ?");
        ps.setString(1, userLogin); 

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
