package example;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Student {
    private String name;
    private int age;
    private String id;

    // Default constructor (required for JAXB)
    public Student() {
    }

    public Student(String name, int age, String id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @XmlAttribute(name = "id") // Ensure the ID is treated as an attribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
