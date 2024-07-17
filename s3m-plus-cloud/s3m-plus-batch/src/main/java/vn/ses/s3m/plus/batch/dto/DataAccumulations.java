package vn.ses.s3m.plus.batch.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "data_accumulations")
public class DataAccumulations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "ep")
    private Double ep;
    @Column(name = "device_id")
    private Integer deviceId;
    @Column(name = "view_time")
    private Integer typeTime;
    @Column(name = "view_time")
    private String viewTime;
    @Column(name = "sent_date")
    private Timestamp sentDate;
}
