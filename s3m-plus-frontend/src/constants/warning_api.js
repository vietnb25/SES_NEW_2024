import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/warning"

const WARNING_API = {
    GET_WARNINGS: BASE_API,
    GET_WARNED_DEVICE_LOST_SIGNAL: BASE_API + "/listDeviceLostSignal",
    GET_WARNINGS_BY_WARNING_TYPE: BASE_API + "/type/",
    GET_INFOR_BY_DEVICEID: BASE_API + "/detail",
    UPDATE: BASE_API + "/update",
    DOWNLOAD: BASE_API + "/download",
    GET_WARNINGS_DEVICE_BY_WARNING_TYPE: BASE_API + "/operation/type/",
    GET_LIST_WARNED_DEVICE: BASE_API + "/listWarnedDevice",
    GET_INFOR_WARNED_DEVICE: BASE_API + "/getInfoWarnedDevice",
    GET_INFOR_WARNED_DEVICE_FRAME_2: BASE_API + "/getInfoWarnedDeviceFrame2",
    GET_LIST_DATA_WARNING: BASE_API + "/getListDataWarning",
    GET_LIST_DATA_WARNING_FRAME_2: BASE_API + "/getListDataWarningFrame2",
}

export default WARNING_API
