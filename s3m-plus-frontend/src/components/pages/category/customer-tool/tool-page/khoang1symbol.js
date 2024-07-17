import COMMON from "./common";
import CONSTANTS from "./constant";
import ARROW from "./arrow";
import METER from "./meter";
import MBA from "./mba";
import RMU from "./rmu";
import FEEDER from "./feeder";
import MC from "./mc";
import MCHOR from "./mchor";
import ULTILITY from "./ultility";
import LABEL from "./label";
import BUSBAR from "./busbar";
import BUSBARVER from "./busbarver";
import STMV from "./stmv";
import SGMV from "./sgmv";
import { DATA, g_connect, setAction, outFocus, showProperties } from "./data";
import * as d3 from "d3";
import INVERTER from "./inverter";
import INVERTERSYMBOL from "./invertersymbol";
import COMBINER from "./combiner";
import COMBINERSYMBOL from "./combinersymbol";
import PANEL from "./panel";
import PANELSYMBOL from "./panelsymbol";
import STRING from "./string";
import STRINGSYMBOL from "./stringsymbol";
import WEATHER from "./weather";
import WEATHERSYMBOL from "./weathersymbol";
import UPS from "./ups";
import KHOANG1 from "./khoang1";
import KHOANGCAP from "./khoangcap";
import KHOANGCAPSYMBOL from "./khoangcapsymbol";
import KHOANGCHI from "./khoangchi";
import KHOANGDODEM from "./khoangdodem";
import KHOANGDODEMSYMBOL from "./khoangdodemsymbol";
import KHOANGMAYCAT from "./khoangmaycat";
import KHOANGMAYCATSYMBOL from "./khoangmaycatsymbol";
import KHOANGTHANHCAI from "./khoangthanhcai";
import KHOANGTHANHCAISYMBOL from "./khoangthanhcaisymbol";
import { getOperationInformation } from './../../../home/solar/systemMap/index';

const $ = window.$;

