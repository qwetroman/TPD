/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package view.backing;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.html.HtmlSelectBooleanCheckbox;
import jakarta.faces.component.html.HtmlCommandButton;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import java.util.ResourceBundle;

/**
 *
 * @author User
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {

    private String username;
    private String password;
    private HtmlSelectBooleanCheckbox acceptCheckbox;
    private HtmlCommandButton loginButton;

    public LoginBean() {
    }

    public String getUsername() {
        return username;
    }

public void activateButton(ValueChangeEvent e) {
    if (acceptCheckbox.isSelected()) {
    loginButton.setDisabled(false);
    } else {
    loginButton.setDisabled(true);
    }
    FacesContext context = FacesContext.getCurrentInstance();
    context.renderResponse();
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        if(username.equals("scott") && password.equals("tiger")) {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = ResourceBundle.getBundle("ApplicationMessages", context.getViewRoot().getLocale());
            String message = bundle.getString("error.notOracle");
            FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
            context.addMessage(null, errorMessage);
            return "index";
        }
        if (username.equals(password)) {
            return "success";
        } else {
            return "failure";
        }
    }

    /**
     * @return the acceptCheckbox
     */
    public HtmlSelectBooleanCheckbox getAcceptCheckbox() {
        return acceptCheckbox;
    }

    /**
     * @param acceptCheckbox the acceptCheckbox to set
     */
    public void setAcceptCheckbox(HtmlSelectBooleanCheckbox acceptCheckbox) {
        this.acceptCheckbox = acceptCheckbox;
    }

    /**
     * @return the loginButton
     */
    public HtmlCommandButton getLoginButton() {
        return loginButton;
    }

    /**
     * @param loginButton the loginButton to set
     */
    public void setLoginButton(HtmlCommandButton loginButton) {
        this.loginButton = loginButton;
    }

}
