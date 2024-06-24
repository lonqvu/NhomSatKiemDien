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
public class Classify {
    private String id;
    private String classify;

    // Constructor
    public Classify(String id, String classify) {
        this.id = id;
        this.classify = classify;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
    
     @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Classify classify = (Classify) obj;
        return id.equals(classify.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Override toString
    @Override
    public String toString() {
        return classify; // Hoặc bất kỳ định dạng chuỗi nào bạn muốn hiển thị trong combobox
    }
}

