import { useEffect, useState } from "react";
import './detailhistory.css';
const DetailHistory = (props) => {
    const DetailHistory = props.schedules;


    const [Details, setDetails] = useState([]);

    const getDetailHistorys = async () => {
        setDetails(DetailHistory);
    }
    useEffect(() => {
        document.title = "Lưu trữ";
        getDetailHistorys();

    })
    return (
        <>
            <div className="detail-container">
                <div className="modal-body">
                    <div className="btnclose" onClick={(state) => { props.closeDetail() }}>x</div>
                    <h5 className="title">History Detail</h5>
                    <hr />
                    <div className="tab-content">
                        <table className="tbl-overview">
                            <thead>
                                <tr>
                                    <th scope="col" style={{ width: "50px" }} >TT</th>
                                    <th scope="col" style={{ width: "500px" }}>Khu vực/Nhà máy</th>
                                    <th scope="col" style={{ width: "180px" }}>Công suất định mức</th>
                                    <th scope="col" style={{ width: "180px" }}>Công suất tiết giảm</th>
                                    <th scope="col" style={{ width: "300px" }}>Công suất cho phép phát</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    Details.length > 0 ?
                                        Details.map((schedule, index) => {
                                            return (
                                                <tr key={index}>
                                                    <td style={{ textAlign: "center" }}>{schedule.id}</td>
                                                    <td>{schedule.addRess}</td>
                                                    <td style={{ textAlign: "right" }}>{(schedule.congSuatTietGiam + schedule.congSuatChoPhep).toFixed(3)}</td>
                                                    <td style={{ textAlign: "right" }}>{schedule.congSuatTietGiam}</td>
                                                    <td style={{ textAlign: "right" }}>{schedule.congSuatChoPhep}</td>

                                                </tr>

                                            )


                                        })
                                        : <tr>
                                            <td colSpan={"5"}>Không có dữ liệu</td>
                                        </tr>
                                }
                            </tbody>
                        </table>
                    </div>
                    <hr />
                    <button className="btnclose2" onClick={(state) => props.closeDetail()}>Close</button>
                </div>
            </div>
        </>
    )


}
export default DetailHistory;