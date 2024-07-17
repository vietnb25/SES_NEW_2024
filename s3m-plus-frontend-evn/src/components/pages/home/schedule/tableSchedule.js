import React from "react";
import { useEffect } from "react";
import { useState } from "react";
import AuthService from "../../../../services/AuthService";
import ScheduleService from "../../../../services/ScheduleService";
import Pickadate from 'pickadate/builds/react-dom';
import moment from "moment/moment";
import TimePicker from 'rc-time-picker';
import 'rc-time-picker/assets/index.css';
import $ from "jquery";
import 'jquery-confirm';
import { Calendar } from 'primereact/calendar';
import 'primeicons/primeicons.css';
import { useLocation, useHistory, Link } from 'react-router-dom';

const TableSchedule = (props) => {

    const location = useLocation();

    const history = useHistory();

    const [dateForm, setDateForm] = useState({
        fromDate: new Date(),
        toDate: new Date()
    });

    const [auto, setAuto] = useState(true)

    const [timeFormState, setTimeForm] = useState(" ")

    const [timeToState, setTimeTo] = useState(" ")

    const [CSDM, setCSDM] = useState()

    const [tongCSDM, setTongCSDM] = useState()

    const [congSuatChoPhep, setCongSuatChoPhep] = useState([]);

    const [tongCSCP, setTongCSCP] = useState();

    const [CSTG, setCSTG] = useState();

    const [idSchedule, setIdSchedule] = useState()

    const [schedules, setSchedules] = useState([])

    const [detailHistory, setDetailHistory] = useState([]);

    const [date, setDate] = useState(null);

    const [displayDetail, setDisplayDetail] = useState(false);

    const [closeTableDetailHistory, setCloseTableDetailHistory] = useState(true);

    const users = props.users
    const type = props.type
    const id = props.id
    const schedule = props.schedules
    const historySettings = props.historySettings
    const itongCSDM = props.tongCSDM

    const getData = async () => {
        if (type === 3) {
            setSchedules(schedule)
        }
        setTongCSDM(itongCSDM)
    }

    const handleChange = (e, data, i) => {
        let tongCSG = 0;
        let tongCSCP = 0;
        let congSuatTietGiam = e;
        console.log("congSuatTietGiam", congSuatTietGiam);
        console.log(data);
        let dataResult = (data - e).toFixed(3);
        console.log(dataResult);
        setCongSuatChoPhep((prevState) => {
            if (prevState[i]) {
                prevState[i] = dataResult
                console.log(prevState);
                return [...prevState]
            } else {
                return [...prevState, dataResult]
            }
        })
        console.log("congSuatChoPhep: " + congSuatChoPhep);
        for (let schedule of schedules) {
            let csGiam = $("#inpCSTG" + schedule.stt).val()
            if (csGiam) {
                tongCSG += Number(csGiam)
            }
            $("#tongCongSuatGiam").html(tongCSG.toFixed(3))
            if (type === 3) {
                tongCSCP = tongCSDM - tongCSG
                $("#tongCongSuatChoPhep").html(tongCSCP.toFixed(3))
            } else {
                tongCSCP = CSDM - tongCSG
                $("#tongCongSuatChoPhep").html(tongCSCP.toFixed(3))
            }
        }
    }

    const changeCSTG = (e) => {
        let CSTG = e.target.value
        let CSCP = tongCSDM - CSTG
        setTongCSCP(CSCP.toFixed(2))
        console.log('auto: ', auto);
        if (!auto) {
            if (type === 3) {
                if ($("#idCSTG").val() && $("#idCSTG").val() != "") {
                    if ($("#idCSTG").val() == 0) {
                        console.log("k doi");
                        setCongSuatChoPhep([])
                        for (let schedule of schedules) {
                            let id = "inpCSTG" + schedule.stt
                            $(`#${id}`).val(0)
                            setCongSuatChoPhep((prevState) => {
                                return [...prevState, schedule.acPower]
                            })
                        }
                        $("#tongCongSuatGiam").html($("#idCSTG").val())
                        $("#tongCongSuatChoPhep").html(tongCSDM)
                    } else {
                        let tongCSTG = 0;
                        for (let schedule of schedules) {
                            let CSTG = schedule.acPower / tongCSDM * $("#idCSTG").val()
                            let idCSTG = "inpCSTG" + schedule.stt
                            let idCSCP = "inpCSCP" + schedule.stt
                            tongCSTG += CSTG
                            $(`#${idCSTG}`).val(CSTG.toFixed(3))
                            $(`#${idCSCP}`).html((schedule.acPower - $(`#${idCSTG}`).val()).toFixed(3))
                        }

                        $("#tongCongSuatGiam").html(tongCSTG)
                        $("#tongCongSuatChoPhep").html((tongCSDM - tongCSTG).toFixed(3))

                    }
                }

            } else {
                let tongCSTG = 0;
                for (let schedule of schedules) {
                    if (schedule.acPower != 0) {
                        let idCSTG = "inpCSTG" + schedule.stt
                        let idCSCP = "inpCSCP" + schedule.stt
                        console.log(CSTG);
                        console.log(schedule.acPower);
                        console.log(CSDM);
                        let iCSTG = schedule.acPower / CSDM * CSTG
                        console.log("iCSTG " + iCSTG);
                        tongCSTG += iCSTG
                        $(`#${idCSTG}`).val(iCSTG.toFixed(3))
                        $(`#${idCSCP}`).html((schedule.acPower - $(`#${idCSTG}`).val()).toFixed(3))
                    }
                }
                $("#tongCongSuatGiam").html(tongCSTG.toFixed(3))
                $("#tongCongSuatChoPhep").html((CSDM - tongCSTG).toFixed(3))

            }
        }
    }

    const isCheckSetting = () => {
        setAuto(!auto)
        if (type === 3) {
            console.log(auto);
            if (auto) {
                console.log("auto");
                if ($("#idCSTG").val() && $("#idCSTG").val() != "") {
                    if ($("#idCSTG").val() == 0) {
                        console.log("k doi");
                        setCongSuatChoPhep([])
                        for (let schedule of schedules) {
                            let id = "inpCSTG" + schedule.stt
                            $(`#${id}`).val(0)
                            setCongSuatChoPhep((prevState) => {
                                return [...prevState, schedule.acPower]
                            })
                        }
                        $("#tongCongSuatGiam").html($("#idCSTG").val())
                        $("#tongCongSuatChoPhep").html(tongCSDM)
                    } else {
                        let tongCSTG = 0;
                        for (let schedule of schedules) {
                            let CSTG = Math.round((schedule.acPower / tongCSDM * $("#idCSTG").val()) * 100) / 100
                            let idCSTG = "inpCSTG" + schedule.stt
                            let idCSCP = "inpCSCP" + schedule.stt
                            tongCSTG += CSTG
                            $(`#${idCSTG}`).val(CSTG.toFixed(3))
                            $(`#${idCSCP}`).html((schedule.acPower - $(`#${idCSTG}`).val()).toFixed(3))
                        }

                        $("#tongCongSuatGiam").html(tongCSTG)
                        $("#tongCongSuatChoPhep").html((tongCSDM - tongCSTG).toFixed(3))

                    }
                }

            } else {
                console.log("unauto");
                setCongSuatChoPhep([])
                for (let schedule of schedules) {
                    let idCSTG = "inpCSTG" + schedule.stt
                    let idCSCP = "inpCSCP" + schedule.stt
                    $(`#${idCSTG}`).val("")
                    $(`#${idCSCP}`).html("")
                    $("#tongCongSuatGiam").html("")
                    $("#tongCongSuatChoPhep").html("")
                    $("#idCSTG").val(0.0)
                    setTongCSCP("")
                }
            }
        } else {
            if (auto) {
                let tongCSTG = 0;
                for (let schedule of schedules) {
                    if (schedule.acPower != 0) {
                        let idCSTG = "inpCSTG" + schedule.stt
                        let idCSCP = "inpCSCP" + schedule.stt
                        console.log(CSTG);
                        console.log(schedule.acPower);
                        console.log(CSDM);
                        let iCSTG = Math.round((schedule.acPower / CSDM * CSTG) * 100) / 100
                        console.log("iCSTG " + iCSTG);
                        tongCSTG += iCSTG
                        $(`#${idCSTG}`).val(iCSTG.toFixed(3))
                        $(`#${idCSCP}`).html((schedule.acPower - $(`#${idCSTG}`).val()).toFixed(3))
                    }
                }
                $("#tongCongSuatGiam").html(tongCSTG.toFixed(3))
                $("#tongCongSuatChoPhep").html((CSDM - tongCSTG).toFixed(3))

            } else {
                console.log("unauto");
                setCongSuatChoPhep([])
                for (let index = 0; index < schedules.length; index++) {
                    let textCSTG = "CSTG" + index
                    let idCSTG = "inpCSTG" + schedules[index].stt
                    let idCSCP = "inpCSCP" + schedules[index].stt
                    $(`#${idCSTG}`).val("")
                    $(`#${idCSCP}`).html("")
                    $("#tongCongSuatGiam").html("")
                    $("#tongCongSuatChoPhep").html("")
                }
            }
        }
    }

    const saveSchedule = async () => {
        let CSDM = $("#idTongCongSuatDinhMuc").text();
        let tongCSTG = $("#tongCongSuatGiam").text();
        let tongCSCP = $("#tongCongSuatChoPhep").text();
        let timeTo;
        let timeFrom;

        if (type === 3) {
            timeTo = moment(timeToState).format("HH:mm");
            timeFrom = moment(timeFormState).format("HH:mm");
        } else {
            for (let index = 0; index < historySettings.length; index++) {
                let idTimeFrame = "timeFrame" + index;
                let timeFrame = $(`#${idTimeFrame}`).text().split(" ~ ")
                timeTo = timeFrame[1];
                timeFrom = timeFrame[0];
            }
        }

        let secondFrom = new Date(dateForm.fromDate + " " + timeFrom).getTime()
        let secondTo = new Date(dateForm.toDate + " " + timeTo).getTime()
        let listSchedule = []

        for (let schedule of schedules) {
            let idCSTG = "inpCSTG" + schedule.stt
            let idCSCP = "inpCSCP" + schedule.stt

            let iSchedule = {
                "stt": schedule.stt,
                "addRess": schedule.addRess,
                "congSuatTietGiam": $(`#${idCSTG}`).val(),
                "congSuatChoPhep": $(`#${idCSCP}`).html(),
                "congSuatDinhMuc": schedule.acPower,
                "typeScrop": 0
            }
            listSchedule.push(iSchedule)
        }

        let dataSend = {
            "type": type,
            "timeTo": timeTo,
            "timeFrom": timeFrom,
            "fromDate": moment(dateForm.fromDate).format('YYYY-MM-DD'),
            "toDate": moment(dateForm.toDate).format('YYYY-MM-DD'),
            "sumCSCP": tongCSCP,
            "sumCSDM": CSDM,
            "sumCSTG": tongCSTG,
            data: listSchedule,
            idSchedule: idSchedule
        }

        if (secondFrom > secondTo) {
            alert("Sai giờ")
        }

        if (type === 3) {
            let dataResult = await checkSchedule(dataSend)
            console.log(dataResult.status);
            if (dataResult.status === 200) {
                console.log("hi");
                dataSend = {
                    "type": type,
                    "timeTo": timeTo,
                    "timeFrom": timeFrom,
                    "fromDate": dateForm.fromDate,
                    "toDate": dateForm.toDate,
                    "sumCSCP": tongCSCP,
                    "sumCSDM": CSDM,
                    "sumCSTG": tongCSTG,
                    data: listSchedule,
                    "textStatus": "success",
                    dataCheck: dataResult.data,
                }
                replaceSchedule(dataSend)
            } else {
                addSchedule(dataSend)
            }
        } else {
            addSchedule(dataSend)
        }

        $(".table-edit").attr("hidden", true);
    }

    const checkSchedule = async (data) => {
        let res = await ScheduleService.checkSchedule(data)
        return res
    }

    const replaceSchedule = async (data) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn ghi đè lịch này không?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async function () {
                        let { status } = await ScheduleService.addSchedule(data);
                        if (status === 200) {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Ghi đè thành công!',
                            });
                            history.push(location.pathname);
                            isCheckSetting();
                        } else {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Ghi đè thất bại!',
                            });
                        }
                    }
                },
                cancel: {
                    text: 'Hủy bỏ',
                    action: function () {

                    }
                }
            }
        });
    }

    const addSchedule = async (data) => {
        await ScheduleService.addSchedule(data);
        $.confirm({
            type: 'blue',
            typeAnimated: true,
            icon: 'fa fa-check',
            title: 'Xác nhận!',
            content: 'Thêm lịch tiết giảm thành công',
            buttons: {
                confirm: {
                    text: 'Xác nhận',
                    action: function () {
                        history.push(location.pathname);
                        isCheckSetting();
                        $("#chb-setting").prop("checked", false);
                    }
                }
            }
        });
    }

    const editSchedule = async (id, CSTG, CSDM) => {
        console.log("id nè: " + id);
        setCSTG(CSTG / 1000000)
        setCSDM(CSDM / 1000000)
        let tong = 0;
        let dataSend = {
            type: type,
            id: id,
            targetId: users.targetId,
        }
        console.log("dataSend", dataSend);
        let res = await ScheduleService.editSchedule(dataSend)
        $(".table-edit").removeAttr('hidden');
        let schedules = res.data.data
        let iSchedules = []
        for (let schedule of schedules) {
            iSchedules.push(schedule)
        }
        console.log(iSchedules);
        setSchedules(iSchedules)
        for (let schedule of schedules) {
            tong += schedule.acPower;
        }
        setIdSchedule(id)
    }

    const deleteHistory = async (id) => {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn xóa lịch tiết giảm này không?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async function () {
                        let { status } = await ScheduleService.delete(id);
                        if (status === 200) {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Xóa lịch tiết giảm thành công!',
                            });
                            history.push(location.pathname);
                        } else {
                            $.alert({
                                title: 'Thông báo!',
                                content: 'Không thể xóa lịch tiết giảm hiện tại!',
                            });
                        }
                    }
                },
                cancel: {
                    text: 'Hủy bỏ',
                    action: function () {

                    }
                }
            }
        });
    }

    const onClickTr = async (historyId, fromDate) => {
        let dataSend = {
            type: type,
            id: historyId,
            fromDate: fromDate,
        }
        let res;
        if (users.userType === "3") {
            res = await ScheduleService.historyDetail(dataSend);
        }
        
        if (res.status == 200) {
            setDetailHistory(res.data);
            setCloseTableDetailHistory(false);
        }

        console.log("res: ", res);
    }

    const closeDetail = async () => {
        setCloseTableDetailHistory(true);
    }


    useEffect(() => {
        document.title = "Content Schedule"
        getData()
    })

    return (
        <div id="main-content" style={{ width: "100%" }}>
            <>
                <div style={{ width: "100%", marginRight: "20px" }}>

                    {(type === 3 && id === null) &&
                        <>
                            <div style={{ width: "100%" }}>
                                <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar"></span>
                                        </span>
                                    </div>
                                    <Calendar
                                        value={dateForm.fromDate}
                                        id="chart-date-from"
                                        className="celendar-picker"
                                        dateFormat="yy-mm-dd"
                                        onChange={e => setDateForm({ ...dateForm, fromDate: e.value })}
                                    />
                                </div>

                                <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar"></span>
                                        </span>
                                    </div>
                                    <Calendar
                                        value={dateForm.toDate}
                                        id="chart-date-to"
                                        className="celendar-picker"
                                        dateFormat="yy-mm-dd"
                                        onChange={e => setDateForm({ ...dateForm, toDate: e.value })}
                                    />
                                </div>

                                <div className="input-group float-left mr-1" style={{ width: '410px', height: '31.5px', marginTop: '-0.5px' }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text clockicon">
                                            <span className="far fa-clock"></span>
                                        </span>
                                    </div>
                                    {/* <TimePicker id="timeForm" minuteStep={15} style={{ width: 100 }} showSecond={false} className="pick-time" onChange={(e) => { setTimeForm(e) }} value={timeForm} />
                                    <TimePicker id="timeTo" minuteStep={15} style={{ width: 100 }} showSecond={false} className="pick-time" onChange={(e) => { setTimeTo(e) }} value={timeTo} /> */}
                                    <Calendar id="calendar-timeonly" placeholder="Time Start" value={timeFormState} onChange={(e) => setTimeForm(e.value)} timeOnly style={{marginRight: '2px'}} />
                                    <Calendar id="calendar-timeonly" placeholder="Time End" value={timeToState} onChange={(e) => setTimeTo(e.value)} timeOnly />
                                </div>
                                <div className="clearfix"></div>
                                <div style={{ marginTop: "10px" }}>
                                    <div id="daily-chart-date" className="chart-date input-group float-left mr-1 mb-1" style={{ width: "300px", height: "31.5px" }}>
                                        <div className="input-group-prepend" style={{ height: "100%" }}>
                                            <span id="CSDM-text" className="input-group-text" style={{ padding: "8px 10px 9px", fontSize: '13px' }}>
                                                Tổng công suất lắp đặt (MW)
                                            </span>
                                        </div>
                                        <input type="text" id="idTongCSDM" defaultValue={tongCSDM} className="form-control text-right" style={{ padding: "0 10px", height: "100%" }} disabled />
                                    </div>
                                    <div id="daily-chart-date" className="chart-date input-group float-left mr-1 mb-1" style={{ width: "300px", height: "31.5px" }}>
                                        <div className="input-group-prepend" style={{ height: "100%" }}>
                                            <span id="CSTG-text" className="input-group-text" style={{ padding: "8px 10px 9px", fontSize: '13px', height: "100%" }}>
                                                Tổng công suất tiết giảm (MW)
                                            </span>
                                        </div>
                                        <input type="text" id="idCSTG" className="form-control text-right" style={{ padding: "0 10px", height: "100%" }} defaultValue="0" onBlur={changeCSTG} />
                                    </div>
                                    <div id="daily-chart-date" className="chart-date input-group float-left mr-1 mb-1" style={{ width: "300px", height: "31.5px" }}>
                                        <div className="input-group-prepend" style={{ height: "100%" }}>
                                            <span id="CSCP-text" className="input-group-text" style={{ padding: "8px 10px 9px", fontSize: '13px' }}>
                                                Tổng công suất phát cho phép (MW)
                                            </span>
                                        </div>
                                        <input type="text" id="idTongCSCP" defaultValue={tongCSCP} className="form-control text-right" style={{ padding: "0 10px", height: "100%" }} disabled />
                                    </div>
                                </div>
                                <div id="toggle-id" className="can-toggle demo-rebrand-1" style={{ float: "right", width: "135px", marginTop: "25px" }}>
                                    <input id="chb-setting" type="checkbox" onClick={isCheckSetting} />
                                    <label htmlFor="chb-setting">
                                        <div className="can-toggle__switch" data-checked="Auto" data-unchecked="Manual"></div>
                                    </label>
                                </div>
                                <div className="tab-content">
                                    <table className="tbl-overview">
                                        <thead>
                                            <tr>
                                                <th width="40px">TT</th>
                                                <th>Vị trí</th>
                                                <th width="200px">Tổng công suất lắp đặt (MW)</th>
                                                <th width="200px">Tổng công suất tiết giảm (MW)</th>
                                                <th width="200px">Tổng công suất phát cho phép (MW)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                schedules.length > 0 ? schedules.map((schedule, index) => {
                                                    return (
                                                        <tr key={schedule.stt}>
                                                            <td className="text-center">{index + 1}</td>
                                                            <td className="text-center">{schedule.addRess}</td>
                                                            <td className="text-right">{schedule.acPower}</td>
                                                            <td>
                                                                <input className="inpCSTG" id={"inpCSTG" + schedule.stt} onBlur={e => handleChange(e.target.value, schedule.acPower, index)} />
                                                            </td>
                                                            <td id={"inpCSCP" + schedule.stt} className="text-right">{congSuatChoPhep[index]}</td>
                                                        </tr>
                                                    )
                                                }) :
                                                    <tr>
                                                        <td className="text-center">No data</td>
                                                    </tr>
                                            }
                                        </tbody>
                                        <tfoot>
                                            <tr className="item-model-number">
                                                <td colSpan="2" className="text-center valText">Tổng</td>
                                                <td className="text-right valText" id="idTongCongSuatDinhMuc">{tongCSDM}</td>
                                                <td className="text-right valText" id="tongCongSuatGiam">0</td>
                                                <td className="text-right valText" id="tongCongSuatChoPhep"></td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                    <div style={{ width: "100%", textAlign: "right" }}>
                                        <button className="btn btn-outline-secondary btn-s3m w-120px" style={{ width: "160px !important", margin: "10px 0", padding: "5px 10px" }} onClick={saveSchedule}>Gửi lệnh điều độ</button>
                                    </div>
                                </div>
                            </div>
                        </>
                    }
                    <div className="tab-content">
                        <table className="tbl-overview">
                            <thead>
                                <tr>
                                    <th width="50px">TT</th>
                                    <th width="200px">Vị Trí</th>
                                    <th width="150px">Từ ngày</th>
                                    <th width="150px">Đến ngày</th>
                                    <th width="150px">Khung giờ</th>
                                    <th width="200px">Thời gian gửi lệnh điều độ</th>
                                    {(type === 3 && id === null) &&
                                        <>
                                            <th width="180px">Công suất phát cho phép (MW)</th>
                                            <th width="150px">Trạng thái</th>
                                            <th width="180px">Thời gian xóa</th>
                                        </>
                                    }
                                    {!(type === 3 && id === null) &&
                                        <>
                                            <th width="250px">Thời gian xử lý</th>
                                            <th width="180px">Công suất định mức</th>
                                            <th width="180px">Công suất tiết giảm</th>
                                            <th width="150px">Trạng thái</th>
                                            <th width="180px">Thời gian hủy</th>
                                        </>
                                    }
                                    {(id === null &&
                                        <th width="50px"></th>
                                    )}
                                </tr>

                            </thead>
                            <tbody>
                                {
                                    historySettings.length > 0 ? historySettings.map((historySetting, index) => {
                                        return (
                                            <tr key={historySetting.historyId} onClick={(state) => onClickTr(historySetting.historyId, historySetting.fromDate)}>
                                                <td className="text-center">{index + 1}</td>
                                                {(type === 3 && id === null) &&
                                                    <>
                                                        <td className="text-center">EVN</td>
                                                        <td className="text-center">{historySetting.fromDate}</td>
                                                        <td className="text-center">{historySetting.toDate}</td>
                                                        <td className="text-center">{historySetting.timeFrame}</td>
                                                        <td className="text-center">{moment(historySetting.timeInsert).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td className="text-right">{Math.round((historySetting.congSuatChoPhep / 1000000) * 1000) / 1000}</td>
                                                        {(historySetting.deleteFlag === 0) &&
                                                            <>
                                                                <td className="text-center">Đã gửi</td>
                                                                <td className="text-center"></td>
                                                                <td className="text-center">
                                                                    <Link to="/" className="button-icon" title="Xóa" onClick={(e) => {
                                                                        e.preventDefault();
                                                                        deleteHistory(historySetting.historyId);
                                                                    }}>
                                                                        <img height="16px" src="/resources/image/icon-delete.png" alt="delete" />
                                                                    </Link>
                                                                </td>
                                                            </>
                                                        }
                                                        {(historySetting.deleteFlag === 1) &&
                                                            <>
                                                                <td className="text-center">Đã xóa</td>
                                                                <td className="text-center">{moment(historySetting.deleteDate).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                                <td></td>
                                                            </>
                                                        }
                                                    </>
                                                }
                                                {!(type === 3 && id === null) &&
                                                    <>
                                                        <td className="text-center">{historySetting.viTri}</td>
                                                        <td className="text-center">{historySetting.fromDate}</td>
                                                        <td className="text-center">{historySetting.toDate}</td>
                                                        <td className="text-center" id={"timeFrame" + index}>{historySetting.timeFrame}</td>
                                                        <td className="text-center">{moment(historySetting.timeInsert).format("YYYY-MM-DD HH:mm:ss")}</td>
                                                        <td className="text-center">{historySetting.createDate ? moment(historySetting.createDate).format("YYYY-MM-DD HH:mm:ss") : ''}</td>
                                                        <td className="text-center">{Math.round((historySetting.congSuatDinhMuc / 1000000) * 1000) / 1000}</td>
                                                        <td className="text-center" id={"CSTG" + index}>{Math.round((historySetting.congSuatTietGiam / 1000000) * 1000) / 1000}</td>
                                                        {(historySetting.deleteFlag === 0) &&
                                                            <>
                                                                {(historySetting.updateFlag === 0) &&
                                                                    <>
                                                                        {(historySetting.status === 0) &&
                                                                            <>
                                                                                <td className="text-center">Chưa xử lý</td>
                                                                                <td></td>
                                                                            </>
                                                                        }
                                                                        {(historySetting.status === 1) &&
                                                                            <>
                                                                                <td className="text-center">Đã xử lý</td>
                                                                                <td></td>
                                                                            </>
                                                                        }
                                                                    </>
                                                                }
                                                                {(historySetting.updateFlag === 1) &&
                                                                    <>
                                                                        <td className="text-center">Đã thay đổi</td>
                                                                        <td></td>
                                                                    </>
                                                                }
                                                                {(historySetting.updateFlag === 2) &&
                                                                    <>
                                                                        <td className="text-center">Đã gửi</td>
                                                                        <td></td>
                                                                    </>
                                                                }
                                                                {(type !== 3 && id === null) &&
                                                                    <>
                                                                        <td className="text-center">
                                                                            <Link to="/" className="button-icon" title="Cập nhật" onClick={(e) => {
                                                                                e.preventDefault();
                                                                                editSchedule(historySetting.historyId, historySetting.congSuatTietGiam, historySetting.congSuatDinhMuc);
                                                                            }}>
                                                                                <img height="16px" src="/resources/image/icon-edit.png" alt="delete" />
                                                                            </Link>
                                                                        </td>
                                                                    </>
                                                                }
                                                            </>
                                                        }
                                                        {(historySetting.deleteFlag === 1) &&
                                                            <>
                                                                <td className="text-center">Đã hủy</td>
                                                                <td className="text-center">{historySetting.deleteDate}</td>
                                                                <td></td>
                                                            </>
                                                        }
                                                    </>
                                                }
                                            </tr>
                                        )
                                    }) :
                                        <tr>
                                            <td className="text-center" colSpan={12}>No data</td>
                                        </tr>
                                }
                            </tbody>
                            <tfoot>
                            </tfoot>
                        </table>
                    </div>

                    {(type !== 3) &&
                        <>
                            <div className="table-edit" hidden>

                                <div id="toggle-id" className="can-toggle demo-rebrand-1" style={{ float: "right", width: "135px", marginTop: "25px" }}>
                                    <input id="chb-setting" type="checkbox" onClick={isCheckSetting} />
                                    <label htmlFor="chb-setting">
                                        <div className="can-toggle__switch" data-checked="Auto" data-unchecked="Manual"></div>
                                    </label>
                                </div>
                                <div className="tab-content">
                                    <table className="tbl-overview">
                                        <thead>
                                            <tr>
                                                <th width="40px">TT</th>
                                                <th>Khu vực nhà máy</th>
                                                <th width="200px">Công suất định mức</th>
                                                <th width="200px">Công suất tiết giảm</th>
                                                <th width="200px">Công suất cho phép</th>
                                            </tr>

                                        </thead>
                                        <tbody>
                                            {
                                                schedules.length > 0 ? schedules.map((schedule, index) => {
                                                    return (
                                                        <>
                                                            <tr key={schedule.stt}>
                                                                <td className="text-center">{index + 1}</td>
                                                                <td className="text-center">{schedule.addRess}</td>
                                                                <td className="text-right">{Math.round((schedule.acPower) * 1000) / 1000}</td>
                                                                <td>
                                                                    <input className="inpCSTG" id={"inpCSTG" + schedule.stt} onBlur={e => handleChange(e.target.value, schedule.acPower, schedule.stt)} />
                                                                </td>
                                                                <td id={"inpCSCP" + schedule.stt} className="text-right">{congSuatChoPhep[index]}</td>
                                                            </tr>

                                                        </>
                                                    )
                                                }) :
                                                    <tr>
                                                        <td className="text-center" colSpan={12}>No data</td>
                                                    </tr>
                                            }
                                        </tbody>
                                        <tfoot>
                                            <tr className="item-model-number">
                                                <td colSpan="2" className="text-center valText">Tổng</td>
                                                <td className="text-right valText" id="idTongCongSuatDinhMuc">{Math.round((CSDM) * 1000) / 1000}</td>
                                                <td className="text-right valText" id="tongCongSuatGiam">0</td>
                                                <td className="text-right valText" id="tongCongSuatChoPhep"></td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                                <div style={{ width: "100%", textAlign: "right" }}>
                                    <button className="btn btn-outline-secondary btn-s3m w-120px" style={{ width: "160px !important", margin: "10px 0", padding: "5px 10px" }} onClick={saveSchedule}>Gửi lệnh điều độ</button>
                                </div>
                            </div>
                        </>
                    }
                </div>
                {(!closeTableDetailHistory) && (type == 3) &&
                    <>
                        <div id="modal-detail-history">
                            <div className="detail-container">
                                <div className="modal-body">
                                    <div className="btnclose" onClick={(state) => { closeDetail() }}>x</div>
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
                                                    detailHistory.length > 0 ?
                                                        detailHistory.map((schedule, index) => {
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
                                    <button className="btnclose2" onClick={(state) => { closeDetail() }}>Close</button>
                                </div>
                            </div>
                        </div>
                    </>
                }
            </>
        </div>
    )
}

export default TableSchedule;