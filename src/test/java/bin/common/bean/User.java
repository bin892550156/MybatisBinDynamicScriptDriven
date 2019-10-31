package bin.common.bean;


import bin.common.driver.annotation.BinIgnoreMapping;
import bin.common.driver.annotation.BinMappingTable;

@BinMappingTable
public class User {

    private int id;
    private String name;
    private String deptId;
    @BinIgnoreMapping
    private String phone;
    @BinIgnoreMapping
    private String website;
    private String desName;
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDesName() {
        return desName;
    }

    public void setDesName(String desName) {
        this.desName = desName;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deptId='" + deptId + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", desName='" + desName + '\'' +
                ", age=" + age +
                '}';
    }
}
