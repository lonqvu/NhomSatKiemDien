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
import jdk.nashorn.internal.codegen.types.NumericType;

/**
 *
 * @author Admin
 */
public class ProductDialog extends javax.swing.JDialog {

    private Detail detail;
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    String sql = "select * from products order by Name ASC";
    private String maSP = "";
    private String tenSP = "";
    private String maHD = "";
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    /**
     * Creates new form ProductDialog
     */
    public ProductDialog(java.awt.Frame parent, boolean modal, String maHd) {
        super(parent, "Thêm sản phẩm", modal);
        initComponents();
        connection();
        Load();
        maHD = maHd;
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
        DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        model.setRowCount(0);

        int stt = 0;
        try ( PreparedStatement pst = conn.prepareStatement(sql);  ResultSet rs = pst.executeQuery()) {
            String[] arr = {"STT", "Mã hàng", "Tên hàng", "Đơn vị", "Giá"};
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
                stt++;

                Vector vector = new Vector();
                vector.add(stt);
                vector.add(rs.getString("ID").trim());
                vector.add(productName);
                vector.add(rs.getString("UnitName"));
                vector.add(price);

                modle.addRow(vector);
            }
            tableProduct.setModel(modle);
            TableColumn tableColumn1 = tableProduct.getColumnModel().getColumn(1);
            tableProduct.getColumnModel().removeColumn(tableColumn1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateTableDefault(String name) {
        DefaultTableModel model = (DefaultTableModel) tableProduct.getModel();

        String query = "SELECT * FROM Products WHERE Name LIKE ?";

        try ( PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            // Sử dụng Vector để lưu trữ các hàng mới
            Vector<Vector<Object>> rows = new Vector<>();

            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            int stt = 0;

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                stt++;
                String productName = resultSet.getString("Name").trim();
                String price = decimalFormat.format(Double.parseDouble(resultSet.getString("Price").trim()));

                row.add(stt);
                row.add(resultSet.getString("ID").trim());
                row.add(productName);
                row.add(resultSet.getString("UnitName"));
                row.add(price);

                rows.add(row);
            }

            // Cập nhật bảng trên EDT
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0); // Xóa tất cả các hàng hiện tại
                for (Vector<Object> row : rows) {
                    model.addRow(row); // Thêm các hàng mới
                }
                tableProduct.setModel(model);
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
        txbAmount = new javax.swing.JTextField();
        txbPrice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProduct = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txbSoTam = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txbHeSo = new javax.swing.JTextField();
        txbTotalAmount = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txbFind = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txbIntoMoney = new javax.swing.JTextField();
        btnExit = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();
        txbUnit = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));
        setForeground(new java.awt.Color(102, 102, 255));
        setLocation(new java.awt.Point(200, 300));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel1.setText("Nhập tên sản phẩm");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel3.setText("Số Lượng/ Mét:");

        txbAmount.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbAmountupdateTotal(evt);
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

        txbPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPriceKeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel7.setText("Tổng Số Lượng: ");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel4.setText("Đơn Giá: ");

