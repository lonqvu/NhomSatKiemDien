
package UserInterFace;

public class Detail {
    private String user;
    private String name;
    private int role;
    
    public Detail(){
       
    }
    
    public Detail(String us, String na, int role){
        this.user=us;
        this.name=na;
        this.role=role;
    }

    public Detail(Detail detail){
        this.user=detail.user;
        this.name=detail.name;
        this.role=detail.role;
    }
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
     public int getRole() {
        return role;
    }

    public void setName(int role) {
        this.role = role;
    }
}
