package wilbur.demo.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SpringbootApplication.class, args);
    }

}
