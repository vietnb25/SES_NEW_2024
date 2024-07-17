package vn.ses.s3m.plus.batch.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import vn.ses.s3m.plus.batch.service.DataCombiner1ViewService;
import vn.ses.s3m.plus.batch.service.DataInverter1ViewService;
import vn.ses.s3m.plus.batch.service.DataLoadFrame1ViewService;
import vn.ses.s3m.plus.batch.service.DataPlanEnergyService;
import vn.ses.s3m.plus.batch.service.DataPqsService;
import vn.ses.s3m.plus.batch.service.DataRmuDrawer1ViewService;
import vn.ses.s3m.plus.batch.service.DataSchemaService;

public class ScheduleAllJob {

    @Autowired
    DataLoadFrame1ViewService dataLoadFrame1ViewService;

    @Autowired
    DataInverter1ViewService dataInverter1ViewService;

    @Autowired
    DataCombiner1ViewService dataCombiner1ViewService;

    @Autowired
    DataRmuDrawer1ViewService dataRmuDrawer1ViewService;

    @Autowired
    DataPqsService dataPqsService;

    @Autowired
    DataPlanEnergyService dataPlanEnergyService;
    
    @Autowired
    DataSchemaService dataSchemaService;

    @Scheduled (cron = "*/1 * * * * ?")
    public void scheduleFixedDelayTask() {
        boolean isNotData = dataLoadFrame1ViewService.doProcess();
        if (isNotData) {
            System.exit(0);
        }
    }

    @Scheduled (cron = "*/1 * * * * ?")
    public void scheduleFixedDelayTaskCombiner() {
        boolean isNotData = dataCombiner1ViewService.doProcess();
        if (isNotData) {
            System.exit(0);
        }
    }

    @Scheduled (cron = "*/1 * * * * ?")
    public void scheduleFixedDelayTaskInverter() {
        boolean isNotData = dataInverter1ViewService.doProcess();
        if (isNotData) {
            System.exit(0);
        }
    }

    @Scheduled (cron = "*/1 * * * * ?")
    public void scheduleFixedDelayTaskDataPqs() {
        boolean isNotData = dataPqsService.doProcess();
        if (isNotData) {
            System.exit(0);
        }
    }

//    @Scheduled (cron = "*/1 * * * * ?")
//    public void scheduleFixedDelayTaskPlanEnergy() {
//        boolean isNotData = dataPlanEnergyService.doProcess();
//        if (isNotData) {
//            System.exit(0);
//        }
//    }
    
    @Scheduled (cron = "*/1 * * * * ?")
    public void scheduleFixedDelayTaskSchema() {
        boolean isNotData = dataSchemaService.doProcess();
        if (isNotData) {
            System.exit(0);
        }
    }
}