        tableProduct.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tên hàng", "Đơn vị", "Giá"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableProduct.setColumnSelectionAllowed(true);
        tableProduct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableProduct.setShowGrid(true);
        tableProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableProduct);
        tableProduct.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tableProduct.getColumnModel().getColumnCount() > 0) {
            tableProduct.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableProduct.getColumnModel().getColumn(0).setMaxWidth(50);
            tableProduct.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableProduct.getColumnModel().getColumn(2).setMaxWidth(100);
            tableProduct.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableProduct.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel5.setText("Số Tấm: ");

        txbSoTam.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbSoTamupdateTotal(evt);
            }
        });
        txbSoTam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbSoTamActionPerformed(evt);
            }
        });
        txbSoTam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbSoTamKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setText("Hệ Số:");

        txbHeSo.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbHeSoupdateTotal(evt);
            }
        });
        txbHeSo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbHeSoActionPerformed(evt);
            }
        });
        txbHeSo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbHeSoKeyReleased(evt);
            }
        });

        txbTotalAmount.setEnabled(false);
        txbTotalAmount.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbTotalAmountupdateTotal(evt);
            }
        });
        txbTotalAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbTotalAmountActionPerformed(evt);
            }
        });
        txbTotalAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbTotalAmountKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel8.setText("Đơn Vị: ");

        txbFind.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbFindKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel9.setText("Thành Tiền:");

        txbIntoMoney.setEnabled(false);
        txbIntoMoney.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txbIntoMoneyupdateTotal(evt);
            }
        });
        txbIntoMoney.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txbIntoMoneyActionPerformed(evt);
            }
        });
        txbIntoMoney.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbIntoMoneyKeyReleased(evt);
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

        btnSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Save.png"))); // NOI18N
        btnSave1.setText("Lưu");
        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });

        txbUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbUnitKeyReleased(evt);
            }
        });

        jButton1.setText("Làm mới");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 887, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txbAmount)
                                            .addComponent(txbHeSo)
                                            .addComponent(txbPrice)
                                            .addComponent(txbTotalAmount)
                                            .addComponent(txbSoTam, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txbUnit)))
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txbFind, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbSoTam, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txbHeSo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(txbTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txbAmountupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbAmountupdateTotal
        try {
            int quantity = Integer.parseInt(txbAmount.getText());
            double total = Double.parseDouble(txbPrice.getText()) * quantity;
            txbIntoMoney.setText(moneyDis(String.valueOf(total)));
        } catch (NumberFormatException ex) {
            // Nếu không phải là số, không làm gì cả
        }
    }//GEN-LAST:event_txbAmountupdateTotal

    private void txbAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbAmountActionPerformed

    private void txbAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbAmountKeyReleased
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String text = txbAmount.getText().replaceAll("[^0-9.]", "");
        txbAmount.setText(text);
        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull)) {
            try {
                BigDecimal tongSl = getTongSoLuong();
                txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(tongSl))));
                txbTotalAmount.setText(decimalFormat.format(tongSl));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, checkNull, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txbAmountKeyReleased

    private String moneyDis(String money) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String price = formatter.format(Double.parseDouble(money));
        return price;
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

    private double convertedToNumbers(String s) {
        String number = "";
        String[] array = s.replace(",", " ").split("\\s");
        for (String i : array) {
            number = number.concat(i);
        }
        if (StringUtils.isEmpty(number)) {
            return 0;
        }
        return Double.parseDouble(number);
    }

    private void txbPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPriceKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");

        if (txbPrice.getText().equals("")) {
            return;
        } else {
            txbPrice.setText(formatter.format(convertedToNumbers(txbPrice.getText())));
        }

        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull)) {
            try {
                BigDecimal tongSoLuong = getTongSoLuong();
                txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(tongSoLuong))));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, checkNull, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txbPriceKeyReleased

    private void tableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProductMouseClicked
        int click = tableProduct.getSelectedRow();
        TableModel model = tableProduct.getModel();

        String[] price = model.getValueAt(click, 4).toString().split("\\s");
        txbUnit.setText(model.getValueAt(click, 3).toString());
        txbPrice.setText(price[0]);
        txbHeSo.setText("0");
        txbSoTam.setText("0");
