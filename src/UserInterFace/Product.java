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

public class Product extends javax.swing.JFrame {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private String sql = "SELECT * FROM Products Order By Name ASC";
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
        LoadClassify();
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Load(String sql) {
        DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        model.setRowCount(0);
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"Mã Sản Phẩm", "Loại sản phẩm", "Tên sản phẩm", "đơn vị", "Giá", "ClassifyID"};
            DefaultTableModel modle = new DefaultTableModel(arr, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Làm cho toàn bộ bảng không thể chỉnh sửa
                    return false;
                }
            };

            while (rs.next()) {
                String productName = rs.getString("Name").trim();
                String price = decimalFormat.format(Double.parseDouble(rs.getString("Price").trim()));
                String classifyId = rs.getString("ClassifyID").trim();

                Vector vector = new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(getClassifyById(rs.getString("ClassifyID").trim()));
                vector.add(productName);
                vector.add(rs.getString("UnitName").trim());
                vector.add(price);
                vector.add(classifyId);
                modle.addRow(vector);
            }
            tableProduct.setModel(modle);
            TableColumn tableColumn5 = tableProduct.getColumnModel().getColumn(5);
            tableProduct.getColumnModel().removeColumn(tableColumn5);
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

    private void Enabled() {
        btnClassify.setEnabled(true);
        txbName.setEnabled(true);
        txbPrice.setEnabled(true);
        txbUnit.setEnabled(true);
        cbxClassify.setEnabled(true);
        cbxClassify.setEnabled(true);
    }

    public void Disabled() {
        btnClassify.setEnabled(false);
        txbName.setEnabled(false);
        txbPrice.setEnabled(false);
        cbxClassify.setEnabled(false);
        txbUnit.setEnabled(false);
    }

    public void Refresh() {
        txbName.setText("");
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
        if (String.valueOf(this.txbName.getText()).length() == 0) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập Tên sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (String.valueOf(this.txbUnit.getText()).length() == 0) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập đơn vị cho sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (String.valueOf(this.txbPrice.getText()).length() == 0) {
            JOptionPane.showMessageDialog(null, "Bạn chưa nhập giá cho sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return kq;
    }

    private void addProduct() {
        if (checkNull()) {
            String sqlInsert = "INSERT INTO Products (ClassifyID,Name,UnitName,Price,RegistDate) VALUES(?,?,?,?,?)";
            try {
                Classify classify = (Classify) cbxClassify.getSelectedItem();
                LocalDateTime now = LocalDateTime.now();
                Timestamp sqlDate = Timestamp.valueOf(now);
                String classifyId = classify.getId();

                pst = conn.prepareStatement(sqlInsert);
                pst.setString(1, classifyId);
                pst.setString(2, (String) txbName.getText());
                pst.setString(3, txbUnit.getText());
                pst.setBigDecimal(4, convertToMoney(txbPrice.getText()));
                pst.setTimestamp(5, sqlDate);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Thêm sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
            String sqlChange = "UPDATE Products set ClassifyID=?,Name=?,UnitName=?,Price=?,RegistDate=? WHERE ID='" + model.getValueAt(Click, 0).toString().trim() + "'";
            try {
                Classify classify = (Classify) cbxClassify.getSelectedItem();
                LocalDate now = LocalDate.now();
                Date sqlDate = Date.valueOf(now);
                String classifyId = classify.getId();

                pst = conn.prepareStatement(sqlChange);
                pst.setString(1, classifyId);
                pst.setString(2, (String) txbName.getText());
                pst.setString(3, txbUnit.getText());
                pst.setBigDecimal(4, convertToMoney(txbPrice.getText()));
                pst.setDate(5, sqlDate);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Lưu thay đổi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        jPanel1 = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        txbFind = new javax.swing.JTextField();
        btnFindProducer = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        image = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbxClassify = new javax.swing.JComboBox<>();
        btnClassify = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txbName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txbPrice = new javax.swing.JTextField();
        txbUnit = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(0, 0));
        setResizable(false);
        setSize(new java.awt.Dimension(0, 0));
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Save.png"))); // NOI18N
        btnSave.setText("Lưu");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnFindProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindProducer.setText("Tìm");
        btnFindProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindProducerActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Tìm Kiếm:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(182, 182, 182)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(186, 186, 186))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFindProducer)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChange, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFindProducer, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText("Tên Sản Phẩm:");

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
        jLabel3.setText("Loại Sản Phẩm:");

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

        txbUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbUnitKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addGap(37, 37, 37)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(81, 81, 81)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnClassify))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel7)
                                .addComponent(txbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel8)
                            .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1169, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBackHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProductMouseClicked
        int Click = tableProduct.getSelectedRow();
        TableModel model = tableProduct.getModel();
        cbxClassify.setSelectedItem(getClassifyById(model.getValueAt(Click, 5).toString()));
        txbUnit.setText(model.getValueAt(Click, 3).toString());
        txbName.setText(model.getValueAt(Click, 2).toString());
        String[] s1 = model.getValueAt(Click, 4).toString().split("\\s");
        txbPrice.setText(s1[0]);
        btnChange.setEnabled(true);
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_tableProductMouseClicked

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

    private void btnClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClassifyActionPerformed

        Data data = new Data(detail);
        this.setVisible(false);
        data.setVisible(true);
    }//GEN-LAST:event_btnClassifyActionPerformed

    private void cbxClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxClassifyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxClassifyActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (Add == true) {
            addProduct();

        } else if (Change == true) {
            changeProduct();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa sản phẩm hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            String sqlDelete = "DELETE FROM Products WHERE ID=? AND Name=? ";
            try {
                pst = conn.prepareStatement(sqlDelete);

                int click = tableProduct.getSelectedRow();
                TableModel model = tableProduct.getModel();
                pst.setString(1, model.getValueAt(click, 0).toString());
                pst.setString(2, txbName.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Xóa sản phẩm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                Disabled();
                Refresh();
                Load(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Add = false;
        Change = true;
        Enabled();
        btnAdd.setEnabled(false);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add = true;
        Enabled();
        LoadClassify();
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        Refresh();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnFindProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindProducerActionPerformed
        DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
        model.setRowCount(0);
        String sql = "Select * from Products where Name like ? Order By Name ASC";
        try ( PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, "%" + txbFind.getText() + "%");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String productName = rs.getString("Name").trim();
                String price = decimalFormat.format(Double.parseDouble(rs.getString("Price").trim()));
                String unitId = rs.getString("UnitID").trim();
                String classifyId = rs.getString("ClassifyID").trim();

                Vector<String> vector = new Vector<>();
                vector.add(rs.getString("ID").trim());
                vector.add(getClassifyById(rs.getString("ClassifyID").trim()).getClassify());
                vector.add(productName);
                vector.add(getUnitById(unitId).getUnit());
                vector.add(price);
                vector.add(classifyId);
                vector.add(unitId);
                model.addRow(vector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnFindProducerActionPerformed

    private void txbUnitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbUnitKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbUnitKeyReleased

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
    private javax.swing.JButton btnFindProducer;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<Classify> cbxClassify;
    private javax.swing.JLabel image;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTextField txbFind;
    private javax.swing.JTextField txbName;
    private javax.swing.JTextField txbPrice;
    private javax.swing.JTextField txbUnit;
    // End of variables declaration//GEN-END:variables
}
