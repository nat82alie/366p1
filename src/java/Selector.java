import java.io.Serializable;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.el.ELContext;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
 *
 * @author team1
 */
@Named(value = "selector")
@ManagedBean
@SessionScoped
public class Selector implements Serializable {

    private String[] choices;
    private String[] adminChoices = {"Change Your Password", "View Room Prices", "Change Room Prices",
        "Add Employee", "Delete Employee", "Show All Customers", "Add A Customer", "Delete A Customer",
        "Check In A Customer", "Check Out A Customer", "Add Charges To A Reservation",
        "View A Reservation", "Create A Reservation", "Cancel A Reservation"};
    private String[] employeeChoices = {"Change Your Password", "Show All Customers", "Add A Customer",
        "Delete A Customer", "View Room Prices", "Check In A Customer", "Check Out A Customer",
        "Add Charges To A Reservation", "View A Reservation", "Create A Reservation", "Cancel A Reservation"};
    private String[] customerChoices = {"Check Your Reservations", "Create Your Reservation", "Cancel Your Reservation"};
    private String choice;
    private String userType = "";
    private Login login;
    
    public void setUserType() {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");
        userType = login.getUserType();
    }
    
    public String[] getChoices() {
        setUserType();
        
        if (userType == "admin") {
            choices = adminChoices;
            return adminChoices;
        }
        if (userType == "employee") {
            choices = employeeChoices;
            return employeeChoices;
        }
        if (userType == "customer") {
            choices = customerChoices;
            return customerChoices;
        }

        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String transition() {
        switch (choice) {
            case "Change Room Prices": //a
                return "changeRoomPrices";
            case "Add Employee": //a
                return "addEmpl";
            case "Delete Employee": //a
                return "deleteEmpl";

            case "Show All Customers": //a,e
                return "showAllCustomers";
            case "Change Your Password": //a,e
                return "changePwd";
            case "View Room Prices": //a,e
                return "roomPrices";
            case "Add A Customer": //a,e
                return "addCustomer";
            case "Delete A Customer": //a,e
                return "deleteCustomer";
            case "Check In A Customer": //a,e
                return "checkInCustomer";
            case "Check Out A Customer": //a,e
                return "checkOutCustomer";
            case "Add Charges To A Reservation": //a,e
                return "addCharges";
            case "View A Reservation": //a,e
                return "viewReservation";
            case "Create A Reservation": //a,e
                return "createReservation";
            case "Cancel A Reservation": //a,e
                return "cancelReservation";
                
            case "Check Your Reservations": //customer
                return "checkYourReservations";
            case "Create Your Reservation": //customer
                return "createYourReservation";
            case "Cancel Your Reservation": //customer
                return "cancelYourReservation";
            default:
                return null;
        }
    }

}
