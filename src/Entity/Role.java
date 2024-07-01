/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

/**
 *
 * @author Admin
 */
import java.util.Objects;

public class Role {
    private int id;
    private String roleName;

    // Constructor
    public Role(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for RoleName
    public String getRoleName() {
        return roleName;
    }

    // Setter for RoleName
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // toString method
    @Override
    public String toString() {
        return roleName;
    }

    // equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return id == role.id && Objects.equals(roleName, role.roleName);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id, roleName);
    }
}

