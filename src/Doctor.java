package src;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String phone;
    private String availableDays;
    private String availableTime;

    public Doctor() {}

    public Doctor(String name, String specialization, String phone,
                  String availableDays, String availableTime) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.availableDays = availableDays;
        this.availableTime = availableTime;
    }

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }
    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }
    public String getSpecialization()         { return specialization; }
    public void setSpecialization(String s)   { this.specialization = s; }
    public String getPhone()                  { return phone; }
    public void setPhone(String p)            { this.phone = p; }
    public String getAvailableDays()          { return availableDays; }
    public void setAvailableDays(String d)    { this.availableDays = d; }
    public String getAvailableTime()          { return availableTime; }
    public void setAvailableTime(String t)    { this.availableTime = t; }

    @Override
    public String toString() { return name + " (" + specialization + ")"; }
}