package def_pkg;

import java.sql.*;

public class Bank_Account {
	private String acc_num;
	private String client_id;
	private String login_id;
	private String type;
	private String balance;
	private String status;
	private String opening_date;


	public Bank_Account() {
		acc_num = "";
		client_id = "";
		login_id = "";
		type = "";
		balance = "";
		status = "";
		opening_date = "";
	}

	public Bank_Account(String acc_num, String client_id, String login_id, String type,
						String balance, String status, String opening_date) {
		this.acc_num = acc_num;
		this.client_id = client_id;
		this.login_id = login_id;
		this.type = type;
		this.balance = balance;
		this.status = status;
		this.opening_date = opening_date;
	}

	public Bank_Account(String accNum, String type, String balance, String status, String openingDate) {
		this.acc_num = accNum;
		this.type = type;
		this.balance = balance;
		this.status = status;
		this.opening_date = openingDate;
	}
	@Override
	public String toString() {
		return String.format(
				"Account Number: %s%n" +
						"Client ID: %s%n" +
						"Login ID: %s%n" +
						"Type: %s%n" +
						"Balance: %s%n" +
						"Status: %s%n" +
						"Opening Date: %s",
				acc_num, client_id, login_id, type, balance, status, opening_date
		);
	}

	private Connection establishConnection() {
		String url = "jdbc:mysql://localhost:3306/bank_schema";
		String username = "root";
		String password = "";
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connection established successfully!");
			return connection;
		} catch (SQLException e) {
			throw new IllegalStateException("Unable to connect to the database. " + e.getMessage());
		}
	}
	// Getters
	public String getAccountNum() { return acc_num; }
	public String getClientId() { return client_id; }
	public String getLoginId() { return login_id; }
	public String getType() { return type; }
	public String getBalance() { return balance; }
	public String getStatus() { return status; }
	public String getOpeningDate() { return opening_date; }
	public void setStatus(String status) { this.status = status; }

	// Database operations
	public static Bank_Account getByAccountNumber(Connection conn, String accountNum) throws SQLException {
		String sql = "SELECT * FROM bank_account WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, accountNum);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Bank_Account(
							rs.getString("acc_num"),
							rs.getString("client_id"),
							rs.getString("login_id"),
							rs.getString("type"),
							rs.getString("balance"),
							rs.getString("status"),
							rs.getString("opening_date")
					);
				}
			}
		}
		return null;
	}

	public static Bank_Account getByClientId(Connection conn, String clientId) throws SQLException {
		String sql = "SELECT * FROM bank_account WHERE client_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, clientId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Bank_Account(
							rs.getString("acc_num"),
							rs.getString("client_id"),
							rs.getString("login_id"),
							rs.getString("type"),
							rs.getString("balance"),
							rs.getString("status"),
							rs.getString("opening_date")

					);
				}
			}
		}
		return null;
	}

	public void updateBalance(Connection conn) throws SQLException {
		String sql = "SELECT balance FROM bank_account WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, acc_num);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					balance = rs.getString("balance");
				}
			}
		}
	}

	public static Bank_Account getByLoginId(Connection conn, String loginId) throws SQLException {
		String sql = "SELECT * FROM bank_account WHERE login_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, loginId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					// Construct a Bank_Account object from the row
					return new Bank_Account(
							rs.getString("acc_num"),
							rs.getString("client_id"),
							rs.getString("login_id"),
							rs.getString("type"),
							rs.getString("balance"),
							rs.getString("status"),
							rs.getString("opening_date")
					);
				}
			}
		}
		return null;
	}
}