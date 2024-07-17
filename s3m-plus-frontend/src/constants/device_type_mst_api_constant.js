import CONS from "./constant";

const DEVICE_TYPE_HOST = CONS.LOCAL_HOST + "common/device-type-mst/";

const BASE_API_DEVICE_TYPE_MST = {
    DEVICETYPE: {
        LIST: DEVICE_TYPE_HOST + "list",
        LIST_DEVICE_TYPE: DEVICE_TYPE_HOST + "listDeviceType",
        SEARCH: DEVICE_TYPE_HOST + "searchDeviceType",
        ADD_DEVICE_TYPE : DEVICE_TYPE_HOST + "addDeviceType",
        UPDATE_DEVICE_TYPE: DEVICE_TYPE_HOST + "updateDeviceType",
        DELETE: DEVICE_TYPE_HOST + "deleteDeviceType/",
        GET_BY_ID : DEVICE_TYPE_HOST + "getDeviceTypeById/"

    }
}

export default BASE_API_DEVICE_TYPE_MST;