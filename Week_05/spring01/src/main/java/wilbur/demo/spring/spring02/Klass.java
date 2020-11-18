package wilbur.demo.spring.spring02;

import lombok.Data;
import wilbur.demo.spring.spring01.Student;

import java.util.List;

@Data
public class Klass {

    List<Student> students;

    public void dong(){
        System.out.println(this.getStudents());
    }

}
