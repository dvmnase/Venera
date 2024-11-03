package condorcet.Models.Entities;

import java.io.Serializable;
import java.util.HashSet;

//POJO
public class User implements Serializable {

    private int Id;
    private String Login;
    private String Password;
    private  String Role;


    private Client personData;


    //private Set<UserMark> UserMarks = new HashSet<>();
    public User (){};

    public User(int id,String name,String login,String password, String role,Client personData){
        Id=id;
        Login=login;
        Password=password;
        Role=role;
        this.personData=personData;
        // UserMarks=userMarks;
    }
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public Client getPersonData() {
        return personData;
    }

    public void setPersonData(Client personData) {
        this.personData = personData;
    }
}
