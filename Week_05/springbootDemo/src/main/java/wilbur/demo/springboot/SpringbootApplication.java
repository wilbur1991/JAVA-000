package wilbur.demo.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import wilbur.demo.springboot.beans.Klass;
import wilbur.demo.springboot.beans.School;
import wilbur.demo.springboot.beans.Student;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SpringbootApplication.class, args);
        //获取自动加载的Student、Klass、School Bean
        Student student = (Student) ctx.getBean("student100");
        System.out.println(student.toString());
        Klass klass = ctx.getBean(Klass.class);
        klass.dong();
        School school = ctx.getBean(School.class);
        school.ding();
    }

}
