package vn.ses.s3m.plus.pv.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.DataCombiner1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOperationCombinerResponse {

    private Long id;

    private Long deviceId;

    private Integer DCAMax;

    private Integer N;

    private Integer Evt;

    private Integer LOW_VOLTAGE;

    private Integer LOW_POWER;

    private Integer LOW_EFFICIENCY;

    private Integer CURRENT;

    private Integer VOLTAGE;

    private Integer POWER;

    private Integer DISCONNECTED;

    private Integer FUSE_FAULT;

    private Integer COMBINER_FUSE_FAULT;

    private Integer COMBINER_CABINET_OPEN;

    private Integer TEMP;

    private Integer GROUNDFAULT;

    private Integer REVERSED_POLARITY;

    private Integer INCOMPATIBLE;

    private Integer COMM_ERROR;

    private Integer INTERNAL_ERROR;

    private Integer THEFT;

    private Integer ARC_DETECTED;

    private Float IdcCombiner;

    private Float DCAh;

    private Float VdcCombiner;

    private Float T;

    private Float PdcCombiner;

    private Float PR;

    private Float EpCombiner;

    private String sentDate;

    private Long transactionDate;

    public ChartOperationCombinerResponse(final DataCombiner1 combiner1) {
        this.id = combiner1.getId();
        this.deviceId = combiner1.getDeviceId();
        this.DCAMax = combiner1.getDCAMax();
        this.N = combiner1.getN();
        this.Evt = combiner1.getEvt();
        this.LOW_VOLTAGE = combiner1.getLOW_VOLTAGE();
        this.LOW_POWER = combiner1.getLOW_POWER();
        this.LOW_EFFICIENCY = combiner1.getLOW_EFFICIENCY();
        this.CURRENT = combiner1.getCURRENT();
        this.VOLTAGE = combiner1.getVOLTAGE();
        this.POWER = combiner1.getPOWER();
        this.DISCONNECTED = combiner1.getDISCONNECTED();
        this.FUSE_FAULT = combiner1.getFUSE_FAULT();
        this.COMBINER_FUSE_FAULT = combiner1.getCOMBINER_FUSE_FAULT();
        this.COMBINER_CABINET_OPEN = combiner1.getCOMBINER_CABINET_OPEN();
        this.TEMP = combiner1.getTEMP();
        this.GROUNDFAULT = combiner1.getGROUNDFAULT();
        this.REVERSED_POLARITY = combiner1.getREVERSED_POLARITY();
        this.INCOMPATIBLE = combiner1.getINCOMPATIBLE();
        this.COMM_ERROR = combiner1.getCOMM_ERROR();
        this.INTERNAL_ERROR = combiner1.getINTERNAL_ERROR();
        this.THEFT = combiner1.getTHEFT();
        this.ARC_DETECTED = combiner1.getARC_DETECTED();
        this.IdcCombiner = combiner1.getIdcCombiner();
        this.DCAh = combiner1.getDCAh();
        this.VdcCombiner = combiner1.getVdcCombiner();
        this.T = combiner1.getT();
        this.PdcCombiner = combiner1.getPdcCombiner();
        this.PR = combiner1.getPR();
        this.EpCombiner = combiner1.getEpCombiner();
        DateFormat parser = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        Date sentDate1 = null;
        try {
            sentDate1 = parser.parse(combiner1.getSentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        this.sentDate = formatter.format(sentDate1);
        this.transactionDate = combiner1.getTransactionDate();
    }
}
