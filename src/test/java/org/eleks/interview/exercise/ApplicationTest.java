package org.eleks.interview.exercise;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                // satisfy @Value injections if your app wires these
                "org.eleks.interview.exercise.weather.api.url=http://localhost",
                "org.eleks.interview.exercise.weather.api.key=dummy",
                // if you have a CSV-backed converter, point it to a test resource
                "org.eleks.interview.exercise.city.codes.path=city_codes.csv"
        }
)
public class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertNotNull(context);
    }

    @Test
    public void main_invokes_SpringApplication_run() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(null);

            String[] args = new String[] {"--spring.profiles.active=test"};
            Application.main(args);

            mocked.verify(() -> SpringApplication.run(eq(Application.class), any(String[].class)));
        }
    }
}
