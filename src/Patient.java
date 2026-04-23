package src;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String address;

    public Patient() {}

    public Patient(String name, int age, String gender, String phone, String address) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }
    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge()              { return age; }
    public void setAge(int age)      { this.age = age; }
    public String getGender()        { return gender; }
    public void setGender(String g)  { this.gender = g; }
    public String getPhone()         { return phone; }
    public void setPhone(String p)   { this.phone = p; }
    public String getAddress()       { return address; }
    public void setAddress(String a) { this.address = a; }

    @Override
    public String toString() { return name; }
}