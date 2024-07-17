package vn.ses.s3m.plus.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class DataRmuDrawer1 {

    @Id
    @Column (name = "id")
    private Long id;

    @Column (name = "device_id")
    private Long deviceId;

    @Column (name = "P_Total")
    private Long pTotal;

    @Column (name = "EP")
    private Long ep;

    @Column (name = "sent_date")
    private String sentDate;

    @Column (name = "transaction_date")
    private Long transactionDate;

}
