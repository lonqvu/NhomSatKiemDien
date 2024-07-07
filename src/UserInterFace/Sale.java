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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.TableColumn;

class Sale extends javax.swing.JFrame implements Runnable {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private boolean Add = false, Change = false, Pay = false;
    private String sql = "SELECT * FROM Bill";

    private Thread thread;
    private Detail detail;
    private Customers customer;

    private String MaHD = "";
    private String classifyID;

    public Sale() {
        // Constructor mặc định (có thể thêm các hành động khởi tạo tại đây)
    }

    public Sale(Detail d) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail = new Detail(d);
        lblStatus.setForeground(Color.red);
        setData();
        connection();
        Start();
        checkBill();
        loadCustomer();
        txbAddress.setEnabled(false);
        txbCustomerName.setEnabled(false);
        txbPhoneNumber.setEnabled(false);
        if (this.detail.getRole() == 1) {
            btnCustomer.setEnabled(true);
        }
    }

    private void loadCustomer() {
        String sql = "Select * from Customer";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Customer cus = new Customer(rs.getInt("ID"), rs.getString("CustomerName"), rs.getString("Address"), rs.getString("PhoneNumber"));
                this.cbxCustomer.addItem(cus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setData() {
        Disabled();
        txbInputMoney.setText("0");
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

    private void Update() {
//        lblTime.setText(String.valueOf(new SimpleDateFormat("HH:mm:ss").format(new java.util.Date())));
    }

    private void Enabled() {
        cbxClassify.setEnabled(true);
        checkProduct.setEnabled(true);
    }

    private void Disabled() {
        cbxClassify.setEnabled(false);
        checkProduct.setEnabled(false);
        txbAmount.setEnabled(false);
        cbxUnit.setEnabled(false);
        txbProductName.setEnabled(false);
        txbPrice.setEnabled(false);
    }

    private void clear() {
        txbCode.setText("");
        txbPrice.setText("");
        txbAmount.setText("");
        txbIntoMoney.setText("");
        txbProductName.setText("");
        lblStatus.setText("");
        txbInputMoney.setEnabled(true);
        checkProduct.setSelected(true);
        cbxProduct.removeAllItems();
        cbxClassify.removeAllItems();
        LoadClassify();
    }

    private void Refresh() {
        Add = false;
        Change = false;
        Pay = false;
        txbCode.setText("");
        txbPrice.setText("");
        txbAmount.setText("");
        txbIntoMoney.setText("");
        txbProductName.setText("");
        cbxProduct.removeAllItems();
        cbxClassify.removeAllItems();
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(false);
        btnPrint.setEnabled(false);
        btnNew.setEnabled(true);
        btnAdd.setEnabled(false);
        btnPay.setEnabled(false);
        checkProduct.setSelected(true);
        lbltotalMoney.setText("0");
        lblStatus.setText("");
        lbltotalDebt.setText("0");
        lblSurplus.setText("0");
        txbProductName.setText("");
        txbInputMoney.setEnabled(true);
        Disabled();
        tableBill.removeAll();
    }

    private void refreshCustomer() {
        txbCustomerName.setText("");
        txbPhoneNumber.setText("");
        txbAddress.setText("");
        txbDebt.setText("");
        cbxCustomer.removeAllItems();
        loadCustomer();
        cbxCustomer.setEnabled(true);
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
            lbltotalMoney.setText("0 VNĐ");
        }
    }

    private boolean Check() {
        boolean kq = true;
        String sqlCheck = "SELECT * FROM Bill where OrderID = ?";
        try {
            PreparedStatement pstCheck = conn.prepareStatement(sqlCheck);
            pstCheck.setString(1, MaHD);
            ResultSet rsCheck = pstCheck.executeQuery();
            while (rsCheck.next()) {
                if (rsCheck.getString("ProductID") != null) {
                    if (this.txbCode.getText().equals(rsCheck.getString("ProductID"))) {
                        return false;
                    }
                } else {
                    if (this.txbProductName.getText().equals(rsCheck.getString("Product"))) {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kq;
    }

    private boolean checkNull() {
        boolean kq = true;
        if (checkProduct.isSelected()) {
            if (String.valueOf(this.txbCode.getText()).length() == 0) {
                lblStatus.setText("Bạn chưa chọn sản phẩm!");
                return false;
            } else if (String.valueOf(this.txbAmount.getText()).length() == 0) {
                lblStatus.setText("Bạn chưa nhập số lượng sản phẩm!");
                return false;
            }
        } else {
            if (String.valueOf(this.txbProductName.getText()).length() == 0) {
                lblStatus.setText("Bạn chưa chọn sản phẩm!");
                return false;
            } else if (String.valueOf(this.txbAmount.getText()).length() == 0) {
                lblStatus.setText("Bạn chưa nhập số lượng sản phẩm!");
                return false;
            }
        }
        return kq;
    }

    private void Sucessful() {
        btnSave.setEnabled(false);
        btnAdd.setEnabled(true);
        btnNew.setEnabled(true);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void deleteInformation() {
        String sqlDelete = "DELETE FROM Information";
        try {
            pst = conn.prepareStatement(sqlDelete);
            pst.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addProduct() {
        if (checkNull()) {
            String sqlInsert = "INSERT INTO Bill (Amount, IntoMoney, ProductID, OrderID, RegistDate, Product, UnitID, Price) VALUES(?,?,?,?,?,?,?,?)";
            Product productSelected = (Product) cbxProduct.getSelectedItem();
            Unit unit = (Unit) cbxUnit.getSelectedItem();
            try {
                pst = conn.prepareStatement(sqlInsert);
                double amount = Double.parseDouble(txbAmount.getText());
                if (checkProduct.isSelected()) {
                    pst.setBigDecimal(1, BigDecimal.valueOf(amount));
                    pst.setString(2, txbIntoMoney.getText());
                    pst.setString(3, productSelected.getId());
                    pst.setString(4, MaHD);

                    LocalDateTime now = LocalDateTime.now();
                    Timestamp sqlDate = Timestamp.valueOf(now);
                    pst.setTimestamp(5, sqlDate);
                    pst.setString(6, "");
                    pst.setString(7, null);
                    pst.setBigDecimal(8, null);
                    pst.executeUpdate();
                    lblStatus.setText("Thêm sản phẩm thành công!");
                } else {
                    pst.setBigDecimal(1, BigDecimal.valueOf(amount));
                    pst.setString(2, txbIntoMoney.getText());
                    pst.setString(3, null);
                    pst.setString(4, MaHD);
                    LocalDateTime now = LocalDateTime.now();
                    Timestamp sqlDate = Timestamp.valueOf(now);
                    pst.setTimestamp(5, sqlDate);
                    pst.setString(6, txbProductName.getText());
                    pst.setString(7, unit.getId());
                    pst.setBigDecimal(8, convertToMoney(txbPrice.getText()));
                    pst.executeUpdate();
                    lblStatus.setText("Thêm sản phẩm thành công!");
                }
                Disabled();
                Sucessful();
                btnPay.setEnabled(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void LoadClassify() {
        List<Classify> classifies = fetchClassify();
        for (Classify classify : classifies) {
            cbxClassify.addItem(classify);
        }
    }

    private List<Classify> fetchClassify() {
        List<Classify> classifies = new ArrayList<>();

        String query = "SELECT * FROM classify";

        try {
            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String classifyName = rs.getString("classify");
                classifies.add(new Classify(id, classifyName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classifies;
    }

    private void LoadCustomer() {
        cbxCustomer.removeAll();
        String sql = "SELECT * FROM Customer";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Customer cus = new Customer(rs.getInt("ID"), rs.getString("CustomerName"), rs.getString("Address"), rs.getString("PhoneNumber"));
                this.cbxCustomer.addItem(cus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeProduct() {
        int Click = tableBill.getSelectedRow();
        TableModel model = tableBill.getModel();

        String sqlChange = "UPDATE Bill SET Amount=?, IntoMoney=? WHERE ID='" + model.getValueAt(Click, 6).toString().trim() + "'";
        try {
            double amount = Double.parseDouble(txbAmount.getText());
            pst = conn.prepareStatement(sqlChange);
            pst.setBigDecimal(1, BigDecimal.valueOf(amount));
            pst.setBigDecimal(2, convertToMoney(txbIntoMoney.getText()));
            pst.executeUpdate();
            Disabled();
            Sucessful();
            lblStatus.setText("Lưu thay đổi thành công!");
            Load();
            btnPay.setEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getCustomer() {
        String sql = "SELECT * FROM Customer where ID=?";
        Customer customer = (Customer) cbxCustomer.getSelectedItem();
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, customer.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                txbCustomerName.setText(rs.getString("CustomerName").trim());
                txbPhoneNumber.setText(rs.getString("PhoneNumber").trim());
                txbAddress.setText(rs.getString("Address").trim());
                txbDebt.setText(moneyDis(rs.getString("Debt")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Load() {
        DecimalFormat formatter = new DecimalFormat("#.##");
        // Xóa tất cả các dòng hiện tại trong bảng
        tableBill.removeAll();
        BigDecimal money = BigDecimal.ZERO;
        String sql = "SELECT Bill.*, Products.Price as ProductPrice FROM Bill Left Join Products on Bill.ProductID = Products.ID WHERE OrderID = ?";
        if (MaHD != "") {
            // Sử dụng try-with-resources để tự động đóng PreparedStatement và ResultSet
            try (
                     PreparedStatement pst = conn.prepareStatement(sql);) {
                String[] arr = {"Mã Sản Phẩm", "Tên Sản Phẩm", "Đơn Vị", "Số Lượng", "Đơn Giá", "Thành Tiền", "ID Bill"};
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
                        String product = rs.getString("Product");
                        BigDecimal amount = rs.getBigDecimal("Amount");
                        String productID = rs.getString("ProductID");
                        Unit unitSelect = (Unit) cbxUnit.getSelectedItem();

                        Vector<Object> vector = new Vector<>();

                        if (productID != null) {
                            vector.add(productID);
                        } else {
                            vector.add("SP000");
                        }

                        if (product != null && productID == null) {
                            vector.add(product);
                            vector.add(getUnitNameById(rs.getString("UnitID")));
                            vector.add(formatter.format(amount));
                            vector.add(moneyDis(rs.getString("Price")));
                            money = money.add(rs.getBigDecimal("Price"));
                        } else {
                            Product productEntity = getProductById(productID);
                            vector.add(productEntity.getProductName());
                            vector.add(getUnitNameById(productEntity.getUnitId()));
                            vector.add(formatter.format(amount));
                            vector.add(moneyDis(rs.getString("ProductPrice")));
                            money = money.add(rs.getBigDecimal("ProductPrice"));
                        }

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
                TableColumn tableColumn = tableBill.getColumnModel().getColumn(6);
                tableBill.getColumnModel().removeColumn(tableColumn);

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
                return new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitID"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private String getUnitNameById(String id) {
        String sql = "select * from Unit where id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                return rs.getString("Unit");
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

    private void loadPriceandClassify(String s) {

        String sql = "SELECT * FROM Products WHERE ID = ?";
        try (
                 PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, s.trim());
            try ( ResultSet rs = pst.executeQuery()) {
                if (rs.next()) { // Sử dụng if thay vì while vì chỉ mong đợi một kết quả
                    String price = rs.getString("Price");
                    String classifyID = rs.getString("ClassifyID").trim();

                    // Cập nhật giao diện người dùng
                    Classify classify = getClassifyById(classifyID);
                    if (classify != null) {
                        this.cbxClassify.setSelectedItem(classify);
                    }
                    this.txbPrice.setText(moneyDis(price.trim()));
                    Product product = new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitID"));
                    this.cbxProduct.addItem(product);
                    this.cbxProduct.setSelectedItem(product);
                    Unit unit = getUnitById(rs.getString("UnitID"));
                    if (unit != null) {
                        this.cbxUnit.setSelectedItem(unit);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                    product = new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitID"));
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbxClassify = new javax.swing.JComboBox<>();
        cbxProduct = new javax.swing.JComboBox<>();
        txbIntoMoney = new javax.swing.JTextField();
        txbAmount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txbCode = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txbPrice = new javax.swing.JTextField();
        checkProduct = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        txbProductName = new javax.swing.JTextField();
        cbxUnit = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnPay = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lbltotalMoney = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txbCustomerName = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txbPhoneNumber = new javax.swing.JTextField();
        txbAddress = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        cbxCustomer = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        txbDebt = new javax.swing.JTextField();
        btnCustomer = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txbInputMoney = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        lbltotalDebt = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblSurplus = new javax.swing.JLabel();

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

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel1.setText("Loại Sản Phẩm:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel2.setText("Mã Sản Phẩm:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel3.setText("Số Lượng:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel4.setText("Thành Tiền:");

        cbxClassify.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbxClassifyPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cbxClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxClassifyActionPerformed(evt);
            }
        });

        cbxProduct.setEnabled(false);
        cbxProduct.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbxProductPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cbxProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxProductActionPerformed(evt);
            }
        });

        txbIntoMoney.setEnabled(false);

        txbAmount.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                updateTotal(evt);
            }
        });
        txbAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbAmountActionPerformed(evt);
            }
        });
        txbAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbAmountKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel5.setText("Tên Sản Phẩm:");

        txbCode.setEnabled(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel7.setText("Giá:");

        txbPrice.setEnabled(false);
        txbPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPriceKeyReleased(evt);
            }
        });

        checkProduct.setSelected(true);
        checkProduct.setText("Đã có thông tin sản phẩm");
        checkProduct.setEnabled(false);
        checkProduct.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ProductEnable(evt);
            }
        });
        checkProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkProductActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel15.setText("Tên Sản Phẩm:");

        txbProductName.setEnabled(false);

        cbxUnit.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkProduct)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbxProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbCode, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txbAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(70, 70, 70)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addComponent(checkProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txbAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txbProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

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

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Save.png"))); // NOI18N
        btnSave.setText("Lưu");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnChange.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Change.png"))); // NOI18N
        btnChange.setText("Sửa");
        btnChange.setEnabled(false);
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/New.png"))); // NOI18N
        btnNew.setText("Hóa Đơn Mới");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        btnPrint.setText("Xuất HD");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Tổng Tiền Hóa Đơn:");

        lbltotalMoney.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbltotalMoney.setText("0 VND");

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

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Trạng Thái");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setEnabled(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setText("Khách Hàng");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel12.setText("Địa Chỉ:");

        txbCustomerName.setEnabled(false);
        txbCustomerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbCustomerNameActionPerformed(evt);
            }
        });
        txbCustomerName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbCustomerNameKeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel13.setText("SĐT:");

        txbPhoneNumber.setEnabled(false);
        txbPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbPhoneNumberActionPerformed(evt);
            }
        });
        txbPhoneNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPhoneNumberKeyReleased(evt);
            }
        });

        txbAddress.setEnabled(false);
        txbAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbAddressActionPerformed(evt);
            }
        });
        txbAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbAddressKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel14.setText("Tên Khách Hàng:");

        cbxCustomer.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbxCustomerPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cbxCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCustomerActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel16.setText("Nợ Cũ");

        txbDebt.setEnabled(false);
        txbDebt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbDebtActionPerformed(evt);
            }
        });
        txbDebt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbDebtKeyReleased(evt);
            }
        });

        btnCustomer.setText("...");
        btnCustomer.setEnabled(false);
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbxCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txbCustomerName))
                .addGap(50, 50, 50)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txbDebt, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(txbDebt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(cbxCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(txbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCustomer)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txbCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel14))
                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Khách Thanh Toán: ");

        txbInputMoney.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbInputMoneyActionPerformed(evt);
            }
        });
        txbInputMoney.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbInputMoneyKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setText("Tổng Nợ:");

        lbltotalDebt.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbltotalDebt.setText("0 VND");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel17.setText("Tiền Dư: ");

        lblSurplus.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblSurplus.setText("0 VND");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbltotalMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txbInputMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(64, 64, 64)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbltotalDebt, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSurplus, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBackHome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSurplus, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbltotalDebt, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbltotalMoney, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addComponent(txbInputMoney)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus))
        );

        lbltotalMoney.getAccessibleContext().setAccessibleName("0");

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

        if (!model.getValueAt(Click, 0).toString().equals("SP000")) {
            txbCode.setText(model.getValueAt(Click, 0).toString());
            txbAmount.setText(model.getValueAt(Click, 3).toString());
            txbIntoMoney.setText(model.getValueAt(Click, 5).toString());
            loadPriceandClassify(model.getValueAt(Click, 0).toString());
            txbProductName.setText("");
        } else {
            txbProductName.setText(model.getValueAt(Click, 1).toString());
            txbAmount.setText(model.getValueAt(Click, 3).toString());
            txbIntoMoney.setText(model.getValueAt(Click, 5).toString());
            txbPrice.setText(model.getValueAt(Click, 4).toString());
        }
        btnChange.setEnabled(true);
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

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        clear();
        refreshCustomer();
        btnAdd.setEnabled(true);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed

        try {
            JasperReport report = JasperCompileManager.compileReport("D:\\NhomSatKiemDien\\src\\UserInterFace\\Bill.jrxml");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ORDER_ID", MaHD);
            parameters.put("TOTAL_MONEY", convertToMoney(lbltotalMoney.getText()));
            parameters.put("INPUT_MONEY", convertToMoney(txbInputMoney.getText()));
            parameters.put("TOTAL_DEBT", convertToMoney(lbltotalDebt.getText()));
            parameters.put("DEBT", convertToMoney(txbDebt.getText()));
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);

            JasperViewer.viewReport(print, false);
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
                this.lblStatus.setText("Đã tạo hóa đơn mới!");
                checkBill();
                Refresh();
                btnAdd.setEnabled(true);
                btnNew.setEnabled(false);
                btnRefresh.setEnabled(true);

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
            String insertOrderSQL = "INSERT INTO Orders (ID, Date, IsPayed) VALUES (?, ?, ?)";
            PreparedStatement pstOrder = conn.prepareStatement(insertOrderSQL);
            pstOrder.setString(1, invoiceNumber);
            pstOrder.setTimestamp(2, sqlDate);
            pstOrder.setBoolean(3, false);
            pstOrder.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Add = false;
        Change = true;
        btnAdd.setEnabled(false);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(true);
        txbAmount.setEnabled(true);
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (Add == true) {
            if (Check()) {
                addProduct();

            } else {
                lblStatus.setText("Sản phẩm đã tồn tại trong hóa đơn");
            }
        } else if (Change == true) {
            changeProduct();
        }
        checkBill();
        Pays();
        cbxProduct.setEnabled(false);
        Load();
        txbInputMoney.setText("0");
        btnNew.setEnabled(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa sản phẩm khỏi hóa đơn hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM Bill WHERE ID = ?";
            try {
                int click = tableBill.getSelectedRow();
                TableModel model = tableBill.getModel();

                pst = conn.prepareStatement(sqlDelete);
                pst.setString(1, model.getValueAt(click, 6).toString().trim());
                pst.executeUpdate();
                this.lblStatus.setText("Xóa sản phẩm thành công!");
                Refresh();
                Load();
                Sucessful();
                checkBill();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add = true;
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
        btnNew.setEnabled(false);
        Enabled();
        LoadClassify();
    }//GEN-LAST:event_btnAddActionPerformed

    private void txbCustomerNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbCustomerNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbCustomerNameKeyReleased

    private void txbPhoneNumberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPhoneNumberKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbPhoneNumberKeyReleased

    private void txbAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbAddressKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbAddressKeyReleased

    private void txbCustomerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbCustomerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbCustomerNameActionPerformed

    private void cbxCustomerPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxCustomerPopupMenuWillBecomeInvisible
        String sql = "SELECT * FROM Customer where ID=?";
        Customer customer = (Customer) cbxCustomer.getSelectedItem();
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, customer.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                txbCustomerName.setText(rs.getString("CustomerName").trim());
                txbPhoneNumber.setText(rs.getString("PhoneNumber").trim());
                txbAddress.setText(rs.getString("Address").trim());
                txbDebt.setText(moneyDis(rs.getString("Debt")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cbxCustomerPopupMenuWillBecomeInvisible

    private void cbxCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCustomerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxCustomerActionPerformed

    private void txbPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbPhoneNumberActionPerformed

    private void txbAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbAddressActionPerformed

    private void checkProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkProductActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkProductActionPerformed

    private void ProductEnable(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ProductEnable
        if (checkProduct.isSelected() == false) {
            cbxProduct.setEnabled(false);
            cbxClassify.setEnabled(false);
            txbProductName.setEnabled(true);
            cbxUnit.setEnabled(true);
            txbAmount.setEnabled(true);
            txbPrice.setEnabled(true);
            loadUnit();
        } else {
            cbxClassify.setEnabled(true);
            txbProductName.setEnabled(false);
            txbPrice.setEnabled(false);
            cbxUnit.setEnabled(false);
            txbAmount.setEnabled(false);
        }
    }//GEN-LAST:event_ProductEnable

    private void loadUnit() {
        String sql = "SELECT * FROM Unit";
        cbxUnit.removeAllItems();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Unit unit = new Unit(rs.getString("ID"), rs.getString("Unit"));
                this.cbxUnit.addItem(unit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void txbAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbAmountKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        String text = txbAmount.getText().replaceAll("[^0-9.]", "");
        txbAmount.setText(text);
        float soluong = Float.parseFloat(txbAmount.getText());

        if (checkProduct.isSelected()) {
            if (text.isBlank() || text.isEmpty()) {
                txbIntoMoney.setText("0");
                lblStatus.setText("Vui lòng nhập đơn giá");
            } else {
                try {
                    txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(BigDecimal.valueOf(soluong)))));
                    btnSave.setEnabled(true);
                    lblStatus.setText("");
                } catch (NumberFormatException e) {
                    lblStatus.setText("Số lượng không hợp lệ");
                }
            }
        } else {
            if (text.isBlank() || text.isEmpty()) {
                txbIntoMoney.setText("0");
                lblStatus.setText("Vui lòng nhập số lượng");
            } else {
                if (txbPrice.getText().isEmpty() || txbPrice.getText().isBlank()) {
                    lblStatus.setText("Vui lòng nhập đơn giá");
                } else {
                    try {
                        txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(BigDecimal.valueOf(soluong)))));
                        lblStatus.setText("");
                        btnSave.setEnabled(true);
                    } catch (NumberFormatException e) {
                        lblStatus.setText("Số lượng không hợp lệ");
                    }
                }
            }
        }
    }//GEN-LAST:event_txbAmountKeyReleased
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

    private void txbAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbAmountActionPerformed

    private void updateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_updateTotal
        try {
            int quantity = Integer.parseInt(txbAmount.getText());
            double total = Double.parseDouble(txbPrice.getText()) * quantity;
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            txbIntoMoney.setText(formatter.format(total));
        } catch (NumberFormatException ex) {
            // Nếu không phải là số, không làm gì cả
        }
    }//GEN-LAST:event_updateTotal

    private void cbxProductPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxProductPopupMenuWillBecomeInvisible
        String sql = "SELECT * FROM Products where ID=?";
        Product productSelected = (Product) cbxProduct.getSelectedItem();
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, productSelected.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                txbCode.setText(rs.getString("ID").trim());
                txbPrice.setText(moneyDis(rs.getBigDecimal("price").toString()));
                Unit unit = getUnitById(rs.getString("UnitID"));
                if (unit != null) {
                    this.cbxUnit.setSelectedItem(unit);
                }
                txbAmount.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cbxProductPopupMenuWillBecomeInvisible

    private String moneyDis(String money) {
        DecimalFormat format = new DecimalFormat("#,##0.##");
        String price = format.format(Double.parseDouble(money));
        return price;
    }

    private void cbxClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxClassifyActionPerformed

    }//GEN-LAST:event_cbxClassifyActionPerformed

    private void cbxProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxProductActionPerformed

    }//GEN-LAST:event_cbxProductActionPerformed

    private void cbxClassifyPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxClassifyPopupMenuWillBecomeInvisible
        Classify selectedClassify = (Classify) cbxClassify.getSelectedItem();
        cbxProduct.removeAllItems();
        String sql = "SELECT * FROM Products where ClassifyID=?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, selectedClassify.getId());
            rs = pst.executeQuery();
            while (rs.next()) {
                Product product = new Product(rs.getString("ID"), rs.getString("Name"), rs.getString("UnitID"));
                this.cbxProduct.addItem(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cbxProduct.getItemCount() == 0) {
            cbxProduct.setEnabled(false);
            txbAmount.setEnabled(false);
            txbCode.setText("");
            txbPrice.setText("");
            txbAmount.setText("");
            txbIntoMoney.setText("");
        } else {
            cbxProduct.setEnabled(true);
            loadUnit();
        }
    }//GEN-LAST:event_cbxClassifyPopupMenuWillBecomeInvisible

    private void txbPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPriceKeyReleased

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        NumberFormat format = NumberFormat.getNumberInstance();

        txbPrice.setText(cutChar(txbPrice.getText()));
        if (txbPrice.getText().equals("")) {
            return;
        } else {
            txbPrice.setText(formatter.format(convertedToNumbers(txbPrice.getText())));
        }

        if (checkProduct.isSelected() == false) {
            if (txbAmount.getText().isBlank() || txbAmount.getText().isEmpty()) {
                txbIntoMoney.setText("0");
            } else {
                int soluong = Integer.parseInt(txbAmount.getText().toString());
                if (soluong < 1) {
                    lblStatus.setText("Vui lòng nhập số lượng.");
                } else {
                    txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(BigDecimal.valueOf(soluong)))));
                    lblStatus.setText("");
                    btnSave.setEnabled(true);
                }
            }

        }
    }//GEN-LAST:event_txbPriceKeyReleased

    private void txbDebtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbDebtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDebtActionPerformed

    private void txbDebtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbDebtKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbDebtKeyReleased

    private void txbInputMoneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbInputMoneyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbInputMoneyActionPerformed

    private void txbInputMoneyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbInputMoneyKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        NumberFormat format = NumberFormat.getNumberInstance();

        txbInputMoney.setText(cutChar(txbInputMoney.getText()));
        if (txbInputMoney.getText().equals("")) {
            return;
        } else {
            txbInputMoney.setText(formatter.format(convertedToNumbers(txbInputMoney.getText())));
        }
    }//GEN-LAST:event_txbInputMoneyKeyReleased

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn thanh toán hóa đơn này không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            if (txbCustomerName.getText().isBlank() || txbCustomerName.getText().isEmpty()) {
                lblStatus.setText("Vui Lòng chọn khách hàng");
            } else {
                if (MaHD.equals("") == false) {
                    Customer cus = (Customer) cbxCustomer.getSelectedItem();
                    String sqlUpdate = "Update Orders set CustomerID = ? where ID = ?";
                    try ( PreparedStatement pst = conn.prepareStatement(sqlUpdate);) {
                        pst.setInt(1, cus.getId());
                        pst.setString(2, MaHD);
                        pst.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                if (txbInputMoney.getText().isBlank() || txbInputMoney.getText().isEmpty()) {
                    lblStatus.setText("Vui lòng nhập tiền khách thanh toán");
                } else {
                    BigDecimal totalDebt = BigDecimal.ZERO;
                    BigDecimal surplus = BigDecimal.ZERO;
                    BigDecimal money = convertToMoney(lbltotalMoney.getText());
                    if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) < 0) {
                        lblStatus.setText("Số tiền khách thanh toán phải lớn hơn hoặc bằng 0");
                    } else {
                        // Update nợ
                        if (convertToMoney(txbDebt.getText()).compareTo(BigDecimal.ZERO) > 0) {

                            // Số tiền nhập bằng = 0
                            if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) == 0) {
                                // Tổng nợ = Nợ cũ + tiền hàng
                                totalDebt = convertToMoney(txbDebt.getText()).add(money);
                            } else {

                                // Tiền nhập > Nợ + tổng tiền hàng
                                if (convertToMoney(txbInputMoney.getText()).compareTo(convertToMoney(txbDebt.getText()).add(money)) > 0) {
                                    totalDebt = BigDecimal.ZERO;
                                    surplus = (convertToMoney(txbInputMoney.getText()).subtract(money.add(convertToMoney(txbDebt.getText()))));
                                    lbltotalDebt.setText("0");
                                } else {
                                    totalDebt = (convertToMoney(txbDebt.getText()).add(money)).subtract(convertToMoney(txbInputMoney.getText()));
                                }
                            }
                        } else {
                            // Nếu tiền nhập = 0
                            if (convertToMoney(txbInputMoney.getText()).compareTo(BigDecimal.ZERO) == 0) {
                                totalDebt = money;
                            } else {
                                if (convertToMoney(txbInputMoney.getText()).compareTo(money) > 0) {
                                    surplus = (convertToMoney(txbInputMoney.getText()).subtract(money));
                                } else if (convertToMoney(txbInputMoney.getText()).compareTo(money) < 0) {
                                    totalDebt = money.subtract(convertToMoney(txbInputMoney.getText()));
                                }
                            }
                        }
                    }
                    lbltotalDebt.setText(moneyDis(totalDebt.toString()));
                    lblSurplus.setText(moneyDis(surplus.toString()));

                    btnPay.setEnabled(false);
                    txbInputMoney.setEnabled(false);
                    btnPrint.setEnabled(true);
                    btnNew.setEnabled(false);
                    btnAdd.setEnabled(false);
                    btnChange.setEnabled(false);
                    btnDelete.setEnabled(false);
                    updateDebt(totalDebt);
                    getCustomer();
                    updateOrder();
                }
            }
        }
    }//GEN-LAST:event_btnPayActionPerformed

    private void updateDebt(BigDecimal debt) {
        Customer cus = (Customer) cbxCustomer.getSelectedItem();
        String sqlChange = "UPDATE Customer SET Debt=?, OldDebt=? WHERE ID='" + cus.getId() + "'";
        try {
            pst = conn.prepareStatement(sqlChange);
            pst.setBigDecimal(1, debt);
            pst.setBigDecimal(2, convertToMoney(txbDebt.getText()));
            pst.executeUpdate();
            loadCustomer();
            cbxCustomer.setSelectedItem(cus);

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

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed

        Customers cus = new Customers(detail);
        this.setVisible(false);
        cus.setVisible(true);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackHomeActionPerformed

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
            java.util.logging.Logger.getLogger(Sale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<Classify> cbxClassify;
    private javax.swing.JComboBox<Customer> cbxCustomer;
    private javax.swing.JComboBox<Product> cbxProduct;
    private javax.swing.JComboBox<Unit> cbxUnit;
    private javax.swing.JCheckBox checkProduct;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSurplus;
    private javax.swing.JLabel lbltotalDebt;
    private javax.swing.JLabel lbltotalMoney;
    private javax.swing.JTable tableBill;
    private javax.swing.JTextField txbAddress;
    private javax.swing.JTextField txbAmount;
    private javax.swing.JTextField txbCode;
    private javax.swing.JTextField txbCustomerName;
    private javax.swing.JTextField txbDebt;
    private javax.swing.JTextField txbInputMoney;
    private javax.swing.JTextField txbIntoMoney;
    private javax.swing.JTextField txbPhoneNumber;
    private javax.swing.JTextField txbPrice;
    private javax.swing.JTextField txbProductName;
    // End of variables declaration//GEN-END:variables

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
