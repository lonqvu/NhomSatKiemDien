/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterFace;

import Utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class ThanhToanNo extends javax.swing.JDialog {
    private String customerID;
    private Detail detail;
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private double soTienNo = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    /**
     * Creates new form ThanhToanNo
     */
    public ThanhToanNo(String customerID, Detail detail) {
        initComponents();
        this.customerID = customerID;
        this.detail = detail;
        connection();
        loadCustomerData();
        setLocationRelativeTo(null);
    }

    public ThanhToanNo(javax.swing.JDialog parent, boolean modal, String customerID, Detail detail) {
        super(parent, "Thanh Toán Nợ", modal);
        initComponents();
        this.customerID = customerID;
        this.detail = detail;
        connection();
        loadCustomerData();
        setLocationRelativeTo(parent);
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCustomerData() {
        try {
            if (conn == null) return;
            
            // In ra ID và kiểm tra kết nối
            System.out.println("ID khách hàng: " + customerID);
            System.out.println("Trạng thái kết nối: " + (conn != null ? "Đã kết nối" : "Chưa kết nối"));
            
            // Lấy thông tin khách hàng
            String sql = "SELECT * FROM Customer WHERE ID = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(customerID));
            rs = pst.executeQuery();
            
            System.out.println("Đã thực hiện truy vấn");
            
            if (rs.next()) {
                // In ra các giá trị từ ResultSet để debug
                System.out.println("Tên KH: " + rs.getString("CustomerName"));
                System.out.println("SĐT: " + rs.getString("PhoneNumber"));
                System.out.println("Nợ: " + rs.getDouble("Debt"));
                
                // Đảm bảo các biến không null trước khi sử dụng
                if (txtTenKH != null) txtTenKH.setText(rs.getString("CustomerName"));
                if (txtSDT != null) txtSDT.setText(rs.getString("PhoneNumber"));
                
                soTienNo = rs.getDouble("Debt");
                
                if (txtSoTienNo != null) txtSoTienNo.setText(formatter.format(soTienNo) + " VNĐ");
                if (txtSoTienConLai != null) txtSoTienConLai.setText(formatter.format(soTienNo) + " VNĐ");
            } else {
                System.out.println("Không tìm thấy khách hàng với ID: " + customerID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void tinhSoTienConLai() {
        try {
            // Đảm bảo xử lý đúng định dạng số tiền
            String soTienThanhToanText = txtSoTienThanhToan.getText().replaceAll("[,\\s]", "").replace("VNĐ", "");
            if (soTienThanhToanText.isEmpty()) {
                txtSoTienConLai.setText(formatter.format(soTienNo) + " VNĐ");
                return;
            }
            
            double soTienThanhToan = Double.parseDouble(soTienThanhToanText);
            if (soTienThanhToan > soTienNo) {
                JOptionPane.showMessageDialog(this, "Số tiền thanh toán không được lớn hơn số tiền nợ!");
                txtSoTienThanhToan.setText("");
                txtSoTienConLai.setText(formatter.format(soTienNo) + " VNĐ");
                return;
            }
            double soTienConLai = soTienNo - soTienThanhToan;
            txtSoTienConLai.setText(formatter.format(soTienConLai) + " VNĐ");
            
            // Format lại số tiền thanh toán để dễ đọc
            txtSoTienThanhToan.setText(formatter.format(soTienThanhToan));
        } catch (NumberFormatException e) {
            // Nếu không phải số, giữ nguyên giá trị cũ
            if (!txtSoTienThanhToan.getText().isEmpty()) {
                txtSoTienConLai.setText(formatter.format(soTienNo) + " VNĐ");
            }
        }
    }

    private void luuThanhToan() {
        try {
            if (conn == null) return;
            
            // Lấy giá trị số tiền thanh toán từ trường nhập liệu
            String soTienThanhToanText = txtSoTienThanhToan.getText().replaceAll("[,\\s]", "").replace("VNĐ", "");
            if (soTienThanhToanText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền thanh toán");
                return;
            }
            
            double soTienThanhToan = Double.parseDouble(soTienThanhToanText);
            double soTienConLai = soTienNo - soTienThanhToan;
            
            if(soTienConLai <= 0){
                soTienConLai = 0;
            }
            
            // Cập nhật số tiền nợ trong bảng Customer
            String sqlUpdateDebt = "UPDATE Customer SET Debt = ?, OldDebt = ? WHERE ID = ?";
            pst = conn.prepareStatement(sqlUpdateDebt);
            pst.setDouble(1, soTienConLai);
            pst.setDouble(2, soTienNo); // Lưu nợ cũ là số tiền nợ trước khi thanh toán
            pst.setString(3, customerID);
            pst.executeUpdate();
            
            // Thêm lịch sử thanh toán với số tiền còn nợ
            String sqlInsertHistory = "INSERT INTO PaymentHistory (CustomerID, Amount, PaymentDate, RemainingDebt, OldDebt, Deleted) VALUES (?, ?, GETDATE(), ?, ?, ?)";
            pst = conn.prepareStatement(sqlInsertHistory);
            pst.setString(1, customerID);
            pst.setDouble(2, soTienThanhToan);
            pst.setDouble(3, soTienConLai);
            pst.setDouble(4, soTienNo);
            // Nếu nợ = 0 thì đánh dấu là đã xóa
            pst.setInt(5, soTienConLai == 0 ? 1 : 0);
            pst.executeUpdate();
            
            // Nếu nợ = 0, cập nhật tất cả bản ghi PaymentHistory của khách hàng thành Deleted = 1
            if (soTienConLai == 0) {
                String sqlUpdateAllHistory = "UPDATE PaymentHistory SET Deleted = 1 WHERE CustomerID = ?";
                pst = conn.prepareStatement(sqlUpdateAllHistory);
                pst.setString(1, customerID);
                pst.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Lưu thanh toán thành công!");
            
            // Đóng form hiện tại
            this.dispose();
            
            // Load lại dữ liệu ở màn hình Customers
            Customers customersScreen = Customers.getInstance();
            if (customersScreen != null) {
                customersScreen.loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thanh toán: " + e.getMessage());
        }
    }

    private void xuatHoaDon() {
        try {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không có kết nối đến cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Kiểm tra xem có bản ghi nào của customerID có deleted = 0 không
            String checkSql = "SELECT COUNT(*) FROM PaymentHistory WHERE CustomerID = ? AND Deleted = 0";
            pst = conn.prepareStatement(checkSql);
            pst.setString(1, customerID);
            rs = pst.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Không có bản ghi thanh toán nào để xuất hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Lấy giá trị số tiền thanh toán từ trường nhập liệu
            String soTienThanhToanText = txtSoTienThanhToan.getText().replaceAll("[,\\s]", "").replace("VNĐ", "");
            double soTienThanhToan = 0;
            if (!soTienThanhToanText.isEmpty()) {
                soTienThanhToan = Double.parseDouble(soTienThanhToanText);
            }
            
            // Biên dịch mẫu báo cáo JasperReports
            String reportPath = "src/UserInterFace/ThanhToanNoReport.jrxml";
            JasperReport report = JasperCompileManager.compileReport(reportPath);
            
            // Thay thế các tham số theo yêu cầu
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CustomerID", customerID);
            parameters.put("PaymentAmount", soTienThanhToan);
            
            // Điền dữ liệu vào báo cáo
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);
            
            // Hiển thị báo cáo trong JasperViewer
            JasperViewer.viewReport(print, false);
            
            // Tạo và lưu file PDF
            String directoryPath = "D:/HoaDon";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = "ThanhToanNo_" + customerID + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            String filePath = directoryPath + File.separator + fileName;
            
            JasperExportManager.exportReportToPdfFile(print, filePath);
            
            JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn thành công!\nĐã lưu tại: " + filePath, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTenKH = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        txtSoTienNo = new javax.swing.JTextField();
        txtSoTienThanhToan = new javax.swing.JTextField();
        txtSoTienConLai = new javax.swing.JTextField();
        btnLuu = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();

        setTitle("Thanh Toán Nợ");

        jLabel1.setText("Tên Khách Hàng:");

        jLabel2.setText("Số điện thoại:");

        jLabel3.setText("Số tiền còn nợ:");

        jLabel4.setText("Số tiền thanh toán:");

        jLabel5.setText("Số tiền còn lại:");

        txtTenKH.setEnabled(false);

        txtSDT.setEnabled(false);

        txtSoTienNo.setEnabled(false);

        txtSoTienConLai.setEnabled(false);

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnXuat.setText("Xuất");
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });

        btnDong.setText("Đóng");
        btnDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDongActionPerformed(evt);
            }
        });

        txtSoTienThanhToan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSoTienThanhToanKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTenKH, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtSDT)
                            .addComponent(txtSoTienNo)
                            .addComponent(txtSoTienThanhToan)
                            .addComponent(txtSoTienConLai)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSoTienNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSoTienThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtSoTienConLai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLuu)
                    .addComponent(btnLamMoi)
                    .addComponent(btnXuat)
                    .addComponent(btnDong))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn lưu thanh toán này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            luuThanhToan();
        }
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        txtSoTienThanhToan.setText("");
        loadCustomerData();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        xuatHoaDon();
    }//GEN-LAST:event_btnXuatActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void txtSoTienThanhToanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSoTienThanhToanKeyReleased
        tinhSoTienConLai();
    }//GEN-LAST:event_txtSoTienThanhToanKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnXuat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSoTienConLai;
    private javax.swing.JTextField txtSoTienNo;
    private javax.swing.JTextField txtSoTienThanhToan;
    private javax.swing.JTextField txtTenKH;
    // End of variables declaration//GEN-END:variables
}
