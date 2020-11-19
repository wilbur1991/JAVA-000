package wilbur.demo.startersample.beans;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wilbur.demo.spring.boot.autoconfigure.properties.Student;

import javax.annotation.Resource;

@Data
@Component
public class School implements ISchool {

    // Resource
    @Autowired(required = true) //primary
    Klass class1;

    @Resource
    Student student100;

    @Override
    public void ding(){

        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);

    }

}
