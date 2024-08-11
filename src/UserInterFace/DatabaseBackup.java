/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterFace;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Admin
 */
public class DatabaseBackup {
    public static void backupDatabase() {
        Statement stmt = null;
        String URL = "jdbc:sqlserver://LONGVIPPRONO1\\SQLEXPRESS01:1433;databaseName=NhomSatKiemDien;encrypt=false";
        String USER = "sa";
        String PASSWORD = "123";
        Connection connection = null;
        try {
            // Kết nối tới cơ sở dữ liệu
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            String directoryPath = "D:/Sao lưu dữ liệu"; // Đường dẫn thư mục lưu trữ
            String fileName = "BackupFile" + ".bak"; // Tên file dựa trên mã hóa đơn
            String filePath = directoryPath + File.separator + fileName;

            // Đảm bảo thư mục tồn tại
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // Tạo thư mục nếu không tồn tại
            }

            
            // Lệnh sao lưu
            String backupSQL = "BACKUP DATABASE NhomSatKiemDien TO DISK = '"+ filePath +"' WITH FORMAT, INIT;";
            
            stmt = connection.createStatement();
            stmt.execute(backupSQL);
            
            System.out.println("Database backup completed successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
