package wilbur.demo.spring.spring02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wilbur.demo.spring.aop.ISchool;
import wilbur.demo.spring.beans.annotation.ComponentDemo;
import wilbur.demo.spring.spring01.Student;

public class SpringDemo01 {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Student studentUsingXml = (Student) context.getBean("student123");
        System.out.println(studentUsingXml.toString());


        Student studentUsingBean= (Student) context.getBean("studentUsingBean");
        System.out.println(studentUsingBean.toString());
        ComponentDemo componentDemo = context.getBean(ComponentDemo.class);
        System.out.println(componentDemo.getClass());
        Klass class1 = context.getBean(Klass.class);
        System.out.println(class1);

        School school = context.getBean(School.class);
        school.ding();

        class1.dong();

        System.out.println("   context.getBeanDefinitionNames() ===>> "+ String.join(",", context.getBeanDefinitionNames()));


    }
}
