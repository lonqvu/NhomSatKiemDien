
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

public class EmployeesManagement extends javax.swing.JFrame {

    private Connection conn = null;  
    private PreparedStatement pst = null;  
    private ResultSet rs = null;
    
    private boolean Add=false,Change=false;
    private String sql = "SELECT * FROM NhanVien";
    
    private Detail detail;
    
    public EmployeesManagement(Detail d) {
        initComponents();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        connection();
        detail=new Detail(d);
        Load(sql);
        Disabled();
        LoadPosition();
        lblStatus.setForeground(Color.red);
    }

    private void connection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn=DriverManager.getConnection("jdbc:sqlserver://THANHTRUNG:1433;databaseName=CuaHangDienTu;user=sa;password=sa2016");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void Enabled(){
        cbxPosition.setEnabled(true);
        txbCode.setEnabled(true);
        txbName.setEnabled(true);
        txbBirthday.setEnabled(true);
        cbxSex.setEnabled(true);
        txbPhone.setEnabled(true);
        txbLevel.setEnabled(true);
        txbAddress.setEnabled(true);
        txbEmail.setEnabled(true);
        btn.setEnabled(true);
        lblStatus.setText("Trạng Thái!");
    }
    
    private void Disabled(){
        cbxPosition.setEnabled(false);
        txbCode.setEnabled(false);
        txbName.setEnabled(false);
        txbBirthday.setEnabled(false);
        cbxSex.setEnabled(false);
        txbPhone.setEnabled(false);
        txbLevel.setEnabled(false);
        txbAddress.setEnabled(false);
        txbEmail.setEnabled(false);
        btn.setEnabled(false);
    }
    
    private void Refresh(){
        Add=false;
        Change=false;
        txbCode.setText("");
        txbName.setText("");
        ((JTextField)txbBirthday.getDateEditor().getUiComponent()).setText("");
        txbPhone.setText("");
        txbLevel.setText("");
        txbAddress.setText("");
        txbEmail.setText("");
        btnAdd.setEnabled(true);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(false);
        LoadPosition();
        LoadGT();
        lblStatus.setText("Trạng Thái");
    }
    
    private void LoadGT(){
        cbxSex.removeAllItems();
        cbxSex.addItem("Nam");
        cbxSex.addItem("Nử");
        cbxSex.addItem("Khác");
    }
    
