package wilbur.demo.startersample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import wilbur.demo.spring.boot.autoconfigure.properties.Student;
import wilbur.demo.startersample.beans.Klass;
import wilbur.demo.startersample.beans.School;

@SpringBootApplication
public class StarterSampleApplication {

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(StarterSampleApplication.class, args);
        //获取自动加载的Student、Klass、School Bean
        Student student = (Student) ctx.getBean("student");
        System.out.println(student.toString());
        Klass klass = ctx.getBean(Klass.class);
        klass.dong();
        School school = ctx.getBean(School.class);
        school.ding();
    }

}
