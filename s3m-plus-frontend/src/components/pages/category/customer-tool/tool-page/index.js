import { useEffect, useState } from 'react';
import './index.css';

import * as d3 from "d3";
import { DATA, createInstance, showProperties, outFocus, resetForm, focusSvg, onChangeUpdate } from "./data";
import ARROW from "./arrow";
import ARROW2 from "./arrow2";
import METER from "./meter";
import MBA from "./mba";
import RMU from "./rmu";
import FEEDER from "./feeder";
import MC from "./mc";
import MCHOR from './mchor';
import ULTILITY from "./ultility";
import LABEL from "./label";
import LABEL2 from "./label2";
import BUSBAR from './busbar';
import BUSBARVER from './busbarver';
import METER_LOAD from '../../../home/load/systemMap/meter';
import { Link, useLocation, useParams, useHistory } from 'react-router-dom';
import SystemMapService from '../../../../../services/SystemMapService';
import DeviceService from '../../../../../services/DeviceService';
import COMMON from "./common";
import SGMV from './sgmv';
import STMV from './stmv';
import INVERTER from './inverter';
import COMBINER from './combiner';
import STRING from './string';
import PANEL from './panel';
import PANEL2 from './panel2';
import INVERTERSYMBOL from './invertersymbol';
import COMBINERSYMBOL from './combinersymbol';
import PANELSYMBOL from './panelsymbol';
import STRINGSYMBOL from './stringsymbol';
import UPS from './ups';
import WEATHERSYMBOL from './weather';
import WEATHER from './weather';
import KHOANG1 from './khoang1';
import KHOANGCAP from './khoangcap';
import KHOANGDODEM from './khoangdodem';
import KHOANGMAYCAT from './khoangmaycat';
import KHOANGTHANHCAI from './khoangthanhcai';
import KHOANG1SYMBOL from './khoang1symbol';
import KHOANGCAPSYMBOL from './khoangcapsymbol';
import KHOANGCHI from './khoangchi';
import KHOANGDODEMSYMBOL from './khoangdodemsymbol';
import KHOANGMAYCATSYMBOL from './khoangmaycatsymbol';
import KHOANGTHANHCAISYMBOL from './khoangthanhcaisymbol';
import PDHTR02 from './pd-htr02';
import PDAMS01 from './pd-ams01';
import TEXT from './text';
import IMAGE from './image';
import OverviewLoadService from '../../../../../services/OverviewLoadService';

import { FaHome } from "react-icons/fa";
import PD_AMS01 from './pd-ams01';

const $ = window.$;

