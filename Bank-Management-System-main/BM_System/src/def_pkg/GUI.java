package def_pkg;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUI {

	public void remakeScreen(JFrame frame, JPanel f) {
		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}

	//------------------------------------------------------------
	//               Login Related Screen Functions               |
	//------------------------------------------------------------

	// Custom colors for the new design
	private final Color PRIMARY_COLOR = new Color(0, 119, 182);  // Blue
	private final Color SECONDARY_COLOR = new Color(72, 202, 228);  // Light blue
	private final Color ACCENT_COLOR = new Color(255, 183, 3);  // Yellow
	private final Color BACKGROUND_COLOR = new Color(248, 249, 250);  // Light gray
	private final Color TEXT_COLOR = new Color(33, 37, 41);  // Dark gray

	// Custom fonts
	private Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
	private Font normalFont = new Font("Segoe UI", Font.PLAIN, 16);
	private Font smallFont = new Font("Segoe UI", Font.PLAIN, 14);

	public void openSignInForm(JFrame frame, Login_Account user) {
		JPanel f = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				// Gradient background
				GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(),SECONDARY_COLOR);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		f.setLayout(null);

		// Logo (scaled to better fit)
		ImageIcon originalIcon = new ImageIcon(System.getProperty("user.dir") + "\\src\\def_pkg\\anchor.png");
		Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		JLabel imgLabel = new JLabel(new ImageIcon(scaledImage));
		imgLabel.setBounds(50, 50, 200, 200);
		f.add(imgLabel);

		// Login panel
		JPanel loginPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2d.setColor(new Color(0, 0, 0, 10));
				g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 50, 0);
			}
		};
		loginPanel.setLayout(null);
		loginPanel.setBounds(300, 50, 400, 400);

		JLabel l_hSignIn = new JLabel("Welcome Back");
		l_hSignIn.setFont(titleFont);
		l_hSignIn.setForeground(TEXT_COLOR);
		l_hSignIn.setBounds(50, 30, 300, 40);
		loginPanel.add(l_hSignIn);

		JLabel l_UserName = new JLabel("Username");
		l_UserName.setFont(normalFont);
		l_UserName.setForeground(TEXT_COLOR);
		l_UserName.setBounds(50, 100, 100, 20);
		loginPanel.add(l_UserName);

		JTextField tf_UserName = new JTextField();
		tf_UserName.setFont(normalFont);
		tf_UserName.setBounds(50, 130, 300, 40);
		tf_UserName.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		loginPanel.add(tf_UserName);

		JLabel l_Password = new JLabel("Password");
		l_Password.setFont(normalFont);
		l_Password.setForeground(TEXT_COLOR);
		l_Password.setBounds(50, 190, 100, 20);
		loginPanel.add(l_Password);

		JPasswordField tf_Password = new JPasswordField();
		tf_Password.setFont(normalFont);
		tf_Password.setBounds(50, 220, 300, 40);
		tf_Password.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		loginPanel.add(tf_Password);

		JButton btn_SignIn = new JButton("SIGN IN");
		btn_SignIn.setFont(normalFont.deriveFont(Font.BOLD));
		btn_SignIn.setBackground(PRIMARY_COLOR);
		btn_SignIn.setForeground(Color.WHITE);
		btn_SignIn.setBounds(50, 290, 300, 45);
		btn_SignIn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btn_SignIn.setFocusPainted(false);

		// Hover effect
		btn_SignIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn_SignIn.setBackground(PRIMARY_COLOR.darker());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btn_SignIn.setBackground(PRIMARY_COLOR);
			}
		});
		loginPanel.add(btn_SignIn);

		JLabel l_Signup = new JLabel("Don't have an account?");
		l_Signup.setFont(smallFont);
		l_Signup.setForeground(TEXT_COLOR);
		l_Signup.setBounds(50, 360, 150, 20);
		loginPanel.add(l_Signup);

		JButton btn_SignUp = new JButton("Sign Up");
		btn_SignUp.setFont(smallFont.deriveFont(Font.BOLD));
		btn_SignUp.setContentAreaFilled(false);
		btn_SignUp.setBorderPainted(false);
		btn_SignUp.setForeground(PRIMARY_COLOR);
		btn_SignUp.setBounds(200, 355, 80, 30);
		btn_SignUp.setFocusPainted(false);

		// Underline on hover
		btn_SignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn_SignUp.setText("<html><u>Sign Up</u></html>");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btn_SignUp.setText("Sign Up");
			}
		});
		loginPanel.add(btn_SignUp);

		f.add(loginPanel);

		// Keep original functionality
		btn_SignUp.addActionListener(e -> open_Signup_form_1(frame, user));

		btn_SignIn.addActionListener(e -> {
			try (DB_handler db = new DB_handler()) {
				Connection conn = db.getConnection();
				Login_Account loggedInUser = Login_Account.signIn(conn,
						tf_UserName.getText(),
						new String(tf_Password.getPassword())
				);

				if (loggedInUser != null) {
					String userType = loggedInUser.getType();
					switch(userType) {
						case "Client":
							Bank_Account account = Bank_Account.getByLoginId(conn, Integer.parseInt(loggedInUser.getLoginId()));
							Client client = Client.getById(conn, account.getClientId());
							if ("1".equals(account.getStatus())) {
								frame.remove(f);
								openClientMenu(frame, client, account);
							} else {
								JOptionPane.showMessageDialog(f, "Account not active");
							}
							break;
						case "Manager":
							Manager manager = new Manager(Login_Account.getEmployeeName(conn, loggedInUser.getLoginId()));
							frame.remove(f);
							openManagerMenu(frame, manager);
							break;
					}
				} else {
					// Shake animation for invalid login
					final int originalX = loginPanel.getX();
					Timer shakeTimer = new Timer(10, ev -> {
						int offset = (int) (5 * Math.sin(System.currentTimeMillis() / 100));
						loginPanel.setLocation(originalX + offset, loginPanel.getY());
					});
					shakeTimer.setRepeats(true);
					shakeTimer.start();
					new Timer(500, ev -> {
						shakeTimer.stop();
						loginPanel.setLocation(originalX, loginPanel.getY());
					}).start();

					JOptionPane.showMessageDialog(f, "Invalid credentials");
				}
				conn.close();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(f, "Database error: " + ex.getMessage());
			}
		});

		remakeScreen(frame, f);
	}

	// Signup Form 1
	void open_Signup_form_1(JFrame frame, Login_Account user) {
		JPanel f = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				// Gradient background
				GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), BACKGROUND_COLOR);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		f.setLayout(null);

		// Back button
		JButton btn_BACK = new JButton("← Back");
		btn_BACK.setFont(normalFont);
		btn_BACK.setContentAreaFilled(false);
		btn_BACK.setBorderPainted(false);
		btn_BACK.setForeground(PRIMARY_COLOR);
		btn_BACK.setBounds(20, 20, 80, 30);
		btn_BACK.setFocusPainted(false);
		f.add(btn_BACK);

		// Signup panel
		JPanel signupPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2d.setColor(new Color(0, 0, 0, 10));
				g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
			}
		};
		signupPanel.setLayout(null);
		signupPanel.setBounds(200, 50, 400, 400);

		JLabel lSignUp = new JLabel("Verify Your Account");
		lSignUp.setFont(titleFont);
		lSignUp.setForeground(TEXT_COLOR);
		lSignUp.setBounds(50, 30, 300, 40);
		signupPanel.add(lSignUp);

		JLabel l_AccountNo = new JLabel("Account Number");
		l_AccountNo.setFont(normalFont);
		l_AccountNo.setForeground(TEXT_COLOR);
		l_AccountNo.setBounds(50, 100, 200, 20);
		signupPanel.add(l_AccountNo);

		JTextField tf_AccountNo = new JTextField();
		tf_AccountNo.setFont(normalFont);
		tf_AccountNo.setBounds(50, 130, 300, 40);
		tf_AccountNo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		signupPanel.add(tf_AccountNo);

		JLabel l_CNIC = new JLabel("CNIC");
		l_CNIC.setFont(normalFont);
		l_CNIC.setForeground(TEXT_COLOR);
		l_CNIC.setBounds(50, 190, 200, 20);
		signupPanel.add(l_CNIC);

		JTextField tf_CNIC = new JTextField();
		tf_CNIC.setFont(normalFont);
		tf_CNIC.setBounds(50, 220, 300, 40);
		tf_CNIC.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		signupPanel.add(tf_CNIC);

		JButton btn_Verify_Account = new JButton("VERIFY");
		btn_Verify_Account.setFont(normalFont.deriveFont(Font.BOLD));
		btn_Verify_Account.setBackground(PRIMARY_COLOR);
		btn_Verify_Account.setForeground(Color.WHITE);
		btn_Verify_Account.setBounds(50, 290, 300, 45);
		btn_Verify_Account.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btn_Verify_Account.setFocusPainted(false);

		// Hover effect
		btn_Verify_Account.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn_Verify_Account.setBackground(PRIMARY_COLOR.darker());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btn_Verify_Account.setBackground(PRIMARY_COLOR);
			}
		});
		signupPanel.add(btn_Verify_Account);

		f.add(signupPanel);

		btn_BACK.addActionListener(e -> {
			frame.remove(f);
			openSignInForm(frame, user);
		});

		btn_Verify_Account.addActionListener(e -> {
			try (DB_handler db = new DB_handler()) {
				Connection conn = db.getConnection();
				boolean isValid = Login_Account.verifyAccount(conn,
						tf_AccountNo.getText(),
						tf_CNIC.getText()
				);

				if (isValid) {
					open_Signup_form_2(frame, user, tf_AccountNo.getText());
				} else {
					// Shake animation for invalid verification
					final int originalX = signupPanel.getX();
					Timer shakeTimer = new Timer(10, ev -> {
						int offset = (int) (5 * Math.sin(System.currentTimeMillis() / 100));
						signupPanel.setLocation(originalX + offset, signupPanel.getY());
					});
					shakeTimer.setRepeats(true);
					shakeTimer.start();
					new Timer(500, ev -> {
						shakeTimer.stop();
						signupPanel.setLocation(originalX, signupPanel.getY());
					}).start();

					JOptionPane.showMessageDialog(f, "Invalid account or CNIC");
				}
				conn.close();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(f, "Database error: " + ex.getMessage());
			}
		});

		remakeScreen(frame, f);
	}

	// Signup Form 2
	void open_Signup_form_2(JFrame frame, Login_Account user, String acc_num) {
		JPanel f = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				// Gradient background
				GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), BACKGROUND_COLOR);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		f.setLayout(null);

		// Back button
		JButton btn_BACK = new JButton("← Back");
		btn_BACK.setFont(normalFont);
		btn_BACK.setContentAreaFilled(false);
		btn_BACK.setBorderPainted(false);
		btn_BACK.setForeground(PRIMARY_COLOR);
		btn_BACK.setBounds(20, 20, 80, 30);
		btn_BACK.setFocusPainted(false);
		f.add(btn_BACK);

		// Create account panel
		JPanel createPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2d.setColor(new Color(0, 0, 0, 10));
				g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
			}
		};
		createPanel.setLayout(null);
		createPanel.setBounds(200, 50, 400, 500);

		JLabel lSignUp = new JLabel("Create Account");
		lSignUp.setFont(titleFont);
		lSignUp.setForeground(TEXT_COLOR);
		lSignUp.setBounds(50, 30, 300, 40);
		createPanel.add(lSignUp);

		JLabel l_username = new JLabel("Username");
		l_username.setFont(normalFont);
		l_username.setForeground(TEXT_COLOR);
		l_username.setBounds(50, 100, 200, 20);
		createPanel.add(l_username);

		JTextField tf_username = new JTextField();
		tf_username.setFont(normalFont);
		tf_username.setBounds(50, 130, 300, 40);
		tf_username.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		createPanel.add(tf_username);

		JLabel l_password = new JLabel("Password");
		l_password.setFont(normalFont);
		l_password.setForeground(TEXT_COLOR);
		l_password.setBounds(50, 190, 200, 20);
		createPanel.add(l_password);

		JPasswordField tf_password = new JPasswordField();
		tf_password.setFont(normalFont);
		tf_password.setBounds(50, 220, 300, 40);
		tf_password.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		createPanel.add(tf_password);

		JLabel l_password_2 = new JLabel("Confirm Password");
		l_password_2.setFont(normalFont);
		l_password_2.setForeground(TEXT_COLOR);
		l_password_2.setBounds(50, 280, 200, 20);
		createPanel.add(l_password_2);

		JPasswordField tf_password_2 = new JPasswordField();
		tf_password_2.setFont(normalFont);
		tf_password_2.setBounds(50, 310, 300, 40);
		tf_password_2.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		createPanel.add(tf_password_2);

		// Password strength indicator
		JProgressBar strengthBar = new JProgressBar(0, 100);
		strengthBar.setBounds(50, 360, 300, 5);
		strengthBar.setForeground(Color.RED);
		strengthBar.setBorderPainted(false);
		strengthBar.setStringPainted(false);
		createPanel.add(strengthBar);

		// Update strength bar as user types
		tf_password.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { update(); }
			public void removeUpdate(DocumentEvent e) { update(); }
			public void insertUpdate(DocumentEvent e) { update(); }

			private void update() {
				String pass = new String(tf_password.getPassword());
				int strength = calculatePasswordStrength(pass);
				strengthBar.setValue(strength);

				// Change color based on strength
				if (strength < 30) strengthBar.setForeground(Color.RED);
				else if (strength < 70) strengthBar.setForeground(Color.ORANGE);
				else strengthBar.setForeground(Color.GREEN);
			}

			private int calculatePasswordStrength(String password) {
				int strength = 0;
				if (password.length() > 0) strength += 10;
				if (password.length() >= 8) strength += 20;
				if (password.matches(".*[A-Z].*")) strength += 20;
				if (password.matches(".*[a-z].*")) strength += 20;
				if (password.matches(".*[0-9].*")) strength += 20;
				if (password.matches(".*[!@#$%^&*()].*")) strength += 20;
				return Math.min(strength, 100);
			}
		});

		JButton btn_create_login_Account = new JButton("CREATE ACCOUNT");
		btn_create_login_Account.setFont(normalFont.deriveFont(Font.BOLD));
		btn_create_login_Account.setBackground(PRIMARY_COLOR);
		btn_create_login_Account.setForeground(Color.WHITE);
		btn_create_login_Account.setBounds(50, 390, 300, 45);
		btn_create_login_Account.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btn_create_login_Account.setFocusPainted(false);

		// Hover effect
		btn_create_login_Account.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn_create_login_Account.setBackground(PRIMARY_COLOR.darker());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btn_create_login_Account.setBackground(PRIMARY_COLOR);
			}
		});
		createPanel.add(btn_create_login_Account);

		f.add(createPanel);

		btn_BACK.addActionListener(e -> {
			frame.remove(f);
			open_Signup_form_1(frame, user);
		});

		btn_create_login_Account.addActionListener(e -> {
			String pass1 = new String(tf_password.getPassword());
			String pass2 = new String(tf_password_2.getPassword());

			if (!pass1.equals(pass2)) {
				// Bounce animation for mismatch
				final int originalY = createPanel.getY();
				Timer bounceTimer = new Timer(50, ev -> {
					int offset = (int) (10 * Math.sin(System.currentTimeMillis() / 100));
					createPanel.setLocation(createPanel.getX(), originalY + offset);
				});
				bounceTimer.setRepeats(true);
				bounceTimer.start();
				new Timer(500, ev -> {
					bounceTimer.stop();
					createPanel.setLocation(createPanel.getX(), originalY);
				}).start();

				JOptionPane.showMessageDialog(f, "Passwords do not match!");
				return;
			}

			try (DB_handler db = new DB_handler()) {
				Connection conn = db.getConnection();
				int result = Login_Account.signUp(
						conn,
						tf_username.getText(),
						pass1,
						pass2,
						acc_num
				);

				if (result == 0) {
					JOptionPane.showMessageDialog(f, "Account created successfully!");
					frame.remove(f);
					openSignInForm(frame, new Login_Account());
				} else {
					JOptionPane.showMessageDialog(f, "Error: " + getSignupError(result));
				}
				conn.close();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(f, "Database error: " + ex.getMessage());
			}
		});


		remakeScreen(frame, f);
	}

	private String getSignupError(int code) {
		return switch (code) {
			case -1 -> "Passwords mismatch";
			case -4 -> "Weak password (use 8+ chars with mix of letters, numbers, symbols)";
			default -> "Unknown error";
		};
	}
	//------------------------------------------------------
	//               Client Related Functions               |
	//------------------------------------------------------

	void openClientMenu(JFrame frame, Client client, Bank_Account account) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);

		JButton btnLogOut = new JButton("Sign Out");
		btnLogOut.setFont(btnLogOut.getFont().deriveFont(14f));
		btnLogOut.setBackground(new Color(0, 204, 153));
		btnLogOut.setForeground(Color.white);
		btnLogOut.setBounds(650,30,100, 30);
		btnLogOut.setFocusPainted(false);
		f.add(btnLogOut);

		btnLogOut.addActionListener(e -> {
			frame.remove(f);
			openSignInForm(frame, new Login_Account());
		});

		JLabel lUser = new JLabel(client.getFName() + " " + client.getLName());
		lUser.setFont(lUser.getFont().deriveFont(30f));
		lUser.setBounds(100,50,250, 40);
		f.add(lUser);

		JLabel lDesg = new JLabel("User");
		lDesg.setBounds(100,80,100, 40);
		f.add(lDesg);

		JButton btnAccInfo = new JButton("Account Info");
		btnAccInfo.setBounds(100,180,150, 40);
		btnAccInfo.setBackground(new Color(0, 204, 153));
		btnAccInfo.setForeground(Color.white);
		f.add(btnAccInfo);

		btnAccInfo.addActionListener(e -> open_info_page(frame, client, account));


		JButton btnTransferMoney = new JButton("Transfer Money");
		btnTransferMoney.setBounds(550,180,150, 40);
		btnTransferMoney.setBackground(new Color(0, 204, 153));
		btnTransferMoney.setForeground(Color.white);
		f.add(btnTransferMoney);

		btnTransferMoney.addActionListener(e -> {
			frame.remove(f);
			openTransferMoneyForm(frame, client, account);
		});

		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}

	// Client Account Info Page
	void open_info_page(JFrame frame, Client client, Bank_Account account) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);
		remakeScreen(frame, f);

		JLabel lAccInfo = new JLabel("Account Info");
		lAccInfo.setHorizontalAlignment(JLabel.CENTER);
		lAccInfo.setFont(lAccInfo.getFont().deriveFont(30f));
		lAccInfo.setBounds(300,40,200, 40);
		f.add(lAccInfo);

		// Account Holder Name
		JLabel l_name = new JLabel("Account Holder Name");
		l_name.setHorizontalAlignment(JLabel.CENTER);
		l_name.setBounds(300, 70, 200, 90);
		f.add(l_name);

		JTextField tf_name = new JTextField();
		tf_name.setHorizontalAlignment(JTextField.CENTER);
		tf_name.setBounds(300, 130 ,200, 25);
		tf_name.setText(client.getFName() + " " + client.getLName());
		tf_name.setEditable(false);
		f.add(tf_name);

		// Account Number
		JLabel l_acc_num = new JLabel("Account Number:");
		l_acc_num.setHorizontalAlignment(JLabel.CENTER);
		l_acc_num.setBounds(150, 160, 200, 90);
		f.add(l_acc_num);

		JTextField tf_acc_num = new JTextField();
		tf_acc_num.setHorizontalAlignment(JTextField.CENTER);
		tf_acc_num.setBounds(150, 220 ,200, 25);
		tf_acc_num.setText(account.getAccountNum());
		tf_acc_num.setEditable(false);
		f.add(tf_acc_num);

		// Account Type
		JLabel l_acc_type = new JLabel("Account Type");
		l_acc_type.setHorizontalAlignment(JLabel.CENTER);
		l_acc_type.setBounds(400, 160, 200, 90);
		f.add(l_acc_type);

		JTextField tf_acc_type = new JTextField(account.getType());
		tf_acc_type.setHorizontalAlignment(JTextField.CENTER);
		tf_acc_type.setBounds(400, 220 ,200, 25);
		tf_acc_type.setEditable(false);
		f.add(tf_acc_type);

		// Balance
		JLabel l_balance = new JLabel("Current Balance");
		l_balance.setHorizontalAlignment(JLabel.CENTER);
		l_balance.setBounds(150, 250, 200, 90);
		f.add(l_balance);

		JTextField tf_balance = new JTextField();
		tf_balance.setHorizontalAlignment(JTextField.CENTER);
		tf_balance.setBounds(150, 310 ,200, 25);
		tf_balance.setText(account.getBalance());
		tf_balance.setEditable(false);
		f.add(tf_balance);

		// Opening Date
		JLabel l_open_date = new JLabel("Opening Date");
		l_open_date.setHorizontalAlignment(JLabel.CENTER);
		l_open_date.setBounds(400, 250, 200, 90);
		f.add(l_open_date);

		JTextField tf_open_date = new JTextField();
		tf_open_date.setHorizontalAlignment(JTextField.CENTER);
		tf_open_date.setBounds(400, 310 ,200, 25);
		tf_open_date.setText(account.getOpeningDate());
		tf_open_date.setEditable(false);
		f.add(tf_open_date);

		JButton btn_BACK = new JButton("Main Menu");
		btn_BACK.setBackground(new Color(0, 204, 153));
		btn_BACK.setForeground(Color.white);
		btn_BACK.setBounds(50,400,100, 30);
		f.add(btn_BACK);

		btn_BACK.addActionListener(e -> {
			frame.remove(f);
			openClientMenu(frame, client, account);
		});

		JButton btn_sign_out = new JButton("Sign Out");
		btn_sign_out.setBackground(new Color(0, 204, 153));
		btn_sign_out.setForeground(Color.white);
		btn_sign_out.setBounds(650,30,100, 30);
		f.add(btn_sign_out);

		btn_sign_out.addActionListener(e -> {
			frame.remove(f);
			openSignInForm(frame, new Login_Account());
		});
	}

	// Money Transfer Screen
	void openTransferMoneyForm(JFrame frame, Client client, Bank_Account account) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);

		JLabel lTransfer = new JLabel("Transfer Money");
		lTransfer.setFont(lTransfer.getFont().deriveFont(30f));
		lTransfer.setBounds(300,60,300, 40);
		f.add(lTransfer);

		JLabel l_rAccNum = new JLabel("Receiver Account:");
		l_rAccNum.setBounds(200,140,200, 40);
		f.add(l_rAccNum);

		JLabel l_amount = new JLabel("Amount:");
		l_amount.setBounds(200,190,100, 40);
		f.add(l_amount);

		JTextField tf_rAccNum = new JTextField();
		tf_rAccNum.setBounds(400,150,180, 25);
		f.add(tf_rAccNum);

		JTextField tf_amount = new JTextField();
		tf_amount.setBounds(400,200,180, 25);
		f.add(tf_amount);

		JButton btn_Transfer = new JButton("Transfer");
		btn_Transfer.setBackground(new Color(0, 204, 153));
		btn_Transfer.setForeground(Color.white);
		btn_Transfer.setBounds(300,270,180, 40);
		f.add(btn_Transfer);
		JButton btn_mm = new JButton("Main Menu");
		btn_mm.setBackground(new Color(0, 204, 153));
		btn_mm.setForeground(Color.white);
		btn_mm.setBounds(50,400,100, 30);
		f.add(btn_mm);

		btn_mm.addActionListener(e -> {
			frame.remove(f);
			openClientMenu(frame, client, account);
		});

		JButton btn_sign_out = new JButton("Sign Out");
		btn_sign_out.setBackground(new Color(0, 204, 153));
		btn_sign_out.setForeground(Color.white);
		btn_sign_out.setBounds(650,30,100, 30);
		f.add(btn_sign_out);

		btn_sign_out.addActionListener(e -> {
			frame.remove(f);
			openSignInForm(frame, new Login_Account());
		});

		btn_Transfer.addActionListener(e -> {
			String receiverAcc = tf_rAccNum.getText();
			String amountStr = tf_amount.getText();

			if (receiverAcc.isEmpty() || amountStr.isEmpty()) {
				JOptionPane.showMessageDialog(f, "Fill all fields!");
				return;
			}

			if (receiverAcc.equals(account.getAccountNum())) {
				JOptionPane.showMessageDialog(f, "Cannot transfer to yourself!");
				return;
			}

			try (DB_handler db = new DB_handler()) {
				Connection conn = db.getConnection();
				int amount = Integer.parseInt(amountStr);
				int result = client.transferMoney(conn, receiverAcc, amount);

				switch (result) {
					case 0:
						JOptionPane.showMessageDialog(f, "Transfer successful!");
						account.updateBalance(conn);
						frame.remove(f);
						openClientMenu(frame, client, account);
						break;
					case 1:
						JOptionPane.showMessageDialog(f, "Account not found");
						break;
					case 2:
						JOptionPane.showMessageDialog(f, "Insufficient balance");
						break;
					default:
						JOptionPane.showMessageDialog(f, "Transfer failed");
				}
				conn.close();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(f, "Invalid amount format");
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(f, "Database error: " + ex.getMessage());
			}
		});

		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}

