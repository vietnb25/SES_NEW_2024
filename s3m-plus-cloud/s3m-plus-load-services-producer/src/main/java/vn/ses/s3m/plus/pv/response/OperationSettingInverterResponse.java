package vn.ses.s3m.plus.pv.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataInverter1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationSettingInverterResponse {

    private Long id;

    private Long deviceId;

    private Float wmax;

    private Float vref;

    private Float vAMax;

    private Float vAMaxQ1;

    private Float vAMaxQ2;

    private Float vAMaxQ3;

    private Float vAMaxQ4;

    private Float fNormal;

    private Float outPFSet;

    private String sentDate;

    private Long transactionDate;

    public OperationSettingInverterResponse(final DataInverter1 inverter1) {
        this.id = inverter1.getId();
        this.deviceId = inverter1.getDeviceId();
        this.wmax = inverter1.getWmax();
        this.vref = inverter1.getVref();
        this.vAMax = inverter1.getVAMax();
        this.vAMaxQ1 = inverter1.getVAMaxQ1();
        this.vAMaxQ2 = inverter1.getVAMaxQ2();
        this.vAMaxQ3 = inverter1.getVAMaxQ3();
        this.vAMaxQ4 = inverter1.getVAMaxQ4();
        this.fNormal = inverter1.getF_normal();
        this.outPFSet = inverter1.getOutPFSet();
        this.sentDate = inverter1.getSentDate();
        this.transactionDate = inverter1.getTransactionDate();
    }

}
