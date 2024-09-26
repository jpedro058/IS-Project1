package example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {

        Student s1 = new Student("Alberto", 21, "201134441110");
        Student s2 = new Student("Patricia", 22, "201134441116");
        Student s3 = new Student("Luis", 21, "201134441210");

        List<Student> students = new ArrayList<>();
        students.add(s1);
        students.add(s2);
        students.add(s3);

        // --------------------------------------
        // UTILIZANDO A BIBLIOTECA GSON
        Gson gson = new Gson();

        long startSerializationTime = System.nanoTime();
        String json = gson.toJson(students); // Serialização
        long endSerializationTime = System.nanoTime();
        long serializationDuration = endSerializationTime - startSerializationTime;

        System.out.println("JSON Serializado:");
        System.out.println(json);
        System.out.println("Tempo de Serialização (ns): " + serializationDuration);

        Type studentListType = new TypeToken<ArrayList<Student>>() {
        }.getType();
        long startDeserializationTime = System.nanoTime();
        List<Student> deserializedStudents = gson.fromJson(json, studentListType); // Deserialização
        long endDeserializationTime = System.nanoTime();
        long deserializationDuration = endDeserializationTime - startDeserializationTime;

        System.out.println("\nObjetos Deserializados:");
        for (Student student : deserializedStudents) {
            System.out.println(student.getName() + " - " + student.getAge() + " - " + student.getId());
        }

        System.out.println("Tempo de Deserialização (ns): " + deserializationDuration);
    }
}