    private void Load(String sql){
        try{
            String [] arr={"Mã NV","Chức Vụ","Họ Tên","Bậc Lương","Năm Sinh","Giới Tính","Địa Chỉ","SĐT","Email","Lương"};
            DefaultTableModel modle=new DefaultTableModel(arr,0);
            pst=conn.prepareStatement(sql);
            rs=pst.executeQuery();
            while(rs.next()){
                Vector vector=new Vector();
                vector.add(rs.getString("EmployeeCode").trim());
                vector.add(rs.getString("Position").trim());
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
    
    private void LoadPosition(){
        cbxPosition.removeAllItems();
        String sqlcbxPosition = "SELECT * FROM Position";
        try {
            pst=conn.prepareStatement(sqlcbxPosition);
            rs=pst.executeQuery();
            while(rs.next()){
                this.cbxPosition.addItem(rs.getString("Position").trim());
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
    }
    
    private boolean checkNull(){
        boolean kq=true;
        if(String.valueOf(this.txbCode.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập mã nhân viên!");
            return false;
        }
        if(String.valueOf(this.txbName.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập họ tên nhân viên!");
            return false;
        }
        if(String.valueOf(this.txbLevel.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập bậc lương!");
            return false;
        }
        if(String.valueOf(((JTextField)this.txbBirthday.getDateEditor().getUiComponent()).getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập năm sinh!");
            return false;
            }
        if(String.valueOf(this.txbAddress.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập địa chỉ!");
            return false;
        }
        if(String.valueOf(this.txbPhone.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập số điện thoại!");
            return false;
        }
        if(String.valueOf(this.txbEmail.getText()).length()==0){
            lblStatus.setText("Bạn chưa nhập email!");
            return false;
        }
        if(String.valueOf(this.txbPayroll.getText()).length()==0){
            lblStatus.setText("Bạn cần cập nhật lại bậc lương!");
            return false;
        }
        return kq;
    }
    
    private void addEmployees(){
        if(checkNull()){
            String sqlInsert="INSERT INTO NhanVien (Position,EmployeeCode,FullName,Level,YearofBirth,Sex,Phone,Address,Email,Payroll) VALUES(?,?,?,?,?,?,?,?,?,?)";
            try{
                pst=conn.prepareStatement(sqlInsert);
                pst.setString(1, (String)cbxPosition.getSelectedItem());
                pst.setString(2, this.txbCode.getText());
                pst.setString(3, txbName.getText());
                pst.setInt(4, Integer.parseInt(txbLevel.getText()));
                pst.setDate(5,new java.sql.Date(this.txbBirthday.getDate().getTime()));
                pst.setString(6, (String)cbxSex.getSelectedItem());
                pst.setString(7, this.txbPhone.getText());
                pst.setString(8, txbAddress.getText());
                pst.setString(9, txbEmail.getText());
                pst.setString(10, txbPayroll.getText() +" "+"VND");
                pst.executeUpdate();
                Refresh();
                lblStatus.setText("Thêm nhân viên thành công!");
                Disabled();
                
                Load(sql);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    private void changeEmployees(){
        int Click=tableEmployees.getSelectedRow();
        TableModel model=tableEmployees.getModel();
        
        if(checkNull()){
            String sqlChange="UPDATE NhanVien SET Position=?,EmployeeCode=?,FullName=?,Level=?,YearofBirth=?,Sex=?,Address=?,Phone=?,Email=?,Payroll=? WHERE EmployeeCode='"+model.getValueAt(Click,1).toString().trim()+"'";;
            try{
                pst=conn.prepareStatement(sqlChange);
                pst.setString(1, (String)cbxPosition.getSelectedItem());
                pst.setString(2, this.txbCode.getText());
                pst.setString(3, txbName.getText());
                pst.setInt(4, Integer.parseInt(txbLevel.getText()));
                pst.setDate(5,new java.sql.Date(this.txbBirthday.getDate().getTime()));
                pst.setString(6, (String)cbxSex.getSelectedItem());
                pst.setString(7, this.txbAddress.getText());
                pst.setString(8, txbPhone.getText());
                pst.setString(9, txbEmail.getText());
                pst.setString(10, txbPayroll.getText() +" "+"VND");
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
    
    private boolean Check(){
        boolean kq=true;
        String sqlCheck="SELECT * FROM NhanVien";
        try{
            pst=conn.prepareStatement(sqlCheck);
            rs=pst.executeQuery();
            while(rs.next()){
                if(this.txbCode.getText().equals(rs.getString("EmployeeCode").toString().trim())){
                    return false;
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableEmployees = new javax.swing.JTable();
        btnHomeBack = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txbCode = new javax.swing.JTextField();
        cbxPosition = new javax.swing.JComboBox<>();
        txbName = new javax.swing.JTextField();
        cbxSex = new javax.swing.JComboBox<>();
        txbPhone = new javax.swing.JTextField();
        txbAddress = new javax.swing.JTextField();
        txbBirthday = new com.toedter.calendar.JDateChooser();
        txbEmail = new javax.swing.JTextField();
        btn = new javax.swing.JButton();
        txbPayroll = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txbLevel = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableEmployees.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tableEmployees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableEmployees.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableEmployeesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableEmployees);

        btnHomeBack.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnHomeBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Back.png"))); // NOI18N
        btnHomeBack.setText("Hệ Thống");
        btnHomeBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHomeBackMouseClicked(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Trạng Thái");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel1.setText("Chức vụ:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel2.setText("Mã nhân viên:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel3.setText("Họ và tên:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel4.setText("Ngày Sinh:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel5.setText("Giới tính:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel6.setText("Địa chỉ:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel7.setText("Số điện thoại:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel8.setText("Email:");

        txbCode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cbxPosition.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxPosition.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbxPositionPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        txbName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cbxSex.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txbPhone.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txbPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbPhoneKeyReleased(evt);
            }
        });

        txbAddress.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        txbBirthday.setDateFormatString("dd/MM/yyyy");

        txbEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn.setText("....");
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionPerformed(evt);
            }
        });

        txbPayroll.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txbPayroll.setEnabled(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel10.setText("Lương:");

        txbLevel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txbLevel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txbLevelKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel11.setText("Bậc lương:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbxPosition, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txbCode, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txbBirthday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbxSex, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txbPhone)
                    .addComponent(txbEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txbPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txbLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(cbxPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txbCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(cbxSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(txbAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(txbBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(txbPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txbLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txbPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(txbEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 71, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Print Sale.png"))); // NOI18N
        jButton1.setText("In Danh Sách");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChange, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 28)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Cập Nhật Nhân Viên");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnHomeBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHomeBack, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHomeBackMouseClicked
        Home home = new Home(detail);
        this.setVisible(false);
        home.setVisible(true);
    }//GEN-LAST:event_btnHomeBackMouseClicked

    private void tableEmployeesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableEmployeesMouseClicked
        cbxSex.removeAllItems();
        cbxPosition.removeAllItems();
        
        int Click=tableEmployees.getSelectedRow();
        TableModel model=tableEmployees.getModel();
        
        cbxPosition.addItem(model.getValueAt(Click,1).toString());
        txbCode.setText(model.getValueAt(Click,0).toString());
        txbName.setText(model.getValueAt(Click,2).toString());
        txbLevel.setText(model.getValueAt(Click,3).toString());
        ((JTextField)txbBirthday.getDateEditor().getUiComponent()).setText(model.getValueAt(Click,4).toString());
        cbxSex.addItem(model.getValueAt(Click,5).toString());
        txbAddress.setText(model.getValueAt(Click,6).toString());
        txbPhone.setText(model.getValueAt(Click,7).toString());
        txbEmail.setText(model.getValueAt(Click,8).toString());
        String []s=model.getValueAt(Click,9).toString().split("\\s");
        txbPayroll.setText(s[0]);
        
        btnDelete.setEnabled(true);
        btnChange.setEnabled(true);
    }//GEN-LAST:event_tableEmployeesMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Refresh();
        Add=true;
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
        LoadGT();
        Enabled();
        LoadPosition();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Add=false;
        Change=true;
        txbLevel.setText("");
        txbPayroll.setText("");
        btnAdd.setEnabled(false);
        btnChange.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(true);
        LoadGT();
        Enabled();
        LoadPosition();
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int Click = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa nhân viên hay không?", "Thông Báo",2);
        if(Click ==JOptionPane.YES_OPTION){
            String sqlDelete="DELETE FROM NhanVien WHERE EmployeeCode=? AND FullName=? AND Phone=? ";//
            try{
                pst=conn.prepareStatement(sqlDelete);
                pst.setString(1, String.valueOf(this.txbCode.getText()));
                pst.setString(2, txbName.getText());
                pst.setString(3, txbPhone.getText());
                pst.executeUpdate();
                
                Disabled();
                Refresh();
                lblStatus.setText("Xóa nhân viên thành công!");
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
                addEmployees();
            }
            else lblStatus.setText("Không thể thêm nhân viên vì mã số nhân viên bạn nhập đã tồn tại");
        }else if(Change==true){
            changeEmployees();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        LoadGT();
        Refresh();
        Disabled();
        LoadPosition();
        Load(sql);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txbLevelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbLevelKeyReleased
        String sql = "SELECT * FROM Position where Position=?";
        txbLevel.setText(cutChar(txbLevel.getText()));
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        try {
            pst=conn.prepareStatement(sql);
            pst.setString(1, this.cbxPosition.getSelectedItem().toString());
            rs=pst.executeQuery();
            while(rs.next()){
                if(txbLevel.getText().equals("")){
                    txbPayroll.setText("0");
                }
                else{   
                    if(txbLevel.getText().equals("0")){
                        int Level=Integer.parseInt(txbLevel.getText());
                        
                        String[]s=rs.getString("Payroll").trim().split("\\s");
                        
                        txbPayroll.setText(formatter.format((int)(convertedToNumbers(s[0])/2)));
                    }
                    else{
                        int Level=Integer.parseInt(txbLevel.getText().toString());
                        String[]s=rs.getString("Payroll").trim().split("\\s");
                        
                        txbPayroll.setText(formatter.format(Level*convertedToNumbers(s[0])));
                    }
                }   
            }
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }
    }//GEN-LAST:event_txbLevelKeyReleased

    private void cbxPositionPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbxPositionPopupMenuWillBecomeInvisible
        txbLevel.setText("");
        txbPayroll.setText("");
    }//GEN-LAST:event_cbxPositionPopupMenuWillBecomeInvisible

    private void btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionPerformed
        Data data = new Data(detail);
        this.setVisible(false);
        data.setVisible(true);
    }//GEN-LAST:event_btnActionPerformed

    private void txbPhoneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txbPhoneKeyReleased
        txbPhone.setText(cutChar(txbPhone.getText()));
    }//GEN-LAST:event_txbPhoneKeyReleased

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            JasperReport report=JasperCompileManager.compileReport("C:\\Users\\D.Thanh Trung\\Documents\\NetBeansProjects\\Quan Ly Cua Hang Mua Ban Thiet Bi Dien Tu\\src\\UserInterFace\\Empployees.jrxml");
            
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
            java.util.logging.Logger.getLogger(EmployeesManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeesManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeesManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeesManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Detail detail=new Detail();
                new EmployeesManagement(detail).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnHomeBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxPosition;
    private javax.swing.JComboBox<String> cbxSex;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tableEmployees;
    private javax.swing.JTextField txbAddress;
    private com.toedter.calendar.JDateChooser txbBirthday;
    private javax.swing.JTextField txbCode;
    private javax.swing.JTextField txbEmail;
    private javax.swing.JTextField txbLevel;
    private javax.swing.JTextField txbName;
    private javax.swing.JTextField txbPayroll;
    private javax.swing.JTextField txbPhone;
    // End of variables declaration//GEN-END:variables
}
