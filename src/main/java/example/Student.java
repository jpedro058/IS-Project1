package example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;

// Root element for XML
@JacksonXmlRootElement(localName = "student")
public class Student implements Serializable {
    
    // Use Jackson annotations for both JSON and XML serialization
    private String name;
    private int age;
    private String id;

    // Default constructor is needed for serialization/deserialization
    public Student() {
    }

    // Constructor for easier initialization
    public Student(String name, int age, String id) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    // Use JsonProperty and JacksonXmlProperty for JSON and XML respectively
    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("age")
    @JacksonXmlProperty(localName = "age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // The `id` field is an attribute in XML, and a regular property in JSON
    @JsonProperty("id")
    @JacksonXmlProperty(isAttribute = true) // Attribute in XML
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Override toString for better object description
    @Override
    public String toString() {
        return "Student [name=" + name + ", age=" + age + ", id=" + id + "]";
    }
}
