import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "load/warning"

const WARNING_LOAD_API = {
    GET_WARNINGS : BASE_API,
    GET_WARNINGS_BY_WARNING_TYPE: BASE_API + "/type/",
    GET_DETAIL_BY_DEVICEID: BASE_API + "/detail",
    DOWNLOAD: BASE_API + "/download",
    UPDATE: BASE_API + "/update"
}

export default WARNING_LOAD_API
