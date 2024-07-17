import COMMON from "./common";
import CONSTANTS from "./constant";
import ARROW from "./arrow";
import METER from "./meter";
import MBA from "./mba";
import RMU from "./rmu";
import FEEDER from "./feeder";
import MC from "./mc";
import MCHOR from "./mchor";
import LABEL from "./label";
import ULTILITY from "./ultility";
import BUSBAR from "./busbar";
import { DATA, g_connect, setAction, outFocus, showProperties } from "./data";
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
import KHOANGDODEMSYMBOL from "./khoangdodemsymbol";
import KHOANGMAYCAT from "./khoangmaycat";
import KHOANGMAYCATSYMBOL from "./khoangmaycatsymbol";
import KHOANGTHANHCAI from "./khoangthanhcai";
import KHOANGTHANHCAISYMBOL from "./khoangthanhcaisymbol";

const $ = window.$;

let BUSBARVER = {
    ID: "gbv-",
    BUS_X: 5,
    BUS_Y: 15,
    ID_BOX: "b",
    DEFAULT_WIDTH: 0,
    DEFAULT_HEIGHT: 2,
    DEFAULT_X: 20,
    DEFAULT_Y: 20,
    MIN_RANGE: 160,
    MIN_RANGE2: 80,
    ID_CIRCLE_CONNECT: "vc",
    ID_RESIZE_TOP: "rt",
    ID_RESIZE_BOTTOM: "rb",
    ID_CIRCLE_TOP: "vbt",
    ID_CIRCLE_BOTTOM: "vbb",
    ID_ROTATE: "bbvr-",
    ID_REMOVE: "bvrm-",
    directArrow: {},

    collect: function () {
        var _qty = document.getElementById("jointer-qty").value;
        var _width = BUSBARVER.MIN_RANGE * (parseInt(_qty) - 1);
        var _range = Number(_width) / (parseInt(_qty) - 1);
        var _busbarsver = {
            "id": Date.now(),
            "range": _range,
            "qty": parseInt(_qty),
            "width": _width,
            "x": BUSBARVER.DEFAULT_X,
            "y": BUSBARVER.DEFAULT_Y,
            "xTop": "0",
            "yTop": "23",
            "rectX": "0",
            "rectY": "0",
            "cxTop": "-8",
            "cyTop": "15",
            "xBottom": "0",
            "yBottom": Number(_width),
            "cxBottom": "-8",
            "cyBottom": BUSBARVER.BUS_Y + Number(_width),
            "type": "busbarver",
        };
        return _busbarsver;
    },

    create: function () {
        var _oCard = BUSBARVER.collect();
        DATA.aCards.push(_oCard);
        BUSBARVER.draw(_oCard);
    },

    drawLoad: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (BUSBARVER.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        var _cardBox = _gCard.datum(card).append("rect")
            .attr("id", function (d) {
                return (BUSBARVER.ID_BOX + d.id);
            })
            .attr("x", BUSBARVER.BUS_X)
            .attr("y", BUSBARVER.BUS_Y)
            .attr("transform", function (d) { return 'translate(' + d.rectX + ',' + d.rectY + ')'; })
            .attr("width", BUSBARVER.DEFAULT_HEIGHT)
            .attr("height", function (d) { return d.width })
            .attr("fill-opacity", "1")
            .attr("fill", "rgb(4.705882%,10.980392%,14.509804%)")
            .attr("stroke-width", "2")
            .attr("stroke", "rgb(4.705882%,10.980392%,14.509804%)");
    },

    draw: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (BUSBARVER.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            })
            .on("click", BUSBARVER.select)
            .call(BUSBARVER.drag);

        var _cardBox = _gCard.datum(card).append("rect")
            .attr("id", function (d) {
                return (BUSBARVER.ID_BOX + d.id);
            })
            .attr("x", BUSBARVER.BUS_X)
            .attr("y", BUSBARVER.BUS_Y)
            .attr("transform", function (d) { return 'translate(' + d.rectX + ',' + d.rectY + ')'; })
            .attr("width", BUSBARVER.DEFAULT_HEIGHT)
            .attr("height", function (d) { return d.width })
            .attr("fill-opacity", "1")
            .attr("fill", "rgb(4.705882%,10.980392%,14.509804%)")
            .attr("stroke-width", "2")
            .attr("stroke", "rgb(4.705882%,10.980392%,14.509804%)");

        for (var i = 1; i <= card.qty; i++) {
            var x = 0;
            x = (i - 1) * card.range;
            var k = 0;
            if (i == 1) {
                k = 1;
            } else if (i == card.qty) {
                k = - 1;
            }

            var _circleConnect = _gCard.datum(card).append("circle")
                .attr("id", function (d) {
                    return (BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id)
                })
                .attr("class", "circle-connect")
                .attr("transform", function (d) {
                    return ("translate(" + (BUSBARVER.BUS_Y - 14) + "," + (Number(d.rectY) + x + k) + ")")
                })
                .attr("cursor", "pointer")
                .attr("marker", BUSBARVER.ID_CIRCLE_CONNECT + i + "-")
                .attr("cx", BUSBARVER.BUS_X)
                .attr("cy", BUSBARVER.BUS_Y)
                .attr("fill", "#276cb8")
                .attr("r", "7")
                .call(BUSBARVER.connect);
        };

        var _resizeTop = _gCard.datum(card).append('path')
            .attr("id", function (d) { return (BUSBARVER.ID_RESIZE_TOP + d.id); })
            .attr("class", "circle-connect")
            .attr("transform", function (d) { return 'translate(' + d.xTop + ',' + d.yTop + ')'; })
            .attr("d", "M -10 0 l 0 -10 l -3 0 l 5 -5 l 5 5 l -3 0 l 0 10")
            .attr("opacity", "0")
            .attr("fill", "#276cb8");

        var _circleTop = _gCard.datum(card).append("circle")
            .attr("id", function (d) {
                return (BUSBARVER.ID_CIRCLE_TOP + d.id);
            })
            .attr("cx", function (d) { return d.cxTop })
            .attr("cy", function (d) { return d.cyTop })
            .attr("fill", "transparent")
            .attr("r", "6")
            .attr("opacity", "0")
            .attr("cursor", "se-resize")
            .call(BUSBARVER.resize);

        var _resizeBottom = _gCard.datum(card).append('path')
            .attr("id", function (d) { return (BUSBARVER.ID_RESIZE_BOTTOM + d.id); })
            .attr("class", "circle-connect")
            .attr("transform", function (d) { return 'translate(' + Number(d.xBottom) + ',' + (Number(d.yBottom) + 10) + ')'; })
            .attr("d", "M -5 0 l 0 10 l 3 0 l -5 5 l -5 -5 l 3 0 l 0 -10")
            .attr("opacity", "0")
            .attr("fill", "#276cb8");

        var _circleBottom = _gCard.datum(card).append("circle")
            .attr("id", function (d) {
                return (BUSBARVER.ID_CIRCLE_BOTTOM + d.id);
            })
            .attr("cx", function (d) { return d.cxBottom })
            .attr("cy", function (d) { return d.cyBottom })
            .attr("fill", "transparent")
            .attr("r", "6")
            .attr("opacity", "0")
            .attr("cursor", "se-resize")
            .call(BUSBARVER.resize);

        var _gRotate = _gCard.datum(card).append('g')
            .attr("id", function (d) { return (BUSBARVER.ID_ROTATE + d.id); })
            .attr("transform", function (d) { return 'translate(' + (Number(d.xBottom) + 20) + ',' + Number(d.yBottom + 7) + ')'; })
            .attr("class", "rotate")
            .attr("opacity", "0")
            .on("click", BUSBARVER.rotate);

        var _rotate = _gRotate.append('path')
            .attr("d", "M 6.492188 0.703125 C 4.570312 0.996094 2.835938 2.132812 1.746094 3.796875 C 1.171875 4.675781 0.84375 5.542969 0.691406 6.644531 C 0.585938 7.382812 0.597656 7.558594 0.761719 7.804688 C 0.960938 8.109375 1.558594 8.203125 1.710938 7.945312 C 1.757812 7.875 1.863281 7.371094 1.933594 6.84375 C 2.226562 4.898438 3.339844 3.316406 4.992188 2.507812 C 7.21875 1.40625 9.632812 1.804688 11.472656 3.574219 C 12.269531 4.347656 12.667969 4.511719 12.984375 4.195312 C 13.078125 4.101562 13.125 3.550781 13.125 2.496094 C 13.125 0.972656 13.113281 0.9375 12.832031 0.75 C 12.292969 0.398438 11.941406 0.773438 11.8125 1.839844 C 11.777344 2.074219 11.742188 2.0625 11.015625 1.628906 C 9.703125 0.832031 7.945312 0.46875 6.492188 0.703125 Z M 6.492188 0.703125 ")
            .attr("fill", "#276cb8");

        var _rotate1 = _gRotate.append('path')
            .attr("d", "M 5.929688 3.445312 C 4.699219 3.960938 3.855469 4.828125 3.398438 6.058594 C 3.023438 7.066406 3.105469 8.460938 3.597656 9.433594 C 4.019531 10.265625 4.851562 11.085938 5.683594 11.484375 C 6.1875 11.742188 6.433594 11.777344 7.5 11.777344 C 8.601562 11.777344 8.800781 11.742188 9.375 11.460938 C 10.207031 11.050781 11.050781 10.207031 11.460938 9.375 C 11.742188 8.800781 11.777344 8.601562 11.777344 7.5 C 11.777344 6.433594 11.742188 6.1875 11.484375 5.683594 C 11.085938 4.851562 10.265625 4.019531 9.433594 3.597656 C 8.835938 3.292969 8.578125 3.234375 7.675781 3.199219 C 6.761719 3.164062 6.527344 3.199219 5.929688 3.445312 Z M 5.929688 3.445312 ")
            .attr("fill", "#276cb8");

        var _rotate1 = _gRotate.append('path')
            .attr("d", "M 13.324219 7.125 C 13.207031 7.253906 13.125 7.535156 13.125 7.804688 C 13.125 10.03125 11.25 12.351562 8.964844 12.949219 C 6.996094 13.453125 5.074219 12.914062 3.503906 11.414062 C 2.707031 10.640625 2.390625 10.511719 2.0625 10.851562 C 1.910156 10.992188 1.875 11.320312 1.875 12.527344 C 1.875 13.828125 1.898438 14.050781 2.085938 14.214844 C 2.578125 14.660156 3.070312 14.332031 3.164062 13.5 L 3.222656 12.914062 L 3.984375 13.371094 C 5.167969 14.097656 6.152344 14.355469 7.617188 14.34375 C 8.660156 14.332031 8.976562 14.285156 9.726562 14.003906 C 12.339844 13.03125 14.097656 10.816406 14.367188 8.144531 C 14.4375 7.5 14.414062 7.347656 14.238281 7.148438 C 13.96875 6.855469 13.570312 6.84375 13.324219 7.125 Z M 13.324219 7.125 ")
            .attr("fill", "#276cb8");

        var _gX = _gCard.datum(card).append("g")
            .attr("id", function (d) { return (BUSBARVER.ID_REMOVE + d.id); })
            .attr("transform", function (d) { return 'translate(' + (Number(d.xTop) + 20) + ',' + Number(d.yTop - 30) + ')'; })
            .attr("opacity", "1")
            .attr("cursor", "pointer")
            .on("click", BUSBARVER.delete);

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

            showProperties(10);
            BUSBARVER.focus(DATA.selectedID);

            var _oCard = BUSBARVER.get(d.id);
            document.getElementById("jointer-qty").value = _oCard.qty;
            document.getElementById("busbar-vertical").checked = true;

            var isMarker = false;
            for (var i = 0; i < DATA.aArrows.length; i++) {
                if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                    isMarker = true;
                    break;
                } else {
                    isMarker = false;
                }
            }

            if (isMarker) {
                $('#jointer-qty').attr("disabled", true);
                $('input[name=busbar_position]').attr("disabled", true);
            } else {
                $('#jointer-qty').attr("disabled", false);
                $('input[name=busbar_position]').attr('disabled', false);
            }

            $("#btn-update").show();
            $("#btn-remove").show();

            d3.select(this).classed("active", true);
        })
        .on("drag", function (i, d) {
            var _gCard = d3.select("g[id=" + BUSBARVER.ID + d.id + "]");
            var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + "]");
            var _busbarsIndex = DATA.aCards.findIndex(x => x.id === Number(d.id));
            var _busbarsQty = Number(DATA.aCards[_busbarsIndex].qty);

            d3.select(this).attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
            d.x = i.x;
            d.y = i.y;
            BUSBARVER.moveArrows(d.id);

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

            d3.selectAll("g[id^=" + KHOANG1SYMBOL.ID + "]").each(function (c) {
                var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
                if (_isOverlap) {
                    KHOANG1SYMBOL.focus(this.id.replace(KHOANG1SYMBOL.ID, ''));
                } else {
                    KHOANG1SYMBOL.outFocus(this.id.replace(KHOANG1SYMBOL.ID, ''));
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
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "meter";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "mba";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "rmu";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "feeder";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "mc";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "mchor";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "label";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "ultility";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "busbar";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "stmv";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "sgmv";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "inverter";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "invertersymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "combiner";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "combinersymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "panel";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "panelsymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "string";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "stringsymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "weather";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "weathersymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "ups";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoang1";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANG1SYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoang1symbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangcap";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangcapsymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangchi";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangdodem";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangdodemsymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangmaycat";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangmaycatsymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangthanhcai";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });

                d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
                    for (var i = 1; i <= _busbarsQty; i++) {
                        var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                        var _isOverlap = COMMON.intersectRect(_circleConnect.node(), this);
                        if (_isOverlap) {
                            g_connect.id = this.id;
                            g_connect.cardId = this.id.split("-").pop();
                            g_connect.type = "khoangthanhcaisymbol";
                            g_connect.circleFrom = BUSBARVER.ID_CIRCLE_CONNECT + i + "-";
                            break;
                        };
                    }
                });
            };

            if (g_connect.cardId != "") {
                var _circleMarker = d3.select("circle[id=" + g_connect.id + "]");
                var _circleConnect = d3.select("circle[id=" + g_connect.circleFrom + d.id + "]");
                var _isOverlap = COMMON.intersectRect(_circleConnect.node(), _circleMarker.node());
                if (_isOverlap) {
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
                    case "weathersymbol":
                        _cardTo = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.cardId + "]");
                        break;
                    case "ups":
                        _cardTo = d3.select("g[id=" + UPS.ID + g_connect.cardId + "]");
                        break;
                    case "khoang1":
                        _cardTo = d3.select("g[id=" + KHOANG1.ID + g_connect.cardId + "]");
                        break;
                    case "khoang1symbol":
                        _cardTo = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.cardId + "]");
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

            BUSBARVER.moveArrows(d.id);
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

            showProperties(10);
            BUSBARVER.focus(DATA.selectedID);

            var _oCard = BUSBARVER.get(d.id);
            document.getElementById("jointer-qty").value = _oCard.qty;
            document.getElementById("busbar-vertical").checked = true;

            var isMarker = false;
            for (var i = 0; i < DATA.aArrows.length; i++) {
                if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                    isMarker = true;
                    break;
                } else {
                    isMarker = false;
                }
            }

            if (isMarker) {
                $('#jointer-qty').attr("disabled", true);
                $('input[name=busbar_position]').attr("disabled", true);
            } else {
                $('#jointer-qty').attr("disabled", false);
                $('input[name=busbar_position]').attr('disabled', false);
            }

            $("#btn-update").show();
            $("#btn-remove").show();

            d3.select(this).classed("active", true);

            var _gCard = d3.select("g[id=" + BUSBARVER.ID + d.id + "]");

            var _attachMarker = d3.select("circle[id=" + this.id + "]");

            var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_attachMarker.attr("cx")) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[0]);
            var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_attachMarker.attr("cy")) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[1]);

            var _markerIdPrefix = this.id.replace(d.id, "");

            BUSBARVER.directArrow = {
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
                "fromType": BUSBARVER.ID,
                "position": 1,
                "type": 1,
            };

            var g_svg = d3.select("#svg-container");

            var _gArrow = g_svg.datum(BUSBARVER.directArrow).append('g')
                .attr("id", function (a) { return ARROW.ID + a.id; })
                .attr("transform", function (a) { return 'translate(' + a.x + ',' + a.y + ')'; })
                .datum({
                    x: function (a) { return a.x },
                    y: function (a) { return a.y }
                })
                .call(ARROW.drag);

            var _arrowLine = _gArrow.datum(BUSBARVER.directArrow).append("line")
                .attr("id", function (a) { return (ARROW.ID_LINE + a.id); })
                .attr("x1", function (a) { return a.x1; })
                .attr("y1", function (a) { return a.y1; })
                .attr("x2", function (a) { return a.x2; })
                .attr("y2", function (a) { return a.y2; })
                .attr("stroke-width", function (a) { return a.strokeWidth; })
                .attr("stroke", function (a) { return a.stroke; })
                .attr("marker-end", "url(#arrow)")
                .on("click", ARROW.focus);

            var _arrowStartPoint = _gArrow.datum(BUSBARVER.directArrow).append('circle')
                .attr("id", function (a) { return ARROW.ID_LINE_POINT_START + a.id; })
                .attr("class", "circle-connect")
                .attr("r", ARROW.MARKER_RADIUS)
                .attr("cx", function (a) { return a.x1; })
                .attr("cy", function (a) { return a.y1; })
                .attr("opacity", 0)
                .attr("cursor", "pointer")
                .attr("fill", ARROW.MARKER_CONNECTION_FILL)
                .call(ARROW.drag);

            var _arrowEndPoint = _gArrow.datum(BUSBARVER.directArrow).append('circle')
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
            var _gCard = d3.select("g[id=" + BUSBARVER.ID + d.id + "]");

            var _gArrow = d3.select("g[id=" + ARROW.ID + BUSBARVER.directArrow.id + "]");
            var _arrowLine = d3.select("line[id=" + ARROW.ID_LINE + BUSBARVER.directArrow.id + "]");
            var _arrowEndPoint = d3.select("circle[id=" + ARROW.ID_LINE_POINT_END + BUSBARVER.directArrow.id + "]");
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

                d3.selectAll("g[id^=" + METER.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "meter";
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

                d3.selectAll("g[id^=" + RMU.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "rmu";
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

                d3.selectAll("g[id^=" + KHOANG1SYMBOL.ID + "]").each(function (c) {
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), this);
                    if (_isOverlap) {
                        g_connect.cardId = c.id;
                        g_connect.type = "khoang1symbol";
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
                if (g_connect.type == "mba") {
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
                } else if (g_connect.type == "meter") {
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
                } else if (g_connect.type == "khoang1symbol") {
                    var _oCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.cardId + "]");
                    var _isOverlap = COMMON.intersectRect(_arrowEndPoint.node(), _oCard.node());
                    if (_isOverlap) {
                        KHOANG1SYMBOL.focus(g_connect.cardId);

                        var _nCount = 0;
                        d3.selectAll("circle[id^=" + KHOANG1SYMBOL.ID_CIRCLE_CONNECT + "]").each(function (m) {
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
                        KHOANG1SYMBOL.outFocus(g_connect.cardId);
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
                if (g_connect.type == "mba") {
                    _gCard = d3.select("g[id=" + MBA.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = MBA.ID;
                } else if (g_connect.type == "label") {
                    _gCard = d3.select("g[id=" + LABEL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = LABEL.ID;
                } else if (g_connect.type == "meter") {
                    _gCard = d3.select("g[id=" + METER.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = METER.ID;
                } else if (g_connect.type == "mc") {
                    _gCard = d3.select("g[id=" + MC.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = MC.ID;
                } else if (g_connect.type == "mchor") {
                    _gCard = d3.select("g[id=" + MCHOR.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = MCHOR.ID;
                } else if (g_connect.type == "feeder") {
                    _gCard = d3.select("g[id=" + FEEDER.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = FEEDER.ID;
                } else if (g_connect.type == "rmu") {
                    _gCard = d3.select("g[id=" + RMU.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = RMU.ID;
                } else if (g_connect.type == "ultility") {
                    _gCard = d3.select("g[id=" + ULTILITY.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = ULTILITY.ID;
                } else if (g_connect.type == "busbar") {
                    _gCard = d3.select("g[id=" + BUSBAR.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = BUSBAR.ID;
                } else if (g_connect.type == "stmv") {
                    _gCard = d3.select("g[id=" + STMV.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = STMV.ID;
                } else if (g_connect.type == "sgmv") {
                    _gCard = d3.select("g[id=" + SGMV.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = SGMV.ID;
                } else if (g_connect.type == "inverter") {
                    _gCard = d3.select("g[id=" + INVERTER.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = INVERTER.ID;
                } else if (g_connect.type == "invertersymbol") {
                    _gCard = d3.select("g[id=" + INVERTERSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = INVERTERSYMBOL.ID;
                } else if (g_connect.type == "combiner") {
                    _gCard = d3.select("g[id=" + COMBINER.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = COMBINER.ID;
                } else if (g_connect.type == "combinersymbol") {
                    _gCard = d3.select("g[id=" + COMBINERSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = COMBINERSYMBOL.ID;
                } else if (g_connect.type == "panel") {
                    _gCard = d3.select("g[id=" + PANEL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = PANEL.ID;
                } else if (g_connect.type == "panelsymbol") {
                    _gCard = d3.select("g[id=" + PANELSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = PANELSYMBOL.ID;
                } else if (g_connect.type == "string") {
                    _gCard = d3.select("g[id=" + STRING.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = STRING.ID;
                } else if (g_connect.type == "stringsymbol") {
                    _gCard = d3.select("g[id=" + STRINGSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = STRINGSYMBOL.ID;
                } else if (g_connect.type == "weather") {
                    _gCard = d3.select("g[id=" + WEATHER.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = WEATHER.ID;
                } else if (g_connect.type == "weathersymbol") {
                    _gCard = d3.select("g[id=" + WEATHERSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = WEATHERSYMBOL.ID;
                } else if (g_connect.type == "ups") {
                    _gCard = d3.select("g[id=" + UPS.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = UPS.ID;
                } else if (g_connect.type == "khoang1") {
                    _gCard = d3.select("g[id=" + KHOANG1.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANG1.ID;
                } else if (g_connect.type == "khoang1symbol") {
                    _gCard = d3.select("g[id=" + KHOANG1SYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANG1SYMBOL.ID;
                } else if (g_connect.type == "khoangcap") {
                    _gCard = d3.select("g[id=" + KHOANGCAP.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGCAP.ID;
                } else if (g_connect.type == "khoangcapsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGCAPSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGCAPSYMBOL.ID;
                } else if (g_connect.type == "khoangchi") {
                    _gCard = d3.select("g[id=" + KHOANGCHI.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGCHI.ID;
                } else if (g_connect.type == "khoangdodem") {
                    _gCard = d3.select("g[id=" + KHOANGDODEM.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGDODEM.ID;
                } else if (g_connect.type == "khoangdodemsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGDODEMSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGDODEMSYMBOL.ID;
                } else if (g_connect.type == "khoangmaycat") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCAT.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGMAYCAT.ID;
                } else if (g_connect.type == "khoangmaycatsymbol") {
                    _gCard = d3.select("g[id=" + KHOANGMAYCATSYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGMAYCATSYMBOL.ID;
                } else if (g_connect.type == "khoangthanhcai") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAI.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGTHANHCAI.ID;
                } else if (g_connect.type == "khoangthanhcaisymbol") {
                    _gCard = d3.select("g[id=" + KHOANGTHANHCAISYMBOL.ID + g_connect.id + "]");
                    BUSBARVER.directArrow.toType = KHOANGTHANHCAISYMBOL.ID;
                };

                var _attachMarker = d3.select("circle[id=" + g_connect.attachMarker + g_connect.id + "]");

                var _gArrow = d3.select("g[id=" + ARROW.ID + BUSBARVER.directArrow.id + "]");
                var _arrowLine = d3.select("line[id=" + ARROW.ID_LINE + BUSBARVER.directArrow.id + "]");
                var _arrowEndPoint = d3.select("circle[id=" + ARROW.ID_LINE_POINT_END + BUSBARVER.directArrow.id + "]");

                var _x = Number(COMMON.getTranslation(_gCard.attr("transform"))[0]) + Number(_attachMarker.attr("cx")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[0]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[0]);
                var _y = Number(COMMON.getTranslation(_gCard.attr("transform"))[1]) + Number(_attachMarker.attr("cy")) - Number(COMMON.getTranslation(_gArrow.attr("transform"))[1]) + Number(COMMON.getTranslation(_attachMarker.attr("transform"))[1]);

                _arrowLine
                    .attr("x2", _x)
                    .attr("y2", _y);

                _arrowEndPoint
                    .attr("cx", _x)
                    .attr("cy", _y);

                BUSBARVER.directArrow.to = g_connect.id;
                BUSBARVER.directArrow.toMarker = g_connect.attachMarker;
                BUSBARVER.directArrow.x2 = _x;
                BUSBARVER.directArrow.y2 = _y;
                DATA.aArrows.push(BUSBARVER.directArrow);
                ARROW.update(BUSBARVER.directArrow.id, 1);
            } else {
                d3.select("g[id=" + ARROW.ID + BUSBARVER.directArrow.id + "]").remove();
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
        BUSBARVER.focus(DATA.selectedID);

        var _oCard = BUSBARVER.get(d.id);
        document.getElementById("jointer-qty").value = _oCard.qty;
        document.getElementById("busbar-vertical").checked = true;

        var isMarker = false;
        for (var i = 0; i < DATA.aArrows.length; i++) {
            if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                isMarker = true;
                break;
            } else {
                isMarker = false;
            }
        }

        if (isMarker) {
            $('#jointer-qty').attr("disabled", true);
            $('input[name=busbar_position]').attr("disabled", true);
        } else {
            $('#jointer-qty').attr("disabled", false);
            $('input[name=busbar_position]').attr('disabled', false);
        }

        $("#btn-update").show();
        $("#btn-remove").show();
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
            d3.select("g[id=" + BUSBARVER.ID + id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != id;
            });
        }
    },

    focus: function (id) {
        var busbarsIndex = DATA.aCards.findIndex(x => x.id === Number(id));
        if (busbarsIndex == 0) {
        var busbarQty = Number(DATA.aCards[busbarsIndex].qty);

        if (id != null) {
            $('path[id^=' + BUSBARVER.ID_RESIZE_TOP + id + ']').attr("opacity", 0.8);
            $('path[id^=' + BUSBARVER.ID_RESIZE_BOTTOM + id + ']').attr("opacity", 0.8);
            $('g[id^=' + BUSBARVER.ID_ROTATE + id + ']').attr("opacity", 0.8);
            for (var i = 1; i <= busbarQty; i++) {
                $('circle[id^=' + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + id + ']').attr("opacity", 1);
            };
            $('g[id^=' + BUSBARVER.ID_REMOVE + id + ']').attr("opacity", 0.8);
        };
    }
    },

    outFocus: function (id) {
        if (id != null) {
            $('path[id^=' + BUSBARVER.ID_RESIZE_TOP + id + ']').attr("opacity", 0);
            $('path[id^=' + BUSBARVER.ID_RESIZE_BOTTOM + id + ']').attr("opacity", 0);
            $('g[id^=' + BUSBARVER.ID_ROTATE + id + ']').attr("opacity", 0);
            $('circle[id^=' + BUSBARVER.ID_CIRCLE_CONNECT + ']').attr("opacity", 0);
            $('g[id^=' + BUSBARVER.ID_REMOVE + id + ']').attr("opacity", 0);

        } else {
            $('path[id^=' + BUSBARVER.ID_RESIZE_TOP + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('path[id^=' + BUSBARVER.ID_RESIZE_BOTTOM + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + BUSBARVER.ID_CIRCLE_CONNECT + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('g[id^=' + BUSBARVER.ID_ROTATE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('g[id^=' + BUSBARVER.ID_REMOVE + ']').each(function () {
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

            var _gCard = d3.select("g[id=" + BUSBARVER.ID + id + "]");
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

    resize: d3.drag()
        .on("start", function (i, d) {
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            showProperties(10);
            BUSBARVER.focus(DATA.selectedID);

            var _oCard = BUSBARVER.get(d.id);
            document.getElementById("jointer-qty").value = _oCard.qty;
            document.getElementById("busbar-vertical").checked = true;

            var isMarker = false;
            for (var i = 0; i < DATA.aArrows.length; i++) {
                if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                    isMarker = true;
                    break;
                } else {
                    isMarker = false;
                }
            }

            if (isMarker) {
                $('#jointer-qty').attr("disabled", true);
                $('input[name=busbar_position]').attr("disabled", true);
            } else {
                $('#jointer-qty').attr("disabled", false);
                $('input[name=busbar_position]').attr('disabled', false);
            }

            $("#btn-update").show();
            $("#btn-remove").show();
        })
        .on("drag", function (event, d) {
            var _oCard = BUSBARVER.get(d.id);
            var _qty = _oCard.qty;

            var _gCard = d3.select("g[id=" + BUSBARVER.ID + d.id + "]");
            var _cardBox = d3.select("rect[id=" + BUSBARVER.ID_BOX + d.id + "]");
            var _resizeTop = d3.select("path[id=" + BUSBARVER.ID_RESIZE_TOP + d.id + "]");
            var _resizeBottom = d3.select("path[id=" + BUSBARVER.ID_RESIZE_BOTTOM + d.id + "]");
            var _circleTop = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_TOP + d.id + "]");
            var _circleBottom = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_BOTTOM + d.id + "]");
            var _gRotate = d3.select("g[id=" + BUSBARVER.ID_ROTATE + d.id + "]");
            var _gRemove = d3.select("g[id=" + BUSBARVER.ID_REMOVE + d.id + "]");
            var _g_svg = d3.select("#svg-container");
            var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);

            var _topY = Number(_circleTop.attr("cy"));
            var _bottomY = Number(_circleBottom.attr("cy"));

            var _cardHeight = _bottomY - _topY;
            var _minHeight = BUSBARVER.MIN_RANGE2 * (_qty - 1);
            var _range = _cardHeight / (_qty - 1);

            if (this.id.indexOf(BUSBARVER.ID_CIRCLE_TOP) >= 0) {
                var _y;
                var _yPointer = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gCard.attr("transform"))[1]);

                if ((_bottomY - _yPointer) < _minHeight) {
                    _y = _bottomY - _minHeight;
                } else {
                    _y = _yPointer;
                }

                _cardBox.attr("transform", "translate(0, " + (_y - 15) + ")");

                d3.select(this).attr("cy", _y);

                _resizeTop.attr("transform", "translate(0," + (_y + 10) + ")");

                _gRemove.attr("transform", "translate(20," + (_y - 15) + ")");

                for (var i = 1; i <= _qty; i++) {
                    var translateX = _topY + ((i - 1) * _range) - 14;
                    if (i == 1) {
                        translateX = translateX + 1;
                    } else if (i == _qty) {
                        translateX = translateX - 1;
                    }

                    var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                    _circleConnect.attr("transform", "translate(1," + translateX + ")");
                };

                _cardHeight = _bottomY - _y;
                _cardBox.attr("height", _cardHeight);

            } else if (this.id.indexOf(BUSBARVER.ID_CIRCLE_BOTTOM) >= 0) {
                var _y;
                var _yPointer = (d3.pointer(event)[1] - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale - Number(COMMON.getTranslation(_gCard.attr("transform"))[1]);

                if (_yPointer < (+_topY + +_minHeight)) {
                    _y = +_topY + +_minHeight;
                } else {
                    _y = _yPointer;
                }

                d3.select(this).attr("cy", _y);


                _resizeBottom.attr("transform", "translate(0," + (_y - 5) + ")");

                for (var i = 1; i <= _qty; i++) {
                    var translateX = _topY + ((i - 1) * _range) - 14;
                    if (i == 1) {
                        translateX = translateX + 1;
                    } else if (i == _qty) {
                        translateX = translateX - 1;
                    }

                    var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                    _circleConnect.attr("transform", "translate(1," + translateX + ")");
                };

                _cardHeight = _y - _topY;
                _cardBox.attr("height", _cardHeight);

            };

            BUSBARVER.moveArrows(d.id);

            var busbarsIndex = DATA.aCards.findIndex(x => x.id === d.id);
            DATA.aCards[busbarsIndex].width = _cardHeight;
            DATA.aCards[busbarsIndex].range = _range;
            DATA.aCards[busbarsIndex].xTop = COMMON.getTranslation(_resizeTop.attr("transform"))[0];
            DATA.aCards[busbarsIndex].yTop = COMMON.getTranslation(_resizeTop.attr("transform"))[1];
            DATA.aCards[busbarsIndex].cyTop = _circleTop.attr("cy");
            DATA.aCards[busbarsIndex].xBottom = COMMON.getTranslation(_resizeBottom.attr("transform"))[0];
            DATA.aCards[busbarsIndex].yBottom = COMMON.getTranslation(_resizeBottom.attr("transform"))[1];
            DATA.aCards[busbarsIndex].cyBottom = _circleBottom.attr("cy");
            DATA.aCards[busbarsIndex].rectX = COMMON.getTranslation(_cardBox.attr("transform"))[0];
            DATA.aCards[busbarsIndex].rectY = COMMON.getTranslation(_cardBox.attr("transform"))[1];

            _gRotate.attr("transform", "translate(" + (Number(DATA.aCards[busbarsIndex].xBottom) + 20) + "," + Number(DATA.aCards[busbarsIndex].yBottom) + ")");

        })

        .on("end", function (i, d) {
            setAction("dragend");
            var _oCard = BUSBARVER.get(d.id);
            var _qty = _oCard.qty;

            var _gCard = d3.select("g[id=" + BUSBARVER.ID + d.id + "]");
            var _cardBox = d3.select("rect[id=" + BUSBARVER.ID_BOX + d.id + "]");
            var _resizeTop = d3.select("path[id=" + BUSBARVER.ID_RESIZE_TOP + d.id + "]");
            var _resizeBottom = d3.select("path[id=" + BUSBARVER.ID_RESIZE_BOTTOM + d.id + "]");
            var _circleTop = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_TOP + d.id + "]");
            var _circleBottom = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_BOTTOM + d.id + "]");
            var _gRotate = d3.select("g[id=" + BUSBARVER.ID_ROTATE + d.id + "]");

            var _circleTop = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_TOP + d.id + "]");
            var _circleBottom = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_BOTTOM + d.id + "]");

            var _topY = Number(_circleTop.attr("cy"));
            var _bottomY = Number(_circleBottom.attr("cy"));

            var _cardHeight = _bottomY - _topY;

            var _range = (_bottomY - _topY) / Number(_qty - 1);

            for (var i = 1; i <= _qty; i++) {
                var translateX = _topY + ((i - 1) * _range) - 14;
                if (i == 1) {
                    translateX = translateX + 1;
                } else if (i == _qty) {
                    translateX = translateX - 1;
                }

                var _circleConnect = d3.select("circle[id=" + BUSBARVER.ID_CIRCLE_CONNECT + i + "-" + d.id + "]");
                _circleConnect.attr("transform", "translate(1," + translateX + ")");
            };

            BUSBARVER.moveArrows(d.id);

            var busbarsIndex = DATA.aCards.findIndex(x => x.id === d.id);
            DATA.aCards[busbarsIndex].width = _cardHeight;
            DATA.aCards[busbarsIndex].range = _range;
            DATA.aCards[busbarsIndex].xTop = COMMON.getTranslation(_resizeTop.attr("transform"))[0];
            DATA.aCards[busbarsIndex].yTop = COMMON.getTranslation(_resizeTop.attr("transform"))[1];
            DATA.aCards[busbarsIndex].cyTop = _circleTop.attr("cy");
            DATA.aCards[busbarsIndex].xBottom = COMMON.getTranslation(_resizeBottom.attr("transform"))[0];
            DATA.aCards[busbarsIndex].yBottom = COMMON.getTranslation(_resizeBottom.attr("transform"))[1];
            DATA.aCards[busbarsIndex].cyBottom = _circleBottom.attr("cy");
            DATA.aCards[busbarsIndex].rectX = COMMON.getTranslation(_cardBox.attr("transform"))[0];
            DATA.aCards[busbarsIndex].rectY = COMMON.getTranslation(_cardBox.attr("transform"))[1];
        }),

    update: function (id) {

        var _oCurrent = {};
        var _oCard = {};

        var busbarIndex = DATA.aCards.findIndex(x => x.id === id);

        if (busbarIndex > -1) {
            var _qty = parseInt(document.getElementById("jointer-qty").value);
            var _width = Number(BUSBAR.MIN_RANGE2 * (_qty - 1));
            var _position = document.querySelector('input[name="busbar_position"]:checked').value;
            var _range = Math.round(_width / (_qty - 1));
            if (DATA.aCards[busbarIndex].type == "busbar") {
                _oCurrent = BUSBAR.get(id);
                if (_oCurrent.qty != _qty) {
                    if (_oCurrent.width / (_qty - 1) >= BUSBARVER.MIN_RANGE2) {
                        _width = _oCurrent.width;
                        _range = _oCurrent.width / (_qty - 1);
                    }
                } else {
                    _width = _oCurrent.width;
                    _range = Math.round(_width / (_qty - 1));
                }
                _oCard = {
                    "id": _oCurrent.id,
                    "range": _range,
                    "qty": _qty,
                    "width": _width,
                    "x": _oCurrent.x,
                    "y": _oCurrent.y,
                    "rectX": _oCurrent.rectX,
                    "rectY": _oCurrent.rectY,
                    "xTop": "0",
                    "yTop": "23",
                    "cxTop": "-8",
                    "cyTop": "15",
                    "xBottom": "0",
                    "yBottom": Number(_width),
                    "cxBottom": "-8",
                    "cyBottom": BUSBARVER.BUS_Y + Number(_width),
                    "type": "busbarver",
                }
                d3.select("g[id=" + BUSBAR.ID + id + "]").remove();

                DATA.aCards = $.grep(DATA.aCards, function (card) {
                    return card.id != id;
                });

                DATA.aCards.push(_oCard);
                BUSBARVER.draw(_oCard);
            } else if (DATA.aCards[busbarIndex].type == "busbarver") {
                _oCurrent = BUSBARVER.get(id);
                if (_oCurrent.qty != _qty) {
                    if (_oCurrent.width / (_qty - 1) >= BUSBARVER.MIN_RANGE2) {
                        _width = _oCurrent.width;
                        _range = _oCurrent.width / (_qty - 1);
                    }
                } else {
                    _width = _oCurrent.width;
                    _range = Math.round(_width / (_qty - 1));
                }
                _oCard = {
                    "id": _oCurrent.id,
                    "range": _range,
                    "qty": _qty,
                    "width": _width,
                    "x": _oCurrent.x,
                    "y": _oCurrent.y,
                    "rectX": _oCurrent.rectX,
                    "rectY": _oCurrent.rectY,
                    "xTop": _oCurrent.xTop,
                    "yTop": _oCurrent.yTop,
                    "cxTop": _oCurrent.cxTop,
                    "cyTop": _oCurrent.cyTop,
                    "xBottom": _oCurrent.xBottom,
                    "yBottom": Number(_oCurrent.yTop) + _width - 20,
                    "cxBottom": _oCurrent.cxBottom,
                    "cyBottom": Number(_oCurrent.cyTop) + _width,
                    "type": "busbarver",
                }
                d3.select("g[id=" + BUSBARVER.ID + id + "]").remove();

                DATA.aCards = $.grep(DATA.aCards, function (card) {
                    return card.id != id;
                });

                DATA.aCards.push(_oCard);
                BUSBARVER.draw(_oCard);
            }
        }

    },

    rotate: function (e, d) {
        var isMarker = false;

        for (var i = 0; i < DATA.aArrows.length; i++) {
            if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                isMarker = true;
                break;
            } else {
                isMarker = false;
            }
        }
        if (!isMarker) {

            var _oCard = {
                "id": d.id,
                "range": d.range,
                "qty": d.qty,
                "width": d.width,
                "x": d.x,
                "y": d.y,
                "rectX": "0",
                "rectY": "0",
                "xLeft": "10",
                "yLeft": "20",
                "cxLeft": BUSBAR.BUS_X,
                "cyLeft": "32",
                "xRight": Number(d.width),
                "yRight": "20",
                "cxRight": BUSBAR.BUS_X + d.width,
                "cyRight": "32",
                "type": "busbar",
            }
            d3.select("g[id=" + BUSBARVER.ID + d.id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != d.id;
            });

            DATA.aCards.push(_oCard);
            BUSBAR.draw(_oCard);
        }

    },

    delete: function (e) {
        e.stopPropagation();
        BUSBARVER.remove(DATA.selectedID);
    },
}

export default BUSBARVER;