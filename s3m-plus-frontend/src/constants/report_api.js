import CONS from "./constant";
const REPORT_API = {
    LIST: CONS.LOCAL_HOST + "load/report/",
    LISTPV: CONS.LOCAL_HOST + "pv/report/",
    LISTGRID: CONS.LOCAL_HOST +"grid/report/",
    GENERATEREPORT: CONS.LOCAL_HOST + "load/report/generateReports/",
    GENERATEREPORTPV: CONS.LOCAL_HOST + "pv/report/generateReports/",
    GENERATEREPORTGRID: CONS.LOCAL_HOST + "grid/report/generateReports/",
    ADDREPORT: CONS.LOCAL_HOST + "load/report/addReport/",
    ADDREPORTPV: CONS.LOCAL_HOST + "pv/report/addReport/",
    ADDREPORTGRID: CONS.LOCAL_HOST + "grid/report/addReport/",
    DOWNLOAD_REPORT: CONS.LOCAL_HOST + "load/report/download",
    DOWNLOAD_REPORTPV: CONS.LOCAL_HOST + "pv/report/download",
    DOWNLOAD_REPORTGRID: CONS.LOCAL_HOST + "grid/report/download",
    DELETE_REPORT: CONS.LOCAL_HOST + "load/report/delete/",
    DELETE_REPORTPV: CONS.LOCAL_HOST + "pv/report/delete/",
    DELETE_REPORTGRID: CONS.LOCAL_HOST + "grid/report/delete/",
    DOWNLOAD_NEW_REPORT:  CONS.LOCAL_HOST + "common/report/download",
    EXPORT_REPORT:  CONS.LOCAL_HOST + "common/report/export"
}
export default REPORT_API;