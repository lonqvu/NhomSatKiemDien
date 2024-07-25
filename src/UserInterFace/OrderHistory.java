package UserInterFace;

import Entity.Classify;
import Entity.Unit;
import Utils.DatabaseUtil;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class OrderHistory extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private String sql = "SELECT * FROM Orders  where isPayed = 1 Order By Date ASC ";

    private Detail detail;

    public OrderHistory(Detail d) {
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        connection();
        detail = new Detail(d);
        Load(sql);
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Load(String sql) {
        DefaultTableModel model = (DefaultTableModel) tableOrderHistory.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        model.setRowCount(0);
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"STT","Mã hóa đơn", "Ngày", "Người lập đơn", "Tiền hàng", "Nợ cũ", "Tổng cộng", "Đã thu", "Còn nợ", "Khách hàng", "Điện thoại", "Địa chỉ", "Ghi chú"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Làm cho toàn bộ bảng không thể chỉnh sửa
                    return false;
                }
            };
            int stt = 1;
            while (rs.next()) {
                stt++;
                Vector vector = new Vector();
                vector.add(stt);
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getDate("Date"));
                vector.add(rs.getString("StaffName"));
                vector.add(decimalFormat.format(rs.getBigDecimal("TotalMoneyBill")));
                vector.add(decimalFormat.format(rs.getBigDecimal("OldDebt")));
                vector.add(decimalFormat.format(rs.getBigDecimal("TotalMoneyOrder")));
                vector.add(decimalFormat.format(rs.getBigDecimal("PayMoney")));
                vector.add(decimalFormat.format(rs.getBigDecimal("DebtBack")));
                vector.add(rs.getString("Customer"));
                vector.add(rs.getString("Phone"));
                vector.add(rs.getString("Address"));
                vector.add(rs.getString("Note"));
                modle.addRow(vector);
            }
            tableOrderHistory.setModel(modle);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Unit getUnitById(String id) {
        Unit unit = null;
        String sql = "select * from Unit where id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                unit = new Unit(rs.getString("ID"), rs.getString("Unit"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return unit;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableOrderHistory = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableOrderHistory.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableOrderHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableOrderHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableOrderHistoryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableOrderHistory);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Trạng Thái");
        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnBackHome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBackHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnBackHome.setText("Hệ Thống");
        btnBackHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackHomeMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Lịch Sử Đơn Hàng");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1169, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBackHome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 11, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableOrderHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableOrderHistoryMouseClicked
        int Click = tableOrderHistory.getSelectedRow();
        TableModel model = tableOrderHistory.getModel();

        if (evt.getClickCount() == 2) {
            OrderDetailDialog dialog = new OrderDetailDialog(this, true, model.getValueAt(Click, 1).toString());
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

    }//GEN-LAST:event_tableOrderHistoryMouseClicked

    private void btnBackHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackHomeMouseClicked
        if (this.detail.getUser().toString().toString().equals("Admin")) {
            Home home = new Home(detail);
            this.setVisible(false);
            home.setVisible(true);
        } else {
            HomeUser home = new HomeUser(detail);
            this.setVisible(false);
            home.setVisible(true);
        }
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

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new OrderHistory(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackHome;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tableOrderHistory;
    // End of variables declaration//GEN-END:variables
}
