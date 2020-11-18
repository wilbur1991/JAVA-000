package wilbur.demo.springboot.beans;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class Klass {


    private List<Student> students;

    @Autowired
    public void setStudents(List<Student> students){
        this.students = students;
    }

    public void dong(){
        System.out.println(this.getStudents());
    }

}
