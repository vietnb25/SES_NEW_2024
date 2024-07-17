package vn.ses.s3m.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main Class Application.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class S3mPlusAuthApplication {
    public static void main(final String[] args) {
        SpringApplication.run(S3mPlusAuthApplication.class, args);
    }
}
