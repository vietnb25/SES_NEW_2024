package vn.ses.s3m.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class S3mPlusLoadServicesApplication {

    public static void main(final String[] args) {
        SpringApplication.run(S3mPlusLoadServicesApplication.class, args);
    }

}
