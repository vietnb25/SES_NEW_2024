package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataCombiner1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationCombinerResponse {

    private Long id;

    private Long deviceId;

    private Integer dcaMax;

    private Integer n;

    private Integer evt;

    private Integer lowVoltage;

    private Integer lowPower;

    private Integer lowEfficiency;

    private Integer current;

    private Integer voltage;

    private Integer power;

    private Integer disconnected;

    private Integer fuseFault;

    private Integer combinerFuseFault;

    private Integer combinerCabinetOpen;

    private Integer temp;

    private Integer groundfault;

    private Integer reversedPolarity;

    private Integer incompatible;

    private Integer commError;

    private Integer internalError;

    private Integer theft;

    private Integer arcDetected;

    private Integer dca;

    private Integer dcaH;

    private Integer dcv;

    private Integer t;

    private Integer dcw;

    private Integer pr;

    private Integer wattHours;

    private String sentDate;

    private Long transactionDate;

    public OperationCombinerResponse(final DataCombiner1 combiner1) {
        this.id = combiner1.getId();
        this.deviceId = combiner1.getDeviceId();
        this.dcaMax = combiner1.getDcaMax();
        this.n = combiner1.getN();
        this.evt = combiner1.getEvt();
        this.lowVoltage = combiner1.getLowVoltage();
        this.lowPower = combiner1.getLowPower();
        this.lowEfficiency = combiner1.getLowEfficiency();
        this.current = combiner1.getCurrent();
        this.voltage = combiner1.getVoltage();
        this.power = combiner1.getPower();
        this.disconnected = combiner1.getDisconnected();
        this.fuseFault = combiner1.getFuseFault();
        this.combinerFuseFault = combiner1.getCombinerFuseFault();
        this.combinerCabinetOpen = combiner1.getCombinerCabinetOpen();
        this.temp = combiner1.getTemp();
        this.groundfault = combiner1.getGroundfault();
        this.reversedPolarity = combiner1.getReversedPolarity();
        this.incompatible = combiner1.getIncompatible();
        this.commError = combiner1.getCommError();
        this.internalError = combiner1.getInternalError();
        this.theft = combiner1.getTheft();
        this.arcDetected = combiner1.getArcDetected();
        this.dca = combiner1.getDca();
        this.dcaH = combiner1.getDcaH();
        this.dcv = combiner1.getDcv();
        this.t = combiner1.getT();
        this.dcw = combiner1.getDcw();
        this.pr = combiner1.getPr();
        this.wattHours = combiner1.getWattHours();
        this.sentDate = combiner1.getSentDate();
        this.transactionDate = combiner1.getTransactionDate();
    }

}
