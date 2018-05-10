import java.io.Serializable;
import java.sql.*;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.swing.JFrame;

import java.io.Serializable;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
 *
 * @author team1
 */
@Named(value = "login")
@SessionScoped
@ManagedBean
public class Login extends DBConnect implements Serializable {

    private String login;
    private String password;
    private UIInput loginUI;
    public String userType;
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public UIInput getLoginUI() {
        return loginUI;
    }

    public void setLoginUI(UIInput loginUI) {
        this.loginUI = loginUI;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException, SQLException {
        DBConnect dbc = new DBConnect();
        Connection con = dbc.getConnection();
        con.setAutoCommit(false);
        
        login = loginUI.getLocalValue().toString();
        password = value.toString();
        
        String selectStmt = "select * from Employees where Login='" + login + "' and Pwd='" + password + "'";
        try (Statement stmt = con.createStatement()) {
            stmt.execute(selectStmt);
            ResultSet res = stmt.executeQuery(selectStmt);
            
            if (!res.next()) {
                selectStmt = "select * from Customers where Login='" + login + "' and Pwd='" + password + "'";
                try (Statement s = con.createStatement()) {
                    s.execute(selectStmt);
                    res = stmt.executeQuery(selectStmt);
                    
                    if (!res.next()) {
                        FacesMessage errorMessage = new FacesMessage("Incorrect login/password");
                        throw new ValidatorException(errorMessage);
                    }
                    
                    setUserType("customer");
                    go();
                } catch (SQLException e) {
                    con.rollback();
                }
            }
            
            boolean isAdmin = res.getBoolean("Admin");
            if (isAdmin)
                setUserType("admin");
            else if (!isAdmin)
                setUserType("employee");
            go();
            
        } catch (SQLException e) {
            con.rollback();
        }
    }

    public String go() {
      //  Util.invalidateUserSession();
        return "success";
    }
}
