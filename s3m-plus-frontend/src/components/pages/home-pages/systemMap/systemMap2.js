import { useEffect, useState } from 'react';
import * as d3 from "d3";
import { DATA } from "../../category/customer-tool/tool-page/data";
import { Link, useHistory, useLocation, useParams } from 'react-router-dom';
import SystemMapService from '../../../../services/SystemMapService';
import ARROW from '../../category/customer-tool/tool-page/arrow';
import ARROW2 from '../../category/customer-tool/tool-page/arrow2';
import MBA from '../../category/customer-tool/tool-page/mba';
import RMU from '../../category/customer-tool/tool-page/rmu';
import MC from '../../category/customer-tool/tool-page/mc';
import MCHOR from '../../category/customer-tool/tool-page/mchor';
import LABEL from '../../category/customer-tool/tool-page/label';
import LABEL2 from '../../category/customer-tool/tool-page/label2';
import ULTILITY from '../../category/customer-tool/tool-page/ultility';
import FEEDER from '../../category/customer-tool/tool-page/feeder';
import BUSBAR from '../../category/customer-tool/tool-page/busbar';
import BUSBARVER from '../../category/customer-tool/tool-page/busbarver';
import STMV from '../../category/customer-tool/tool-page/stmv';
import SGMV from '../../category/customer-tool/tool-page/sgmv';
import { createInstance } from '../../category/customer-tool/tool-page/data';
import CONSTANTS from '../../category/customer-tool/tool-page/constant';
import INVERTER from '../../category/customer-tool/tool-page/inverter';
import COMBINER from '../../category/customer-tool/tool-page/combiner';
import PANEL from '../../category/customer-tool/tool-page/panel';
import PANEL2 from '../../category/customer-tool/tool-page/panel2';
import STRING from '../../category/customer-tool/tool-page/string';
import WEATHER from '../../category/customer-tool/tool-page/weather';
import INVERTERSYMBOL from '../../category/customer-tool/tool-page/invertersymbol';
import COMBINERSYMBOL from '../../category/customer-tool/tool-page/combinersymbol';
import PANELSYMBOL from '../../category/customer-tool/tool-page/panelsymbol';
import STRINGSYMBOL from '../../category/customer-tool/tool-page/stringsymbol';
import WEATHERSYMBOL from '../../category/customer-tool/tool-page/weathersymbol';
import UPS from '../../category/customer-tool/tool-page/ups';
import "./index.css";
import METER_LOAD from '../../home/load/systemMap/meter';
import TEXT from '../../category/customer-tool/tool-page/text';
import KHOANG1 from '../../category/customer-tool/tool-page/khoang1';
import KHOANG1SYMBOL from '../../category/customer-tool/tool-page/khoang1symbol';
import KHOANGCAP from '../../category/customer-tool/tool-page/khoangcap';
import KHOANGCAPSYMBOL from '../../category/customer-tool/tool-page/khoangcapsymbol';
import KHOANGCHI from '../../category/customer-tool/tool-page/khoangchi';
import KHOANGDODEM from '../../category/customer-tool/tool-page/khoangdodem';
import KHOANGDODEMSYMBOL from '../../category/customer-tool/tool-page/khoangdodemsymbol';
import KHOANGMAYCAT from '../../category/customer-tool/tool-page/khoangmaycat';
import KHOANGMAYCATSYMBOL from '../../category/customer-tool/tool-page/khoangmaycatsymbol';
import KHOANGTHANHCAI from '../../category/customer-tool/tool-page/khoangthanhcai';
import KHOANGTHANHCAISYMBOL from '../../category/customer-tool/tool-page/khoangthanhcaisymbol';
import converter from '../../../../common/converter';
import PD_HTR02 from '../../category/customer-tool/tool-page/pd-htr02';
import PD_AMS01 from '../../category/customer-tool/tool-page/pd-ams01';
import IMAGE from '../../category/customer-tool/tool-page/image';
import { t } from 'i18next';

const $ = window.$;

var deviceIdCurrent = 0;
let viewTypeModal;

