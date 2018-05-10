import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;
import java.time.LocalDate; 

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
    private String fname;
    private String lname;
    private String email;
    private String address;
    private String ccn;
    private String exp_date;
    private Integer crccode; 
    private Date created_date;

    public String getLoginUser() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
    
        return login.getLogin();
    }
    
    public String getFName() {
        if (fname == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select fname from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                fname = result.getString("fname"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return fname;
    }

    public void setFName(String fname) {
        this.fname = fname;
    }
    
    public String getLName() {
        if (lname == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select lname from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                lname = result.getString("lname"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return lname;
    }
    
    public void setLName(String lname) {
        this.lname = lname;
    }
    
    public String getEmail() {
        if (email == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select email from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                email = result.getString("email"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return email; 
    }
    
    public void setEmail(String email) {
        this.email = email; 
    }

    public String getAddress() {
        if (address == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select address from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                address = result.getString("address"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCCN() {
        if (ccn == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select ccn from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                ccn = result.getString("ccn"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return ccn;
    }
    
    public void setCCN(String ccn) {
        this.ccn = ccn; 
    }
    
    public String getExpDate() {
        if (exp_date == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select expdate from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                exp_date = result.getString("expdate"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return exp_date;
    }
    
    public void setExpDate(String exp_date) {
        this.exp_date = exp_date; 
    }
    
    public Integer getCRCCode() {
        if (crccode == null) {
            try(Connection con = dbConnect.getConnection()) {
                PreparedStatement ps = con.prepareStatement("select crccode from customer");
                ResultSet result = ps.executeQuery();
                if (!result.next()) {
                    return null;
                }
                crccode = result.getInt("crccode"); 
                result.close();
                con.close();
            } catch(SQLException e) {
                System.out.println("Can't get database connection"); 
            }
        }
        return crccode;
    }
    
    public void setCRCCode(Integer code) {
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

        PreparedStatement preparedStatement = con.prepareStatement("Insert into Customer values(?,?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, getLoginUser()); // check difference between these two
        preparedStatement.setString(2, login.getPassword());
        preparedStatement.setString(3, fname);
        preparedStatement.setString(4, lname);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, address);
        preparedStatement.setString(7, ccn); 
        preparedStatement.setDate(8, java.sql.Date.valueOf(exp_date));
        preparedStatement.setInt(9, crccode); 
        preparedStatement.setDate(8, new java.sql.Date(created_date.getTime()));
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

        Statement statement = con.createStatement();
        statement.executeUpdate("Delete from Customer where login = " + getLoginUser());
        statement.close();
        con.commit();
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String showCustomer() {
        return "showCustomer";
    }

    /*public Customer getCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customer where customer_id = " + CID);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        fname = result.getString("name");
        address = result.getString("address");
        created_date = result.getDate("created_date");
        return this;
    }*/

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement(
            "select login, fname, lname, email, address from customer order by lname");

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            
            Customer cust = new Customer();

            cust.setFName(result.getString("fname"));
            cust.setLName(result.getString("lname"));
            cust.setEmail(result.getString("email"));
            cust.setAddress(result.getString("address"));
            //cust.setCreated_date(result.getDate("created_date"));

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

    public void validateCustomerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        String loginUser = (String) value;
        if (existsCustomerLogin((String) loginUser)) {
            FacesMessage errorMessage = new FacesMessage("User already exists");
            throw new ValidatorException(errorMessage);
        }
    }

    private boolean existsCustomerLogin(String userLogin) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from customer where login = " + userLogin);

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
