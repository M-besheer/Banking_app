package def_pkg;

import java.sql.*;
import java.util.List;

public class Accountant {
	private String name;

	public Accountant() {
		this.name = "";
	}

	public Accountant(String name) {
		this.name = name;
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
	public String getName() {
		return this.name;
	}

	// Account operations
	public Bank_Account searchAccount(Connection conn, String accountNum, String CNIC) throws SQLException {
		String sql = "SELECT ba.* FROM bank_account ba " +
				"JOIN client c ON ba.client_id = c.client_id " +
				"WHERE ba.acc_num = ? AND c.CNIC = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, accountNum);
			pstmt.setString(2, CNIC);
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

	public Client searchClient(Connection conn, String accountNum, String CNIC) throws SQLException {
		String sql = "SELECT c.* FROM client c " +
				"JOIN bank_account ba ON c.client_id = ba.client_id " +
				"WHERE ba.acc_num = ? AND c.CNIC = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// this is a comment by omar
			pstmt.setString(1, accountNum);
			pstmt.setString(2, CNIC);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Client(
							rs.getString("client_id"),
							rs.getString("f_name"),
							rs.getString("l_name"),
							rs.getString("father_name"),
							rs.getString("mother_name"),
							rs.getString("CNIC"),
							rs.getString("DOB"),
							rs.getString("phone"),
							rs.getString("email"),
							rs.getString("address")
					);
				}
			}
		}
		return null;
	}

//	public int chequeDeposit(Connection conn, String accNum, String chequeNum, int amount) throws SQLException {
//		try {
//			conn.setAutoCommit(false);
//
//			// Verify account status
//			Bank_Account account = Bank_Account.getByAccountNumber(conn, accNum);
//			if (account == null) return 2; // Account not found
//			if (account.getStatus().equals("0")) return 4; // Closed account
//			if (account.getStatus().equals("2")) return 3; // Blocked account
//
//			// Update balance
//			String updateSql = "UPDATE bank_account SET balance = balance + ? WHERE acc_num = ?";
//			try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
//				pstmt.setInt(1, amount);
//				pstmt.setString(2, accNum);
//				pstmt.executeUpdate();
//			}
//
//			// Record transaction
//			String insertSql = "INSERT INTO transaction_history " +
//					"(amount, type, date, time, account_num, cheque_num) " +
//					"VALUES (?, 'cheque', CURDATE(), CURRENT_TIME(), ?, ?)";
//			try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
//				pstmt.setInt(1, amount);
//				pstmt.setString(2, accNum);
//				pstmt.setString(3, chequeNum);
//				pstmt.executeUpdate();
//			}
//
//			conn.commit();
//			return 1; // Success
//		} catch (SQLException e) {
//			conn.rollback();
//			throw e;
//		} finally {
//			conn.setAutoCommit(true);
//		}
//	}

	public List<Transaction_History> getTransactions(Connection conn, String accNum,
													 String fromDate, String toDate) throws SQLException {
		return Transaction_History.getTransactions(conn, accNum, fromDate, toDate);
	}

	// Additional business logic methods
	public boolean verifyAccountStatus(Connection conn, String accNum) throws SQLException {
		Bank_Account account = Bank_Account.getByAccountNumber(conn, accNum);
		return account != null && account.getStatus().equals("1"); // 1 = active
	}

	public String generateAccountStatement(Connection conn, Client client,
										   Bank_Account account, String fromDate,
										   String toDate) throws SQLException {
		// Implementation similar to original createPDF method
		// Would generate HTML/PDF statement using transaction data
		// Return generated file path or null
		return null;
	}
}