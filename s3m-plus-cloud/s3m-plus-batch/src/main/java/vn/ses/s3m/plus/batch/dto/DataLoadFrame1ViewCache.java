package vn.ses.s3m.plus.batch.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table (name = "s3m_data_load_frame_1_2022_view_cache")
public class DataLoadFrame1ViewCache implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Integer id;

    @Column (name = "device_id")
    private Long deviceId;

    @Column (name = "view_type")
    private Integer viewType;

    @Column (name = "ep")
    private Integer ep;
}
