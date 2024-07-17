package vn.ses.s3m.plus.batch.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import vn.ses.s3m.plus.batch.mapper.DataAccumulationsMapper;
import vn.ses.s3m.plus.batch.service.*;

public class ScheduleJob {

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

    @Autowired
    DataAccumulationsService dataAccumulationsService;

    @Scheduled (cron = "0 */5 * * * ?")
    public void scheduleFixedDelayTask() {
        dataLoadFrame1ViewService.doProcess();
    }

//    @Scheduled (cron = "0 */5 * * * ?")
//    public void scheduleFixedDelayTaskCombiner() {
//        dataCombiner1ViewService.doProcess();
//    }

    @Scheduled (cron = "0 */5 * * * ?")
    public void scheduleFixedDelayTaskInverter() {
        dataInverter1ViewService.doProcess();
    }

    @Scheduled (cron = "0 */5 * * * ?")
    public void scheduleFixedDelayTaskDataPqs() {
        dataPqsService.doProcess();
    }

    @Scheduled (cron = "0 */15 * * * ?")
    public void scheduleAccumulations() {
        dataAccumulationsService.doProcess();
    }

//    @Scheduled (cron = "0 */5 * * * ?")
//    public void scheduleFixedDelayTaskPlanEnergy() {
//        dataPlanEnergyService.doProcess();
//    }
    
    @Scheduled (cron = "0 0 0 31 12 *")
    public void scheduleFixedDelayTaskSchema() {
    	dataSchemaService.doProcess();
    }
}
