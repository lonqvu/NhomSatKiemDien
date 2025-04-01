package UserInterFace;

import static UserInterFace.Customers.detail;
import Utils.DatabaseUtil;
import java.awt.Color;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
//import org.jfree.chart.text.ItemLabelPosition;
//import org.jfree.chart.text.ItemLabelAnchor;
//import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.DecimalFormat;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ThongKe extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    static Detail detail;
    // Khai báo biến cho giao diện
    private javax.swing.JLabel lblSoDonHang;
    private javax.swing.JLabel lblGiaTriTB;
    private javax.swing.JTable tblTopSanPham;
    private javax.swing.JTable tblTopKhachHang;
    private DefaultTableModel modelTopSP;
    private DefaultTableModel modelTopKH;

    public ThongKe(Detail d) {
        initComponents();
        this.setLocationRelativeTo(null);
        detail = new Detail(d);
        // Khởi tạo model cho bảng
        modelTopSP = (DefaultTableModel) tblSanPham.getModel();
        modelTopKH = (DefaultTableModel) tblKhachHang.getModel();
        
        connection();
        loadDoanhThu();
        loadDonHang(); 
        loadKhachHang();
        loadBieuDo();
        loadImprove(); // Thêm hàm mới để tính % tăng trưởng
    }

   private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadImprove() {
        try {
            if (conn == null) return;
            
            // Tính % tăng trưởng theo ngày
            String sqlNgayTruoc = "SELECT SUM(IntoMoney) FROM Bill WHERE CAST(RegistDate AS DATE) = DATEADD(day, -1, CAST(GETDATE() AS DATE))";
            pst = conn.prepareStatement(sqlNgayTruoc);
            rs = pst.executeQuery();
            double doanhThuNgayTruoc = 0;
            if(rs != null && rs.next()) {
                doanhThuNgayTruoc = rs.getDouble(1);
            }
            
            String sqlNgay = "SELECT SUM(IntoMoney) FROM Bill WHERE CAST(RegistDate AS DATE) = CAST(GETDATE() AS DATE)";
            pst = conn.prepareStatement(sqlNgay);
            rs = pst.executeQuery();
            double doanhThuNgay = 0;
            if(rs != null && rs.next()) {
                doanhThuNgay = rs.getDouble(1);
            }
            
            double phanTramNgay = ((doanhThuNgay - doanhThuNgayTruoc) / doanhThuNgayTruoc) * 100;
            improveDay.setText(String.format("%.1f%%", phanTramNgay));
            improveDay.setForeground(phanTramNgay >= 0 ? new Color(0, 128, 0) : Color.RED);

            // Tính % tăng trưởng theo tuần
            String sqlTuanTruoc = "SELECT SUM(IntoMoney) FROM Bill WHERE RegistDate BETWEEN DATEADD(day, -14, GETDATE()) AND DATEADD(day, -7, GETDATE())";
            pst = conn.prepareStatement(sqlTuanTruoc);
            rs = pst.executeQuery();
            double doanhThuTuanTruoc = 0;
            if(rs != null && rs.next()) {
                doanhThuTuanTruoc = rs.getDouble(1);
            }
            
            String sqlTuan = "SELECT SUM(IntoMoney) FROM Bill WHERE RegistDate >= DATEADD(day, -7, GETDATE())";
            pst = conn.prepareStatement(sqlTuan);
            rs = pst.executeQuery();
            double doanhThuTuan = 0;
            if(rs != null && rs.next()) {
                doanhThuTuan = rs.getDouble(1);
            }
            
            double phanTramTuan = ((doanhThuTuan - doanhThuTuanTruoc) / doanhThuTuanTruoc) * 100;
            improveWeek.setText(String.format("%.1f%%", phanTramTuan));
            improveWeek.setForeground(phanTramTuan >= 0 ? new Color(0, 128, 0) : Color.RED);

            // Tính % tăng trưởng theo tháng
            String sqlThangTruoc = "SELECT SUM(IntoMoney) FROM Bill WHERE MONTH(RegistDate) = MONTH(DATEADD(MONTH, -1, GETDATE())) AND YEAR(RegistDate) = YEAR(DATEADD(MONTH, -1, GETDATE()))";
            pst = conn.prepareStatement(sqlThangTruoc);
            rs = pst.executeQuery();
            double doanhThuThangTruoc = 0;
            if(rs != null && rs.next()) {
                doanhThuThangTruoc = rs.getDouble(1);
            }
            
            String sqlThang = "SELECT SUM(IntoMoney) FROM Bill WHERE MONTH(RegistDate) = MONTH(GETDATE()) AND YEAR(RegistDate) = YEAR(GETDATE())";
            pst = conn.prepareStatement(sqlThang);
            rs = pst.executeQuery();
            double doanhThuThang = 0;
            if(rs != null && rs.next()) {
                doanhThuThang = rs.getDouble(1);
            }
            
            double phanTramThang = ((doanhThuThang - doanhThuThangTruoc) / doanhThuThangTruoc) * 100;
            improveMonth.setText(String.format("%.1f%%", phanTramThang));
            improveMonth.setForeground(phanTramThang >= 0 ? new Color(0, 128, 0) : Color.RED);

            // Tính % tăng trưởng theo năm
            String sqlNamTruoc = "SELECT SUM(IntoMoney) FROM Bill WHERE YEAR(RegistDate) = YEAR(DATEADD(YEAR, -1, GETDATE()))";
            pst = conn.prepareStatement(sqlNamTruoc);
            rs = pst.executeQuery();
            double doanhThuNamTruoc = 0;
            if(rs != null && rs.next()) {
                doanhThuNamTruoc = rs.getDouble(1);
            }
            
            String sqlNam = "SELECT SUM(IntoMoney) FROM Bill WHERE YEAR(RegistDate) = YEAR(GETDATE())";
            pst = conn.prepareStatement(sqlNam);
            rs = pst.executeQuery();
            double doanhThuNam = 0;
            if(rs != null && rs.next()) {
                doanhThuNam = rs.getDouble(1);
            }
            
            double phanTramNam = ((doanhThuNam - doanhThuNamTruoc) / doanhThuNamTruoc) * 100;
            improveYear.setText(String.format("%.1f%%", phanTramNam));
            improveYear.setForeground(phanTramNam >= 0 ? new Color(0, 128, 0) : Color.RED);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDoanhThu() {
        try {
            if (conn == null) return;
            
            // Doanh thu theo ngày
            String sqlNgay = "SELECT SUM(IntoMoney) FROM Bill WHERE CAST(RegistDate AS DATE) = CAST(GETDATE() AS DATE)";
            pst = conn.prepareStatement(sqlNgay);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblDoanhThuNgay != null) {
                lblDoanhThuNgay.setText(String.format("%,.0f VNĐ", rs.getDouble(1)));
            }

            // Doanh thu theo tuần  
            String sqlTuan = "SELECT SUM(IntoMoney) FROM Bill WHERE RegistDate >= DATEADD(day, -7, GETDATE())";
            pst = conn.prepareStatement(sqlTuan);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblDoanhThuTuan != null) {
                lblDoanhThuTuan.setText(String.format("%,.0f VNĐ", rs.getDouble(1)));
            }

            // Doanh thu theo tháng
            String sqlThang = "SELECT SUM(IntoMoney) FROM Bill WHERE MONTH(RegistDate) = MONTH(GETDATE()) AND YEAR(RegistDate) = YEAR(GETDATE())";
            pst = conn.prepareStatement(sqlThang);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblDoanhThuThang != null) {
                lblDoanhThuThang.setText(String.format("%,.0f VNĐ", rs.getDouble(1)));
            }

            // Doanh thu theo năm
            String sqlNam = "SELECT SUM(IntoMoney) FROM Bill WHERE YEAR(RegistDate) = YEAR(GETDATE())";
            pst = conn.prepareStatement(sqlNam);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblDoanhThuNam != null) {
                lblDoanhThuNam.setText(String.format("%,.0f VNĐ", rs.getDouble(1)));
            }

            // Top sản phẩm bán chạy
            if (modelTopSP != null) {
                modelTopSP.setRowCount(0); // Xóa dữ liệu cũ
                String sqlTopSP = "SELECT TOP 10 b.ProductID, b.ProductName, " +
                                "SUM(b.TongSoLuong) as SoLuong, " +
                                "SUM(b.IntoMoney) as DoanhThu " +
                                "FROM Bill b " + 
                                "GROUP BY b.ProductID, b.ProductName " +
                                "ORDER BY " + (filterProduct.getSelectedItem().toString().equals("Số lượng bán") ? 
                                             "SoLuong DESC" : "DoanhThu DESC");
                pst = conn.prepareStatement(sqlTopSP);
                rs = pst.executeQuery();
                while(rs != null && rs.next()) {
                    modelTopSP.addRow(new Object[]{
                        rs.getString("ProductID"),
                        rs.getString("ProductName"),
                        rs.getInt("SoLuong"),
                        String.format("%,.0f VNĐ", rs.getDouble("DoanhThu"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDonHang() {
        try {
            if (conn == null) return;
            
            // Số lượng đơn hàng
            String sqlDonHang = "SELECT COUNT(*) FROM Orders WHERE MONTH(Date) = MONTH(GETDATE())";
            pst = conn.prepareStatement(sqlDonHang);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblSoDonHang != null) {
                lblSoDonHang.setText(rs.getString(1));
            }

            // Giá trị đơn hàng trung bình
            String sqlTrungBinh = "SELECT AVG(TotalMoneyOrder) FROM Orders WHERE MONTH(Date) = MONTH(GETDATE())";
            pst = conn.prepareStatement(sqlTrungBinh);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblGiaTriTB != null) {
                lblGiaTriTB.setText(String.format("%,.0f VNĐ", rs.getDouble(1)));
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    private void loadKhachHang() {
        try {
            if (conn == null) return;
            
            // Số khách hàng mới
            String sqlKHMoi = "SELECT COUNT(*) FROM Customer WHERE MONTH(RegistDate) = MONTH(GETDATE())";
            pst = conn.prepareStatement(sqlKHMoi);
            rs = pst.executeQuery();
            if(rs != null && rs.next() && lblKhachHangMoi != null) {
                lblKhachHangMoi.setText(rs.getString(1));
            }

            // Tính tỷ lệ khách hàng quay lại
            String sqlQuayLai = "SELECT COUNT(DISTINCT o.CustomerID) as SoKHQuayLai " +
                               "FROM Orders o " +
                               "WHERE o.CustomerID IN (SELECT CustomerID FROM Orders GROUP BY CustomerID HAVING COUNT(*) > 1)";
            pst = conn.prepareStatement(sqlQuayLai);
            rs = pst.executeQuery();
            if(rs != null && rs.next()) {
                int soKHQuayLai = rs.getInt("SoKHQuayLai");
                String sqlTongKH = "SELECT COUNT(*) FROM Customer";
                pst = conn.prepareStatement(sqlTongKH);
                rs = pst.executeQuery();
                if(rs != null && rs.next()) {
                    int tongKH = rs.getInt(1);
                    double tyLe = (double)soKHQuayLai / tongKH * 100;
                    lblKhachHangQuayLai.setText(String.format("%.1f%%", tyLe));
                }
            }

            // Top khách hàng thân thiết
            if (modelTopKH != null) {
                modelTopKH.setRowCount(0); // Xóa dữ liệu cũ
                String sqlTopKH = "";
                
                // Xác định câu SQL dựa trên lựa chọn filterBy
                if(filterBy.getSelectedItem().toString().equals("Số đơn hàng")) {
                    sqlTopKH = "SELECT TOP 10 c.ID, c.CustomerName, COUNT(o.ID) as SoDonHang, " +
                              "SUM(o.TotalMoneyOrder) as TongTien " +
                              "FROM Customer c " +
                              "LEFT JOIN Orders o ON c.ID = o.CustomerID " +
                              "GROUP BY c.ID, c.CustomerName " +
                              "ORDER BY SoDonHang DESC";
                } else {
                    sqlTopKH = "SELECT TOP 10 c.ID, c.CustomerName, COUNT(o.ID) as SoDonHang, " +
                              "SUM(o.TotalMoneyOrder) as TongTien " +
                              "FROM Customer c " +
                              "LEFT JOIN Orders o ON c.ID = o.CustomerID " +
                              "GROUP BY c.ID, c.CustomerName " +
                              "ORDER BY TongTien DESC";
                }
                
                pst = conn.prepareStatement(sqlTopKH);
                rs = pst.executeQuery();
                while(rs != null && rs.next()) {
                    modelTopKH.addRow(new Object[]{
                        rs.getString("ID"),
                        rs.getString("CustomerName"),
                        rs.getInt("SoDonHang"),
                        String.format("%,.0f VNĐ", rs.getDouble("TongTien"))
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBieuDo() {
        try {
            if (conn == null) return;
            
            // Biểu đồ doanh thu theo tháng
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Khởi tạo giá trị 0 cho tất cả các tháng
            for (int i = 1; i <= 12; i++) {
                dataset.addValue(0, "Doanh thu", "Tháng " + i);
            }
            
            String sqlDoanhThu = "SELECT MONTH(RegistDate) as Thang, SUM(IntoMoney) as DoanhThu " +
                                "FROM Bill WHERE YEAR(RegistDate) = YEAR(GETDATE()) " +
                                "GROUP BY MONTH(RegistDate) " +
                                "ORDER BY Thang";
            pst = conn.prepareStatement(sqlDoanhThu);
            rs = pst.executeQuery();
            while(rs != null && rs.next()) {
                dataset.setValue(rs.getDouble("DoanhThu"), "Doanh thu", "Tháng " + rs.getInt("Thang"));
            }

            JFreeChart chart = ChartFactory.createBarChart(
                "Biểu đồ doanh thu theo tháng",
                "Tháng", 
                "Doanh thu (VNĐ)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Tùy chỉnh giao diện biểu đồ
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setOutlinePaint(null);
            
            // Thêm biểu đồ vào panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(745, 277));
            panelChart1.setLayout(new BorderLayout());
            panelChart1.removeAll();
            panelChart1.add(chartPanel, BorderLayout.CENTER);
            panelChart1.revalidate();
            panelChart1.repaint();

            // Biểu đồ sản phẩm theo danh mục
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            String sqlSanPham = "SELECT c.Classify, COUNT(DISTINCT p.ID) as SoLoaiSP, " +
                              "SUM(b.TongSoLuong) as TongSoLuong, " +
                              "SUM(b.IntoMoney) as TongDoanhThu " +
                              "FROM Classify c " +
                              "LEFT JOIN Products p ON c.ID = p.ClassifyID " +
                              "LEFT JOIN Bill b ON p.ID = b.ProductID " +
                              "GROUP BY c.Classify";
            pst = conn.prepareStatement(sqlSanPham);
            rs = pst.executeQuery();
            while(rs != null && rs.next()) {
                String classify = rs.getString("Classify");
                int soLoaiSP = rs.getInt("SoLoaiSP");
                int tongSoLuong = rs.getInt("TongSoLuong");
                double tongDoanhThu = rs.getDouble("TongDoanhThu");
                // Thêm cả số loại SP và tổng số lượng vào tên danh mục
                DecimalFormat formatter = new DecimalFormat("#,###");
                String tongDoanhThuFormatted = formatter.format(tongDoanhThu);
                pieDataset.setValue(classify + " (" + soLoaiSP + " loại, " + tongSoLuong + " SP, " + tongDoanhThuFormatted + " VNĐ)", tongDoanhThu);
            }

            JFreeChart pieChart = ChartFactory.createPieChart(
                "Phân bố sản phẩm theo danh mục", 
                pieDataset,
                true, true, false
            );

            // Tùy chỉnh giao diện biểu đồ tròn
            PiePlot piePlot = (PiePlot) pieChart.getPlot();
            piePlot.setBackgroundPaint(Color.WHITE);
            piePlot.setOutlinePaint(null);
            
            // Tùy chỉnh hiển thị label
            piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} VNĐ ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0%")
            ));
            
            // Thêm biểu đồ tròn vào panel
            ChartPanel pieChartPanel = new ChartPanel(pieChart);
            pieChartPanel.setPreferredSize(new java.awt.Dimension(745, 259));
            panelChart2.setLayout(new BorderLayout());
            panelChart2.removeAll();
            panelChart2.add(pieChartPanel, BorderLayout.CENTER);
            panelChart2.revalidate();
            panelChart2.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblDoanhThuNgay = new javax.swing.JLabel();
        lblDoanhThuTuan = new javax.swing.JLabel();
        lblDoanhThuThang = new javax.swing.JLabel();
        lblDoanhThuNam = new javax.swing.JLabel();
        improveDay = new javax.swing.JLabel();
        improveWeek = new javax.swing.JLabel();
        improveMonth = new javax.swing.JLabel();
        improveYear = new javax.swing.JLabel();
        panelChart1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblKhachHangMoi = new javax.swing.JLabel();
        lblKhachHangQuayLai = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKhachHang = new javax.swing.JTable();
        filterBy = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();
        panelChart2 = new javax.swing.JPanel();
        filterProduct = new javax.swing.JComboBox<>();
        btnBackHome = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        startDate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        startDate1 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Thống Kê");

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin thống kê cơ bản"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Thống kê doanh thu"));

        jLabel1.setText("Doanh thu ngày");

        jLabel2.setText("Doanh thu tuần");

        jLabel3.setText("Doanh thu tháng");

        jLabel4.setText("Doanh thu năm");

        lblDoanhThuNgay.setText("0");

        lblDoanhThuTuan.setText("0");

        lblDoanhThuThang.setText("0");

        lblDoanhThuNam.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblDoanhThuNam, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(lblDoanhThuThang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDoanhThuTuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDoanhThuNgay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(improveDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(improveWeek, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(improveMonth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(improveYear, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblDoanhThuNgay)
                    .addComponent(improveDay))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblDoanhThuTuan)
                    .addComponent(improveWeek))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblDoanhThuThang)
                    .addComponent(improveMonth))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblDoanhThuNam)
                    .addComponent(improveYear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelChart1.setBorder(javax.swing.BorderFactory.createTitledBorder("Biểu đồ doanh thu"));

        javax.swing.GroupLayout panelChart1Layout = new javax.swing.GroupLayout(panelChart1);
        panelChart1.setLayout(panelChart1Layout);
        panelChart1Layout.setHorizontalGroup(
            panelChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1012, Short.MAX_VALUE)
        );
        panelChart1Layout.setVerticalGroup(
            panelChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ngày" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelChart1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(panelChart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Doanh thu", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin khách hàng"));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Thống kê khách hàng"));

        jLabel5.setText("Số khách hàng mới");

        jLabel6.setText("Tỷ lệ khách hàng quay lại");

        lblKhachHangMoi.setText("0");

        lblKhachHangQuayLai.setText("0%");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(30, 30, 30)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblKhachHangMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(lblKhachHangQuayLai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblKhachHangMoi))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblKhachHangQuayLai))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Top khách hàng thân thiết"));

        tblKhachHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã KH", "Tên khách hàng", "Số đơn hàng", "Tổng tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblKhachHang);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
        );

        filterBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Số đơn hàng", "Tổng tiền" }));
        filterBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterByActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(filterBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Khách hàng", jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Thống kê sản phẩm"));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Top sản phẩm bán chạy"));

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã SP", "Tên sản phẩm", "Số lượng bán", "Doanh thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblSanPham);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        panelChart2.setBorder(javax.swing.BorderFactory.createTitledBorder("Biểu đồ phân tích"));

        javax.swing.GroupLayout panelChart2Layout = new javax.swing.GroupLayout(panelChart2);
        panelChart2.setLayout(panelChart2Layout);
        panelChart2Layout.setHorizontalGroup(
            panelChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1012, Short.MAX_VALUE)
        );
        panelChart2Layout.setVerticalGroup(
            panelChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        filterProduct.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Số lượng bán", "Doanh thu" }));
        filterProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelChart2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(filterProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(filterProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelChart2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sản phẩm", jPanel3);

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

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Thống Kê");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Thời gian");

        startDate.setDateFormatString("dd/mm/yyyy");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("-");

        startDate1.setDateFormatString("dd/mm/yyyy");

        jButton1.setText("Tìm");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 64, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBackHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
                        .addGap(171, 171, 171))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(startDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jButton1))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(startDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackHomeMouseClicked
        backHome();
    }//GEN-LAST:event_btnBackHomeMouseClicked

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackHomeActionPerformed

    private void filterByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByActionPerformed
        loadKhachHang();
    }//GEN-LAST:event_filterByActionPerformed

    private void filterProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterProductActionPerformed
        loadDoanhThu();
    }//GEN-LAST:event_filterProductActionPerformed

       private void backHome() {
        Home home = new Home(detail);
        this.setVisible(false);
        home.setVisible(true);
    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ThongKe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ThongKe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ThongKe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ThongKe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ThongKe(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackHome;
    private javax.swing.JComboBox<String> filterBy;
    private javax.swing.JComboBox<String> filterProduct;
    private javax.swing.JLabel improveDay;
    private javax.swing.JLabel improveMonth;
    private javax.swing.JLabel improveWeek;
    private javax.swing.JLabel improveYear;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDoanhThuNam;
    private javax.swing.JLabel lblDoanhThuNgay;
    private javax.swing.JLabel lblDoanhThuThang;
    private javax.swing.JLabel lblDoanhThuTuan;
    private javax.swing.JLabel lblKhachHangMoi;
    private javax.swing.JLabel lblKhachHangQuayLai;
    private javax.swing.JPanel panelChart1;
    private javax.swing.JPanel panelChart2;
    private com.toedter.calendar.JDateChooser startDate;
    private com.toedter.calendar.JDateChooser startDate1;
    private javax.swing.JTable tblKhachHang;
    private javax.swing.JTable tblSanPham;
    // End of variables declaration//GEN-END:variables
}
