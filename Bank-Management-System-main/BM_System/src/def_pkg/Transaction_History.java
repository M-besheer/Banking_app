package def_pkg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//import com.itextpdf.html2pdf.HtmlConverter;

public class Transaction_History {
	private String serial_no;
	private String amount;
	private String type;
	private String date;
	private String time;
	private String account_num;
	private String recv_acc_num;
	private String cheque_num;

	public Transaction_History() {
		this.serial_no = "";
		this.amount = "";
		this.type = "";
		this.date = "";
		this.time = "";
		this.account_num = "";
		this.recv_acc_num = "";
		this.cheque_num = "";
	}

	public Transaction_History(String serial_no, String amount, String type, String date,
							   String time, String account_num, String recv_acc_num,
							   String cheque_num) {
		this.serial_no = serial_no;
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.time = time;
		this.account_num = account_num;
		this.recv_acc_num = recv_acc_num;
		this.cheque_num = cheque_num;
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
	public String getSerialNo() { return serial_no; }
	public String getAmount() { return amount; }
	public String getType() { return type; }
	public String getDate() { return date; }
	public String getTime() { return time; }
	public String getAccountNumber() { return account_num; }
	public String getRecvAccNum() { return recv_acc_num; }
	public String getChequeNum() { return cheque_num; }

	// Database operations
	public static List<Transaction_History> getTransactions(Connection conn, String accNum,
															String fromDate, String toDate)
			throws SQLException {
		List<Transaction_History> transactions = new ArrayList<>();
		String sql = "SELECT * FROM transaction_history "
				+ "WHERE (account_num = ? OR recv_acc_num = ?) "
				+ "AND date BETWEEN ? AND ? "
				+ "ORDER BY date DESC, time DESC";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, accNum);
			pstmt.setString(2, accNum);
			pstmt.setString(3, fromDate);
			pstmt.setString(4, toDate);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					transactions.add(mapTransaction(rs));
				}
			}
		}
		return transactions;
	}

	public void save(Connection conn) throws SQLException {
		String sql = "INSERT INTO transaction_history "
				+ "(amount, type, date, time, account_num, recv_acc_num, cheque_num) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, this.amount);
			pstmt.setString(2, this.type);
			pstmt.setString(3, this.date);
			pstmt.setString(4, this.time);
			pstmt.setString(5, this.account_num);
			pstmt.setString(6, this.recv_acc_num);
			pstmt.setString(7, this.cheque_num);
			pstmt.executeUpdate();
		}
	}

	public static String generateStatement(Connection conn, Client client,
										   Bank_Account account, String fromDate,
										   String toDate) throws SQLException {
		try {
			String html = buildHTMLHeader(client, account, fromDate, toDate);
			html += buildTransactionTable(conn, account.getAccountNum(), fromDate, toDate);
			html += "</body></html>";

			String fileName = "E_Statement_" + System.currentTimeMillis() + ".pdf";
//			HtmlConverter.convertToPdf(html, new FileOutputStream(fileName));
			return fileName;

		} catch (Exception e) {
			throw new SQLException("Failed to generate PDF statement: " + e.getMessage());
		}
	}

	private static String buildHTMLHeader(Client client, Bank_Account account,
										  String fromDate, String toDate) {
		return "<!DOCTYPE html><html><head><style>"
				+ "table { width: 100%; border-collapse: collapse; margin-top: 20px; }"
				+ "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }"
				+ "th { background-color: #f2f2f2; }"
				+ "</style></head><body>"
				+ "<h1>ABC Bank</h1>"
				+ "<h4>F9 Branch, Islamabad</h4>"
				+ "<p>" + client.getFName() + " " + client.getLName() + "<br>"
				+ "Account Number: " + account.getAccountNum() + "<br>"
				+ "Account Type: " + account.getType() + "<br>"
				+ "Statement Period: " + fromDate + " - " + toDate + "</p>"
				+ "<h4>Transaction History</h4>"
				+ "<table>"
				+ "<tr>"
				+ "<th>Date</th><th>Time</th><th>Type</th><th>Amount</th>"
				+ "<th>Sender Account</th><th>Receiver Account</th><th>Cheque #</th>"
				+ "</tr>";
	}

	private static String buildTransactionTable(Connection conn, String accNum,
												String fromDate, String toDate)
			throws SQLException {
		StringBuilder html = new StringBuilder();
		List<Transaction_History> transactions = getTransactions(conn, accNum, fromDate, toDate);

		for (Transaction_History t : transactions) {
			html.append("<tr>")
					.append("<td>").append(t.getDate()).append("</td>")
					.append("<td>").append(t.getTime()).append("</td>")
					.append("<td>").append(t.getType()).append("</td>")
					.append("<td>").append(t.getAmount()).append("</td>")
					.append("<td>").append(t.getAccountNumber()).append("</td>")
					.append("<td>").append(t.getRecvAccNum()).append("</td>")
					.append("<td>").append(t.getChequeNum()).append("</td>")
					.append("</tr>");
		}
		return html.toString();
	}

	private static Transaction_History mapTransaction(ResultSet rs) throws SQLException {
		return new Transaction_History(
				rs.getString("serial_no"),
				rs.getString("amount"),
				rs.getString("type"),
				rs.getString("date"),
				rs.getString("time"),
				rs.getString("account_num"),
				rs.getString("recv_acc_num"),
				rs.getString("cheque_num")
		);
	}
}