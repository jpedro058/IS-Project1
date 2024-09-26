package example;

import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "class")
public class Classroom {
   private ArrayList<Student> students;

   public Classroom() {
      students = new ArrayList<>();
   }

   @XmlElement(name = "student")
   public ArrayList<Student> getStudents() {
      return students;
   }

   public void setStudents(ArrayList<Student> students) {
      this.students = students;
   }
}
