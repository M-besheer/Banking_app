package def_pkg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Manager {
	private String name;

	public Manager() {
		this.name = "";
	}

	public Manager(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	// Account management operations
	public int createAccount(Connection conn, Client newClient, String type) throws SQLException {
		try {
			conn.setAutoCommit(false);

			// Check if client exists
			Client existingClient = Client.getByCNIC(conn, newClient.getCNIC());
			if (existingClient == null) {
				newClient.save(conn);
				existingClient = newClient;


				// Create new account
				String sql = "INSERT INTO bank_account (client_id, type, balance, status, opening_date) "
						+ "VALUES (?, ?, 0, 1, CURDATE())";
				try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
					pstmt.setString(1, existingClient.getClientID());
					pstmt.setString(2, type);
					pstmt.executeUpdate();
				}

				conn.commit();
				return 0;
			}
			else return 1;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	public int blockAccount(Connection conn, Bank_Account acc, String cnic) throws SQLException {
		if (!verifyAccountOwnership(conn, Integer.parseInt(acc.getAccountNum()), cnic)) {
			return -1;
		}

		String sql = "UPDATE bank_account SET status = 2 WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, Integer.parseInt(acc.getAccountNum()));
			int affected = pstmt.executeUpdate();

			if (affected > 0) {
				acc.setStatus("2"); // directly update the same object
			}
			return affected > 0 ? 0 : -2;
		}
	}

	public int unblockAccount(Connection conn, Bank_Account acc, String cnic) throws SQLException {
		if (!verifyAccountOwnership(conn, Integer.parseInt(acc.getAccountNum()), cnic)) {
			return -1; // CNIC doesn't match
		}

		String sql = "UPDATE bank_account SET status = 1 WHERE acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, Integer.parseInt(acc.getAccountNum()));
			int affected = pstmt.executeUpdate();

			if (affected > 0) {
				acc.setStatus("1");
			}
			return affected > 0 ? 0 : -2; // -2 if no rows affected
		}
	}

	boolean verifyAccountOwnership(Connection conn, int accNum, String cnic) throws SQLException {
		String sql = "SELECT c.CNIC FROM client c "
				+ "JOIN bank_account ba ON c.client_id = ba.client_id "
				+ "WHERE ba.acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, accNum);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getString("CNIC").equals(cnic);
			}
		}
	}

	// Information retrieval methods
	public Client getClientInfo(Connection conn, String accNum) throws SQLException {
		String sql = "SELECT c.* FROM client c "
				+ "JOIN bank_account ba ON c.client_id = ba.client_id "
				+ "WHERE ba.acc_num = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, accNum);
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


	public void updateClientInfo(Connection conn, String clientId, String phone,  String email, String address) throws SQLException {
		String sql = "UPDATE client SET phone = ?, email = ?, address = ? WHERE client_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, phone);
			pstmt.setString(2, email);
			pstmt.setString(3, address);
			pstmt.setString(4, clientId);
			pstmt.executeUpdate();
		}
	}

	public int getTotalAccounts(Connection conn,Manager manager) throws SQLException {
		String sql = "SELECT COUNT(*) AS total_accounts FROM bank_account";
		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				int totalAccounts = rs.getInt("total_accounts");
				System.out.println("Total accounts in the system: " + totalAccounts);
				return totalAccounts;
			}
		}
		return 0; // Default value if no rows are found
	}


	public int getTotalEmployees(Connection conn,Manager manager) throws SQLException {
		String sql = "SELECT COUNT(*) AS total_employees FROM employee";
		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				int totalEmployees = rs.getInt("total_employees");
				System.out.println("Total Employees in the system: " + totalEmployees);
				return totalEmployees;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return 0;
	}
}