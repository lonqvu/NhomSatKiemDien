/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterFace;

import Entity.Classify;
import Entity.Unit;
import Utils.DatabaseUtil;
import com.microsoft.sqlserver.jdbc.StringUtils;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author Admin
 */
public class CustomerDialog extends javax.swing.JDialog {

    private Detail detail;
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    String sql = "select * from customer";
    private String maKH = "";
    private String tenKH = "";
    private String diaChi = "";
    private String sdt = "";

    /**
     * Creates new form ProductDialog
     */
    public CustomerDialog(java.awt.Frame parent, boolean modal) {
        super(parent, "Chọn khách hàng", modal);
        initComponents();
        connection();
        Load();
        setLocationRelativeTo(null);
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Load() {
        DefaultTableModel model = (DefaultTableModel) tableKhachHang.getModel();
        model.setRowCount(0);

        int stt = 0;
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"STT", "Tên khách hàng", "Địa chỉ", "Số điện thoại", "CustomerID"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Làm cho toàn bộ bảng không thể chỉnh sửa
                    return false;
                }
            };

            while (rs.next()) {
                stt++;

                Vector vector = new Vector();
                vector.add(stt);
                vector.add(rs.getString("CustomerName").trim());
                vector.add(rs.getString("Address").trim());
                vector.add(rs.getString("PhoneNumber").trim());
                vector.add(rs.getString("ID").trim());

                modle.addRow(vector);
            }
            tableKhachHang.setModel(modle);
            TableColumn tableColumn4 = tableKhachHang.getColumnModel().getColumn(4);
            tableKhachHang.getColumnModel().removeColumn(tableColumn4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateTableDefault(String name) {
        DefaultTableModel model = (DefaultTableModel) tableKhachHang.getModel();

        String query = "SELECT * FROM Customer WHERE CustomerName LIKE ? OR PhoneNumber LIKE ?";

        try ( PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, "%" + name + "%");
            statement.setString(2, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            // Sử dụng Vector để lưu trữ các hàng mới
            Vector<Vector<Object>> rows = new Vector<>();
            int stt = 0;

            while (resultSet.next()) {
                Vector<Object> vector = new Vector<>();
                stt++;
                vector.add(stt);
                vector.add(resultSet.getString("CustomerName").trim());
                vector.add(resultSet.getString("Address").trim());
                vector.add(resultSet.getString("PhoneNumber").trim());
                vector.add(resultSet.getString("ID").trim());

                rows.add(vector);
            }

            // Cập nhật bảng trên EDT
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0); // Xóa tất cả các hàng hiện tại
                for (Vector<Object> row : rows) {
                    model.addRow(row); // Thêm các hàng mới
                }
                tableKhachHang.setModel(model);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txbKhachHang = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableKhachHang = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txbSoDienThoai = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txbDiaChi = new javax.swing.JTextField();
        txbFind = new javax.swing.JTextField();
        btnExit = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();
        btnSave2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));
        setForeground(new java.awt.Color(102, 102, 255));
        setLocation(new java.awt.Point(200, 300));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel1.setText("Tìm kiếm theo tên, số điện thoại");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel3.setText("Tên khách hàng");

