package UserInterFace;

import Entity.Classify;
import Entity.Unit;
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

public class Product extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private String sql = "SELECT * FROM Products";
    private boolean Add = false, Change = false;

    private Detail detail;

    public Product(Detail d) {
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        connection();
        detail = new Detail(d);
        Disabled();
        Load(sql);
        loadUnit();
        LoadClassify();
        lblStatus.setForeground(Color.red);
    }

    private void connection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-L247M4M:1433;databaseName=NhomSatKiemDien;user=sa;password=123;encrypt=false;");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Load(String sql) {
        DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        model.setRowCount(0);
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"Mã Sản Phẩm", "Loại sản phẩm", "Tên sản phẩm", "đơn vị", "Giá", "ClassifyID", "UnitID"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0);

            while (rs.next()) {
                String productName = rs.getString("Name").trim();
                String price = decimalFormat.format(Double.parseDouble(rs.getString("Price").trim()));
                String unitId = rs.getString("UnitID").trim();
                String classifyId = rs.getString("ClassifyID").trim();

                Vector vector = new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(getClassifyById(rs.getString("ClassifyID").trim()));
                vector.add(productName);
                vector.add(getUnitById(unitId).getUnit());
                vector.add(price);
                vector.add(classifyId);
                vector.add(unitId);
                modle.addRow(vector);
            }
            tableProduct.setModel(modle);
            TableColumn tableColumn5 = tableProduct.getColumnModel().getColumn(5);
            TableColumn tableColumn6 = tableProduct.getColumnModel().getColumn(6);
            tableProduct.getColumnModel().removeColumn(tableColumn5);
            tableProduct.getColumnModel().removeColumn(tableColumn6);
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

    private Classify getClassifyById(String id) {
        String sql = "select * from Classify where id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                return new Classify(rs.getString("ID"), rs.getString("Classify"));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private void LoadClassify() {
        cbxClassify.removeAllItems();
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

    private void Enabled() {
        btnClassify.setEnabled(true);
//        btnNSX.setEnabled(true);
        txbID.setEnabled(true);
        txbName.setEnabled(true);
//        txbWarrantyPeriod.setEnabled(true);
//        txbAmount.setEnabled(true);
        txbPrice.setEnabled(true);
        cbxClassify.setEnabled(true);
//        cbxProducer.setEnabled(true);
//        cbxTime.setEnabled(true);
//        btnUpdate.setEnabled(true);
        cbxClassify.setEnabled(true);
//        cbxProducer.setEnabled(true);
//        cbxTime.setEnabled(true);
//        btnUpdate.setEnabled(true);
        cbxUnit.setEnabled(true);
        lblStatus.setText("Trạng Thái!");
    }

    public void Disabled() {
        btnClassify.setEnabled(false);
        txbID.setEnabled(false);
        txbName.setEnabled(false);
        txbPrice.setEnabled(false);
        cbxClassify.setEnabled(false);
        cbxUnit.setEnabled(false);
    }

    public void Refresh() {
        txbID.setText("");
        txbName.setText("");
//        txbWarrantyPeriod.setText("");
//        txbAmount.setText("");
        txbPrice.setText("");
        btnAdd.setEnabled(true);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(false);
        Add = false;
        Change = false;
        image.setIcon(null);
        Disabled();
    }

    public boolean checkNull() {
        boolean kq = true;
        if (String.valueOf(this.txbID.getText()).length() == 0) {
            lblStatus.setText("Bạn chưa nhập mã cho sản phẩm!");
            return false;
        }
        if (String.valueOf(this.txbName.getText()).length() == 0) {
            lblStatus.setText("Bạn chưa nhập Tên sản phẩm!");
            return false;
        }
//        if(String.valueOf(this.txbWarrantyPeriod.getText()).length()==0){
//            lblStatus.setText("Bạn chưa nhập thời gian bảo hành!");
//            return false;
//        }
//        if(String.valueOf(this.txbAmount.getText()).length()==0){
//            lblStatus.setText("Bạn chưa nhập số lượng sản phẩm hiện có!");
//            return false;
//        }

        if (String.valueOf(this.txbPrice.getText()).length() == 0) {
            lblStatus.setText("Bạn chưa nhập giá cho sản phẩm!");
            return false;
        }
//        if(String.valueOf(this.txbLink.getText()).length()==0){
//            lblStatus.setText("Bạn chưa chọn ảnh cho sản phẩm!");
//            return false;
//        }
        return kq;
    }

    public boolean Check() {
        boolean kq = true;
        String sqlCheck = "SELECT * FROM Products";
        try {
            PreparedStatement pstCheck = conn.prepareStatement(sqlCheck);
            ResultSet rsCheck = pstCheck.executeQuery();
            while (rsCheck.next()) {
                if (this.txbID.getText().equals(rsCheck.getString("ID").toString().trim())) {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return kq;
    }

    private void addProduct() {
        if (checkNull()) {
            String sqlInsert = "INSERT INTO Products (ID,ClassifyID,Name,UnitID,Price,RegistDate) VALUES(?,?,?,?,?,?)";
            try {
                Classify classify = (Classify) cbxClassify.getSelectedItem();
                Unit unit = (Unit) cbxUnit.getSelectedItem();
                LocalDateTime now = LocalDateTime.now();
                Timestamp sqlDate = Timestamp.valueOf(now);
                String classifyId = classify.getId();
                String unitId = unit.getId();

                pst = conn.prepareStatement(sqlInsert);
                pst.setString(1, (String) txbID.getText());
                pst.setString(2, classifyId);
                pst.setString(3, (String) txbName.getText());
                pst.setString(4, unitId);
                pst.setBigDecimal(5, convertToMoney(txbPrice.getText()));
                pst.setTimestamp(6, sqlDate);
                pst.executeUpdate();
                lblStatus.setText("Thêm sản phẩm thành công!");
                Disabled();
                Refresh();
                Load(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void changeProduct() {
        int Click = tableProduct.getSelectedRow();
        TableModel model = tableProduct.getModel();

        if (checkNull()) {
            String sqlChange = "UPDATE Products SET ID=?,ClassifyID=?,Name=?,UnitID=?,Price=?,RegistDate=? WHERE ID='" + model.getValueAt(Click, 0).toString().trim() + "'";
            try {
                Classify classify = (Classify) cbxClassify.getSelectedItem();
                Unit unit = (Unit) cbxUnit.getSelectedItem();
                LocalDate now = LocalDate.now();
                Date sqlDate = Date.valueOf(now);
                String classifyId = classify.getId();
                String unitId = unit.getId();

                pst = conn.prepareStatement(sqlChange);
                pst.setString(1, (String) txbID.getText());
                pst.setString(2, classifyId);
                pst.setString(3, (String) txbName.getText());
                pst.setString(4, unitId);
                pst.setBigDecimal(5, convertToMoney(txbPrice.getText()));
                pst.setDate(6, sqlDate);
                pst.executeUpdate();
                lblStatus.setText("Lưu thay đổi thành công!");
                Disabled();
                Refresh();
                Load(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableProduct = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnBackHome = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        image = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txbID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbxClassify = new javax.swing.JComboBox<>();
        btnClassify = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txbName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txbPrice = new javax.swing.JTextField();
        cbxUnit = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableProduct.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Loại sản phẩm", "Tên sản phẩm", "Đơn vị", "Giá"
            }
        ));
        tableProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableProduct);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Trạng Thái");
        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Add.png"))); // NOI18N
        btnAdd.setText("Thêm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        jButton2.setText("In Danh Sách");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChange, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        jLabel10.setText("Cập Nhật Sản Phẩm");

        image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setText("Mã Sản Phẩm:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Loại Sản Phẩm:");

        cbxClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxClassifyActionPerformed(evt);
            }
        });

        btnClassify.setText("...");
        btnClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClassifyActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Tên Sản Phẩm:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Đơn Vị Tính:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel8.setText("Giá:");

        txbPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbPriceActionPerformed(evt);
            }
        });
        txbPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPriceKeyReleased(evt);
            }
        });

        cbxUnit.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbID, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel3))
                .addGap(39, 39, 39)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txbID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(cbxUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnClassify)
                                .addComponent(jLabel8))
                            .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnBackHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProductMouseClicked
        int Click = tableProduct.getSelectedRow();
        TableModel model = tableProduct.getModel();
        txbID.setText(model.getValueAt(Click, 0).toString());
        cbxClassify.setSelectedItem(getClassifyById(model.getValueAt(Click, 5).toString()));
        cbxUnit.setSelectedItem(getUnitById(model.getValueAt(Click, 6).toString()));
        txbName.setText(model.getValueAt(Click, 2).toString());
        String[] s1 = model.getValueAt(Click, 4).toString().split("\\s");
        txbPrice.setText(s1[0]);
        btnChange.setEnabled(true);
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_tableProductMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add = true;
        Enabled();
        LoadClassify();
