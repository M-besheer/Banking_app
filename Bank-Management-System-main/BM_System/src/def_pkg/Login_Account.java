package def_pkg;

import java.sql.*;
import java.util.Objects;

import javafx.util.Pair;


public class Login_Account {
	private String login_id;
	private String username;
	private String password;
	private String type;

	public Login_Account(String login_id, String username, String password, String type) {
		this.login_id = login_id;
		this.username = username;
		this.password = password;
		this.type = type;
	}


	// Getters
	public String getLoginId() { return login_id; }
	public String getUsername() { return username; }
	public String getType() {
		switch(type.toUpperCase()) {
			case "C": return "Client";
			case "M": return "Manager";
			default: return "Unknown";
		}
	}

	// Authentication methods
	public static Pair<Login_Account,Integer> signIn(Connection conn, String username, String password) throws SQLException {
		String sql = "SELECT login_id, type FROM login_account WHERE username = ? AND password = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Login_Account(
							rs.getString("login_id"),
							username,
							"", // Empty password for security
							rs.getString("type"));
				}}}
		return null;
	}


	public static boolean verifyAccount(Connection conn, String accNum, String cnic) throws SQLException {
		String sql = "SELECT c.CNIC FROM client c " +
				"JOIN bank_account ba ON c.client_id = ba.client_id " +
				"WHERE ba.acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, accNum);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getString("CNIC").equals(cnic);
			}
		}
	}

	public static int signUp(Connection conn, String username, String pass1, String pass2, String accNum) throws SQLException {
		if (!pass1.equals(pass2)) return -1;
		try {
			conn.setAutoCommit(false);


			// Create login entry
			String sqlselect = "SELECT login_id FROM bank_account WHERE acc_num = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sqlselect)) {
				pstmt.setString(1, accNum);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						// After reading a value, for instance, after calling rs.getInt("login_id"),
						// you can use rs.wasNull() to check if it was null.
						int currentLoginId = rs.getInt("login_id");
						if (rs.wasNull()) {  // Proceed only if login_id was NULL
							String sql = "INSERT INTO login_account (username, password, type) VALUES (?, ?, 'C')";
							try (PreparedStatement pstmt2 = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
								pstmt2.setString(1, username);
								pstmt2.setString(2, pass1);
								pstmt2.executeUpdate();}
						} else {
							// login_id is not null; account is already linked
							System.out.println("DSDS");
							return -2;
						}
					}
				}
			}
			conn.commit();
			return 0;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}


	// User information
	public static String getEmployeeName(Connection conn, String loginId) throws SQLException {
		String sql = "SELECT f_name, l_name FROM employee WHERE login_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, loginId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("f_name") + " " + rs.getString("l_name");
				}
			}
		}
		return "";
	}

	public static Login_Account getByUsername(Connection conn, String username) throws SQLException {
		String sql = "SELECT login_id, type FROM login_account WHERE username = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					if (!rs.wasNull()) {
						if(Objects.equals(rs.getString("type"), "M"))
						{
							return new Pair<>(new Login_Account(
									rs.getString("login_id"),
									username,
									"", // Empty password for security
									rs.getString("type")
							), 1);
						}
						else
						{
							String sql2 = "SELECT status FROM bank_account where login_id=?";
							try (PreparedStatement pstmt2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
								System.out.println(rs.getString("login_id"));
								pstmt2.setString(1, rs.getString("login_id"));
								try (ResultSet rs2 = pstmt2.executeQuery()) {
									if (rs2.next()) {
										if (rs2.getInt("status") == 1) {
											return new Pair<>(new Login_Account(
													rs.getString("login_id"),
													username,
													"", // Empty password for security
													rs.getString("type")
											), 1);
										} else if (rs2.getInt("status") == 2) {
											return new Pair<>(new Login_Account(
													rs.getString("login_id"),
													username,
													"", // Empty password for security
													rs.getString("type")
											), 2);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return new Pair<>(null,0);
	}
}