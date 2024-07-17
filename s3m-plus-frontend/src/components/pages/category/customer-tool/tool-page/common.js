import * as d3 from "d3";
import { DATA, g_connect, g_svg, g_container, action } from "./data";
const $ = window.$;
const COMMON = {
    
    getDocWidth: function () {

        return $(window).width();
    },

    getDocHeight: function () {
        var D = document;

        return Math.max(
            D.body.scrollHeight, D.documentElement.scrollHeight,
            D.body.offsetHeight, D.documentElement.offsetHeight,
            D.body.clientHeight, D.documentElement.clientHeight
        );
    },

    getTranslation: function (transform) {
        var g = document.createElementNS("http://www.w3.org/2000/svg", "g");
        g.setAttributeNS(null, "transform", transform);
        var matrix = g.transform.baseVal.consolidate().matrix;

        return [matrix.e, matrix.f, matrix.d];
    },

    intersectRect: function (r1, r2) {
        var r1 = r1.getBoundingClientRect();
        var r2 = r2.getBoundingClientRect();

        return !(r2.left > r1.right || r2.right < r1.left || r2.top > r1.bottom || r2.bottom < r1.top);
    },

    dragStart: function (d) {
        d3.select(this).attr("class", "active");
    },

    dragOn: function dragged(d, i) {
    },

    dragEnd: function (d) {
        d3.select(this).classed("active", false);
        g_connect.id = "";
    },
};

export default COMMON;