//        txbAmount.setText("1");
        maSP = model.getValueAt(click, 1).toString();
        tenSP = model.getValueAt(click, 2).toString();
        txbFind.setText(model.getValueAt(click, 2).toString());
        updateTableDefault(model.getValueAt(click, 2).toString());

        if (StringUtils.isEmpty(txbAmount.getText()) == false) {
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(txbAmount.getText()));
            BigDecimal price1 = convertToMoney(txbPrice.getText());

            txbIntoMoney.setText(moneyDis(amount.multiply(price1).toString()));
            txbTotalAmount.setText(decimalFormat.format(getTongSoLuong()));
        }
    }//GEN-LAST:event_tableProductMouseClicked

    private void txbSoTamupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbSoTamupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbSoTamupdateTotal

    private void txbSoTamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbSoTamActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txbSoTamActionPerformed

    private void txbSoTamKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbSoTamKeyReleased
        String text = txbSoTam.getText().replaceAll("[^0-9]", "");
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        DecimalFormat df = new DecimalFormat("#");
        txbSoTam.setText(df.format(convertedToNumbers(text)));
        if (text.equals("0") == false) {
            txbHeSo.setText("1.08");
        }

        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull)) {
            try {
                BigDecimal tongSoLuong = getTongSoLuong();
                txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(tongSoLuong))));
                txbTotalAmount.setText(decimalFormat.format(tongSoLuong));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, checkNull, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txbSoTamKeyReleased

    private void txbHeSoupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbHeSoupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbHeSoupdateTotal

    private void txbHeSoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbHeSoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbHeSoActionPerformed

    private void txbHeSoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbHeSoKeyReleased
        String text = txbHeSo.getText().replaceAll("[^0-9.]", "");
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        txbHeSo.setText(text);

        String checkNull = checkNull();
        if (StringUtils.isEmpty(checkNull)) {
            try {
                BigDecimal tongSoLuong = getTongSoLuong();
                txbTotalAmount.setText(decimalFormat.format(tongSoLuong));
                txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(tongSoLuong))));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, checkNull, "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txbHeSoKeyReleased

    private void txbTotalAmountupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbTotalAmountupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbTotalAmountupdateTotal

    private void txbTotalAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbTotalAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbTotalAmountActionPerformed

    private void txbTotalAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbTotalAmountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbTotalAmountKeyReleased

    private void txbIntoMoneyupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbIntoMoneyupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyupdateTotal

    private void txbIntoMoneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbIntoMoneyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyActionPerformed

    private void txbIntoMoneyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbIntoMoneyKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyKeyReleased

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed

        if (StringUtils.isEmpty(maSP)) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm!", "Error", JOptionPane.WARNING_MESSAGE);
        } else {
            String checkNull = checkNull();
            if (StringUtils.isEmpty(checkNull)) {
                String sqlInsert = "INSERT INTO Bill (Amount, IntoMoney, ProductID, OrderID, RegistDate, UnitName, Price, HeSo, SoTam, TongSoLuong, ProductName) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                try {
                    pst = conn.prepareStatement(sqlInsert);
                    double amount = Double.parseDouble(txbAmount.getText());
                    pst.setBigDecimal(1, BigDecimal.valueOf(amount));
                    pst.setBigDecimal(2, convertToMoney(txbIntoMoney.getText()));
                    pst.setString(3, maSP);
                    pst.setString(4, maHD);
                    LocalDateTime now = LocalDateTime.now();
                    Timestamp sqlDate = Timestamp.valueOf(now);
                    pst.setTimestamp(5, sqlDate);
                    pst.setString(6, txbUnit.getText());
                    pst.setBigDecimal(7, convertToMoney(txbPrice.getText()));
                    pst.setBigDecimal(8, BigDecimal.valueOf(Double.parseDouble(txbHeSo.getText())));
                    pst.setInt(9, Integer.valueOf(txbSoTam.getText()));
                    pst.setBigDecimal(10, BigDecimal.valueOf(Double.parseDouble(txbTotalAmount.getText())));
                    pst.setString(11, tenSP);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Lưu thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
            else{
                JOptionPane.showMessageDialog(null, checkNull, "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnSave1ActionPerformed

    private void txbFindKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbFindKeyReleased
        updateTableDefault(txbFind.getText());
    }//GEN-LAST:event_txbFindKeyReleased

    private void txbUnitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbUnitKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbUnitKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Load();
    }//GEN-LAST:event_jButton1ActionPerformed

    private String checkNull() {
        String mess = "";
        if (txbAmount.getText().isBlank() || txbAmount.getText().isEmpty()) {
            mess = "Vui lòng nhập số lượng!";
        } else if (txbSoTam.getText().isBlank() || txbSoTam.getText().isEmpty()) {
            mess = "Vui lòng nhập số tấm!";
        } else if (txbHeSo.getText().isBlank() || txbHeSo.getText().isEmpty()) {
            mess = "Vui lòng nhập hệ số!";
        } else if (txbPrice.getText().isBlank() || txbPrice.getText().isEmpty()) {
            mess = "Vui lòng nhập giá sản phẩm!";
        }

        return mess;
    }

    private BigDecimal getTongSoLuong() {
        BigDecimal tongSoLuong = BigDecimal.ZERO;
        BigDecimal soLuong = BigDecimal.valueOf(Double.parseDouble(txbAmount.getText()));
        BigDecimal soTam = BigDecimal.valueOf(Double.parseDouble(txbSoTam.getText()));
        BigDecimal heSo = BigDecimal.valueOf(Double.parseDouble(txbHeSo.getText()));

        if (soTam.compareTo(BigDecimal.ZERO) == 0 && heSo.compareTo(BigDecimal.ZERO) == 0) {
            tongSoLuong = soLuong;
        } else {
            tongSoLuong = soLuong.multiply(soTam).multiply(heSo);
        }
        return tongSoLuong;
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
            java.util.logging.Logger.getLogger(ProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProductDialog dialog = new ProductDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTextField txbAmount;
    private javax.swing.JTextField txbFind;
    private javax.swing.JTextField txbHeSo;
    private javax.swing.JTextField txbIntoMoney;
    private javax.swing.JTextField txbPrice;
    private javax.swing.JTextField txbSoTam;
    private javax.swing.JTextField txbTotalAmount;
    private javax.swing.JTextField txbUnit;
    // End of variables declaration//GEN-END:variables
}
