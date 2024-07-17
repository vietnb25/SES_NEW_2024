import React, { useEffect, useState } from "react";
import moment from "moment/moment";
import WarningService from "../../../../../services/WarningService";
import CONS from "../../../../../constants/constant";
import ReactModal from "react-modal";
import AuthService from "../../../../../services/AuthService";
import { useFormik } from 'formik';
import { Link, useLocation } from "react-router-dom";
import { Calendar } from 'primereact/calendar';
import Pagination from "react-js-pagination";
import converter from "../../../../../common/converter";
import authService from "../../../../../services/AuthService";
import { useTranslation } from "react-i18next";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { useRef } from "react";
import { useDownloadExcel } from "react-export-table-to-excel";
const $ = window.$;

const WarningLoadDevice = ({ customerId, projectId, systemTypeId, deviceId }) => {
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [totalPage, setTotalPage] = useState(1);
    const [viewTypeModal, setViewTypeModal] = useState(null);
    const [settingValue, setSettingValue] = useState(null);
    const [isActiveButton, setIsActiveButton] = useState(true);

    const [display, setDisplay] = useState(false)
    const [valueTime, setValueTime] = useState(0);
    const { t } = useTranslation();
    const [warnedDevice, setWarnedDevice] = useState([]);
    const [searchWarnedDevice, setSearchWarnedDevice] = useState([]);
    const [tableOrChart, setTableOrChart] = useState(0);
    const [listWarning, setListWarning] = useState([]);
    const [listWarningFrame2, setListWarningFrame2] = useState([]);
    const [selectedWarningType, setSelectedWarningType] = useState();

    // total warning state
    const [warnings, setWarnings] = useState({
        nguongApCao: 0,
        nguongApThap: 0,
        quaTai: 0,
        heSoCongSuatThap: 0,
        tanSoCao: 0,
        tanSoThap: 0,
        lechPha: 0,
        songHai: 0,
        nguocPha: 0,
        matDienTong: 0,
        nhietDoCao: 0,
        nhietDoThap: 0,
        matKetNoiAC: 0,
        matKetNoiDC: 0,
        dienApCaoAC: 0,
        dienApThapAC: 0,
        tanSoThap: 0,
        tanSoCao: 0,
        matNguonLuoi: 0,
        chamDat: 0,
        hongCauChi: 0,
        dongMoCua: 0,
        dienApCaoDC: 0,
        matBoNho: 0,
        doAm: 0,
        FITuRMU: 0,
        khoangTonThat: 0,
        mucDauThap: 0,
        roleGas: 0,
        chamVo: 0,
        mucDauCao: 0,
        camBienHongNgoai: 0,
        apSuatNoiBoMBA: 0,
        roleNhietDoDau: 0,
        nhietDoCuonDay: 0,
        khiGasMBA: 0
    });
    const [typeNameDowload, setTypeNameDowload] = useState("tableEp");
    const tableRef = useRef(null);
    const { onDownload } = useDownloadExcel({
        currentTableRef: tableRef.current,
        filename: typeNameDowload,
        sheet: typeNameDowload
    });

    // Location
    const location = useLocation();

    // active warning state
    const [activeWarning, setActiveWaring] = useState("warning-all");

    // current page state
    const [page, setPage] = useState(1);

    // detail warning
    const [detailWarnings, setDetailWarnings] = useState([]);

    // active modal state
    const [isModalOpen, setIsModalOpen] = useState(false);

    // active modal update warning
    const [isModalUpdateOpen, setIsModalUpdateOpen] = useState(false);

    // update warning state
    const [updateWarning, setUpdateWarning] = useState(null);

    // warning type table
    const [warningType, setWarningType] = useState(0);

    const [inforWarning, setInforWarning] = useState({
        deviceName: "",
        warningTypeName: "",
        value: "-",
        settingValue: "-",
        deviceLevel: "-",
        warningLevel: "-",
        fromDate: "",
        toDate: ""
    });

    const formik = useFormik({
        initialValues: updateWarning,
        enableReinitialize: true,
        onSubmit: async data => {
            let updateWarningData = {
                id: data.warningId,
                status: data.status,
                description: data.description,
                username: AuthService.getAuth().username
            }
            let res = await WarningService.updateWarningCache(updateWarningData, customerId);
            if (res.status === 200) {
                setIsModalUpdateOpen(false);
                deviceWarning(warningType, activeWarning);
            }
        }
    });

    // data load frame warning by warning type
    const [dataLoadFrameWarning, setDataLoadFrameWarning] = useState({
        page: 1,
        totalPage: 1,
        warningType: null,
        data: []
    });

    const getWarning = async (fromTime, toTime) => {
        deviceWarning("ALL", "warning-all", fromTime, toTime);
        let fDate = moment(fromTime).format("YYYY-MM-DD");
        let tDate = moment(toTime).format("YYYY-MM-DD");
        let res = await WarningService.getWarnings(fDate, tDate, projectId, customerId, systemTypeId);
        if (res.status === 200) {
            setWarnings(res.data);
            console.log("Project(CustomerId) warning", res.data);
        }
    }
    const deviceWarning = async (type, idSelector, fromTime, toTime) => {
        $('#warnedDevices').hide();
        $('#warning-loading').show();
        setActiveWaring(idSelector);
        setWarningType(type);
        let fDate = moment(fromTime).format("YYYY-MM-DD");
        let tDate = moment(toTime).format("YYYY-MM-DD");
        let res = await WarningService.getListWarnedDevice(fDate, tDate, projectId, customerId, systemTypeId, type);
        if (res.status === 200) {
            setWarnedDevice(res.data);
            console.log("device warning", res.data);
            if (deviceId != undefined) {
                let dataR = res.data.filter(data => data.deviceId === deviceId);
                setSearchWarnedDevice(dataR);
                console.log("search device warning", dataR);
            } else {
                setSearchWarnedDevice(res.data)
                console.log("search device warning", res.data);
            }
            $('#warning-loading').hide();
            $('#warnedDevices').show();

        }
    }









    const handleChangeView = (isActive) => {
        setIsActiveButton(!isActive)
        setValueTime(() => 1)
        let fromTime = moment(new Date).format("YYYY-MM-DD") + " 00:00:00"
        let toTime = moment(new Date).format("YYYY-MM-DD") + " 23:59:59"
        setFromDate(fromTime)
        setToDate(toTime)
    }

    // const getDataByDate = () => {
    //     if (fromDate > toDate) {
    //         console.log("error");
    //         setDisplay(true)
    //     } else {
    //         setDisplay(false)
    //         let fromTime = moment(fromDate).format("YYYY-MM-DD") + " 00:00:00";
    //         let toTime = moment(toDate).format("YYYY-MM-DD") + " 23:59:59";
    //         setFromDate(fromTime)
    //         setToDate(toTime)
    //         //     funcGetWarnings(fromTime, toTime, warningLevel, 1, 1, projectId)
    //         //     funcGetWarningCar(fromTime, toTime, 1, 1, projectId)
    //         getWarning(fromTime, toTime);
    //     }
    // }

    const onChangeValue = async (e) => {
        let time = e.target.value;
        setValueTime(() => e.target.value)
        const today = new Date();
        let fromTime = "";
        let toTime = "";
        if (time == 2) {
            today.setDate(today.getDate() - 1);
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 3) {
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 4) {
            today.setMonth(today.getMonth() - 1);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối tháng trước */
            today.setMonth(today.getMonth() + 1)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 5) {
            today.setMonth(today.getMonth() - 3);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối 3 tháng trước */
            today.setMonth(today.getMonth() + 3)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 6) {
            today.setMonth(today.getMonth() - 6);
            fromTime = moment(today).format("YYYY-MM") + "-01" + " 00:00:00";
            /**Xét ngày cuối 6 tháng trước */
            today.setMonth(today.getMonth() + 6)
            let temp = new Date(today.getFullYear() + "-" + today.getMonth() + "-" + "01")
            today.setDate(temp.getDate() - 1);
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 7) {
            fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else if (time == 8) {
            today.setYear(today.getFullYear() - 1);
            fromTime = moment(today).format("YYYY") + "-01-01" + " 00:00:00";
            /**Xét ngày cuối năm ngoái */
            toTime = moment(today).format("YYYY") + "-12-31" + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)

        } else {
            fromTime = moment(today).format("YYYY-MM-DD") + " 00:00:00";
            toTime = moment(today).format("YYYY-MM-DD") + " 23:59:59";
            setFromDate(fromTime)
            setToDate(toTime)
        }
        getWarning(fromTime, toTime);
    }
    function removeDiacritics(text) {
        return text.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }
    const searchDevice = async (e) => {
        let deviceName = e.target.value.toLowerCase();
        if (deviceName === "") {
            setSearchWarnedDevice(warnedDevice);
        } else {
            let customerSearch = warnedDevice?.map(d => ({
                ...d,
                listWarning: d.listWarning.filter(warning => removeDiacritics(warning.warningTypeName.toLowerCase()).includes(removeDiacritics(deviceName)))
            })).filter(d => d.listWarning.length > 0);
            setSearchWarnedDevice(customerSearch);
        }
    }

    const funcInforWarning = async (warning) => {
        let data = warning;
        let res = await WarningService.getInfoWarnedDevice(customerId, systemTypeId, warning.deviceId, warning.warningType, warning.toDate);
        if (res.status == 200) {
            let dataWarning = res.data.data;
            let setting = res.data.setting;
            console.log("Setting: ", setting);
            console.log("DataWarning", dataWarning);
            let warningType = warning.warningType;
            let settingValue = setting.split(",");
            if (settingValue.length > 1) {

                // thiết bị meter
                if (warningType == 104) {
                    settingValue = settingValue[1];
                } else {
                    settingValue = settingValue[0];
                }
            } else {
                if (warningType == 103) {
                    settingValue = parseFloat(settingValue[0]) * parseFloat(dataWarning.imccb);
                }
            }
            let value = "-"
            if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO) {
                value = Math.max(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
            } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                value = Math.min(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
            } else if (warningType == CONS.WARNING_TYPE.QUA_TAI) {
                value = Math.max(dataWarning.ia, dataWarning.ib, dataWarning.ic);
            } else if (warningType == CONS.WARNING_TYPE.HE_SO_CONG_SUAT_THAP) {
                value = Math.max(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc);
            } else if (CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                value = dataWarning.f
            } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType)) {
                value = dataWarning.f
            } else if (warningType == CONS.WARNING_TYPE.LECH_PHA) {
                value = Math.min(dataWarning.ia, dataWarning.ib, dataWarning.ic);
            } else if (CONS.WARNING_TYPE.SONG_HAI.includes(warningType)) {
                if (warningType == 110) {
                    value = Math.min(dataWarning.thdVan, dataWarning.thdVbn, dataWarning.thdVcn);
                } else if (warningType == 111) {
                    value = Math.min(dataWarning.thdIa, dataWarning.thdIb, dataWarning.thdIc);
                }
            } else if (warningType == CONS.WARNING_TYPE.NGUOC_PHA) {
                value = Math.min(dataWarning.pfa, dataWarning.pfb, dataWarning.pfc);
            } else if (warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                value = Math.min(dataWarning.uan, dataWarning.ubn, dataWarning.ucn);
            } else if (warningType == CONS.WARNING_TYPE.SONG_HAI_DIEN_AP_BAC_N) {
                value = 2
            }

            // thiết bị temp - hummidity sensor
            if (CONS.WARNING_TYPE.NHIET_DO_CAO.includes(warningType)) {
                value = dataWarning.t;
            } else if (CONS.WARNING_TYPE.NHIET_DO_THAP.includes(warningType)) {
                value = dataWarning.t;
            } else if (CONS.WARNING_TYPE.DO_AM.includes(warningType)) {
                value = dataWarning.h;
            }

            settingValue = { settingValue: settingValue }
            value = { value: value }
            data = { ...data, ...settingValue, ...value }

            funcDrawChart(customerId, warning.deviceId, warning.warningType,warning.fromDate, warning.toDate, settingValue.settingValue)
        }
        setInforWarning(data)
    }

    const funcInforWarningFrame2 = async (warning) => {
        let data = warning;
        let res = await WarningService.getInfoWarnedDeviceFrame2(customerId, systemTypeId, warning.deviceId, warning.warningType, warning.toDate);
        if (res.status == 200) {
            let dataWarning = res.data.dataFrame2;
            let setting = res.data.setting;
            console.log("Setting: ", setting);
            console.log("DataWarning", dataWarning);

            let warningType = warning.warningType;
            console.log("warning type la", warningType);
            let settingValue = setting.split(",");


            // if (warningType == 108) {


            //     settingValue = settingValue.toString()


            //     console.log("warning 108", settingValue);
            // }
            let value = "-"
            if (warningType == CONS.WARNING_TYPE.SONG_HAI_DIEN_AP_BAC_N) {


                let minValue = Infinity;

                for (let i = 2; i <= 31; i++) {
                    const vanHValue = dataWarning["van_H" + i];
                    const vbnHValue = dataWarning["vbn_H" + i];
                    const vcnHValue = dataWarning["vcn_H" + i];

                    if (vanHValue >= 3 && vanHValue < minValue) {
                        minValue = vanHValue;
                    }

                    else if (vbnHValue >= 3 && vbnHValue < minValue) {
                        minValue = vbnHValue;
                    }

                    else if (vcnHValue >= 3 && vcnHValue < minValue) {
                        minValue = vcnHValue;
                    }
                }

                if (minValue !== Infinity) {
                    value = minValue;
                } else {
                    // Không có giá trị nào lớn hơn 3
                    // Thực hiện xử lý tương ứng nếu cần
                }

            } else if (warningType == CONS.WARNING_TYPE.SONG_HAI_DONG_DIEN_BAC_N) {
                // const settingValueString = "6,3,2.25,0.9,0.35";
                // const settingValueArray = settingValueString.toString() // Chuyển đổi chuỗi thành mảng số
                // settingValue = settingValueArray
                let minValue = Infinity;

                for (let i = 2; i <= 10; i++) {
                    const currentValueIA = dataWarning["ia_H" + i];
                    const currentValueIB = dataWarning["ib_H" + i];
                    const currentValueIC = dataWarning["ic_H" + i];
                    if (currentValueIA >= 6 || currentValueIB >= 6 || currentValueIC >= 6) {
                        minValue = Math.min(currentValueIA, currentValueIB, currentValueIC);

                        console.log("setting value 108: ", settingValue);
                    }
                }

                for (let i = 11; i <= 16; i++) {
                    const currentValueIA = dataWarning["ia_H" + i];
                    const currentValueIB = dataWarning["ib_H" + i];
                    const currentValueIC = dataWarning["ic_H" + i];
                    if (currentValueIA >= 3 || currentValueIB >= 3 || currentValueIC >= 3) {
                        minValue = Math.min(currentValueIA, currentValueIB, currentValueIC);

                    }
                }


                for (let i = 17; i <= 22; i++) {
                    const currentValueIA = dataWarning["ia_H" + i];
                    const currentValueIB = dataWarning["ib_H" + i];
                    const currentValueIC = dataWarning["ic_H" + i];
                    if (currentValueIA >= 2.25 || currentValueIB >= 2.25 || currentValueIC >= 2.25) {
                        minValue = Math.min(currentValueIA, currentValueIB, currentValueIC);

                    }
                }

                for (let i = 23; i <= 34; i++) {
                    const currentValueIA = dataWarning["ia_H" + i];
                    const currentValueIB = dataWarning["ib_H" + i];
                    const currentValueIC = dataWarning["ic_H" + i];
                    if (currentValueIA >= 0.9 || currentValueIB >= 0.9 || currentValueIC >= 0.9) {
                        minValue = Math.min(currentValueIA, currentValueIB, currentValueIC);

                    }
                }

                if (minValue !== Infinity) {
                    value = minValue;
                } else {
                    // Không có giá trị nào lớn hơn 3
                    // Thực hiện xử lý tương ứng nếu cần
                }
            }

            // thiết bị temp - hummidity sensor


            settingValue = { settingValue: settingValue }

            value = { value: value }
            data = { ...data, ...settingValue, ...value }

            funcDrawChartFrame2(customerId, warning.deviceId, warning.warningType, warning.toDate, settingValue.settingValue)
        }
        setInforWarning(data)
    }

    const funcDrawChart = async (customerId, deviceId, warningType,fromDate, toDate, setting) => {
        let res = await WarningService.getListDataWarning(customerId, systemTypeId, deviceId, warningType, fromDate, toDate);

        if (res.status == 200) {
            setListWarning(res.data.data);
            setSelectedWarningType(warningType);
            drawChart(res.data.data, setting, warningType);
            console.log("listwarningFrame1 funcdrawchat", listWarning);
        }

    }

    const funcDrawChartFrame2 = async (customerId, deviceId, warningType, toDate, setting) => {

        let resFrame2 = await WarningService.getListDataWarningFrame2(customerId, systemTypeId, deviceId, warningType, toDate);


        if (resFrame2.status == 200) {
            setListWarningFrame2(resFrame2.data.data)
            console.log("listwarningFrame2 funcdrawchat", listWarningFrame2);
            setSelectedWarningType(warningType);
            drawChart(resFrame2.data.data, setting, warningType);
        }
    }



    const drawChart = (dataWarning, settingValue, warningType) => {
        const data = dataWarning.map((item) => {
            return { ...item, settingValue: parseFloat(settingValue) };
        });
        // const data = dataWarning

        console.log("list Warning", data);
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdivWarning") {
                    root.dispose();
                }
            }
        });
        am5.ready(function () {
            var root = am5.Root.new("chartdivWarning");
            root._logo.dispose();
            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
                am5themes_Animated.new(root)
            ]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            var chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    focusable: true,
                    panX: true,
                    panY: false,
                    heelX: "panX",
                    wheelY: "zoomX",
                    layout: root.verticalLayout

                })
            );

            chart.get("colors").set("colors", [
                am5.color(0xe41a1c),
                am5.color(0x377eb8),
                am5.color(0x4daf4a),
                am5.color(0x984ea3),
                am5.color(0xff7f00),
                am5.color(0xffff33),
                am5.color(0xa65628),
                am5.color(0x999999),
                am5.color(0x66c2a5),
                am5.color(0xfc8d62),
                am5.color(0xe78ac3),
                am5.color(0xa6d854),
                am5.color(0xffd92f),
            ]);

            var easing = am5.ease.linear;

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                behavior: "none"
            }));
            cursor.lineY.set("visible", false);

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            var xRenderer = am5xy.AxisRendererX.new(root, {
                minGridDistance: 50,
                strokeOpacity: 0.2,
            });
            xRenderer.labels.template.setAll({
                rotation: -30,
                paddingTop: 10,
                paddingRight: 10,
                fontSize: 10
            });
            xRenderer.grid.template.set("forceHidden", true);

            var xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "viewTime",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                })
            );

            xAxis.data.setAll(data);

            var yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxPrecision: 0,
                    renderer: am5xy.AxisRendererY.new(root, {
                        inversed: false
                    })
                })
            );

            // var seriesRangeDataItem = yAxis.makeDataItem({});

            // seriesRangeDataItem.get("grid").setAll({
            //     strokeOpacity: 1,
            //     visible: true,
            //     stroke: am5.color(0x000000),
            //     strokeDasharray: [2, 2]
            // })

            // seriesRangeDataItem.get("label")({
            //     location: 0,
            //     visible: true,
            //     text: "Target",
            //     inside: true,
            //     centerX: 0,
            //     centerY: am5.p100,
            //     fontWeight: "bold"
            // })



            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

            function createSeries(name, field, checked) {
                var series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "viewTime",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "[bold]{name}[/]\n{categoryX}: {valueY}"
                        })

                    })
                );
                if (checked) {
                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            sprite: am5.Circle.new(root, {
                                radius: 2,
                                fill: series.get("fill")
                            })
                        });
                    });
                }

                series.set("setStateOnChildren", true);
                series.states.create("hover", {});

                series.mainContainer.set("setStateOnChildren", true);
                series.mainContainer.states.create("hover", {});

                series.strokes.template.states.create("hover", {
                    strokeWidth: 4
                });
                chart.yAxes.getIndex(0).set("separateStacks", true);
                series.data.setAll(data);
                series.appear(1000);
                chart.appear(1000, 100);
            }

            function createSeriesN(name, field, checked) {
                var series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "viewTime",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "[bold]{name}[/]\n{categoryX}: {valueY}"
                        })
                    })
                );
                if (checked) {
                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            sprite: am5.Circle.new(root, {
                                radius: 2,
                                fill: series.get("fill")
                            })
                        });
                    });
                }
                var seriesRangeDataItem = yAxis.makeDataItem({ value: field, endValue: 0 });
                var seriesRange = series.createAxisRange(seriesRangeDataItem);
                seriesRange.fills.template.setAll({
                    visible: true,
                    opacity: 0.3
                });

                seriesRange.fills.template.set("fill", am5.color(0xFF0000));

                // Tạo một đối tượng Cursor

                seriesRangeDataItem.get("grid").setAll({
                    strokeOpacity: 1,
                    visible: true,
                    stroke: am5.color(0xFF0000),
                    strokeDasharray: [0, 0],
                    strokeWidth: 2,
                    step: 100
                })


                seriesRangeDataItem.get("label").setAll({
                    location: 0,
                    visible: true,
                    text: "",

                    inside: true,
                    centerX: 0,
                    centerY: am5.p100,
                    fontWeight: "bold"
                })

                series.set("setStateOnChildren", true);
                series.states.create("hover", {});

                series.mainContainer.set("setStateOnChildren", true);
                series.mainContainer.states.create("hover", {});

                series.strokes.template.states.create("hover", {
                    strokeWidth: 4
                });

                series.data.setAll(data);
                chart.appear(1000, 100);
                series.appear(1000);
            }

            // for (let x = 0; x < settingValue.length; x++) {


            //     createSeriesN("Ngưỡng " + (x + 1), settingValue[x])

            // }
            if (warningType == CONS.WARNING_TYPE.SONG_HAI_DONG_DIEN_BAC_N) {
                createSeries("Ngưỡng", 'settingValue')
                for (let x = 0; x < settingValue.length; x++) {


                    createSeriesN("Ngưỡng " + (x + 1), settingValue[x])

                }


            } else {
                createSeries("Ngưỡng", 'settingValue')
            }
            // for (let x = 0; x < settingValue.length; x++) {
            //     createSeriesN("Ngưỡng" + x, x)
            //     console.log(settingValue.length);

            // }

            // thiết bị meter
            if (warningType == CONS.WARNING_TYPE.NGUONG_AP_CAO) {
                createSeries("Uan", "uan", true);
                createSeries("Ubn", "ubn", true);
                createSeries("Ucn", "ucn", true);
            } else if (warningType == CONS.WARNING_TYPE.NGUONG_AP_THAP) {
                createSeries("Uan", "uan", true);
                createSeries("Ubn", "ubn", true);
                createSeries("Ucn", "ucn", true);
            } else if (warningType == CONS.WARNING_TYPE.LECH_PHA) {
                createSeries("Ia", "ia", true);
                createSeries("Ib", "ib", true);
                createSeries("Ic", "ic", true);
            } else if (warningType == CONS.WARNING_TYPE.HE_SO_CONG_SUAT_THAP) {
                createSeries("PFa", "pfa", true);
                createSeries("PFb", "pfb", true);
                createSeries("PFc", "pfc", true);
            } else if (warningType == CONS.WARNING_TYPE.QUA_TAI) {
                createSeries("Ia", "ia", true);
                createSeries("Ib", "ib", true);
                createSeries("Ic", "ic", true);
            } else if (CONS.WARNING_TYPE.TAN_SO_THAP.includes(warningType)) {
                createSeries("F", "f", true);
            } else if (CONS.WARNING_TYPE.TAN_SO_CAO.includes(warningType)) {
                createSeries("F", "f", true);
            } else if (warningType == CONS.WARNING_TYPE.MAT_DIEN_TONG) {
                createSeries("Uan", "uan", true);
                createSeries("Ubn", "ubn", true);
                createSeries("Ucn", "ucn", true);
            } else if (warningType == CONS.WARNING_TYPE.NGUOC_PHA) {
                createSeries("PFa", "pfa", true);
                createSeries("PFb", "pfb", true);
                createSeries("PFc", "pfc", true);
            } else if (CONS.WARNING_TYPE.SONG_HAI.includes(warningType)) {
                if (warningType == 111) {
                    createSeries("TH_dIa", "thdIa", true);
                    createSeries("TH_dIb", "thdIb", true);
                    createSeries("TH_dIc", "thdIc", true);
                }
                if (warningType == 109) {

                    // Thêm các phần tử vào container
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VAN_H${i}`;
                        const valueName = `van_H${i}`;
                        createSeries(baseName, valueName, true);
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VBN_H${i}`;
                        const valueName = `vbn_H${i}`;
                        createSeries(baseName, valueName, true);
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `VCN_H${i}`;
                        const valueName = `vcn_H${i}`;
                        createSeries(baseName, valueName, true);
                    }
                }
                if (warningType == 108) {

                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IA_H${i}`;
                        const valueName = `ia_H${i}`;
                        createSeriesN(baseName, valueName, true);
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IB_H${i}`;
                        const valueName = `ib_H${i}`;
                        createSeriesN(baseName, valueName, true);
                    }
                    for (let i = 2; i <= 31; i++) {
                        const baseName = `IC_H${i}`;
                        const valueName = `ic_H${i}`;
                        createSeriesN(baseName, valueName, true);
                    }
                }
                if (warningType == 110) {
                    createSeries("TH_dVan", "thdVan", true);
                    createSeries("TH_dVbn", "thdVbn", true);
                    createSeries("TH_dVcn", "thdVcn", true);
                }
            }

            //thiết bị cảm biến nhiệt độ - độ ẩm
            if (CONS.WARNING_TYPE.NHIET_DO_CAO.includes(warningType)) {
                createSeries("T", "t", true);
            } else if (CONS.WARNING_TYPE.NHIET_DO_THAP.includes(warningType)) {
                createSeries("T", "t", true);
            } else if (CONS.WARNING_TYPE.DO_AM.includes(warningType)) {
                createSeries("Humidity", "h", true);
            }
            var legend = chart.children.push(
                am5.Legend.new(root, {
                    centerX: am5.p50,
                    x: am5.p50
                })
            );

            // Make series change state when legend item is hovered
            legend.itemContainers.template.states.create("hover", {});

            legend.itemContainers.template.events.on("pointerover", function (e) {
                e.target.dataItem.dataContext.hover();
            });
            legend.itemContainers.template.events.on("pointerout", function (e) {
                e.target.dataItem.dataContext.unhover();
            });

            legend.data.setAll(chart.series.values);

            // Make stuff animate on load
            // https://www.amcharts.com/docs/v5/concepts/animations/
            chart.appear(1000, 100);

        });
    };
    // const thElements = document.querySelectorAll(".table-container table thead tr th");

    const funcTableOrChart = (e) => {
        setTableOrChart(e)
    }

    useEffect(() => {
        document.title = "Cảnh báo"
        getWarning(fromDate, toDate);
        // if (location.state) {
        //     setNotification(location.state);
        // };
    }, [customerId, projectId, systemTypeId, fromDate, toDate]);

    return (
        <div className="mt-2">
            <div>
                {/* <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div> */}
                <div className="">
                    <div className="input-group p-1">
                        <div className="input-group-prepend float-left" style={{ zIndex: 0 }}>
                            <button className="btn btn-outline-secondary" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#0a1a5c" : "#e9ecef" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-calendar.svg" style={{ height: "18px" }}></img>
                            </button>
                            <button className="btn btn-outline-secondary btn-time" title="Kiểu xem" type="button" style={{ backgroundColor: isActiveButton ? "#e9ecef" : "#0a1a5c" }} onClick={() => handleChangeView(isActiveButton)}>
                                <img src="/resources/image/icon-play.svg" style={{ height: "18px" }}></img>
                            </button>
                        </div>
                        {!isActiveButton && (
                            <div className="input-group float-left mr-1 select-calendar" style={{ width: "100px", marginLeft: 10, height: 34 }}>
                                <select className="form-control select-value"
                                    //onChange={(e) => handleChangeChartType(e.target.value)}
                                    style={{ backgroundColor: "#0a1a5c", borderRadius: 5, border: "1px solid #FFA87D", color: "white" }}
                                    title="Chi tiết"
                                    onChange={onChangeValue}
                                >
                                    <option className="value" key={1} value={1}>Hôm nay</option>
                                    <option className="value" key={2} value={2}>Hôm qua</option>
                                    <option className="value" key={3} value={3}>Tháng này</option>
                                    <option className="value" key={4} value={4}>Tháng trước</option>
                                    <option className="value" key={5} value={5}>3 Tháng trước</option>
                                    <option className="value" key={6} value={6}>6 Tháng trước</option>
                                    <option className="value" key={7} value={7}>Năm nay</option>
                                    <option className="value" key={8} value={8}>Năm trước</option>
                                </select>
                            </div>
                        )}
                        {isActiveButton && (
                            <div className="input-group float-left mr-1 select-time" title="Chi tiết" style={{ width: "300px", marginLeft: 10, height: 34 }}>
                                <button className="form-control button-calendar" readOnly data-toggle="modal" data-target={"#modal-calendar"} style={{ backgroundColor: "#ffffff", border: "1px solid #0A1A5C" }}>
                                    {moment(fromDate).format("YYYY-MM-DD") + " - " + moment(toDate).format("YYYY-MM-DD")}
                                </button>
                                <div className="input-group-append" style={{ zIndex: 0 }}>
                                    <button className="btn button-infor" type="button" data-toggle="modal" data-target={"#modal-calendar"} style={{ fontWeight: "bold", height: 34 }}>......</button>
                                </div>
                            </div>
                        )}
                        <div className="modal fade" id="modal-calendar" tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div className="modal-dialog" role="document">
                                <div className="modal-content">
                                    <div className="modal-header" style={{ backgroundColor: "#0a1a5c", height: "44px", color: "white" }}>
                                        <h5 style={{ color: "white" }}>CALENDAR</h5>
                                        <button style={{ color: "#fff" }} type="button" className="close" data-dismiss="modal" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                    <div className="modal-body">
                                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                            <h5>Từ ngày</h5>
                                            <Calendar
                                                id="from-value"
                                                className="celendar-picker"
                                                dateFormat="yy-mm-dd"
                                                maxDate={(new Date)}
                                                value={fromDate}
                                                onChange={e => setFromDate(e.value)}
                                            />
                                            <div className="input-group-prepend background-ses">
                                                <span className="input-group-text pickericon">
                                                    <span className="far fa-calendar"></span>
                                                </span>
                                            </div>
                                        </div>
                                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                                            <h5>Đến ngày</h5>
                                            <Calendar
                                                id="to-value"
                                                className="celendar-picker"
                                                dateFormat="yy-mm-dd"
                                                maxDate={(new Date)}
                                                value={toDate}
                                                onChange={e => setToDate(e.value)}
                                            />
                                            <div className="input-group-prepend background-ses">
                                                <span className="input-group-text pickericon" >
                                                    <span className="far fa-calendar"></span>
                                                </span>
                                            </div>
                                        </div>
                                        <div className="input-group float-left mr-1">
                                        </div>
                                    </div>
                                    <div className="modal-footer">
                                        <button type="button" className="btn btn-secondary" data-dismiss="modal">Đóng</button>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <div className="">
                    <div className="warning-search-device">
                        <input className="warning-search-device-input" type="text" placeholder={t('content.home_page.search')} onChange={searchDevice}></input>
                        <i className="fas fa-solid fa-search position-absolute" style={{ color: "#333", left: "90%", top: "35%" }}></i>
                    </div>
                    <div className="loading" id="warning-loading" style={{ marginTop: "", marginLeft: "45%" }}>
                        <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                    </div>
                    <div id="warnedDevices">
                        {searchWarnedDevice.length > 0 ?
                            <>
                                {searchWarnedDevice.map((item, index) => (
                                    <div key={index} className="" style={{ height: "100%" }}>
                                        <div className="">
                                            <div>
                                                <div className="title" style={{ marginBottom: "10px" }}></div>
                                                <div className="priority pl-1" style={{ marginBottom: "10px", fontWeight: "bold" }}>
                                                    <label>Mức ưu tiên thiết bị: Cao</label>

                                                </div>
                                                <div className="table-container">
                                                    <table className="table">
                                                        <thead >
                                                            <tr height="40px">
                                                                <th width="30px">STT</th>
                                                                <th width="400px">
                                                                    Loại cảnh báo
                                                                </th>
                                                                <th width="200px">
                                                                    Thời gian bắt đầu
                                                                </th>
                                                                <th width="200px">
                                                                    Thời gian kết thúc
                                                                </th>
                                                                <th width="100px">
                                                                    Số lần
                                                                </th>
                                                                <th width="100px">
                                                                    Mức cảnh báo
                                                                </th>
                                                            </tr>
                                                        </thead>

                                                        <tbody style={{ lineHeight: 1 }} >
                                                            {
                                                                item.listWarning.map((warning, i) => (
                                                                    <tr key={i} height="30px" data-toggle="modal" data-target="#infor-warning-modal-lg" onClick={() => {
                                                                        if (warning.warningType === 108 || warning.warningType === 109) {
                                                                            funcInforWarningFrame2(warning);
                                                                        } else {
                                                                            funcInforWarning(warning);
                                                                        }
                                                                        // funcInforWarning(warning);
                                                                    }}>
                                                                        <td className="text-center">{i + 1}</td>
                                                                        <td className="text-left">{warning.warningTypeName}</td>
                                                                        <td className="text-center" >{warning.fromDate}</td>
                                                                        <td className="text-center" >{warning.toDate}</td>
                                                                        <td className="text-center" >{warning.total}</td>
                                                                        <td className="text-center" >
                                                                            {warning.warningLevel == 1 && <div className="level1"></div>}
                                                                            {warning.warningLevel == 2 && <div className="level2"></div>}
                                                                            {warning.warningLevel == 3 && <div className="level3"></div>}
                                                                        </td>

                                                                    </tr>
                                                                ))
                                                            }
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))
                                }
                            </>
                            :
                            <div className="text-center loading-chart mt-1">{t('content.home_page.chart.no_data')}</div>
                        }
                    </div>
                </div>
            </div>
            <div className="modal fade bd-example-modal-lg" id="infor-warning-modal-lg" tabIndex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-xl" style={{ maxWidth: "1500px", maxHeight: "800px" }}>
                    <div className="modal-content" style={{ height: '700px' }} >
                        <div className="left-warning">
                            <div className="infor" >
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Tên điểm đo: &nbsp;</label>{inforWarning.deviceName} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Loại cảnh báo: &nbsp;</label>{inforWarning.warningTypeName} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Giá trị cảnh báo: &nbsp;</label>{inforWarning.value} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Ngưỡng cảnh báo: &nbsp;</label>{inforWarning.settingValue.toString()} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Mức độ quan trọng của điểm đo: &nbsp;</label> -<br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Mức ưu tiên của cảnh báo: &nbsp;</label>
                                {inforWarning.warningLevel == 1 && "Thấp"}
                                {inforWarning.warningLevel == 2 && "Trung bình"}
                                {inforWarning.warningLevel == 3 && "Cao"}<br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Thời điểm bắt đầu: &nbsp;</label>{moment(inforWarning.fromDate).format("YYYY-MM-DD HH:mm:ss")} <br />
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Thời điểm kết thúc: &nbsp;</label>{moment(inforWarning.toDate).format("YYYY-MM-DD HH:mm:ss")}  <br />
                                {/* <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>    <label>Khoảng thời gian: &nbsp;</label>{moment(inforWarning.toDate).diff(moment(inforWarning.fromDate), 'days')} ngày */}
                                <i className="fa-solid fa-circle fa-2xs"> &nbsp;</i>
                                <label>Khoảng thời gian: &nbsp;</label>
                                {(() => {
                                    const fromDate = moment(inforWarning.fromDate);
                                    const toDate = moment(inforWarning.toDate);
                                    const durationInHours = toDate.diff(fromDate, 'hours');

                                    if (durationInHours >= 24) {
                                        const days = Math.floor(durationInHours / 24);
                                        return `${days} ngày `;
                                    } else {
                                        const hours = Math.floor(durationInHours);
                                        const minutes = Math.floor((durationInHours % 1) * 60);
                                        return `${hours} giờ ${minutes} phút`;
                                    }
                                })()}

                            </div>
                        </div>
                        <div className="right-warning" style={{ height: '700px' }}>
                            <div className="chart" style={{ overflow: "auto" }}>
                                <div className="text-right p-1" style={{ height: "fit-content" }}>
                                    <button className="btn" onClick={() => funcTableOrChart(1)} hidden={tableOrChart == 0 ? false : true} style={{ border: "1px solid #F37021" }}>
                                        <i className="fas fa-solid fa-bars" style={{ color: "var(--ses-orange-100-color)" }} ></i>
                                    </button>
                                    <button className="btn" onClick={() => funcTableOrChart(0)} hidden={tableOrChart == 1 ? false : true} style={{ border: "1px solid #F37021" }}>
                                        <i className="fas fa-solid fa-chart-line" style={{ color: "var(--ses-orange-100-color)" }}></i>
                                    </button>
                                    <button type="button" className="btn btn-dashboard ml-1" style={{ border: "1px solid #F37021" }} onClick={onDownload}>
                                        <i className="fas fa-solid fa-download fa-1x" style={{ color: "var(--ses-orange-100-color)" }}></i>
                                    </button>

                                </div>
                                {(selectedWarningType == 109 || selectedWarningType == 108) ? <div id="chartdivWarning" style={{ height: "1000px", width: "100%" }} hidden={tableOrChart == 0 ? false : true}>
                                </div> :
                                    <div id="chartdivWarning" style={{ height: "95%", width: "100%" }} hidden={tableOrChart == 0 ? false : true}>
                                    </div>}


                                <div id="chartdivWarning" style={{ height: "95%", width: "100%" }} class="table-container" hidden={tableOrChart == 1 ? false : true}  >
                                    {(selectedWarningType !== 109 || selectedWarningType !== 108) &&
                                        <table id="myTable" className="table" style={{ width: "100%" }} ref={tableRef}>

                                            <thead>
                                                <tr>
                                                    {(selectedWarningType == 109 || selectedWarningType == 108) ?
                                                        null :
                                                        <>
                                                            <th width="50px">TT</th>
                                                            <th style={{ minWidth: "200px" }} >THỜI GIAN</th>
                                                        </>
                                                    }

                                                    {(selectedWarningType == 101 || selectedWarningType == 102 || selectedWarningType == 117) &&
                                                        <>
                                                            <th>UAn</th>
                                                            <th>UBn</th>
                                                            <th>Ucn</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 103 &&
                                                        <>
                                                            <th>T1</th>
                                                            <th>T2</th>
                                                            <th>T3</th>
                                                        </>
                                                    }
                                                    {(selectedWarningType == 104 || selectedWarningType == 110) &&
                                                        <>
                                                            <th>PFA</th>
                                                            <th>PFB</th>
                                                            <th>PFC</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 107 &&
                                                        <>
                                                            <th>Ia</th>
                                                            <th>Ib</th>
                                                            <th>Ic</th>
                                                        </>
                                                    }
                                                    {(selectedWarningType == 106 || selectedWarningType == 105) &&
                                                        <>
                                                            <th>F</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 111 &&
                                                        <>
                                                            <th>THD_Ia</th>
                                                            <th>THD_Ib</th>
                                                            <th>THD_Ic</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 112 &&
                                                        <>
                                                            <th>THD_Van</th>
                                                            <th>THD_Vbn</th>
                                                            <th>THD_Vcn</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 117 &&
                                                        <>
                                                            <th>In</th>
                                                        </>
                                                    }
                                                    {selectedWarningType == 117 &&
                                                        <>
                                                            <th>In</th>
                                                        </>
                                                    }

                                                </tr>
                                            </thead>

                                            <tbody>
                                                {(selectedWarningType !== 108 && selectedWarningType !== 109) &&
                                                    listWarning?.map((item, index) => (
                                                        <tr key={index}>
                                                            <td>{index + 1}</td>
                                                            <td style={{ textAlign: "center" }}>{item.viewTime}</td>

                                                            {(selectedWarningType == 101 || selectedWarningType == 102 || selectedWarningType == 117) &&
                                                                <>
                                                                    <td className="text-right">{item.uan}</td>
                                                                    <td className="text-right">{item.ubn}</td>
                                                                    <td className="text-right">{item.ucn}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 103 &&
                                                                <>
                                                                    <td>{item.t1}</td>
                                                                    <td>{item.t2}</td>
                                                                    <td>{item.t3}</td>
                                                                </>
                                                            }
                                                            {(selectedWarningType == 104 || selectedWarningType == 110) &&
                                                                <>
                                                                    <td>{item.pfa}</td>
                                                                    <td>{item.pfb}</td>
                                                                    <td>{item.pfc}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 107 &&
                                                                <>
                                                                    <td>{item.ia}</td>
                                                                    <td>{item.ib}</td>
                                                                    <td>{item.ic}</td>
                                                                </>
                                                            }
                                                            {(selectedWarningType == 106 || selectedWarningType == 105) &&
                                                                <>
                                                                    <td>{item.f}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 111 &&
                                                                <>
                                                                    <td>{item.thdIa}</td>
                                                                    <td>{item.thdIb}</td>
                                                                    <td>{item.thdIb}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 112 &&
                                                                <>
                                                                    <td>{item.thdVan}</td>
                                                                    <td>{item.thdVbn}</td>
                                                                    <td>{item.thdVcn}</td>
                                                                </>
                                                            }
                                                            {selectedWarningType == 117 &&
                                                                <>
                                                                    <td>{item.ia}</td>
                                                                </>
                                                            }
                                                        </tr>

                                                    ))
                                                }
                                            </tbody>

                                        </table>

                                    }
                                    {(selectedWarningType == 109 || selectedWarningType == 108) &&
                                        <table id="myTable" className="table" style={{ width: "auto" }} ref={tableRef}>
                                            <thead>
                                                <tr>
                                                    <th width="50px">TT</th>
                                                    <th style={{ minWidth: "200px" }} >THỜI GIAN</th>

                                                    {selectedWarningType == 108 &&
                                                        <>
                                                            <th>IA_H1</th>
                                                            <th>IA_H2</th>
                                                            <th>IA_H3</th>
                                                            <th>IA_H4</th>
                                                            <th>IA_H5</th>
                                                            <th>IA_H6</th>
                                                            <th>IA_H7</th>
                                                            <th>IA_H8</th>
                                                            <th>IA_H9</th>
                                                            <th>IA_H10</th>
                                                            <th>IA_H11</th>
                                                            <th>IA_H12</th>
                                                            <th>IA_H13</th>
                                                            <th>IA_H14</th>
                                                            <th>IA_H15</th>
                                                            <th>IA_H16</th>
                                                            <th>IA_H17</th>
                                                            <th>IA_H18</th>
                                                            <th>IA_H19</th>
                                                            <th>IA_H20</th>
                                                            <th>IA_H21</th>
                                                            <th>IA_H22</th>
                                                            <th>IA_H23</th>
                                                            <th>IA_H24</th>
                                                            <th>IA_H25</th>
                                                            <th>IA_H26</th>
                                                            <th>IA_H27</th>
                                                            <th>IA_H28</th>
                                                            <th>IA_H29</th>
                                                            <th>IA_H30</th>
                                                            <th>IA_H31</th>
                                                            <th>IB_H1</th>
                                                            <th>IB_H2</th>
                                                            <th>IB_H3</th>
                                                            <th>IB_H4</th>
                                                            <th>IB_H5</th>
                                                            <th>IB_H6</th>
                                                            <th>IB_H7</th>
                                                            <th>IB_H8</th>
                                                            <th>IB_H9</th>
                                                            <th>IB_H10</th>
                                                            <th>IB_H11</th>
                                                            <th>IB_H12</th>
                                                            <th>IB_H13</th>
                                                            <th>IB_H14</th>
                                                            <th>IB_H15</th>
                                                            <th>IB_H16</th>
                                                            <th>IB_H17</th>
                                                            <th>IB_H18</th>
                                                            <th>IB_H19</th>
                                                            <th>IB_H20</th>
                                                            <th>IB_H21</th>
                                                            <th>IB_H22</th>
                                                            <th>IB_H23</th>
                                                            <th>IB_H24</th>
                                                            <th>IB_H25</th>
                                                            <th>IB_H26</th>
                                                            <th>IB_H27</th>
                                                            <th>IB_H28</th>
                                                            <th>IB_H29</th>
                                                            <th>IB_H30</th>
                                                            <th>IB_H31</th>
                                                            <th>IC_H1</th>
                                                            <th>IC_H2</th>
                                                            <th>IC_H3</th>
                                                            <th>IC_H4</th>
                                                            <th>IC_H5</th>
                                                            <th>IC_H6</th>
                                                            <th>IC_H7</th>
                                                            <th>IC_H8</th>
                                                            <th>IC_H9</th>
                                                            <th>IC_H10</th>
                                                            <th>IC_H11</th>
                                                            <th>IC_H12</th>
                                                            <th>IC_H13</th>
                                                            <th>IC_H14</th>
                                                            <th>IC_H15</th>
                                                            <th>IC_H16</th>
                                                            <th>IC_H17</th>
                                                            <th>IC_H18</th>
                                                            <th>IC_H19</th>
                                                            <th>IC_H20</th>
                                                            <th>IC_H21</th>
                                                            <th>IC_H22</th>
                                                            <th>IC_H23</th>
                                                            <th>IC_H24</th>
                                                            <th>IC_H25</th>
                                                            <th>IC_H26</th>
                                                            <th>IC_H27</th>
                                                            <th>IC_H28</th>
                                                            <th>IC_H29</th>
                                                            <th>IC_H30</th>
                                                            <th>IC_H31</th>

                                                        </>
                                                    }
                                                    {selectedWarningType == 109 &&
                                                        <>
                                                            <th>VAN_H1</th>
                                                            <th>VAN_H2</th>
                                                            <th>VAN_H3</th>
                                                            <th>VAN_H4</th>
                                                            <th>VAN_H5</th>
                                                            <th>VAN_H6</th>
                                                            <th>VAN_H7</th>
                                                            <th>VAN_H8</th>
                                                            <th>VAN_H9</th>
                                                            <th>VAN_H10</th>
                                                            <th>VAN_H11</th>
                                                            <th>VAN_H12</th>
                                                            <th>VAN_H13</th>
                                                            <th>VAN_H14</th>
                                                            <th>VAN_H15</th>
                                                            <th>VAN_H16</th>
                                                            <th>VAN_H17</th>
                                                            <th>VAN_H18</th>
                                                            <th>VAN_H19</th>
                                                            <th>VAN_H20</th>
                                                            <th>VAN_H21</th>
                                                            <th>VAN_H22</th>
                                                            <th>VAN_H23</th>
                                                            <th>VAN_H24</th>
                                                            <th>VAN_H25</th>
                                                            <th>VAN_H26</th>
                                                            <th>VAN_H27</th>
                                                            <th>VAN_H28</th>
                                                            <th>VAN_H29</th>
                                                            <th>VAN_H30</th>
                                                            <th>VAN_H31</th>
                                                            <th>VBN_H1</th>
                                                            <th>VBN_H2</th>
                                                            <th>VBN_H3</th>
                                                            <th>VBN_H4</th>
                                                            <th>VBN_H5</th>
                                                            <th>VBN_H6</th>
                                                            <th>VBN_H7</th>
                                                            <th>VBN_H8</th>
                                                            <th>VBN_H9</th>
                                                            <th>VBN_H10</th>
                                                            <th>VBN_H11</th>
                                                            <th>VBN_H12</th>
                                                            <th>VBN_H13</th>
                                                            <th>VBN_H14</th>
                                                            <th>VBN_H15</th>
                                                            <th>VBN_H16</th>
                                                            <th>VBN_H17</th>
                                                            <th>VBN_H18</th>
                                                            <th>VBN_H19</th>
                                                            <th>VBN_H20</th>
                                                            <th>VBN_H21</th>
                                                            <th>VBN_H22</th>
                                                            <th>VBN_H23</th>
                                                            <th>VBN_H24</th>
                                                            <th>VBN_H25</th>
                                                            <th>VBN_H26</th>
                                                            <th>VBN_H27</th>
                                                            <th>VBN_H28</th>
                                                            <th>VBN_H29</th>
                                                            <th>VBN_H30</th>
                                                            <th>VBN_H31</th>
                                                            <th>VCN_H1</th>
                                                            <th>VCN_H2</th>
                                                            <th>VCN_H3</th>
                                                            <th>VCN_H4</th>
                                                            <th>VCN_H5</th>
                                                            <th>VCN_H6</th>
                                                            <th>VCN_H7</th>
                                                            <th>VCN_H8</th>
                                                            <th>VCN_H9</th>
                                                            <th>VCN_H10</th>
                                                            <th>VCN_H11</th>
                                                            <th>VCN_H12</th>
                                                            <th>VCN_H13</th>
                                                            <th>VCN_H14</th>
                                                            <th>VCN_H15</th>
                                                            <th>VCN_H16</th>
                                                            <th>VCN_H17</th>
                                                            <th>VCN_H18</th>
                                                            <th>VCN_H19</th>
                                                            <th>VCN_H20</th>
                                                            <th>VCN_H21</th>
                                                            <th>VCN_H22</th>
                                                            <th>VCN_H23</th>
                                                            <th>VCN_H24</th>
                                                            <th>VCN_H25</th>
                                                            <th>VCN_H26</th>
                                                            <th>VCN_H27</th>
                                                            <th>VCN_H28</th>
                                                            <th>VCN_H29</th>
                                                            <th>VCN_H30</th>
                                                            <th>VCN_H31</th>
                                                        </>

                                                    }
                                                </tr>
                                            </thead>
                                            <tbody>

                                                {

                                                    listWarningFrame2?.map((item, index) => (
                                                        <tr key={index}>
                                                            <td>{index + 1}</td>

                                                            <td style={{ textAlign: "center" }}>{moment(item.viewTime).format("YYYY-MM-DD HH:mm:ss")}</td>

                                                            {selectedWarningType == 109 &&
                                                                <>
                                                                    <td>{item.van_H1}</td>
                                                                    <td>{item.van_H2}</td>
                                                                    <td>{item.van_H3}</td>
                                                                    <td>{item.van_H4}</td>
                                                                    <td>{item.van_H5}</td>
                                                                    <td>{item.van_H6}</td>
                                                                    <td>{item.van_H7}</td>
                                                                    <td>{item.van_H8}</td>
                                                                    <td>{item.van_H9}</td>
                                                                    <td>{item.van_H10}</td>
                                                                    <td>{item.van_H11}</td>
                                                                    <td>{item.van_H12}</td>
                                                                    <td>{item.van_H13}</td>
                                                                    <td>{item.van_H14}</td>
                                                                    <td>{item.van_H15}</td>
                                                                    <td>{item.van_H16}</td>
                                                                    <td>{item.van_H17}</td>
                                                                    <td>{item.van_H18}</td>
                                                                    <td>{item.van_H19}</td>
                                                                    <td>{item.van_H20}</td>
                                                                    <td>{item.van_H21}</td>
                                                                    <td>{item.van_H22}</td>
                                                                    <td>{item.van_H23}</td>
                                                                    <td>{item.van_H24}</td>
                                                                    <td>{item.van_H25}</td>
                                                                    <td>{item.van_H26}</td>
                                                                    <td>{item.van_H27}</td>
                                                                    <td>{item.van_H28}</td>
                                                                    <td>{item.van_H29}</td>
                                                                    <td>{item.van_H30}</td>
                                                                    <td>{item.van_H31}</td>
                                                                    <td>{item.vbn_H1}</td>
                                                                    <td>{item.vbn_H2}</td>
                                                                    <td>{item.vbn_H3}</td>
                                                                    <td>{item.vbn_H4}</td>
                                                                    <td>{item.vbn_H5}</td>
                                                                    <td>{item.vbn_H6}</td>
                                                                    <td>{item.vbn_H7}</td>
                                                                    <td>{item.vbn_H8}</td>
                                                                    <td>{item.vbn_H9}</td>
                                                                    <td>{item.vbn_H10}</td>
                                                                    <td>{item.vbn_H11}</td>
                                                                    <td>{item.vbn_H12}</td>
                                                                    <td>{item.vbn_H13}</td>
                                                                    <td>{item.vbn_H14}</td>
                                                                    <td>{item.vbn_H15}</td>
                                                                    <td>{item.vbn_H16}</td>
                                                                    <td>{item.vbn_H17}</td>
                                                                    <td>{item.vbn_H18}</td>
                                                                    <td>{item.vbn_H19}</td>
                                                                    <td>{item.vbn_H20}</td>
                                                                    <td>{item.vbn_H21}</td>
                                                                    <td>{item.vbn_H22}</td>
                                                                    <td>{item.vbn_H23}</td>
                                                                    <td>{item.vbn_H24}</td>
                                                                    <td>{item.vbn_H25}</td>
                                                                    <td>{item.vbn_H26}</td>
                                                                    <td>{item.vbn_H27}</td>
                                                                    <td>{item.vbn_H28}</td>
                                                                    <td>{item.vbn_H29}</td>
                                                                    <td>{item.vbn_H30}</td>
                                                                    <td>{item.vbn_H31}</td>
                                                                    <td>{item.vcn_H1}</td>
                                                                    <td>{item.vcn_H2}</td>
                                                                    <td>{item.vcn_H3}</td>
                                                                    <td>{item.vcn_H4}</td>
                                                                    <td>{item.vcn_H5}</td>
                                                                    <td>{item.vcn_H6}</td>
                                                                    <td>{item.vcn_H7}</td>
                                                                    <td>{item.vcn_H8}</td>
                                                                    <td>{item.vcn_H9}</td>
                                                                    <td>{item.vcn_H10}</td>
                                                                    <td>{item.vcn_H11}</td>
                                                                    <td>{item.vcn_H12}</td>
                                                                    <td>{item.vcn_H13}</td>
                                                                    <td>{item.vcn_H14}</td>
                                                                    <td>{item.vcn_H15}</td>
                                                                    <td>{item.vcn_H16}</td>
                                                                    <td>{item.vcn_H17}</td>
                                                                    <td>{item.vcn_H18}</td>
                                                                    <td>{item.vcn_H19}</td>
                                                                    <td>{item.vcn_H20}</td>
                                                                    <td>{item.vcn_H21}</td>
                                                                    <td>{item.vcn_H22}</td>
                                                                    <td>{item.vcn_H23}</td>
                                                                    <td>{item.vcn_H24}</td>
                                                                    <td>{item.vcn_H25}</td>
                                                                    <td>{item.vcn_H26}</td>
                                                                    <td>{item.vcn_H27}</td>
                                                                    <td>{item.vcn_H28}</td>
                                                                    <td>{item.vcn_H29}</td>
                                                                    <td>{item.vcn_H30}</td>
                                                                    <td>{item.vcn_H31}</td>

                                                                </>
                                                            }
                                                            {selectedWarningType == 108 &&
                                                                <>
                                                                    <td>{item.ia_H1}</td>
                                                                    <td>{item.ia_H2}</td>
                                                                    <td>{item.ia_H3}</td>
                                                                    <td>{item.ia_H4}</td>
                                                                    <td>{item.ia_H5}</td>
                                                                    <td>{item.ia_H6}</td>
                                                                    <td>{item.ia_H7}</td>
                                                                    <td>{item.ia_H8}</td>
                                                                    <td>{item.ia_H9}</td>
                                                                    <td>{item.ia_H10}</td>
                                                                    <td>{item.ia_H11}</td>
                                                                    <td>{item.ia_H12}</td>
                                                                    <td>{item.ia_H13}</td>
                                                                    <td>{item.ia_H14}</td>
                                                                    <td>{item.ia_H15}</td>
                                                                    <td>{item.ia_H16}</td>
                                                                    <td>{item.ia_H17}</td>
                                                                    <td>{item.ia_H18}</td>
                                                                    <td>{item.ia_H19}</td>
                                                                    <td>{item.ia_H20}</td>
                                                                    <td>{item.ia_H21}</td>
                                                                    <td>{item.ia_H22}</td>
                                                                    <td>{item.ia_H23}</td>
                                                                    <td>{item.ia_H24}</td>
                                                                    <td>{item.ia_H25}</td>
                                                                    <td>{item.ia_H26}</td>
                                                                    <td>{item.ia_H27}</td>
                                                                    <td>{item.ia_H28}</td>
                                                                    <td>{item.ia_H29}</td>
                                                                    <td>{item.ia_H30}</td>
                                                                    <td>{item.ia_H31}</td>
                                                                    <td>{item.ib_H1}</td>
                                                                    <td>{item.ib_H2}</td>
                                                                    <td>{item.ib_H3}</td>
                                                                    <td>{item.ib_H4}</td>
                                                                    <td>{item.ib_H5}</td>
                                                                    <td>{item.ib_H6}</td>
                                                                    <td>{item.ib_H7}</td>
                                                                    <td>{item.ib_H8}</td>
                                                                    <td>{item.ib_H9}</td>
                                                                    <td>{item.ib_H10}</td>
                                                                    <td>{item.ib_H11}</td>
                                                                    <td>{item.ib_H12}</td>
                                                                    <td>{item.ib_H13}</td>
                                                                    <td>{item.ib_H14}</td>
                                                                    <td>{item.ib_H15}</td>
                                                                    <td>{item.ib_H16}</td>
                                                                    <td>{item.ib_H17}</td>
                                                                    <td>{item.ib_H18}</td>
                                                                    <td>{item.ib_H19}</td>
                                                                    <td>{item.ib_H20}</td>
                                                                    <td>{item.ib_H21}</td>
                                                                    <td>{item.ib_H22}</td>
                                                                    <td>{item.ib_H23}</td>
                                                                    <td>{item.ib_H24}</td>
                                                                    <td>{item.ib_H25}</td>
                                                                    <td>{item.ib_H26}</td>
                                                                    <td>{item.ib_H27}</td>
                                                                    <td>{item.ib_H28}</td>
                                                                    <td>{item.ib_H29}</td>
                                                                    <td>{item.ib_H30}</td>
                                                                    <td>{item.ib_H31}</td>
                                                                    <td>{item.ic_H1}</td>
                                                                    <td>{item.ic_H2}</td>
                                                                    <td>{item.ic_H3}</td>
                                                                    <td>{item.ic_H4}</td>
                                                                    <td>{item.ic_H5}</td>
                                                                    <td>{item.ic_H6}</td>
                                                                    <td>{item.ic_H7}</td>
                                                                    <td>{item.ic_H8}</td>
                                                                    <td>{item.ic_H9}</td>
                                                                    <td>{item.ic_H10}</td>
                                                                    <td>{item.ic_H11}</td>
                                                                    <td>{item.ic_H12}</td>
                                                                    <td>{item.ic_H13}</td>
                                                                    <td>{item.ic_H14}</td>
                                                                    <td>{item.ic_H15}</td>
                                                                    <td>{item.ic_H16}</td>
                                                                    <td>{item.ic_H17}</td>
                                                                    <td>{item.ic_H18}</td>
                                                                    <td>{item.ic_H19}</td>
                                                                    <td>{item.ic_H20}</td>
                                                                    <td>{item.ic_H21}</td>
                                                                    <td>{item.ic_H22}</td>
                                                                    <td>{item.ic_H23}</td>
                                                                    <td>{item.ic_H24}</td>
                                                                    <td>{item.ic_H25}</td>
                                                                    <td>{item.ic_H26}</td>
                                                                    <td>{item.ic_H27}</td>
                                                                    <td>{item.ic_H28}</td>
                                                                    <td>{item.ic_H29}</td>
                                                                    <td>{item.ic_H30}</td>
                                                                    <td>{item.ic_H31}</td>

                                                                </>
                                                            }
                                                        </tr>
                                                    ))
                                                }

                                            </tbody>
                                        </table>
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div >
    )
}

export default WarningLoadDevice;