package vn.ses.s3m.plus.batch.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import vn.ses.s3m.plus.batch.service.DataCombiner1ViewService;
import vn.ses.s3m.plus.batch.service.DataInverter1ViewService;
import vn.ses.s3m.plus.batch.service.DataLoadFrame1ViewService;

@Configuration
@EnableScheduling
public class ScheduleTaskConfiguration {

    @Autowired
    DataLoadFrame1ViewService dataLoadFrame1ViewService;

    @Autowired
    DataCombiner1ViewService dataCombiner1ViewService;

    @Autowired
    DataInverter1ViewService dataInverter1ViewService;

    @Bean
    @ConditionalOnProperty (value = "jobs.runAll.enabled", matchIfMissing = true, havingValue = "false")
    public ScheduleJob scheduleJob() {
        return new ScheduleJob();
    }

    @Bean
    @ConditionalOnProperty (value = "jobs.runAll.enabled", matchIfMissing = true, havingValue = "true")
    public ScheduleAllJob scheduleAllJob() {
        return new ScheduleAllJob();
    }

}