        txbKhachHang.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbKhachHangupdateTotal(evt);
            }
        });
        txbKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbKhachHangActionPerformed(evt);
            }
        });
        txbKhachHang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbKhachHangKeyReleased(evt);
            }
        });

        tableKhachHang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tên khách hàng", "Địa chỉ", "Số điện thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableKhachHang.setColumnSelectionAllowed(true);
        tableKhachHang.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableKhachHang.setShowGrid(true);
        tableKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableKhachHangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableKhachHang);
        tableKhachHang.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tableKhachHang.getColumnModel().getColumnCount() > 0) {
            tableKhachHang.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableKhachHang.getColumnModel().getColumn(0).setMaxWidth(50);
            tableKhachHang.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableKhachHang.getColumnModel().getColumn(2).setMaxWidth(100);
            tableKhachHang.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableKhachHang.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel5.setText("Số điện thoại");

        txbSoDienThoai.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbSoDienThoaiupdateTotal(evt);
            }
        });
        txbSoDienThoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbSoDienThoaiActionPerformed(evt);
            }
        });
        txbSoDienThoai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbSoDienThoaiKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setText("Địa chỉ");

        txbDiaChi.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbDiaChiupdateTotal(evt);
            }
        });
        txbDiaChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbDiaChiActionPerformed(evt);
            }
        });
        txbDiaChi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbDiaChiKeyReleased(evt);
            }
        });

        txbFind.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbFindKeyReleased(evt);
            }
        });

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Exit mini.png"))); // NOI18N
        btnExit.setText("Thoát");
        btnExit.setToolTipText("");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/New.png"))); // NOI18N
        btnSave1.setText("Khách lẻ");
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        btnSave2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/New.png"))); // NOI18N
        btnSave2.setText("Thêm mới");
        btnSave2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 877, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txbSoDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                            .addComponent(txbKhachHang)
                                            .addComponent(txbDiaChi)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(59, 59, 59)
                                        .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSave2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(20, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(119, 119, 119))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txbKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txbDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSave2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(65, 65, 65)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txbKhachHangupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbKhachHangupdateTotal

    }//GEN-LAST:event_txbKhachHangupdateTotal

    private void txbKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbKhachHangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbKhachHangActionPerformed

    private void txbKhachHangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbKhachHangKeyReleased

    }//GEN-LAST:event_txbKhachHangKeyReleased

    private void tableKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableKhachHangMouseClicked
        int click = tableKhachHang.getSelectedRow();
        TableModel model = tableKhachHang.getModel();

        if (evt.getClickCount() == 2) {
            maKH = model.getValueAt(click, 4).toString();
            this.dispose();
        }
    }//GEN-LAST:event_tableKhachHangMouseClicked

    public String getKhachHang() {
        return maKH;
    }

    private void txbSoDienThoaiupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbSoDienThoaiupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbSoDienThoaiupdateTotal

    private void txbSoDienThoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbSoDienThoaiActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txbSoDienThoaiActionPerformed

    private void txbSoDienThoaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbSoDienThoaiKeyReleased

    }//GEN-LAST:event_txbSoDienThoaiKeyReleased

    private void txbDiaChiupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbDiaChiupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDiaChiupdateTotal

    private void txbDiaChiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbDiaChiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDiaChiActionPerformed

    private void txbDiaChiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbDiaChiKeyReleased

    }//GEN-LAST:event_txbDiaChiKeyReleased

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull) == false) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập thông tin khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        } else {
            tenKH = txbKhachHang.getText();
            diaChi = txbDiaChi.getText();
            sdt = txbSoDienThoai.getText();
            this.dispose();
        }
    }//GEN-LAST:event_btnSave1ActionPerformed

    public String getKhachLe() {
        return tenKH + "," + diaChi + "," + sdt;
    }

    private void txbFindKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbFindKeyReleased
        updateTableDefault(txbFind.getText());
    }//GEN-LAST:event_txbFindKeyReleased

    private void btnSave2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave2ActionPerformed
        String sqlInsert = "INSERT INTO Customer (CustomerName, Address, PhoneNumber, Debt, OldDebt, RegistDate) VALUES(?,?,?,?,?,?)";

        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull) == false) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập thông tin khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                pst = conn.prepareStatement(sqlInsert);

                LocalDateTime now = LocalDateTime.now();
                Timestamp sqlDate = Timestamp.valueOf(now);

                pst.setString(1, txbKhachHang.getText());
                pst.setString(2, txbDiaChi.getText());
                pst.setString(3, txbSoDienThoai.getText());
                pst.setBigDecimal(4, BigDecimal.ZERO);
                pst.setBigDecimal(5, BigDecimal.ZERO);
                pst.setTimestamp(6, sqlDate);
                pst.executeUpdate();

                Load();

                JOptionPane.showMessageDialog(null, "Thêm khách hàng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnSave2ActionPerformed

    private String checkNull() {
        String mess = "";
        if (txbKhachHang.getText().isBlank() || txbKhachHang.getText().isEmpty()) {
            mess = "Vui lòng nhập tên khách hàng!";
        }

        return mess;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CustomerDialog dialog = new CustomerDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave1;
    private javax.swing.JButton btnSave2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableKhachHang;
    private javax.swing.JTextField txbDiaChi;
    private javax.swing.JTextField txbFind;
    private javax.swing.JTextField txbKhachHang;
    private javax.swing.JTextField txbSoDienThoai;
    // End of variables declaration//GEN-END:variables
}
