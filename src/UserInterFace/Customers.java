package UserInterFace;

import static UserInterFace.PayDialog.user;
import Utils.DatabaseUtil;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Customers extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    static Detail detail;
    private boolean Add = false, Change = false;
    private static Customers instance;

    String sql = "SELECT * FROM Customer Order by CustomerName";

    public Customers(Detail d) {
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail = new Detail(d);
        connection();
        loadCustomer(sql);
        DisabledCustomer();
        txbPay.setText("0");
        instance = this; // Lưu instance hiện tại
//        if(this.detail.getUser().toString().toString().equals("User")){
//            jTabbedPane1.setEnabledAt(1, false);
//        }
    }

    // Phương thức static để lấy instance hiện tại
    public static Customers getInstance() {
        return instance;
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCustomer(String sql) {
        tableCustomer.removeAll();
        try {
            String[] arr = {"ID", "Tên Khách Hàng", "Số Điện Thoại", "Địa Chỉ", "Tiền Nợ"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Làm cho toàn bộ bảng không thể chỉnh sửa
                    return false;
                }
            };
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String disp = "";
                String debt = rs.getString("Debt");
                if(debt == null || debt.isBlank() || debt.isEmpty()){
                  disp  = "";
                }
                else{
                   disp =  debt.trim();
                }
                 
                Vector vector = new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("CustomerName").trim());
                vector.add(rs.getString("PhoneNumber").trim());
                vector.add(rs.getString("Address").trim());
                
                if(!disp.equals("")){
                vector.add(moneyDis(disp));
                }
                modle.addRow(vector);
            }
            tableCustomer.setModel(modle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Phương thức công khai để cập nhật dữ liệu từ ThanhToanNo
    public void loadData() {
        loadCustomer(sql);
    }

    private String moneyDis(String money) {
        DecimalFormat format = new DecimalFormat("#,##0.##");
        String price = format.format(Double.parseDouble(money));
        return price;
    }

    private void backHome() {
        Home home = new Home(detail);
        this.setVisible(false);
        home.setVisible(true);
    }

    private void EnabledCustomer() {
        txbCustomerName.setEnabled(true);
        txbPhoneNumber.setEnabled(true);
        txbAddress.setEnabled(true);
        txbDebt.setEnabled(true);
    }

    private void DisabledCustomer() {
        txbCustomerName.setEnabled(false);
        txbPhoneNumber.setEnabled(false);
        txbAddress.setEnabled(false);
        txbDebt.setEnabled(false);
    }

    private void Refresh() {
        Change = false;
        Add = false;
        txbCustomerName.setText("");
        txbPhoneNumber.setText("");
        txbAddress.setText("");
        txbDebt.setText("0");
        txbPay.setText("0");
        btnAddCustomer.setEnabled(true);
        btnChangeCustomer.setEnabled(false);
        btnDeleteCustomer.setEnabled(false);
        btnSaveCustomer.setEnabled(false);
        txbPay.setEnabled(false);
        DisabledCustomer();
    }

    private boolean checkCustomer() {
        boolean kq = true;
        String sqlCheck = "SELECT * FROM Customer";
        try {
            pst = conn.prepareStatement(sqlCheck);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (this.txbCustomerName.getText().equals(rs.getString("CustomerName").toString().trim())) {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kq;
    }

    private boolean checkNullCustomer() {
        boolean kq = true;
        if (String.valueOf(this.txbCustomerName.getText()).length() == 0) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập tên khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return kq;
    }

    private void addCustomer() {
        if (checkNullCustomer()) {
            String sqlInsert = "INSERT INTO Customer (CustomerName, Address, PhoneNumber, Debt, OldDebt) VALUES(?,?,?,?,?)";
            try {
                pst = conn.prepareStatement(sqlInsert);
                pst.setString(1, txbCustomerName.getText());
                pst.setString(2, txbAddress.getText());
                pst.setString(3, txbPhoneNumber.getText());
                pst.setBigDecimal(4, convertToMoney(txbDebt.getText()));
                pst.setBigDecimal(5, convertToMoney(txbDebt.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                DisabledCustomer();
                Refresh();
                loadCustomer(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private BigDecimal convertToMoney(String money) {
        String text = money.replace(",", ""); // Remove existing commas
        if (text.isEmpty()) {
            return null;
        }
        try {
            BigDecimal number = BigDecimal.valueOf(Double.parseDouble(text));
            return number;
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void changedCustomer() {
        boolean checkLogin = showPasswordDialog();
        if (checkLogin) {
            int Click = tableCustomer.getSelectedRow();
            TableModel model = tableCustomer.getModel();
            if (checkNullCustomer()) {
                String sqlChange = "UPDATE Customer SET CustomerName=?, PhoneNumber=?, Address=?, Debt=?, OldDebt=? WHERE ID='" + model.getValueAt(Click, 0).toString().trim() + "'";;
                try {
                    pst = conn.prepareStatement(sqlChange);
                    pst.setString(1, txbCustomerName.getText());
                    pst.setString(2, txbPhoneNumber.getText());
                    pst.setString(3, txbAddress.getText());
                    if(txbDebt.getText().isBlank() || txbDebt.getText().isEmpty()){
                        JOptionPane.showMessageDialog(null, "Vui lòng khônng đươc để trống", "Thông báo", JOptionPane.ERROR);
                    }
                    else{
                    if (convertToMoney(txbPay.getText()).compareTo(BigDecimal.ZERO) != 0) {
                        if (convertToMoney(txbDebt.getText()).compareTo(BigDecimal.ZERO) == 0) {
                            JOptionPane.showMessageDialog(null, "Khách hàng này hiện chưa có khoản nợ nào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        } else {
                            if (convertToMoney(txbDebt.getText()).compareTo(convertToMoney(txbPay.getText())) < 0) {
                                pst.setBigDecimal(4, BigDecimal.ZERO);
                                pst.setBigDecimal(5, BigDecimal.ZERO);
                            } else {
                                pst.setBigDecimal(4, convertToMoney(txbDebt.getText()).subtract(convertToMoney(txbPay.getText())));
                                pst.setBigDecimal(5, convertToMoney(txbDebt.getText()).subtract(convertToMoney(txbPay.getText())));
                            }
                        }
                    } else {
                        pst.setBigDecimal(4, convertToMoney(txbDebt.getText()));
                        pst.setBigDecimal(5, convertToMoney(txbDebt.getText()));
                    }}
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Lưu thay đổi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    DisabledCustomer();
                    Refresh();
                    loadCustomer(sql);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Mật khẩu không hợp lệ", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static boolean showPasswordDialog() {
        CheckAdminDialog dialog = new CheckAdminDialog(new javax.swing.JFrame(), true, detail.getUser());
        dialog.setVisible(true);
        return dialog.isCheckLogin();
    }

    private double convertedToNumbers(String s) {
        String number = "";
        String[] array = s.replace(",", " ").split("\\s");
        for (String i : array) {
            number = number.concat(i);
        }
        return Double.parseDouble(number);
    }

    private String cutChar(String arry) {
        return arry.replaceAll("\\D+", "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableCustomer = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txbCustomerName = new javax.swing.JTextField();
        txbPhoneNumber = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txbAddress = new javax.swing.JTextField();
        txbDebt = new javax.swing.JTextField();
        txbPay = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        btnRefreshClassify = new javax.swing.JButton();
        btnAddCustomer = new javax.swing.JButton();
        btnChangeCustomer = new javax.swing.JButton();
        btnDeleteCustomer = new javax.swing.JButton();
        btnSaveCustomer = new javax.swing.JButton();
        jPanel27 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txbFind = new javax.swing.JTextField();
        btnFindProducer = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableCustomer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableCustomerMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableCustomer);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel13.setText("SDT: ");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Tên Khách Hàng: ");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setText("Địa Chỉ: ");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel16.setText("Tiền Nợ: ");

        txbDebt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbDebtKeyReleased(evt);
            }
        });

        txbPay.setEnabled(false);
        txbPay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPayKeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel17.setText("Thanh Toán:");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txbCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(115, 115, 115)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbDebt, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txbPay, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txbCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(txbDebt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(txbPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        txbAddress.getAccessibleContext().setAccessibleName("");

        btnRefreshClassify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshClassify.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshClassifyMouseClicked(evt);
            }
        });
        btnRefreshClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshClassifyActionPerformed(evt);
            }
        });

        btnAddCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Add.png"))); // NOI18N
        btnAddCustomer.setText("Thêm");
        btnAddCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomerActionPerformed(evt);
            }
        });

        btnChangeCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Change.png"))); // NOI18N
        btnChangeCustomer.setText("Sửa");
        btnChangeCustomer.setEnabled(false);
        btnChangeCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeCustomerActionPerformed(evt);
            }
        });

        btnDeleteCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Delete.png"))); // NOI18N
        btnDeleteCustomer.setText("Xóa");
        btnDeleteCustomer.setEnabled(false);
        btnDeleteCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCustomerActionPerformed(evt);
            }
        });

        btnSaveCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Save.png"))); // NOI18N
        btnSaveCustomer.setText("Lưu");
        btnSaveCustomer.setEnabled(false);
        btnSaveCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRefreshClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(btnChangeCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(btnDeleteCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(btnSaveCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(162, 162, 162)
                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(174, 174, 174))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRefreshClassify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnSaveCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel29, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChangeCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Tìm Kiếm:");

        btnFindProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindProducer.setText("Tìm");
        btnFindProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1246, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFindProducer)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, 1028, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFindProducer, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Khách Hàng", jPanel3);

        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Cập Nhật Thông Tin Khách Hàng");

        btnBackHome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBackHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnBackHome.setText("Hệ Thống");
        btnBackHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackHomeMouseClicked(evt);
            }
        });
        btnBackHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackHomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 1006, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackHome)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Khách hàng");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackHomeMouseClicked
        backHome();
    }//GEN-LAST:event_btnBackHomeMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int lick = JOptionPane.showConfirmDialog(null, "Bạn Có Muốn Thoát Khỏi Chương Trình Hay Không?", "Thông Báo", 2);
        if (lick == JOptionPane.OK_OPTION) {
            System.exit(0);
        } else {
            if (lick == JOptionPane.CANCEL_OPTION) {
                this.setVisible(true);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnFindCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindCustomerActionPerformed
        DefaultTableModel model = (DefaultTableModel) tableCustomer.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        model.setRowCount(0);
        String sql = "Select * from Customer where CustomerName like ? Order By CustomerName ASC";
        try ( PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, "%" + txbFind.getText() + "%");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                //                String productName = rs.getString("Name").trim();
                //                String price = decimalFormat.format(Double.parseDouble(rs.getString("Price").trim()));
                //                String unitId = rs.getString("UnitID").trim();
                //                String classifyId = rs.getString("ClassifyID").trim();

                Vector<String> vector = new Vector<>();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("CustomerName").trim());
                vector.add(rs.getString("PhoneNumber").trim());
                vector.add(rs.getString("Address").trim());
                vector.add(decimalFormat.format(Double.parseDouble(rs.getString("Debt").trim())));
                model.addRow(vector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnFindCustomerActionPerformed

    private void btnSaveCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveCustomerActionPerformed
        if (Add == true)
            if (checkCustomer()) {
                addCustomer();
            } else {
                JOptionPane.showMessageDialog(null, "Mã khách hàng hoặc tên khách hàng bạn nhập đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        else {
            if (Change == true) {
                changedCustomer();
            }
        }
    }//GEN-LAST:event_btnSaveCustomerActionPerformed

    private void btnDeleteCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCustomerActionPerformed
         boolean checkLogin = showPasswordDialog();
    if (checkLogin) {
        int click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa khách hàng hay không?", "Thông Báo", JOptionPane.YES_NO_OPTION);
        if (click == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM Customer WHERE ID=?";
            String sqlDeleteOrder = "DELETE FROM Orders WHERE CustomerID=?";
            String sqlDeleteBill = "DELETE FROM Bill WHERE OrderID IN "
                    + "(SELECT o.ID FROM Orders o Join Customer c on o.CustomerID = c.Id"
                    + " Where c.id = ?)";
            try {
                int selectedRow = tableCustomer.getSelectedRow();
                if (selectedRow != -1) { // Ensure a row is selected
                    TableModel model = tableCustomer.getModel();
                    String customerId = model.getValueAt(selectedRow, 0).toString();
                    // Delete bill for the order
                    pst = conn.prepareStatement(sqlDeleteBill);
                    pst.setString(1, customerId);
                    pst.executeUpdate();
                    
                    // Delete bill for the order
                    pst = conn.prepareStatement(sqlDeleteOrder);
                    pst.setString(1, customerId);
                    pst.executeUpdate();
                    
                    // Delete orders for the customer
                    pst = conn.prepareStatement(sqlDeleteOrder);
                    pst.setString(1, customerId);
                    pst.executeUpdate();

                    // Delete customer
                    pst = conn.prepareStatement(sqlDelete);
                    pst.setString(1, customerId);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    DisabledCustomer();
                    Refresh();
                    loadCustomer(sql);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi xóa khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    }//GEN-LAST:event_btnDeleteCustomerActionPerformed

    private void btnChangeCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeCustomerActionPerformed
        Add = false;
        Change = true;
        btnAddCustomer.setEnabled(false);
        btnChangeCustomer.setEnabled(false);
        btnDeleteCustomer.setEnabled(false);
        btnSaveCustomer.setEnabled(true);
        txbPay.setEnabled(true);
        EnabledCustomer();
    }//GEN-LAST:event_btnChangeCustomerActionPerformed

    private void btnAddCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomerActionPerformed
        Refresh();
        Add = true;
        btnAddCustomer.setEnabled(false);
        btnSaveCustomer.setEnabled(true);
        EnabledCustomer();
    }//GEN-LAST:event_btnAddCustomerActionPerformed

    private void btnRefreshClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshClassifyActionPerformed
        Refresh();
    }//GEN-LAST:event_btnRefreshClassifyActionPerformed

    private void btnRefreshClassifyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshClassifyMouseClicked
        Refresh();
    }//GEN-LAST:event_btnRefreshClassifyMouseClicked

    private void txbPayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPayKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        NumberFormat format = NumberFormat.getNumberInstance();

        txbPay.setText(cutChar(txbPay.getText()));
        if (txbPay.getText().equals("")) {
           txbPay.setText("0");
        } else {
            txbPay.setText(formatter.format(convertedToNumbers(txbPay.getText())));
        }
    }//GEN-LAST:event_txbPayKeyReleased

    private void txbDebtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbDebtKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        NumberFormat format = NumberFormat.getNumberInstance();

        txbDebt.setText(cutChar(txbDebt.getText()));
        if (txbDebt.getText().equals("")) {
            txbDebt.setText("0");
        } else {
            txbDebt.setText(formatter.format(convertedToNumbers(txbDebt.getText())));
        }
    }//GEN-LAST:event_txbDebtKeyReleased

    private void tableCustomerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableCustomerMouseClicked
        int Click = tableCustomer.getSelectedRow();
        TableModel model = tableCustomer.getModel();

        txbCustomerName.setText(model.getValueAt(Click, 1).toString());
        txbPhoneNumber.setText(model.getValueAt(Click, 2).toString());
        txbAddress.setText(model.getValueAt(Click, 3).toString());
        txbDebt.setText(model.getValueAt(Click, 4).toString());

        btnChangeCustomer.setEnabled(true);
        btnDeleteCustomer.setEnabled(true);

        if (evt.getClickCount() == 2) {
            int row = tableCustomer.getSelectedRow();
            if (row >= 0) {
                String customerId = tableCustomer.getValueAt(row, 0).toString();
                ThanhToanNo dialog = new ThanhToanNo(customerId, detail);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_tableCustomerMouseClicked

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackHomeActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Customers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new Customers(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCustomer;
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnChangeCustomer;
    private javax.swing.JButton btnDeleteCustomer;
    private javax.swing.JButton btnFindProducer;
    private javax.swing.JButton btnRefreshClassify;
    private javax.swing.JButton btnSaveCustomer;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tableCustomer;
    private javax.swing.JTextField txbAddress;
    private javax.swing.JTextField txbCustomerName;
    private javax.swing.JTextField txbDebt;
    private javax.swing.JTextField txbFind;
    private javax.swing.JTextField txbPay;
    private javax.swing.JTextField txbPhoneNumber;
    // End of variables declaration//GEN-END:variables
}