const Tool = () => {
    const { projectId, systemTypeId, systemMapId, editType, customerId } = useParams();

    var G_KEY_DELETE = 46;

    $(document).keydown(function (e) {
        if (e.keyCode === G_KEY_DELETE) {
            remove();
        }
    });

    const [systemMaps, setSystemMaps] = useState([]);
    const [metersEmpty, setMetersEmpty] = useState([]);
    const [deviceType3, setType3Empty] = useState([]);
    const [deviceType4, setType4Empty] = useState([]);
    const [deviceType5, setType5Empty] = useState([]);
    const [deviceType6, setType6Empty] = useState([]);
    const [deviceType7, setType7Empty] = useState([]);
    const [deviceType8, setType8Empty] = useState([]);
    const [deviceType9, setType9Empty] = useState([]);
    const [deviceType10, setType10Empty] = useState([]);
    const [deviceType11, setType11Empty] = useState([]);
    const [deviceType12, setType12Empty] = useState([]);
    const [deviceType13, setType13Empty] = useState([]);
    const [deviceType14, setType14Empty] = useState([]);
    const [deviceType15, setType15Empty] = useState([]);
    const [deviceType19, setType19Empty] = useState([]);
    const [deviceLoad, setDeviceLoad] = useState([]);
    const [stmvsEmpty, setStmvsEmpty] = useState([]);
    const [sgmvsEmpty, setSgmvsEmpty] = useState([]);
    const [invertersEmpty, setInvertersEmpty] = useState([]);
    const [combinersEmpty, setCombinersEmpty] = useState([]);
    const [panelsEmpty, setPanelsEmpty] = useState([]);
    const [stringsEmpty, setStringsEmpty] = useState([]);
    const [weathersEmpty, setWeathersEmpty] = useState([]);
    const [khoangsEmpty, setKhoangsEmpty] = useState([]);
    const [deviceAlready, setDeviceAlready] = useState([]);
    const [systemMapCurrent, setSystemMapCurrent] = useState({});
    const [layer, setLayer] = useState("");
    const [systemMapName, setSystemMapName] = useState("");
    const [breadscrum, setBreadscrum] = useState("");
    const [deviceTime, setDeviceTime] = useState("");
    let [tab1, setToggleTab1] = useState("active");
    let [tab2, setToggleTab2] = useState("");
    let [dragState, setDragState] = useState(0);
    let [deviceCurrent, setDeviceCurrent] = useState(0);
    var listSystemMap = [];

    const location = useLocation();
    const history = useHistory();

    const [projectInfor, setProjectInfor] = useState();
    const getPowers = async () => {
        let res = await OverviewLoadService.getPower(customerId, projectId, "");
        if (res.status === 200) {
            if (systemTypeId == 1) {
                setProjectInfor(res.data.projectInfor + "LOAD");
            } else if (systemTypeId == 2) {
                setProjectInfor(res.data.projectInfor + "SOLAR");
            } else if (systemTypeId == 3) {
                setProjectInfor(res.data.projectInfor + "WIND");
            } else if (systemTypeId == 4) {
                setProjectInfor(res.data.projectInfor + "BATTERY");
            } else if (systemTypeId == 5) {
                setProjectInfor(res.data.projectInfor + "GRID");
            }
        }
    }

    const getSystemMapList = async () => {
        let systemMapsResponse = await SystemMapService.getSystemMapByProjectIdAndSystemTypeId(projectId, systemTypeId);
        setSystemMaps(systemMapsResponse.data);
        if (!systemMapId && systemMapsResponse.data.length > 0) {
            history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMapsResponse.data[0].id}`);
        }
        getDeviceList();
    };

    const getDeviceList = async () => {
        // let res = await DeviceService.listDevice(projectId, systemTypeId, null);
        // if (res.data.length > 0) {

        // }
    }

    const getDeviceEmptyList = async () => {
        let devicesEmptyResponse = await SystemMapService.getDevicesEmpty(projectId, systemTypeId);
        if (devicesEmptyResponse.data.length > 0) {
            if (systemTypeId == 1) {
                var deviceType1 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 1
                );
                var deviceType3 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 3
                );
                var deviceType4 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 4
                );
                var deviceType5 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 5
                );
                var deviceType6 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 6
                );
                var deviceType7 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 7
                );
                var deviceType8 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 8
                );
                var deviceType9 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 9
                );
                var deviceType10 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 10
                );
                var deviceType11 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 11
                );
                var deviceType12 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 12
                );
                var deviceType13 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 13
                );
                var deviceType14 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 14
                );
                var deviceType15 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 15
                );
                var deviceType19 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 19
                );
                setMetersEmpty(deviceType1);
                setType3Empty(deviceType3);
                setType4Empty(deviceType4);
                setType5Empty(deviceType5);
                setType6Empty(deviceType6);
                setType7Empty(deviceType7);
                setType8Empty(deviceType8);
                setType9Empty(deviceType9);
                setType10Empty(deviceType10);
                setType11Empty(deviceType11);
                setType12Empty(deviceType12);
                setType13Empty(deviceType13);
                setType14Empty(deviceType14);
                setType15Empty(deviceType15);
                setType19Empty(deviceType19);
            } else if (systemTypeId == 2) {
                var deviceType1 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 1
                );
                var deviceType3 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 3
                );
                var deviceType4 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 4
                );
                var deviceType5 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 5
                );
                var deviceType6 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 6
                );
                var deviceType7 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 7
                );
                var deviceType8 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 8
                );
                var deviceType9 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 9
                );
                var deviceType10 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 10
                );
                var deviceType11 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 11
                );
                var deviceType12 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 12
                );
                var deviceType13 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 13
                );
                var deviceType14 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 14
                );
                var deviceType15 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 15
                );
                var deviceType19 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 19
                );
                setMetersEmpty(deviceType1);
                setType3Empty(deviceType3);
                setType4Empty(deviceType4);
                setType5Empty(deviceType5);
                setType6Empty(deviceType6);
                setType7Empty(deviceType7);
                setType8Empty(deviceType8);
                setType9Empty(deviceType9);
                setType10Empty(deviceType10);
                setType11Empty(deviceType11);
                setType12Empty(deviceType12);
                setType13Empty(deviceType13);
                setType14Empty(deviceType14);
                setType15Empty(deviceType15);
                setType19Empty(deviceType19);
                setInvertersEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 2
                ));
                // setWeathersEmpty(devicesEmptyResponse.data.filter(
                //     (device) => device.deviceType == 2
                // ));
                setCombinersEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 17
                ));
                setStringsEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 16
                ));
                setPanelsEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 18
                ));
            } else if (systemTypeId == 5) {
                var deviceType1 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 1
                );
                var deviceType3 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 3
                );
                var deviceType4 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 4
                );
                var deviceType5 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 5
                );
                var deviceType6 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 6
                );
                var deviceType7 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 7
                );
                var deviceType8 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 8
                );
                var deviceType9 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 9
                );
                var deviceType10 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 10
                );
                var deviceType11 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 11
                );
                var deviceType12 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 12
                );
                var deviceType13 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 13
                );
                var deviceType14 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 14
                );
                var deviceType15 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 15
                );
                var deviceType19 = devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 19
                );
                setMetersEmpty(deviceType1);
                setType3Empty(deviceType3);
                setType4Empty(deviceType4);
                setType5Empty(deviceType5);
                setType6Empty(deviceType6);
                setType7Empty(deviceType7);
                setType8Empty(deviceType8);
                setType9Empty(deviceType9);
                setType10Empty(deviceType10);
                setType11Empty(deviceType11);
                setType12Empty(deviceType12);
                setType13Empty(deviceType13);
                setType14Empty(deviceType14);
                setType15Empty(deviceType15);
                setType19Empty(deviceType19);
                setInvertersEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 2
                ));
                setKhoangsEmpty(devicesEmptyResponse.data.filter(
                    (device) => device.deviceType == 1
                ));
            }
        }
    };

    const getDeviceAlreadyList = async () => {
        let devicesAlreadyResponse = await SystemMapService.getDevicesAlready(projectId, systemTypeId);
        if (devicesAlreadyResponse.data.length > 0) {
            setDeviceAlready(devicesAlreadyResponse.data);
        };
    };

    const getSystemMapCurrent = async () => {
        let systemMapsResponse = await SystemMapService.getSystemMap(systemMapId);
        setSystemMapCurrent(systemMapsResponse.data);
        setLayer(systemMapsResponse.data.layer);
        setSystemMapName(systemMapsResponse.data.name);
        loadDataFromJson(JSON.parse(systemMapsResponse.data.jsonData));

        let dataAll = await SystemMapService.getSystemMapByProjectIdAndSystemTypeId(projectId, systemTypeId);
        listSystemMap = dataAll.data;

        setDeviceCurrent(0);

        if (new URLSearchParams(location.search).get("deviceId")) {
            focusDevice(new URLSearchParams(location.search).get("deviceId"));
        };

        let breadscrumCurrent = "Layer " + systemMapsResponse.data.layer + "/ " + systemMapsResponse.data.name;
        if (listSystemMap.length > 0) {
            for (let i = 0; i < listSystemMap.length; i++) {
                if (listSystemMap[i].jsonData != null && listSystemMap[i].jsonData != "") {
                    let systemMapData = JSON.parse(listSystemMap[i].jsonData);
                    for (let j = 0; j < systemMapData.aCards.length; j++) {
                        let obj = systemMapData.aCards[j];
                        if (obj.type == "feeder" && obj.linkId == systemMapsResponse.data.id) {
                            breadscrumCurrent = "Layer " + listSystemMap[i].layer + "/ " + listSystemMap[i].name + " -> Layer " + systemMapsResponse.data.layer + "/ " + systemMapsResponse.data.name;
                            break;
                        }
                    }
                }
            }
        }

        setBreadscrum(breadscrumCurrent);

        let systemMapJsonData = JSON.parse(systemMapsResponse.data.jsonData);
        let deviceJsonList = "";

        if (systemMapJsonData != null && systemMapJsonData != "") {
            for (let i = 0; i < systemMapJsonData.aCards.length; i++) {
                let obj = systemMapJsonData.aCards[i];
                if (obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv"
                    || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather"
                    || obj.type == "khoang1" || obj.type == "khoangcap" || obj.type == "khoangdodem" || obj.type == "khoangmaycat" || obj.type == "khoangthanhcai" || obj.type == "pd-htr02" || obj.type == "pd-ams01") {
                    deviceJsonList = deviceJsonList + obj.deviceId + ",";
                }
            }
        }

        if (deviceJsonList !== "") {
            deviceJsonList = deviceJsonList.slice(0, -1);
            let systemInfoTime = await SystemMapService.getSystemInfoTime(deviceJsonList, customerId);
            if (systemInfoTime.length > 0) {
                setDeviceTime(systemInfoTime.data);
            } else {
                setDeviceTime("Không có dữ liệu");
            }
        } else {
            setDeviceTime("-");
        }

    };

    function setToggleTab(x) {
        if (x === 1) {
            setToggleTab1("active");
            setToggleTab2("");
        } else if (x === 2) {
            setToggleTab1("");
            setToggleTab2("active");
        }
    };

    function handleDragStartArrow() {
        setDragState(2);
    };

    function handleDragStartMeter() {
        setDragState(3);
    };

    function handleDragStartMBA() {
        setDragState(4);
    };

    function handleDragStartRMU() {
        setDragState(5);
    };

    function handleDragStartFeeder() {
        setDragState(6);
    };

    function handleDragStartMc() {
        setDragState(7);
    };

    function handleDragStartLabel() {
        setDragState(8);
    };

    function handleDragStartUltility() {
        setDragState(9);
    };

    function handleDragStartBusbar() {
        setDragState(10);
    };

    function handleDragStartGateway() {
        setDragState(11);
    };

    function handleDragStartTrangThai() {
        setDragState(12);
    };

    function handleDragStartInverter() {
        setDragState(13);
    };

    function handleDragStartInverterSymbol() {
        setDragState(14);
    };

    function handleDragStartCombiner() {
        setDragState(15);
    };

    function handleDragStartCombinerSymbol() {
        setDragState(16);
    };

    function handleDragStartPanel() {
        setDragState(17);
    };

    function handleDragStartPanelSymbol() {
        setDragState(18);
    };

    function handleDragStartString() {
        setDragState(19);
    };

    function handleDragStartStringSymbol() {
        setDragState(20);
    };

    function handleDragStartWeather() {
        setDragState(21);
    };

    function handleDragStartWeatherSymbol() {
        setDragState(22);
    };

    function handleDragStartUps() {
        setDragState(23);
    };

    function handleDragStartKhoang1() {
        setDragState(24);
    };

    function handleDragStartKhoang1Symbol() {
        setDragState(25);
    };

    function handleDragStartKhoangCap() {
        setDragState(26);
    };

    function handleDragStartKhoangCapSymbol() {
        setDragState(27);
    };

    function handleDragStartKhoangChi() {
        setDragState(28);
    };

    function handleDragStartKhoangDoDem() {
        setDragState(29);
    };

    function handleDragStartKhoangDoDemSymbol() {
        setDragState(30);
    };

    function handleDragStartKhoangMayCat1() {
        setDragState(311);
    };

    function handleDragStartKhoangMayCat2() {
        setDragState(312);
    };

    function handleDragStartKhoangMayCat3() {
        setDragState(313);
    };

    function handleDragStartKhoangMayCatSymbol1() {
        setDragState(321);
    };

    function handleDragStartKhoangMayCatSymbol2() {
        setDragState(322);
    };

    function handleDragStartKhoangMayCatSymbol3() {
        setDragState(323);
    };

    function handleDragStartKhoangThanhCai() {
        setDragState(33);
    };

    function handleDragStartKhoangThanhCaiSymbol1() {
        setDragState(341);
    };

    function handleDragStartKhoangThanhCaiSymbol2() {
        setDragState(342);
    };

    function handleDragStartKhoangThanhCaiSymbol3() {
        setDragState(343);
    };

    function handleDragStartText() {
        setDragState(35);
    };

    function handleDragStartNhienLieu() {
        setDragState(36);
    };

    function handleDragStartPhongDien() {
        setDragState(37);
    };

    function handleDragStartApSuat() {
        setDragState(38);
    };

    function handleDragStartLuuLuong() {
        setDragState(39);
    };

    function handleDragStartNhietDo() {
        setDragState(40);
    };

    function handleDragStartBucXa() {
        setDragState(41);
    };

    function handleDragStartMucNuoc() {
        setDragState(42);
    };

    function handleDragStartPH() {
        setDragState(43);
    };

    function handleDragStartGio() {
        setDragState(44);
    };

    function handleDragStartPin() {
        setDragState(45);
    };

    function handleDragStartLabel02() {
        setDragState(46);
    };

    function handleDragStartLabel03() {
        setDragState(47);
    };

    function handleDragStartLabel04() {
        setDragState(48);
    };

    function handleDragStartLabel05() {
        setDragState(49);
    };

    function handleDragStartLabel06() {
        setDragState(50);
    };

    function handleDragStartLabel07() {
        setDragState(51);
    };

    function handleDragStartLabel08() {
        setDragState(52);
    };

    function handleDragStartPanel2() {
        setDragState(53);
    };

    function handleDragStartPhongDienHTR02() {
        setDragState(54);
    };

    function handleDragStartPhongDienAMS01() {
        setDragState(55);
    };

    function handleDragStartComponent(number) {
        setDragState(number);
    };

    function handleShowProperties(type) {
        if (editType != 1) {
            resetForm();
            $("#btn-update").hide();
            $("#btn-remove").hide();
            showProperties(type);
        }
    };

    function loadDataFromJson(dataJson) {
        DATA.aCards = [];
        DATA.aArrows = [];

        if (dataJson != null && dataJson != "") {
            for (let i = 0; i < dataJson.aCards.length; i++) {
                let obj = dataJson.aCards[i];
                DATA.aCards.push(obj);

                switch (obj.type) {
                    case "meter":
                        if (editType == 1) {
                            METER_LOAD.drawLoad(obj);
                        } else {
                            METER.draw(obj);
                        }
                        break;
                    case "mba":
                        if (editType == 1) {
                            MBA.drawLoad(obj);
                        } else {
                            MBA.draw(obj);
                        }
                        break;
                    case "rmu":
                        if (editType == 1) {
                            RMU.drawLoad(obj);
                        } else {
                            RMU.draw(obj);
                        }
                        break;
                    case "feeder":
                        if (editType == 1) {
                            FEEDER.drawLoad(obj);
                        } else {
                            FEEDER.draw(obj);
                        }
                        break;
                    case "mc":
                        if (editType == 1) {
                            MC.drawLoad(obj);
                        } else {
                            MC.draw(obj);
                        }
                        break;
                    case "mchor":
                        if (editType == 1) {
                            MCHOR.drawLoad(obj);
                        } else {
                            MCHOR.draw(obj);
                        }
                        break;
                    case "label":
                        if (editType == 1) {
                            LABEL.drawLoad(obj);
                        } else {
                            LABEL.draw(obj);
                        }

                        break;
                    case "label2":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label3":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label4":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label5":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label6":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label7":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "label8":
                        if (editType == 1) {
                            LABEL2.drawLoad(obj);
                        } else {
                            LABEL2.draw(obj);
                        }
                        break;
                    case "ultility":
                        if (editType == 1) {
                            ULTILITY.drawLoad(obj);
                        } else {
                            ULTILITY.draw(obj);
                        }

                        break;
                    case "busbar":
                        if (editType == 1) {
                            BUSBAR.drawLoad(obj);
                        } else {
                            BUSBAR.draw(obj);
                        }
                        break;
                    case "busbarver":
                        if (editType == 1) {
                            BUSBARVER.drawLoad(obj);
                        } else {
                            BUSBARVER.draw(obj);
                        }
                        break;
                    case "stmv":
                        if (editType == 1) {
                            STMV.drawLoad(obj);
                        } else {
                            STMV.draw(obj);
                        }
                        break;
                    case "sgmv":
                        if (editType == 1) {
                            SGMV.drawLoad(obj);
                        } else {
                            SGMV.draw(obj);
                        }
                        break;
                    case "inverter":
                        if (editType == 1) {
                            INVERTER.drawLoad(obj);
                        } else {
                            INVERTER.draw(obj);
                        }
                        break;
                    case "invertersymbol":
                        if (editType == 1) {
                            INVERTERSYMBOL.drawLoad(obj);
                        } else {
                            INVERTERSYMBOL.draw(obj);
                        }
                        break;
                    case "combiner":
                        if (editType == 1) {
                            COMBINER.drawLoad(obj);
                        } else {
                            COMBINER.draw(obj);
                        }
                        break;
                    case "combinersymbol":
                        if (editType == 1) {
                            COMBINERSYMBOL.drawLoad(obj);
                        } else {
                            COMBINERSYMBOL.draw(obj);
                        }
                        break;
                    case "panel":
                        if (editType == 1) {
                            PANEL.drawLoad(obj);
                        } else {
                            PANEL.draw(obj);
                        }
                        break;
                    case "panelsymbol":
                        if (editType == 1) {
                            PANELSYMBOL.drawLoad(obj);
                        } else {
                            PANELSYMBOL.draw(obj);
                        }
                        break;
                    case "string":
                        if (editType == 1) {
                            STRING.drawLoad(obj);
                        } else {
                            STRING.draw(obj);
                        }
                        break;
                    case "stringsymbol":
                        if (editType == 1) {
                            STRINGSYMBOL.drawLoad(obj);
                        } else {
                            STRINGSYMBOL.draw(obj);
                        }
                        break;
                    case "weather":
                        if (editType == 1) {
                            WEATHER.drawLoad(obj);
                        } else {
                            WEATHER.draw(obj);
                        }
                        break;
                    case "weathersymbol":
                        if (editType == 1) {
                            WEATHERSYMBOL.drawLoad(obj);
                        } else {
                            WEATHERSYMBOL.draw(obj);
                        }
                        break;
                    case "ups":
                        if (editType == 1) {
                            UPS.drawLoad(obj);
                        } else {
                            UPS.draw(obj);
                        }
                        break;
                    case "khoang1":
                        if (editType == 1) {
                            KHOANG1.drawLoad(obj);
                        } else {
                            KHOANG1.draw(obj);
                        }
                        break;
                    case "khoang1symbol":
                        if (editType == 1) {
                            KHOANG1SYMBOL.drawLoad(obj);
                        } else {
                            KHOANG1SYMBOL.draw(obj);
                        }
                        break;
                    case "khoangcap":
                        if (editType == 1) {
                            KHOANGCAP.drawLoad(obj);
                        } else {
                            KHOANGCAP.draw(obj);
                        }
                        break;
                    case "khoangcapsymbol":
                        if (editType == 1) {
                            KHOANGCAPSYMBOL.drawLoad(obj);
                        } else {
                            KHOANGCAPSYMBOL.draw(obj);
                        }
                        break;
                    case "khoangchi":
                        if (editType == 1) {
                            KHOANGCHI.drawLoad(obj);
                        } else {
                            KHOANGCHI.draw(obj);
                        }
                        break;
                    case "khoangdodem":
                        if (editType == 1) {
                            KHOANGDODEM.drawLoad(obj);
                        } else {
                            KHOANGDODEM.draw(obj);
                        }
                        break;
                    case "khoangdodemsymbol":
                        if (editType == 1) {
                            KHOANGDODEMSYMBOL.drawLoad(obj);
                        } else {
                            KHOANGDODEMSYMBOL.draw(obj);
                        }
                        break;
                    case "khoangmaycat":
                        if (editType == 1) {
                            KHOANGMAYCAT.drawLoad(obj);
                        } else {
                            KHOANGMAYCAT.draw(obj);
                        }
                        break;
                    case "khoangmaycatsymbol":
                        if (editType == 1) {
                            KHOANGMAYCATSYMBOL.drawLoad(obj);
                        } else {
                            KHOANGMAYCATSYMBOL.draw(obj);
                        }
                        break;
                    case "khoangthanhcai":
                        if (editType == 1) {
                            KHOANGTHANHCAI.drawLoad(obj);
                        } else {
                            KHOANGTHANHCAI.draw(obj);
                        }
                        break;
                    case "khoangthanhcaisymbol":
                        if (editType == 1) {
                            KHOANGTHANHCAISYMBOL.drawLoad(obj);
                        } else {
                            KHOANGTHANHCAISYMBOL.draw(obj);
                        }
                        break;
                    case "text":
                        if (editType == 1) {
                            TEXT.drawLoad(obj);
                        } else {
                            TEXT.draw(obj);
                        }
                        break;
                    case "panel2":
                        if (editType == 1) {
                            PANEL2.drawLoad(obj);
                        } else {
                            PANEL2.draw(obj);
                        }
                        break;
                    case "pd-htr02":
                        if (editType == 1) {
                            PDHTR02.drawLoad(obj);
                        } else {
                            PDHTR02.draw(obj);
                        }
                        break;
                    case "pd-ams01":
                        if (editType == 1) {
                            PDAMS01.drawLoad(obj);
                        } else {
                            PDAMS01.draw(obj);
                        }
                        break;
                    case "sys-image":
                        if (editType == 1) {
                            IMAGE.drawLoad(obj);
                        } else {
                            IMAGE.draw(obj);
                        }
                        break;
                };
            };
            for (let i = 0; i < dataJson.aArrows.length; i++) {
                var arrow = dataJson.aArrows[i];
                DATA.aArrows.push(arrow);
                 if (arrow.componentType == "arrow") {
                    if (editType == 1) {
                        ARROW2.drawLoad(arrow);
                    } else {
                        ARROW2.draw(arrow);
                    }
                } else {
                        if (editType == 1) {
                            ARROW.drawLoad(arrow);
                        } else {
                            ARROW.draw(arrow);
                        }
                }
            };

            DATA.selectedID = 0;
            outFocus();
        }
    };

    function selectDeviceMeter(e) {

        let selectList = $('#meterIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#meterIds').html(selectList);

        $("#meterForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#meterForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#meterIds option').length > 0) {

                            let deviceId = this.$content.find('#meterIds').val();
                            let deviceName = this.$content.find('#meterIds option:selected').html();
                            $("#meter-device-name").val(deviceName);
                            let meter = METER.collect();
                            meter.x = _x;
                            meter.y = _y;
                            meter.customerId = customerId;
                            meter.deviceId = deviceId;

                            METER.draw(meter);
                            DATA.aCards.push(meter);
                            METER.focus(meter.id);
                            DATA.selectedID = meter.id;

                            $("#meterIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#meterForm").hide();
                    }

                },
                cancel: function () {
                    $("#meterForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceStmv(e) {

        let selectList = $('#stmvIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#stmvIds').html(selectList);

        $("#stmvForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#stmvForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#stmvIds option').length > 0) {
                            let deviceId = this.$content.find('#stmvIds').val();
                            let deviceName = this.$content.find('#stmvIds option:selected').html();

                            $("#stmv-device-name").val(deviceName);

                            let stmv = STMV.collect();
                            stmv.x = _x;
                            stmv.y = _y;
                            stmv.customerId = customerId;
                            stmv.deviceName = deviceName;
                            stmv.deviceId = deviceId;

                            STMV.draw(stmv);
                            DATA.aCards.push(stmv);

                            $("#stmvIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#stmvForm").hide();
                    }

                },
                cancel: function () {
                    $("#stmvForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceSgmv(e) {

        let selectList = $('#sgmvIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#sgmvIds').html(selectList);

        $("#sgmvForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#sgmvForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#sgmvIds option').length > 0) {
                            let deviceId = this.$content.find('#sgmvIds').val();
                            let deviceName = this.$content.find('#sgmvIds option:selected').html();

                            $("#sgmv-device-name").val(deviceName);

                            let sgmv = SGMV.collect();
                            sgmv.x = _x;
                            sgmv.y = _y;
                            sgmv.customerId = customerId;
                            sgmv.deviceName = deviceName;
                            sgmv.deviceId = deviceId;

                            SGMV.draw(sgmv);
                            DATA.aCards.push(sgmv);
                            SGMV.focus(sgmv.id);
                            DATA.selectedID = sgmv.id;

                            $("#sgmvIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#sgmvForm").hide();
                    }
                },
                cancel: function () {
                    $("#sgmvForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceInverter(e) {

        let selectList = $('#inverterIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#inverterIds').html(selectList);

        $("#inverterForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#inverterForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#inverterIds option').length > 0) {
                            let deviceId = this.$content.find('#inverterIds').val();
                            let deviceName = this.$content.find('#inverterIds option:selected').html();

                            $("#inverter-device-name").val(deviceName);

                            let inverter = INVERTER.collect();
                            inverter.x = _x;
                            inverter.y = _y;
                            inverter.customerId = customerId;
                            inverter.deviceName = deviceName;
                            inverter.deviceId = deviceId;

                            INVERTER.draw(inverter);
                            DATA.aCards.push(inverter);
                            INVERTER.focus(inverter.id);
                            DATA.selectedID = inverter.id;

                            $("#inverterIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#inverterForm").hide();
                    }
                },
                cancel: function () {
                    $("#inverterForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceCombiner(e) {

        let selectList = $('#combinerIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#combinerIds').html(selectList);

        $("#combinerForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#combinerForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#combinerIds option').length > 0) {
                            let deviceId = this.$content.find('#combinerIds').val();
                            let deviceName = this.$content.find('#combinerIds option:selected').html();

                            $("#combiner-device-name").val(deviceName);

                            let combiner = COMBINER.collect();
                            combiner.x = _x;
                            combiner.y = _y;
                            combiner.customerId = customerId;
                            combiner.deviceName = deviceName;
                            combiner.deviceId = deviceId;

                            COMBINER.draw(combiner);
                            DATA.aCards.push(combiner);
                            COMBINER.focus(combiner.id);
                            DATA.selectedID = combiner.id;

                            $("#combinerIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#combinerForm").hide();
                    }
                },
                cancel: function () {
                    $("#combinerForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDevicePanel(e) {

        let selectList = $('#panelIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#panelIds').html(selectList);

        $("#panelForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#panelForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#panelIds option').length > 0) {
                            let deviceId = this.$content.find('#panelIds').val();
                            let deviceName = this.$content.find('#panelIds option:selected').html();

                            $("#panel-device-name").val(deviceName);

                            let panel = PANEL.collect();
                            panel.x = _x;
                            panel.y = _y;
                            panel.customerId = customerId;
                            panel.deviceName = deviceName;
                            panel.deviceId = deviceId;

                            PANEL.draw(panel);
                            DATA.aCards.push(panel);
                            PANEL.focus(panel.id);
                            DATA.selectedID = panel.id;

                            $("#panelIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#panelForm").hide();
                    }
                },
                cancel: function () {
                    $("#panelForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceString(e) {

        let selectList = $('#stringIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#stringIds').html(selectList);

        $("#stringForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#stringForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#stringIds option').length > 0) {
                            let deviceId = this.$content.find('#stringIds').val();
                            let deviceName = this.$content.find('#stringIds option:selected').html();

                            $("#string-device-name").val(deviceName);

                            let string = STRING.collect();
                            string.x = _x;
                            string.y = _y;
                            string.customerId = customerId;
                            string.deviceName = deviceName;
                            string.deviceId = deviceId;

                            STRING.draw(string);
                            DATA.aCards.push(string);
                            STRING.focus(string.id);
                            DATA.selectedID = string.id;

                            $("#stringIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#stringForm").hide();
                    }
                },
                cancel: function () {
                    $("#stringForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceWeather(e) {

        let selectList = $('#weatherIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#weatherIds').html(selectList);

        $("#weatherForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#weatherForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#weatherIds option').length > 0) {
                            let deviceId = this.$content.find('#weatherIds').val();
                            let deviceName = this.$content.find('#weatherIds option:selected').html();

                            $("#weather-device-name").val(deviceName);

                            let weather = WEATHER.collect();
                            weather.x = _x;
                            weather.y = _y;
                            weather.customerId = customerId;
                            weather.deviceName = deviceName;
                            weather.deviceId = deviceId;

                            WEATHER.draw(weather);
                            DATA.aCards.push(weather);
                            WEATHER.focus(weather.id);
                            DATA.selectedID = weather.id;

                            $("#weatherIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#weatherForm").hide();
                    }
                },
                cancel: function () {
                    $("#weatherForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {
                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoang1(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang1-device-name").val(deviceName);

                            let khoang = KHOANG1.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;

                            KHOANG1.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangCap(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-cap-device-name").val(deviceName);

                            let khoang = KHOANGCAP.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;

                            KHOANGCAP.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangDoDem(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-do-dem-device-name").val(deviceName);

                            let khoang = KHOANGDODEM.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;

                            KHOANGDODEM.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangMayCat1(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-may-cat-device-name").val(deviceName);

                            let khoang = KHOANGMAYCAT.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;
                            khoang.rmuType = 1;

                            KHOANGMAYCAT.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangMayCat2(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-may-cat-device-name").val(deviceName);

                            let khoang = KHOANGMAYCAT.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;
                            khoang.rmuType = 2;

                            KHOANGMAYCAT.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangMayCat3(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-may-cat-device-name").val(deviceName);

                            let khoang = KHOANGMAYCAT.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;
                            khoang.rmuType = 3;

                            KHOANGMAYCAT.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    function selectDeviceKhoangThanhCai(e) {

        let selectList = $('#khoangIds option');

        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

        selectList.sort(function (a, b) {
            a = a.value;
            b = b.value;

            return a - b;
        });

        $('#khoangIds').html(selectList);

        $("#khoangForm").show();
        $.confirm({
            title: 'Chọn thiết bị đã có hay không?!',
            content: $("#khoangForm").html(),
            buttons: {
                formSubmit: {
                    text: 'Chọn',
                    btnClass: 'btn-blue',
                    action: function () {
                        if ($('#khoangIds option').length > 0) {
                            let deviceId = this.$content.find('#khoangIds').val();
                            let deviceName = this.$content.find('#khoangIds option:selected').html();

                            $("#khoang-thanh-cai-device-name").val(deviceName);

                            let khoang = KHOANGTHANHCAI.collect();
                            khoang.x = _x;
                            khoang.y = _y;
                            khoang.customerId = customerId;
                            khoang.deviceName = deviceName;
                            khoang.deviceId = deviceId;

                            KHOANGTHANHCAI.draw(khoang);
                            DATA.aCards.push(khoang);

                            $("#khoangIds option[value='" + deviceId + "']").each(function () {
                                $(this).remove();
                            });
                        }
                        $("#khoangForm").hide();
                    }

                },
                cancel: function () {
                    $("#khoangForm").hide();
                },
            },
            onContentReady: function () {
                let jc = this;
                this.$content.find('form').on('submit', function (e) {

                    e.preventDefault();
                    jc.$$formSubmit.trigger('click');
                });
            }
        });
    };

    const handleOnDrop = (e) => {
        var meterAlreadyList = [];

        DATA.aCards.forEach(element => {
            meterAlreadyList.push(element);
        });

        meterAlreadyList.forEach(element => {
            if (element.deviceId != "") {
                $("#meter-device-id-" + element.meterType + " option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
                $("#htr02-device" + " option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
            }
        });

        var inverterAlreadyList = [];

        DATA.aCards.forEach(element => {
            if (element.type == "inverter") {
                inverterAlreadyList.push(element);
            }
        });

        inverterAlreadyList.forEach(element => {
            if (element.deviceId != "") {
                $("#inverterIds option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
            }
        });

        var combinerAlreadyList = [];

        DATA.aCards.forEach(element => {
            if (element.type == "combiner") {
                combinerAlreadyList.push(element);
            }
        });

        combinerAlreadyList.forEach(element => {
            if (element.deviceId != "") {
                $("#combinerIds option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
            }
        });

        var stringAlreadyList = [];

        DATA.aCards.forEach(element => {
            if (element.type == "string") {
                stringAlreadyList.push(element);
            }
        });

        stringAlreadyList.forEach(element => {
            if (element.deviceId != "") {
                $("#stringIds option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
            }
        });

        var panelAlreadyList = [];

        DATA.aCards.forEach(element => {
            if (element.type == "panel") {
                panelAlreadyList.push(element);
            }
        });

        panelAlreadyList.forEach(element => {
            if (element.deviceId != "") {
                $("#panelIds option[value='" + element.deviceId + "']").each(function () {
                    $(this).remove();
                });
            }
        });

        resetForm();
        $("#btn-update").show();
        $("#btn-remove").show();
        var _g_svg = d3.select("#svg-container");
        var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
        var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
        var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;
        switch (dragState) {
            case 2:
                let arrow = ARROW.collect();
                arrow.x = _x;
                arrow.y = _y;
                arrow.rotate = 1;
                ARROW.draw(arrow);
                DATA.aArrows.push(arrow);
                ARROW.focus(arrow.id);
                DATA.selectedID = arrow.id;
                break;
            case 3:
                let meter = METER.collect();
                meter.x = _x;
                meter.y = _y;
                meter.meterType = 1;
                meter.customerId = customerId;

                METER.draw(meter);
                DATA.aCards.push(meter);
                METER.focus(meter.id);
                DATA.selectedID = meter.id;
                DATA.deviceType = 1;
                $('#load-device-type-1').show();
                break;
            case 4:
                let mba = MBA.collect();
                mba.x = _x;
                mba.y = _y;

                MBA.draw(mba);
                DATA.aCards.push(mba);
                MBA.focus(mba.id);
                DATA.selectedID = mba.id;
                break;
            case 5:
                let rmu = RMU.collect();
                rmu.x = _x;
                rmu.y = _y;

                RMU.draw(rmu);
                DATA.aCards.push(rmu);
                RMU.focus(rmu.id);
                DATA.selectedID = rmu.id;
                break;
            case 6:
                let feeder = FEEDER.collect();
                feeder.x = _x;
                feeder.y = _y;

                FEEDER.draw(feeder);
                DATA.aCards.push(feeder);
                FEEDER.focus(feeder.id);
                DATA.selectedID = feeder.id;
                break;
            case 7:
                var position = $('input[name=mccb_position]:checked').val();
                if (position == "1") {
                    let mchor = MCHOR.collect();
                    mchor.x = _x;
                    mchor.y = _y;

                    MCHOR.draw(mchor);
                    DATA.aCards.push(mchor);
                    MCHOR.focus(mchor.id);
                    DATA.selectedID = mchor.id;
                } else if (position == "2") {
                    let mc = MC.collect();
                    mc.x = _x;
                    mc.y = _y;

                    MC.draw(mc);
                    DATA.aCards.push(mc);
                    MC.focus(mc.id);
                    DATA.selectedID = mc.id;
                };
                break;
            case 8:
                let label = LABEL.collect();
                label.x = _x;
                label.y = _y;

                LABEL.draw(label);
                DATA.aCards.push(label);
                LABEL.focus(label.id);
                DATA.selectedID = label.id;
                break;
            case 9:
                let ultility = ULTILITY.collect();
                ultility.x = _x;
                ultility.y = _y;

                ULTILITY.draw(ultility);
                DATA.aCards.push(ultility);
                ULTILITY.focus(ultility.id);
                DATA.selectedID = ultility.id;
                break;
            case 10:
                var position = $('input[name=busbar_position]:checked').val();
                var qty = $("#jointer-qty").val();
                if (qty == "") {
                    qty = 2;
                    $("#jointer-qty").val(2);
                }
                if (qty > 1) {
                    if (position == "1") {
                        let busbar = BUSBAR.collect();
                        busbar.x = _x;
                        busbar.y = _y;

                        DATA.aCards.push(busbar);
                        BUSBAR.focus(busbar.id);
                        DATA.selectedID = busbar.id;
                        BUSBAR.draw(busbar);
                    } else if (position == "2") {
                        let busbar = BUSBARVER.collect();
                        busbar.x = _x;
                        busbar.y = _y;

                        DATA.aCards.push(busbar);
                        BUSBAR.focus(busbar.id);
                        DATA.selectedID = busbar.id;
                        BUSBARVER.draw(busbar);
                    };
                }
                break;
            case 11:
                let gateway = METER.collect();
                gateway.x = _x;
                gateway.y = _y;
                gateway.meterType = 9;
                gateway.customerId = customerId;

                METER.draw(gateway);
                DATA.aCards.push(gateway);
                METER.focus(gateway.id);
                DATA.selectedID = gateway.id;
                DATA.deviceType = 9;
                $('#load-device-type-9').show();
                break;
            case 12:
                let trangThai = METER.collect();
                trangThai.x = _x;
                trangThai.y = _y;
                trangThai.meterType = 4;
                trangThai.customerId = customerId;

                METER.draw(trangThai);
                DATA.aCards.push(trangThai);
                METER.focus(trangThai.id);
                DATA.selectedID = trangThai.id;
                DATA.deviceType = 4;
                $('#load-device-type-4').show();
                break;
            case 13:
                let inverter = INVERTER.collect();
                inverter.x = _x;
                inverter.y = _y;
                inverter.customerId = customerId;

                INVERTER.draw(inverter);
                DATA.aCards.push(inverter);
                INVERTER.focus(inverter.id);
                DATA.selectedID = inverter.id;
                break;
            case 14:
                let invertersymbol = INVERTERSYMBOL.collect();
                invertersymbol.x = _x;
                invertersymbol.y = _y;

                INVERTERSYMBOL.draw(invertersymbol);
                DATA.aCards.push(invertersymbol);
                INVERTERSYMBOL.focus(invertersymbol.id);
                DATA.selectedID = invertersymbol.id;
                break;
            case 15:
                let combiner = COMBINER.collect();
                combiner.x = _x;
                combiner.y = _y;
                combiner.customerId = customerId;

                COMBINER.draw(combiner);
                DATA.aCards.push(combiner);
                COMBINER.focus(combiner.id);
                DATA.selectedID = combiner.id;
                break;
            case 16:
                let combinersymbol = COMBINERSYMBOL.collect();
                combinersymbol.x = _x;
                combinersymbol.y = _y;

                COMBINERSYMBOL.draw(combinersymbol);
                DATA.aCards.push(combinersymbol);
                COMBINERSYMBOL.focus(combinersymbol.id);
                DATA.selectedID = combinersymbol.id;
                break;
            case 17:
                let panel = PANEL.collect();
                panel.x = _x;
                panel.y = _y;
                panel.customerId = customerId;

                PANEL.draw(panel);
                DATA.aCards.push(panel);
                PANEL.focus(panel.id);
                DATA.selectedID = panel.id;
                break;
            case 18:
                let panelsymbol = PANELSYMBOL.collect();
                panelsymbol.x = _x;
                panelsymbol.y = _y;

                PANELSYMBOL.draw(panelsymbol);
                DATA.aCards.push(panelsymbol);
                PANELSYMBOL.focus(panelsymbol.id);
                DATA.selectedID = panelsymbol.id;
                break;
            case 19:
                let string = STRING.collect();
                string.x = _x;
                string.y = _y;
                string.customerId = customerId;

                STRING.draw(string);
                DATA.aCards.push(string);
                STRING.focus(string.id);
                DATA.selectedID = string.id;
                break;
            case 20:
                let stringsymbol = STRINGSYMBOL.collect();
                stringsymbol.x = _x;
                stringsymbol.y = _y;

                STRINGSYMBOL.draw(stringsymbol);
                DATA.aCards.push(stringsymbol);
                STRINGSYMBOL.focus(stringsymbol.id);
                DATA.selectedID = stringsymbol.id;
                break;
            case 21:
                selectDeviceWeather(e);
                break;
            case 22:
                let weathersymbol = WEATHERSYMBOL.collect();
                weathersymbol.x = _x;
                weathersymbol.y = _y;

                WEATHERSYMBOL.draw(weathersymbol);
                DATA.aCards.push(weathersymbol);
                WEATHERSYMBOL.focus(weathersymbol.id);
                DATA.selectedID = weathersymbol.id;
                break;
            case 23:
                let ups = UPS.collect();
                ups.x = _x;
                ups.y = _y;

                UPS.draw(ups);
                DATA.aCards.push(ups);
                UPS.focus(ups.id);
                DATA.selectedID = ups.id;
                break;
            case 24:
                selectDeviceKhoang1(e);
                break;
            case 25:
                let khoang1symbol = KHOANG1SYMBOL.collect();
                khoang1symbol.x = _x;
                khoang1symbol.y = _y;

                KHOANG1SYMBOL.draw(khoang1symbol);
                DATA.aCards.push(khoang1symbol);
                KHOANG1SYMBOL.focus(khoang1symbol.id);
                DATA.selectedID = khoang1symbol.id;
                break;
            case 26:
                selectDeviceKhoangCap(e);
                break;
            case 27:
                let khoangcapsymbol = KHOANGCAPSYMBOL.collect();
                khoangcapsymbol.x = _x;
                khoangcapsymbol.y = _y;

                KHOANGCAPSYMBOL.draw(khoangcapsymbol);
                DATA.aCards.push(khoangcapsymbol);
                KHOANGCAPSYMBOL.focus(khoangcapsymbol.id);
                DATA.selectedID = khoangcapsymbol.id;
                break;
            case 28:
                let khoangchi = KHOANGCHI.collect();
                khoangchi.x = _x;
                khoangchi.y = _y;

                KHOANGCHI.draw(khoangchi);
                DATA.aCards.push(khoangchi);
                KHOANGCHI.focus(khoangchi.id);
                DATA.selectedID = khoangchi.id;
                break;
            case 29:
                selectDeviceKhoangDoDem(e);
                break;
            case 30:
                let khoangdodemsymbol = KHOANGDODEMSYMBOL.collect();
                khoangdodemsymbol.x = _x;
                khoangdodemsymbol.y = _y;

                KHOANGDODEMSYMBOL.draw(khoangdodemsymbol);
                DATA.aCards.push(khoangdodemsymbol);
                KHOANGDODEMSYMBOL.focus(khoangdodemsymbol.id);
                DATA.selectedID = khoangdodemsymbol.id;
                break;
            case 311:
                selectDeviceKhoangMayCat1(e);
                break;
            case 312:
                selectDeviceKhoangMayCat2(e);
                break;
            case 313:
                selectDeviceKhoangMayCat3(e);
                break;
            case 321:
                let khoangmaycatsymbol1 = KHOANGMAYCATSYMBOL.collect();
                khoangmaycatsymbol1.x = _x;
                khoangmaycatsymbol1.y = _y;
                khoangmaycatsymbol1.rmuType = 1;

                KHOANGMAYCATSYMBOL.draw(khoangmaycatsymbol1);
                DATA.aCards.push(khoangmaycatsymbol1);
                KHOANGMAYCATSYMBOL.focus(khoangmaycatsymbol1.id);
                DATA.selectedID = khoangmaycatsymbol1.id;
                break;
            case 322:
                let khoangmaycatsymbol2 = KHOANGMAYCATSYMBOL.collect();
                khoangmaycatsymbol2.x = _x;
                khoangmaycatsymbol2.y = _y;
                khoangmaycatsymbol2.rmuType = 2;

                KHOANGMAYCATSYMBOL.draw(khoangmaycatsymbol2);
                DATA.aCards.push(khoangmaycatsymbol2);
                KHOANGMAYCATSYMBOL.focus(khoangmaycatsymbol2.id);
                DATA.selectedID = khoangmaycatsymbol2.id;
                break;
            case 323:
                let khoangmaycatsymbol3 = KHOANGMAYCATSYMBOL.collect();
                khoangmaycatsymbol3.x = _x;
                khoangmaycatsymbol3.y = _y;
                khoangmaycatsymbol3.rmuType = 3;

                KHOANGMAYCATSYMBOL.draw(khoangmaycatsymbol3);
                DATA.aCards.push(khoangmaycatsymbol3);
                KHOANGMAYCATSYMBOL.focus(khoangmaycatsymbol3.id);
                DATA.selectedID = khoangmaycatsymbol3.id;
                break;
            case 33:
                selectDeviceKhoangThanhCai(e);
                break;
            case 341:
                let khoangthanhcaisymbol1 = KHOANGTHANHCAISYMBOL.collect();
                khoangthanhcaisymbol1.x = _x;
                khoangthanhcaisymbol1.y = _y;
                khoangthanhcaisymbol1.rmuType = 1;

                KHOANGTHANHCAISYMBOL.draw(khoangthanhcaisymbol1);
                DATA.aCards.push(khoangthanhcaisymbol1);
                KHOANGTHANHCAISYMBOL.focus(khoangthanhcaisymbol1.id);
                DATA.selectedID = khoangthanhcaisymbol1.id;
                break;
            case 342:
                let khoangthanhcaisymbol2 = KHOANGTHANHCAISYMBOL.collect();
                khoangthanhcaisymbol2.x = _x;
                khoangthanhcaisymbol2.y = _y;
                khoangthanhcaisymbol2.rmuType = 2;

                KHOANGTHANHCAISYMBOL.draw(khoangthanhcaisymbol2);
                DATA.aCards.push(khoangthanhcaisymbol2);
                KHOANGTHANHCAISYMBOL.focus(khoangthanhcaisymbol2.id);
                DATA.selectedID = khoangthanhcaisymbol2.id;
                break;
            case 343:
                let khoangthanhcaisymbol3 = KHOANGTHANHCAISYMBOL.collect();
                khoangthanhcaisymbol3.x = _x;
                khoangthanhcaisymbol3.y = _y;
                khoangthanhcaisymbol3.rmuType = 3;

                KHOANGTHANHCAISYMBOL.draw(khoangthanhcaisymbol3);
                DATA.aCards.push(khoangthanhcaisymbol3);
                KHOANGTHANHCAISYMBOL.focus(khoangthanhcaisymbol3.id);
                DATA.selectedID = khoangthanhcaisymbol3.id;
                break;
            case 35:
                let text = TEXT.collect();
                text.x = _x;
                text.y = _y;

                TEXT.draw(text);
                DATA.aCards.push(text);
                TEXT.focus(text.id);
                DATA.selectedID = text.id;
                break;
            case 36:
                let nhienLieu = METER.collect();
                nhienLieu.x = _x;
                nhienLieu.y = _y;
                nhienLieu.meterType = 19;
                nhienLieu.customerId = customerId;

                METER.draw(nhienLieu);
                DATA.aCards.push(nhienLieu);
                METER.focus(nhienLieu.id);
                DATA.selectedID = nhienLieu.id;
                DATA.deviceType = 19;
                $('#load-device-type-19').show();
                break;
            case 37:
                let phongDien = METER.collect();
                phongDien.x = _x;
                phongDien.y = _y;
                phongDien.meterType = 5;
                phongDien.customerId = customerId;

                METER.draw(phongDien);
                DATA.aCards.push(phongDien);
                METER.focus(phongDien.id);
                DATA.selectedID = phongDien.id;
                DATA.deviceType = 5;
                $('#load-device-type-5').show();
                break;
            case 38:
                let apSuat = METER.collect();
                apSuat.x = _x;
                apSuat.y = _y;
                apSuat.meterType = 7;
                apSuat.customerId = customerId;

                METER.draw(apSuat);
                DATA.aCards.push(apSuat);
                METER.focus(apSuat.id);
                DATA.selectedID = apSuat.id;
                DATA.deviceType = 7;
                $('#load-device-type-7').show();
                break;
            case 39:
                let luuLuong = METER.collect();
                luuLuong.x = _x;
                luuLuong.y = _y;
                luuLuong.meterType = 10;
                luuLuong.customerId = customerId;

                METER.draw(luuLuong);
                DATA.aCards.push(luuLuong);
                METER.focus(luuLuong.id);
                DATA.selectedID = luuLuong.id;
                DATA.deviceType = 10;
                $('#load-device-type-10').show();
                break;
            case 40:
                let nhietDo = METER.collect();
                nhietDo.x = _x;
                nhietDo.y = _y;
                nhietDo.meterType = 3;
                nhietDo.customerId = customerId;

                METER.draw(nhietDo);
                DATA.aCards.push(nhietDo);
                METER.focus(nhietDo.id);
                DATA.selectedID = nhietDo.id;
                DATA.deviceType = 3;
                $('#load-device-type-3').show();
                break;
            case 41:
                let bucXa = METER.collect();
                bucXa.x = _x;
                bucXa.y = _y;
                bucXa.meterType = 11;
                bucXa.customerId = customerId;

                METER.draw(bucXa);
                DATA.aCards.push(bucXa);
                METER.focus(bucXa.id);
                DATA.selectedID = bucXa.id;
                DATA.deviceType = 11;
                $('#load-device-type-11').show();
                break;
            case 42:
                let mucNuoc = METER.collect();
                mucNuoc.x = _x;
                mucNuoc.y = _y;
                mucNuoc.meterType = 12;
                mucNuoc.customerId = customerId;

                METER.draw(mucNuoc);
                DATA.aCards.push(mucNuoc);
                METER.focus(mucNuoc.id);
                DATA.selectedID = mucNuoc.id;
                DATA.deviceType = 12;
                $('#load-device-type-12').show();
                break;
            case 43:
                let ph = METER.collect();
                ph.x = _x;
                ph.y = _y;
                ph.meterType = 13;
                ph.customerId = customerId;

                METER.draw(ph);
                DATA.aCards.push(ph);
                METER.focus(ph.id);
                DATA.selectedID = ph.id;
                DATA.deviceType = 13;
                $('#load-device-type-13').show();
                break;
            case 44:
                let gio = METER.collect();
                gio.x = _x;
                gio.y = _y;
                gio.meterType = 14;
                gio.customerId = customerId;

                METER.draw(gio);
                DATA.aCards.push(gio);
                METER.focus(gio.id);
                DATA.selectedID = gio.id;
                DATA.deviceType = 14;
                $('#load-device-type-14').show();
                break;
            case 45:
                let pin = METER.collect();
                pin.x = _x;
                pin.y = _y;
                pin.meterType = 15;
                pin.customerId = customerId;

                METER.draw(pin);
                DATA.aCards.push(pin);
                METER.focus(pin.id);
                DATA.selectedID = pin.id;
                DATA.deviceType = 15;
                $('#load-device-type-15').show();
                break;
            case 46:
                let label2 = LABEL2.collect(2);
                label2.x = _x;
                label2.y = _y;

                LABEL2.draw(label2);
                DATA.aCards.push(label2);
                LABEL2.focus(label2.id);
                DATA.selectedID = label2.id;
                break;
            case 47:
                let label3 = LABEL2.collect(3);
                label3.x = _x;
                label3.y = _y;

                label3.ox = _x;
                label3.oy = _y;

                LABEL2.draw(label3);
                DATA.aCards.push(label3);
                LABEL2.focus(label3.id);
                DATA.selectedID = label3.id;
                break;
            case 48:
                let label4 = LABEL2.collect(4);
                label4.x = _x;
                label4.y = _y;

                LABEL2.draw(label4);
                DATA.aCards.push(label4);
                LABEL2.focus(label4.id);
                DATA.selectedID = label4.id;
                break;
            case 49:
                let label5 = LABEL2.collect(5);
                label5.x = _x;
                label5.y = _y;

                LABEL2.draw(label5);
                DATA.aCards.push(label5);
                LABEL2.focus(label5.id);
                DATA.selectedID = label5.id;
                break;
            case 50:
                let label6 = LABEL2.collect(6);
                label6.x = _x;
                label6.y = _y;

                LABEL2.draw(label6);
                DATA.aCards.push(label6);
                LABEL2.focus(label6.id);
                DATA.selectedID = label6.id;
                break;
            case 51:
                let label7 = LABEL2.collect(7);
                label7.x = _x;
                label7.y = _y;

                LABEL2.draw(label7);
                DATA.aCards.push(label7);
                LABEL2.focus(label7.id);
                DATA.selectedID = label7.id;
                break;
            case 52:
                let label8 = LABEL2.collect(8);
                label8.x = _x;
                label8.y = _y;

                LABEL2.draw(label8);
                DATA.aCards.push(label8);
                LABEL2.focus(label8.id);
                DATA.selectedID = label8.id;
                break;
            case 53:
                let panel2 = PANEL2.collect();
                panel2.x = _x;
                panel2.y = _y;

                panel2.ox = _x;
                panel2.oy = _y;

                PANEL2.draw(panel2);
                DATA.aCards.push(panel2);
                PANEL2.focus(panel2.id);
                DATA.selectedID = panel2.id;
                break;
            case 54:
                let cbHTR02 = PDHTR02.collect();
                cbHTR02.x = _x;
                cbHTR02.y = _y;
                cbHTR02.deviceType = 5;
                cbHTR02.customerId = customerId;

                PDHTR02.draw(cbHTR02);
                DATA.aCards.push(cbHTR02);
                PDHTR02.focus(cbHTR02.id);
                DATA.selectedID = cbHTR02.id;
                DATA.deviceType = 5;
                $('#htr02-device').show();
                break;
            case 55:
                let cbAMS01 = PDAMS01.collect();
                cbAMS01.x = _x;
                cbAMS01.y = _y;
                cbAMS01.deviceType = 6;
                cbAMS01.customerId = customerId;

                PDAMS01.draw(cbAMS01);
                DATA.aCards.push(cbAMS01);
                PDAMS01.focus(cbAMS01.id);
                DATA.selectedID = cbAMS01.id;
                DATA.deviceType = 6;
                $('#ams01-device').show();
                break;
            case 56:
                let arrow2 = ARROW2.collect();
                arrow2.x = _x;
                arrow2.y = _y;
                arrow2.rotate = 1;
                ARROW2.draw(arrow2);
                DATA.aArrows.push(arrow2);
                ARROW2.focus(arrow2.id);
                DATA.selectedID = arrow2.id;
                break;
        }
        // if (dragState == 3 || dragState == 11 || dragState == 12 || dragState == 36 || dragState == 37 || dragState == 38 || dragState == 39 || dragState == 40 || dragState == 41 || dragState == 42 || dragState == 43 || dragState == 44 || dragState == 45) {
        //     showProperties(3);
        // } else {
        //     showProperties(dragState);
        // }
    };

    function handleOnDragOver(event) {
        event.preventDefault();
    };

    function handleUpdate() {
        update();
    };

    function handleNew() {
        window.location.href = "/category/tool-page/egrid-page-add/" + customerId + "/" + projectId + "/" + systemTypeId;
    };

    function handleRemove() {
        remove();
    };

    function update() {
        if ($("#arrow-properties").css("display") !== "none") {
            ARROW.updateArrow(DATA.selectedID);
            ARROW.focus(DATA.selectedID);
        } else if ($("#meter-properties").css("display") !== "none") {
            METER.update(DATA.selectedID);
            METER.focus(DATA.selectedID);
        } else if ($("#mba-properties").css("display") !== "none") {
            MBA.update(DATA.selectedID);
            MBA.focus(DATA.selectedID);
        } else if ($("#rmu-properties").css("display") !== "none") {
            RMU.update(DATA.selectedID);
            RMU.focus(DATA.selectedID);
        } else if ($("#feeder-properties").css("display") !== "none") {
            FEEDER.update(DATA.selectedID);
            FEEDER.focus(DATA.selectedID);
        } else if ($("#mc-properties").css("display") !== "none") {
            var position = $('input[name=mccb_position]:checked').val();
            if (position == "1") {
                MCHOR.update(DATA.selectedID);
                MCHOR.focus(DATA.selectedID);
            } else if (position == "2") {
                MC.update(DATA.selectedID);
                MC.focus(DATA.selectedID);
            };
        } else if ($("#label-properties").css("display") !== "none") {
            LABEL.update(DATA.selectedID);
            LABEL.focus(DATA.selectedID);
        } else if ($("#ultility-properties").css("display") !== "none") {
            ULTILITY.update(DATA.selectedID);
            ULTILITY.focus(DATA.selectedID);
        } else if ($("#busbar-properties").css("display") !== "none") {
            var position = $('input[name=busbar_position]:checked').val();
            var qty = $("#jointer-qty").val();
            if (qty > 1) {
                if (position == "1") {
                    BUSBAR.update(DATA.selectedID);
                } else if (position == "2") {
                    BUSBARVER.update(DATA.selectedID);
                };
            }
        } else if ($("#stmv-properties").css("display") !== "none") {
            STMV.update(DATA.selectedID);
            STMV.focus(DATA.selectedID);
        } else if ($("#sgmv-properties").css("display") !== "none") {
            SGMV.update(DATA.selectedID);
            SGMV.focus(DATA.selectedID);
        } else if ($("#inverter-properties").css("display") !== "none") {
            INVERTER.update(DATA.selectedID);
            INVERTER.focus(DATA.selectedID);
        } else if ($("#inverter-symbol-properties").css("display") !== "none") {
            INVERTERSYMBOL.update(DATA.selectedID);
            INVERTERSYMBOL.focus(DATA.selectedID);
        } else if ($("#combiner-properties").css("display") !== "none") {
            COMBINER.update(DATA.selectedID);
            COMBINER.focus(DATA.selectedID);
        } else if ($("#combiner-symbol-properties").css("display") !== "none") {
            COMBINERSYMBOL.update(DATA.selectedID);
            COMBINERSYMBOL.focus(DATA.selectedID);
        } else if ($("#panel-properties").css("display") !== "none") {
            PANEL.update(DATA.selectedID);
            PANEL.focus(DATA.selectedID);
        } else if ($("#panel-symbol-properties").css("display") !== "none") {
            PANELSYMBOL.update(DATA.selectedID);
            PANELSYMBOL.focus(DATA.selectedID);
        } else if ($("#string-properties").css("display") !== "none") {
            STRING.update(DATA.selectedID);
            STRING.focus(DATA.selectedID);
        } else if ($("#string-symbol-properties").css("display") !== "none") {
            STRINGSYMBOL.update(DATA.selectedID);
            STRINGSYMBOL.focus(DATA.selectedID);
        } else if ($("#weather-properties").css("display") !== "none") {
            WEATHER.update(DATA.selectedID);
            WEATHER.focus(DATA.selectedID);
        } else if ($("#weather-symbol-properties").css("display") !== "none") {
            WEATHERSYMBOL.update(DATA.selectedID);
            WEATHERSYMBOL.focus(DATA.selectedID);
        } else if ($("#ups-properties").css("display") !== "none") {
            UPS.update(DATA.selectedID);
            UPS.focus(DATA.selectedID);
        } else if ($("#khoang1-properties").css("display") !== "none") {
            KHOANG1.update(DATA.selectedID);
            KHOANG1.focus(DATA.selectedID);
        } else if ($("#khoang1-symbol-properties").css("display") !== "none") {
            KHOANG1SYMBOL.update(DATA.selectedID);
            KHOANG1SYMBOL.focus(DATA.selectedID);
        } else if ($("#khoang-cap-properties").css("display") !== "none") {
            KHOANGCAP.update(DATA.selectedID);
            KHOANGCAP.focus(DATA.selectedID);
        } else if ($("#khoang-cap-symbol-properties").css("display") !== "none") {
            KHOANGCAPSYMBOL.update(DATA.selectedID);
            KHOANGCAPSYMBOL.focus(DATA.selectedID);
        } else if ($("#khoang-chi-properties").css("display") !== "none") {
            KHOANGCHI.update(DATA.selectedID);
            KHOANGCHI.focus(DATA.selectedID);
        } else if ($("#khoang-do-dem-properties").css("display") !== "none") {
            KHOANGDODEM.update(DATA.selectedID);
            KHOANGDODEM.focus(DATA.selectedID);
        } else if ($("#khoang-do-dem-symbol-properties").css("display") !== "none") {
            KHOANGDODEMSYMBOL.update(DATA.selectedID);
            KHOANGDODEMSYMBOL.focus(DATA.selectedID);
        } else if ($("#khoang-may-cat-properties").css("display") !== "none") {
            KHOANGMAYCAT.update(DATA.selectedID);
            KHOANGMAYCAT.focus(DATA.selectedID);
        } else if ($("#khoang-may-cat-symbol-properties").css("display") !== "none") {
            KHOANGMAYCATSYMBOL.update(DATA.selectedID);
            KHOANGMAYCATSYMBOL.focus(DATA.selectedID);
        } else if ($("#khoang-thanh-cai-properties").css("display") !== "none") {
            KHOANGTHANHCAI.update(DATA.selectedID);
            KHOANGTHANHCAI.focus(DATA.selectedID);
        } else if ($("#khoang-thanh-cai-symbol-properties").css("display") !== "none") {
            KHOANGTHANHCAISYMBOL.update(DATA.selectedID);
            KHOANGTHANHCAISYMBOL.focus(DATA.selectedID);
        } else if ($("#text-properties").css("display") !== "none") {
            TEXT.update(DATA.selectedID);
            TEXT.focus(DATA.selectedID);
        } else if ($("#label2-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label3-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label4-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label5-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label6-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label7-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#label8-properties").css("display") !== "none") {
            LABEL2.update(DATA.selectedID);
            LABEL2.focus(DATA.selectedID);
        } else if ($("#htr02-properties").css("display") !== "none") {
            PDHTR02.update(DATA.selectedID);
            PDHTR02.focus(DATA.selectedID);
        } else if ($("#ams01-properties").css("display") !== "none") {
            PDAMS01.update(DATA.selectedID);
            PDAMS01.focus(DATA.selectedID);
        }

    };

    function remove() {
        if ($("#arrow-properties").css("display") !== "none") {
            ARROW.remove(DATA.selectedID);
        } else if ($("#meter-properties").css("display") !== "none") {
            METER.remove(DATA.selectedID);
        } else if ($("#mba-properties").css("display") !== "none") {
            MBA.remove(DATA.selectedID);
        } else if ($("#rmu-properties").css("display") !== "none") {
            RMU.remove(DATA.selectedID);
        } else if ($("#feeder-properties").css("display") !== "none") {
            FEEDER.remove(DATA.selectedID);
        } else if ($("#mc-properties").css("display") !== "none") {
            var position = $('input[name=mccb_position]:checked').val();
            if (position == "1") {
                MCHOR.remove(DATA.selectedID);
            } else if (position == "2") {
                MC.remove(DATA.selectedID);
            };
        } else if ($("#feeder-properties").css("display") !== "none") {
            FEEDER.remove(DATA.selectedID);
        } else if ($("#label-properties").css("display") !== "none") {
            LABEL.remove(DATA.selectedID);
        } else if ($("#ultility-properties").css("display") !== "none") {
            ULTILITY.remove(DATA.selectedID);
        } else if ($("#busbar-properties").css("display") !== "none") {
            var position = $('input[name=busbar_position]:checked').val();
            if (position == "1") {
                BUSBAR.remove(DATA.selectedID);
            } else if (position == "2") {
                BUSBARVER.remove(DATA.selectedID);
            };
        } else if ($("#stmv-properties").css("display") !== "none") {
            STMV.remove(DATA.selectedID);
        } else if ($("#sgmv-properties").css("display") !== "none") {
            SGMV.remove(DATA.selectedID);
        } else if ($("#inverter-properties").css("display") !== "none") {
            INVERTER.remove(DATA.selectedID);
        } else if ($("#inverter-symbol-properties").css("display") !== "none") {
            INVERTERSYMBOL.remove(DATA.selectedID);
        } else if ($("#combiner-properties").css("display") !== "none") {
            COMBINER.remove(DATA.selectedID);
        } else if ($("#combiner-symbol-properties").css("display") !== "none") {
            COMBINERSYMBOL.remove(DATA.selectedID);
        } else if ($("#panel-properties").css("display") !== "none") {
            PANEL.remove(DATA.selectedID);
        } else if ($("#panel-symbol-properties").css("display") !== "none") {
            PANELSYMBOL.remove(DATA.selectedID);
        } else if ($("#string-properties").css("display") !== "none") {
            STRING.remove(DATA.selectedID);
        } else if ($("#string-symbol-properties").css("display") !== "none") {
            STRINGSYMBOL.remove(DATA.selectedID);
        } else if ($("#weather-properties").css("display") !== "none") {
            WEATHER.remove(DATA.selectedID);
        } else if ($("#weather-symbol-properties").css("display") !== "none") {
            WEATHERSYMBOL.remove(DATA.selectedID);
        } else if ($("#ups-properties").css("display") !== "none") {
            UPS.remove(DATA.selectedID);
        } else if ($("#khoang1-properties").css("display") !== "none") {
            KHOANG1.remove(DATA.selectedID);
        } else if ($("#khoang1-symbol-properties").css("display") !== "none") {
            KHOANG1SYMBOL.remove(DATA.selectedID);
        } else if ($("#khoang-cap-properties").css("display") !== "none") {
            KHOANGCAP.remove(DATA.selectedID);
        } else if ($("#khoang-cap-symbol-properties").css("display") !== "none") {
            KHOANGCAPSYMBOL.remove(DATA.selectedID);
        } else if ($("#khoang-chi-properties").css("display") !== "none") {
            KHOANGCHI.remove(DATA.selectedID);
        } else if ($("#khoang-do-dem-properties").css("display") !== "none") {
            KHOANGDODEM.remove(DATA.selectedID);
        } else if ($("#khoang-do-dem-symbol-properties").css("display") !== "none") {
            KHOANGDODEMSYMBOL.remove(DATA.selectedID);
        } else if ($("#khoang-may-cat-properties").css("display") !== "none") {
            KHOANGMAYCAT.remove(DATA.selectedID);
        } else if ($("#khoang-may-cat-symbol-properties").css("display") !== "none") {
            KHOANGMAYCATSYMBOL.remove(DATA.selectedID);
        } else if ($("#khoang-thanh-cai-properties").css("display") !== "none") {
            KHOANGTHANHCAI.remove(DATA.selectedID);
        } else if ($("#khoang-thanh-cai-symbol-properties").css("display") !== "none") {
            KHOANGTHANHCAISYMBOL.remove(DATA.selectedID);
        } else if ($("#text-properties").css("display") !== "none") {
            TEXT.remove(DATA.selectedID);
        } else if ($("#label2-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label3-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label4-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label5-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label6-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label7-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#label8-properties").css("display") !== "none") {
            LABEL2.remove(DATA.selectedID);
        } else if ($("#htr02-properties").css("display") !== "none") {
            PDHTR02.remove(DATA.selectedID);
        } else if ($("#ams01-properties").css("display") !== "none") {
            PDAMS01.remove(DATA.selectedID);
        }
        resetForm();
        showProperties(1);
    };

    function focusDevice(deviceId) {
        var deviceIndex = DATA.aCards.findIndex(x => x.deviceId == deviceId);
        if (deviceIndex >= 0) {
            setDeviceCurrent(deviceId);
            var deviceX = DATA.aCards[deviceIndex].x;
            var deviceY = DATA.aCards[deviceIndex].y;
            var canvasWidth = $("#grid-canvas").width();
            var canvasHeight = $("#grid-canvas").height();
            var deviceWidth = null;
            var deviceHeight = null;
            var deviceRect = null
            if (DATA.aCards[deviceIndex].type == "meter") {
                deviceWidth = $("#" + METER_LOAD.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + METER_LOAD.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + METER_LOAD.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "stmv") {
                deviceWidth = $("#" + STMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + STMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + STMV.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "sgmv") {
                deviceWidth = $("#" + SGMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + SGMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + SGMV.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "inverter") {
                deviceWidth = $("#" + INVERTER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + INVERTER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + INVERTER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "combiner") {
                deviceWidth = $("#" + COMBINER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + COMBINER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + COMBINER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "panel") {
                deviceWidth = $("#" + PANEL.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PANEL.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PANEL.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "string") {
                deviceWidth = $("#" + STRING.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + STRING.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + STRING.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "weather") {
                deviceWidth = $("#" + WEATHER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + WEATHER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + WEATHER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "khoang1") {
                deviceWidth = $("#" + KHOANG1.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + KHOANG1.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + KHOANG1.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "khoangcap") {
                deviceWidth = $("#" + KHOANGCAP.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + KHOANGCAP.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + KHOANGCAP.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "khoangdodem") {
                deviceWidth = $("#" + KHOANGDODEM.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + KHOANGDODEM.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + KHOANGDODEM.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "khoangmaycat") {
                deviceWidth = $("#" + KHOANGMAYCAT.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + KHOANGMAYCAT.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + KHOANGMAYCAT.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "khoangthanhcai") {
                deviceWidth = $("#" + KHOANGTHANHCAI.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + KHOANGTHANHCAI.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + KHOANGTHANHCAI.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "pd-htr02") {
                deviceWidth = $("#" + PDHTR02.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PDHTR02.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PDHTR02.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "pd-ams01") {
                deviceWidth = $("#" + PDAMS01.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PDAMS01.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PDAMS01.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            }

            var xCenter = (canvasWidth / 2) - deviceX - deviceWidth / 2;
            var yCenter = (canvasHeight / 2) - deviceY - deviceHeight / 2;

            var g_container = d3.select("#svg-container");
            g_container.attr('transform', "translate(" + xCenter + "," + yCenter + ")");
            deviceRect.attr('style', "stroke-width:1;stroke:rgb(167, 201, 66)");
        }

    }

    async function handleDeleteSystemMap(targetId) {
        $.confirm({
            type: 'red',
            typeAnimated: true,
            icon: 'fa fa-warning',
            title: 'Xác nhận!',
            content: 'Bạn có chắc chắn muốn xóa sơ đồ này?',
            buttons: {
                confirm: {
                    text: 'Đồng ý',
                    action: async () => {
                        let deleteSystemMapResponse = await SystemMapService.deleteSystemMap(targetId);
                        if (deleteSystemMapResponse.status === 200) {
                            window.location.href = "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId;
                        }
                    },
                },
                cancel: {
                    text: 'Hủy bỏ',
                    action: function () {
                        // $.alert('Hủy bỏ!');
                    }
                }
            }
        })
    }

    async function handleUpdateSystemMap() {
        focusSvg();
        let data = JSON.stringify(DATA);
        let deviceUpdateList = [];
        $("#meter-device-id-1 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-3 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-4 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-5 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-6 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-7 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-8 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-9 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-10 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-11 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-12 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-13 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-14 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-15 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#meter-device-id-19 option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#stmvIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#sgmvIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#inverterIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#combinerIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#panelIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#stringIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });
        $("#khoangIds option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });

        $("#htr02-device option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });

        $("#ams01-device option:not(:selected)").each(function () {
            if ($(this).val() != "") {
                deviceUpdateList.push($(this).val());
            }
        });

        var deviceInMapIndexList = [];

        if (DATA.aCards.length > 0) {
            for (let i = 0; i < DATA.aCards.length; i++) {
                if (typeof DATA.aCards[i].deviceId !== "undefined" && DATA.aCards[i].deviceId != "") {
                    deviceInMapIndexList.push(DATA.aCards[DATA.aCards.findIndex(x => x.deviceId == DATA.aCards[i].deviceId)].deviceId);
                }
            }
        }

        var deviceEmptyList = []

        for (var i = 0; i < deviceUpdateList.length; i++) {
            if (!deviceInMapIndexList.includes(deviceUpdateList[i])) {
                deviceEmptyList.push(deviceUpdateList[i]);
            }
        }

        let listDeviceUpdate = JSON.stringify(deviceEmptyList);
        let deviceJsonList = "";

        if (DATA.aCards.length > 0) {
            for (let i = 0; i < DATA.aCards.length; i++) {
                let obj = DATA.aCards[i];
                if ((obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv"
                    || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather"
                    || obj.type == "khoang1" || obj.type == "khoangcap" || obj.type == "khoangdodem" || obj.type == "khoangmaycat" || obj.type == "khoangthanhcai"
                ) && obj.caculator == 1) {
                    deviceJsonList = deviceJsonList + obj.deviceId + ",";
                }
            }
        }

        systemMapCurrent.name = systemMapName;
        systemMapCurrent.jsonData = data;

        let updateSystemMapResponse = await SystemMapService.updateSystemMap(systemMapCurrent, listDeviceUpdate, deviceJsonList);
        if (updateSystemMapResponse.status == 200) {
            // $("#system-map-name-" + systemMapCurrent.id).text(systemMapName);

            getSystemMapList();
            getDeviceAlreadyList();
            $.alert("Cập nhật window thành công");
        } else {
            $.alert("Cập nhật window không thành công");
        }

        window.location.href = "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId;

    };

    async function handleBack() {
        if (editType == 2) {
            focusSvg();
            let data = JSON.stringify(DATA);
            let deviceUpdateList = [];
            $("#meter-device-id-1 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-3 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-4 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-5 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-6 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-7 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-8 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-9 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-10 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-11 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-12 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-13 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-14 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-15 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#meter-device-id-19 option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#stmvIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#sgmvIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#inverterIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#combinerIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#panelIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#stringIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });
            $("#khoangIds option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });

            $("#htr02-device option").each(function () {
                if ($(this).val() != "") {
                    deviceUpdateList.push($(this).val());
                }
            });

            var deviceInMapIndexList = [];

            if (DATA.aCards.length > 0) {
                for (let i = 0; i < DATA.aCards.length; i++) {
                    if (typeof DATA.aCards[i].deviceId !== "undefined" && DATA.aCards[i].deviceId != "") {
                        deviceInMapIndexList.push(DATA.aCards[DATA.aCards.findIndex(x => x.deviceId == DATA.aCards[i].deviceId)].deviceId);
                    }
                }
            }

            var deviceEmptyList = []

            for (var i = 0; i < deviceUpdateList.length; i++) {
                if (!deviceInMapIndexList.includes(deviceUpdateList[i])) {
                    deviceEmptyList.push(deviceUpdateList[i]);
                }
            }

            let listDeviceUpdate = JSON.stringify(deviceEmptyList);
            let jsonDataCurrent = "";
            let deviceJsonList = "";
            if (DATA.aCards.length > 0) {
                for (let i = 0; i < DATA.aCards.length; i++) {
                    let obj = DATA.aCards[i];
                    if ((obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv"
                        || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather"
                        || obj.type == "khoang1" || obj.type == "khoangcap" || obj.type == "khoangdodem" || obj.type == "khoangmaycat" || obj.type == "khoangthanhcai"
                    ) && obj.caculator == 1) {
                        deviceJsonList = deviceJsonList + obj.deviceId + ",";
                    }
                }
            }
            if (systemMapCurrent.jsonData != null) {
                jsonDataCurrent = JSON.parse(systemMapCurrent.jsonData);
            }
            if (JSON.stringify(jsonDataCurrent.aCards) != JSON.stringify(DATA.aCards) || JSON.stringify(jsonDataCurrent.aArrows) != JSON.stringify(DATA.aArrows) || systemMapCurrent.name != systemMapName) {
                systemMapCurrent.name = systemMapName;
                systemMapCurrent.jsonData = data;

                $.confirm({
                    type: 'red',
                    typeAnimated: true,
                    icon: 'fa fa-warning',
                    title: 'Xác nhận!',
                    content: 'Bạn có muốn lưu dữ liệu?',
                    buttons: {
                        confirm: {
                            text: 'Đồng ý',
                            action: async () => {
                                let updateSystemMapResponse = await SystemMapService.updateSystemMap(systemMapCurrent, listDeviceUpdate, deviceJsonList);
                                if (updateSystemMapResponse.status === 200) {
                                    window.location.href = "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId;
                                }
                            },
                        },
                        cancel: {
                            text: 'Hủy bỏ',
                            action: function () {
                                window.location.href = "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId;
                            }
                        }
                    }
                })
            } else {
                window.location.href = "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId;
            }
        } else {
            window.location.href = "/category/tool-page/project-page/" + customerId;
        }
    };

    function handleChangeUpdate() {
        onChangeUpdate();
    };

    function funcOpenWindowImage(e) {
        let subMenu = document.getElementById(e.currentTarget.id).nextSibling;
        if (subMenu.style.display == 'block') {
            subMenu.style.display = 'none'
        } else {
            subMenu.style.display = 'block'
        }

    };

    function funcDragOverImage(e) {
        e.preventDefault();
        let drag = document.getElementById("system-map-drag");
        drag.classList.add('active');
        if (drag.children.length > 1) {
            drag.children[0].style.color = "#0a1a5c";
            drag.children[1].textContent = "Thả ảnh";

        }

    };

    function funcDragLeaveImage(e) {
        let drag = document.getElementById("system-map-drag");
        drag.classList.remove('active');
        if (drag.children.length > 1) {
            drag.children[0].style.color = "#c9c9c9";
            drag.children[1].textContent = "Kéo & thả";

        }
    };

    function funcDropImage(e) {
        e.preventDefault();
        let drag = document.getElementById("system-map-drag");
        let file = e.dataTransfer.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (
                file.type === "image/jpeg" ||
                file.type === "image/jpg" ||
                file.type === "image/png" ||
                file.type === "image/gif" ||
                file.type === "image/svg+xml" ||
                file.type === "image/tiff" ||
                file.type === "image/bmp" ||
                file.type === "image/webp"
            ) {
                reader.onload = (event) => {

                    var image = new Image();
                    image.src = event.target.result;

                    image.onload = function () {
                        var height = this.height;
                        var width = this.width;
                        let imgTag = `<img src= "${image.src}" alt="" width="${width}" height="${height}">`;
                        drag.innerHTML = imgTag;
                    };

                };
            } else {
                $.alert({
                    title: 'Thông báo',
                    content: 'Định dạng ảnh không hợp lệ (vd: image.jpg, image.png, ...)'
                });
            }
        } else {
            $.alert({
                title: 'Thông báo',
                content: 'Size ảnh không được quá 10mb'
            });
        }
    };

    function funcUpImage(e) {
        e.preventDefault();
        let drag = document.getElementById("system-map-drag");
        let file = e.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        if (file.size < 10000000) {
            if (
                file.type === "image/jpeg" ||
                file.type === "image/jpg" ||
                file.type === "image/png" ||
                file.type === "image/gif" ||
                file.type === "image/svg+xml" ||
                file.type === "image/tiff" ||
                file.type === "image/bmp" ||
                file.type === "image/webp"
            ) {
                reader.onload = (event) => {
                    var image = new Image();
                    image.src = event.target.result;

                    image.onload = function () {
                        var height = this.height;
                        var width = this.width;
                        let imgTag = `<img src= "${image.src}" alt="" width="${width}" height="${height}">`;
                        drag.innerHTML = imgTag;
                    };
                };
            } else {
                $.alert({
                    title: 'Thông báo',
                    content: 'Định dạng ảnh không hợp lệ (vd: image.jpg, image.png, ...)'
                });
            }
        } else {
            $.alert({
                title: 'Thông báo',
                content: 'Size ảnh không được quá 10mb'
            });
        }
    };

    function funcSystemMapImage(e) {
        let drag = document.getElementById("system-map-drag");
        if (drag.children.length > 0) {
            let srcImage = drag.children[0].src;
            let width = drag.children[0].naturalWidth;
            let height = drag.children[0].naturalHeight;
            var _g_svg = d3.select("#svg-container");
            var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
            var _x = (e.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
            var _y = (e.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;
            let img = IMAGE.collect(srcImage, width, height);
            img.x = _x;
            img.y = _y;

            img.ox = _x;
            img.oy = _y;

            IMAGE.draw(img);
            DATA.aCards.push(img);
            IMAGE.focus(img.id);
            DATA.selectedID = img.id;
        }
    };

    useEffect(() => {
        getPowers();
        createInstance();
        getSystemMapList();
        getDeviceAlreadyList();
        if (systemMapId) {
            getSystemMapCurrent();
            getDeviceEmptyList();
        };
        $("#meterForm").hide();
        $("#stmvForm").hide();
        $("#sgmvForm").hide();
        $("#inverterForm").hide();
        $("#combinerForm").hide();
        $("#panelForm").hide();
        $("#stringForm").hide();
        $("#weatherForm").hide();
        $("#khoangForm").hide();
        showProperties(1);
    }, [location]);

    return (
        <div>
            <div id="page-body">
                <div className="project-infor" style={{ padding: "5px 10px", display: "block" }}>
                    <FaHome className='d-inline-block' /> &nbsp; <span className="project-tree">{projectInfor}</span>
                </div>
                <div className="window-info">
                    <div style={{ color: `${deviceTime == "Không có dữ liệu" ? "#dc3545" : "#45484D"}` }}>
                    </div>
                    <div className='mt-4'>
                        {breadscrum}
                    </div>
                    {
                        editType == 2 &&
                        <>
                            <div className='mt-4 d-inline-block' id="id-sys-map-img" onClick={(e) => funcOpenWindowImage(e)}>
                                <i className="fa-regular fa-image fa-xl" style={{ cursor: "pointer" }}></i>
                            </div>
                            <div className='mt-4 p-1 border-window-image' style={{ display: 'none', width: '200px' }}>
                                <div id="system-map-drag" className='p-1 border-dash-window-image system-map-drag' onDragOver={(e) => funcDragOverImage(e)} onDragLeave={(e) => funcDragLeaveImage(e)} onDrop={(e) => funcDropImage(e)}>
                                    <div className='system-map-image'>
                                        <i className='icon-system-map fa-solid fa-images'> </i>
                                    </div>
                                    <div>
                                        <label readOnly>Kéo & thả</label>
                                    </div>

                                </div>
                                <div className='p-1' style={{ display: "grid", gridTemplateColumns: "50% 50%" }}>
                                    <div className='p-1 w-100'>
                                        <input type="file" name="file" id="file" className="inputfile" onChange={(e) => funcUpImage(e)} />
                                        <label htmlFor="file"> <i className="fa-solid fa-arrow-up-from-bracket pr-1" style={{ color: "#FFF" }}></i>Tải</label>
                                    </div>

                                    <div className='p-1 w-100'>
                                        <input type="button" name="button" id="buttonSystemMapImage" className="inputfile" onClick={(e) => funcSystemMapImage(e)} />
                                        <label htmlFor="buttonSystemMapImage"> <i className="fa-solid fa-plus pr-1" style={{ color: "#FFF" }}></i>Thêm</label>
                                    </div>
                                </div>
                            </div>
                        </>
                    }

                </div>
                <div className="grid-utility">
                    <ul className="nav nav-tabs nav-top-border nav-justified" >
                        {systemMapId > 0 && editType != 1 ?
                            <li className={`nav-item active`}>
                                <a className="nav-link" id="tab-1" data-toggle="tab" aria-controls="tab-toolbox" href="#tab-toolbox"
                                    role="tab" aria-selected="false" onClick={() => setToggleTab(2)}>
                                    <i className="fas fa-tools"></i><span>ToolBox</span>
                                </a>
                            </li>
                            :
                            <li className={`nav-item active`}>
                                <a className="nav-link" id="tab-0" data-toggle="tab" aria-controls="tab-window" href="#tab-window"
                                    role="tab" aria-selected="true" onClick={() => setToggleTab(1)}>
                                    <i className="fa fa-tasks"></i> <span>Windows</span>
                                </a>
                            </li>
                        }
                    </ul>

                    <div className="tab-content pt-1">
                        {
                            editType == 2 ?
                                <div className="tab-pane active" id="tab-toolbox"
                                    aria-labelledby="baseIcon-tab-toolbox">
                                    <button id="btn-update-map" className="btn btn-outline-primary mr-1 btn-tool" onClick={handleUpdateSystemMap}>
                                        <i className="fa-solid fa-circle-check"> Update</i>
                                    </button>
                                    <button id="reset-zoom" className="btn btn-outline-primary mr-1 btn-tool">
                                        <i className="fa-solid fa-rotate"> Reset Zoom</i>
                                    </button>
                                    <button id="btn-switch-map" className="btn btn-outline-primary mr-1 btn-tool" onClick={handleBack}>
                                        <i className="fa-solid fa-circle-check"> Back</i>
                                    </button>
                                    <div id="component-2" className="grid-component" onClick={() => handleShowProperties(2)}
                                        draggable="true" onDragStart={handleDragStartArrow}>
                                        <img src="/resources/image/components/icon-line.png" alt="Line" />
                                        <span > Line</span>
                                    </div>
                                    {/**Arrow set up giá trị 56 */}
                                    <div id="component-56" className="grid-component" onClick={() => handleShowProperties(56)}
                                        draggable="true" onDragStart={() => handleDragStartComponent(56)}>
                                        <img src="/resources/image/components/icon-arrow.png" alt="Arrow" />
                                        <span > Arrow</span>
                                    </div>
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div id="component-3" className="grid-component"
                                            draggable="true" onDragStart={handleDragStartMeter}>
                                            <img src="/resources/image/components/new/meter.png" alt="Meter" />
                                            <span > Meter</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartGateway}>
                                            <img src="/resources/image/components/new/gateway.png" alt="Gateway" />
                                            <span > Gateway</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartTrangThai}>
                                            <img src="/resources/image/components/new/cb_trang_thai.png" alt="Cảm biến trạng thái" />
                                            <span > CB Trạng Thái</span>
                                        </div>
                                    }
                                    {/* {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartPhongDien}>
                                            <img src="/resources/image/components/new/cb_phong_dien.png" alt="Cảm biến phóng điện" />
                                            <span > CB Phóng Điện</span>
                                        </div>
                                    } */}
                                    {
                                        <div id="component-54" className="grid-component"
                                            draggable="true" onDragStart={handleDragStartPhongDienHTR02} onClick={() => handleShowProperties(54)}>
                                            <img src="/resources/image/components/cb-phong-dien/cb_phong_dien_htr02.svg" alt="Cảm biến phóng điện" />
                                            <span > CB Phóng Điện HTR02</span>
                                        </div>
                                    }
                                    {
                                        <div id="component-55" className="grid-component"
                                            draggable="true" onDragStart={handleDragStartPhongDienAMS01} onClick={() => handleShowProperties(55)}>
                                            <img src="/resources/image/components/cb-phong-dien/cb_phong_dien_ams01.svg" alt="Cảm biến phóng điện" />
                                            <span > CB Phóng Điện AMS01</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartApSuat}>
                                            <img src="/resources/image/components/new/cb_ap_suat.png" alt="Cảm biến áp suất" />
                                            <span > CB Áp Suất</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartLuuLuong}>
                                            <img src="/resources/image/components/new/cb_luu_luong.png" alt="Cảm biến lưu lượng" />
                                            <span > CB Lưu Lượng</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartNhietDo}>
                                            <img src="/resources/image/components/new/cb_nhiet_do_do_am.png" alt="Cảm biến nhiệt độ, độ ẩm" />
                                            <span > CB Nhiệt Độ, Độ Ẩm</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartBucXa}>
                                            <img src="/resources/image/components/new/cb_buc_xa.png" alt="Cảm biến bức xạ" />
                                            <span > CB Bức Xạ</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartMucNuoc}>
                                            <img src="/resources/image/components/new/cb_muc_nuoc.png" alt="Cảm biến mức nước" />
                                            <span > CB Mức Nước</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartPH}>
                                            <img src="/resources/image/components/new/cb_ph.png" alt="Cảm biến ph" />
                                            <span > CB PH</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartGio}>
                                            <img src="/resources/image/components/new/cb_toc_do_gio.png" alt="Cảm biến tốc độ gió" />
                                            <span > CB Tốc Độ Gió</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartNhienLieu}>
                                            <img src="/resources/image/components/new/cb_muc_nhien_lieu.png" alt="Cảm biến mức nhiên liệu" />
                                            <span > CB Mức Nhiên Liệu</span>
                                        </div>
                                    }
                                    {
                                        (systemTypeId == 1 || systemTypeId == 2 || systemTypeId == 5) &&
                                        <div className="grid-component"
                                            draggable="true" onDragStart={handleDragStartPin}>
                                            <img src="/resources/image/components/new/cb_dung_luong_pin.png" alt="Cảm biến dung lượng pin" />
                                            <span > CB Dung Lượng Pin</span>
                                        </div>
                                    }
                                    <div id="component-4" className="grid-component" onClick={() => handleShowProperties(4)}
                                        draggable="true" onDragStart={handleDragStartMBA}>
                                        <img src="/resources/image/components/mba/mba-symbol.png" alt="Mba" />
                                        <span > Mba</span>
                                    </div>
                                    <div id="component-5" className="grid-component" onClick={() => handleShowProperties(5)}
                                        draggable="true" onDragStart={handleDragStartRMU}>
                                        <img src="/resources/image/components/rmu/rmu-symbol.png" alt="Rmu" />
                                        <span > Rmu</span>
                                    </div>
                                    <div id="component-6" className="grid-component" onClick={() => handleShowProperties(6)}
                                        draggable="true" onDragStart={handleDragStartFeeder}>
                                        <img src="/resources/image/components/icon-feeder.png" alt="Feeder" />
                                        <span > Feeder</span>
                                    </div>
                                    <div id="component-7" className="grid-component" onClick={() => handleShowProperties(7)}
                                        draggable="true" onDragStart={handleDragStartMc}>
                                        <img src="/resources/image/components/icon-mccb.png" alt="Mc" />
                                        <span > Mc</span>
                                    </div>
                                    <div id="component-8" className="grid-component" onClick={() => handleShowProperties(8)}
                                        draggable="true" onDragStart={handleDragStartLabel}>
                                        <img src="/resources/image/components/icon-label.png" alt="Label" />
                                        <span > Label</span>
                                    </div>
                                    <div id="component-46" className="grid-component" onClick={() => handleShowProperties(46)}
                                        draggable="true" onDragStart={handleDragStartLabel02}>
                                        <img src="/resources/image/components/label_02/label_02.svg" alt="Label 02" />
                                        <span > Label 02</span>
                                    </div>
                                    <div id="component-47" className="grid-component" onClick={() => handleShowProperties(47)}
                                        draggable="true" onDragStart={handleDragStartLabel03}>
                                        <img src="/resources/image/components/label_02/label_03.svg" alt="Label 03" />
                                        <span > Label 03</span>
                                    </div>
                                    <div id="component-48" className="grid-component" onClick={() => handleShowProperties(48)}
                                        draggable="true" onDragStart={handleDragStartLabel04}>
                                        <img src="/resources/image/components/label_02/label_04.svg" alt="Label 04" />
                                        <span > Label 04</span>
                                    </div>
                                    <div id="component-49" className="grid-component" onClick={() => handleShowProperties(49)}
                                        draggable="true" onDragStart={handleDragStartLabel05}>
                                        <img src="/resources/image/components/label_02/label_05.svg" alt="Label 05" />
                                        <span > Label 05</span>
                                    </div>
                                    <div id="component-50" className="grid-component" onClick={() => handleShowProperties(50)}
                                        draggable="true" onDragStart={handleDragStartLabel06}>
                                        <img src="/resources/image/components/label_02/label_06.svg" alt="Label 06" />
                                        <span > Label 06</span>
                                    </div>
                                    <div id="component-51" className="grid-component" onClick={() => handleShowProperties(51)}
                                        draggable="true" onDragStart={handleDragStartLabel07}>
                                        <img src="/resources/image/components/label_02/label_07.svg" alt="Label 07" />
                                        <span > Label 07</span>
                                    </div>
                                    <div id="component-52" className="grid-component" onClick={() => handleShowProperties(52)}
                                        draggable="true" onDragStart={handleDragStartLabel08}>
                                        <img src="/resources/image/components/label_02/label_08.svg" alt="Label 08" />
                                        <span > Label 08</span>
                                    </div>
                                    <div id="component-9" className="grid-component" onClick={() => handleShowProperties(9)}
                                        draggable="true" onDragStart={handleDragStartUltility}>
                                        <img src="/resources/image/components/icon-ultility.png" alt="Ultility" />
                                        <span > Utility</span>
                                    </div>
                                    <div id="component-10" className="grid-component" onClick={() => handleShowProperties(10)}
                                        draggable="true" onDragStart={handleDragStartBusbar}>
                                        <img src="/resources/image/components/icon-busbar.png" alt="Busbar" />
                                        <span > Busbar</span>
                                    </div>
                                    <div id="component-35" className="grid-component" onClick={() => handleShowProperties(35)}
                                        draggable="true" onDragStart={handleDragStartText}>
                                        <img src="/resources/image/components/icon-text.png" alt="Text" />
                                        <span > Text</span>
                                    </div>
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-13" className="grid-component" onClick={() => handleShowProperties(13)}
                                            draggable="true" onDragStart={handleDragStartInverter}>
                                            <img src="/resources/image/components/inverter/inverter-symbol.png" alt="Inverter" />
                                            <span > Inverter</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-15" className="grid-component" onClick={() => handleShowProperties(15)}
                                            draggable="true" onDragStart={handleDragStartCombiner}>
                                            <img src="/resources/image/components/combiner/combiner-symbol.png" alt="Combiner" />
                                            <span > Combiner</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-17" className="grid-component" onClick={() => handleShowProperties(17)}
                                            draggable="true" onDragStart={handleDragStartPanel}>
                                            <img src="/resources/image/components/panel/panel-symbol.png" alt="Panel" />
                                            <span > Panel</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-19" className="grid-component" onClick={() => handleShowProperties(19)}
                                            draggable="true" onDragStart={handleDragStartString}>
                                            <img src="/resources/image/components/string/string-symbol.png" alt="String" />
                                            <span > String</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-21" className="grid-component" onClick={() => handleShowProperties(21)}
                                            draggable="true" onDragStart={handleDragStartWeather}>
                                            <img src="/resources/image/components/weather/weather-symbol.png" alt="Weather" />
                                            <span > Weather</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 2 &&
                                        <div id="component-23" className="grid-component" onClick={() => handleShowProperties(23)}
                                            draggable="true" onDragStart={handleDragStartUps}>
                                            <img src="/resources/image/components/ups/ups.png" alt="Ups" />
                                            <span > Ups</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-24" className="grid-component" onClick={() => handleShowProperties(24)}
                                            draggable="true" onDragStart={handleDragStartKhoang1}>
                                            <img src="/resources/image/components/khoang-new/khoang-do-luong.png" alt="Khoang Đo Lường" />
                                            <span > Khoang Đo Lường</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-25" className="grid-component" onClick={() => handleShowProperties(25)}
                                            draggable="true" onDragStart={handleDragStartKhoang1Symbol}>
                                            <img src="/resources/image/components/khoang-new/khoang-do-luong-icon.png" alt="Khoang Đo Lường Symbol" />
                                            <span > Khoang Đo Lường Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-26" className="grid-component" onClick={() => handleShowProperties(26)}
                                            draggable="true" onDragStart={handleDragStartKhoangCap}>
                                            <img src="/resources/image/components/khoang-new/khoang-cap.png" alt="Khoang Cáp" />
                                            <span > Khoang Cáp</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-27" className="grid-component" onClick={() => handleShowProperties(27)}
                                            draggable="true" onDragStart={handleDragStartKhoangCapSymbol}>
                                            <img src="/resources/image/components/khoang-new/khoang-cap-icon.png" alt="Khoang Cáp Symbol" />
                                            <span > Khoang Cáp Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-28" className="grid-component d-none" onClick={() => handleShowProperties(28)}
                                            draggable="true" onDragStart={handleDragStartKhoangChi}>
                                            <img src="/resources/image/components/khoang/khoang-chi.png" alt="Khoang Chì" />
                                            <span > Khoang Chì</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-29" className="grid-component d-none" onClick={() => handleShowProperties(29)}
                                            draggable="true" onDragStart={handleDragStartKhoangDoDem}>
                                            <img src="/resources/image/components/khoang/khoang-do-dem.png" alt="Khoang Đo Đếm" />
                                            <span > Khoang Đo Đếm</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-30" className="grid-component d-none" onClick={() => handleShowProperties(30)}
                                            draggable="true" onDragStart={handleDragStartKhoangDoDemSymbol}>
                                            <img src="/resources/image/components/khoang/khoang-do-dem-icon.png" alt="Khoang Đo Đếm Symbol" />
                                            <span > Khoang Đo Đếm Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-31-1" className="grid-component" onClick={() => handleShowProperties(31)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCat1}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-1.png" alt="Khoang Máy Cắt 1" />
                                            <span > Khoang Máy Cắt 1</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-31-2" className="grid-component" onClick={() => handleShowProperties(31)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCat2}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-2.png" alt="Khoang Máy Cắt 2" />
                                            <span > Khoang Máy Cắt 2</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-31-3" className="grid-component" onClick={() => handleShowProperties(31)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCat3}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-3.png" alt="Khoang Máy Cắt 3" />
                                            <span > Khoang Máy Cắt 3</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-32-1" className="grid-component" onClick={() => handleShowProperties(32)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCatSymbol1}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-1-icon.png" alt="Khoang Máy Cắt 1 Symbol" />
                                            <span > Khoang Máy Cắt 1 Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-32-2" className="grid-component" onClick={() => handleShowProperties(32)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCatSymbol2}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-2-icon.png" alt="Khoang Máy Cắt 2 Symbol" />
                                            <span > Khoang Máy Cắt 2 Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-32-3" className="grid-component" onClick={() => handleShowProperties(32)}
                                            draggable="true" onDragStart={handleDragStartKhoangMayCatSymbol3}>
                                            <img src="/resources/image/components/khoang-new/khoang-may-cat-3-icon.png" alt="Khoang Máy Cắt 3 Symbol" />
                                            <span > Khoang Máy Cắt 3 Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-33" className="grid-component" onClick={() => handleShowProperties(33)}
                                            draggable="true" onDragStart={handleDragStartKhoangThanhCai}>
                                            <img src="/resources/image/components/khoang-new/khoang-thanh-cai.png" alt="Khoang Thanh Cái" />
                                            <span > Khoang Thanh Cái</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-34" className="grid-component" onClick={() => handleShowProperties(34)}
                                            draggable="true" onDragStart={handleDragStartKhoangThanhCaiSymbol1}>
                                            <img src="/resources/image/components/khoang-new/khoang-thanh-cai-1-icon.png" alt="Khoang Thanh Cái 1 Symbol" />
                                            <span > Khoang Thanh Cái 1 Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-34" className="grid-component" onClick={() => handleShowProperties(34)}
                                            draggable="true" onDragStart={handleDragStartKhoangThanhCaiSymbol2}>
                                            <img src="/resources/image/components/khoang-new/khoang-thanh-cai-2-icon.png" alt="Khoang Thanh Cái 2 Symbol" />
                                            <span > Khoang Thanh Cái 2 Symbol</span>
                                        </div>
                                    }
                                    {
                                        systemTypeId == 5 &&
                                        <div id="component-34" className="grid-component" onClick={() => handleShowProperties(34)}
                                            draggable="true" onDragStart={handleDragStartKhoangThanhCaiSymbol3}>
                                            <img src="/resources/image/components/khoang-new/khoang-thanh-cai-3-icon.png" alt="Khoang Thanh Cái 3 Symbol" />
                                            <span > Khoang Thanh Cái 3 Symbol</span>
                                        </div>
                                    }
                                    <div id="component-53" className="grid-component" onClick={() => handleShowProperties(53)}
                                        draggable="true" onDragStart={handleDragStartPanel2}>
                                        <img src="/resources/image/components/panel/panel.svg" alt="Panel" />
                                        <span > Panel</span>
                                    </div>
                                </div>
                                :
                                <div className="tab-pane active" id="tab-window"
                                    aria-labelledby="baseIcon-tab-window">
                                    <button id="btn-add" className="btn btn-outline-primary mr-1 btn-tool" onClick={handleNew}>
                                        <i className="fa-solid fa-circle-plus"> New</i>
                                    </button>
                                    <button id="btn-switch-map" className="btn btn-outline-primary mr-1 btn-tool" onClick={handleBack}>
                                        <i className="fa-solid fa-circle-check"> Back</i>
                                    </button>
                                    <span className="tool-layer layer-01">
                                        <i className="fa-solid fa-tag"></i>
                                        &nbsp;Layer 1
                                    </span>
                                    {
                                        systemMaps?.map(
                                            (systemMap, index) =>
                                                systemMap.layer == 1 &&
                                                <div key={index}>
                                                    {
                                                        systemMapId == systemMap.id ?
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}>{systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                            :
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ECECEC' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}>{systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                    }

                                                    {
                                                        deviceAlready?.map(
                                                            (meterItem, index) =>
                                                                (meterItem.systemMapId == systemMap.id) &&
                                                                <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={index}>
                                                                    <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                        <i className="fas fa-server"></i>
                                                                        &nbsp;{meterItem.deviceName.length > 29 ? meterItem.deviceName.substring(0, 27) + "..." : meterItem.deviceName}
                                                                    </span>
                                                                </Link>
                                                        )
                                                    }
                                                </div>
                                        )
                                    }
                                    <span className="tool-layer layer-02">
                                        <i className="fa-solid fa-tag"></i>
                                        &nbsp;Layer 2
                                    </span>
                                    {
                                        systemMaps?.map(
                                            (systemMap, index) =>
                                                systemMap.layer == 2 &&
                                                <div key={index}>
                                                    {
                                                        systemMapId == systemMap.id ?
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}>{systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name} </span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                            :
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ECECEC' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                    }

                                                    {
                                                        deviceAlready?.map(
                                                            (meterItem, index) =>
                                                                meterItem.systemMapId == systemMap.id &&
                                                                <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={index}>
                                                                    <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                        <i className="fas fa-server"></i>
                                                                        &nbsp;{meterItem.deviceName.length > 29 ? meterItem.deviceName.substring(0, 27) + "..." : meterItem.deviceName}
                                                                    </span>
                                                                </Link>
                                                        )
                                                    }
                                                </div>
                                        )
                                    }
                                    <span className="tool-layer layer-03">
                                        <i className="fa-solid fa-tag"></i>
                                        &nbsp;Layer 3
                                    </span>
                                    {
                                        systemMaps?.map(
                                            (systemMap, index) =>
                                                systemMap.layer == 3 &&
                                                <div key={index}>
                                                    {
                                                        systemMapId == systemMap.id ?
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                            :
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ECECEC' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                    }

                                                    {
                                                        deviceAlready?.map(
                                                            (meterItem, index) =>
                                                                meterItem.systemMapId == systemMap.id &&
                                                                <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={index}>
                                                                    <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                        <i className="fas fa-server"></i>
                                                                        &nbsp;{meterItem.deviceName.length > 29 ? meterItem.deviceName.substring(0, 27) + "..." : meterItem.deviceName}
                                                                    </span>
                                                                </Link>
                                                        )
                                                    }
                                                </div>
                                        )
                                    }
                                    <span className="tool-layer layer-04">
                                        <i className="fa-solid fa-tag"></i>
                                        &nbsp;Layer 4
                                    </span>
                                    {
                                        systemMaps?.map(
                                            (systemMap, index) =>
                                                systemMap.layer == 4 &&
                                                <div key={index}>
                                                    {
                                                        systemMapId == systemMap.id ?
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                            :
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ECECEC' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                    }

                                                    {
                                                        deviceAlready?.map(
                                                            (meterItem, index) =>
                                                                meterItem.systemMapId == systemMap.id &&
                                                                <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={index}>
                                                                    <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                        <i className="fas fa-server"></i>
                                                                        &nbsp;{meterItem.deviceName.length > 29 ? meterItem.deviceName.substring(0, 27) + "..." : meterItem.deviceName}
                                                                    </span>
                                                                </Link>
                                                        )
                                                    }
                                                </div>
                                        )
                                    }
                                    <span className="tool-layer layer-05">
                                        <i className="fa-solid fa-tag"></i>
                                        &nbsp;Layer 5

                                    </span>
                                    {
                                        systemMaps?.map(
                                            (systemMap, index) =>
                                                systemMap.layer == 5 &&
                                                <div key={index}>
                                                    {
                                                        systemMapId == systemMap.id ?
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}> {systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                            :
                                                            <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}`} style={{ display: 'block', color: '#495057', background: '#ECECEC' }} className="grid-component"><span>
                                                                <i className="fa-regular fa-map"></i>
                                                                <span id={`system-map-name-${systemMap.id}`}>{systemMap.name.length > 21 ? systemMap.name.substring(0, 19) + "..." : systemMap.name}</span>
                                                            </span>
                                                                <i className="fa-solid fa-trash-can grid-icon" onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    handleDeleteSystemMap(systemMap.id);
                                                                }}></i>
                                                                <i onClick={(e) => {
                                                                    e.stopPropagation();
                                                                    e.preventDefault();
                                                                    history.push(`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/2/systemMap/${systemMap.id}`)
                                                                }} className="fa-solid fa-pen grid-icon" style={{ marginRight: 3 }}></i>
                                                            </Link>
                                                    }

                                                    {
                                                        deviceAlready?.map(
                                                            (meterItem, index) =>
                                                                meterItem.systemMapId == systemMap.id &&
                                                                <Link to={`/category/tool-page/egrid-page/${customerId}/${projectId}/${systemTypeId}/1/systemMap/${systemMap.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={index}>
                                                                    <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                        <i className="fas fa-server"></i>
                                                                        &nbsp;{meterItem.deviceName.length > 29 ? meterItem.deviceName.substring(0, 27) + "..." : meterItem.deviceName}
                                                                    </span>
                                                                </Link>
                                                        )
                                                    }
                                                </div>
                                        )
                                    }
                                </div>
                        }
                    </div>
                </div>

                <div id="grid-canvas" onDrop={handleOnDrop} onDragOver={handleOnDragOver}></div>

                <div id="grid-properties">
                    <span className="title">Properties</span>

                    <div id="grid-table">
                        <table className="tbl component-properties" id="window-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Sơ đồ</td>
                                    <td className="tool-td">
                                        <input id="systemMapName" type="text"
                                            className="tool-input" onChange={e => setSystemMapName(e.target.value)} value={systemMapName} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Layer</td>
                                    <td className="tool-td">
                                        <select id="layer" className="custom-select block" value={layer} disabled>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="arrow-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td">Type</td>
                                    <td className="tool-td">
                                        <input type="radio" name="arrow_type" id="arrow-type-01" value="1" onClick={() => handleChangeUpdate()} /> Thường &nbsp;
                                        <input type="radio" name="arrow_type" id="arrow-type-02" value="2" onClick={() => handleChangeUpdate()} defaultChecked /> 3 pha &nbsp;
                                        <input type="radio" name="arrow_type" id="arrow-type-03" value="3" onClick={() => handleChangeUpdate()} /> Mũi tên 
                                        <input style={{ display: 'none' }} type="radio" name="arrow_type" id="arrow-type-03" value="3" />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td">Position</td>
                                    <td className="tool-td">
                                        <div className="switch-field">
                                            <input type="radio" name="arrow_position" id="arrow-position-1" value="1" defaultChecked /><label id="arrow-position-1" htmlFor="arrow-position-1" onClick={() => handleChangeUpdate()}></label>
                                            <input type="radio" name="arrow_position" id="arrow-position-2" value="2" /><label id="arrow-position-2" htmlFor="arrow-position-2" onClick={() => handleChangeUpdate()}></label>
                                            <input type="radio" name="arrow_position" id="arrow-position-3" value="3" /><label id="arrow-position-3" htmlFor="arrow-position-3" onClick={() => handleChangeUpdate()}></label>
                                            <input type="radio" name="arrow_position" id="arrow-position-4" value="4" /><label id="arrow-position-4" htmlFor="arrow-position-4" onClick={() => handleChangeUpdate()}></label>
                                        </div>

                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="meter-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td id="load-device-type-1" className="tool-td">
                                        <select id="meter-device-id-1" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                metersEmpty?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-3" className="tool-td">
                                        <select id="meter-device-id-3" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType3?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-4" className="tool-td">
                                        <select id="meter-device-id-4" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType4?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-5" className="tool-td">
                                        <select id="meter-device-id-5" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType5?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-6" className="tool-td">
                                        <select id="meter-device-id-6" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType6?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-7" className="tool-td">
                                        <select id="meter-device-id-7" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType7?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-8" className="tool-td">
                                        <select id="meter-device-id-8" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType8?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-9" className="tool-td">
                                        <select id="meter-device-id-9" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType9?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-10" className="tool-td">
                                        <select id="meter-device-id-10" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType10?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-11" className="tool-td">
                                        <select id="meter-device-id-11" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType11?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-12" className="tool-td">
                                        <select id="meter-device-id-12" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType12?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-13" className="tool-td">
                                        <select id="meter-device-id-13" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType13?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-14" className="tool-td">
                                        <select id="meter-device-id-14" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType14?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-15" className="tool-td">
                                        <select id="meter-device-id-15" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType15?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                    <td id="load-device-type-19" className="tool-td">
                                        <select id="meter-device-id-19" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType19?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="device_caculator" id="caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="device_caculator" id="caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>

                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="stmv-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="stmv-tag-name" type="text"
                                            className="tool-input" />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="stmv-description" type="text" className="tool-input" />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="stmv-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="stmv_caculator" id="stmv-caculator-1" value="1" /> Có &nbsp;
                                        <input type="radio" name="stmv_caculator" id="stmv-caculator-0" value="0" defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table className="tbl component-properties" id="sgmv-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="sgmv-tag-name" type="text"
                                            className="tool-input" />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="sgmv-description" type="text" className="tool-input" />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="sgmv-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="sgmv_caculator" id="sgmv-caculator-1" value="1" /> Có &nbsp;
                                        <input type="radio" name="sgmv_caculator" id="sgmv-caculator-0" value="0" defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="mba-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="mba-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="mba-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="rmu-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="rmu-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="rmu-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="feeder-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td">Position</td>
                                    <td className="tool-td">
                                        <input type="radio" name="feeder_position" className="align-middle" id="feeder-up" value="1" />&nbsp; <label className="form-check-label align-middle" htmlFor="feeder-up" onClick={() => handleChangeUpdate()}>Up</label> &nbsp;
                                        <input type="radio" name="feeder_position" className="align-middle" id="feeder-down" value="2" defaultChecked />&nbsp; <label className="form-check-label align-middle" htmlFor="feeder-down" onClick={() => handleChangeUpdate()}>Down</label> &nbsp;
                                        <input type="radio" name="feeder_position" className="align-middle" id="feeder-left" value="3" />&nbsp; <label className="form-check-label align-middle" htmlFor="feeder-right" onClick={() => handleChangeUpdate()}>Left</label> &nbsp;
                                        <input type="radio" name="feeder_position" className="align-middle" id="feeder-right" value="4" />&nbsp; <label className="form-check-label align-middle" htmlFor="feeder-left" onClick={() => handleChangeUpdate()}>Right</label>
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Link ID</td>
                                    <td className="tool-td">
                                        <select id="feeder-link-id" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                systemMaps?.map(
                                                    (systemMap, index) =>
                                                        <option value={systemMap.id} key={index}>{systemMap.name}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="mc-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td">Position</td>
                                    <td className="tool-td">
                                        <input type="radio" name="mccb_position" className="align-middle" id="mccb-horizontal" value="1" onClick={() => handleChangeUpdate()} />
                                        <label className="form-check-label align-middle"
                                            htmlFor="mccb-horizontal">Horizontal</label> &nbsp; <input
                                            type="radio" name="mccb_position" className="align-middle"
                                            id="mccb-vertical" value="2" onClick={() => handleChangeUpdate()} defaultChecked /> <label
                                                className="form-check-label align-middle" htmlFor="mccb-vertical">Vertical</label>
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td">Type</td>
                                    <td className="tool-td">
                                        <div className="switch-field">
                                            <input type="radio" name="mccb_type" id="mccb-type-01" value="1" onClick={() => handleChangeUpdate()}
                                            /> <label htmlFor="mccb-type-01"
                                                id="lbl-mccb-type-01"></label> <input type="radio"
                                                    name="mccb_type" id="mccb-type-02" value="2" onClick={() => handleChangeUpdate()} /> <label
                                                        htmlFor="mccb-type-02" id="lbl-mccb-type-02"></label> <input
                                                type="radio" name="mccb_type" id="mccb-type-03" value="3" onClick={() => handleChangeUpdate()} defaultChecked />
                                            <label htmlFor="mccb-type-03" id="lbl-mccb-type-03"></label>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="label-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Type</td>
                                    <td className="tool-td">
                                        <input type="radio" name="label_type" id="label-type-01" value="1" onClick={() => handleChangeUpdate()} defaultChecked /> Dọc &nbsp;
                                        <input type="radio" name="label_type" id="label-type-02" value="2" onClick={() => handleChangeUpdate()} /> &nbsp;Ngang
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label2-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label2-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label3-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label3-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label4-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label4-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label5-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label5-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label6-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label6-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label7-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label7-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="label8-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="label8-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="ultility-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="ultility-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="ultility-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="busbar-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Jointer No</td>
                                    <td className="tool-td">
                                        <input id="jointer-qty" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Type</td>
                                    <td className="tool-td">
                                        <input type="radio" name="busbar_position" id="busbar-horizontal" value="1" onClick={() => handleChangeUpdate()} defaultChecked /> Ngang &nbsp;
                                        <input type="radio" name="busbar_position" id="busbar-vertical" value="2" onClick={() => handleChangeUpdate()} /> &nbsp;Dọc
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="inverter-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="inverter-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="inverter-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td className="tool-td">
                                        <select id="inverterIds" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                invertersEmpty?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="inverter_caculator" id="inverter-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="inverter_caculator" id="inverter-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="inverter-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="inverter-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="inverter-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="combiner-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="combiner-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="combiner-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td className="tool-td">
                                        <select id="combinerIds" className="custom-select block" onClick={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                combinersEmpty?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="combiner_caculator" id="combiner-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="combiner_caculator" id="combiner-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="combiner-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="combiner-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="combiner-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="panel-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="panel-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="panel-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td className="tool-td">
                                        <select id="panelIds" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                panelsEmpty?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="panel_caculator" id="panel-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="panel_caculator" id="panel-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="panel-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="panel-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="panel-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="string-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="string-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="string-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td className="tool-td">
                                        <select id="stringIds" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                stringsEmpty?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="string_caculator" id="string-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="string_caculator" id="string-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="string-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="string-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="string-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="weather-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="weather-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="weather-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="weather-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="weather_caculator" id="weather-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="weather_caculator" id="weather-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="weather-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="weather-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="weather-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="ups-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="ups-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="ups-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang1-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang1-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang1-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="khoang1-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="khoang1_caculator" id="khoang1-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="khoang1_caculator" id="khoang1-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang1-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang1-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang1-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-cap-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-cap-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-cap-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="khoang-cap-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="khoang_cap_caculator" id="khoang-cap-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="khoang_cap_caculator" id="khoang-cap-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-cap-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-cap-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-cap-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-chi-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-chi-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-chi-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-do-dem-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-do-dem-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-do-dem-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="khoang-do-dem-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="khoang_do_dem_caculator" id="khoang-do-dem-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="khoang_do_dem_caculator" id="khoang-do-dem-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-do-dem-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-do-dem-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-do-dem-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-may-cat-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-may-cat-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-may-cat-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="khoang-may-cat-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="khoang_may_cat_caculator" id="khoang-may-cat-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="khoang_may_cat_caculator" id="khoang-may-cat-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-may-cat-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-may-cat-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-may-cat-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-thanh-cai-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-thanh-cai-tag-name" type="text"
                                            className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-thanh-cai-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr>
                                    <td className="tool-td" width="80px">Tên thiết bị</td>
                                    <td className="tool-td">
                                        <input id="khoang-thanh-cai-device-name" type="text" className="tool-input" disabled />
                                    </td>
                                </tr>
                                <tr className={`${systemMapCurrent.layer != 1 && "d-none"}`}>
                                    <td className="tool-td" width="80px">Tính công suất</td>
                                    <td className="tool-td">
                                        <input type="radio" name="khoang_thanh_cai_caculator" id="khoang-thanh-cai-caculator-1" value="1" onClick={() => handleChangeUpdate()} /> Có &nbsp;
                                        <input type="radio" name="khoang_thanh_cai_caculator" id="khoang-thanh-cai-caculator-0" value="0" onClick={() => handleChangeUpdate()} defaultChecked /> &nbsp;Không
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="khoang-thanh-cai-symbol-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="khoang-thanh-cai-symbol-device-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="khoang-thanh-cai-symbol-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table className="tbl component-properties" id="text-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Tiêu đề</td>
                                    <td className="tool-td">
                                        <input id="text-name" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                                <tr className="d-none">
                                    <td className="tool-td" width="80px">Mô tả</td>
                                    <td className="tool-td">
                                        <input id="text-description" type="text" className="tool-input" onChange={() => handleChangeUpdate()} />
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table className="tbl component-properties" id="htr02-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td id="load-device-type-1" className="tool-td">
                                        <select id="htr02-device" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType5?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <table className="tbl component-properties" id="ams01-properties" style={{ display: 'none' }}>
                            <tbody>
                                <tr>
                                    <td className="tool-td" width="80px">Thiết bị</td>
                                    <td id="load-device-type-1" className="tool-td">
                                        <select id="ams01-device" className="custom-select block" onChange={() => handleChangeUpdate()}>
                                            <option value=""></option>
                                            {
                                                deviceType6?.map(
                                                    (device, index) =>
                                                        <option value={device.deviceId} key={index}>{device.deviceName}</option>
                                                )
                                            }
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    {/* <div id="grid-action" className="text-right">
                        <button type="submit" id="btn-update"
                            className="btn btn-outline-warning mr-1" onClick={handleUpdate}>
                            <i className="fas fa-sync"></i> Update
                        </button>
                        <button type="submit" id="btn-remove"
                            className="btn btn-outline-warning mr-1" onClick={handleRemove}>
                            <i className="far fa-trash-alt"></i> Remove
                        </button>
                    </div> */}
                </div>
            </div >
        </div >

    )
}

export default Tool;