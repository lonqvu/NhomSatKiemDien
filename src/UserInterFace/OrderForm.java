
package UserInterFace;

import java.awt.Color;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class OrderForm extends javax.swing.JFrame {

    private static Connection conn = null;//,connData=null,connProduct;  
    private static PreparedStatement pst = null;  
    private static ResultSet rs = null;
    
    private boolean Add=false,Change=false;
    private String sql = "SELECT * FROM Orders";
    
    private Detail detail;
    
    public OrderForm(Detail d) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        connection();
        detail=new Detail(d);
        Load(sql);
        Disabled();
        lblStatus.setForeground(Color.red);
    }
    
    private void connection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn=DriverManager.getConnection("jdbc:sqlserver://LONGVIPPRONO1\\SQLEXPRESS01:1433;databaseName=CuaHangDienTu;user=sa;password=123");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void Enabled(){
        txbID.setEnabled(true);
        cbxClassify.setEnabled(true);
        txbName.setEnabled(true);
        txbAddress.setEnabled(true);
        txbPhone.setEnabled(true);
        cbxClassify.setEnabled(true);
        //txbAmount.setEnabled(true);
        txbDate.setEnabled(true);
        cbxPaymentMethods.setEnabled(true);
        btnProduct.setEnabled(true);
        lblStatus.setText("Trạng Thái!");
    }
    
    private void Disabled(){
        txbID.setEnabled(false);
        cbxClassify.setEnabled(false);
        txbName.setEnabled(false);
        txbAddress.setEnabled(false);
        txbPhone.setEnabled(false);
        cbxProduct.setEnabled(false);
        btnProduct.setEnabled(false);
        txbAmount.setEnabled(false);
        txbPrice.setEnabled(false);
        txbWarrantyPeriod.setEnabled(false);
        txbIntoMoney.setEnabled(false);
        txbDate.setEnabled(false);
        cbxPaymentMethods.setEnabled(false);
    }
    
    public void Load(String sql){
        tableOrder.removeAll();
        try{
            String [] arr={"Mã Đơn Hàng","Khách Hàng","Địa Chỉ","Số Điện Thoại","Sản Phẩm","Số Lượng","Giá","Bảo Hành","Thành Tiền","Ngày Đặt","Thanh Toán"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("Customer").trim());
                vector.add(rs.getString("Address").trim());
                vector.add(rs.getString("Phone").trim());
                vector.add(rs.getString("Product").trim());
                vector.add(rs.getInt("Amount"));
                vector.add(rs.getString("Price").trim());
                vector.add(rs.getString("WarrantyPeriod").trim());
                vector.add(rs.getString("intoMoney").trim());
                vector.add(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("Date")));
                vector.add(rs.getString("PaymentMethods").trim());
                modle.addRow(vector);
            }
            tableOrder.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void LoadClassify(){
        String sql = "SELECT * FROM Classify";
        try {
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                this.cbxClassify.addItem(rs.getString("Classify").trim());
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
    }
    
    private void LoadPaymentMethods(){
        cbxPaymentMethods.removeAllItems();
        cbxPaymentMethods.addItem("Chuyển khoản");
        cbxPaymentMethods.addItem("Nhận hàng trả tiền");
    }
    
    private void Refresh(){
        Add=false;
        Change=false;
        txbName.setText("");
        txbAddress.setText("");
        txbPhone.setText("");
        txbID.setText("");
        txbAmount.setText("");
        txbPrice.setText("");
        txbWarrantyPeriod.setText("");
        txbIntoMoney.setText("");
        ((JTextField)txbDate.getDateEditor().getUiComponent()).setText("");
        cbxClassify.removeAllItems();
        cbxProduct.removeAllItems();
        cbxPaymentMethods.removeAllItems();
        btnAdd.setEnabled(true);
        btnDelete.setEnabled(false);
        btnChange.setEnabled(false);
        btnSave.setEnabled(false);
        Disabled();
    }
    
    private void addOrder(){
        if(checkNull()){
            String sqlInsert="INSERT INTO Orders (ID,Customer,Address,Phone,Product,Amount,Price,WarrantyPeriod,intoMoney,Date,PaymentMethods) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            try{
                pst=conn.prepareStatement(sqlInsert);
                
                pst.setString(1, this.txbID.getText());
                pst.setString(2, this.txbName.getText());
                pst.setString(3, txbAddress.getText());
                pst.setString(4,txbPhone.getText());
                pst.setString(5, String.valueOf(this.cbxProduct.getSelectedItem()));
                pst.setInt(6, Integer.parseInt(this.txbAmount.getText()));
                pst.setString(7, txbPrice.getText());
                pst.setString(8,this.txbWarrantyPeriod.getText());
                pst.setString(9,this.txbIntoMoney.getText());
                pst.setDate(10, new java.sql.Date(txbDate.getDate().getTime()));
                pst.setString(11, String.valueOf(this.cbxPaymentMethods.getSelectedItem()));
                pst.executeUpdate();
                lblStatus.setText("Thêm Đơn đặt hàng thành công!");
                Disabled();
                Refresh();
                Load(sql);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    private void changeOrder(){
        int Click=tableOrder.getSelectedRow();
        TableModel model=tableOrder.getModel();
        
        if(checkNull()){
            String sqlChange="UPDATE Orders SET ID=?,Customer=?,Address=?,Phone=?,Product=?,Amount=?,Price=?,WarrantyPeriod=?,intoMoney=?,Date=?,PaymentMethods=? WHERE ID='"+model.getValueAt(Click,0).toString().trim()+"'";
            try{
                pst=conn.prepareStatement(sqlChange);
                
                pst.setString(1, this.txbID.getText());
                pst.setString(2, this.txbName.getText());
                pst.setString(3, txbAddress.getText());
                pst.setString(4,txbPhone.getText());
                pst.setString(5, String.valueOf(this.cbxProduct.getSelectedItem()));
                pst.setInt(6, Integer.parseInt(this.txbAmount.getText()));
                pst.setString(7, txbPrice.getText());
                pst.setString(8,this.txbWarrantyPeriod.getText());
                pst.setString(9,this.txbIntoMoney.getText());
                pst.setDate(10, new java.sql.Date(txbDate.getDate().getTime()));
                pst.setString(11, String.valueOf(this.cbxPaymentMethods.getSelectedItem()));
                pst.executeUpdate();
                lblStatus.setText("Lưu thay đổi thành công!");
                Disabled();
                Refresh();
                Load(sql);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    public boolean Check(){
        boolean kq=true;
        String sqlCheck="SELECT * FROM Orders";
        try{
            pst=conn.prepareStatement(sqlCheck);
            rs=pst.executeQuery();
            while(rs.next()){
                if(this.txbID.getText().equals(rs.getString("ID").toString().trim())){
                    return false;                                           
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return kq;
    }
    
    public boolean checkNull(){
        boolean kq=true;
        if(String.valueOf(this.txbName.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập Tên khách hàng!");
            return false;
        }
        if(String.valueOf(this.txbAddress.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập địa chỉ khách hàng!");
            return false;
        }
        if(String.valueOf(this.txbPhone.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập số điện thoại khách hàng!");
            return false;
        }
        if(String.valueOf(this.cbxClassify.getSelectedItem()).length()==0){
            lblStatus.setText("Bạn chưa chọn loại linh kiện!");
            return false;
        }
        if(String.valueOf(this.cbxProduct.getSelectedItem()).length()==0){
            lblStatus.setText("Bạn chưa chọn sản phẩm!");
            return false;
        }
        if(String.valueOf(this.txbAmount.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập số lượng khách hàng đặt!");
            return false;
        }
        if(String.valueOf(((JTextField)this.txbDate.getDateEditor().getUiComponent()).getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập ngày đặt hàng!");
            return false;
        }
        return kq;
    }
    
    private double convertedToNumbers(String s){
        String number="";
        String []array=s.replace(","," ").split("\\s");
        for(String i:array){
            number=number.concat(i);
        }
        return Double.parseDouble(number);
    }
    
    private String cutChar(String arry){
        return arry.replaceAll("\\D+","");
    }
    
    private void loadData(){
        cbxClassify.removeAllItems();
        String sql = "SELECT * FROM Products where Name=?";
        try {
            pst=conn.prepareStatement(sql);
            pst.setString(1, this.cbxProduct.getSelectedItem().toString());
            rs=pst.executeQuery();
            while(rs.next()){
                cbxClassify.addItem(rs.getString("Classify").trim());
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableOrder = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txbID = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txbName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txbAddress = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txbPhone = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbxClassify = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbxProduct = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txbAmount = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txbPrice = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txbWarrantyPeriod = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txbDate = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        txbIntoMoney = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbxPaymentMethods = new javax.swing.JComboBox<>();
        btnProduct = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableOrderMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableOrder);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Cập Nhật Đơn Đặt Hàng");

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Trạng Thái");

        btnBackHome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBackHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnBackHome.setText("Hệ Thống");
        btnBackHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackHomeMouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel15.setText("Mã Đơn Hàng:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Tên Khách Hàng:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Địa Chỉ:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Số Điện Thoại:");

        txbPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPhoneKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Loại Linh Kiện:");

        cbxClassify.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbxClassifyPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setText("Tên Linh Kiện:");
        jLabel7.setToolTipText("");

        cbxProduct.setMaximumRowCount(50);
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

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Số Lượng:");

        txbAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbAmountKeyReleased(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setText("Giá:");
        jLabel10.setToolTipText("");

        txbPrice.setEnabled(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("Thời Gian Bảo Hành:");

        txbWarrantyPeriod.setEnabled(false);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel14.setText("Ngày Đặt:");

        txbDate.setDateFormatString("dd/MM/yyyy");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel12.setText("Thành Tiền:");

        txbIntoMoney.setEnabled(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel13.setText("Thanh Toán:");

        btnProduct.setText("...");
        btnProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txbID, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(txbName)
                    .addComponent(txbAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbxProduct, 0, 130, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbxClassify, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txbAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(txbPrice)
                    .addComponent(txbWarrantyPeriod))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txbIntoMoney)
                    .addComponent(txbDate, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(cbxPaymentMethods, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txbID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txbPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(txbAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cbxClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(txbIntoMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cbxProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txbWarrantyPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cbxPaymentMethods, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProduct))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
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

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        jButton1.setText("In Danh Sách");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnBackHome)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBackHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackHomeMouseClicked
        if(this.detail.getUser().toString().toString().equals("Admin")){
            Home home=new Home(detail);
            this.setVisible(false);
            home.setVisible(true);
        }
        else{
            HomeUser home=new HomeUser(detail);
            this.setVisible(false);
            home.setVisible(true);
        }
    }//GEN-LAST:event_btnBackHomeMouseClicked

    private void cbxClassifyPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxClassifyPopupMenuWillBecomeInvisible
        cbxProduct.removeAllItems();
        String sql = "SELECT * FROM Products where Classify=?";
        try {
            pst=conn.prepareStatement(sql);
            pst.setString(1, this.cbxClassify.getSelectedItem().toString());
            rs=pst.executeQuery();
            while(rs.next()){
                this.cbxProduct.addItem(rs.getString("Name").trim());
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
        if(cbxProduct.getItemCount()==0){
            cbxProduct.setEnabled(false);
            txbAmount.setEnabled(false);
            txbAmount.setText("");
            txbPrice.setText("");
            txbWarrantyPeriod.setText("");
            txbIntoMoney.setText("");
        }
        else cbxProduct.setEnabled(true);
    }//GEN-LAST:event_cbxClassifyPopupMenuWillBecomeInvisible

    private void cbxProductPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxProductPopupMenuWillBecomeInvisible
        String sql = "SELECT * FROM Products where Name=?";
        try {
            pst=conn.prepareStatement(sql);
            pst.setString(1, this.cbxProduct.getSelectedItem().toString());
            rs=pst.executeQuery();
            while(rs.next()){
                txbAmount.setEnabled(true);
                txbPrice.setText(rs.getString("Price").trim());
                txbWarrantyPeriod.setText(String.valueOf(rs.getInt("WarrantyPeriod"))+" "+rs.getString("SingleTime").trim());
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
    }//GEN-LAST:event_cbxProductPopupMenuWillBecomeInvisible

    private void txbAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbAmountKeyReleased
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        txbAmount.setText(cutChar(txbAmount.getText()));
        if(txbAmount.getText().equals("")){
            String []s=txbPrice.getText().split("\\s");
            txbIntoMoney.setText("0"+" "+s[1]);
        }
        else{
            int soluong=Integer.parseInt(txbAmount.getText());
            
            String []s=txbPrice.getText().split("\\s");
            
            txbIntoMoney.setText(formatter.format(convertedToNumbers(s[0])*soluong)+" "+s[1]);
        }
    }//GEN-LAST:event_txbAmountKeyReleased

    private void tableOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableOrderMouseClicked
        int Click=tableOrder.getSelectedRow();
        TableModel model=tableOrder.getModel();
        cbxProduct.removeAllItems();
        cbxPaymentMethods.removeAllItems();
        cbxClassify.removeAllItems();
        txbID.setText(model.getValueAt(Click,0).toString());
        txbName.setText(model.getValueAt(Click,1).toString());
        txbAddress.setText(model.getValueAt(Click,2).toString());
        txbPhone.setText(model.getValueAt(Click,3).toString());
        cbxProduct.addItem(model.getValueAt(Click,4).toString());
        txbAmount.setText(model.getValueAt(Click,5).toString());
        txbPrice.setText(model.getValueAt(Click,6).toString());
        txbWarrantyPeriod.setText(model.getValueAt(Click,7).toString());
        txbIntoMoney.setText(model.getValueAt(Click,8).toString());
        ((JTextField)txbDate.getDateEditor().getUiComponent()).setText(model.getValueAt(Click,9).toString());
        cbxPaymentMethods.addItem(model.getValueAt(Click,10).toString());
        
        loadData();
        
        btnChange.setEnabled(true);
        btnDelete.setEnabled(true);
    }//GEN-LAST:event_tableOrderMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add=true;
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
        Enabled();
        LoadClassify();
        LoadPaymentMethods();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Change=true;
        Add=false;
        btnAdd.setEnabled(false);
        btnDelete.setEnabled(false);
        btnChange.setEnabled(false);
        btnSave.setEnabled(true);
        Enabled();
        LoadClassify();
        LoadPaymentMethods();
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa đơn đặt hàng hay không?", "Thông Báo",2);
        if(Click ==JOptionPane.YES_OPTION){
            String sqlDelete="DELETE FROM Orders WHERE Phone=? AND Customer=? AND Address=?";
            try{
                pst=conn.prepareStatement(sqlDelete);
                pst.setString(1, txbPhone.getText());
                pst.setString(2, this.txbName.getText());
                pst.setString(3, txbAddress.getText());
                pst.executeUpdate();
                lblStatus.setText("Xóa đơn đặt hàng thành công!");
                Disabled();
                Refresh();
                Load(sql);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(Add==true){
            if(Check()){
                addOrder();
            }
            else lblStatus.setText("Không thể thêm đơn đặt hàng vì mã đơn đặt hàng bạn nhập đã tồn tại");
        }else if(Change==true){
                changeOrder();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        Refresh();
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int lick=JOptionPane.showConfirmDialog(null,"Bạn Có Muốn Thoát Khỏi Chương Trình Hay Không?","Thông Báo",2);
        if(lick==JOptionPane.OK_OPTION){
            System.exit(0);
        }
        else{
            if(lick==JOptionPane.CANCEL_OPTION){    
                this.setVisible(true);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void txbPhoneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPhoneKeyReleased
        txbPhone.setText(cutChar(txbPhone.getText()));
    }//GEN-LAST:event_txbPhoneKeyReleased

    private void btnProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductActionPerformed
        Product product = new Product(detail);
        this.setVisible(false);
        product.setVisible(true);
    }//GEN-LAST:event_btnProductActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            JasperReport report=JasperCompileManager.compileReport("C:\\Users\\D.Thanh Trung\\Documents\\NetBeansProjects\\Quan Ly Cua Hang Mua Ban Thiet Bi Dien Tu\\src\\UserInterFace\\Orders.jrxml");
            
            JasperPrint print=JasperFillManager.fillReport(report, null, conn);
            
            JasperViewer.viewReport(print,false);
        }
        catch (JRException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail=new Detail();
                new OrderForm(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnProduct;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxClassify;
    private javax.swing.JComboBox<String> cbxPaymentMethods;
    private javax.swing.JComboBox<String> cbxProduct;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tableOrder;
    private javax.swing.JTextField txbAddress;
    private javax.swing.JTextField txbAmount;
    private com.toedter.calendar.JDateChooser txbDate;
    private javax.swing.JTextField txbID;
    private javax.swing.JTextField txbIntoMoney;
    private javax.swing.JTextField txbName;
    private javax.swing.JTextField txbPhone;
    private javax.swing.JTextField txbPrice;
    private javax.swing.JTextField txbWarrantyPeriod;
    // End of variables declaration//GEN-END:variables
}