//        LoadProducer();
//        LoadSingleTime();
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Add = false;
        Change = true;
        Enabled();
//        LoadClassify();
//        LoadProducer();
//        LoadSingleTime();
        btnAdd.setEnabled(false);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa sản phẩm hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM Products WHERE ID=? AND Name=? ";
            try {
                pst = conn.prepareStatement(sqlDelete);
                pst.setString(1, (String) txbID.getText());
                pst.setString(2, txbName.getText());
                pst.executeUpdate();
                lblStatus.setText("Xóa sản phẩm thành công!");
                Disabled();
                Refresh();
                Load(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (Add == true) {
            if (Check()) {
                addProduct();
            } else {
                lblStatus.setText("Không thể thêm sản phẩm vì mã sản phẩm bạn nhập đã tồn tại");
            }
        } else if (Change == true) {
            changeProduct();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

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

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        Refresh();
    }//GEN-LAST:event_btnRefreshActionPerformed

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

    private void btnClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClassifyActionPerformed

        Data data = new Data(detail);
        this.setVisible(false);
        data.setVisible(true);
    }//GEN-LAST:event_btnClassifyActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            JasperReport report = JasperCompileManager.compileReport("C:\\Users\\D.Thanh Trung\\Documents\\NetBeansProjects\\Quan Ly Cua Hang Mua Ban Thiet Bi Dien Tu\\src\\UserInterFace\\Menu.jrxml");

            JasperPrint print = JasperFillManager.fillReport(report, null, conn);

            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void cbxClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxClassifyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxClassifyActionPerformed

    private void txbPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPriceKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        NumberFormat format = NumberFormat.getNumberInstance();

        txbPrice.setText(cutChar(txbPrice.getText()));
        if (txbPrice.getText().equals("")) {
            return;
        } else {
            txbPrice.setText(formatter.format(convertedToNumbers(txbPrice.getText())));
        }
    }//GEN-LAST:event_txbPriceKeyReleased

    private void txbPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbPriceActionPerformed

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Product.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Product.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Product.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Product.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new Product(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnClassify;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<Classify> cbxClassify;
    private javax.swing.JComboBox<Unit> cbxUnit;
    private javax.swing.JLabel image;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTextField txbID;
    private javax.swing.JTextField txbName;
    private javax.swing.JTextField txbPrice;
    // End of variables declaration//GEN-END:variables
}
