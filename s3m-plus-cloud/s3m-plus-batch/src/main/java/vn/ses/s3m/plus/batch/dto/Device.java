package vn.ses.s3m.plus.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table (name = "s3m_device")
public class Device {

    @Id
    @Column (name = "id")
    private Long id;

    @Column (name = "device_id")
    private Long deviceId;

    @Column (name = "device_code")
    private String deviceCode;

    @Column (name = "device_name")
    private String deviceName;

    @Column (name = "rho")
    private Double rho;

    @Column (name = "cable_length")
    private Double cableLength;

    private Long deviceType;

}
