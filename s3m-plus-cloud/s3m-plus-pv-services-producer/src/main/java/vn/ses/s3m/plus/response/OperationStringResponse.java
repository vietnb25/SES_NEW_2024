package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataString1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationStringResponse {

    private Long id;

    private Long deviceId;

    private Integer inId;

    private Integer inEvt;

    private Integer lowVoltage;

    private Integer lowPower;

    private Integer lowEfficiency;

    private Integer current;

    private Integer voltage;

    private Integer power;

    private Integer pr;

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

    private Integer inDca;

    private Integer inDCAhr;

    private Integer inDCV;

    private Integer inDCW;

    private Integer inDCWh;

    private Integer inDCPR;

    private Integer inN;

    private String sentDate;

    private Long transactionDate;

    public OperationStringResponse(final DataString1 string1) {
        this.id = string1.getId();
        this.deviceId = string1.getDeviceId();
        this.inId = string1.getInId();
        this.inEvt = string1.getInEvt();
        this.lowVoltage = string1.getLowVoltage();
        this.lowPower = string1.getLowPower();
        this.lowEfficiency = string1.getLowEfficiency();
        this.current = string1.getCurrent();
        this.voltage = string1.getVoltage();
        this.power = string1.getPower();
        this.pr = string1.getPr();
        this.disconnected = string1.getDisconnected();
        this.fuseFault = string1.getFuseFault();
        this.combinerFuseFault = string1.getCombinerFuseFault();
        this.combinerCabinetOpen = string1.getCombinerCabinetOpen();
        this.temp = string1.getTemp();
        this.groundfault = string1.getGroundfault();
        this.reversedPolarity = string1.getReversedPolarity();
        this.incompatible = string1.getIncompatible();
        this.commError = string1.getCommError();
        this.internalError = string1.getInternalError();
        this.theft = string1.getTheft();
        this.arcDetected = string1.getArcDetected();
        this.inDca = string1.getInDca();
        this.inDCAhr = string1.getInDCAhr();
        this.inDCV = string1.getInDCV();
        this.inDCW = string1.getInDCW();
        this.inDCWh = string1.getInDCWh();
        this.inDCPR = string1.getInDCPR();
        this.inN = string1.getInN();
        this.sentDate = string1.getSentDate();
        this.transactionDate = string1.getTransactionDate();
    }
}
