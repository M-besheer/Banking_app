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
	//public String getClosingDate() { return closing_date; }
	//public String getCardNum() { return card_num; }

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

//	public int addAmount(Connection conn, int amount) throws SQLException {
//		String sql = "UPDATE bank_account SET balance = balance + ? WHERE acc_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setInt(1, amount);
//			pstmt.setString(2, acc_num);
//			int affectedRows = pstmt.executeUpdate();
//			if (affectedRows > 0) {
//				balance = String.valueOf(Integer.parseInt(balance) + amount);
//			}
//			return affectedRows;
//		}
//	}
//
//	public int removeAmount(Connection conn, int amount) throws SQLException {
//		if (Integer.parseInt(balance) < amount) {
//			return 0;
//		}
//
//		String sql = "UPDATE bank_account SET balance = balance - ? WHERE acc_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setInt(1, amount);
//			pstmt.setString(2, acc_num);
//			int affectedRows = pstmt.executeUpdate();
//			if (affectedRows > 0) {
//				balance = String.valueOf(Integer.parseInt(balance) - amount);
//			}
//			return affectedRows;
//		}
//	}

//	public static String createCardlessWithdrawal(Connection conn, int accNum, int amount, String pin) throws SQLException {
//		Random rand = new Random();
//		String otc = String.format("%04d%04d%04d",
//				rand.nextInt(9999),
//				rand.nextInt(9999),
//				rand.nextInt(9999)
//		);
//
//		String sql = "INSERT INTO cardless_withdrawl (card_no, amount, temp_otc, temp_pin, status) "
//				+ "SELECT card_num, ?, ?, ?, 'p' FROM bank_account WHERE acc_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setInt(1, amount);
//			pstmt.setString(2, otc);
//			pstmt.setString(3, pin);
//			pstmt.setInt(4, accNum);
//			pstmt.executeUpdate();
//		}
//		return otc;
	//}

//	public static boolean isCardActive(Connection conn, int cardNum) throws SQLException {
//		String sql = "SELECT status FROM card WHERE card_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setInt(1, cardNum);
//			try (ResultSet rs = pstmt.executeQuery()) {
//				return rs.next() && rs.getString("status").equalsIgnoreCase("A");
//			}
//		}
//	}

	public static int getClientId(Connection conn, int accNum) throws SQLException {
		String sql = "SELECT client_id FROM bank_account WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, accNum);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() ? rs.getInt("client_id") : -1;
			}
		}
	}

//	public static void updateStatus(Connection conn, int accNum, String newStatus) throws SQLException {
//		String sql = "UPDATE bank_account SET status = ? WHERE acc_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setString(1, newStatus);
//			pstmt.setInt(2, accNum);
//			pstmt.executeUpdate();
//		}
//	}
//
//	public static void updateCardStatus(Connection conn, int cardNum, String status) throws SQLException {
//		String sql = "UPDATE card SET status = ? WHERE card_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setString(1, status);
//			pstmt.setInt(2, cardNum);
//			pstmt.executeUpdate();
//		}
//	}

	public static void closeAccount(Connection conn, int accNum) throws SQLException { ///////////////boobs
		String sql = "UPDATE bank_account SET status = '0', closing_date = CURDATE() WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, accNum);
			pstmt.executeUpdate();
		}
	}

	public static String getCNIC(Connection conn, int clientId) throws SQLException {
		String sql = "SELECT CNIC FROM client WHERE client_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, clientId);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() ? rs.getString("CNIC") : "";
			}
		}
	}

	//	public static int getCardNumber(Connection conn, int accNum) throws SQLException {
//		String sql = "SELECT card_num FROM bank_account WHERE acc_num = ?";
//		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//			pstmt.setInt(1, accNum);
//			try (ResultSet rs = pstmt.executeQuery()) {
//				return rs.next() ? rs.getInt("card_num") : -1;
//			}
//		}
//	}
	public static Bank_Account getByLoginId(Connection conn, int loginId) throws SQLException {
		String sql = "SELECT * FROM bank_account WHERE login_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, loginId);
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