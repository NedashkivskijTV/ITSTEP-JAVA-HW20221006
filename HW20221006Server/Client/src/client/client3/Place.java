package client.client3;

import java.io.Serializable;

public class Place implements Serializable {
    private long id;
    private String num;
    private String phone;

    public Place(String num, String phone) {
        this.num = num;
        this.phone = phone;
    }

    public Place(long id, String num, String phone) {
        this.id = id;
        this.num = num;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", num='" + num + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