let KHOANG1SYMBOL = {
    ID: "idkhgsb-",
    ID_CIRCLE_CONNECT: "khgsbc",
    ID_CONNECT_TOP_MARKER: "khgsbct-",
    ID_CONNECT_LEFT_MARKER: "khgsbcl-",
    ID_CONNECT_RIGHT_MARKER: "khgsbcr-",
    ID_CONNECT_BOTTOM_MARKER: "khgsbcb-",

    ID_PATH_1_KHOANG1SYMBOL: "ip1khgsb-",
    ID_PATH_2_KHOANG1SYMBOL: "ip2khgsb-",
    ID_REMOVE: "irmkhgsb-",

    DEFAULT_WIDTH: 178,
    DEFAULT_HEIGHT: 178,

    directArrow: {},

    collect: function () {
        var _deviceName = document.getElementById("khoang1-symbol-device-name").value;
        var _description = document.getElementById("khoang1-symbol-description").value;

        var _khoang1symbol = {
            "id": Date.now(),
            "deviceName": _deviceName,
            "description": _description,
            "type": "khoang1symbol",
        };
        return _khoang1symbol;
    },

    create: function () {
        var _oCard = KHOANG1SYMBOL.collect();
        DATA.aCards.push(_oCard);
        KHOANG1SYMBOL.draw(_oCard);
    },

    drawLoad: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (KHOANG1SYMBOL.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        var _path1 = _gCard.datum(card).append("path")
            .attr("id", function (d) { return (KHOANG1SYMBOL.ID_PATH_1_KHOANG1SYMBOL + d.deviceId); })
            .attr("d", CONSTANTS.KHOANGDOLUONGSYMBOL.path1)
            .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,13.72549%,14.117647%);fill-opacity:1;");

        var _gTagName = _gCard.datum(card).append('g')
            .attr("transform", "translate(-46,177) rotate(270)");

        var _svgTagName = _gTagName.append('svg')
            .attr("width", "200");

        var _gDeviceName = _gCard.datum(card).append('g')
            .attr("transform", "translate(-60,25)");

        var _svgDeviceName = _gDeviceName.append('svg')
            .attr("width", "300");

        var _deviceName = _svgDeviceName.datum(card).append("text")
            .attr("x", "50%")
            .attr("y", "25")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "11")
            .text(function (d) { return d.deviceName.length > 27 ? d.deviceName.substring(0, 25) + "..." : d.deviceName });

        var _deviceDescription = _svgTagName.datum(card).append("text")
            .attr("x", "50%")
            .attr("y", "40")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "13")
            .text(function (d) { return "" });

        var _rect = _gCard.datum(card).append("rect")
            .attr("x", "0")
            .attr("y", "0")
            .attr("width", "178")
            .attr("height", "178")
            .attr("fill", "transparent");
    },

    draw: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (KHOANG1SYMBOL.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            })
            .on("click", KHOANG1SYMBOL.select)
            .call(KHOANG1SYMBOL.drag);

        var _path1 = _gCard.datum(card).append("path")
            .attr("id", function (d) { return (KHOANG1SYMBOL.ID_PATH_1_KHOANG1SYMBOL + d.deviceId); })
            .attr("d", CONSTANTS.KHOANGDOLUONGSYMBOL.path1)
            .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,13.72549%,14.117647%);fill-opacity:1;");

        var _gDeviceName = _gCard.datum(card).append('g')
            .attr("transform", "translate(-60,25)");

        var _svgDeviceName = _gDeviceName.append('svg')
            .attr("width", "300");

        var _deviceName = _svgDeviceName.datum(card).append("text")
            .attr("x", "50%")
            .attr("y", "25")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "11")
            .text(function (d) { return d.deviceName.length > 27 ? d.deviceName.substring(0, 25) + "..." : d.deviceName });

        var _deviceDescription = _svgDeviceName.datum(card).append("text")
            .attr("x", "50%")
            .attr("y", "40")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "9")
            .text(function (d) { return "" });

        var _rect = _gCard.datum(card).append("rect")
            .attr("x", "0")
            .attr("y", "0")
            .attr("width", "178")
            .attr("height", "178")
            .attr("fill", "transparent");

        var _cardLeftMaker = _gCard.datum(card).append('circle')
            .attr("id", function (d) { return KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER + d.id; })
            .attr("class", "circle-connect")
            .attr("r", "7")
            .attr("transform", "translate(0,0)")
            .attr("cx", 0)
            .attr("cy", 31)
            .attr("opacity", 0.5)
            .attr("cursor", "pointer")
            .attr("marker", KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER)
            .attr("fill", "#237ba0")
            .call(KHOANG1SYMBOL.connect);

        var _cardRightMaker = _gCard.datum(card).append('circle')
            .attr("id", function (d) { return KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER + d.id; })
            .attr("class", "circle-connect")
            .attr("r", "7")
            .attr("transform", "translate(0,0)")
            .attr("cx", 178)
            .attr("cy", 31)
            .attr("opacity", 0.5)
            .attr("cursor", "pointer")
            .attr("marker", KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER)
            .attr("fill", "#237ba0")
            .call(KHOANG1SYMBOL.connect);

        var _gX = _gCard.datum(card).append("g")
            .attr("id", function (d) { return (KHOANG1SYMBOL.ID_REMOVE + d.id); })
            .attr("transform", "translate(-20,-40)")
            .attr("opacity", "1")
            .attr("cursor", "pointer")
            .on("click", KHOANG1SYMBOL.delete);

        var _lineX1 = _gX.datum(card).append("line")
            .attr("x1", "5")
            .attr("y1", "25")
            .attr("x2", "15")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");

        var _lineX2 = _gX.datum(card).append("line")
            .attr("x1", "15")
            .attr("y1", "25")
            .attr("x2", "5")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");
    },

    drag: d3.drag()
        .on("start", function (i, d) {
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            showProperties(25);
            KHOANG1SYMBOL.focus(DATA.selectedID);

            var _oCard = KHOANG1SYMBOL.get(d.id);
            document.getElementById("khoang1-symbol-device-name").value = _oCard.deviceName;
            document.getElementById("khoang1-symbol-description").value = _oCard.description;

            $("#btn-update").show();
            $("#btn-remove").show();

            d3.select(this).classed("active", true);
        })
        .on("drag", function (i, d) {
            var _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + d.id + "]");
            var _circleLeftMaker = d3.select("circle[id=" + KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER + d.id + "]");
            var _circleRightMaker = d3.select("circle[id=" + KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER + d.id + "]");

            d3.select(this).attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
            d.x = i.x;
            d.y = i.y;
            KHOANG1SYMBOL.moveArrows(d.id);

            d3.selectAll("g[id^=" + METER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    METER.focus(this.id.replace(METER.ID, ''));
                } else {
                    METER.outFocus(this.id.replace(METER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + MBA.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    MBA.focus(this.id.replace(MBA.ID, ''));
                } else {
                    MBA.outFocus(this.id.replace(MBA.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + RMU.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    RMU.focus(this.id.replace(RMU.ID, ''));
                } else {
                    RMU.outFocus(this.id.replace(RMU.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + FEEDER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    FEEDER.focus(this.id.replace(FEEDER.ID, ''));
                } else {
                    FEEDER.outFocus(this.id.replace(FEEDER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + MC.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    MC.focus(this.id.replace(MC.ID, ''));
                } else {
                    MC.outFocus(this.id.replace(MC.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + MCHOR.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    MCHOR.focus(this.id.replace(MCHOR.ID, ''));
                } else {
                    MCHOR.outFocus(this.id.replace(MCHOR.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + LABEL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    LABEL.focus(this.id.replace(LABEL.ID, ''));
                } else {
                    LABEL.outFocus(this.id.replace(LABEL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + ULTILITY.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    ULTILITY.focus(this.id.replace(ULTILITY.ID, ''));
                } else {
                    ULTILITY.outFocus(this.id.replace(ULTILITY.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + BUSBAR.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    BUSBAR.focus(this.id.replace(BUSBAR.ID, ''));
                } else {
                    BUSBAR.outFocus(this.id.replace(BUSBAR.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + BUSBARVER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    BUSBARVER.focus(this.id.replace(BUSBARVER.ID, ''));
                } else {
                    BUSBARVER.outFocus(this.id.replace(BUSBARVER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + STMV.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    STMV.focus(this.id.replace(STMV.ID, ''));
                } else {
                    STMV.outFocus(this.id.replace(STMV.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + SGMV.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    SGMV.focus(this.id.replace(SGMV.ID, ''));
                } else {
                    SGMV.outFocus(this.id.replace(SGMV.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + INVERTER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    INVERTER.focus(this.id.replace(INVERTER.ID, ''));
                } else {
                    INVERTER.outFocus(this.id.replace(INVERTER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + INVERTERSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    INVERTERSYMBOL.focus(this.id.replace(INVERTERSYMBOL.ID, ''));
                } else {
                    INVERTERSYMBOL.outFocus(this.id.replace(INVERTERSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + COMBINER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    COMBINER.focus(this.id.replace(COMBINER.ID, ''));
                } else {
                    COMBINER.outFocus(this.id.replace(COMBINER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + COMBINERSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    COMBINERSYMBOL.focus(this.id.replace(COMBINERSYMBOL.ID, ''));
                } else {
                    COMBINERSYMBOL.outFocus(this.id.replace(COMBINERSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + PANEL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    PANEL.focus(this.id.replace(PANEL.ID, ''));
                } else {
                    PANEL.outFocus(this.id.replace(PANEL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + PANELSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    PANELSYMBOL.focus(this.id.replace(PANELSYMBOL.ID, ''));
                } else {
                    PANELSYMBOL.outFocus(this.id.replace(PANELSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + STRING.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    STRING.focus(this.id.replace(STRING.ID, ''));
                } else {
                    STRING.outFocus(this.id.replace(STRING.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + STRINGSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    STRINGSYMBOL.focus(this.id.replace(STRINGSYMBOL.ID, ''));
                } else {
                    STRINGSYMBOL.outFocus(this.id.replace(STRINGSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + WEATHER.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    WEATHER.focus(this.id.replace(WEATHER.ID, ''));
                } else {
                    WEATHER.outFocus(this.id.replace(WEATHER.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + WEATHERSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    WEATHERSYMBOL.focus(this.id.replace(WEATHERSYMBOL.ID, ''));
                } else {
                    WEATHERSYMBOL.outFocus(this.id.replace(WEATHERSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + UPS.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    UPS.focus(this.id.replace(UPS.ID, ''));
                } else {
                    UPS.outFocus(this.id.replace(UPS.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANG1.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANG1.focus(this.id.replace(KHOANG1.ID, ''));
                } else {
                    KHOANG1.outFocus(this.id.replace(KHOANG1.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGCAP.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGCAP.focus(this.id.replace(KHOANGCAP.ID, ''));
                } else {
                    KHOANGCAP.outFocus(this.id.replace(KHOANGCAP.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGCAPSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGCAPSYMBOL.focus(this.id.replace(KHOANGCAPSYMBOL.ID, ''));
                } else {
                    KHOANGCAPSYMBOL.outFocus(this.id.replace(KHOANGCAPSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGCHI.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGCHI.focus(this.id.replace(KHOANGCHI.ID, ''));
                } else {
                    KHOANGCHI.outFocus(this.id.replace(KHOANGCHI.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGDODEM.focus(this.id.replace(KHOANGDODEM.ID, ''));
                } else {
                    KHOANGDODEM.outFocus(this.id.replace(KHOANGDODEM.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGDODEMSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGDODEMSYMBOL.focus(this.id.replace(KHOANGDODEMSYMBOL.ID, ''));
                } else {
                    KHOANGDODEMSYMBOL.outFocus(this.id.replace(KHOANGDODEMSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGMAYCAT.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGMAYCAT.focus(this.id.replace(KHOANGMAYCAT.ID, ''));
                } else {
                    KHOANGMAYCAT.outFocus(this.id.replace(KHOANGMAYCAT.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGMAYCATSYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGMAYCATSYMBOL.focus(this.id.replace(KHOANGMAYCATSYMBOL.ID, ''));
                } else {
                    KHOANGMAYCATSYMBOL.outFocus(this.id.replace(KHOANGMAYCATSYMBOL.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGTHANHCAI.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGTHANHCAI.focus(this.id.replace(KHOANGTHANHCAI.ID, ''));
                } else {
                    KHOANGTHANHCAI.outFocus(this.id.replace(KHOANGTHANHCAI.ID, ''));
                }
            });

            d3.selectAll("g[id^=" + KHOANGTHANHCAISYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANGTHANHCAISYMBOL.focus(this.id.replace(KHOANGTHANHCAISYMBOL.ID, ''));
                } else {
                    KHOANGTHANHCAISYMBOL.outFocus(this.id.replace(KHOANGTHANHCAISYMBOL.ID, ''));
                }
            });

            if (g_connect.cardId == "") {
                d3.selectAll("circle[id^=" + METER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "meter";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "mba";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "rmu";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "feeder";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "mc";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "mchor";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "label";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "ultility";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "busbar";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + BUSBARVER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "busbarver";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "stmv";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "sgmv";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "inverter";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "invertersymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "combiner";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "combinersymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "panel";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "panelsymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "string";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "stringsymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "weather";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "weathersymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "ups";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoang1";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangcap";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangcapsymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangchi";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangdodem";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangdodemsymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangmaycat";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangmaycatsymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangthanhcai";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });

                d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
                    var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
                    if (_isOverlapL || _isOverlapR) {
                        g_connect.id = this.id;
                        g_connect.cardId = this.id.split("-").pop();
                        g_connect.type = "khoangthanhcaisymbol";
                        if (_isOverlapL) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER;
                        } else if (_isOverlapR) {
                            g_connect.circleFrom = KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER;
                        };
                    };
                });
            };

            if (g_connect.cardId != "") {
                var _circleConnect = d3.select("circle[id=" + g_connect.id + "]");
                var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), _circleConnect.node());
                var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), _circleConnect.node());

                if (_isOverlapL || _isOverlapR) {
                    var _nCount = 0;
                    g_connect.attachFlag = true;
                    g_connect.attachMarker = $(this).attr("marker");
                    _nCount++;

                    if (_nCount == 0) {
                        g_connect.attachFlag = false;
                    };
                } else {
                    g_connect.cardId = "";
                    g_connect.attachFlag = false;
                }
            };
        })
        .on("end", function (i, d) {
            setAction("dragend");
            if (g_connect.attachFlag && g_connect.id != "") {
                var _circleConnect = d3.select("circle[id=" + g_connect.id + "]");
                var _circleFrom = d3.select("circle[id=" + g_connect.circleFrom + d.id + "]");
                var _cardTo = {};
                switch (g_connect.type) {
                    case "meter":
                        _cardTo = d3.select("g[id=" + METER.ID + g_connect.cardId + "]");
                        break;
                    case "mba":
                        _cardTo = d3.select("g[id=" + MBA.ID + g_connect.cardId + "]");
                        break;
                    case "rmu":
                        _cardTo = d3.select("g[id=" + RMU.ID + g_connect.cardId + "]");
                        break;
                    case "feeder":
                        _cardTo = d3.select("g[id=" + FEEDER.ID + g_connect.cardId + "]");
                        break;
                    case "mc":
                        _cardTo = d3.select("g[id=" + MC.ID + g_connect.cardId + "]");
                        break;
                    case "mchor":
                        _cardTo = d3.select("g[id=" + MCHOR.ID + g_connect.cardId + "]");
                        break;
                    case "label":
                        _cardTo = d3.select("g[id=" + LABEL.ID + g_connect.cardId + "]");
                        break;
                    case "ultility":
                        _cardTo = d3.select("g[id=" + ULTILITY.ID + g_connect.cardId + "]");
                        break;
                    case "busbar":
                        _cardTo = d3.select("g[id=" + BUSBAR.ID + g_connect.cardId + "]");
                        break;
                    case "busbarver":
                        _cardTo = d3.select("g[id=" + BUSBARVER.ID + g_connect.cardId + "]");
                        break;
                    case "stmv":
                        _cardTo = d3.select("g[id=" + STMV.ID + g_connect.cardId + "]");
                        break;
                    case "sgmv":
                        _cardTo = d3.select("g[id=" + SGMV.ID + g_connect.cardId + "]");
                        break;
                    case "inverter":
                        _cardTo = d3.select("g[id=" + INVERTER.ID + g_connect.cardId + "]");
                        break;
                    case "invertersymbol":
                        _cardTo = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "combiner":
                        _cardTo = d3.select("g[id=" + COMBINER.ID + g_connect.cardId + "]");
                        break;
                    case "combinersymbol":
                        _cardTo = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "panel":
                        _cardTo = d3.select("g[id=" + PANEL.ID + g_connect.cardId + "]");
                        break;
                    case "panelsymbol":
                        _cardTo = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "string":
                        _cardTo = d3.select("g[id=" + STRING.ID + g_connect.cardId + "]");
                        break;
                    case "stringsymbol":
                        _cardTo = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "weather":
                        _cardTo = d3.select("g[id=" + WEATHER.ID + g_connect.cardId + "]");
                        break;
                    case "WEATHERSYMBOL":
                        _cardTo = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "ups":
                        _cardTo = d3.select("g[id=" + UPS.ID + g_connect.cardId + "]");
                        break;
                    case "khoang1":
                        _cardTo = d3.select("g[id=" + KHOANG1.ID + g_connect.cardId + "]");
                        break;
                    case "khoangcap":
                        _cardTo = d3.select("g[id=" + KHOANGCAP.ID + g_connect.cardId + "]");
                        break;
                    case "khoangcapsymbol":
                        _cardTo = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "khoangchi":
                        _cardTo = d3.select("g[id=" + KHOANGCHI.ID + g_connect.cardId + "]");
                        break;
                    case "khoangdodem":
                        _cardTo = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.cardId + "]");
                        break;
                    case "khoangdodemsymbol":
                        _cardTo = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "khoangmaycat":
                        _cardTo = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.cardId + "]");
                        break;
                    case "khoangmaycatsymbol":
                        _cardTo = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "khoangthanhcai":
                        _cardTo = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.cardId + "]");
                        break;
                    case "khoangthanhcaisymbol":
                        _cardTo = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.cardId + "]");
                        break;
                };

                var xTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[0]) + Number(_circleConnect.attr("cx")) + Number(COMMON.getTranslation(_circleConnect.attr("transform"))[0]);
                var xFrom = xTo - Number(_circleFrom.attr("cx")) - Number(COMMON.getTranslation(_circleFrom.attr("transform"))[0]);

                var yTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[1]) + Number(_circleConnect.attr("cy")) + Number(COMMON.getTranslation(_circleConnect.attr("transform"))[1]);
                var yFrom = yTo - Number(_circleFrom.attr("cy")) - Number(COMMON.getTranslation(_circleFrom.attr("transform"))[1]);

                d3.select(this).attr("transform", "translate(" + (d.x = xFrom) + "," + (d.y = yFrom) + ")");

            };

            KHOANG1SYMBOL.moveArrows(d.id);
            g_connect.id = "";
            g_connect.connectId = "";
            g_connect.cardId = "";
            g_connect.attachFlag = false;
        }),

    connect: d3.drag()
        .on("start", function (i, d) {
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            showProperties(25);
            KHOANG1SYMBOL.focus(DATA.selectedID);

            var _oCard = KHOANG1SYMBOL.get(d.id);
            document.getElementById("khoang1-symbol-device-name").value = _oCard.deviceName;
            document.getElementById("khoang1-symbol-description").value = _oCard.description;

            $("#btn-update").show();
            $("#btn-remove").show();

            var _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + d.id + "]");

            var _attachMarker = d3.select("circle[id=" + this.id + "]");

            var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_attachMarker.attr("cx")) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[0]);
            var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_attachMarker.attr("cy")) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[1]);

            var _markerIdPrefix = this.id.replace(d.id, "");

            KHOANG1SYMBOL.directArrow = {
                "id": Date.now(),
                "x": _x,
                "y": _y,
                "x1": 0,
                "y1": 0,
                "x2": 0,
                "y2": 0,
                "x3": -100,
                "y3": 50,
                "stroke": "rgb(4.705882%,10.980392%,14.509804%)",
                "strokeWidth": ARROW.DEFAULT_STROKE_SIZE,
                "type": 1,
                "order": ARROW.DEFAULT_ORDER,
                "from": d.id,
                "fromMarker": _markerIdPrefix,
                "to": "",
                "toMarker": "",
                "fromType": KHOANG1SYMBOL.ID,
                "position": 1,
                "type": 1,
            };

            var g_svg = d3.select("#svg-container");

            var _gArrow = g_svg.datum(KHOANG1SYMBOL.directArrow).append('g')
                .attr("id", function (a) { return ARROW.ID + a.id; })
                .attr("transform", function (a) { return 'translate(' + a.x + ',' + a.y + ')'; })
                .datum({
                    x: function (a) { return a.x },
                    y: function (a) { return a.y }
                })
                .call(ARROW.drag);

            var _arrowLine = _gArrow.datum(KHOANG1SYMBOL.directArrow).append("line")
                .attr("id", function (a) { return (ARROW.ID_LINE + a.id); })
                .attr("x1", function (a) { return a.x1; })
                .attr("y1", function (a) { return a.y1; })
                .attr("x2", function (a) { return a.x2; })
                .attr("y2", function (a) { return a.y2; })
                .attr("stroke-width", function (a) { return a.strokeWidth; })
                .attr("stroke", function (a) { return a.stroke; })
                .attr("marker-end", "url(#arrow)")
                .on("click", ARROW.focus);

            var _arrowStartPoint = _gArrow.datum(KHOANG1SYMBOL.directArrow).append('circle')
                .attr("id", function (a) { return ARROW.ID_LINE_POINT_START + a.id; })
                .attr("class", "circle-connect")
                .attr("r", ARROW.MARKER_RADIUS)
                .attr("cx", function (a) { return a.x1; })
                .attr("cy", function (a) { return a.y1; })
                .attr("opacity", 0)
                .attr("cursor", "pointer")
                .attr("fill", ARROW.MARKER_CONNECTION_FILL)
                .call(ARROW.drag);

            var _arrowEndPoint = _gArrow.datum(KHOANG1SYMBOL.directArrow).append('circle')
                .attr("id", function (a) { return ARROW.ID_LINE_POINT_END + a.id; })
                .attr("class", "circle-connect")
                .attr("r", ARROW.MARKER_RADIUS)
                .attr("cx", function (a) { return a.x2; })
                .attr("cy", function (a) { return a.y2; })
                .attr("opacity", 0)
                .attr("cursor", "pointer")
                .attr("fill", ARROW.MARKER_CONNECTION_FILL)
                .call(ARROW.drag);
        })
        .on("drag", function (event, d) {
            var _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + d.id + "]");

            var _gArrow = d3.select("g[id=" + ARROW.ID + KHOANG1SYMBOL.directArrow.id + "]");
            var _arrowLine = d3.select("line[id=" + ARROW.ID_LINE + KHOANG1SYMBOL.directArrow.id + "]");
            var _arrowEndPoint = d3.select("circle[id=" + ARROW.ID_LINE_POINT_END + KHOANG1SYMBOL.directArrow.id + "]");
            var _g_svg = d3.select("#svg-container");
            var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);

            var _x = d3.pointer(event)[0];
            var _y = d3.pointer(event)[1];

            _x = (d3.pointer(event)[0] - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]);
            _y = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]);

            _arrowLine
                .attr("x2", _x)
                .attr("y2", _y)
                .attr("marker-end", "url(#arrow)");

            _arrowEndPoint
                .attr("cx", _x)
                .attr("cy", _y);

            if (g_connect.cardId == "") {
                d3.selectAll("g[id^=" + METER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "meter";
                    }
                });

                d3.selectAll("g[id^=" + MBA.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "mba";
                    }
                });

                d3.selectAll("g[id^=" + LABEL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "label";
                    }
                });

                d3.selectAll("g[id^=" + RMU.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "rmu";
                    }
                });

                d3.selectAll("g[id^=" + MC.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "mc";
                    }
                });

                d3.selectAll("g[id^=" + MCHOR.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "mchor";
                    }
                });

                d3.selectAll("g[id^=" + FEEDER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "feeder";
                    }
                });

                d3.selectAll("g[id^=" + ULTILITY.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "ultility";
                    }
                });

                d3.selectAll("g[id^=" + BUSBAR.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "busbar";
                    }
                });

                d3.selectAll("g[id^=" + BUSBARVER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "busbarver";
                    }
                });

                d3.selectAll("g[id^=" + STMV.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "stmv";
                    }
                });

                d3.selectAll("g[id^=" + SGMV.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "sgmv";
                    }
                });

                d3.selectAll("g[id^=" + INVERTER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "inverter";
                    }
                });

                d3.selectAll("g[id^=" + INVERTERSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "invertersymbol";
                    }
                });

                d3.selectAll("g[id^=" + COMBINER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "combiner";
                    }
                });

                d3.selectAll("g[id^=" + COMBINERSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "combinersymbol";
                    }
                });

                d3.selectAll("g[id^=" + PANEL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "panel";
                    }
                });

                d3.selectAll("g[id^=" + PANELSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "panelsymbol";
                    }
                });

                d3.selectAll("g[id^=" + STRING.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "string";
                    }
                });

                d3.selectAll("g[id^=" + STRINGSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "stringsymbol";
                    }
                });

                d3.selectAll("g[id^=" + WEATHER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "weather";
                    }
                });

                d3.selectAll("g[id^=" + WEATHERSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "weathersymbol";
                    }
                });

                d3.selectAll("g[id^=" + UPS.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "ups";
                    }
                });

                d3.selectAll("g[id^=" + KHOANG1.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoang1";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGCAP.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangcap";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGCAPSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangcapsymbol";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGCHI.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangchi";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangdodem";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGDODEMSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangdodemsymbol";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGMAYCAT.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangmaycat";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGMAYCATSYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangmaycatsymbol";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGTHANHCAI.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangthanhcai";
                    }
                });

                d3.selectAll("g[id^=" + KHOANGTHANHCAISYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoangthanhcaisymbol";
                    }
                });
            }

            if (g_connect.cardId != "") {
                if (g_connect.type == "meter") {
                    var _oCard = d3.select("g[id=" + METER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        METER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + METER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        METER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "mba") {
                    var _oCard = d3.select("g[id=" + MBA.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        MBA.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        MBA.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "label") {
                    var _oCard = d3.select("g[id=" + LABEL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        LABEL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        LABEL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "rmu") {
                    var _oCard = d3.select("g[id=" + RMU.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        RMU.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        RMU.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "mc") {
                    var _oCard = d3.select("g[id=" + MC.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        MC.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        MC.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "mchor") {
                    var _oCard = d3.select("g[id=" + MCHOR.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        MCHOR.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        MCHOR.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "feeder") {
                    var _oCard = d3.select("g[id=" + FEEDER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        FEEDER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        FEEDER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "ultility") {
                    var _oCard = d3.select("g[id=" + ULTILITY.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        ULTILITY.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        ULTILITY.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "busbar") {
                    var _oCard = d3.select("g[id=" + BUSBAR.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        BUSBAR.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        BUSBAR.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "busbarver") {
                    var _oCard = d3.select("g[id=" + BUSBARVER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        BUSBARVER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + BUSBARVER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        BUSBARVER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "stmv") {
                    var _oCard = d3.select("g[id=" + STMV.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        STMV.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        STMV.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "sgmv") {
                    var _oCard = d3.select("g[id=" + SGMV.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        SGMV.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        SGMV.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "inverter") {
                    var _oCard = d3.select("g[id=" + INVERTER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        INVERTER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        INVERTER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "invertersymbol") {
                    var _oCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        INVERTERSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        INVERTERSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "combiner") {
                    var _oCard = d3.select("g[id=" + COMBINER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        COMBINER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        COMBINER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "combinersymbol") {
                    var _oCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        COMBINERSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        COMBINERSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "panel") {
                    var _oCard = d3.select("g[id=" + PANEL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        PANEL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        PANEL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "panelsymbol") {
                    var _oCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        PANELSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        PANELSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "string") {
                    var _oCard = d3.select("g[id=" + STRING.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        STRING.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        STRING.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "stringsymbol") {
                    var _oCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        STRINGSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        STRINGSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "weather") {
                    var _oCard = d3.select("g[id=" + WEATHER.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        WEATHER.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        WEATHER.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "weathersymbol") {
                    var _oCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        WEATHERSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        WEATHERSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "ups") {
                    var _oCard = d3.select("g[id=" + UPS.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        UPS.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        UPS.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoang1") {
                    var _oCard = d3.select("g[id=" + KHOANG1.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANG1.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANG1.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangcap") {
                    var _oCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGCAP.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGCAP.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangcapsymbol") {
                    var _oCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGCAPSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGCAPSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangchi") {
                    var _oCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGCHI.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGCHI.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangdodem") {
                    var _oCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGDODEM.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGDODEM.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangdodemsymbol") {
                    var _oCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGDODEMSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGDODEMSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangmaycat") {
                    var _oCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGMAYCAT.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGMAYCAT.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangmaycatsymbol") {
                    var _oCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGMAYCATSYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGMAYCATSYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangthanhcai") {
                    var _oCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGTHANHCAI.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGTHANHCAI.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                } else if (g_connect.type == "khoangthanhcaisymbol") {
                    var _oCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANGTHANHCAISYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
                            if (COMMON.intersectRect(_arrowEndPoint.node(), this)) {
                                g_connect.attachFlag = true;
                                g_connect.id = m.id;
                                g_connect.attachMarker = $(this).attr("marker");
                                _nCount++;
                            }
                        });

                        if (_nCount == 0) {
                            g_connect.attachFlag = false;
                        }

                    } else {
                        KHOANGTHANHCAISYMBOL.outFocus(g_connect.cardId);
                        g_connect.cardId = "";
                        g_connect.attachFlag = false;
                    }
                }
            }
        })
        .on("end", function (i, d) {
            setAction("dragend");
            if (g_connect.attachFlag && g_connect.id != "") {
                var _gCard = {};
                if (g_connect.type == "meter") {
                    _gCard = d3.select("g[id=" + METER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = METER.ID;
                } else if (g_connect.type == "mba") {
                    _gCard = d3.select("g[id=" + MBA.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = MBA.ID;
                } else if (g_connect.type == "label") {
                    _gCard = d3.select("g[id=" + LABEL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = LABEL.ID;
                } else if (g_connect.type == "rmu") {
                    _gCard = d3.select("g[id=" + RMU.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = RMU.ID;
                } else if (g_connect.type == "mc") {
                    _gCard = d3.select("g[id=" + MC.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = MC.ID;
                } else if (g_connect.type == "mchor") {
                    _gCard = d3.select("g[id=" + MCHOR.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = MCHOR.ID;
                } else if (g_connect.type == "feeder") {
                    _gCard = d3.select("g[id=" + FEEDER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = FEEDER.ID;
                } else if (g_connect.type == "ultility") {
                    _gCard = d3.select("g[id=" + ULTILITY.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = ULTILITY.ID;
                } else if (g_connect.type == "busbar") {
                    _gCard = d3.select("g[id=" + BUSBAR.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = BUSBAR.ID;
                } else if (g_connect.type == "busbarver") {
                    _gCard = d3.select("g[id=" + BUSBARVER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = BUSBARVER.ID;
                } else if (g_connect.type == "stmv") {
                    _gCard = d3.select("g[id=" + STMV.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = STMV.ID;
                } else if (g_connect.type == "sgmv") {
                    _gCard = d3.select("g[id=" + SGMV.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = SGMV.ID;
                } else if (g_connect.type == "inverter") {
                    _gCard = d3.select("g[id=" + INVERTER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = INVERTER.ID;
                } else if (g_connect.type == "invertersymbol") {
                    _gCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = INVERTERSYMBOL.ID;
                } else if (g_connect.type == "combiner") {
                    _gCard = d3.select("g[id=" + COMBINER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = COMBINER.ID;
                } else if (g_connect.type == "combinersymbol") {
                    _gCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = COMBINERSYMBOL.ID;
                } else if (g_connect.type == "panel") {
                    _gCard = d3.select("g[id=" + PANEL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = PANEL.ID;
                } else if (g_connect.type == "panelsymbol") {
                    _gCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = PANELSYMBOL.ID;
                } else if (g_connect.type == "string") {
                    _gCard = d3.select("g[id=" + STRING.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = STRING.ID;
                } else if (g_connect.type == "stringsymbol") {
                    _gCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = STRINGSYMBOL.ID;
                } else if (g_connect.type == "weather") {
                    _gCard = d3.select("g[id=" + WEATHER.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = WEATHER.ID;
                } else if (g_connect.type == "weathersymbol") {
                    _gCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = WEATHERSYMBOL.ID;
                } else if (g_connect.type == "ups") {
                    _gCard = d3.select("g[id=" + UPS.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = UPS.ID;
                } else if (g_connect.type == "khoang1") {
                    _gCard = d3.select("g[id=" + KHOANG1.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANG1.ID;
                } else if (g_connect.type == "khoangcap") {
                    _gCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGCAP.ID;
                } else if (g_connect.type == "khoangcapsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGCAPSYMBOL.ID;
                } else if (g_connect.type == "khoangchi") {
                    _gCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGCHI.ID;
                } else if (g_connect.type == "khoangdodem") {
                    _gCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGDODEM.ID;
                } else if (g_connect.type == "khoangdodemsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGDODEMSYMBOL.ID;
                } else if (g_connect.type == "khoangmaycat") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGMAYCAT.ID;
                } else if (g_connect.type == "khoangmaycatsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGMAYCATSYMBOL.ID;
                } else if (g_connect.type == "khoangthanhcai") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGTHANHCAI.ID;
                } else if (g_connect.type == "khoangthanhcaisymbol") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.id + "]");
                    KHOANG1SYMBOL.directArrow.toType = KHOANGTHANHCAISYMBOL.ID;
                };

                var _attachMarker = d3.select("circle[id=" + g_connect.attachMarker + g_connect.id + "]");

                var _gArrow = d3.select("g[id=" + ARROW.ID + KHOANG1SYMBOL.directArrow.id + "]");
                var _arrowLine = d3.select("line[id=" + ARROW.ID_LINE + KHOANG1SYMBOL.directArrow.id + "]");
                var _arrowEndPoint = d3.select("circle[id=" + ARROW.ID_LINE_POINT_END + KHOANG1SYMBOL.directArrow.id + "]");

                var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_attachMarker.attr("cx")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[0]);
                var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_attachMarker.attr("cy")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[1]);

                _arrowLine
                    .attr("x2", _x)
                    .attr("y2", _y);

                _arrowEndPoint
                    .attr("cx", _x)
                    .attr("cy", _y);

                KHOANG1SYMBOL.directArrow.to = g_connect.id;
                KHOANG1SYMBOL.directArrow.toMarker = g_connect.attachMarker;
                KHOANG1SYMBOL.directArrow.x2 = _x;
                KHOANG1SYMBOL.directArrow.y2 = _y;
                DATA.aArrows.push(KHOANG1SYMBOL.directArrow);
                ARROW.update(KHOANG1SYMBOL.directArrow.id, 1);
            } else {
                d3.select("g[id=" + ARROW.ID + KHOANG1SYMBOL.directArrow.id + "]").remove();
            }

            g_connect.id = "";
            g_connect.cardId = "";
            g_connect.attachFlag = false;
        }),

    select: function (e, d) {
        e.preventDefault();
        e.stopPropagation();
        outFocus();
        DATA.selectedID = d.id;
        DATA.selectedType = 1;
        KHOANG1SYMBOL.focus(DATA.selectedID);
        showProperties(25);

        var _oCard = KHOANG1SYMBOL.get(d.id);
        document.getElementById("khoang1-symbol-device-name").value = _oCard.deviceName;
        document.getElementById("khoang1-symbol-description").value = _oCard.description;

        $("#btn-update").show();
        $("#btn-remove").show();
    },

    remove: function (id) {
        var isMarker = false;

        for (var i = 0; i < DATA.aArrows.length; i++) {
            if (id == DATA.aArrows[i].from || id == DATA.aArrows[i].to) {
                isMarker = true;
                break;
            } else {
                isMarker = false;
            }
        }
        if (!isMarker) {
            d3.select("g[id=" + KHOANG1SYMBOL.ID + id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != id;
            });

            DATA.selectedType = 1;
        }
    },

    focus: function (id) {
        if (id != null) {
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER + id + ']').attr("opacity", 1);
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER + id + ']').attr("opacity", 1);
            $('g[id^=' + KHOANG1SYMBOL.ID_REMOVE + id + ']').attr("opacity", 1);
        }
    },

    outFocus: function (id) {
        if (id != null) {
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER + id + ']').attr("opacity", 0);
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER + id + ']').attr("opacity", 0);
            $('g[id^=' + KHOANG1SYMBOL.ID_REMOVE + id + ']').attr("opacity", 0)
        } else {
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_LEFT_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + KHOANG1SYMBOL.ID_CONNECT_RIGHT_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('g[id^=' + KHOANG1SYMBOL.ID_REMOVE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
        }
    },

    moveArrows: function (id) {
        var _attachArrows = [];
        if (DATA.aArrows.length > 0) {
            $.each(DATA.aArrows, function (i, arrow) {
                if (arrow.from == id) {
                    _attachArrows.push(arrow.id + ":"
                        + ARROW.ID_LINE_POINT_START + arrow.id + ":" + arrow.fromMarker + arrow.from);
                }

                if (arrow.to == id) {
                    _attachArrows.push(arrow.id + ":"
                        + ARROW.ID_LINE_POINT_END + arrow.id + ":" + arrow.toMarker + arrow.to);
                }
            });
        }

        $.each(_attachArrows, function (i, data) {
            var _ids = data.split(":");

            var _oArrow = ARROW.get(_ids[0]);
            var _arrowLine = d3.select("line[id=" + ARROW.ID_LINE + _ids[0] + "]");
            var _arrowMarker = d3.select("circle[id=" + _ids[1] + "]");
            var _cardMarker = d3.select("circle[id=" + _ids[2] + "]");

            var _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + id + "]");
            var _gArrow = d3.select("g[id=" + ARROW.ID + _ids[0] + "]");

            var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_cardMarker.attr("cx")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]) + Number(COMMON.getTranslation(_cardMarker.attr("transform"))[0]);
            var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_cardMarker.attr("cy")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]) + Number(COMMON.getTranslation(_cardMarker.attr("transform"))[1]);

            _arrowMarker
                .attr("cx", _x)
                .attr("cy", _y);

            if (_ids[1].indexOf(ARROW.ID_LINE_POINT_START) >= 0) {
                _arrowLine
                    .attr("x1", _arrowMarker.attr("cx"))
                    .attr("y1", _arrowMarker.attr("cy"));
            } else if (_ids[1].indexOf(ARROW.ID_LINE_POINT_END) >= 0) {
                _arrowLine
                    .attr("x2", _arrowMarker.attr("cx"))
                    .attr("y2", _arrowMarker.attr("cy"));
            }
            ARROW.update(_ids[0]);
        });
    },

    update: function (id) {

        var _oCurrent = KHOANG1SYMBOL.get(id);

        if (typeof _oCurrent !== "undefined" && _oCurrent.type == "khoang1symbol") {
            var _deviceName = document.getElementById("khoang1-symbol-device-name").value;
            var _description = document.getElementById("khoang1-symbol-description").value;
            var _oCard = {
                "id": _oCurrent.id,
                "deviceName": _deviceName,
                "description": _description,
                "x": _oCurrent.x,
                "y": _oCurrent.y,
                "type": "khoang1symbol",
            }

            d3.select("g[id=" + KHOANG1SYMBOL.ID + id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != id;
            });

            DATA.aCards.push(_oCard);
            KHOANG1SYMBOL.draw(_oCard);
        }

    },

    delete: function (e) {
        e.stopPropagation();
        KHOANG1SYMBOL.remove(DATA.selectedID);
    },

    get: function (id) {
        var _oCard;
        $.each(DATA.aCards, function (i, card) {
            if (card.id == id) {
                _oCard = card;
                return false;
            }
        });

        return _oCard;
    },

};

export default KHOANG1SYMBOL;