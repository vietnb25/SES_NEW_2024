import COMMON from "./common";
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
import { DATA, g_connect, setAction, outFocus, showProperties, getAction } from "./data";
import * as d3 from "d3";
import STMV from "./stmv";
import SGMV from "./sgmv";
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
import KHOANG1SYMBOL from "./khoang1symbol";
import KHOANGCAP from "./khoangcap";
import KHOANGCAPSYMBOL from "./khoangcapsymbol";
import KHOANGCHI from "./khoangchi";
import KHOANGDODEM from "./khoangdodem";
import KHOANGMAYCAT from "./khoangmaycat";
import KHOANGMAYCATSYMBOL from "./khoangmaycatsymbol";
import KHOANGTHANHCAI from "./khoangthanhcai";
import KHOANGTHANHCAISYMBOL from "./khoangthanhcaisymbol";
import KHOANGDODEMSYMBOL from "./khoangdodemsymbol";


const $ = window.$;

let ARROW2 = {
    ID: "air2-",
    ID_LINE: "ail2-",
    ID_LINE_CONNECTOR_1: "ac2-",
    ID_REMOVE: "arm2",

    DEFAULT_X: 20,
    DEFAULT_Y: 20,
    DEFAULT_ORDER: 1,
    DEFAULT_STROKE_SIZE: 4,
    DEFAULT_STROKE_FILL: "#D0CECE",

    ID_CIRCLE_CONNECT: "ap2",
    ID_LINE_POINT_START: "aps2-",
    ID_LINE_POINT_MIDDLE: "apm2-",
    ID_LINE_POINT_END: "ape2-",

    ID_ARROW_STYLE: "ars2-",
    ID_ARROW_TEXT: "art2-",
    ID_ARROW: "arrow-",

    MARKER_RADIUS: 6.8,
    MARKER_CONNECTION_FILL: "#237ba0",

    TYPE: 0,

    strokeColors: {
        '0': '#FFFFFF',
        '1': '#D0CECE',
        '2': '#276cb8',
        '3': '#FBE5D6',
        '4': '#FFFFFF',
        '5': '#D8D29E'
    },

    collect: function () {
        var _arrowPosition = parseInt($("input[type='radio'][name='arrow_position']:checked").val());
        var _arrowStroke = "rgb(4.705882%,10.980392%,14.509804%)";
        var _arrowStrokeWidth = 4;
        var _arrowStrokeDash = 0;

        var _arrowX = 200;
        var _arrowY = 100;

        var _x3 = -100;
        var _y3 = 50;

        var _arrow = {
            "id": Date.now(),
            "x": ARROW2.DEFAULT_X,
            "y": ARROW2.DEFAULT_Y,
            "x1": 0,
            "y1": 0,
            "x2": _arrowX,
            "y2": _arrowY,
            "x3": _x3,
            "y3": _y3,
            "stroke": _arrowStroke,
            "strokeWidth": _arrowStrokeWidth,
            "strokeDash": _arrowStrokeDash,
            "type": "arrow2",
            "from": "",
            "fromMarker": "",
            "to": "",
            "toMarker": "",
            "position": _arrowPosition,
            "componentType": "arrow"
        };

        return _arrow;
    },

    create: function () {
        var _oArrow = ARROW2.collect();
        DATA.aArrows.push(_oArrow);
        ARROW2.draw(_oArrow);
    },

    drawLoad: function (arrow) {
        var g_svg = d3.select("#svg-container");

        var _gArrow = g_svg.datum(arrow).append('g')
            .attr("id", function (d) { return ARROW2.ID + d.id; })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        var _arrowDefs = _gArrow.datum(arrow).append("marker")
            .attr("id", function (d) { return (ARROW2.ID_ARROW + d.id); })
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", "5")
            .attr("refY", "0")
            .attr("markerWidth", "4")
            .attr("markerHeight", "4")
            .attr("orient", "auto");

        var _arrowPath = _arrowDefs.datum(arrow).append("path")
            .attr("d", "M0,-5L10,0L0,5")
            .attr("style", "fill: 'black'; stroke: 'black'");

        var _arrowLine = _gArrow.datum(arrow).append("line")
            .attr("id", function (d) { return (ARROW2.ID_LINE + d.id); })
            .attr("x1", function (d) { return d.x1; })
            .attr("y1", function (d) { return d.y1; })
            .attr("x2", function (d) { return d.x2; })
            .attr("y2", function (d) { return d.y2; })
            .attr("stroke-width", function (d) { return d.strokeWidth; })
            .attr("stroke-dasharray", function (d) { return d.strokeDash; })
            .attr("stroke", function (d) { return d.stroke; })
            .attr("marker-end", function (d) { return `url(#` + ARROW2.ID_ARROW + d.id + `)` })
            .attr("transform", "translate(0,0)");

    },

    draw: function (arrow) {
        var g_svg = d3.select("#svg-container");
        console.log("arrow", arrow);
        var _gArrow = g_svg.datum(arrow).append('g')
            .attr("id", function (d) { return ARROW2.ID + d.id; })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            })
            .on("click", ARROW2.select)
            .call(ARROW2.drag);

        var _arrowDefs = _gArrow.datum(arrow).append("marker")
            .attr("id", function (d) { return (ARROW2.ID_ARROW + d.id); })
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", "5")
            .attr("refY", "0")
            .attr("markerWidth", "4")
            .attr("markerHeight", "4")
            .attr("orient", "auto");

        var _arrowPath = _arrowDefs.datum(arrow).append("path")
            .attr("d", "M0,-5L10,0L0,5")
            .attr("style", "fill: 'black'; stroke: 'black'");

        var _arrowLine = _gArrow.datum(arrow).append("line")
            .attr("id", function (d) { return (ARROW2.ID_LINE + d.id); })
            .attr("x1", function (d) { return d.x1; })
            .attr("y1", function (d) { return d.y1; })
            .attr("x2", function (d) { return d.x2; })
            .attr("y2", function (d) { return d.y2; })
            .attr("stroke-width", function (d) { return d.strokeWidth; })
            .attr("stroke-dasharray", function (d) { return d.strokeDash; })
            .attr("stroke", function (d) { return d.stroke; })
            .attr("marker-end", function (d) { return `url(#` + ARROW2.ID_ARROW + d.id + `)` })
            .attr("transform", "translate(0,0)");

        var _arrowStartPoint = _gArrow.datum(arrow).append('circle')
            .attr("id", function (d) { return ARROW2.ID_LINE_POINT_START + d.id; })
            .attr("class", "circle-connect")
            .attr("r", ARROW2.MARKER_RADIUS)
            .attr("cx", function (d) { return d.x1; })
            .attr("cy", function (d) { return d.y1; })
            .attr("opacity", 0)
            .attr("cursor", "pointer")
            .attr("transform", "translate(0,0)")
            .attr("fill", ARROW2.MARKER_CONNECTION_FILL)
            .attr("marker", ARROW2.ID_LINE_POINT_START)
            .call(ARROW2.drag);

        var _arrowEndPoint = _gArrow.datum(arrow).append('circle')
            .attr("id", function (d) { return ARROW2.ID_LINE_POINT_END + d.id; })
            .attr("class", "circle-connect")
            .attr("r", ARROW2.MARKER_RADIUS)
            .attr("cx", function (d) { return d.x2; })
            .attr("cy", function (d) { return d.y2; })
            .attr("opacity", 0)
            .attr("cursor", "pointer")
            .attr("transform", "translate(0,0)")
            .attr("fill", ARROW2.MARKER_CONNECTION_FILL)
            .attr("marker", ARROW2.ID_LINE_POINT_END)
            .call(ARROW2.drag);

        var _gX = _gArrow.datum(arrow).append("g")
            .attr("id", function (d) { return (ARROW2.ID_REMOVE + d.id); })
            .attr("transform", function (d) { return 'translate(' + (Number(d.x1) - 20) + ',' + (Number(d.y1) - 40) + ')'; })
            .attr("opacity", "0")
            .attr("cursor", "pointer")
            .on("click", ARROW2.delete);

        var _lineX1 = _gX.datum(arrow).append("line")
            .attr("x1", "5")
            .attr("y1", "25")
            .attr("x2", "15")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");

        var _lineX2 = _gX.datum(arrow).append("line")
            .attr("x1", "15")
            .attr("y1", "25")
            .attr("x2", "5")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");

    },

    drag: d3.drag()
        .on("start", function (i, d) {
            setAction("");
            outFocus();
            DATA.selectedID = d.id;

            ARROW2.focus(d.id);

            var _oArrow = ARROW2.get(d.id);

            var _gArrow = d3.select("g[id=" + ARROW2.ID + d.id + "]");

            DATA.oldX = Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]);
            DATA.oldY = Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]);

            $("#btn-update").show();
            $("#btn-remove").show();
        })
        .on("drag", function dragged(event, d) {
            var _oArrow = ARROW2.get(d.id);
            var _arrowMarker = d3.select(this);

            ARROW2.moveArrows(d.id);

            DATA.selectedID = d.id;
            DATA.selectedType = 2;

            var _gArrow = d3.select("g[id=" + ARROW2.ID + d.id + "]");
            var _attachMarker = d3.select("circle[id=" + this.id + "]");
            var _arrowLine = d3.select("line[id=" + ARROW2.ID_LINE + d.id + "]");

            var _arrowStartPoint = d3.select("circle[id=" + ARROW2.ID_LINE_POINT_START + d.id + "]");
            var _arrowMiddlePoint = d3.select("circle[id=" + ARROW2.ID_LINE_POINT_MIDDLE + d.id + "]");
            var _arrowEndPoint = d3.select("circle[id=" + ARROW2.ID_LINE_POINT_END + d.id + "]");
            var _garrowRemove = d3.select("g[id=" + ARROW2.ID_REMOVE + d.id + "]");
            var _g_svg = d3.select("#svg-container");

            var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
            var _x = d3.pointer(event)[0];
            var _y = d3.pointer(event)[1];

            if (this.id.indexOf(ARROW2.ID_LINE_POINT_START) >= 0) {
                _x = (d3.pointer(event)[0] - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]);
                _y = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]);
                _garrowRemove.attr("transform", 'translate(' + (Number(_x) - 20) + ',' + (Number(_y) - 40) + ')');

                _arrowLine
                    .attr("x1", _x)
                    .attr("y1", _y);

            } else if (this.id.indexOf(ARROW2.ID_LINE_POINT_END) >= 0) {
                _x = (d3.pointer(event)[0] - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]);
                _y = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]);

                _arrowLine
                    .attr("x2", _x)
                    .attr("y2", _y);

            } else if (this.id.indexOf(ARROW2.ID_LINE_POINT_MIDDLE) >= 0) {
                _x = (d3.pointer(event)[0] - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]);
                _y = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]);

            } else {
                d3.select(this).attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
                d.x = event.x;
                d.y = event.y;

                DATA.x = Number(event.x);
                DATA.y = Number(event.y);
            }

            _arrowMarker.attr("cx", _x);
            _arrowMarker.attr("cy", _y);


            if (this.id.indexOf(ARROW2.ID_LINE_POINT_START) >= 0) {
                if (g_connect.cardId == "") {
                    d3.selectAll("circle[id^=" + ARROW2.ID_CIRCLE_CONNECT + "]").each(function (e) {
                        if (d.id != e.id) {
                            var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                            if (_isOverlap) {
                                g_connect.cardId = e.id;
                                g_connect.type = "arrow";
                            }
                        }
                    });

                    d3.selectAll("g[id^=" + METER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "meter";
                        }
                    });

                    d3.selectAll("g[id^=" + MBA.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mba";
                        }
                    });

                    d3.selectAll("g[id^=" + RMU.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "rmu";
                        }
                    });

                    d3.selectAll("g[id^=" + FEEDER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "feeder";
                        }
                    });

                    d3.selectAll("g[id^=" + MC.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mc";
                        }
                    });

                    d3.selectAll("g[id^=" + MCHOR.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mchor";
                        }
                    });

                    d3.selectAll("g[id^=" + LABEL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "label";
                        }
                    });

                    d3.selectAll("g[id^=" + ULTILITY.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "ultility";
                        }
                    });

                    d3.selectAll("g[id^=" + BUSBAR.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "busbar";
                        }
                    });

                    d3.selectAll("g[id^=" + BUSBARVER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "busbarver";
                        }
                    });

                    d3.selectAll("g[id^=" + STMV.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "stmv";
                        }
                    });

                    d3.selectAll("g[id^=" + SGMV.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "sgmv";
                        }
                    });

                    d3.selectAll("g[id^=" + INVERTER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "inverter";
                        }
                    });

                    d3.selectAll("g[id^=" + INVERTERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "invertersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + COMBINER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "combiner";
                        }
                    });

                    d3.selectAll("g[id^=" + COMBINERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "combinersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + PANEL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "panel";
                        }
                    });

                    d3.selectAll("g[id^=" + PANELSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "panelsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + STRING.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "string";
                        }
                    });

                    d3.selectAll("g[id^=" + STRINGSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "stringsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + WEATHER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "weather";
                        }
                    });

                    d3.selectAll("g[id^=" + WEATHERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "weathersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + UPS.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "ups";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANG1.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoang1";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANG1SYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoang1symbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCAP.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangcap";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCAPSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangcapsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCHI.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangchi";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangdodem";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGDODEMSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangdodemsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGMAYCAT.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangmaycat";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGMAYCATSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangmaycatsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGTHANHCAI.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangthanhcai";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGTHANHCAISYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangthanhcaisymbol";
                        }
                    });
                }

                if (g_connect.cardId != "") {
                    if (g_connect.type == "arrow") {
                        var _oCard = d3.select("g[id=" + ARROW2.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            ARROW2.focus(g_connect.cardId);
                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + ARROW2.ID_CIRCLE_CONNECT + "]").each(function (e) {
                                if (d.id != e.id) {
                                    if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                        g_connect.attachFlag = true;
                                        g_connect.id = e.id;
                                        g_connect.attachMarker = $(this).attr("marker");
                                        g_connect.attachFrom = true;
                                        _nCount++;
                                    }
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            ARROW2.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "meter") {
                        var _oCard = d3.select("g[id=" + METER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            METER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + METER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            METER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "mba") {
                        var _oCard = d3.select("g[id=" + MBA.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MBA.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            MBA.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "rmu") {
                        var _oCard = d3.select("g[id=" + RMU.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            RMU.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            RMU.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "feeder") {
                        var _oCard = d3.select("g[id=" + FEEDER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            FEEDER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            FEEDER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "mc") {
                        var _oCard = d3.select("g[id=" + MC.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MC.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            MC.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "mchor") {
                        var _oCard = d3.select("g[id=" + MCHOR.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MCHOR.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            MCHOR.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "label") {
                        var _oCard = d3.select("g[id=" + LABEL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            LABEL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            LABEL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "ultility") {
                        var _oCard = d3.select("g[id=" + ULTILITY.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            ULTILITY.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            ULTILITY.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "busbar") {
                        var _oCard = d3.select("g[id=" + BUSBAR.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            BUSBAR.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            BUSBAR.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "busbarver") {
                        var _oCard = d3.select("g[id=" + BUSBARVER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            BUSBARVER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + BUSBARVER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            BUSBARVER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "stmv") {
                        var _oCard = d3.select("g[id=" + STMV.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STMV.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            STMV.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "sgmv") {
                        var _oCard = d3.select("g[id=" + SGMV.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            SGMV.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            SGMV.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "inverter") {
                        var _oCard = d3.select("g[id=" + INVERTER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            INVERTER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            INVERTER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "invertersymbol") {
                        var _oCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            INVERTERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            INVERTERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "combiner") {
                        var _oCard = d3.select("g[id=" + COMBINER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            COMBINER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            COMBINER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "combinersymbol") {
                        var _oCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            COMBINERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            COMBINERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "panel") {
                        var _oCard = d3.select("g[id=" + PANEL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            PANEL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            PANEL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "panelsymbol") {
                        var _oCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            PANELSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            PANELSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "string") {
                        var _oCard = d3.select("g[id=" + STRING.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STRING.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            STRING.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "stringsymbol") {
                        var _oCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STRINGSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            STRINGSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "weather") {
                        var _oCard = d3.select("g[id=" + WEATHER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            WEATHER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            WEATHER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "weathersymbol") {
                        var _oCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            WEATHERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            WEATHERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "ups") {
                        var _oCard = d3.select("g[id=" + UPS.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            UPS.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            UPS.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoang1") {
                        var _oCard = d3.select("g[id=" + KHOANG1.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANG1.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANG1.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoang1symbol") {
                        var _oCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANG1SYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANG1SYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANG1SYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangcap") {
                        var _oCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCAP.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGCAP.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangcapsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCAPSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGCAPSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangchi") {
                        var _oCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCHI.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGCHI.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangdodem") {
                        var _oCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGDODEM.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGDODEM.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangdodemsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGDODEMSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGDODEMSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangmaycat") {
                        var _oCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGMAYCAT.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGMAYCAT.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangmaycatsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGMAYCATSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGMAYCATSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangthanhcai") {
                        var _oCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGTHANHCAI.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGTHANHCAI.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    } else if (g_connect.type == "khoangthanhcaisymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGTHANHCAISYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachFrom = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachFrom = false;
                            }

                        } else {
                            KHOANGTHANHCAISYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachFrom = false;
                        };
                    }
                }
            } else if (this.id.indexOf(ARROW2.ID_LINE_POINT_END) >= 0) {
                if (g_connect.cardId == "") {
                    d3.selectAll("circle[id^=" + ARROW2.ID_CIRCLE_CONNECT + "]").each(function (e) {
                        if (d.id != e.id) {
                            var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                            if (_isOverlap) {
                                g_connect.cardId = e.id;
                                g_connect.type = "arrow";
                            }
                        }
                    });

                    d3.selectAll("g[id^=" + METER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "meter";
                        }
                    });

                    d3.selectAll("g[id^=" + MBA.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mba";
                        }
                    });

                    d3.selectAll("g[id^=" + RMU.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "rmu";
                        }
                    });

                    d3.selectAll("g[id^=" + FEEDER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "feeder";
                        }
                    });

                    d3.selectAll("g[id^=" + MC.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mc";
                        }
                    });

                    d3.selectAll("g[id^=" + MCHOR.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "mchor";
                        }
                    });

                    d3.selectAll("g[id^=" + LABEL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "label";
                        }
                    });

                    d3.selectAll("g[id^=" + ULTILITY.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "ultility";
                        }
                    });

                    d3.selectAll("g[id^=" + BUSBAR.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "busbar";
                        }
                    });

                    d3.selectAll("g[id^=" + BUSBARVER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "busbarver";
                        }
                    });

                    d3.selectAll("g[id^=" + STMV.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "stmv";
                        }
                    });

                    d3.selectAll("g[id^=" + SGMV.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "sgmv";
                        }
                    });

                    d3.selectAll("g[id^=" + INVERTER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "inverter";
                        }
                    });

                    d3.selectAll("g[id^=" + INVERTERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "invertersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + COMBINER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "combiner";
                        }
                    });

                    d3.selectAll("g[id^=" + COMBINERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "combinersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + PANEL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "panel";
                        }
                    });

                    d3.selectAll("g[id^=" + PANELSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "panelsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + STRING.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "string";
                        }
                    });

                    d3.selectAll("g[id^=" + STRINGSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "strngsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + WEATHER.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "weather";
                        }
                    });

                    d3.selectAll("g[id^=" + WEATHERSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "weathersymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + UPS.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "ups";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANG1.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoang1";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANG1SYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoang1symbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCAP.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangcap";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCAPSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangcapsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGCHI.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangchi";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangdodem";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangdodemsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGMAYCAT.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangmaycat";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGMAYCATSYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangmaycatsymbol";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGTHANHCAI.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangthanhcai";
                        }
                    });

                    d3.selectAll("g[id^=" + KHOANGTHANHCAISYMBOL.ID + "]").each(function (d) {
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), this);
                        if (_isOverlap) {
                            g_connect.cardId = d.id;
                            g_connect.type = "khoangthanhcaisymbol";
                        }
                    });
                }

                if (g_connect.cardId != "") {
                    if (g_connect.type == "arrow") {
                        var _oCard = d3.select("g[id=" + ARROW2.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            ARROW2.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + ARROW2.ID_CIRCLE_CONNECT + "]").each(function (e) {
                                if (d.id != e.id) {
                                    if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                        g_connect.attachFlag = true;
                                        g_connect.id = e.id;
                                        g_connect.attachMarker = $(this).attr("marker");
                                        g_connect.attachTo = true;
                                        _nCount++;
                                    }
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            ARROW2.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "meter") {
                        var _oCard = d3.select("g[id=" + METER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            METER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + METER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            METER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "mba") {
                        var _oCard = d3.select("g[id=" + MBA.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MBA.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            MBA.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "rmu") {
                        var _oCard = d3.select("g[id=" + RMU.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            RMU.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            RMU.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "feeder") {
                        var _oCard = d3.select("g[id=" + FEEDER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            FEEDER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            FEEDER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "mc") {
                        var _oCard = d3.select("g[id=" + MC.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MC.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            MC.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "mchor") {
                        var _oCard = d3.select("g[id=" + MCHOR.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            MCHOR.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            MCHOR.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "label") {
                        var _oCard = d3.select("g[id=" + LABEL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            LABEL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            LABEL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "ultility") {
                        var _oCard = d3.select("g[id=" + ULTILITY.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            ULTILITY.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            ULTILITY.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "busbar") {
                        var _oCard = d3.select("g[id=" + BUSBAR.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            BUSBAR.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            BUSBAR.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "busbarver") {
                        var _oCard = d3.select("g[id=" + BUSBARVER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            BUSBARVER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + BUSBARVER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            BUSBARVER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "stmv") {
                        var _oCard = d3.select("g[id=" + STMV.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STMV.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            STMV.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "sgmv") {
                        var _oCard = d3.select("g[id=" + SGMV.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            SGMV.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            SGMV.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "inverter") {
                        var _oCard = d3.select("g[id=" + INVERTER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            INVERTER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            INVERTER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "invertersymbol") {
                        var _oCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            INVERTERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            INVERTERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "combiner") {
                        var _oCard = d3.select("g[id=" + COMBINER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            COMBINER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            COMBINER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "combinersymbol") {
                        var _oCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            COMBINERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            COMBINERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "panel") {
                        var _oCard = d3.select("g[id=" + PANEL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            PANEL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            PANEL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "panelsymbol") {
                        var _oCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            PANELSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            PANELSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "string") {
                        var _oCard = d3.select("g[id=" + STRING.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STRING.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            STRING.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "stringsymbol") {
                        var _oCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            STRINGSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            STRINGSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "weather") {
                        var _oCard = d3.select("g[id=" + WEATHER.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            WEATHER.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            WEATHER.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "weathersymbol") {
                        var _oCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            WEATHERSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            WEATHERSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "ups") {
                        var _oCard = d3.select("g[id=" + UPS.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            UPS.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            UPS.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoang1") {
                        var _oCard = d3.select("g[id=" + KHOANG1.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANG1.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANG1.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoang1symbol") {
                        var _oCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANG1SYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANG1SYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANG1SYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangcap") {
                        var _oCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCAP.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGCAP.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangcapsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCAPSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGCAPSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangchi") {
                        var _oCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGCHI.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGCHI.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangdodem") {
                        var _oCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGDODEM.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGDODEM.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangdodemsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGDODEMSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGDODEMSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangmaycat") {
                        var _oCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGMAYCAT.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGMAYCAT.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangmaycatsymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGMAYCATSYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGMAYCATSYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangthanhcai") {
                        var _oCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGTHANHCAI.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGTHANHCAI.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    } else if (g_connect.type == "khoangthanhcaisymbol") {
                        var _oCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.cardId + "]");
                        var _isOverlap = COMMON.intersectRect(_arrowMarker.node(), _oCard.node());
                        if (_isOverlap) {
                            KHOANGTHANHCAISYMBOL.focus(g_connect.cardId);

                            var _nCount = 0;
                            d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (d) {
                                if (COMMON.intersectRect(_arrowMarker.node(), this)) {
                                    g_connect.attachFlag = true;
                                    g_connect.id = d.id;
                                    g_connect.attachMarker = $(this).attr("marker");
                                    g_connect.attachTo = true;
                                    _nCount++;
                                }
                            });

                            if (_nCount == 0) {
                                g_connect.attachFlag = false;
                                g_connect.attachTo = false;
                            }

                        } else {
                            KHOANGTHANHCAISYMBOL.outFocus(g_connect.cardId);
                            g_connect.cardId = "";
                            g_connect.attachFlag = false;
                            g_connect.attachTo = false;
                        };
                    }
                }
            };

            $.each(DATA.aArrows, function (i, arrow) {
                if (arrow.id == d.id) {
                    arrow.x1 = _arrowStartPoint.attr("cx");
                    arrow.y1 = _arrowStartPoint.attr("cy");
                    arrow.x2 = _arrowEndPoint.attr("cx");
                    arrow.y2 = _arrowEndPoint.attr("cy");
                    if (arrow.position == 3 || arrow.position == 4) {
                        arrow.x3 = _arrowMiddlePoint.attr("cx");
                        arrow.y3 = _arrowMiddlePoint.attr("cy");
                    }
                    return false;
                }
            });

            if (_oArrow.position == 3) {
                _arrowMiddlePoint.attr("cx", (Number(d.x1) + Number(d.x2)) / 2);
            } else if (_oArrow.position == 4) {
                _arrowMiddlePoint.attr("cy", (Number(d.y1) + Number(d.y2)) / 2);
            }
        })
        .on("end", function (i, d) {
            setAction("dragend");
            var _oArrow = ARROW2.get(d.id);
            var isUpdate = true;
            if (g_connect.attachFlag) {
                var _attachMarker = d3.select("circle[id=" + g_connect.attachMarker + g_connect.id + "]");
                var _arrowLine = d3.select("line[id=" + ARROW2.ID_LINE + d.id + "]");
                var _gCard = {};
                var _type = "";

                if (g_connect.type == "arrow") {
                    _gCard = d3.select("g[id=" + ARROW2.ID + g_connect.id + "]");
                    _type = ARROW2.ID;
                } else if (g_connect.type == "meter") {
                    _gCard = d3.select("g[id=" + METER.ID + g_connect.id + "]");
                    _type = METER.ID;
                } else if (g_connect.type == "mba") {
                    _gCard = d3.select("g[id=" + MBA.ID + g_connect.id + "]");
                    _type = MBA.ID;
                } else if (g_connect.type == "rmu") {
                    _gCard = d3.select("g[id=" + RMU.ID + g_connect.id + "]");
                    _type = RMU.ID;
                } else if (g_connect.type == "feeder") {
                    _gCard = d3.select("g[id=" + FEEDER.ID + g_connect.id + "]");
                    _type = FEEDER.ID;
                } else if (g_connect.type == "mc") {
                    _gCard = d3.select("g[id=" + MC.ID + g_connect.id + "]");
                    _type = MC.ID;
                } else if (g_connect.type == "mchor") {
                    _gCard = d3.select("g[id=" + MCHOR.ID + g_connect.id + "]");
                    _type = MCHOR.ID;
                } else if (g_connect.type == "label") {
                    _gCard = d3.select("g[id=" + LABEL.ID + g_connect.id + "]");
                    _type = LABEL.ID;
                } else if (g_connect.type == "ultility") {
                    _gCard = d3.select("g[id=" + ULTILITY.ID + g_connect.id + "]");
                    _type = ULTILITY.ID;
                } else if (g_connect.type == "busbar") {
                    _gCard = d3.select("g[id=" + BUSBAR.ID + g_connect.id + "]");
                    _type = BUSBAR.ID;
                } else if (g_connect.type == "busbarver") {
                    _gCard = d3.select("g[id=" + BUSBARVER.ID + g_connect.id + "]");
                    _type = BUSBARVER.ID;
                } else if (g_connect.type == "stmv") {
                    _gCard = d3.select("g[id=" + STMV.ID + g_connect.id + "]");
                    _type = STMV.ID;
                } else if (g_connect.type == "sgmv") {
                    _gCard = d3.select("g[id=" + SGMV.ID + g_connect.id + "]");
                    _type = SGMV.ID;
                } else if (g_connect.type == "inverter") {
                    _gCard = d3.select("g[id=" + INVERTER.ID + g_connect.id + "]");
                    _type = INVERTER.ID;
                } else if (g_connect.type == "invertersymbol") {
                    _gCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.id + "]");
                    _type = INVERTERSYMBOL.ID;
                } else if (g_connect.type == "combiner") {
                    _gCard = d3.select("g[id=" + COMBINER.ID + g_connect.id + "]");
                    _type = COMBINER.ID;
                } else if (g_connect.type == "combinersymbol") {
                    _gCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.id + "]");
                    _type = COMBINERSYMBOL.ID;
                } else if (g_connect.type == "panel") {
                    _gCard = d3.select("g[id=" + PANEL.ID + g_connect.id + "]");
                    _type = PANEL.ID;
                } else if (g_connect.type == "panelsymbol") {
                    _gCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.id + "]");
                    _type = PANELSYMBOL.ID;
                } else if (g_connect.type == "string") {
                    _gCard = d3.select("g[id=" + STRING.ID + g_connect.id + "]");
                    _type = STRING.ID;
                } else if (g_connect.type == "stringsymbol") {
                    _gCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.id + "]");
                    _type = STRINGSYMBOL.ID;
                } else if (g_connect.type == "weather") {
                    _gCard = d3.select("g[id=" + WEATHER.ID + g_connect.id + "]");
                    _type = WEATHER.ID;
                } else if (g_connect.type == "weathersymbol") {
                    _gCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.id + "]");
                    _type = WEATHERSYMBOL.ID;
                } else if (g_connect.type == "ups") {
                    _gCard = d3.select("g[id=" + UPS.ID + g_connect.id + "]");
                    _type = UPS.ID;
                } else if (g_connect.type == "khoang1") {
                    _gCard = d3.select("g[id=" + KHOANG1.ID + g_connect.id + "]");
                    _type = KHOANG1.ID;
                } else if (g_connect.type == "khoang1symbol") {
                    _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.id + "]");
                    _type = KHOANG1SYMBOL.ID;
                } else if (g_connect.type == "khoangcap") {
                    _gCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.id + "]");
                    _type = KHOANGCAP.ID;
                } else if (g_connect.type == "khoangcapsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.id + "]");
                    _type = KHOANGCAPSYMBOL.ID;
                } else if (g_connect.type == "khoangchi") {
                    _gCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.id + "]");
                    _type = KHOANGCHI.ID;
                } else if (g_connect.type == "khoangdodem") {
                    _gCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.id + "]");
                    _type = KHOANGDODEM.ID;
                } else if (g_connect.type == "khoangdodemsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.id + "]");
                    _type = KHOANGDODEMSYMBOL.ID;
                } else if (g_connect.type == "khoangmaycat") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.id + "]");
                    _type = KHOANGMAYCAT.ID;
                } else if (g_connect.type == "khoangmaycatsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.id + "]");
                    _type = KHOANGMAYCATSYMBOL.ID;
                } else if (g_connect.type == "khoangthanhcai") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.id + "]");
                    _type = KHOANGTHANHCAI.ID;
                } else if (g_connect.type == "khoangthanhcaisymbol") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.id + "]");
                    _type = KHOANGTHANHCAISYMBOL.ID;
                }

                var _gArrow = d3.select("g[id=" + ARROW2.ID + d.id + "]");
                var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_attachMarker.attr("cx")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[0]);
                var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_attachMarker.attr("cy")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[1]);

                d3.select(this)
                    .attr("cx", _x)
                    .attr("cy", _y);

                if (this.id.indexOf(ARROW2.ID_LINE_POINT_START) >= 0) {
                    var _garrowRemove = d3.select("g[id=" + ARROW2.ID_REMOVE + d.id + "]");
                    _oArrow.from = g_connect.id;
                    _oArrow.fromMarker = g_connect.attachMarker;
                    _oArrow.fromType = _type;

                    _arrowLine
                        .attr("x1", d.x1 = d3.select(this).attr("cx"))
                        .attr("y1", d.y1 = d3.select(this).attr("cy"));


                    _garrowRemove.attr("transform", 'translate(' + (Number(d.x1) - 20) + ',' + (Number(d.y1) - 40) + ')');

                } else if (this.id.indexOf(ARROW2.ID_LINE_POINT_END) >= 0) {
                    _oArrow.to = g_connect.id;
                    _oArrow.toMarker = g_connect.attachMarker;
                    _oArrow.toType = _type;

                    _arrowLine
                        .attr("x2", d.x2 = d3.select(this).attr("cx"))
                        .attr("y2", d.y2 = d3.select(this).attr("cy"));

                } else {
                    if ((DATA.oldX == DATA.x) && (DATA.oldY == DATA.y)) {
                        isUpdate = false;
                    }
                }


            } else {

                if (this.id.indexOf(ARROW2.ID_LINE_POINT_START) >= 0) {
                    _oArrow.from = "";
                } else if (this.id.indexOf(ARROW2.ID_LINE_POINT_END) >= 0) {
                    _oArrow.to = "";
                } else {
                    if ((DATA.oldX == DATA.x) && (DATA.oldY == DATA.y)) {
                        isUpdate = false;
                    }
                }

                if (isUpdate) {
                    if (!g_connect.attachFrom) {
                        _oArrow.from = "";
                    }
                    if (!g_connect.attachTo) {
                        _oArrow.to = "";
                    }
                }
            }

            g_connect.id = "";
            g_connect.cardId = "";
            g_connect.attachFlag = false;
            g_connect.attachMarker = "";
            g_connect.attachFrom = false;
            g_connect.attachTo = false;
            ARROW2.moveArrows(d.id);

            d3.select(this).classed("active", false);
        }),

    moveArrows: function (id) {
        var _attachArrows = [];
        if (DATA.aArrows.length > 0) {
            $.each(DATA.aArrows, function (i, arrow) {
                if (arrow.from == id) {
                    _attachArrows.push(arrow.id + ":"
                        + ARROW2.ID_LINE_POINT_START + arrow.id + ":" + arrow.fromMarker + arrow.from);
                }

                if (arrow.to == id) {
                    _attachArrows.push(arrow.id + ":"
                        + ARROW2.ID_LINE_POINT_END + arrow.id + ":" + arrow.toMarker + arrow.to);
                }
            });
        }

        $.each(_attachArrows, function (i, data) {
            var _ids = data.split(":");

            var _oArrow = ARROW2.get(_ids[0]);
            var _arrowLine = d3.select("line[id=" + ARROW2.ID_LINE + _ids[0] + "]");
            var _arrowMarker = d3.select("circle[id=" + _ids[1] + "]");
            var _cardMarker = d3.select("circle[id=" + _ids[2] + "]");

            var _gCard = d3.select("g[id=" + ARROW2.ID + id + "]");
            var _gArrow = d3.select("g[id=" + ARROW2.ID + _ids[0] + "]");

            var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_cardMarker.attr("cx")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]) + Number(COMMON.getTranslation(_cardMarker.attr("transform"))[0]);
            var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_cardMarker.attr("cy")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]) + Number(COMMON.getTranslation(_cardMarker.attr("transform"))[1]);

            _arrowMarker
                .attr("cx", _x)
                .attr("cy", _y);

            if (_ids[1].indexOf(ARROW2.ID_LINE_POINT_START) >= 0) {
                _arrowLine
                    .attr("x1", _arrowMarker.attr("cx"))
                    .attr("y1", _arrowMarker.attr("cy"));
            } else if (_ids[1].indexOf(ARROW2.ID_LINE_POINT_END) >= 0) {
                _arrowLine
                    .attr("x2", _arrowMarker.attr("cx"))
                    .attr("y2", _arrowMarker.attr("cy"));
            }
            ARROW2.update(_ids[0]);
        });
    },

    update: function (id, option) {

        var _oArrow = ARROW2.get(id);

        if (typeof _oArrow !== "undefined" && _oArrow != null) {
            var _gArrow = d3.select("g[id=" + ARROW2.ID + id + "]");
            var _arrowLine = d3.select("line[id=" + ARROW2.ID_LINE + id + "]");

            _oArrow.x1 = _arrowLine.attr("x1");
            _oArrow.y1 = _arrowLine.attr("y1");
            _oArrow.x2 = _arrowLine.attr("x2");
            _oArrow.y2 = _arrowLine.attr("y2");

            _gArrow.remove();

            if (_oArrow.from == "" || _oArrow.to == "") {
                ARROW2.draw(_oArrow);
            } else {
                var _cardFrom = d3.select("g[id=" + _oArrow.fromType + _oArrow.from + "]");
                var _cardTo = d3.select("g[id=" + _oArrow.toType + _oArrow.to + "]");
                var _fromMarker = d3.select("circle[id=" + _oArrow.fromMarker + _oArrow.from + "]");
                var _toMarker = d3.select("circle[id=" + _oArrow.toMarker + _oArrow.to + "]");

                var xFrom = Number(COMMON.getTranslation(_cardFrom.attr("transform"))[0]) + Number(_fromMarker.attr("cx")) + Number(COMMON.getTranslation(_fromMarker.attr("transform"))[0]);
                var xTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[0]) + Number(_toMarker.attr("cx")) + Number(COMMON.getTranslation(_toMarker.attr("transform"))[0]);

                var yFrom = Number(COMMON.getTranslation(_cardFrom.attr("transform"))[1]) + Number(_fromMarker.attr("cy")) + Number(COMMON.getTranslation(_fromMarker.attr("transform"))[1]);
                var yTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[1]) + Number(_toMarker.attr("cy")) + Number(COMMON.getTranslation(_toMarker.attr("transform"))[1]);

                var differentX = parseInt(xFrom) - parseInt(xTo);
                var differentY = parseInt(yFrom) - parseInt(yTo);

                if ((differentX > - 1 && differentX < 1) || (differentY > - 1 && differentY < 1)) {
                    _oArrow.stroke = "#D0CECE";
                } else {
                    _oArrow.stroke = "rgb(4.705882%,10.980392%,14.509804%)";
                };

                if (getAction() == "dragend") {
                    _oArrow.stroke = "rgb(4.705882%,10.980392%,14.509804%)";
                };

                ARROW2.draw(_oArrow);
            }
        }

    },

    select: function (e, d) {
        e.preventDefault();
        e.stopPropagation();
        outFocus();
        DATA.selectedID = d.id;
        DATA.selectedType = 2;
        ARROW2.focus(DATA.selectedID);

        var _oArrow = ARROW2.get(d.id);

        $("#btn-update").show();
        $("#btn-remove").show();
    },

    get: function (id) {
        var _oArrow;

        $.each(DATA.aArrows, function (i, arrow) {
            if (arrow.id == id) {
                if (arrow.componentType == "arrow") {
                    _oArrow = arrow;
                }
                return false;
            }
        });
        return _oArrow;
    },

    focus: function (id) {
        if (id != null) {
            $('circle[id^=' + ARROW2.ID_LINE_POINT_START + id + ']').attr("opacity", 1);
            $('circle[id^=' + ARROW2.ID_LINE_POINT_MIDDLE + id + ']').attr("opacity", 1);
            $('circle[id^=' + ARROW2.ID_LINE_POINT_END + id + ']').attr("opacity", 1);
            $('g[id^=' + ARROW2.ID_REMOVE + id + ']').attr("opacity", 1);
        }
    },

    outFocus: function (id) {
        if (id != null) {
            $('circle[id^=' + ARROW2.ID_LINE_POINT_START + id + ']').attr("opacity", 0);
            $('circle[id^=' + ARROW2.ID_LINE_POINT_END + id + ']').attr("opacity", 0);
            $('circle[id^=' + ARROW2.ID_LINE_POINT_MIDDLE + id + ']').attr("opacity", 0);
            $('g[id^=' + ARROW2.ID_REMOVE + id + ']').attr("opacity", 0);
        } else {
            $('circle[id^=' + ARROW2.ID_LINE_POINT_START + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + ARROW2.ID_LINE_POINT_END + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + ARROW2.ID_LINE_POINT_MIDDLE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('g[id^=' + ARROW2.ID_REMOVE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
        }
    },

    remove: function (id) {
        d3.select("g[id=" + ARROW2.ID + id + "]").remove();

        DATA.aArrows = $.grep(DATA.aArrows, function (arrow) {
            return arrow.id != id;
        });
    },

    delete: function (e) {
        e.stopPropagation();
        ARROW2.remove(DATA.selectedID);
    },
};

export default ARROW2;