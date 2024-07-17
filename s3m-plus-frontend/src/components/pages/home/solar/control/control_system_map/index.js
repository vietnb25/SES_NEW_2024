import React, { useState, useEffect } from "react";
import controlPVService from "../../../../../../services/ControlPVService";
import CONS from "../../../../../../constants/constant";
import moment from "moment";

const ControlSystemMap = (props) => {

    const [controlSystem, setControlSystem] = useState([]);
    const [controlSystemAll, setControlSystemALl] = useState([]);
    const [controlSystemShowed, setControlSystemShow] = useState(1);

    const getControls = async () => {
        let res = await controlPVService.getControlSystem(props.systemMapId);
        if (res.status === 200) {
            setControlSystem(res.data.controls);
            console.log("data:  ",res.data.controls);
            setControlSystemALl(res.data.controlsAll);
        }
    };

    const changeControlSystemShow = (e) => {
        if (e === 1) {
            setControlSystemShow(e);
            document.getElementById("historySystem").style.display = "";
            document.getElementById("historySystemAll").style.display = "none";
        } else {
            setControlSystemShow(e);
            document.getElementById("historySystemAll").style.display = "";
            document.getElementById("historySystem").style.display = "none";
        }
    };

    useEffect(() => {
        getControls();
    }, [props])

    return (
        <>
            <div id="btn-send" style={{ width: "100%", textAlign: "left", marginLeft: "10px", marginTop: "35px"}}>
                <button className={controlSystemShowed === 1 ? "btn btn-outline-warning active" : "btn btn-outline-warning"} style={{ width: "180px", marginBottom: "20px", padding: "5px 10px" }} onClick={() => changeControlSystemShow(1)} >Lệnh hiện tại</button>
                <button className={controlSystemShowed === 2 ? "btn btn-outline-warning active" : "btn btn-outline-warning"} style={{ width: "180px", marginBottom: "20px", padding: "5px 10px" }} onClick={() => changeControlSystemShow(2)} >Lịch sử lệnh</button>
            </div>
            <table className="table tbl-overview tbl-power" id="historySystem">
                <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th scope="col" style={{ width: "50px", textAlign: "center" }}>TT</th>
                        <th scope="col" style={{ width: "200px", textAlign: "center" }}>Vị Trí</th>
                        <th scope="col" style={{ width: "110px", textAlign: "center" }}>Ngày</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Từ giờ</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Đến giờ</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian thiết lập</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian xử lý</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất định mức(kW)</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất tiết giảm(kW)</th>
                        <th scope="col" style={{ width: "150px", textAlign: "center" }}>Trạng Thái <br /> Tiết Giảm</th>
                        <th scope="col" style={{ width: "150px", textAlign: "center" }}>Trạng Thái <br /> Cài Đặt Lại</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian hủy</th>
                    </tr>
                </thead>
                {
                    controlSystem?.length > 0 ?
                        <tbody style={{ display: "block", width: "100%", maxHeight: "600px", overflow: "auto"  }}>
                            {
                                controlSystem?.map(
                                    (itemControl, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                        <td className="text-center" width="50px">{index + 1}</td>
                                        <td className="text-center" width="200px">{itemControl.addRess}</td>
                                        <td className="text-center" width="110px">{itemControl.timeSet}</td>
                                        <td className="text-center" width="100px">{itemControl.fromTime}</td>
                                        <td className="text-center" width="100px">{itemControl.toTime}</td>
                                        <td className="text-center" width="180px">{moment(itemControl.createDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center" width="180px">{(itemControl.status == 1 || itemControl.status2 == 1) && moment(itemControl.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-right">{itemControl.congSuatDinhMuc}</td>
                                        <td className="text-right">{itemControl.congSuatTietGiam}</td>
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === null) &&
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Chưa xử lý</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '0') &&
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Đã gửi</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '1') &&
                                            <td style={{ background: "#40ff00", width: "150px" }} className="text-center">Đã xử lý</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '2') &&
                                            <td style={{ background: "#ff0000", width: "150px" }} className="text-center">Hết hạn</td>
                                        }

                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === null) &&
                                            <>
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Chưa xử lý</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '0') &&
                                            <>
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Đã gửi</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '1') &&
                                            <>
                                            <td style={{ background: "#40ff00", width: "150px" }} className="text-center">Đã xử lý</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '2') &&
                                            <>
                                            <td style={{ background: "#ff0000", width: "150px" }} className="text-center">Hết hạn</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 1) && 
                                            <>
                                                <td className="text-center" colSpan={2}>Đã hủy</td>
                                                
                                                <td className="text-center" width="180px">{moment(itemControl.deleteDate).format(CONS.DATE_FORMAT)}</td>
                                            </>
                                        }


                                    </tr>
                                )
                            }
                        </tbody>
                        :
                        <tbody style={{ display: "table", width: "100%" }}>
                            <tr>
                                <td className="text-center" colSpan={12}>Không có dữ liệu</td>
                            </tr>
                        </tbody>
                }
            </table>

            <table className="table tbl-overview tbl-power" id="historySystemAll" style={{ display: "none" }}>
                <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th scope="col" style={{ width: "50px", textAlign: "center" }}>TT</th>
                        <th scope="col" style={{ width: "200px", textAlign: "center" }}>Vị Trí</th>
                        <th scope="col" style={{ width: "110px", textAlign: "center" }}>Ngày</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Từ giờ</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Đến giờ</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian thiết lập</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian xử lý</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất định mức(kW)</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất tiết giảm(kW)</th>
                        <th scope="col" style={{ width: "150px", textAlign: "center" }}>Trạng Thái <br /> Tiết Giảm</th>
                        <th scope="col" style={{ width: "150px", textAlign: "center" }}>Trạng Thái <br /> Cài Đặt Lại</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian hủy</th>
                    </tr>
                </thead>
                {
                    controlSystemAll?.length > 0 ?
                        <tbody style={{ display: "block", width: "100%", maxHeight: "640px", overflow: "auto"}}>
                            {
                                controlSystemAll?.map(
                                    (itemControl, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                        <td className="text-center" width="50px">{index + 1}</td>
                                        <td className="text-center" width="200px">{itemControl.addRess}</td>
                                        <td className="text-center" width="110px">{itemControl.timeSet}</td>
                                        <td className="text-center" width="100px">{itemControl.fromTime}</td>
                                        <td className="text-center" width="100px">{itemControl.toTime}</td>
                                        <td className="text-center" width="180px">{moment(itemControl.createDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-center" width="180px">{(itemControl.status == 1 || itemControl.status2 == 1) && moment(itemControl.updateDate).format(CONS.DATE_FORMAT)}</td>
                                        <td className="text-right">{itemControl.congSuatDinhMuc}</td>
                                        <td className="text-right">{itemControl.congSuatTietGiam}</td>
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === null) &&
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Chưa xử lý</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '0') &&
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Đã gửi</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '1') &&
                                            <td style={{ background: "#40ff00", width: "150px" }} className="text-center">Đã xử lý</td>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status === '2') &&
                                            <td style={{ background: "#ff0000", width: "150px" }} className="text-center">Hết hạn</td>
                                        }

                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === null) &&
                                            <>
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Chưa xử lý</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '0') &&
                                            <>
                                            <td style={{ background: "#ffff00", width: "150px" }} className="text-center">Đã gửi</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '1') &&
                                            <>
                                            <td style={{ background: "#40ff00", width: "150px" }} className="text-center">Đã xử lý</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 0 && itemControl.status2 === '2') &&
                                            <>
                                            <td style={{ background: "#ff0000", width: "150px" }} className="text-center">Hết hạn</td>
                                            <td style={{ width: "180px", textAlign: "center" }}></td>
                                            </>
                                        }
                                        {
                                            (itemControl.deleteFlag === 1) && 
                                            <>
                                                <td className="text-center" colSpan={2}>Đã hủy</td>
                                                
                                                <td className="text-center" width="180px">{moment(itemControl.deleteDate).format(CONS.DATE_FORMAT)}</td>
                                            </>
                                        }


                                    </tr>
                                )
                            }
                        </tbody>
                        :
                        <tbody style={{ display: "table", width: "100%" }}>
                            <tr>
                                <td className="text-center" colSpan={12}>Không có dữ liệu</td>
                            </tr>
                        </tbody>
                }
            </table>
        </>
    )
}
export default ControlSystemMap;