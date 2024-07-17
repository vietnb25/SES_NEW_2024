package vn.ses.s3m.plus.batch.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "s3m_instant_data")
public class DataInstant {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "device_id")
    private Integer deviceId;
    @Column(name = "ia")
    private Double ia;
    @Column(name = "ib")
    private Double ib;
    @Column(name = "ic")
    private Double ic;
    @Column(name = "sent_date")
    private Timestamp sentDate;
}
