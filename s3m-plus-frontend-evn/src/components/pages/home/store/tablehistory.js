import React, { useEffect } from "react";
import { useState } from "react";
import DetailHistory from "./detailhistory";
import './index.css';

const TableHistory = (props) => {
    const history = props.history;
    const schedule = props.schedules;
    const [historys, setHistorys] = useState([]);
    const [displayDetail, setDisplayDetail] = useState(false);
    const [historyId, setHistoryId] = useState('');
    const [Allschedules, setAllSchedules] = useState([]);


    const getHistorys = async () => {
        setHistorys(history);
        setAllSchedules(schedule);
    }

    useEffect(() => {
        document.title = "Lưu Trữ";
        getHistorys();

    })
    const onClickTr = async (hsID, date, timeInsert) => {
        setHistoryId(hsID);

        setDisplayDetail(!displayDetail);
        console.log(historyId);
    }
    const closeDetail = async () => {
        setDisplayDetail(!displayDetail);
    }
    return (
        <div className="table-container">
            {
                displayDetail ?
                    <DetailHistory
                        closeDetail={closeDetail}
                        schedules={Allschedules.filter(function (e) {
                            return e.historyId === historyId;
                        })}

                    />
                    : <></>
            }
            <div className="tab-content">
                <table className="tbl-overview">
                    <thead>
                        <tr>
                            <th scope="col" style={{width: '20px'}} >TT</th>
                            <th scope="col" style={{width: '120px'}}>Từ ngày</th>
                            <th scope="col" style={{width: '120px'}}>Đến ngày</th>
                            <th scope="col" style={{width: '120px'}}>Khung giờ</th>
                            <th scope="col" >Công suất định mức (MW)</th>
                            <th scope="col" >Công suất tiết giảm (MW)</th>
                            <th scope="col" >Công suất cho phép (MW)</th>
                            <th scope="col" style={{width: '180px'}}>Thời gian thiết lập</th>
                            <th scope="col" >Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            historys.map((historys, index) => {
                                return (
                                    <tr key={index} onClick={(state) => onClickTr(historys.historyId, historys.fromDate, historys.timeInsert)}>
                                        <td style={{ textAlign: "center" }}>{historys.historyId}</td>
                                        <td style={{ textAlign: "center" }}>{historys.fromDate}</td>
                                        <td style={{ textAlign: "center" }}>{historys.toDate}</td>
                                        <td style={{ textAlign: "center" }}>{historys.timeFrame}</td>
                                        <td style={{ textAlign: "right" }}>{historys.congSuatDinhMuc}</td>
                                        <td style={{ textAlign: "right" }}>{historys.congSuatTietGiam}</td>
                                        <td style={{ textAlign: "right" }}>{historys.congSuatChoPhep}</td>
                                        <td style={{ textAlign: "center" }}>{historys.timeInsert.toString()}</td>
                                        <td style={{ textAlign: "center" }}>{historys.status === 0 ? 'Đã xóa' : 'Hết Hạn'}</td>
                                    </tr>

                                )
                            })

                        }
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default TableHistory;