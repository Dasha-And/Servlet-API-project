package home.work.domain;

import home.work.web.model.AccountDetails;
import home.work.web.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

  //ToDo: use these credentials to access the embedded in-memory database. No additional configuration required.

  private static final String JDBC_DRIVER = "org.h2.Driver";
  private static final String DB_URL = "jdbc:h2:~/test";
  private static final String USERNAME = "sa";
  private static final String PASSWORD = "";

  Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

  public AccountRepository() throws SQLException {
  }

  //ToDo: add implementation to create DB table on application startup
  public void init() throws ClassNotFoundException, SQLException {
    Class.forName(JDBC_DRIVER);
    Statement statement = conn.createStatement();
    String createTableScript = "CREATE TABLE IF NOT EXISTS accounts " +
            "(userId VARCHAR(255) not NULL, " +
            "balance INTEGER, " +
            "PRIMARY KEY ( userId ))";
    statement.execute(createTableScript);
  }

  //ToDo: add implementation to retrieve all available account balances from DB (or empty list if there are no entries)
  public List<AccountDetails> getAllAccountDetails() throws SQLException {
    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT * FROM ACCOUNTS ORDER BY BALANCE DESC");
    List<AccountDetails> list = new ArrayList<>();
    while (resultSet.next()) {
      AccountDetails accountDetails = new AccountDetails(resultSet.getString(1), resultSet.getInt(2));
      list.add(accountDetails);
    }
    return list;
  }

  //ToDo: add implementation to add amount from transactionRequest to player balance, or create a new DB entry if
  // userId from transactionRequest not exists in the DB. It should return AccountDetails with updated balance
  public AccountDetails updateBalance(Transaction transactionRequest) throws SQLException {
    boolean isUserExists = false;
    try (PreparedStatement ps = conn.prepareStatement("select 1 from ACCOUNTS where USERID = ?")) {
      ps.setString(1, transactionRequest.getUserId());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          isUserExists = true;
        }
      }
    }
    int balance = 0;
    if (isUserExists) {
      PreparedStatement s = conn.prepareStatement("SELECT BALANCE FROM ACCOUNTS WHERE USERID = ?");
      s.setString(1, transactionRequest.getUserId());
      ResultSet rs = s.executeQuery();

      if (rs.next()) {
        balance = rs.getInt(1)+ transactionRequest.getAmount();
      }
      PreparedStatement statement = conn.prepareStatement("UPDATE ACCOUNTS SET BALANCE = ? WHERE USERID = ?");
      statement.setInt(1, balance);
      statement.setString(2, transactionRequest.getUserId());
      statement.executeUpdate();
    } else if (!isUserExists) {
      balance = transactionRequest.getAmount();
      PreparedStatement statement = conn.prepareStatement("INSERT INTO ACCOUNTS (USERID, BALANCE) VALUES (?, ?)");
      statement.setString(1, transactionRequest.getUserId());
      statement.setInt(2, transactionRequest.getAmount());
      statement.executeUpdate();
    }
    AccountDetails accountDetails = new AccountDetails(transactionRequest.getUserId(), balance);
    return accountDetails;
  }

}