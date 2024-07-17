
import React, { useState, useEffect } from "react";
import controlPVService from "../../../../../../services/ControlPVService";
import CONS from "../../../../../../constants/constant";
import moment from "moment";
import { SelectButton } from 'primereact/selectbutton';
import './index.css';
import $ from "jquery";
import { index } from "d3";

const ControlProject = (props) => {

    const [control, setControl] = useState([]);
    const [controlAll, setControlALl] = useState([]);
    const [controlShowed, setControlShow] = useState(1);
    const [controlDevice, setDeviceControl] = useState();
    const [status, setStatus] = useState();

    const getControls = async () => {
        let res = await controlPVService.getControls(props.projectId);
        if (res.status === 200) {
            setControl(res.data.controls);
            setControlALl(res.data.controlsAll);
        }
    };

    const getControl = async (historyId, stt, congSuatTietGiam, timeFrame, fromDate, toDate, index) => {
        let control = {
            historyId,
            stt,
            congSuatTietGiam,
            timeFrame,
            fromDate,
            toDate
        };
        let res = await controlPVService.getControlDevice(control);
        if (res.status === 200) {
            setDeviceControl(res.data.devices);
            setStatus(res.data.status);
            if (res.data.status === true) {
                setStatus("Auto");
            } else {
                setStatus("Manual");
            }
        };
        countPower(index);
    };

    const autoManualCustomer = (e, index) => {
        setStatus(e);
        let rows = $("#table-control-" + index + " tr").length;
        for (let i = 0; i < rows - 2; i++) {
            if (e == "Auto") {
                let power = $("#p-hidden-" + index + "-" + i).val();
                let csdm = $("#pdm-hidden-" + index + "-" + i).val();
                $("#p-value-" + index + "-" + i).val(power);
                $("#cscp-" + index + "-" + i).html(csdm - power);
                $("#pcp-hidden-" + index + "-" + i).val(csdm - power);
            } else if (e == "Manual") {
                $("#p-value-" + index + "-" + i).val(null);
                $("#cscp-" + index + "-" + i).html(0);
                $("#pcp-hidden-" + index + "-" + i).val(0);
            };
        };

        countPower(index);
    };

    const countPower = (index) => {
        let countDm = 0;
        let countSum = 0;
        let rows = $("#table-control-" + index + " tr").length;
        console.log("rows: ", rows);
        for (let i = 0; i < rows - 2; i++) {
            let powerDinhMuc = $("#pdm-hidden-" + index + "-" + i).val();
            let powerTietGiam = $("#p-value-" + index + "-" + i).val();
            let powerChoPhep = $("#pcp-hidden-" + index + "-" + i).val();
            if (powerDinhMuc != null && powerDinhMuc != "") {
                countDm += 1 * powerDinhMuc;
            }
            if (powerTietGiam != null && powerTietGiam != "") {
                countSum += 1 * powerTietGiam;
            }
            $("#cscp-" + index + "-" + i).html(powerDinhMuc - powerTietGiam);
            $("#pcp-hidden-" + index + "-" + i).val(powerDinhMuc - powerTietGiam);
        }
        $('#power-tg-tong-' + index).html(countSum);
    };

    const saveControl = async (index) => {
        let schedules = [];
        let check = true;
        for (let i = 0; i < controlDevice?.length; i++) {
            let historyId = $("#historyId-" + index + "-" + i).val();
            let deviceId = $("#deviceId-" + index + "-" + i).val();
            let deviceName = $("#deviceName-" + index + "-" + i).val();
            let devicePower = $("#pcp-hidden-" + index + "-" + i).val();
            let pDinhMuc = $("#pdm-hidden-" + index + "-" + i).val();
            let pTietGiam = $("#p-value-" + index + "-" + i).val();
            if (pTietGiam !== null && pTietGiam !== ""  && pTietGiam !== '0') {
                if (1 * pTietGiam > 1 * pDinhMuc) {
                    if (check) {
                        $.alert({
                            title: 'Thông báo!',
                            content: "Công suất tiết giảm phải nhỏ hơn Công suất định mức!"
                        });
                        check = false;
                        return;
                    }
                };
                if (devicePower < 0) {
                    if (check) {
                        $.alert({
                            title: 'Thông báo!',
                            content: 'Công suất cho phép phải lớn hơn 0!',
                        });
                        check = false;
                        return;
                    }
                };
                let timeFrom = $("#time-from-hidden-" + index + "-" + i).val();
                let timeTo = $("#time-to-hidden-" + index + "-" + i).val();
                let parentId = $("#parent-hidden-" + index + "-" + i).val();
                let from = $("#from-hidden-" + index + "-" + i).val();
                let to = $("#to-hidden-" + index + "-" + i).val();
                if (devicePower !== null && devicePower !== "") {
                    let schedule = {
                        "historyId": historyId,
                        "deviceId": deviceId,
                        "csdm": pDinhMuc,
                        "cstg": pTietGiam,
                        "cscp": devicePower,
                        "fromTime": timeFrom,
                        "toTime": timeTo,
                        "status": null,
                        "timeViewFrom": from,
                        "timeViewTo": to,
                        "parentId": parentId
                    };
                    schedules.push(schedule);
                }
            }
        }
        if (schedules.length == 0) {
            $.alert({
                title: 'Thông báo!',
                content: 'Không có thiết lập tiết giảm nào!',
            });
            check = false;
        } else {
           let res = await controlPVService.saveControl(schedules);
            if (res.status === 200) {
                $.alert({
                    title: 'Thông báo!',
                    content: 'Gửi thành công!',
                });
                getControls();
            } else {
                $.alert({
                    title: 'Thông báo!',
                    content: 'Gửi không thành công!',
                });
            }
        }
    };

    const changeControlShow = (e) => {
        if (e === 1) {
            setControlShow(e);
            document.getElementById("history").style.display = "";
            document.getElementById("historyAll").style.display = "none";
        } else {
            setControlShow(e);
            document.getElementById("historyAll").style.display = "";
            document.getElementById("history").style.display = "none";
        }
    };


    useEffect(() => {
        getControls();
    }, [props.projectId]);

    return (
        <>
            <div id="btn-send" style={{ textAlign: "left", marginLeft: "10px", marginTop: "35px" }}>
                <button className={controlShowed === 1 ? "btn btn-outline-warning active" : "btn btn-outline-warning"} style={{ width: "180px", marginBottom: "20px", padding: "5px 10px" }} onClick={() => changeControlShow(1)} >Lệnh hiện tại</button>
                <button className={controlShowed === 2 ? "btn btn-outline-warning active" : "btn btn-outline-warning"} style={{ width: "180px", marginBottom: "20px", padding: "5px 10px" }} onClick={() => changeControlShow(2)} >Lịch sử lệnh</button>
            </div>

            <table className="table tbl-overview tbl-power" id="history">
                <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th scope="col" style={{ width: "50px", textAlign: "center" }}>TT</th>
                        <th scope="col" style={{ width: "200px", textAlign: "center" }}>Vị Trí</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Từ ngày</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Đến ngày</th>
                        <th scope="col" style={{ width: "110px", textAlign: "center" }}>Khung giờ</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian thiết lập</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian xử lý</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất định mức(kW)</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất tiết giảm(kW)</th>
                        <th scope="col" style={{ textAlign: "center", width: "150px" }}>Trạng thái</th>
                        <th scope="col" style={{ textAlign: "center", width: "180px" }}>Thời gian hủy</th>
                        <th scope="col"><i className="fa-regular fa-hand"></i></th>
                    </tr>
                </thead>
                {
                    control?.length > 0 ?
                        <tbody style={{ display: "table", width: "100%" }}>
                            {
                                control?.map(
                                    (item, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }} className={item.updateFlag === 1 ? "item-model-number highlight" : "item-model-number"}>
                                        <td width={"50px"}>{index + 1}</td>
                                        <td width={"200px"}>{item.viTri}</td>
                                        <td width={"100px"}>{item.fromDate}</td>
                                        <td width={"100px"}>{item.toDate}</td>
                                        <td width={"110px"}>{item.timeFrame}</td>
                                        <td width={"180px"}>{moment(item.timeInsert).format(CONS.DATE_FORMAT)}</td>
                                        <td width={"180px"}>{moment(item.createDate).format(CONS.DATE_FORMAT)}</td>
                                        <td >{item.congSuatDinhMuc}</td>
                                        <td>{item.congSuatTietGiam}</td>
                                        {
                                            (item.deleteFlag === 0 && item.updateFlag === 1) &&
                                            <>
                                                <td width="150px">Đã thay đổi</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && item.updateFlag === 2) &&
                                            <>
                                                <td width="150px">Đã gửi</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 0) &&
                                            <>
                                                <td width="150px" >Chưa xử lý</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 1) &&
                                            <>
                                                <td width="150px">Đã xử lý</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 2) &&
                                            <>
                                                <td width="150px">Hết hạn</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 1) &&
                                            <>
                                                <td width="150px">Đã hủy</td>
                                                <td width="180px">{item.deleteDate}</td>
                                            </>
                                        }

                                        <td>
                                            {
                                                (item.status == 0 || item.updateFlag == 1 || item.updateFlag == 2) &&
                                                <>
                                                    <a className="staff-item" title="Edit" data-toggle="modal" data-target={"#modal-control-" + index} onClick={() => { getControl(item.historyId, item.stt, item.congSuatTietGiam, item.timeFrame, item.fromDate, item.toDate, index); }}>
                                                        <img src="/resources/image/icon-edit.png" height="16px"></img>
                                                    </a>
                                                    <div className="modal fade" id={"modal-control-" + index} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                                        <div className="modal-dialog modal-lg" role="document">
                                                            <div className="modal-content">
                                                                <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                                    <h5 className="modal-title" id="exampleModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{item.viTri}</h5>
                                                                    <SelectButton id="get-auto" value={status} onChange={(e) => { autoManualCustomer(e.value, index); }} options={["Auto", "Manual"]} />

                                                                </div>
                                                                <div className="modal-body">
                                                                    <table className="table text-center tbl-overview tbl-power" id={"table-control-" + index} >
                                                                        <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                                                            <tr>
                                                                                <th scope="col" style={{ width: "40px", textAlign: "center" }}>TT</th>
                                                                                <th scope="col" style={{ textAlign: "center" }}>Inverter</th>
                                                                                <th scope="col" style={{ width: "200px", textAlign: "center" }}>Công suất định mức(kW)</th>
                                                                                <th scope="col" style={{ width: "200px", textAlign: "center" }}>Công suất tiết giảm(kW)</th>
                                                                                <th scope="col" style={{ width: "200px", textAlign: "center" }}>Công suất cho phép phát(kW)</th>
                                                                            </tr>
                                                                            {
                                                                                controlDevice?.map(
                                                                                    (device, indexDevice) => <tr key={indexDevice} className="item-control">
                                                                                        <td className="text-center" width="40px">{indexDevice + 1}</td>
                                                                                        <td style={{ textAlign: "center" }}>{device.deviceName}</td>
                                                                                        <td style={{ textAlign: "right", width: "200px" }}>{device.csdm}</td>
                                                                                        <input type="hidden" id={"historyId-" + index + "-" + indexDevice} defaultValue={device.historyId} />
                                                                                        <input type="hidden" id={"deviceId-" + index + "-" + indexDevice} defaultValue={device.deviceId} />
                                                                                        <input type="hidden" id={"p-hidden-" + index + "-" + indexDevice} defaultValue={device.cstg} />
                                                                                        <input type="hidden" id={"pdm-hidden-" + index + "-" + indexDevice} defaultValue={device.csdm} />
                                                                                        <input type="hidden" id={"pcp-hidden-" + index + "-" + indexDevice} defaultValue={device.cscp} />
                                                                                        <input type="hidden" id={"time-from-hidden-" + index + "-" + indexDevice} defaultValue={device.timeViewFrom} />
                                                                                        <input type="hidden" id={"time-to-hidden-" + index + "-" + indexDevice} defaultValue={device.timeViewTo} />
                                                                                        <input type="hidden" id={"parent-hidden-" + index + "-" + indexDevice} defaultValue={device.parentId} />
                                                                                        <input type="hidden" id={"from-hidden-" + index + "-" + indexDevice} defaultValue={device.fromTime} />
                                                                                        <input type="hidden" id={"to-hidden-" + index + "-" + indexDevice} defaultValue={device.toTime} />
                                                                                        <td id="devicePower" width="200px">
                                                                                            <input className="text-right" style={{ width: "100%" }} id={"p-value-" + index + "-" + indexDevice} defaultValue={device.congSuat} onKeyUp={() => { countPower(index) }} />
                                                                                        </td>
                                                                                        <td style={{ textAlign: "right", width: "200px" }}>
                                                                                            <span id={"cscp-" + index + "-" + indexDevice}>
                                                                                                {status === "Manual" ? device.csdm : device.csdm - device.cscp}
                                                                                            </span>
                                                                                        </td>
                                                                                    </tr>
                                                                                )
                                                                            }
                                                                            <tr>
                                                                                <td className="text-center" colSpan="2">Tổng</td>
                                                                                <td className="text-right"></td>
                                                                                <td className="text-right"><span id={"power-tg-tong-" + index}>0</span></td>
                                                                                <td className="text-right"></td>
                                                                            </tr>
                                                                        </thead>
                                                                    </table>
                                                                </div>
                                                                <div className="modal-footer">
                                                                    <button type="button" className="btn btn-secondary" data-dismiss="modal">Đóng</button>
                                                                    <button type="button" className="btn btn-primary" onClick={() => { saveControl(index) }}>Cập nhật</button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </>
                                            }
                                        </td>


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

            <table className="table tbl-overview tbl-power" id="historyAll" style={{ display: "none" }}>
                <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th scope="col" style={{ width: "50px", textAlign: "center" }}>TT</th>
                        <th scope="col" style={{ width: "200px", textAlign: "center" }}>Vị Trí</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Từ ngày</th>
                        <th scope="col" style={{ width: "100px", textAlign: "center" }}>Đến ngày</th>
                        <th scope="col" style={{ width: "110px", textAlign: "center" }}>Khung giờ</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian thiết lập</th>
                        <th scope="col" style={{ width: "180px", textAlign: "center" }}>Thời gian xử lý</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất định mức(kW)</th>
                        <th scope="col" style={{ textAlign: "center" }}>Công suất tiết giảm(kW)</th>
                        <th scope="col" style={{ textAlign: "center", width: "150px" }}>Trạng thái</th>
                        <th scope="col" style={{ textAlign: "center", width: "180px" }}>Thời gian hủy</th>
                    </tr>
                </thead>
                {
                    controlAll?.length > 0 ?
                        <tbody style={{ display: "table", width: "100%" }}>
                            {
                                controlAll?.map(
                                    (item, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed", height: "22.5px" }} className={item.updateFlag === 1 ? "item-model-number highlight" : "item-model-number"}>
                                        <td width={"50px"}>{index + 1}</td>
                                        <td width={"200px"}>{item.viTri}</td>
                                        <td width={"100px"}>{item.fromDate}</td>
                                        <td width={"100px"}>{item.toDate}</td>
                                        <td width={"110px"}>{item.timeFrame}</td>
                                        <td width={"180px"}>{moment(item.timeInsert).format(CONS.DATE_FORMAT)}</td>
                                        <td width={"180px"}>{moment(item.createDate).format(CONS.DATE_FORMAT)}</td>
                                        <td >{item.congSuatDinhMuc}</td>
                                        <td>{item.congSuatTietGiam}</td>
                                        {
                                            (item.deleteFlag === 0 && item.updateFlag === 1) &&
                                            <>
                                                <td width="150px">Đã thay đổi</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && item.updateFlag === 2) &&
                                            <>
                                                <td width="150px">Đã gửi</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 0) &&
                                            <>
                                                <td width="150px">Chưa xử lý</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 1) &&
                                            <>
                                                <td width="150px">Đã xử lý</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 0 && (item.updateFlag === 0 || item.updateFlag === null) && item.status === 2) &&
                                            <>
                                                <td width="150px">Hết hạn</td>
                                                <td width="180px"></td>
                                            </>
                                        }
                                        {
                                            (item.deleteFlag === 1) &&
                                            <>
                                                <td width="150px">Đã hủy</td>
                                                <td width="180px">{item.deleteDate}</td>
                                            </>
                                        }


                                    </tr>
                                )
                            }
                        </tbody>
                        :
                        <tbody style={{ display: "table", width: "100%" }}>
                            <tr>
                                <td className="text-center" colSpan={11}>Không có dữ liệu</td>
                            </tr>
                        </tbody>
                }
            </table>

        </>
    )
}
export default ControlProject; 