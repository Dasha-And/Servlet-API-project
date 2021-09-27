package home.work.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.work.domain.AccountRepository;
import home.work.web.model.AccountDetails;
import home.work.web.model.Transaction;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

//ToDo: make this class a servlet
@WebServlet("/balance")
public class AccountBalanceServlet extends HttpServlet {

  private AccountRepository accountRepository;
  private ObjectMapper objectMapper;
  private static final String JDBC_DRIVER = "org.h2.Driver";
//  private static final String DB_URL = "jdbc:h2:~/test";
//  private static final String USERNAME = "sa";
//  private static final String PASSWORD = "";

  //ToDo: initialize servlet's dependencies
  public void init() {
    try {
      Class.forName(JDBC_DRIVER);
      accountRepository = new AccountRepository();
      accountRepository.init();
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
    objectMapper = new ObjectMapper();
  }

  //ToDo: implement "get" method that will return all available user balances.
  // Check accountRepository to see what DB communication methods can be used
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      Class.forName(JDBC_DRIVER);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    List<AccountDetails> list = null;
    try {
      //Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
      list = accountRepository.getAllAccountDetails();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    resp.getWriter().write("[");
    if (!list.isEmpty()) {
      for (int i = 0; i < list.size() - 1; i++) {
        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, list.get(i));
        String result = writer.toString();
        resp.getWriter().write(result + ",\n");
      }
      StringWriter writer = new StringWriter();
      objectMapper.writeValue(writer, list.get(list.size() - 1));
      resp.getWriter().write(writer.toString());
    }
    resp.getWriter().write("]");
  }

  //ToDo: implement "post" method that will update account balance.
  // Check accountRepository to see what DB communication methods can be used
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    BufferedReader reader = req.getReader();
    Transaction transaction = objectMapper.readValue(reader, Transaction.class);
    AccountDetails accountDetails;
    try {
      accountDetails = accountRepository.updateBalance(transaction);
      StringWriter writer = new StringWriter();
      objectMapper.writeValue(writer, accountDetails);
      String result = writer.toString();
      resp.getWriter().write(result + "\n");
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

}