//	//----------------------------------------------------
//	//               Manager Related Screens              |
//	//----------------------------------------------------

	void openManagerMenu(JFrame frame, Manager manager) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);

		JButton btn_sign_out = new JButton("Sign Out");
		btn_sign_out.setBackground(new Color(0, 204, 153));
		btn_sign_out.setForeground(Color.white);
		btn_sign_out.setBounds(650,30,100, 30);
		f.add(btn_sign_out);

		btn_sign_out.addActionListener(e -> {
			frame.remove(f);
			openSignInForm(frame, new Login_Account());
		});

		JLabel lUser = new JLabel(manager.getName());
		lUser.setFont(lUser.getFont().deriveFont(30f));
		lUser.setBounds(100,50,300, 40);
		f.add(lUser);

		JLabel lDesg = new JLabel("Manager");
		lDesg.setBounds(100,80,100, 40);
		f.add(lDesg);

		JButton btnCreateAcc = new JButton("Create Account");
		btnCreateAcc.setBounds(100,180,150, 40);
		btnCreateAcc.setBackground(new Color(0, 204, 153));
		btnCreateAcc.setForeground(Color.white);
		f.add(btnCreateAcc);

		btnCreateAcc.addActionListener(e -> {
			frame.remove(f);
			openCreateAccountForm(frame, manager);
		});

		JButton btnBlockAcc = new JButton("Block/Unblock Account");
		btnBlockAcc.setBounds(300,180,200, 40);
		btnBlockAcc.setBackground(new Color(0, 204, 153));
		btnBlockAcc.setForeground(Color.white);
		f.add(btnBlockAcc);

		btnBlockAcc.addActionListener(e -> {
			frame.remove(f);
			open_block_unblock_account_page(frame, manager);
		});


		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}
	// Manager: Create Account Form
	void openCreateAccountForm(JFrame frame, Manager manager) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);

		JLabel lCreate = new JLabel("Create Account");
		lCreate.setFont(lCreate.getFont().deriveFont(30f));
		lCreate.setBounds(300,40,300, 40);
		f.add(lCreate);

		// Form fields
		JLabel[] labels = {
				new JLabel("First Name:"), new JLabel("Last Name:"),
				new JLabel("Father Name:"), new JLabel("Mother Name:"),
				new JLabel("CNIC:"), new JLabel("Date of Birth (DD,MM,YYYY):"),
				new JLabel("Phone:"), new JLabel("Email:"),
				new JLabel("Address:"), new JLabel("Account Type:")
		};

		JTextField[] fields = new JTextField[9];
		for (int i = 0; i < 9; i++) fields[i] = new JTextField();

		// Position components
		int y = 100;
		for (int i = 0; i < labels.length; i++) {
			labels[i].setBounds((i % 2 == 0) ? 100 : 400, y, 150, 40);
			f.add(labels[i]);

			if (i < 9) {
				fields[i].setBounds((i % 2 == 0) ? 100 : 400, y + 30, 180, 25);
				f.add(fields[i]);
			}
			y += (i % 2 == 1) ? 70 : 0;
		}

		// Account type dropdown
		JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Savings", "Current"});
		typeCombo.setBounds(400, y + 30, 180, 25);
		f.add(typeCombo);

		JButton btnCreate = new JButton("Create");
		btnCreate.setBackground(new Color(0, 204, 153));
		btnCreate.setForeground(Color.white);
		btnCreate.setBounds(350,400,100, 40);
		f.add(btnCreate);

		btnCreate.addActionListener(e -> {
			try (DB_handler db = new DB_handler()) {
				Connection conn = db.getConnection();

				Client newClient = new Client(
						fields[0].getText(), fields[1].getText(),
						fields[2].getText(), fields[3].getText(),
						fields[4].getText(), fields[5].getText(),
						fields[6].getText(), fields[7].getText(),
						fields[8].getText()
				);

				int result = manager.createAccount(conn, newClient,
						(String) typeCombo.getSelectedItem()
				);

				if (result == 0) {
					JOptionPane.showMessageDialog(f, "Account created!");
					frame.remove(f);
					openManagerMenu(frame, manager);
				} else {
					JOptionPane.showMessageDialog(f, "Error: " +
							(result == 1 ? "Duplicate CNIC" : "Creation failed"));
				}
				conn.close();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(f, "Database error: " + ex.getMessage());
			}
		});

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setBackground(new Color(0, 204, 153));
		btnBack.setForeground(Color.white);
		btnBack.setBounds(50,400,100, 30);
		btnBack.addActionListener(e -> {
			frame.remove(f);
			openManagerMenu(frame, manager);
		});
		f.add(btnBack);

		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}

	// Manager: Block/Unblock Screen
	void open_block_unblock_account_page(JFrame frame, Manager manager) {
		JPanel f = new JPanel();
		f.setBackground(Color.white);
		remakeScreen(frame, f);

		JLabel lTitle = new JLabel("Block/Unblock Account");
		lTitle.setFont(lTitle.getFont().deriveFont(30f));
		lTitle.setBounds(250,40,300, 40);
		f.add(lTitle);

		JLabel lAccNum = new JLabel("Account Number:");
		lAccNum.setBounds(200,100,150, 40);
		f.add(lAccNum);

		JLabel lCNIC = new JLabel("CNIC:");
		lCNIC.setBounds(200,150,150, 40);
		f.add(lCNIC);

		JTextField tfAccNum = new JTextField();
		tfAccNum.setBounds(350,100,200,25);
		f.add(tfAccNum);

		JTextField tfCNIC = new JTextField();
		tfCNIC.setBounds(350,150,200,25);
		f.add(tfCNIC);

		JButton btnBlock = new JButton("Block");
		btnBlock.setBackground(new Color(0, 204, 153));
		btnBlock.setForeground(Color.white);
		btnBlock.setBounds(250,200,100,40);
		f.add(btnBlock);

		JButton btnUnblock = new JButton("Unblock");
		btnUnblock.setBackground(new Color(0, 204, 153));
		btnUnblock.setForeground(Color.white);
		btnUnblock.setBounds(400,200,100,40);
		f.add(btnUnblock);

		btnBlock.addActionListener(e -> handleBlockAction(f, manager, tfAccNum.getText(), tfCNIC.getText(), true));
		btnUnblock.addActionListener(e -> handleBlockAction(f, manager, tfAccNum.getText(), tfCNIC.getText(), false));

		// Back button
		JButton btnBack = new JButton("Back");
		btnBack.setBackground(new Color(0, 204, 153));
		btnBack.setForeground(Color.white);
		btnBack.setBounds(50,400,100, 30);
		btnBack.addActionListener(ev -> {
			frame.remove(f);
			openManagerMenu(frame, manager);
		});
		f.add(btnBack);

		f.setLayout(null);
		frame.setContentPane(f);
		frame.setVisible(true);
	}


	private void handleBlockAction(JPanel panel, Manager manager, String accNum, String cnic, boolean block) {
		try (DB_handler db = new DB_handler()) {
			Connection conn = db.getConnection();
			int result = block ?
					manager.blockAccount(conn, Integer.parseInt(accNum), cnic) :
					manager.unblockAccount(conn, Integer.parseInt(accNum), cnic);

			if (result == 0) {
				JOptionPane.showMessageDialog(panel, "Operation successful!");
			} else {
				JOptionPane.showMessageDialog(panel, "Failed: " +
						(result == -1 ? "Invalid CNIC" : "Account not found"));
			}
			conn.close();
		} catch (NumberFormatException | SQLException ex) {
			JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
		}
	}


} // End of GUI class
