package code;

import java.util.ArrayList;
import java.util.List;

public class Reader {
    private String readerId;
    private String name;
    private String phoneNumber;
    private String address;
    private List<Borrow> borrowHistory = new ArrayList<>();;

    public Reader(String readerId, String name, String address, String PhoneNumber) {
        this.readerId = readerId;
        this.name = name;
        this.phoneNumber = PhoneNumber;
        this.address = address;
        this.borrowHistory = new ArrayList<>(); 
    }

    public Reader(String readerId) {
        this.readerId = readerId;
    }

    public Reader() {
    }

    // Getters and Setters
    public String getReaderId() { 
        return readerId; 
    }
    public void setReaderId(String readerId) { 
        this.readerId = readerId; 
    }

    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() { 
        return address; 
    }
    public void setAddress(String address) { 
        this.address = address; 
    }

    public List<Borrow> getBorrowHistory() { return borrowHistory; }
    public void addBorrow(Borrow borrow) { this.borrowHistory.add(borrow); }

}
