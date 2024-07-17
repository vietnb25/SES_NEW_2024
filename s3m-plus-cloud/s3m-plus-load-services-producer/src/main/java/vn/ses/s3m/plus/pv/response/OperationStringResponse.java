package vn.ses.s3m.plus.pv.response;

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

    private String deviceName;

    private Integer deviceType;

    private String deviceCode;

    private Integer InID;

    private Integer InEvt;

    private Integer LOW_VOLTAGE;

    private Integer LOW_POWER;

    private Integer LOW_EFFICIENCY;

    private Integer CURRENT;

    private Integer VOLTAGE;

    private Integer POWER;

    private Integer PR;

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

    private Float IdcStr;

    private Float InDCAhr;

    private Float VdcStr;

    private Float PdcStr;

    private Float EpStr;

    private Float InDCWh;

    private Float InDCPR;

    private Float InN;

    private Float Tstr;

    private String sentDate;

    private Long transactionDate;

    public OperationStringResponse(final DataString1 string1) {
        this.id = string1.getId();
        this.deviceId = string1.getDeviceId();
        this.deviceName = string1.getDeviceName();
        this.deviceType = string1.getDeviceType();
        this.deviceCode = string1.getDeviceCode();
        this.InID = string1.getInID();
        this.InEvt = string1.getInEvt();
        this.LOW_VOLTAGE = string1.getLOW_VOLTAGE();
        this.LOW_POWER = string1.getLOW_POWER();
        this.LOW_EFFICIENCY = string1.getLOW_EFFICIENCY();
        this.CURRENT = string1.getCURRENT();
        this.VOLTAGE = string1.getVOLTAGE();
        this.POWER = string1.getPOWER();
        this.PR = string1.getPR();
        this.DISCONNECTED = string1.getDISCONNECTED();
        this.FUSE_FAULT = string1.getFUSE_FAULT();
        this.COMBINER_FUSE_FAULT = string1.getCOMBINER_FUSE_FAULT();
        this.COMBINER_CABINET_OPEN = string1.getCOMBINER_CABINET_OPEN();
        this.TEMP = string1.getTEMP();
        this.GROUNDFAULT = string1.getGROUNDFAULT();
        this.REVERSED_POLARITY = string1.getREVERSED_POLARITY();
        this.INCOMPATIBLE = string1.getINCOMPATIBLE();
        this.COMM_ERROR = string1.getCOMM_ERROR();
        this.INTERNAL_ERROR = string1.getINTERNAL_ERROR();
        this.THEFT = string1.getTHEFT();
        this.ARC_DETECTED = string1.getARC_DETECTED();
        this.IdcStr = string1.getIdcStr();
        this.InDCAhr = string1.getInDCAhr();
        this.VdcStr = string1.getVdcStr();
        this.PdcStr = string1.getPdcStr();
        this.EpStr = string1.getEpStr();
        this.InDCWh = string1.getInDCWh();
        this.InDCPR = string1.getInDCPR();
        this.InN = string1.getInN();
        this.Tstr = string1.getTstr();
        this.sentDate = string1.getSentDate();
        this.transactionDate = string1.getTransactionDate();
    }
}
