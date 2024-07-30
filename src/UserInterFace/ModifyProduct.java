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
public class ModifyProduct extends javax.swing.JDialog {

    private Detail detail;
    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    String sql = "select * from products";
    String maBill = "";
    NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    /**
     * Creates new form ProductDialog
     */
    public ModifyProduct(java.awt.Frame parent, boolean modal, BigDecimal amount, int soTam, BigDecimal heSo, String gia, BigDecimal tongSoLuong, String tongTien, String billId) {

        super(parent, "Chỉnh sửa", modal);
        DecimalFormat formatter = new DecimalFormat("#.###");
        initComponents();
        connection();
        txbAmount.setText(formatter.format(amount));
        txbSoTam.setText(String.valueOf(soTam));
        txbHeSo.setText(formatter.format(heSo));
        txbPrice.setText(gia);
        txbTotalAmount.setText(formatter.format(tongSoLuong));
        txbIntoMoney.setText(tongTien);
        maBill = billId;
        setLocationRelativeTo(null);
    }

    private void connection() {
        try {
            conn = DatabaseUtil.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
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

        jLabel3 = new javax.swing.JLabel();
        txbAmount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txbSoTam = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txbHeSo = new javax.swing.JTextField();
        txbTotalAmount = new javax.swing.JTextField();
        btnExit = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();
        txbIntoMoney = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txbPrice = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 255));
        setForeground(new java.awt.Color(102, 102, 255));
        setLocation(new java.awt.Point(200, 300));

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

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel7.setText("Tổng Số Lượng: ");

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

        txbTotalAmount.setEditable(false);
        txbTotalAmount.setBackground(new java.awt.Color(255, 255, 255));
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

        txbIntoMoney.setEditable(false);
        txbIntoMoney.setBackground(new java.awt.Color(255, 255, 255));
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

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel9.setText("Thành Tiền:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel4.setText("Đơn Giá: ");

        txbPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPriceKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(31, 31, 31)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(jLabel9)
                                .addComponent(jLabel4))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txbIntoMoney, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                .addComponent(txbHeSo)
                                .addComponent(txbSoTam)
                                .addComponent(txbAmount)
                                .addComponent(txbTotalAmount)
                                .addComponent(txbPrice)))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbSoTam, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbHeSo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txbTotalAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txbAmountupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbAmountupdateTotal

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
        return Double.parseDouble(number);
    }

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
            txbHeSo.setText("1");
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
                txbIntoMoney.setText(moneyDis(String.valueOf(convertToMoney(txbPrice.getText()).multiply(tongSoLuong).setScale(0, BigDecimal.ROUND_HALF_UP))));

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

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        String sqlChange = "UPDATE Bill SET Amount=?, HeSo =?, SoTam =?,IntoMoney=?, price =?, TongSoLuong=? WHERE ID='" + maBill + "'";
        try {
            double amount = Double.parseDouble(txbAmount.getText());
            int soTam = Integer.parseInt(txbSoTam.getText());
            double heSo = Double.parseDouble(txbHeSo.getText());

            pst = conn.prepareStatement(sqlChange);
            pst.setBigDecimal(1, BigDecimal.valueOf(amount));
            pst.setBigDecimal(2, BigDecimal.valueOf(heSo));
            pst.setBigDecimal(3, BigDecimal.valueOf(soTam));
            pst.setBigDecimal(4, convertToMoney(txbIntoMoney.getText()));
            pst.setBigDecimal(5, convertToMoney(txbPrice.getText()));
            pst.setBigDecimal(6, convertToMoney(txbTotalAmount.getText()));
            pst.executeUpdate();
            this.dispose();
            JOptionPane.showMessageDialog(null, "Lưu thay đổi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSave1ActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void txbIntoMoneyupdateTotal(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txbIntoMoneyupdateTotal
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyupdateTotal

    private void txbIntoMoneyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txbIntoMoneyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyActionPerformed

    private void txbIntoMoneyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbIntoMoneyKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txbIntoMoneyKeyReleased

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
            java.util.logging.Logger.getLogger(ModifyProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModifyProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModifyProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModifyProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ModifyProduct dialog = new ModifyProduct(new javax.swing.JFrame(), true, BigDecimal.ZERO, 0, BigDecimal.ZERO, null, BigDecimal.ZERO, null, null);
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txbAmount;
    private javax.swing.JTextField txbHeSo;
    private javax.swing.JTextField txbIntoMoney;
    private javax.swing.JTextField txbPrice;
    private javax.swing.JTextField txbSoTam;
    private javax.swing.JTextField txbTotalAmount;
    // End of variables declaration//GEN-END:variables
}
