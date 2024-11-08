/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.Objects;

/**
 *
 * @author Admin
 */
public class Product {
    private String id;
    private String productName;
    private String unitName;

    // Constructor
    public Product(String id, String productName, String unitName) {
        this.id = id;
        this.productName = productName;
        this.unitName = unitName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
     public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return productName; // Hoặc bất kỳ định dạng chuỗi nào bạn muốn hiển thị trong combobox
    }
 
}