const SystemMapComponent2 = ({ projectInfo }) => {
    const location = useLocation();
    const history = useHistory();

    const { customerId, type, projectId, systemMapId, projectSearchId } = useParams();
    const [deviceAlready, setDevicesAlready] = useState([]);
    const [systemTypeId, setSystemTypeId] = useState(type);
    const [layerTree, setlayerTree] = useState([]);
    const [nextId, setNextWindowId] = useState(0);
    const [beforeId, setBeforeWindowId] = useState(0);
    const [systemMapCurrent, setSystemMapCurrent] = useState(0);
    let [deviceCurrent, setDeviceCurrent] = useState(0);
    const [breadscrum, setBreadscrum] = useState("");
    const [deviceTime, setDeviceTime] = useState("");
    const [urlSearch, setUrlSearch] = useState("");
    var listSystemMap = [];

    var listId = "";
    var count = 0;
    var ajaxDataInstance = [];
    var systemMapTree = [];
    const [infor, setInfor] = useState();

    var ajaxGetListData = null;
    var ajaxGetInstanceData = null;

    const getSystemMapList = async () => {
        $("#svg-container").hide();
        setNextWindowId(0);
        setBeforeWindowId(0);

        let systemMapsResponse = await SystemMapService.getSystemMapByProjectIdAndSystemTypeId(projectId, type);
        listSystemMap = systemMapsResponse.data;

        if (systemMapsResponse.data.length > 0) {
            for (var i = 0; i < systemMapsResponse.data.length; i++) {
                var tempLayer = {
                    id: systemMapsResponse.data[i].layer,
                    systemMap: [systemMapsResponse.data[i]],
                }

                var layerIndex = systemMapTree.findIndex(x => x.id === tempLayer.id);
                if (layerIndex >= 0) {
                    systemMapTree[layerIndex].systemMap.push(systemMapsResponse.data[i]);
                } else {
                    systemMapTree.push(tempLayer);
                }
            }
        }

        setlayerTree(systemMapTree);

        if (systemMapId) {
            let systemMapResponse = await SystemMapService.getSystemMap(systemMapId);
            loadDataFromJson(JSON.parse(systemMapResponse.data.jsonData));

            if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) < systemMapTree.length - 1) {
                setNextWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) + 1].systemMap[0].id);
            }

            if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) > 0) {
                setBeforeWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) - 1].systemMap[0].id);
            }

            setSystemMapCurrent(systemMapId);

            let breadscrumCurrent = "Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;

            if (listSystemMap.length > 0) {
                for (let i = 0; i < listSystemMap.length; i++) {
                    if (listSystemMap[i].jsonData != null && listSystemMap[i].jsonData != "") {
                        let systemMapData = JSON.parse(listSystemMap[i].jsonData);
                        for (let j = 0; j < systemMapData.aCards.length; j++) {
                            let obj = systemMapData.aCards[j];
                            if (obj.type == "feeder" && obj.linkId == systemMapResponse.data.id) {
                                breadscrumCurrent = "Layer " + listSystemMap[i].layer + "/ " + listSystemMap[i].name + " -> Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;
                                break;
                            }
                        }
                    }
                }
            }

            setBreadscrum(breadscrumCurrent);

            let systemMapJsonData = JSON.parse(systemMapResponse.data.jsonData);
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
                if (systemInfoTime.data.length > 0) {
                    setDeviceTime(systemInfoTime.data);
                } else {
                    setDeviceTime((t('content.no_data')));
                }
            } else {
                setDeviceTime("-");
            }

        } else {
            if (systemMapsResponse.data.length > 0) {

                let systemMapResponse = await SystemMapService.getSystemMap(systemMapsResponse.data[0].id);
                loadDataFromJson(JSON.parse(systemMapResponse.data.jsonData));

                if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) < systemMapTree.length - 1) {
                    setNextWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) + 1].systemMap[0].id);
                }

                if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) > 0) {
                    setBeforeWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) - 1].systemMap[0].id);
                }

                setSystemMapCurrent(systemMapsResponse.data[0].id);

                let breadscrumCurrent = "Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;

                if (listSystemMap.length > 0) {
                    for (let i = 0; i < listSystemMap.length; i++) {
                        if (listSystemMap[i].jsonData != null && listSystemMap[i].jsonData != "") {
                            let systemMapData = JSON.parse(listSystemMap[i].jsonData);
                            for (let j = 0; j < systemMapData.aCards.length; j++) {
                                let obj = systemMapData.aCards[j];
                                if (obj.type == "feeder" && obj.linkId == systemMapsResponse.data[0].id) {
                                    breadscrumCurrent = "Layer " + listSystemMap[i].layer + "/ " + listSystemMap[i].name + " -> Layer " + systemMapsResponse.data[0].layer + "/ " + systemMapsResponse.data[0].name;
                                    break;
                                }
                            }
                        }
                    }
                }

                setBreadscrum(breadscrumCurrent);

                let systemMapJsonData = JSON.parse(systemMapResponse.data.jsonData);
                let deviceJsonList = "";

                if (systemMapJsonData != null && systemMapJsonData != "") {
                    for (let i = 0; i < systemMapJsonData.aCards.length; i++) {
                        let obj = systemMapJsonData.aCards[i];
                        if (obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv"
                            || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather"
                            || obj.type == "khoang1" || obj.type == "khoangcap" || obj.type == "khoangdodem" || obj.type == "khoangmaycat" || obj.type == "khoangthanhcai") {
                            deviceJsonList = deviceJsonList + obj.deviceId + ",";
                        }
                    }
                }

                if (deviceJsonList !== "") {
                    deviceJsonList = deviceJsonList.slice(0, -1);
                    let systemInfoTime = await SystemMapService.getSystemInfoTime(deviceJsonList, customerId);
                    if (systemInfoTime.data.length > 0) {
                        setDeviceTime(systemInfoTime.data);
                    } else {
                        setDeviceTime((t('content.no_data')));
                    }
                } else {
                    setDeviceTime("-");
                }
            }
        }

        let devicesAlreadyResponse = await SystemMapService.getDevicesAlready(projectId, systemTypeId);

        if (devicesAlreadyResponse.data.length > 0) {
            setDevicesAlready(devicesAlreadyResponse.data);
        };

        setDeviceCurrent(0);

        if (new URLSearchParams(location.search).get("deviceId")) {
            focusDevice(new URLSearchParams(location.search).get("deviceId"));
        };

        $("#svg-container").show();


        if (typeof projectSearchId !== "undefined") {
            setUrlSearch(projectSearchId + "/system-map");
        } else {
            setUrlSearch("system-map");
        }
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
            var deviceRect = null;

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
                deviceWidth = $("#" + PD_HTR02.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PD_HTR02.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PD_HTR02.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "pd-ams01") {
                deviceWidth = $("#" + PD_AMS01.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PD_AMS01.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PD_AMS01.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            }

            var xCenter = (canvasWidth / 2) - deviceX - deviceWidth / 2;
            var yCenter = (canvasHeight / 2) - deviceY - deviceHeight / 2;

            var g_container = d3.select("#svg-container");
            g_container.attr('transform', "translate(" + xCenter + "," + yCenter + ")");
            deviceRect.attr('style', "stroke-width:1;stroke:rgb(167, 201, 66)");
        }

    };

    function loadDataFromJson(dataJson) {
        DATA.aCards = [];
        DATA.aArrows = [];

        clearInterval(ajaxGetListData);
        clearInterval(ajaxGetInstanceData);

        if (dataJson != null && dataJson != "") {
            for (var i = 0; i < dataJson.aArrows.length; i++) {
                var arrow = dataJson.aArrows[i];
                DATA.aArrows.push(arrow);
                if (arrow.componentType == "arrow") {
                    ARROW2.drawLoad(arrow);
                } else {
                    ARROW.drawLoad(arrow);
                }
            };

            for (var i = 0; i < dataJson.aCards.length; i++) {
                let obj = dataJson.aCards[i];
                DATA.aCards.push(obj);

                switch (obj.type) {
                    case "meter":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        METER_LOAD.drawLoad(obj, viewTypeModal);
                        break;
                    case "mba":
                        MBA.drawLoad(obj);
                        break;
                    case "rmu":
                        RMU.drawLoad(obj);
                        break;
                    case "feeder":
                        FEEDER.drawLoad(obj);
                        break;
                    case "mc":
                        MC.drawLoad(obj);
                        break;
                    case "mchor":
                        MCHOR.drawLoad(obj);
                        break;
                    case "label":
                        LABEL.drawLoad(obj);
                        break;
                    case "label2":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label3":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label4":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label5":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label6":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label7":
                        LABEL2.drawLoad(obj);
                        break;
                    case "label8":
                        LABEL2.drawLoad(obj);
                        break;
                    case "ultility":
                        ULTILITY.drawLoad(obj);
                        break;
                    case "busbar":
                        BUSBAR.drawLoad(obj);
                        break;
                    case "busbarver":
                        BUSBARVER.drawLoad(obj);
                        break;
                    case "stmv":
                        STMV.drawLoad(obj);
                        break;
                    case "sgmv":
                        SGMV.drawLoad(obj);
                        break;
                    case "inverter":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        INVERTER.drawLoad(obj);
                        break;
                    case "invertersymbol":
                        INVERTERSYMBOL.drawLoad(obj);
                        break;
                    case "combiner":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        COMBINER.drawLoad(obj);
                        break;
                    case "combinersymbol":
                        COMBINERSYMBOL.drawLoad(obj);
                        break;
                    case "panel":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        PANEL.drawLoad(obj);
                        break;
                    case "panelsymbol":
                        PANELSYMBOL.drawLoad(obj);
                        break;
                    case "string":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        STRING.drawLoad(obj);
                        break;
                    case "stringsymbol":
                        STRINGSYMBOL.drawLoad(obj);
                        break;
                    case "weather":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        WEATHER.drawLoad(obj);
                        break;
                    case "weathersymbol":
                        WEATHERSYMBOL.drawLoad(obj);
                        break;
                    case "ups":
                        UPS.drawLoad(obj);
                        break;
                    case "khoang1":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        KHOANG1.drawLoad(obj);
                        break;
                    case "khoang1symbol":
                        KHOANG1SYMBOL.drawLoad(obj);
                        break;
                    case "khoangcap":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        KHOANGCAP.drawLoad(obj);
                        break;
                    case "khoangcapsymbol":
                        KHOANGCAPSYMBOL.drawLoad(obj);
                        break;
                    case "khoangchi":
                        KHOANGCHI.drawLoad(obj);
                        break;
                    case "khoangdodem":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        KHOANGDODEM.drawLoad(obj);
                        break;
                    case "khoangdodemsymbol":
                        KHOANGDODEMSYMBOL.drawLoad(obj);
                        break;
                    case "khoangmaycat":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        KHOANGMAYCAT.drawLoad(obj);
                        break;
                    case "khoangmaycatsymbol":
                        KHOANGMAYCATSYMBOL.drawLoad(obj);
                        break;
                    case "khoangthanhcai":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        KHOANGTHANHCAI.drawLoad(obj);
                        break;
                    case "khoangthanhcaisymbol":
                        KHOANGTHANHCAISYMBOL.drawLoad(obj);
                        break;
                    case "text":
                        TEXT.drawLoad(obj);
                        break;
                    case "panel2":
                        PANEL2.drawLoad(obj);
                        break;
                    case "pd-htr02":
                        PD_HTR02.drawLoad(obj);
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        break;
                    case "pd-ams01":
                        PD_AMS01.drawLoad(obj);
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        break;
                    case "sys-image":
                        IMAGE.drawLoad(obj);
                    default:
                        break;
                };
            };

            listId = listId.slice(0, -1);

            if (listId.length > 0) {
                ajaxDataJson();
                getDataInstance();
                dataTimer();
                getInstanceTimer();
            };
        }
    };

    let ajaxDataJson = async () => {
        if (listId !== "") {
            let ajaxDataTimerResponse = await SystemMapService.getDataJson(listId, customerId);
            ajaxDataInstance = ajaxDataTimerResponse.data;
        } else {
            clearInterval(ajaxGetListData);
        }
    };

    function dataTimer() {
        ajaxGetListData = setInterval(ajaxDataJson, 5000);
    };

    function getInstanceTimer() {
        ajaxGetInstanceData = setInterval(getDataInstance, 1000);
    };

    function getDataInstance() {
        if (ajaxDataInstance != null && ajaxDataInstance.length > 0) {
            var listData = ajaxDataInstance[count].data.split(" ");
            if (listData != "") {
                for (var i = 0; i < listData.length; i++) {
                    var deviceId = listData[i].split(":")[0];
                    var uData = listData[i].split(":")[1].split("*")[0];
                    var iData = listData[i].split(":")[1].split("*")[1];
                    var pfData = listData[i].split(":")[1].split("*")[2];
                    var tData = listData[i].split(":")[1].split("*")[4];
                    var direction = listData[i].split(":")[1].split("*")[5];
                    var status = listData[i].split(":")[1].split("*")[6];
                    var deviceType = listData[i].split(":")[1].split("*")[7];
                    var uDataInverter = listData[i].split(":")[1].split("*")[8];
                    var iDataInverter = listData[i].split(":")[1].split("*")[9];
                    var wData = listData[i].split(":")[1].split("*")[10];
                    var tDataRmu = listData[i].split(":")[1].split("*")[11];
                    var hDataRmu = listData[i].split(":")[1].split("*")[12];
                    var indicator = listData[i].split(":")[1].split("*")[13];
                    var uDataInverter = listData[i].split(":")[1].split("*")[14];
                    var vdcCombiner = listData[i].split(":")[1].split("*")[16];
                    var idcCombiner = listData[i].split(":")[1].split("*")[17];
                    var vdcStr = listData[i].split(":")[1].split("*")[18];
                    var idcStr = listData[i].split(":")[1].split("*")[19];
                    var uPanel = listData[i].split(":")[1].split("*")[20];
                    var iPanel = listData[i].split(":")[1].split("*")[21];
                    var temp = listData[i].split(":")[1].split("*")[22];
                    var h = listData[i].split(":")[1].split("*")[23];
                    var rad = listData[i].split(":")[1].split("*")[24];
                    var pdcCombiner = listData[i].split(":")[1].split("*")[25];
                    var inDCPR = listData[i].split(":")[1].split("*")[26];
                    var p = listData[i].split(":")[1].split("*")[27];
                    var fs = listData[i].split(":")[1].split("*")[28];


                    if (deviceType == 5) {
                        var dataIndicator = d3.select("text[id=" + PD_HTR02.HTR02_INDICATOR_TEXT + deviceId + "]");
                        if (indicator != "null") {
                            dataIndicator.text(indicator);
                        }

                        var dataStatusImg = d3.select("rect[id=" + PD_HTR02.ID_STATUS + deviceId + "]");
                        if (Number(status) == 0) {

                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {

                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                    } else if (deviceType == 6) {
                        var dataIndicator = d3.select("text[id=" + PD_AMS01.INDICATOR_TEXT + deviceId + "]");
                        if (indicator != "null") {
                            dataIndicator.text(indicator);
                        }

                        var dataStatusImg = d3.select("rect[id=" + PD_AMS01.ID_STATUS + deviceId + "]");
                        if (Number(status) == 0) {

                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {

                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                    }
                     // thiết bị áp suất
                     else if (deviceType == 7) {
                        var dataStatusImg = d3.select("rect[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        var dataP = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                        if (p != "null") {
                            dataP.text(p + " bar");
                        }

                        if (Number(status) == 0) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                    }
                    // cảm biến lưu lượng
                    else if (deviceType == 10) {
                        var dataFs = d3.select("text[id=" + METER_LOAD.ID_METER_FS_VALUE + deviceId + "]");
                        var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                        var dataStatusImg = d3.select("rect[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        if (fs != "null") {
                            dataFs.text(fs + " m³/s");
                        }

                        if (tDataRmu != "null") {
                            dataT.text(tDataRmu + " m³");
                        }

                        if (Number(status) == 0) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                    }
                    //cảm biến nhiệt độ - độ ẩm
                    else if (deviceType == 3) {
                        var dataTemperature = d3.select("text[id=" + METER_LOAD.ID_METER_TEMP_VALUE + deviceId + "]");
                        var dataHumidity = d3.select("text[id=" + METER_LOAD.ID_METER_HUMIDITY_VALUE + deviceId + "]");
                        var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        var dataStatusImg = d3.select("rect[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        if (tDataRmu != "null") {
                            dataTemperature.text(tDataRmu + " °C");
                        }

                        if (hDataRmu != "null") {
                            dataHumidity.text(hDataRmu + " %");
                        }
                        if (Number(status) == 0) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                    }



                    if (deviceType == 1 || deviceType == 9) {
                        var dataU = d3.select("text[id=" + METER_LOAD.ID_METER_U_VALUE + deviceId + "]");
                        var dataI = d3.select("text[id=" + METER_LOAD.ID_METER_I_VALUE + deviceId + "]");
                        var dataPF = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                        var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                        var dataDirection = d3.select("path[id=" + METER_LOAD.ID_PATH_METER + deviceId + "]");
                        var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        var dataStatusImg = d3.select("rect[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");

                        if (uData != "null") {
                            dataU.text(uData + " V");
                        }

                        if (iData != "null") {
                            dataI.text(iData + " A");
                        }

                        var _pTotal = converter.convertLabelElectricPower(viewTypeModal, "W");

                        var tss = _pTotal.replace('[', '');
                        var lbl_pTotal = tss.replace(']', '');

                        var p_Total = pfData;

                        if (p_Total != "null") {
                            dataPF.text(p_Total + " " + lbl_pTotal);
                        }

                        if (tData != "null") {
                            dataT.text(tData + " °C");
                        }

                        // if (Number(pfData) < 0) {
                        //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path2);
                        // } else if (Number(pfData) > 0) {
                        //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path1);
                        // } else {
                        //     dataDirection.attr("d", CONSTANTS.METER.path1);
                        // }

                        if (Number(status) == 0) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }

                        if (count == 0) {
                            dataU.attr("fill", "#FF0000");
                            dataI.attr("fill", "#FF0000");
                            dataPF.attr("fill", "#FF0000");
                            dataT.attr("fill", "#FF0000");
                        } else if (count == 1) {
                            dataU.attr("fill", "#FFCA00");
                            dataI.attr("fill", "#FFCA00");
                            dataPF.attr("fill", "#FFCA00");
                            dataT.attr("fill", "#FFCA00");
                        } else if (count == 2) {
                            dataU.attr("fill", "#53A1EA");
                            dataI.attr("fill", "#53A1EA");
                            dataPF.attr("fill", "#53A1EA");
                            dataT.attr("fill", "#53A1EA");
                        }
                    } else if (systemTypeId == 2) {
                        if (deviceType == "2") {
                            let dataU = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_U + deviceId + "]");
                            let dataI = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_I + deviceId + "]");
                            let dataP = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_P + deviceId + "]");
                            let dataDirection = d3.select("path[id=" + INVERTER.ID_PATH_INVERTER + deviceId + "]");
                            let dataStatus = d3.select("path[id=" + INVERTER.ID_STATUS_INVERTER + deviceId + "]");

                            if (uDataInverter != "null") {
                                dataU.text(uDataInverter + " (V)");
                            }

                            if (iDataInverter != "null") {
                                dataI.text(iDataInverter + " (A)");
                            }

                            if (pfData != "null") {
                                dataP.text(pfData + " (kW)");
                            }

                            // if (Number(pfData) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER_DOWN.path1);
                            // } else if (Number(pfData) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER.path1);
                            // }

                            // if (Number(status) == 0) {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            // } else if (Number(status) == 1) {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            // } else {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            // }

                            if (count == 0) {
                                dataU.attr("fill", "#FF0000");
                                dataI.attr("fill", "#FF0000");
                                dataP.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataU.attr("fill", "#FFCA00");
                                dataI.attr("fill", "#FFCA00");
                                dataP.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataU.attr("fill", "#53A1EA");
                                dataI.attr("fill", "#53A1EA");
                                dataP.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "99") {
                            var dataTemp = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_T + deviceId + "]");
                            var dataH = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_H + deviceId + "]");
                            var dataRad = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_BX + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + WEATHER.ID_PATH_WEATHER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + WEATHER.ID_STATUS_WEATHER + deviceId + "]");

                            dataTemp.text(temp);
                            dataH.text(h);
                            dataRad.text(rad);

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            if (count == 0) {
                                dataTemp.attr("fill", "#FF0000");
                                dataH.attr("fill", "#FF0000");
                                dataRad.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataTemp.attr("fill", "#FFCA00");
                                dataH.attr("fill", "#FFCA00");
                                dataRad.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataTemp.attr("fill", "#53A1EA");
                                dataH.attr("fill", "#53A1EA");
                                dataRad.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "17") {
                            var dataVdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_U + deviceId + "]");
                            var dataIdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_I + deviceId + "]");
                            var dataPdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_P + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + COMBINER.ID_PATH_COMBINER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + COMBINER.ID_STATUS_COMBINER + deviceId + "]");

                            if (vdcCombiner != "null") {
                                dataVdcCb.text(vdcCombiner + " (V)");
                            }

                            if (idcCombiner != "null") {
                                dataIdcCb.text(idcCombiner + " (A)");
                            }

                            if (pdcCombiner != "null") {
                                dataPdcCb.text(pdcCombiner + " (kW)");
                            }

                            // if (Number(pdcCombiner) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.COMBINER_DOWN.path1);
                            // } else if (Number(pdcCombiner) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.COMBINER_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.COMBINER.path1);
                            // }

                            // if (Number(status) == 0) {
                            //     dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            // } else if (Number(status) == 1) {
                            //     dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            // } else {
                            //     dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            // }

                            if (count == 0) {
                                dataVdcCb.attr("fill", "#FF0000");
                                dataIdcCb.attr("fill", "#FF0000");
                                dataPdcCb.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataVdcCb.attr("fill", "#FFCA00");
                                dataIdcCb.attr("fill", "#FFCA00");
                                dataPdcCb.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataVdcCb.attr("fill", "#53A1EA");
                                dataIdcCb.attr("fill", "#53A1EA");
                                dataPdcCb.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "16") {
                            var dataVdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_U + deviceId + "]");
                            var dataIdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_I + deviceId + "]");
                            var dataPdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_T + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + STRING.ID_PATH_STRING + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + STRING.ID_STATUS_STRING + deviceId + "]");

                            if (vdcStr != "null") {
                                dataVdcStr.text(vdcStr + " (V)");
                            }

                            if (idcStr != "null") {
                                dataIdcStr.text(idcStr + " (A)");
                            }

                            dataPdcStr.text("-");

                            // set direction
                            // if (Number(dataPdcStr) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.STRING_DOWN.path1);
                            // } else if (Number(dataPdcStr) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.STRING_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.STRING.path1);
                            // }

                            // if (Number(status) == 0) {
                            //     dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            // } else if (Number(status) == 1) {
                            //     dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            // } else {
                            //     dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            // }


                            if (count == 0) {
                                dataVdcStr.attr("fill", "#FF0000");
                                dataIdcStr.attr("fill", "#FF0000");
                                dataPdcStr.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataVdcStr.attr("fill", "#FFCA00");
                                dataIdcStr.attr("fill", "#FFCA00");
                                dataPdcStr.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataVdcStr.attr("fill", "#53A1EA");
                                dataIdcStr.attr("fill", "#53A1EA");
                                dataPdcStr.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "18") {
                            let dataUpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_U + deviceId + "]");
                            let dataIpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_I + deviceId + "]");
                            let dataPpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_T + deviceId + "]");
                            let dataDirection = d3.select("path[id=" + PANEL.ID_PATH_PANEL + deviceId + "]");
                            let dataStatus = d3.select("path[id=" + PANEL.ID_STATUS_PANEL + deviceId + "]");

                            dataUpanel.text(uPanel);
                            dataIpanel.text(iPanel);
                            dataPpanel.text("-");

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            //set direction
                            // if (Number(pDataInverter) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.PANEL_DOWN.path1);
                            // } else if (Number(pDataInverter) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.PANEL_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.PANEL.path1);
                            // }


                            if (count == 0) {
                                dataUpanel.attr("fill", "#FF0000");
                                dataIpanel.attr("fill", "#FF0000");
                                dataPpanel.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataUpanel.attr("fill", "#FFCA00");
                                dataIpanel.attr("fill", "#FFCA00");
                                dataPpanel.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataUpanel.attr("fill", "#53A1EA");
                                dataIpanel.attr("fill", "#53A1EA");
                                dataPpanel.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "1") {
                            var dataU = d3.select("text[id=" + METER_LOAD.ID_METER_U_VALUE + deviceId + "]");
                            var dataI = d3.select("text[id=" + METER_LOAD.ID_METER_I_VALUE + deviceId + "]");
                            var dataPF = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                            var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                            var dataTemperature = d3.select("text[id=" + METER_LOAD.ID_METER_TEMP_VALUE + deviceId + "]");
                            var dataHumidity = d3.select("text[id=" + METER_LOAD.ID_METER_HUMIDITY_VALUE + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + METER_LOAD.ID_PATH_METER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");

                            if (uData != "null") {
                                dataU.text(uData + " V");
                            }

                            if (iData != "null") {
                                dataI.text(iData + " A");
                            }

                            var _pTotal = converter.convertLabelElectricPower(viewTypeModal, "W");

                            var tss = _pTotal.replace('[', '');
                            var lbl_pTotal = tss.replace(']', '');

                            var p_Total = pfData;

                            if (p_Total != "null") {
                                dataPF.text(p_Total + " " + lbl_pTotal);
                            }

                            if (tData != "null") {
                                dataT.text(tData + " °C");
                            }

                            if (tDataRmu != "null") {
                                dataTemperature.text(tDataRmu + " °C");
                            }

                            if (hDataRmu != "null") {
                                dataHumidity.text(hDataRmu + " %");
                            }

                            // if (Number(pfData) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path2);
                            // } else if (Number(pfData) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.METER.path1);
                            // }

                            if (Number(status) == 0) {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                            } else if (Number(status) == 1) {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                            } else {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                            }

                            if (count == 0) {
                                dataU.attr("fill", "#FF0000");
                                dataI.attr("fill", "#FF0000");
                                dataPF.attr("fill", "#FF0000");
                                dataT.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataU.attr("fill", "#FFCA00");
                                dataI.attr("fill", "#FFCA00");
                                dataPF.attr("fill", "#FFCA00");
                                dataT.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataU.attr("fill", "#53A1EA");
                                dataI.attr("fill", "#53A1EA");
                                dataPF.attr("fill", "#53A1EA");
                                dataT.attr("fill", "#53A1EA");
                            }
                        }
                    } else if (systemTypeId == 5) {
                        var dataU = d3.select("text[id=" + METER_LOAD.ID_METER_U_VALUE + deviceId + "]");
                        var dataI = d3.select("text[id=" + METER_LOAD.ID_METER_I_VALUE + deviceId + "]");
                        var dataPF = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                        var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                        var dataTemperature = d3.select("text[id=" + METER_LOAD.ID_METER_TEMP_VALUE + deviceId + "]");
                        var dataHumidity = d3.select("text[id=" + METER_LOAD.ID_METER_HUMIDITY_VALUE + deviceId + "]");
                        var dataDirection = d3.select("path[id=" + METER_LOAD.ID_PATH_METER + deviceId + "]");
                        var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");
                        var dataStatusImg = d3.select("rect[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");

                        if (tDataRmu != "null") {
                            dataTemperature.text(tDataRmu + " °C");
                        }

                        if (hDataRmu != "null") {
                            dataHumidity.text(hDataRmu + " %");
                        }

                        // if (Number(pfData) < 0) {
                        //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path2);
                        // } else if (Number(pfData) > 0) {
                        //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path1);
                        // } else {
                        //     dataDirection.attr("d", CONSTANTS.METER.path1);
                        // }

                        if (Number(status) == 0) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                        } else if (Number(status) == 1) {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                        } else {
                            dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                            dataStatusImg.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                        }
                        if (deviceType == "2") {
                            let dataU = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_U + deviceId + "]");
                            let dataI = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_I + deviceId + "]");
                            let dataP = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_P + deviceId + "]");
                            let dataDirection = d3.select("path[id=" + INVERTER.ID_PATH_INVERTER + deviceId + "]");
                            let dataStatus = d3.select("path[id=" + INVERTER.ID_STATUS_INVERTER + deviceId + "]");

                            if (uDataInverter != "null") {
                                dataU.text(uDataInverter + " (V)");
                            }

                            if (iDataInverter != "null") {
                                dataI.text(iDataInverter + " (A)");
                            }

                            if (pfData != "null") {
                                dataP.text(pfData + " (kW)");
                            }

                            // if (Number(pfData) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER_DOWN.path1);
                            // } else if (Number(pfData) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.INVERTER.path1);
                            // }

                            // if (Number(status) == 0) {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            // } else if (Number(status) == 1) {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            // } else {
                            //     dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                            //         .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            // }

                            if (count == 0) {
                                dataU.attr("fill", "#FF0000");
                                dataI.attr("fill", "#FF0000");
                                dataP.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataU.attr("fill", "#FFCA00");
                                dataI.attr("fill", "#FFCA00");
                                dataP.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataU.attr("fill", "#53A1EA");
                                dataI.attr("fill", "#53A1EA");
                                dataP.attr("fill", "#53A1EA");
                            }
                        } else if (deviceType == "1") {
                            var dataU = d3.select("text[id=" + METER_LOAD.ID_METER_U_VALUE + deviceId + "]");
                            var dataI = d3.select("text[id=" + METER_LOAD.ID_METER_I_VALUE + deviceId + "]");
                            var dataPF = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                            var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                            var dataTemperature = d3.select("text[id=" + METER_LOAD.ID_METER_TEMP_VALUE + deviceId + "]");
                            var dataHumidity = d3.select("text[id=" + METER_LOAD.ID_METER_HUMIDITY_VALUE + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + METER_LOAD.ID_PATH_METER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");

                            if (uData != "null") {
                                dataU.text(uData + " V");
                            }

                            if (iData != "null") {
                                dataI.text(iData + " A");
                            }

                            var _pTotal = converter.convertLabelElectricPower(viewTypeModal, "W");

                            var tss = _pTotal.replace('[', '');
                            var lbl_pTotal = tss.replace(']', '');

                            var p_Total = pfData;

                            if (p_Total != "null") {
                                dataPF.text(p_Total + " " + lbl_pTotal);
                            }

                            if (tData != "null") {
                                dataT.text(tData + " °C");
                            }

                            if (tDataRmu != "null") {
                                dataTemperature.text(tDataRmu + " °C");
                            }

                            if (hDataRmu != "null") {
                                dataHumidity.text(hDataRmu + " %");
                            }

                            // if (Number(pfData) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path2);
                            // } else if (Number(pfData) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.METER.path1);
                            // }

                            if (Number(status) == 0) {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(17.647059%,79.607843%,14.117647%);fill-opacity:1;")
                            } else if (Number(status) == 1) {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,48.627451%,10.980392%);fill-opacity:1;")
                            } else {
                                dataStatus.attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(24.313725%,30.588235%,36.078431%);fill-opacity:1;")
                            }

                            if (count == 0) {
                                dataU.attr("fill", "#FF0000");
                                dataI.attr("fill", "#FF0000");
                                dataPF.attr("fill", "#FF0000");
                                dataT.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataU.attr("fill", "#FFCA00");
                                dataI.attr("fill", "#FFCA00");
                                dataPF.attr("fill", "#FFCA00");
                                dataT.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataU.attr("fill", "#53A1EA");
                                dataI.attr("fill", "#53A1EA");
                                dataPF.attr("fill", "#53A1EA");
                                dataT.attr("fill", "#53A1EA");
                            }
                        }
                        // if (deviceType == "1") {
                        //     var dataT = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_T + deviceId + "]");
                        //     var dataH = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_H + deviceId + "]");
                        //     var dataP = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_P + deviceId + "]");
                        //     var dataU = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_U + deviceId + "]");
                        //     var dataI = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_I + deviceId + "]");

                        //     // var dataDirection = d3.select("path[id=" + KHOANGDODEM.ID_PATH_INVERTER + deviceId + "]");
                        //     var dataStatus = d3.select("circle[id=" + KHOANGDODEM.ID_STATUS_KHOANG + deviceId + "]");

                        //     if (dataT != null) {
                        //         if (tDataRmu == "-3276.8") {
                        //             tDataRmu = "-";
                        //         }
                        //         dataT.text(tDataRmu >= -50.0 && tDataRmu <= 180.0 ? tDataRmu : "-");
                        //     }
                        //     if (dataH != null) {
                        //         if (hDataRmu == "-1") {
                        //             hDataRmu = "-";
                        //         }
                        //         dataH.text(hDataRmu >= 0 && hDataRmu <= 100.0 ? hDataRmu : "-");
                        //     }
                        //     if (dataP != null) {
                        //         if (indicatorDataRmu == "-1") {
                        //             indicatorDataRmu = "-";
                        //         }
                        //         dataP.text(indicatorDataRmu >= 0 && indicatorDataRmu <= 3 ? indicatorDataRmu : "-");
                        //     }
                        //     if (dataU != null) {
                        //         if (uData == "-1") {
                        //             uData = "-";
                        //         }
                        //         dataU.text(uData);
                        //     }
                        //     if (dataI != null) {
                        //         if (iData == "-1") {
                        //             iData = "-";
                        //         }
                        //         dataI.text(iData);
                        //     }

                        //     if (Number(status) == 0) {
                        //         dataStatus.attr("fill", "#33FF33");
                        //     } else if (Number(status) == 1) {
                        //         dataStatus.attr("fill", "#FF8000");
                        //     } else {
                        //         dataStatus.attr("fill", "#FF0000");
                        //     }

                        //     if (count == 0) {
                        //         dataT.attr("fill", "#FF0000");
                        //         dataH.attr("fill", "#FF0000");
                        //         dataP.attr("fill", "#FF0000");
                        //         dataU.attr("fill", "#FF0000");
                        //         dataI.attr("fill", "#FF0000");
                        //     } else if (count == 1) {
                        //         dataT.attr("fill", "#FFCA00");
                        //         dataH.attr("fill", "#FFCA00");
                        //         dataP.attr("fill", "#FFCA00");
                        //         dataU.attr("fill", "#FFCA00");
                        //         dataI.attr("fill", "#FFCA00");
                        //     } else if (count == 2) {
                        //         dataT.attr("fill", "#53A1EA");
                        //         dataH.attr("fill", "#53A1EA");
                        //         dataP.attr("fill", "#53A1EA");
                        //         dataU.attr("fill", "#53A1EA");
                        //         dataI.attr("fill", "#53A1EA");
                        //     }
                        // }
                    }
                }
                count = count + 1;

                if (count > 2) {
                    count = 0;
                }
            }
        } else {
            clearInterval(ajaxGetInstanceData);
        }
    };

    const funcDisplayLayer = async () => {
        let subMenu = document.getElementById("grid-utility-home2");
        if (subMenu.style.display == 'block') {
            subMenu.style.display = 'none'
        } else {
            subMenu.style.display = 'block'
        }
    }
    useEffect(() => {
        createInstance();
        setSystemTypeId(type);
        getSystemMapList();
        return () => {
            clearInterval(ajaxGetListData);
            clearInterval(ajaxGetInstanceData);
        };
    }, [location]);

    return (
        <>
            <div className='border-system-map rounded pl-1 pr-1 pb-1 p-relative'>
                <div>
                    <div className="project-infor" style={{ padding: "10px 8px" }}>
                        <span className="title-up-oneline text-left text-uppercase" style={{ width: "fit-content" }}><div style={{ marginTop: "-21px", color: "white" }}><i className="fa-solid fa-diagram-project ml-1" style={{ color: "#fff" }}></i> {projectInfo}{infor}</div></span>
                        <button className='text-right float-right rounded btn btn-primary' style={{ margin: "0 auto" }} onClick={() => funcDisplayLayer()}>
                            <i className="fa-solid fa-arrow-right" style={{ color: "#FFF" }}></i> &nbsp;
                            LAYER</button>
                    </div>
                </div>
                <div id='grid-utility-home2' className="grid-utility-home2" style={{ display: "none" }}>
                    <div className="tab-content pt-1">
                        <div className="tab-pane active" id="tab-window" role="tabpanel"
                            aria-labelledby="baseIcon-tab-window">
                            {
                                layerTree?.map(
                                    (layer, index) =>
                                        <div key={index}>
                                            <span className={`tool-layer layer-0${layer.id}`}> <i className="fa-solid fa-tag" style={{ color: "white" }}></i>
                                                &nbsp;LAYER {layer.id}</span>
                                            {
                                                layer.systemMap?.map(
                                                    (window, i) =>
                                                        <div key={i}>
                                                            {
                                                                systemMapCurrent == window.id ?
                                                                    <Link to={`/${customerId}/${urlSearch}/${type}/${projectId}/${window.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 21 ? window.name.substring(0, 19) + "..." : window.name}
                                                                    </span></Link>
                                                                    :
                                                                    <Link to={`/${customerId}/${urlSearch}/${type}/${projectId}/${window.id}`} style={{ display: 'block', color: '#495057' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 21 ? window.name.substring(0, 19) + "..." : window.name}
                                                                    </span></Link>
                                                            }
                                                            {
                                                                deviceAlready?.map(
                                                                    (deviceItem, j) =>
                                                                        deviceItem.systemMapId == window.id &&
                                                                        <Link to={`/${customerId}/${urlSearch}/${type}/${projectId}/${window.id}?deviceId=${deviceItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == deviceItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={j}>
                                                                            <span className={`${deviceCurrent == deviceItem.deviceId && "font-weight-bold"}`}>
                                                                                <i className="fas fa-server"></i>
                                                                                &nbsp;{deviceItem.deviceName.length > 29 ? deviceItem.deviceName.substring(0, 27) + "..." : deviceItem.deviceName}
                                                                            </span>
                                                                        </Link>
                                                                )
                                                            }
                                                        </div>
                                                )
                                            }
                                        </div>
                                )
                            }
                        </div>
                    </div>
                </div>

                <div className='window-info-home2' style={{ left: "50px" }}>
                    <div style={{ color: `${deviceTime == (t('content.no_data')) ? "#dc3545" : "#45484D"}` }}>
                        {deviceTime}
                    </div>
                    <div className='mt-1'>
                        {breadscrum}
                    </div>
                </div>
                {
                    beforeId > 0 ?
                        <Link className="system-map" style={{ left: "10px" }} to={`/${customerId}/${urlSearch}/${type}/${projectId}/${beforeId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-up.png" alt="system-map-up" /></Link>
                        :
                        <Link className="system-map" style={{ left: "10px" }} to={`/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-up-disable.png" alt="system-map-up" /></Link>
                }

                {
                    nextId > 0 ?
                        <Link className="system-map" style={{ left: "10px", top: "90px" }} to={`/${customerId}/${urlSearch}/${type}/${projectId}/${nextId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-down.png" alt="system-map-down" /></Link>
                        :
                        <Link className="system-map" style={{ left: "10px", top: "90px" }} to={`/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-down-disable.png" alt="system-map-down" /></Link>
                }

                <Link className="system-map" style={{ left: "10px", top: "120px" }} to={`/`} onClick={(event) => event.preventDefault()}><img id="reset-zoom" className="system-map-to-window" src="/resources/image/icon-reset-zoom.png" alt="reset-zoom" /></Link>
                <div id="grid-canvas" className="grid-system-map">

                </div>

                <div className="modal tab-content" id="model-oper-info-tool">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="value-modal-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th id="value-th-date" style={{ backgroundColor: "white" }}></th>
                                            <th width="100px">Phase A</th>
                                            <th width="100px">Phase B</th>
                                            <th width="100px">Phase C</th>
                                            <th width="100px">Phase N</th>
                                            <th width="100px">Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">U<sub>LL</sub> [V]</th>
                                            <td id="value-td-uab"></td>
                                            <td id="value-td-ubc"></td>
                                            <td id="value-td-uca"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">U<sub>LN</sub> [V]</th>
                                            <td id="value-td-uan"></td>
                                            <td id="value-td-ubn"></td>
                                            <td id="value-td-ucn"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">I [A]</th>
                                            <td id="value-td-ia"></td>
                                            <td id="value-td-ib"></td>
                                            <td id="value-td-ic"></td>
                                            <td id="value-td-in"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-p">P</th>
                                            <td id="value-td-pa"></td>
                                            <td id="value-td-pb"></td>
                                            <td id="value-td-pc"></td>
                                            <td>-</td>
                                            <td id="value-td-ptotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-q">Q [kVAr]</th>
                                            <td id="value-td-qa"></td>
                                            <td id="value-td-qb"></td>
                                            <td id="value-td-qc"></td>
                                            <td>-</td>
                                            <td id="value-td-qtotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-s">S [kVA]</th>
                                            <td id="value-td-sa"></td>
                                            <td id="value-td-sb"></td>
                                            <td id="value-td-sc"></td>
                                            <td>-</td>
                                            <td id="value-td-stotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">PF</th>
                                            <td id="value-td-pfa"></td>
                                            <td id="value-td-pfb"></td>
                                            <td id="value-td-pfc"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">THD U [%]</th>
                                            <td id="value-td-thdVab"></td>
                                            <td id="value-td-thdVbc"></td>
                                            <td id="value-td-thdVca"></td>
                                            <td id="value-td-thdVan"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">THD I [%]</th>
                                            <td id="value-td-thdIa"></td>
                                            <td id="value-td-thdIb"></td>
                                            <td id="value-td-thdIc"></td>
                                            <td id="value-td-thdIn"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Temp [°C]</th>
                                            <td id="value-td-t1"></td>
                                            <td id="value-td-t2"></td>
                                            <td id="value-td-t3"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-ep">Active Energy [kWh]</th>
                                            <td id="value-td-ep" colSpan={5}></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-eq">Reactive Energy [kVArh]</th>
                                            <td id="value-td-eq" colSpan={5}></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Trạng Thái MCCB</th>
                                            <td colSpan={5}></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#model-oper-info-tool').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/load/${customerId}/${projectId}/device-information/${deviceIdCurrent}`);
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SystemMapComponent2;