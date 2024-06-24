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
public class Unit {
    private String id;
    private String unit;

    // Constructor
    public Unit(String id, String unit) {
        this.id = id;
        this.unit = unit;
    }

    // Getter for ID
    public String getId() {
        return id;
    }

    // Setter for ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for Unit
    public String getUnit() {
        return unit;
    }

    // Setter for Unit
    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Override toString method
    @Override
    public String toString() {
        return unit; // Trả về tên đơn vị để hiển thị trong JComboBox
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit1 = (Unit) o;
        return id.equals(unit1.id) && unit.equals(unit1.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unit);
    }
}
