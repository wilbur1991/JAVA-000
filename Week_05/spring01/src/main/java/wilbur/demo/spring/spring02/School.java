package wilbur.demo.spring.spring02;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import wilbur.demo.spring.aop.ISchool;
import wilbur.demo.spring.spring01.Student;

import javax.annotation.Resource;

@Data
public class School implements ISchool {

    // Resource
    @Autowired(required = true) //primary
    Klass class1;

    @Resource(name = "student123")
    Student student123;

    @Override
    public void ding(){

        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student123);

    }

}
