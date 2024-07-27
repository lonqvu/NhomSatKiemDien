package UserInterFace;

import Entity.Role;
import Utils.DatabaseUtil;
import java.awt.Color;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Account extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private Boolean Add = false, Change = false;
    private String sql = "SELECT * FROM Accounts";

    private static Detail detail;

    public Account(Detail d) {
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail = new Detail(d);
        connection();
        Load(sql);
        Disabled();
        loadRole();
    }

    private static boolean showPasswordDialog() {
        CheckAdminDialog dialog = new CheckAdminDialog(new javax.swing.JFrame(), true, detail.getUser());
        dialog.setVisible(true);
        return dialog.isCheckLogin();
    }

    private void loadRole() {
        String sql = "SELECT * FROM Role";
        cbxRole.removeAllItems();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cbxRole.addItem(new Role(rs.getInt("ID"), rs.getString("RoleName")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Load(String sql) {
        TableAccount.removeAll();

        try ( PreparedStatement pst = conn.prepareStatement(sql)) {
            String[] arr = {"Tên Đăng Nhập", "Mật Khẩu", "Tên Nhân Viên", "Quyền", "Ngày Tạo", "HiddenValue"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Làm cho toàn bộ bảng không thể chỉnh sửa
                    return false;
                }
            };
            try ( ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("RegistDate");

                    Vector vector = new Vector();
                    vector.add(rs.getString("Username").trim());
                    vector.add(rs.getString("PassWord").trim());
                    vector.add(rs.getString("FullName").trim());
                    Role role = getRoleById(rs.getString("RoleID"));
                    if (Objects.isNull(role) == false) {
                        vector.add(role);
                    } else {
                        vector.add(null);
                    }
                    vector.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
                    vector.add(role != null ? role.getId() : null);
                    modle.addRow(vector);
                }
                TableAccount.setModel(modle);
                TableColumn tableColumn = TableAccount.getColumnModel().getColumn(5);
                TableAccount.getColumnModel().removeColumn(tableColumn);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Role getRoleById(String roleId) {
        try {
            String sql = "Select * From Role where ID = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, roleId);
            rs = pst.executeQuery();
            while (rs.next()) {
                return new Role(rs.getInt("ID"), rs.getString("RoleName"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void Enabled() {
        user.setEnabled(true);
        pass.setEnabled(true);
        cbxRole.setEnabled(true);
        txbName.setEnabled(true);
    }

    private void Disabled() {
        user.setEnabled(false);
        pass.setEnabled(false);
        cbxRole.setEnabled(false);
        txbName.setEnabled(false);
    }

    private void Refresh() {
        cbxRole.removeAllItems();
        loadRole();
        Add = false;
        Change = false;
        this.user.setText("");
        this.pass.setText("");
        this.txbName.setText("");
        this.btnChange.setEnabled(false);
        this.btnAdd.setEnabled(true);
        this.btnSave.setEnabled(false);
        this.btnDelete.setEnabled(false);
    }

    private void addAccount() {
        boolean checkLogin = showPasswordDialog();
        if (checkLogin) {
            Role role = (Role) cbxRole.getSelectedItem();
            String sqlInsert = "INSERT INTO Accounts (UserName,PassWord,FullName,RoleID ,RegistDate, UpdateDate) VALUES(?,?,?,?,?,?)";
            if (checkNull()) {
                try {
                    pst = conn.prepareStatement(sqlInsert);
                    pst.setString(1, this.user.getText());
                    pst.setString(2, this.pass.getText());
                    pst.setString(3, txbName.getText());
                    pst.setInt(4, role.getId());

                    LocalDateTime now = LocalDateTime.now();
                    Timestamp sqlDate = Timestamp.valueOf(now);

                    pst.setTimestamp(5, sqlDate);
                    pst.setTimestamp(6, sqlDate);
                    pst.executeUpdate();
                    Load(sql);
                    Disabled();
                    Refresh();

                    JOptionPane.showMessageDialog(null, "Thêm tài khoản thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void changeAccount() {
        boolean checkLogin = showPasswordDialog();
        if (checkLogin) {
            int Click = TableAccount.getSelectedRow();
            TableModel model = TableAccount.getModel();
            Role role = (Role) cbxRole.getSelectedItem();
            String sqlUpdate = "UPDATE Accounts SET UserName=?,PassWord=?,FullName=?,UpdateDate=?, RoleID=? WHERE UserName='" + model.getValueAt(Click, 0).toString().trim() + "'";

            if (checkNull()) {
                try {
                    pst = conn.prepareStatement(sqlUpdate);
                    pst.setString(1, this.user.getText());
                    pst.setString(2, this.pass.getText());
                    pst.setString(3, this.txbName.getText());

                    LocalDateTime date = LocalDateTime.now();
                    Timestamp timestamp = Timestamp.valueOf(date);
                    pst.setTimestamp(4, timestamp);
                    pst.setInt(5, role.getId());

                    pst.executeUpdate();
                    Disabled();
                    Refresh();
                    JOptionPane.showMessageDialog(null, "Lưu thay đổi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    Load(sql);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private boolean Check() {
        boolean kq = true;
        String sqlCheck = "SELECT * FROM Accounts";
        try {
            pst = conn.prepareStatement(sqlCheck);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (this.user.getText().equals(rs.getString("Username").toString().trim())) {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kq;
    }

    private boolean checkNull() {
        boolean kq = true;
        if (this.user.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập tên đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (this.pass.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
//        if(((JTextField)date.getDateEditor().getUiComponent()).getText().equals("")){
//            lblStatus.setText("Bạn chưa nhập ngày tạo tài khoản");
//            return false;
//        }
        return kq;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TableAccount = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        user = new javax.swing.JTextField();
        pass = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txbName = new javax.swing.JTextField();
        cbxRole = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnChange = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        TableAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên đăng nhập", "Mật khẩu", "Tên nhân viên", "Quyền", "Ngày tạo"
            }
        ));
        TableAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableAccountMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TableAccount);

        btnBack.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnBack.setText("Hệ Thống");
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Cập Nhật Tài Khoản Đăng Nhập");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Tên Đăng Nhập:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Mật Khẩu:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tên Nhân Viên:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Quyền:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxRole, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        btnChange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Change.png"))); // NOI18N
        btnChange.setText("Sửa");
        btnChange.setEnabled(false);
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Add.png"))); // NOI18N
        btnAdd.setText("Thêm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Delete.png"))); // NOI18N
        btnDelete.setText("Xóa");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Save.png"))); // NOI18N
        btnSave.setText("Lưu");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd)
                        .addComponent(btnChange)
                        .addComponent(btnDelete)
                        .addComponent(btnSave)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnBack)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TableAccountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableAccountMouseClicked
        int Click = TableAccount.getSelectedRow();
        TableModel model = TableAccount.getModel();

        user.setText(model.getValueAt(Click, 0).toString());
        pass.setText(model.getValueAt(Click, 1).toString());
        txbName.setText(model.getValueAt(Click, 2).toString());
        cbxRole.setSelectedItem(getRoleById(model.getValueAt(Click, 5).toString()));

        btnDelete.setEnabled(true);
        btnChange.setEnabled(true);
    }//GEN-LAST:event_TableAccountMouseClicked

    private void btnBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackMouseClicked
        Home login = new Home(detail);
        this.setVisible(false);
        login.setVisible(true);
    }//GEN-LAST:event_btnBackMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add = true;
        Enabled();
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        int Click = TableAccount.getSelectedRow();
        TableModel model = TableAccount.getModel();

        Add = false;
        Change = true;
        Enabled();
//        loadEmployees();

        if (model.getValueAt(Click, 0).toString().trim().equals("Admin")) {
            user.setEnabled(false);
        }
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        Refresh();
        Disabled();
        Load(sql);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (Add == true) {
            if (Check()) {
                addAccount();
            } else {
                JOptionPane.showMessageDialog(null, "Thêm tài khoản thất bại, Tên đăng nhập đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } else if (Change == true) {
            changeAccount();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        boolean checkLogin = showPasswordDialog();
        if (checkLogin) {
            int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa tài khoản hay không?", "Thông Báo", 2);
            if (Click == JOptionPane.YES_OPTION) {
                if (this.user.getText().equals("Admin")) {
                    JOptionPane.showMessageDialog(null, "Không thể xóa tài khoản của Admin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                } else {
                    String sqlDelete = "DELETE FROM Accounts WHERE UserName = ? AND PassWord=?";
                    try {
                        pst = conn.prepareStatement(sqlDelete);
                        pst.setString(1, this.user.getText());
                        pst.setString(2, this.pass.getText());
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Xóa tài khoản thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        Load(sql);
                        Refresh();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

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

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new Account(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableAccount;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<Role> cbxRole;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField pass;
    private javax.swing.JTextField txbName;
    private javax.swing.JTextField user;
    // End of variables declaration//GEN-END:variables
}
