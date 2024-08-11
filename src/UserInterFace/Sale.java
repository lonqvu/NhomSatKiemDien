package UserInterFace;

import java.awt.Color;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;
import Entity.Product;
import Entity.Classify;
import Entity.Customer;
import Entity.Unit;
import Utils.DatabaseUtil;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sun.tools.javac.util.ArrayUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import net.sf.jasperreports.engine.util.JRProperties;

class Sale extends javax.swing.JFrame implements Runnable {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private String sql = "SELECT * FROM Bill";

    private Thread thread;
    private Detail detail;

    private String MaHD = "";
    private String customerId = "";

    private String[] khachHang;

    public Sale() {
        // Constructor mặc định (có thể thêm các hành động khởi tạo tại đây)
    }

    public Sale(Detail d) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail = new Detail(d);
        connection();
        Start();
        checkBill();
//        if (this.detail.getRole() == 1) {
//            btnCustomer.setEnabled(true);
//        }

//        LoadClassify();
//        List<Classify> classifies = fetchClassify();
//
//        // Lấy JTextField từ comboBox để thêm DocumentFilter
//        textField = (JTextField) cbxClassify.getEditor().getEditorComponent();
//        PlainDocument doc = (PlainDocument) textField.getDocument();
//        doc.setDocumentFilter(new ComboBoxFilter(cbxClassify, classifies, textField));
//        cbxClassify.setRenderer(new DefaultListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                if (value != null) {
//                    setText(value.toString());
//                }
//                return this;
//            }
//        });
    }

    private void loadCustomer(String customerId) {
        if (customerId.isBlank() == false || customerId.isEmpty() == false) {

            String sql = "Select * from Customer where ID = ?";
            try {
                pst = conn.prepareStatement(sql);
                pst.setString(1, customerId);
                rs = pst.executeQuery();
                while (rs.next()) {
                    txbKhachHang.setText(rs.getString("CustomerName"));
                    txbDiaChi.setText(rs.getString("Address"));
                    txbSoDienThoai.setText(rs.getString("PhoneNumber"));
                    txbDebt.setText(moneyDis(rs.getString("Debt")));
                }
                lblTong.setText(moneyDis((convertToMoney(lbltotalMoney.getText()).add(convertToMoney(lblNoCu.getText())).toString())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Pays() {
        lbltotalMoney.setText("0");
        String sqlPay = "SELECT * FROM Bill where OrderID = ?";
        try {
            pst = conn.prepareStatement(sqlPay);
            pst.setString(1, MaHD);
            rs = pst.executeQuery();
            while (rs.next()) {
                String s1 = rs.getString("IntoMoney").toString();
                String s2 = lbltotalMoney.getText();
                BigDecimal totalMoney = convertToMoney(s1).add(convertToMoney(s2));

                lbltotalMoney.setText(moneyDis(totalMoney.toString()));
            }

            if (lbltotalMoney.equals("0") == false) {
                btnPay.setEnabled(true);
                lblTong.setText(moneyDis(convertToMoney(lbltotalMoney.getText()).add(convertToMoney(lblNoCu.getText())).toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private void Refresh() {
        btnDelete.setEnabled(false);
        btnPrint.setEnabled(false);
        btnNew.setEnabled(true);
        btnAdd.setEnabled(false);
        btnPay.setEnabled(false);
        btnSavePdf.setEnabled(false);
        lbltotalMoney.setText("0");
        lblNoCu.setText("0");
        lblTong.setText("0");
        lblMaHoaDon.setText("");
        lblNgayLapDon.setText("");
        lblNguoiLapDon.setText("");
        btnDeleteOrder.setEnabled(false);
        txbKhachHang.setText("");
        txbDiaChi.setText("");
        txbSoDienThoai.setText("");
        txbDebt.setText("");
        MaHD = "";
        customerId = "";

        tableBill.removeAll();
    }

    private void refreshCustomer() {
        txbKhachHang.setText("");
        txbDiaChi.setText("");
        txbDebt.setText("");
        txbSoDienThoai.setText("");
    }

    private void refreshOrderDetail() {
        lblTong.setText("0");
        lbltotalMoney.setText("0");
        lblNoCu.setText("0");
        lblKhachThanhToan.setText("0");
        lblConLai.setText("0");
    }

    private void deleteOrder() {
        String sqlDelete = "DELETE FROM Orders where id = ?";
        String sqlDeleteBill = "DELETE FROM Bill where OrderID = ?";
        MaHD = "";
        try {
            pst = conn.prepareStatement(sqlDeleteBill);
            pst.setString(1, MaHD);
            pst.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            pst = conn.prepareStatement(sqlDelete);
            pst.setString(1, MaHD);
            pst.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkBill() {
        if (tableBill.getRowCount() == 0) {
            lbltotalMoney.setText("0");
            lblTong.setText(lblNoCu.getText());
        }
    }

    private void Sucessful() {
        btnAdd.setEnabled(true);
        btnDelete.setEnabled(false);
    }

    private void changeProduct() {
        int Click = tableBill.getSelectedRow();
        TableModel model = tableBill.getModel();

        String sqlChange = "UPDATE Bill SET Amount=?, IntoMoney=? WHERE ID='" + model.getValueAt(Click, 6).toString().trim() + "'";
        try {
//            double amount = Double.parseDouble(txbAmount.getText());
//            pst = conn.prepareStatement(sqlChange);
//            pst.setBigDecimal(1, BigDecimal.valueOf(amount));
//            pst.setBigDecimal(2, convertToMoney(txbIntoMoney.getText()));
//            pst.executeUpdate();
//            Disabled();
//            Sucessful();
//            lblStatus.setText("Lưu thay đổi thành công!");
//            Load();
            btnPay.setEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void getCustomer() {
//        String sql = "SELECT * FROM Customer where ID=?";
//        Customer customer = (Customer) cbxCustomer.getSelectedItem();
//        try {
//            pst = conn.prepareStatement(sql);
//            pst.setInt(1, customer.getId());
//            rs = pst.executeQuery();
//            while (rs.next()) {
////                txbCustomerName.setText(rs.getString("CustomerName").trim());
//                txbPhoneNumber.setText(rs.getString("PhoneNumber").trim());
//                txbAddress.setText(rs.getString("Address").trim());
//                txbDebt.setText(moneyDis(rs.getString("Debt")));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void Load() {
        DecimalFormat formatter = new DecimalFormat("#.###");
        DefaultTableModel modelClear = (DefaultTableModel) tableBill.getModel();
        modelClear.setRowCount(0);
        BigDecimal money = BigDecimal.ZERO;
        String sql = "SELECT Bill.*  FROM Bill WHERE OrderID = ?";
        if (MaHD != "") {
            // Sử dụng try-with-resources để tự động đóng PreparedStatement và ResultSet
            try (
                     PreparedStatement pst = conn.prepareStatement(sql);) {
                String[] arr = {"Mã Sản Phẩm", "Tên Sản Phẩm", "Đơn Vị", "Số Lượng/ Mét", "Số Tấm", "Hệ Số", "SL/ M2", "Đơn Giá", "Thành Tiền", "ID Bill"};
                DefaultTableModel model = new DefaultTableModel(arr, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        // Làm cho toàn bộ bảng không thể chỉnh sửa
                        return false;
                    }
                };

                pst.setString(1, MaHD);
                try ( ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String intoMoney = rs.getString("IntoMoney");
                        BigDecimal amount = rs.getBigDecimal("Amount");
                        String productID = rs.getString("ProductID");
                        int soTam = rs.getInt("SoTam");
                        BigDecimal heSo = rs.getBigDecimal("HeSo");
                        BigDecimal tongSl = rs.getBigDecimal("TongSoLuong");

                        Vector<Object> vector = new Vector<>();

                        vector.add(productID);
                        Product productEntity = getProductById(productID);
                        vector.add(productEntity.getProductName());
                        vector.add(rs.getString("UnitName"));
                        vector.add(formatter.format(amount));
                        vector.add(soTam);
                        vector.add(formatter.format(heSo));
                        vector.add(formatter.format(tongSl));
                        vector.add(moneyDis(rs.getString("Price")));

                        if (intoMoney != null) {
                            vector.add(moneyDis(intoMoney));
                        } else {
                            vector.add("");
                        }

                        vector.add(rs.getString("ID"));
                        model.addRow(vector);
                    }
                }
                tableBill.setCellSelectionEnabled(true);
                tableBill.setRowSelectionAllowed(true);
                tableBill.setColumnSelectionAllowed(true);

                tableBill.setModel(model);
                TableColumn tableColumn = tableBill.getColumnModel().getColumn(0);
                tableBill.getColumnModel().removeColumn(tableColumn);
                TableColumn tableColumn9 = tableBill.getColumnModel().getColumn(8);
                tableBill.getColumnModel().removeColumn(tableColumn9);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Product getProductById(String id) {
        String sql = "select * from Products where id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                return new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitName"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private void consistency() {
        String sqlBill = "SELECT * FROM Bill";
        try {

            PreparedStatement pstBill = conn.prepareStatement(sqlBill);
            ResultSet rsBill = pstBill.executeQuery();

            while (rsBill.next()) {

                try {
                    String sqlTemp = "SELECT * FROM Products WHERE ID ='" + rsBill.getString("Code") + "'";
                    PreparedStatement pstTemp = conn.prepareStatement(sqlTemp);
                    ResultSet rsTemp = pstTemp.executeQuery();

                    if (rsTemp.next()) {

                        String sqlUpdate = "UPDATE Products SET QuantityRemaining=? WHERE ID='" + rsBill.getString("Code").trim() + "'";
                        try {
                            pst = conn.prepareStatement(sqlUpdate);
                            pst.setInt(1, rsTemp.getInt("QuantityRemaining") - rsBill.getInt("Amount"));
                            pst.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

//    private void loadPriceandClassify(String s) {
//
//        String sql = "SELECT * FROM Products WHERE ID = ?";
//        try (
//                 PreparedStatement pst = conn.prepareStatement(sql)) {
//            pst.setString(1, s.trim());
//            try ( ResultSet rs = pst.executeQuery()) {
//                if (rs.next()) { // Sử dụng if thay vì while vì chỉ mong đợi một kết quả
//                    String price = rs.getString("Price");
//                    String classifyID = rs.getString("ClassifyID").trim();
//
//                    // Cập nhật giao diện người dùng
//                    Classify classify = getClassifyById(classifyID);
//                    if (classify != null) {
//                        this.cbxClassify.setSelectedItem(classify);
//                    }
//                    this.txbPrice.setText(moneyDis(price.trim()));
//                    Product product = new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitID"));
//                    this.cbxProduct.addItem(product);
//                    this.cbxProduct.setSelectedItem(product);
//                    Unit unit = getUnitById(rs.getString("UnitID"));
//                    if (unit != null) {
//                        this.cbxUnit.setSelectedItem(unit);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    private Classify getClassifyById(String id) {
        Classify classify = null;
        String sql = "SELECT * FROM Classify WHERE ID = ?";
        try (
                 PreparedStatement pst = conn.prepareStatement(sql)) {
            // Đặt giá trị cho tham số
            pst.setString(1, id);

            try ( ResultSet rs = pst.executeQuery()) {
                if (rs.next()) { // Sử dụng if thay vì while vì chỉ mong đợi một kết quả
                    classify = new Classify(rs.getString("ID"), rs.getString("Classify"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classify;
    }

    private Product getProductObjectById(String id) {
        Product product = null;
        String sql = "select * from Products where id = ?";
        try (
                 PreparedStatement pst = conn.prepareStatement(sql)) {
            // Đặt giá trị cho tham số
            pst.setString(1, id);

            try ( ResultSet rs = pst.executeQuery()) {
                if (rs.next()) { // Sử dụng if thay vì while vì chỉ mong đợi một kết quả
                    product = new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableBill = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnPay = new javax.swing.JButton();
        btnSavePdf = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txbKhachHang = new javax.swing.JLabel();
        txbSoDienThoai = new javax.swing.JLabel();
        txbDiaChi = new javax.swing.JLabel();
        txbDebt = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbltotalMoney = new javax.swing.JLabel();
        lblNoCu = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblTong = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblKhachThanhToan = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblConLai = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        lblMaHoaDon = new javax.swing.JLabel();
        lblNguoiLapDon = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblNgayLapDon = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnDeleteOrder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableBill.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableBill.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng", "Đơn Vị", "Đơn Giá", "Thành Tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableBill.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableBillMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableBill);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Add.png"))); // NOI18N
        btnAdd.setText("Thêm");
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Delete.png"))); // NOI18N
        btnDelete.setText("Xóa Sản Phẩm");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        btnPrint.setText("Xuất HĐ");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Pay.png"))); // NOI18N
        btnPay.setText("Thanh Toán");
        btnPay.setEnabled(false);
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayActionPerformed(evt);
            }
        });

        btnSavePdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        btnSavePdf.setText("Lưu HĐ");
        btnSavePdf.setEnabled(false);
        btnSavePdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavePdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSavePdf, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSavePdf, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Bán Hàng");

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

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setEnabled(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setText("Khách Hàng:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel12.setText("Địa chỉ:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel13.setText("SĐT:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel16.setText("Nợ cũ: ");

        txbKhachHang.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txbKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txbKhachHangMouseClicked(evt);
            }
        });

        txbSoDienThoai.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txbSoDienThoai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txbSoDienThoaiMouseClicked(evt);
            }
        });

        txbDiaChi.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txbDiaChi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txbDiaChiMouseClicked(evt);
            }
        });

        txbDebt.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txbDebt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txbDebtMouseClicked(evt);
            }
        });

        jButton1.setText("Chọn");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel12))
                        .addGap(53, 53, 53)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txbDiaChi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txbDebt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txbKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(75, 75, 75)
                        .addComponent(txbSoDienThoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txbDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txbDebt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setEnabled(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Còn lại:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setText("Nợ cũ:");

        lbltotalMoney.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbltotalMoney.setText("0");

        lblNoCu.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblNoCu.setText("0");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel17.setText("Tổng:");

        lblTong.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblTong.setText("0");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel14.setText("Tổng tiền hóa đơn:");

        lblKhachThanhToan.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblKhachThanhToan.setText("0");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setText("Khách thanh toán:");

        lblConLai.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblConLai.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConLai, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(lbltotalMoney, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(lblNoCu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblKhachThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltotalMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNoCu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTong, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKhachThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblConLai, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setEnabled(false);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel18.setText("Người lập HĐ:");

        lblMaHoaDon.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        lblNguoiLapDon.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel19.setText("Ngày lập HĐ: ");

        lblNgayLapDon.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel20.setText("Mã hóa đơn:");

        btnNew.setText("Hóa Đơn Mới");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnDeleteOrder.setText("Hủy hóa đơn");
        btnDeleteOrder.setEnabled(false);
        btnDeleteOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteOrderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                        .addComponent(btnDeleteOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNgayLapDon, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(lblMaHoaDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNguoiLapDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMaHoaDon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNguoiLapDon, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNgayLapDon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(btnBackHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackHomeMouseClicked
        if (this.detail.getRole() == 1) {
            Home home = new Home(detail);
            this.setVisible(false);
            home.setVisible(true);
        } else {
            HomeUser home = new HomeUser(detail);
            this.setVisible(false);
            home.setVisible(true);
        }
    }//GEN-LAST:event_btnBackHomeMouseClicked

    private void tableBillMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableBillMouseClicked
        int Click = tableBill.getSelectedRow();
        TableModel model = tableBill.getModel();

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(model.getValueAt(Click, 3).toString()));
        int soTam = Integer.parseInt(model.getValueAt(Click, 4).toString());
        BigDecimal heSo = BigDecimal.valueOf(Double.parseDouble(model.getValueAt(Click, 5).toString()));
        String gia = model.getValueAt(Click, 7).toString();
        BigDecimal tongSoLuong = BigDecimal.valueOf(Double.parseDouble(model.getValueAt(Click, 6).toString()));
        String maBill = model.getValueAt(Click, 9).toString();
        String tongTien = model.getValueAt(Click, 8).toString();

        if (evt.getClickCount() == 2) {
            ModifyProduct dialog = new ModifyProduct(this, true, amount, soTam, heSo, gia, tongSoLuong, tongTien, maBill);
            dialog.setVisible(true);
            Load();
            Pays();
        }
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_tableBillMouseClicked

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


    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            JasperReport report = JasperCompileManager.compileReport("D:\\NhomSatKiemDien\\src\\UserInterFace\\Bill.jrxml");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ORDER_ID", MaHD);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);

            JasperViewer.viewReport(print, false);
            btnSavePdf.setEnabled(true);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private String generateInvoiceNumber() {
        String invoiceNumber = null;

        try {
            // Lấy số thứ tự hiện tại
            String selectSQL = "SELECT CurrentInvoiceNumber FROM InvoiceCounter";
            PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
            ResultSet rs = selectStmt.executeQuery();

            int currentNumber = 0;
            if (rs.next()) {
                currentNumber = rs.getInt("CurrentInvoiceNumber");
            }

            // Tăng số thứ tự lên 1
            int newNumber = currentNumber + 1;

            // Cập nhật số thứ tự mới
            String updateSQL = "UPDATE InvoiceCounter SET CurrentInvoiceNumber = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setInt(1, newNumber);
            updateStmt.executeUpdate();

            // Định dạng mã hóa đơn
            invoiceNumber = String.format("HD%08d", newNumber);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoiceNumber;
    }

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn tạo 1 hóa đơn bán hàng mới hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            try {
                if (MaHD != "") {
                    deleteOrder();
                    ((DefaultTableModel) tableBill.getModel()).setRowCount(0);
                }
                createOrder();
                checkBill();
                refreshCustomer();
                refreshOrderDetail();
                btnAdd.setEnabled(true);
                btnNew.setEnabled(false);
                btnPrint.setEnabled(false);
                btnSavePdf.setEnabled(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnNewActionPerformed

    private void createOrder() {
        try {
            // Tạo mã hóa đơn
            String invoiceNumber = generateInvoiceNumber();
            MaHD = invoiceNumber;

            // Tạo ngày giờ hiện tại
            LocalDateTime now = LocalDateTime.now();
            Timestamp sqlDate = Timestamp.valueOf(now);
//                // Thêm đơn hàng
            String insertOrderSQL = "INSERT INTO Orders (ID, Date, IsPayed, StaffName) VALUES (?, ?, ?, ?)";
            PreparedStatement pstOrder = conn.prepareStatement(insertOrderSQL);
            pstOrder.setString(1, invoiceNumber);
            pstOrder.setTimestamp(2, sqlDate);
            pstOrder.setBoolean(3, false);
            pstOrder.setString(4, detail.getName());
            pstOrder.executeUpdate();
            lblMaHoaDon.setText(MaHD);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            lblNgayLapDon.setText(format.format(sqlDate));
            lblNguoiLapDon.setText(detail.getName());
            btnDeleteOrder.setEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa sản phẩm khỏi hóa đơn hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM Bill WHERE ID = ?";
            try {
                int click = tableBill.getSelectedRow();
                TableModel model = tableBill.getModel();

                pst = conn.prepareStatement(sqlDelete);
                pst.setString(1, model.getValueAt(click, 9).toString().trim());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Xóa sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                Load();
                Sucessful();
                checkBill();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        ProductDialog dialog = new ProductDialog(this, true, MaHD);
        dialog.setVisible(true);
        Load();
        Pays();
    }//GEN-LAST:event_btnAddActionPerformed

    public String getNumberBeforeCommaOrAll(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        if (str.endsWith(",")) {
            int index = str.lastIndexOf(',');
            return str.substring(0, index);
        } else {
            return str;
        }
    }

    public boolean endsWithCommaOrDot(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        char lastChar = str.charAt(str.length() - 1);
        return lastChar == ',' || lastChar == '.';
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

    private String moneyDis(String money) {
        DecimalFormat format = new DecimalFormat("###,###,###");
        String price = format.format(Double.parseDouble(money));
        return price;
    }

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        if (tableBill.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Bạn chưa có sản phẩm nào trong đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        } else {
            if (txbKhachHang.getText().isBlank() || txbKhachHang.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui Lòng chọn khách hàng", "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                PayDialog dialog = new PayDialog(this, true, lbltotalMoney.getText(), txbDebt.getText(), lblTong.getText(), customerId, txbKhachHang.getText(), MaHD, txbDiaChi.getText(), txbSoDienThoai.getText(), detail.getUser());
                dialog.setVisible(true);
                btnDeleteOrder.setEnabled(false);

                if (StringUtils.isEmpty(customerId) == false) {
                    loadCustomer(customerId);
                }

                if (dialog.isPayed) {
                    if (StringUtils.isEmpty(dialog.getNoCu()) == false) {
                        lblNoCu.setText(dialog.getNoCu());
                    }

                    if (StringUtils.isEmpty(dialog.getTong()) == false) {
                        lblTong.setText(dialog.getTong());
                    }
                    if (StringUtils.isEmpty(dialog.getKhachTra()) == false) {
                        lblKhachThanhToan.setText(dialog.getKhachTra());
                    }

                    if (StringUtils.isEmpty(dialog.getConLai()) == false) {
                        lblConLai.setText(dialog.getConLai());
                    }

                    btnPay.setEnabled(false);
                    btnPrint.setEnabled(true);
                    btnNew.setEnabled(true);
                    btnAdd.setEnabled(false);
                    btnDeleteOrder.setEnabled(false);
                } else {
                    btnPay.setEnabled(true);
                    btnPrint.setEnabled(false);
                    btnNew.setEnabled(false);
                    btnAdd.setEnabled(true);
                    btnDeleteOrder.setEnabled(true);
                }

//            if (MaHD.equals("") == false) {
//                Customer cus = (Customer) cbxCustomer.getSelectedItem();
//                String sqlUpdate = "Update Orders set CustomerID = ? where ID = ?";
//                try ( PreparedStatement pst = conn.prepareStatement(sqlUpdate);) {
//                    pst.setInt(1, cus.getId());
//                    pst.setString(2, MaHD);
//                    pst.executeUpdate();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            if (txbInputMoney.getText().isBlank() || txbInputMoney.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Bạn chưa nhập số tiền thanh toán", "Thông báo", JOptionPane.WARNING_MESSAGE);
//            } else {
//                BigDecimal totalDebt = BigDecimal.ZERO;
//                BigDecimal surplus = BigDecimal.ZERO;
//                BigDecimal money = convertToMoney(lbltotalMoney.getText());
//                if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) < 0) {
//                    JOptionPane.showMessageDialog(null, "Số tiền khách thanh toán phải lớn hơn hoặc bằng 0", "Thông báo", JOptionPane.WARNING_MESSAGE);
//                } else {
//                    // Update nợ
//                    if (convertToMoney(txbDebt.getText()).compareTo(BigDecimal.ZERO) > 0) {
//
//                        // Số tiền nhập bằng = 0
//                        if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) == 0) {
//                            // Tổng nợ = Nợ cũ + tiền hàng
//                            totalDebt = convertToMoney(txbDebt.getText()).add(money);
//                        } else {
//
//                            // Tiền nhập > Nợ + tổng tiền hàng
//                            if (convertToMoney(txbInputMoney.getText()).compareTo(convertToMoney(txbDebt.getText()).add(money)) > 0) {
//                                totalDebt = BigDecimal.ZERO;
//                                surplus = (convertToMoney(txbInputMoney.getText()).subtract(money.add(convertToMoney(txbDebt.getText()))));
//                                lbltotalDebt.setText("0");
//                            } else {
//                                totalDebt = (convertToMoney(txbDebt.getText()).add(money)).subtract(convertToMoney(txbInputMoney.getText()));
//                            }
//                        }
//                    } else {
//                        // Nếu tiền nhập = 0
//                        if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) == 0) {
//                            totalDebt = money;
//                        } else {
//                            if (convertToMoney(txbInputMoney.getText()).compareTo(money) > 0) {
//                                surplus = (convertToMoney(txbInputMoney.getText()).subtract(money));
//                            } else if (convertToMoney(txbInputMoney.getText()).compareTo(money) < 0) {
//                                totalDebt = money.subtract(convertToMoney(txbInputMoney.getText()));
//                            }
//                        }
//                    }
//                }
//                lbltotalDebt.setText(moneyDis(totalDebt.toString()));
//                lblSurplus.setText(moneyDis(surplus.toString()));
//
//                btnPay.setEnabled(false);
//                txbInputMoney.setEnabled(false);
//                btnPrint.setEnabled(true);
//                btnNew.setEnabled(false);
//                btnAdd.setEnabled(false);
//                btnDelete.setEnabled(false);
//                updateDebt(totalDebt);
//                getCustomer();
//                updateOrder();
//            }
            }
        }

    }//GEN-LAST:event_btnPayActionPerformed

    private void updateDebt(BigDecimal debt) {
        String sqlChange = "UPDATE Customer SET Debt=?, OldDebt=? WHERE ID='" + customerId + "'";
        try {
            pst = conn.prepareStatement(sqlChange);
            pst.setBigDecimal(1, debt);
            pst.setBigDecimal(2, convertToMoney(txbDebt.getText()));
            pst.executeUpdate();
//            loadCustomer();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateOrder() {
        if (MaHD.isBlank() == false || MaHD.isEmpty() == false) {
            String sqlChange = "UPDATE Orders SET IsPayed=? WHERE ID='" + MaHD + "'";
            try {
                pst = conn.prepareStatement(sqlChange);
                pst.setBoolean(1, true);
                pst.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackHomeActionPerformed

    private void txbKhachHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txbKhachHangMouseClicked

    }//GEN-LAST:event_txbKhachHangMouseClicked

    private void txbSoDienThoaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txbSoDienThoaiMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txbSoDienThoaiMouseClicked

    private void txbDiaChiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txbDiaChiMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDiaChiMouseClicked

    private void txbDebtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txbDebtMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDebtMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        txbKhachHang.setText("");
        txbDiaChi.setText("");
        txbDebt.setText("");
        txbSoDienThoai.setText("");
        CustomerDialog diglog = new CustomerDialog(this, true);
        diglog.setVisible(true);
        khachHang = diglog.getKhachLe().split(",");
        if (khachHang.length == 0) {
            loadCustomer(diglog.getKhachHang());
            customerId = diglog.getKhachHang();
        } else {
            customerId = "";
            txbKhachHang.setText(khachHang[0]);
            txbDebt.setText("0");
            if (khachHang.length > 1) {
                txbDiaChi.setText(khachHang[1]);
            }

            if (khachHang.length > 2) {
                txbSoDienThoai.setText(khachHang[2]);
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnDeleteOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteOrderActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa hóa đơn hiện tại không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            String sqlDeleteBill = "DELETE FROM Bill WHERE OrderID = ?";
            String sqlDeleteOrder = "DELETE FROM Orders WHERE ID = ?";
            try {
                pst = conn.prepareStatement(sqlDeleteBill);
                pst.setString(1, MaHD);
                pst.executeUpdate();

                pst = conn.prepareStatement(sqlDeleteOrder);
                pst.setString(1, MaHD);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Xóa hóa đơn thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                Refresh();
                Load();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteOrderActionPerformed

    public static boolean checkIfFileExists(String directory, String fileName) {
        // Tạo đối tượng File cho file PDF cần kiểm tra
        File file = new File(directory + File.separator + fileName + ".pdf");

        // Kiểm tra xem file có tồn tại không
        return file.exists();
    }

    private void btnSavePdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavePdfActionPerformed
        try {
            // Biên dịch mẫu báo cáo JasperReports
            JasperReport report = JasperCompileManager.compileReport("D:\\NhomSatKiemDien\\src\\UserInterFace\\Bill.jrxml");

            // Thay thế các tham số theo yêu cầu
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ORDER_ID", MaHD);

            // Điền dữ liệu vào báo cáo
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);

//            // Hiển thị báo cáo trong JasperViewer
//            JasperViewer.viewReport(print, false);
            // Tạo đường dẫn và tên file PDF
            String directoryPath = "D:/Hóa Đơn"; // Đường dẫn thư mục lưu trữ
            String fileName = MaHD + ".pdf"; // Tên file dựa trên mã hóa đơn
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
    }//GEN-LAST:event_btnSavePdfActionPerformed

    private void deleteRecords() {
        String sqlDeleteOrder = "DELETE FROM Orders WHERE ID = ?";
        String sqlDelete = "DELETE FROM Bill WHERE OrderID = ?";
        String sql = "SELECT * FROM Orders WHERE IsPayed = ?";

        try ( PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBoolean(1, false);
            try ( ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String orderId = rs.getString("ID");

                    try ( PreparedStatement pstDeleteBill = conn.prepareStatement(sqlDelete)) {
                        pstDeleteBill.setString(1, orderId);
                        pstDeleteBill.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try ( PreparedStatement pstDeleteOrder = conn.prepareStatement(sqlDeleteOrder)) {
                        pstDeleteOrder.setString(1, orderId);
                        pstDeleteOrder.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            java.util.logging.Logger.getLogger(Sale.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sale.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sale.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sale.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new Sale(detail).setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteOrder;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSavePdf;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConLai;
    private javax.swing.JLabel lblKhachThanhToan;
    private javax.swing.JLabel lblMaHoaDon;
    private javax.swing.JLabel lblNgayLapDon;
    private javax.swing.JLabel lblNguoiLapDon;
    private javax.swing.JLabel lblNoCu;
    private javax.swing.JLabel lblTong;
    private javax.swing.JLabel lbltotalMoney;
    private javax.swing.JTable tableBill;
    private javax.swing.JLabel txbDebt;
    private javax.swing.JLabel txbDiaChi;
    private javax.swing.JLabel txbKhachHang;
    private javax.swing.JLabel txbSoDienThoai;
    // End of variables declaration//GEN-END:variables

    // Lớp ComboBoxFilter để lọc dữ liệu trong comboBox
    public static class ComboBoxFilter extends DocumentFilter {

        private JComboBox<Classify> comboBox;
        private List<Classify> classifyList;
        private JTextField textField;
        private boolean isAdjusting;

        public ComboBoxFilter(JComboBox<Classify> comboBox, List<Classify> classifyList, JTextField textField) {
            this.comboBox = comboBox;
            this.classifyList = classifyList;
            this.textField = textField;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isAdjusting) {
                return;
            }
            super.insertString(fb, offset, string, attr);
            filter();
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isAdjusting) {
                return;
            }
            super.replace(fb, offset, length, text, attrs);
            filter();
        }

        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            if (isAdjusting) {
                return;
            }
            super.remove(fb, offset, length);
            filter();
        }

        private void filter() {
            isAdjusting = true;
            String text = textField.getText();
            comboBox.removeAllItems();
            if (text.isEmpty()) {
                for (Classify classify : classifyList) {
                    comboBox.addItem(classify);
                }
            } else {
                for (Classify classify : classifyList) {
                    if (classify.getClassify().toLowerCase().contains(text.toLowerCase())) {
                        comboBox.addItem(classify);
                    }
                }
            }
            comboBox.getEditor().setItem(text);
            comboBox.setPopupVisible(true); // Hiển thị lại popup để cập nhật kích thước
            comboBox.revalidate();
            isAdjusting = false;
        }
    }

    @Override
    public void run() {
        while (true) {
            deleteRecords();
            try {
                TimeUnit.MINUTES.sleep(60);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
