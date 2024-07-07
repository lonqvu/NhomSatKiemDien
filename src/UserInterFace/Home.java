package UserInterFace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Home extends javax.swing.JFrame implements Runnable {

    private Detail detail;
    private Thread thread;

    public Home(Detail d) {
        initComponents();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        lblSoftwareName.setForeground(Color.GREEN);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail = new Detail(d);
        //Start();
    }

    private void Start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel(){
            ImageIcon icon = new ImageIcon("src/Image/Background.png");
            public void paintComponent(Graphics g){

                Dimension d = getSize();
                g.drawImage(icon.getImage(), 0, 0, d.width, d.height, this);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        lblSoftwareName = new javax.swing.JLabel();
        btnProduct = new javax.swing.JButton();
        btnData = new javax.swing.JButton();
        btnAccount = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnLogOut = new javax.swing.JButton();
        btnSale = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnAccount1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Giao Diện Hệ Thống");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblSoftwareName.setFont(new java.awt.Font("Times New Roman", 0, 38)); // NOI18N
        lblSoftwareName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSoftwareName.setText("Phần Mềm Bán Hàng Đại Lý Nhôm Sắt Kiểm Diên");

        btnProduct.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Product.png"))); // NOI18N
        btnProduct.setText("Cập Nhật Sản Phẩm");
        btnProduct.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProduct.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnProduct.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductActionPerformed(evt);
            }
        });

        btnData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Database.png"))); // NOI18N
        btnData.setText("Cập Nhật Danh Mục SP");
        btnData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnData.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataActionPerformed(evt);
            }
        });

        btnAccount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Account.png"))); // NOI18N
        btnAccount.setText("Cập nhật khách hàng");
        btnAccount.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAccount.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAccount.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccountActionPerformed(evt);
            }
        });

        btnOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Cart.png"))); // NOI18N
        btnOrders.setText("Cập Nhật Đơn Hàng");
        btnOrders.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOrders.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnOrders.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });

        btnLogOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/LogOut.png"))); // NOI18N
        btnLogOut.setText("Đăng Xuất");
        btnLogOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogOut.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnLogOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        btnSale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Sale.png"))); // NOI18N
        btnSale.setText("Bán Hàng");
        btnSale.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSale.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSale.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaleActionPerformed(evt);
            }
        });

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Exit.png"))); // NOI18N
        btnExit.setText("Thoát");
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnAccount1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAccount1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Change PasssWord.png"))); // NOI18N
        btnAccount1.setText("Cập Nhật Tài Khoản");
        btnAccount1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAccount1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAccount1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAccount1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccount1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSoftwareName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSale, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAccount))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnProduct, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAccount1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(128, 128, 128)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnData, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogOut, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(114, 114, 114)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(72, 72, 72)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAccount1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblSoftwareName)
                        .addGap(69, 69, 69)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnProduct, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnOrders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(btnAccount))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnLogOut, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addGap(144, 144, 144))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        int lick = JOptionPane.showConfirmDialog(null, "Bạn Có Muốn Thoát Khỏi Chương Trình Hay Không?", "Thông Báo", 2);
        if (lick == JOptionPane.OK_OPTION) {
            System.exit(0);
        } else {
            if (lick == JOptionPane.CANCEL_OPTION) {
                this.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn đăng xuất tài khoản khỏi hệ thống hay không?", "Thông Báo", 2);
        if (Click == JOptionPane.YES_OPTION) {
            Login login = new Login();
            this.setVisible(false);
            login.setVisible(true);
        }
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void btnAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccountActionPerformed
        Customers customers = new Customers(detail);
        this.setVisible(false);
        customers.setVisible(true);
    }//GEN-LAST:event_btnAccountActionPerformed

    private void btnDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDataActionPerformed
        Data data = new Data(detail);
        this.setVisible(false);
        data.setVisible(true);
    }//GEN-LAST:event_btnDataActionPerformed

    private void btnProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductActionPerformed
        Product product = new Product(detail);
        this.setVisible(false);
        product.setVisible(true);
    }//GEN-LAST:event_btnProductActionPerformed

    private void btnOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdersActionPerformed
        OrderForm orderForm = new OrderForm(detail);
        this.setVisible(false);
        orderForm.setVisible(true);
    }//GEN-LAST:event_btnOrdersActionPerformed

    private void btnSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaleActionPerformed
        Sale sale = new Sale(detail);
        this.setVisible(false);
        sale.setVisible(true);
    }//GEN-LAST:event_btnSaleActionPerformed

    private void btnAccount1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccount1ActionPerformed
        Account sale = new Account(detail);
        this.setVisible(false);
        sale.setVisible(true);
    }//GEN-LAST:event_btnAccount1ActionPerformed

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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail = new Detail();
                new Home(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccount;
    private javax.swing.JButton btnAccount1;
    private javax.swing.JButton btnData;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnProduct;
    private javax.swing.JButton btnSale;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblSoftwareName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        long FPS = 80;
        long period = 1000 * 1000000 / FPS;
        long beginTime, sleepTime;

        beginTime = System.nanoTime();
        while (true) {

            long deltaTime = System.nanoTime() - beginTime;
            sleepTime = period - deltaTime;
            try {
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1000000);
                } else {
                    Thread.sleep(period / 2000000);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            beginTime = System.nanoTime();
        }
    }
}
