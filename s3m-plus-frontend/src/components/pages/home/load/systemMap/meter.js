import CONSTANTS from "../../../category/customer-tool/tool-page/constant";
import * as d3 from "d3";
import { getOperationInformation } from "./index";
import converter from "../../../../../common/converter";

const $ = window.$;
let METER_LOAD = {
    ID: "gmtl",
    ID_STATUS_METER: "sim-",
    ID_PATH_METER: "pim-",
    ID_METER_U_VALUE: "iuv-",
    ID_METER_I_VALUE: "iiv-",
    ID_METER_P_VALUE: "ipv-",
    ID_METER_FS_VALUE: "ifsv-",
    ID_METER_T_VALUE: "itv-",
    ID_METER_TEMP_VALUE: "itempv-",
    ID_METER_HUMIDITY_VALUE: "ihumidityv-",
    ID_DETAIL: "idtl-",
    ID_DETAIL1: "idtl1-",
    ID_DETAIL2: "idtl2-",
    ID_FOCUS: "mtlfc",
    METER_U_TEXT: "U",
    METER_I_TEXT: "I (A)",
    METER_P_TEXT: "P",
    METER_T_TEXT: "T (°C)",
    ID_METER_LOAD_LEVEL_VALUE: "illv-",

    drawLoad: function (card, viewTypeModal) {

        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (METER_LOAD.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        if (card.meterType == 1) {
            var _rect = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
                .attr("x", "-10")
                .attr("y", "-10")
                .attr("width", "72")
                .attr("height", "70")
                .attr("fill", "transparent");
        } else if (card.meterType != 9) {
            var _rect = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
                .attr("x", "-5")
                .attr("y", "-5")
                .attr("width", "44")
                .attr("height", "44")
                .attr("fill", "transparent");
        }

        if (card.meterType == 1) {
            var _normalPath = _gCard.datum(card).append("path")
                .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
                .attr("d", CONSTANTS.METER.status)
                .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(255,255,255);fill-opacity:1;");
        } else if (card.meterType == 3) {
            var _normalPath = _gCard.datum(card).append("path")
                .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
                .attr("d", CONSTANTS.NHIETDO.status)
                .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(255,255,255);fill-opacity:1;");
        }

        if (card.meterType == 1) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.METER.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 9) {
            var _rect = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
                .attr("x", "-5")
                .attr("y", "-5")
                .attr("width", "60")
                .attr("height", "60")
                .attr("fill", "transparent");

            var _rectBackground = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
                .attr("x", "2")
                .attr("y", "2")
                .attr("width", "46")
                .attr("height", "46")
                .attr("fill", "transparent")
                .attr("cursor", "pointer")

            var _image = _gCard.datum(card).append("image")
                .attr("href", "/resources/image/components/gateway/gateway.svg")
                .attr("width", "50px")
                .attr("height", "50px")
        } else if (card.meterType == 4) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.TRANGTHAI.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 5) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.PHONGDIEN.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 7) {
            var _rect = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
                .attr("x", "-5")
                .attr("y", "-5")
                .attr("width", "60")
                .attr("height", "60")
                .attr("fill", "transparent");

            var _rectBackground = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
                .attr("x", "5")
                .attr("y", "5")
                .attr("width", "25")
                .attr("height", "25")
                .attr("fill", "transparent")
                .attr("cursor", "pointer")

            var _image = _gCard.datum(card).append("image")
                .attr("href", "/resources/image/components/cb-ap-suat/icon-cb-ap-suat.svg")
                .attr("width", "35")
                .attr("height", "35")

            var _valueP = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_P_VALUE + d.deviceId); })
                .attr("x", "45")
                .attr("y", "30")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");
        } else if (card.meterType == 10) {
            var _rect = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
                .attr("x", "-5")
                .attr("y", "-5")
                .attr("width", "60")
                .attr("height", "60")
                .attr("fill", "transparent");

            var _rectBackground = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
                .attr("x", "5")
                .attr("y", "5")
                .attr("width", "25")
                .attr("height", "25")
                .attr("fill", "transparent")
                .attr("cursor", "pointer")

            var _image = _gCard.datum(card).append("image")
                .attr("href", "/resources/image/components/cb-luu-luong/icon-cb-luu-luong.svg")
                .attr("width", "35")
                .attr("height", "35")

            var _valueFs = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_FS_VALUE + d.deviceId); })
                .attr("x", "45")
                .attr("y", "30")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueT = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_T_VALUE + d.deviceId); })
                .attr("x", "45")
                .attr("y", "48")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");
        } else if (card.meterType == 3) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.NHIETDO.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 11) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.BUCXA.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 12) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.MUCNUOC.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 13) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.PH.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 14) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.GIO.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 15) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.PIN.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        } else if (card.meterType == 19) {
            var _path1 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.NHIENLIEU.path1)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");

            var _path2 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.NHIENLIEU.path2)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");

            var _path3 = _gCard.datum(card).append("path")
                .attr("d", CONSTANTS.NHIENLIEU.path3)
                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
        }

        if (card.meterType == 1 || card.meterType == 9) {
            var _deviceName = _gCard.datum(card).append("text")
                .attr("x", "93")
                .attr("y", "10.5")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text(function (d) { return d.deviceName.length > 22 ? d.deviceName.substring(0, 20) + "..." : d.deviceName });
        } else {
            var _deviceName = _gCard.datum(card).append("text")
                .attr("x", "43")
                .attr("y", "10.5")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text(function (d) { return d.deviceName.length > 22 ? d.deviceName.substring(0, 20) + "..." : d.deviceName });
        }

        if (card.meterType == 1) {
            var _valueU = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_U_VALUE + d.deviceId); })
                .attr("x", "75")
                .attr("y", "30")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueI = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_I_VALUE + d.deviceId); })
                .attr("x", "75")
                .attr("y", "48")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueP = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_P_VALUE + d.deviceId); })
                .attr("x", "75")
                .attr("y", "67")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueLoadLevel = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_LOAD_LEVEL_VALUE + d.deviceId); })
                .attr("x", "75")
                .attr("y", "87")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueT = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_T_VALUE + d.deviceId); })
                .attr("x", "75")
                .attr("y", "87")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");
        }

        if (card.meterType == 3) {
            var _valueTemp = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_TEMP_VALUE + d.deviceId); })
                .attr("x", "43")
                .attr("y", "30")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");

            var _valueHumidity = _gCard.datum(card).append("text")
                .attr("id", function (d) { return (METER_LOAD.ID_METER_HUMIDITY_VALUE + d.deviceId); })
                .attr("x", "43")
                .attr("y", "48")
                .attr("fill", "black")
                .attr("font-size", "12")
                .text("");
        }
        if (card.deviceId != null && card.deviceId != '') {
            var _rect1 = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_DETAIL1 + d.id) })
                .attr("x", "0")
                .attr("y", "0")
                .attr("width", "50")
                .attr("height", "50")
                .attr("fill", "transparent")
                .attr("cursor", "pointer")
                .on("click", METER_LOAD.detail);
        } else {
            var _rect1 = _gCard.datum(card).append("rect")
                .attr("id", function (d) { return (METER_LOAD.ID_DETAIL1 + d.id) })
                .attr("x", "0")
                .attr("y", "0")
                .attr("width", "50")
                .attr("height", "50")
                .attr("fill", "transparent");
        }


        // var _rect2 = _gCard.datum(card).append("rect")
        //     .attr("id", function (d) { return (METER_LOAD.ID_DETAIL2 + d.id) })
        //     .attr("x", "100")
        //     .attr("y", "10")
        //     .attr("width", "130")
        //     .attr("height", "140")
        //     .attr("fill", "transparent")
        //     .attr("cursor", "pointer")
        //     .on("click", METER_LOAD.detail);

        var caculatorPosition = "translate(75, -28)";

        if (card.meterType != 1 && card.meterType != 9) {
            caculatorPosition = "translate(45, -28)";
        } else {
            var _gCaculator = _gCard.datum(card).append('g')
                .attr("transform", caculatorPosition);

            var _caculatorRect = _gCaculator.datum(card).append("rect")
                .attr("x", "0")
                .attr("y", "25")
                .attr("width", "13")
                .attr("height", "13")
                .attr("fill", "transparent")
                .attr('style', "stroke-width:1;stroke:rgb(4.705882%,10.980392%,14.509804%)");

            if (card.caculator == 1) {

                var _caculatorLine1 = _gCaculator.append("line")
                    .attr("x1", "3")
                    .attr("y1", "32")
                    .attr("x2", "7")
                    .attr("y2", "35")
                    .attr("stroke", "#276cb8")
                    .attr("style", "stroke-width:2");

                var _caculatorLine2 = _gCaculator.append("line")
                    .attr("x1", "6")
                    .attr("y1", "35")
                    .attr("x2", "10")
                    .attr("y2", "28")
                    .attr("stroke", "#276cb8")
                    .attr("style", "stroke-width:2");
            }
        }
    },

    detail: function (d, data) {
        if (window.location.href.includes("system-map")) {
            window.location.href = window.location.href.substring(0, window.location.href.indexOf("/", 1)) + "/" + data.customerId + "/" + "device-information" + "/" + data.deviceId;
        }
    },
}

export default METER_LOAD;