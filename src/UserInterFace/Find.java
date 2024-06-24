
package UserInterFace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Find extends javax.swing.JFrame {
    private Connection conn = null;  
    private PreparedStatement pst = null;  
    private ResultSet rs = null;

    private Detail detail;
    
    private String sqlAccount = "SELECT * FROM Accounts";
    private String sqlEmployees = "SELECT * FROM NhanVien";
    private String sqlProduct = "SELECT * FROM Products";
    private String sqlOrders = "SELECT * FROM Orders";
    private String sqlPosition = "SELECT * FROM Position";
    private String sqlProducer = "SELECT * FROM Producer";
    private String sqlClassify = "SELECT * FROM Classify";
    
    
    public Find(Detail d) {
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        detail=new Detail(d);
        connection();
        loadData();
    }
    
    private void connection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn=DriverManager.getConnection("jdbc:sqlserver://THANHTRUNG:1433;databaseName=CuaHangDienTu;user=sa;password=sa2016");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadData(){
        loadAccount(sqlAccount);
        loadEmployees(sqlEmployees);
        loadProduct(sqlProduct);
        loadOrders(sqlOrders);
        loadProducer(sqlProducer);
        loadClassify(sqlClassify);
        loadPosition(sqlPosition);
    }
    
    private void loadAccount(String sql){
        TableAccount.removeAll();
        try{
            String [] arr={"Tên Đăng Nhập","Tên Nhân Viên","Ngày Tạo"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("Username").trim());
                vector.add(rs.getString("FullName").trim());
                vector.add(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("DateCreated")));
                modle.addRow(vector);
            }
            TableAccount.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void loadEmployees(String sql){
        try{
            String [] arr={"Chức Vụ","Mã NV","Họ Tên","Bậc Lương","Năm Sinh","Giới Tính","Địa Chỉ","SĐT","Email","Lương"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("Position").trim());
                vector.add(rs.getString("EmployeeCode").trim());
                vector.add(rs.getString("FullName").trim());
                vector.add(rs.getInt("Level"));
                vector.add(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("YearofBirth")));
                vector.add(rs.getString("Sex").trim());
                vector.add(rs.getString("Address").trim());
                vector.add(rs.getString("Phone").trim());
                vector.add(rs.getString("Email").trim());
                vector.add(rs.getString("Payroll").trim());
                modle.addRow(vector);
            }
            tableEmployees.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadProduct(String sql){
        tableProduct.removeAll();
        try{
            String [] arr={"Mã Sản Phẩm","Loại linh kiện","Tên linh kiện","Nhà sản xuất","Thời gian bảo hành","Số lượng còn","đơn vị","Giá"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("Classify").trim());
                vector.add(rs.getString("Name").trim());
                vector.add(rs.getString("Producer").trim());
                vector.add(rs.getInt("WarrantyPeriod")+" "+rs.getString("SingleTime").trim());
                vector.add(rs.getInt("QuantityRemaining"));
                vector.add(rs.getString("Unit").trim());
                vector.add(rs.getString("Price").trim());
                modle.addRow(vector);
            }
            tableProduct.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
     }
    
    public void loadOrders(String sql){
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
    
    private void loadProducer(String sql){
        tableProducer.removeAll();
        try{
            String [] arr={"Mã NSX","Nhà Sản Xuất","Địa Chỉ","Số Điện Thoại","Email"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("ProducerName").trim());
                vector.add(rs.getString("Address").trim());
                vector.add(rs.getString("Phone").trim());
                vector.add(rs.getString("Email").trim());
                modle.addRow(vector);
            }
            tableProducer.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void loadClassify(String sql){
        tableClassify.removeAll();
        try{
            String [] arr={"Mã Loại","Loại Linh Kiện"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("Classify").trim());
                modle.addRow(vector);
            }
            tableClassify.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void loadPosition(String sql){
        tablePosition.removeAll();
        try{
            String [] arr={"Mã Chức Vụ","Chức Vụ","Lương Cơ Bản"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("ID").trim());
                vector.add(rs.getString("Position").trim());
                vector.add(rs.getString("Payroll").trim());
                modle.addRow(vector);
            }
            tablePosition.setModel(modle);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton8 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnBackHome = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txbAccount = new javax.swing.JTextField();
        btnRefreshAccount = new javax.swing.JButton();
        btnFindAccount = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableAccount = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnRefreshEmployees = new javax.swing.JButton();
        btnFindEmployees = new javax.swing.JButton();
        txbEmployees = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableEmployees = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnRefreshProduct = new javax.swing.JButton();
        btnFindProduct = new javax.swing.JButton();
        txbProduct = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableProduct = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnRefreshOrders = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        txbOrders = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableOrder = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        btnRefreshProducer = new javax.swing.JButton();
        btnFindProducer = new javax.swing.JButton();
        txbProducer = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableProducer = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        btnRefreshPosition = new javax.swing.JButton();
        btnFindposition = new javax.swing.JButton();
        txbPosition = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablePosition = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        btnRefreshClassify = new javax.swing.JButton();
        btnFindClassify = new javax.swing.JButton();
        txbClassify = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableClassify = new javax.swing.JTable();

        jButton8.setText("jButton8");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 38)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Tìm Kiếm");

        btnBackHome.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBackHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnBackHome.setText("Hệ Thống");
        btnBackHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackHomeActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Tìm Kiếm:");

        btnRefreshAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAccountActionPerformed(evt);
            }
        });

        btnFindAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindAccount.setText("Tìm");
        btnFindAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindAccountActionPerformed(evt);
            }
        });

        TableAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(TableAccount);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbAccount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txbAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefreshAccount)
                    .addComponent(btnFindAccount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tài Khoản", jPanel1);

        btnRefreshEmployees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshEmployees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshEmployeesActionPerformed(evt);
            }
        });

        btnFindEmployees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindEmployees.setText("Tìm");
        btnFindEmployees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindEmployeesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tìm Kiếm:");

        tableEmployees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableEmployees);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbEmployees)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRefreshEmployees, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFindEmployees, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txbEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Nhân Viên", jPanel2);

        btnRefreshProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshProductActionPerformed(evt);
            }
        });

        btnFindProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindProduct.setText("Tìm");
        btnFindProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindProductActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Tìm Kiếm:");

        tableProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tableProduct);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbProduct)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFindProduct)
                        .addComponent(txbProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(btnRefreshProduct))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sản Phẩm", jPanel3);

        btnRefreshOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshOrdersActionPerformed(evt);
            }
        });

        btnOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnOrders.setText("Tìm");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Tìm Kiếm:");

        tableOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(tableOrder);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbOrders)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOrders)
                        .addComponent(txbOrders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(btnRefreshOrders))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Đơn Hàng", jPanel4);

        btnRefreshProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshProducerActionPerformed(evt);
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

        tableProducer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(tableProducer);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbProducer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindProducer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshProducer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRefreshProducer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFindProducer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txbProducer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Nhà Sản Xuất", jPanel5);

        btnRefreshPosition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshPositionActionPerformed(evt);
            }
        });

        btnFindposition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindposition.setText("Tìm");
        btnFindposition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindpositionActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Tìm Kiếm:");

        tablePosition.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane7.setViewportView(tablePosition);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbPosition)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindposition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFindposition)
                        .addComponent(txbPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addComponent(btnRefreshPosition))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Chức Vụ", jPanel7);

        btnRefreshClassify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Refresh-icon.png"))); // NOI18N
        btnRefreshClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshClassifyActionPerformed(evt);
            }
        });

        btnFindClassify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Find.png"))); // NOI18N
        btnFindClassify.setText("Tìm");
        btnFindClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindClassifyActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Tìm Kiếm:");

        tableClassify.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(tableClassify);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txbClassify)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFindClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshClassify, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRefreshClassify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFindClassify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txbClassify, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Loại Linh Kiện", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBackHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBackHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFindAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindAccountActionPerformed
        String sqlFind="SELECT * FROM Accounts where UserName like N'%"+this.txbAccount.getText()+"%' or FullName like N'%"+this.txbAccount.getText()+"%' or DateCreated like N'%"+this.txbAccount.getText()+"%'";
        loadAccount(sqlFind);
        txbAccount.setText("");
    }//GEN-LAST:event_btnFindAccountActionPerformed

    private void btnRefreshAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAccountActionPerformed
        loadAccount(sqlAccount);
    }//GEN-LAST:event_btnRefreshAccountActionPerformed

    private void btnFindProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindProductActionPerformed
        String sql = "SELECT * FROM Products where ID like N'%"+this.txbProduct.getText()+"%' or Classify like N'%"+this.txbProduct.getText()+"%' or Name like N'%"+this.txbProduct.getText()+"%' or Producer like N'%"+this.txbProduct.getText()+"%' or WarrantyPeriod like N'%"+this.txbProduct.getText()+"%' or SingleTime like N'%"+this.txbProduct.getText()+"%' or QuantityRemaining like N'%"+this.txbProduct.getText()+"%' or Unit like N'%"+this.txbProduct.getText()+"%' or Price like N'%"+this.txbProduct.getText()+"%'";
        loadProduct(sql);
        txbProduct.setText("");
    }//GEN-LAST:event_btnFindProductActionPerformed

    private void btnFindEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindEmployeesActionPerformed
        String sql = "SELECT * FROM NhanVien where Position like N'%"+this.txbEmployees.getText()+"%' or EmployeeCode like N'%"+this.txbEmployees.getText()+"%' or FullName like N'%"+this.txbEmployees.getText()+"%' or YearofBirth like N'%"+this.txbEmployees.getText()+"%' or Sex like N'%"+this.txbEmployees.getText()+"%' or Address like N'%"+this.txbEmployees.getText()+"%' or Phone like N'%"+this.txbEmployees.getText()+"%' or Email like N'%"+this.txbEmployees.getText()+"%' or Level like N'%"+this.txbEmployees.getText()+"' or Payroll like N'%"+this.txbEmployees.getText()+"'";
        loadEmployees(sql);
        txbEmployees.setText("");
    }//GEN-LAST:event_btnFindEmployeesActionPerformed

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
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
    }//GEN-LAST:event_btnBackHomeActionPerformed

    private void btnFindProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindProducerActionPerformed
        String sql = "SELECT * FROM Producer where ID like N'%"+this.txbProducer.getText()+"%' or ProducerName like N'%"+this.txbProducer.getText()+"%' or Address like N'%"+this.txbProducer.getText()+"%' or Phone like N'%"+this.txbProducer.getText()+"%' or Email like N'%"+this.txbProducer.getText()+"%'";
        loadProducer(sql);
        txbProducer.setText("");
    }//GEN-LAST:event_btnFindProducerActionPerformed

    private void btnFindpositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindpositionActionPerformed
        String sql = "SELECT * FROM Position where ID like N'%"+this.txbPosition.getText()+"%' or Position like N'%"+this.txbPosition.getText()+"%' or Payroll like N'%"+this.txbPosition.getText()+"%'";
        loadPosition(sql);
        txbPosition.setText("");
    }//GEN-LAST:event_btnFindpositionActionPerformed

    private void btnFindClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindClassifyActionPerformed
        String sql = "SELECT * FROM Classify where ID like N'%"+this.txbClassify.getText()+"%' or Classify like N'%"+this.txbClassify.getText()+"%'";
        loadClassify(sql);
        txbClassify.setText("");
    }//GEN-LAST:event_btnFindClassifyActionPerformed

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

    private void btnRefreshEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshEmployeesActionPerformed
        loadEmployees(sqlEmployees);
    }//GEN-LAST:event_btnRefreshEmployeesActionPerformed

    private void btnRefreshProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshProductActionPerformed
        loadProduct(sqlProduct);
    }//GEN-LAST:event_btnRefreshProductActionPerformed

    private void btnOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdersActionPerformed
        String sql = "SELECT * FROM Orders where ID like N'%"+this.txbOrders.getText()+"%' or Customer like N'%"+this.txbOrders.getText()+"%' or Address like N'%"+this.txbOrders.getText()+"%' or Phone like N'%"+this.txbOrders.getText()+"%' or Product like N'%"+this.txbOrders.getText()+"%' or Amount like N'%"+this.txbOrders.getText()+"%' or Price like N'%"+this.txbOrders.getText()+"%' or WarrantyPeriod like N'%"+this.txbOrders.getText()+"%' or intoMoney like N'%"+this.txbOrders.getText()+"' or Date like N'%"+this.txbOrders.getText()+"' or PaymentMethods like N'%"+this.txbOrders.getText()+"'";
        loadOrders(sql);
        txbOrders.setText("");
    }//GEN-LAST:event_btnOrdersActionPerformed

    private void btnRefreshProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshProducerActionPerformed
        loadProducer(sqlProducer);
    }//GEN-LAST:event_btnRefreshProducerActionPerformed

    private void btnRefreshPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshPositionActionPerformed
        loadPosition(sqlPosition);
    }//GEN-LAST:event_btnRefreshPositionActionPerformed

    private void btnRefreshClassifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshClassifyActionPerformed
        loadClassify(sqlClassify);
    }//GEN-LAST:event_btnRefreshClassifyActionPerformed

    private void btnRefreshOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshOrdersActionPerformed
        loadOrders(sqlOrders);
    }//GEN-LAST:event_btnRefreshOrdersActionPerformed


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
            java.util.logging.Logger.getLogger(Find.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Find.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Find.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Find.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail=new Detail();
                new Find(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableAccount;
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnFindAccount;
    private javax.swing.JButton btnFindClassify;
    private javax.swing.JButton btnFindEmployees;
    private javax.swing.JButton btnFindProducer;
    private javax.swing.JButton btnFindProduct;
    private javax.swing.JButton btnFindposition;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnRefreshAccount;
    private javax.swing.JButton btnRefreshClassify;
    private javax.swing.JButton btnRefreshEmployees;
    private javax.swing.JButton btnRefreshOrders;
    private javax.swing.JButton btnRefreshPosition;
    private javax.swing.JButton btnRefreshProducer;
    private javax.swing.JButton btnRefreshProduct;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tableClassify;
    private javax.swing.JTable tableEmployees;
    private javax.swing.JTable tableOrder;
    private javax.swing.JTable tablePosition;
    private javax.swing.JTable tableProducer;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTextField txbAccount;
    private javax.swing.JTextField txbClassify;
    private javax.swing.JTextField txbEmployees;
    private javax.swing.JTextField txbOrders;
    private javax.swing.JTextField txbPosition;
    private javax.swing.JTextField txbProducer;
    private javax.swing.JTextField txbProduct;
    // End of variables declaration//GEN-END:variables
}
