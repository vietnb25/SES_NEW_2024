package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataInverter1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationElectricalResponse {

    private Integer id;

    private Long deviceId;

    private Integer aphaA;

    private Integer aphaB;

    private Integer aphaC;

    private Integer pPVphA;

    private Integer pPVphB;

    private Integer pPVphC;

    private Double w;

    private Integer hz;

    private Integer pF;

    private Long wh;

    private Double dCW;

    private Integer dCV;

    private Integer dCA;

    private String sentDate;

    private Long transactionDate;

    public OperationElectricalResponse(final DataInverter1 inverter1) {
        this.id = inverter1.getId();
        this.deviceId = inverter1.getDeviceId();
        this.aphaA = inverter1.getAphaA();
        this.aphaB = inverter1.getAphaB();
        this.aphaC = inverter1.getAphaC();
        this.pPVphA = inverter1.getPPVphA();
        this.pPVphB = inverter1.getPPVphB();
        this.pPVphC = inverter1.getPPVphC();
        this.w = inverter1.getW();
        this.hz = inverter1.getHz();
        this.pF = inverter1.getPF();
        this.wh = inverter1.getWh();
        this.dCW = inverter1.getDCW();
        this.dCV = inverter1.getDCV();
        this.dCA = inverter1.getDCA();
        this.sentDate = inverter1.getSentDate();
        this.transactionDate = inverter1.getTransactionDate();
    }
}
