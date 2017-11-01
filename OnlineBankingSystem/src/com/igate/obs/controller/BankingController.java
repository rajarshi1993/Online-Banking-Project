/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.igate.obs.bean.AccountMaster
 *  com.igate.obs.bean.CustomerInformation
 *  com.igate.obs.bean.LoginRole
 *  com.igate.obs.bean.PayeeTable
 *  com.igate.obs.bean.UserInfo
 *  com.igate.obs.exception.BankingException
 *  com.igate.obs.service.IBankingService
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.dao.DataAccessException
 *  org.springframework.stereotype.Controller
 *  org.springframework.ui.Model
 *  org.springframework.validation.BindingResult
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 */
package com.igate.obs.controller;

import com.igate.obs.bean.AccountMaster;
import com.igate.obs.bean.CustomerInformation;
import com.igate.obs.bean.LoginRole;
import com.igate.obs.bean.PayeeTable;
import com.igate.obs.bean.UserInfo;
import com.igate.obs.exception.BankingException;
import com.igate.obs.service.IBankingService;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BankingController {
    @Autowired
    private IBankingService bankingService;
    LoginRole login = new LoginRole();
    UserInfo userInfo = new UserInfo();
    CustomerInformation custInfo = new CustomerInformation();
    AccountMaster accountMaster = new AccountMaster();
    PayeeTable payeeTable = new PayeeTable();
    ArrayList<AccountMaster> accList = new ArrayList();

    @RequestMapping(value={"showHomePage"})
    public String showStartPage(Model model) {
        model.addAttribute("LoginRole", (Object)new LoginRole());
        return "HomePage";
    }

    @RequestMapping(value={"showFindAtmPage"})
    public String showFindAtmPage(Model model) {
        return "FindATMBranch";
    }

    @RequestMapping(value={"showAboutUsPage"})
    public String showAboutUsPage(Model model) {
        return "AboutUs";
    }

    @RequestMapping(value={"showContactUsPage"})
    public String showContactUsPage(Model model) {
        return "ContactUs";
    }

    @RequestMapping(value={"showDisclaimerPage"})
    public String showDisclaimerPage(Model model) {
        return "Disclaimer";
    }

    @RequestMapping(value={"showStatementsPage"})
    public String showStatementsPage(Model model) {
        return "Statements";
    }

    @RequestMapping(value={"showServiceRequestPage"})
    public String showServiceRequestPage(Model model) {
        return "ServiceRequest";
    }

    @RequestMapping(value={"showTrackServicePage"})
    public String showTrackServicePage(Model model) {
        return "TrackService";
    }

    @RequestMapping(value={"showChangeCommPage"})
    public String showChangeCommPage(Model model) {
        return "ChangeCommunication";
    }

    @RequestMapping(value={"showUserProfilePage"})
    public String showUserProfilePage(Model model) {
        return "UserProfile";
    }

    @RequestMapping(value={"showAdminHomePage"})
    public String showAdminHomePage(Model model) {
        return "Admin";
    }

    @RequestMapping(value={"showForgotPasswordPage"})
    public String showForgotPasswordPage(Model model) {
        return "ForgotPassword";
    }

    @RequestMapping(value={"forgotPassword"})
    public String forgotPasswordPage(@RequestParam(value="accountid") String accId, @RequestParam(value="userid") String userid, Model model, HttpServletRequest request) {
        HttpSession bankSession;
        block3 : {
            bankSession = request.getSession();
            long accountid = Long.parseLong(accId);
            try {
                int isValid = this.bankingService.rightAccIDUserID(accountid, userid);
                if (isValid <= 0) break block3;
                String secretQuestion = this.bankingService.fetchSecurityQuestion(accountid, userid);
                bankSession.setAttribute("userid", (Object)userid);
                bankSession.setAttribute("SecretQues", (Object)secretQuestion);
                return "ForgotPassword";
            }
            catch (BankingException e) {
                bankSession.setAttribute("Wrong", (Object)"Some technical problem occured. Please try again later.");
                return "ForgotPassword";
            }
        }
        bankSession.setAttribute("Wrong", (Object)"Invalid Account Number or User ID");
        return "ForgotPassword";
    }

    @RequestMapping(value={"changePassword"})
    public String changePasswordPage(@RequestParam(value="secretans") String answer, @RequestParam(value="password") String password, Model model, HttpServletRequest request) {
        HttpSession bankSession;
        block3 : {
            bankSession = request.getSession();
            String userId = (String)bankSession.getAttribute("userid");
            try {
                int count = this.bankingService.updatePassword(userId, answer, password);
                if (count <= 0) break block3;
                bankSession.setAttribute("Success", (Object)"Your password is successfully changed");
                return "ForgotPassword";
            }
            catch (BankingException e) {
                bankSession.setAttribute("Fail", (Object)"Some technical problem occured. Please try again later.");
                return "ForgotPassword";
            }
        }
        bankSession.setAttribute("Fail", (Object)"You have entered wrong answer");
        return "ForgotPassword";
    }

    @RequestMapping(value={"showCustRegistrationPage"})
    public String showCustRegistrationPage(Model model) {
        return "CustRegistration";
    }

    @RequestMapping(value={"login"})
    public String showHomePage(@ModelAttribute(value="LoginRole") LoginRole loginRole, BindingResult result, Model model, HttpServletRequest request) throws BankingException {
        HttpSession bankSession;
        block6 : {
            String role;
            block7 : {
                bankSession = request.getSession();
                if (result.hasErrors()) {
                    model.addAttribute("LoginRole", (Object)new LoginRole());
                    return "HomePage";
                }
                boolean isValidUser = this.bankingService.isValidUser(loginRole.getUserId(), loginRole.getLoginPassword());
                if (!isValidUser) break block6;
                this.login = this.bankingService.checkRole(loginRole.getUserId(), loginRole.getLoginPassword());
                role = this.login.getUserRole();
                if (!role.equals("admin")) break block7;
                this.accList = this.bankingService.fetchAllAccountDetails();
                bankSession.setAttribute("AccountList", this.accList);
                return "Admin";
            }
            if (role.equals("user")) {
                this.userInfo = this.bankingService.fetchAccountId(loginRole.getUserId(), loginRole.getLoginPassword());
                long accountId = this.userInfo.getAccountId();
                this.custInfo = this.bankingService.fetchCustomerInfo(accountId);
                this.accountMaster = this.bankingService.fetchAccountDetails(accountId);
                bankSession.setAttribute("AccountId", (Object)accountId);
                bankSession.setAttribute("UserInfo", (Object)this.userInfo);
                bankSession.setAttribute("AccountInfo", (Object)this.accountMaster);
                bankSession.setAttribute("CustInfo", (Object)this.custInfo);
                return "UserHome";
            }
        }
        try {
            bankSession.setAttribute("InvalidUser", (Object)"Invalid Username or Password");
            return "HomePage";
        }
        catch (DataAccessException e) {
            bankSession.setAttribute("InvalidUser", (Object)"Some technical problem occured. Please try again later.");
            return "HomePage";
        }
        //return null;
    }

    @RequestMapping(value={"home"})
    public String showUserHomePage(HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        try {
            this.custInfo = this.bankingService.fetchCustomerInfo(accountId);
            this.accountMaster = this.bankingService.fetchAccountDetails(accountId);
            bankSession.setAttribute("AccountInfo", (Object)this.accountMaster);
            bankSession.setAttribute("CustInfo", (Object)this.custInfo);
            return "UserHome";
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "UserHome";
        }
    }

    @RequestMapping(value={"transfer"})
    public String showTransferPage(Model model, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        try {
            List payeeList = this.bankingService.getAllPayee(accountId);
            if (payeeList.size() == 0) {
                bankSession.setAttribute("NoPayee", (Object)"Yes");
                bankSession.setAttribute("SelectType", (Object)"Yes");
            } else {
                bankSession.setAttribute("PayeeList", (Object)payeeList);
                bankSession.setAttribute("SelectPayee", (Object)"Yes");
            }
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Transfer";
        }
        return "Transfer";
    }

    @RequestMapping(value={"AddPayeeLink"})
    public String showAddPayeeForm(Model model, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        bankSession.setAttribute("AddPayee", (Object)"Yes");
        return "Transfer";
    }

    @RequestMapping(value={"addNewPayee"})
    public String showAddPayeePage(Model model, HttpServletRequest request) throws BankingException {
        HttpSession bankSession;
        block3 : {
            bankSession = request.getSession();
            long payeeAccountNo = Long.parseLong(request.getParameter("payeeAccountId"));
            String payeeNickName = request.getParameter("nickName");
            boolean isValidAccount = this.bankingService.transferAccount(payeeAccountNo);
            if (!isValidAccount) break block3;
            this.payeeTable.setPayeeAccountId(payeeAccountNo);
            this.payeeTable.setNickName(payeeNickName);
            this.payeeTable.setUrn(123456);
            bankSession.setAttribute("PayeeBean", (Object)this.payeeTable);
            bankSession.setAttribute("CheckURN", (Object)"Yes");
            return "Transfer";
        }
        try {
            bankSession.setAttribute("AddPayee", (Object)"Yes");
            bankSession.setAttribute("NotACustomer", (Object)"The account number does not belong to Bank Of Chennai");
            return this.showAddPayeeForm(model, request);
        }
        catch (DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Transfer";
        }
    }

    @RequestMapping(value={"insertPayee"})
    public String insertNewPayee(Model model, HttpServletRequest request) {
        PayeeTable payee;
        HttpSession bankSession = request.getSession();
        int urn = Integer.parseInt(request.getParameter("urn"));
        if (urn == (payee = (PayeeTable)bankSession.getAttribute("PayeeBean")).getUrn()) {
            long accountId = (Long)bankSession.getAttribute("AccountId");
            payee.setAccountId(accountId);
            try {
                boolean isInserted = this.bankingService.insertPayee(payee);
                if (isInserted) {
                    bankSession.setAttribute("PayeeAdded", (Object)"Yes");
                }
            }
            catch (BankingException | DataAccessException e) {
                bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
                return "Transfer";
            }
            return this.showTransferPage(model, request);
        }
        bankSession.setAttribute("WrongURN", (Object)"URN number does not match. Please enter the fields again");
        return this.showAddPayeeForm(model, request);
    }

    @RequestMapping(value={"fundTransfer"})
    public String fundTransfer(Model model, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long transferFrom = (Long)bankSession.getAttribute("AccountId");
        long transferTo = Long.parseLong(request.getParameter("payee"));
        double transferAmount = Double.parseDouble(request.getParameter("transferamount"));
        String transactionPass = request.getParameter("transactionpassword");
        try {
            boolean isValidTP = this.bankingService.checkTransactionPass(transactionPass, transferFrom);
            if (isValidTP) {
                double balance = this.bankingService.getAvailableBalance(transferFrom);
                if (balance >= transferAmount) {
                    int updateSenderBalance = this.bankingService.updateAccountBalance(transferFrom, transferAmount);
                    int updateReceiverBalance = this.bankingService.updateReceiverAccountBalance(transferTo, transferAmount);
                    int insertFundTransfer = this.bankingService.insertFundTransfer(transferFrom, transferAmount, transferTo);
                    this.accountMaster = this.bankingService.fetchAccountDetails(transferFrom);
                    bankSession.setAttribute("AccountInfo", (Object)this.accountMaster);
                    if (updateSenderBalance > 0 && updateReceiverBalance > 0 && insertFundTransfer > 0) {
                        bankSession.setAttribute("SelectPayee", (Object)"Yes");
                        bankSession.setAttribute("SuccessTransfer", (Object)"The amount got transferred successfully");
                        return this.showTransferPage(model, request);
                    }
                    bankSession.setAttribute("SelectPayee", (Object)"Yes");
                    bankSession.setAttribute("FailTransfer", (Object)"Some technical problem occured. Please try again later.");
                    return this.showTransferPage(model, request);
                }
                bankSession.setAttribute("SelectPayee", (Object)"Yes");
                bankSession.setAttribute("NotEnoughBalance", (Object)"You don't have enough balance to transfer");
                return this.showTransferPage(model, request);
            }
            bankSession.setAttribute("SelectPayee", (Object)"Yes");
            bankSession.setAttribute("InvalidTP", (Object)"Yes");
            return this.showTransferPage(model, request);
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Transfer";
        }
    }

    @RequestMapping(value={"showDetailedStatement"})
    public String showDetailedStatement(@RequestParam(value="from") String start, @RequestParam(value="to") String end, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        try {
            ArrayList detailedStat = this.bankingService.getDetailedStatement(start, end, accountId);
            if (detailedStat.size() == 0) {
                bankSession.setAttribute("msg1", (Object)"No transactions done between this period");
            }
            bankSession.setAttribute("MiniStatement", (Object)null);
            bankSession.setAttribute("DetailStatement", (Object)detailedStat);
            return "Statements";
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Statements";
        }
    }

    @RequestMapping(value={"showMiniStatement"})
    public String showMiniStatement(HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        try {
            ArrayList miniStat = this.bankingService.getMiniStatement(accountId);
            if (miniStat.size() == 0) {
                bankSession.setAttribute("msg2", (Object)"No transactions to display");
            }
            bankSession.setAttribute("MiniStatement", (Object)miniStat);
            bankSession.setAttribute("DetailStatement", (Object)null);
            return "Statements";
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Statements";
        }
    }

    @RequestMapping(value={"requestService"})
    public String requestService(@RequestParam(value="servicetype") String serviceType, HttpServletRequest request) throws BankingException {
        HttpSession bankSession;
        block4 : {
            block5 : {
                bankSession = request.getSession();
                long accountId = (Long)bankSession.getAttribute("AccountId");
                boolean bool = this.bankingService.checkServiceRequest(accountId, serviceType);
                if (!bool) break block4;
                int count = this.bankingService.insertServiceTracker(accountId, serviceType);
                long serviceId = this.bankingService.getServiceRequestId(accountId, serviceType);
                if (count <= 0) break block5;
                bankSession.setAttribute("ServiceRequest", (Object)("The request for " + serviceType + " has been successfully processed"));
                bankSession.setAttribute("ServiceId", (Object)("Your service id is " + serviceId));
                return "ServiceRequest";
            }
            bankSession.setAttribute("NoServiceRequest", (Object)("The request for " + serviceType + " has not been successfully processed"));
            return "ServiceRequest";
        }
        try {
            bankSession.setAttribute("NoServiceRequest", (Object)("Request for " + serviceType + " is already submitted"));
            return "ServiceRequest";
        }
        catch (DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "ServiceRequest";
        }
    }

    @RequestMapping(value={"trackService"})
    public String trackService(HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        try {
            ArrayList serviceList = this.bankingService.fetchServiceRequests(accountId);
            bankSession.setAttribute("ServiceList", (Object)serviceList);
            return "TrackService";
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "TrackService";
        }
    }

    @RequestMapping(value={"updateMobileNo"})
    public String updateMobileNo(@RequestParam(value="newmobileno") String mobileno, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        this.custInfo = (CustomerInformation)bankSession.getAttribute("CustInfo");
        try {
            boolean isUpdated = this.bankingService.updateMobileNo(accountId, mobileno);
            if (isUpdated) {
                this.custInfo = this.bankingService.fetchCustomerInfo(accountId);
                String msg = "Mobile number is successfully updated";
                bankSession.setAttribute("CustInfo", (Object)this.custInfo);
                bankSession.setAttribute("Mobile", (Object)msg);
                return "ChangeCommunication";
            }
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "ChangeCommunication";
        }
        return null;
    }

    @RequestMapping(value={"updateAddress.obj"})
    public String updateAddress(@RequestParam(value="newaddress") String address, HttpServletRequest request) {
        HttpSession bankSession = request.getSession();
        long accountId = (Long)bankSession.getAttribute("AccountId");
        this.custInfo = (CustomerInformation)bankSession.getAttribute("CustInfo");
        try {
            boolean isUpdated = this.bankingService.updateAddress(accountId, address);
            if (isUpdated) {
                this.custInfo = this.bankingService.fetchCustomerInfo(accountId);
                bankSession.setAttribute("CustInfo", (Object)this.custInfo);
                String msg = "Address is successfully updated";
                bankSession.setAttribute("Address", (Object)msg);
                return "ChangeCommunication";
            }
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "ChangeCommunication";
        }
        return null;
    }

    @RequestMapping(value={"viewTransactions"})
    public String viewTransactions(HttpServletRequest request, Model model) {
        HttpSession bankSession = request.getSession();
        try {
            long accId = Long.parseLong(request.getParameter("id"));
            ArrayList transaction = this.bankingService.getAllTransactions(accId);
            bankSession.setAttribute("Id", (Object)accId);
            bankSession.setAttribute("Transactions", (Object)transaction);
            if (transaction.size() > 0) {
                bankSession.setAttribute("Status", (Object)"Yes");
            } else {
                bankSession.setAttribute("Status", (Object)null);
            }
            return "Transactions";
        }
        catch (BankingException | DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "Transactions";
        }
    }

    @RequestMapping(value={"newCustRegister"})
    public String newCustRegister(@RequestParam(value="custname") String cname, @RequestParam(value="custemail") String email, @RequestParam(value="gender") String gender, @RequestParam(value="dob") String date, @RequestParam(value="mobileno") String mobileno, @RequestParam(value="custaddress") String address, @RequestParam(value="pancard") String pancard, @RequestParam(value="aadharcard") String aadharcard, @RequestParam(value="password") String password, @RequestParam(value="secretquestion") String secretQuestion, @RequestParam(value="secretanswer") String secretAnswer, @RequestParam(value="transactionpassword") String transactionPassword, Model model, HttpServletRequest request) throws BankingException {
        HttpSession bankSession;
        block3 : {
            bankSession = request.getSession();
            Date dob = Date.valueOf(date);
            CustomerInformation customerinfo = new CustomerInformation();
            UserInfo userInfomation = new UserInfo();
            customerinfo.setCustomerName(cname);
            customerinfo.setEmail(email);
            customerinfo.setGender(gender);
            customerinfo.setDob(dob);
            customerinfo.setMobileNo(mobileno);
            customerinfo.setAddress(address);
            customerinfo.setPancardNo(pancard);
            customerinfo.setAadharcardNo(aadharcard);
            userInfomation.setLoginPassword(password);
            userInfomation.setSecretQuestion(secretQuestion);
            userInfomation.setSecretAnswer(secretAnswer);
            userInfomation.setTransactionPassword(transactionPassword);
            String userId = this.bankingService.insertNewUser(customerinfo, userInfomation);
            if (userId.equals("")) break block3;
            bankSession.setAttribute("UserID", (Object)userId);
            return "CustRegistration";
        }
        try {
            bankSession.setAttribute("Error", (Object)"Some error occured. Please try again later.");
            return "CustRegistration";
        }
        catch (DataAccessException e) {
            bankSession.setAttribute("Error", (Object)"Some technical problem occured. Please try again later.");
            return "CustRegistration";
        }
    }

    @RequestMapping(value={"signout"})
    public String signout(HttpServletRequest request, Model model) {
        HttpSession bankSession = request.getSession();
        bankSession.invalidate();
        model.addAttribute("LoginRole", (Object)new LoginRole());
        return "HomePage";
    }
}
