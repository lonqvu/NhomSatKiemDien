/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterFace;

import Entity.Classify;
import Entity.Unit;
import static UserInterFace.Sale.checkIfFileExists;
import Utils.DatabaseUtil;
import com.microsoft.sqlserver.jdbc.StringUtils;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Admin
 */
public class OrderDetailDialog extends javax.swing.JDialog {

    private Detail detail;
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private String orderId = "";

    /**
     * Creates new form ProductDialog
     */
    public OrderDetailDialog(java.awt.Frame parent, boolean modal, String orderId) {
        super(parent, "Chi tiết đơn hàng", modal);
        initComponents();
        connection();
        this.orderId = orderId;
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
        DefaultTableModel model = (DefaultTableModel) tableOrderDetail.getModel();
        model.setRowCount(0);
        DecimalFormat formatter = new DecimalFormat("#.###");
        String sql = "select * from bill where OrderID = '" + orderId + "'";

        int stt = 0;
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"STT", "Tên hàng", "đơn vị tính", "Số mét", "Số tấm", "Hệ số", "Tổng số lượng", "Đơn giá", "Thành tiền"};
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
                vector.add(rs.getString("ProductName").trim());
                vector.add(rs.getString("UnitName").trim());
                vector.add(formatter.format(rs.getBigDecimal("Amount")));
                vector.add(rs.getInt("SoTam"));
                vector.add(formatter.format(rs.getBigDecimal("HeSo")));
                vector.add(formatter.format(rs.getBigDecimal("TongSoLuong")));
                vector.add(moneyDis(rs.getString("Price")));
                vector.add(moneyDis(rs.getString("IntoMoney")));
                modle.addRow(vector);
            }
            tableOrderDetail.setModel(modle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String moneyDis(String money) {
        DecimalFormat format = new DecimalFormat("###,###,###");
        String price = format.format(Double.parseDouble(money));
        return price;
    }

    private void updateTableDefault(String name) {
        DefaultTableModel model = (DefaultTableModel) tableOrderDetail.getModel();

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
                tableOrderDetail.setModel(model);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tableOrderDetail = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));
        setForeground(new java.awt.Color(102, 102, 255));
        setLocation(new java.awt.Point(200, 300));

        tableOrderDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableOrderDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableOrderDetail.setColumnSelectionAllowed(true);
        tableOrderDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableOrderDetail.setShowGrid(true);
        tableOrderDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableOrderDetailMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableOrderDetail);
        tableOrderDetail.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jButton1.setText("Thoát");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("In Lại");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Lưu hóa đơn");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(414, 414, 414)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(257, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableOrderDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableOrderDetailMouseClicked
        int click = tableOrderDetail.getSelectedRow();
        TableModel model = tableOrderDetail.getModel();
    }//GEN-LAST:event_tableOrderDetailMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            JasperReport report = JasperCompileManager.compileReport("D:\\NhomSatKiemDien\\src\\UserInterFace\\Bill.jrxml");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ORDER_ID", orderId);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);
            JasperViewer viewer = new JasperViewer(print, false);

            this.dispose();
            viewer.setVisible(true);
            viewer.toFront();
            viewer.requestFocus();

        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static boolean checkIfFileExists(String directory, String fileName) {
        // Tạo đối tượng File cho file PDF cần kiểm tra
        File file = new File(directory + File.separator + fileName + ".pdf");

        // Kiểm tra xem file có tồn tại không
        return file.exists();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            // Biên dịch mẫu báo cáo JasperReports
            JasperReport report = JasperCompileManager.compileReport("D:\\NhomSatKiemDien\\src\\UserInterFace\\Bill.jrxml");

            // Thay thế các tham số theo yêu cầu
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ORDER_ID", orderId);

            // Điền dữ liệu vào báo cáo
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);

//            // Hiển thị báo cáo trong JasperViewer
//            JasperViewer.viewReport(print, false);
            // Tạo đường dẫn và tên file PDF
            String directoryPath = "D:/Hóa Đơn"; // Đường dẫn thư mục lưu trữ
            String fileName = orderId + ".pdf"; // Tên file dựa trên mã hóa đơn
            String filePath = directoryPath + File.separator + fileName;

            // Đảm bảo thư mục tồn tại
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // Tạo thư mục nếu không tồn tại
            }

            // Kiểm tra xem file đã tồn tại chưa
            if (checkIfFileExists(directoryPath, fileName) == false) {
                // Xuất báo cáo JasperReports thành file PDF
                JasperExportManager.exportReportToPdfFile(print, filePath);

                // Thông báo thành công
                JOptionPane.showMessageDialog(null, "Hóa đơn đã được lưu tại: " + filePath);
            } else {
                int Click = JOptionPane.showConfirmDialog(null, "Hóa đơn nãy đã được lưu, bạn có muốn lưu lại không?", "Thông Báo", 2);
                if (Click == JOptionPane.YES_OPTION) {
                    // Xuất báo cáo JasperReports thành file PDF
                    JasperExportManager.exportReportToPdfFile(print, filePath);

                    // Thông báo thành công
                    JOptionPane.showMessageDialog(null, "Hóa đơn đã được lưu tại: " + filePath);
                }
            }

        } catch (JRException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tạo file PDF.");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(OrderDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OrderDetailDialog dialog = new OrderDetailDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableOrderDetail;
    // End of variables declaration//GEN-END:variables
}
