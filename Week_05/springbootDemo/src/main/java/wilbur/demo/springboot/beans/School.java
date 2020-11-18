package wilbur.demo.springboot.beans;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Data
@Component
public class School implements ISchool {

    // Resource
    @Autowired(required = true) //primary
    Klass class1;

    @Resource(name = "student100")
    Student student100;

    @Override
    public void ding(){

        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);

    }

}
