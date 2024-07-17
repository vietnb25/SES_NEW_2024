package vn.ses.s3m.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class S3mPlusConfigApplication {

    public static void main(final String[] args) {
        SpringApplication.run(S3mPlusConfigApplication.class, args);
    }

}
