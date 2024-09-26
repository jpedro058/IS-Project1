package example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ConvertJson {

    public static void main(String[] args) {
        Classroom classroom = new Classroom();

        // Create Student objects
        Student s1 = new Student();
        s1.setName("Alberto");
        s1.setAge(21);
        s1.setId("201134441110L");
        classroom.getStudents().add(s1);

        Student s2 = new Student();
        s2.setName("Patricia");
        s2.setAge(22);
        s2.setId("201134441116L");
        classroom.getStudents().add(s2);

        Student s3 = new Student();
        s3.setName("Luis");
        s3.setAge(21);
        s3.setId("201134441210L");
        classroom.getStudents().add(s3);

        // Add more students
        Student s4 = new Student();
        s4.setName("Maria");
        s4.setAge(23);
        s4.setId("201134441310L");
        classroom.getStudents().add(s4);

        Student s5 = new Student();
        s5.setName("Carlos");
        s5.setAge(20);
        s5.setId("201134441410L");
        classroom.getStudents().add(s5);

        Student s6 = new Student();
        s6.setName("Ana");
        s6.setAge(22);
        s6.setId("201134441510L");
        classroom.getStudents().add(s6);

        Student s7 = new Student();
        s7.setName("Jorge");
        s7.setAge(24);
        s7.setId("201134441610L");
        classroom.getStudents().add(s7);

        Student s8 = new Student();
        s8.setName("Beatriz");
        s8.setAge(21);
        s8.setId("201134441710L");
        classroom.getStudents().add(s8);

        Student s9 = new Student();
        s9.setName("Fernando");
        s9.setAge(23);
        s9.setId("201134441810L");
        classroom.getStudents().add(s9);

        Student s10 = new Student();
        s10.setName("Isabel");
        s10.setAge(22);
        s10.setId("201134441910L");
        classroom.getStudents().add(s10);

        // Convert the Classroom object to JSON and write it to a file
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Write the JSON directly to a file
            objectMapper.writeValue(new File("classroomJackson.json"), classroom);

            // Print confirmation message
            System.out.println("JSON file created: classroom.json");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
