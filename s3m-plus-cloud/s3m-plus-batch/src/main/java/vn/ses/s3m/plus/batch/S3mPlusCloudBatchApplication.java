package vn.ses.s3m.plus.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class S3mPlusCloudBatchApplication {

    public static void main(String[] args) {
        log.info("S3mPlusCloudBatchApplication Start!");
        SpringApplication.run(S3mPlusCloudBatchApplication.class, args);
    }
}
