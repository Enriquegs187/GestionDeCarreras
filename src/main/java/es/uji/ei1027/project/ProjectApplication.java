package es.uji.ei1027.project;

import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProjectApplication {
    private static final Logger LOG =
            Logger.getLogger(ProjectApplication.class.getName());

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectApplication.class).run(args);
    }

}
