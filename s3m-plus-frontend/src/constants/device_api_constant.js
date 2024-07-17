import CONS from "./constant";

const DEVICE_HOST = CONS.LOCAL_HOST + "common/device/";

const BASE_API_DEVICE = {
    DEVICE: {
        DETAILS: DEVICE_HOST,
        LIST: DEVICE_HOST + "list",
        ADD: DEVICE_HOST + "add",
        ADD_DEVICE: DEVICE_HOST + "addDevice",
        UPDATE: DEVICE_HOST + "updateDevice/",
        SEARCH: DEVICE_HOST + "search?keyword=",
        DELETE: DEVICE_HOST + "delete/",
        GET_DEVICE_ID: DEVICE_HOST + "/getDeviceId",
        GET_ID: DEVICE_HOST + "/getDeviceCode",
        LIST_DEVICE_BY_CUSTOMER_ID: DEVICE_HOST + "listDevice/",
        LIST_DEVICE_BY_PROJECT_ID: DEVICE_HOST + "listDeviceByProject/",
        LIST_DEVICE_ONE_LEVEL_BY_CUSTOMER_SYS: DEVICE_HOST + "listDeviceOneLevelByCusSys/",
        LIST_DEVICE_ONE_LEVEL_BY_PROJECT_SYS: DEVICE_HOST + "listDeviceOneLevelByProSys/",
        INFO_DEVICE_AND_WARNING_INSTANCE: DEVICE_HOST + "info/",
        INSTANCE_DATA: DEVICE_HOST + "instance/",
        LIST_DATA_INSTANCE: DEVICE_HOST + "listDataInstance/",
        LIST_DEVICE_TWO_LEVEL_BY_CUSTOMER_SYS: DEVICE_HOST + "listDeviceTwoLevelByCusSys/",
        LIST_DEVICE_TWO_LEVEL_BY_PROJECT_SYS: DEVICE_HOST + "listDeviceTwoLevelByProSys/",
        LIST_DEVICE_BY_OBJECT: DEVICE_HOST + "listDeviceByObject/",
        LIST_DEVICE_CACULATE_FLAG: DEVICE_HOST + "listCaculateFlag/",
        LIST_DEVICE_ALL_FLAG: DEVICE_HOST + "listAllFlag/",
        LIST_DEVICE_BY_DEVICE_TYPE: DEVICE_HOST + "list-by-device-type",
        GET_OBJECT_TYPE: DEVICE_HOST + "getObjectType/",
        GET_LIST_OBJECT_TYPE: DEVICE_HOST + "getListObjectType/",
        GET_LIST_OBJECT: DEVICE_HOST + "getListObject/",
        GET_LIST_DEVICE: DEVICE_HOST + "getListDevice/",
        LIST_BY_IDS: DEVICE_HOST + "list-by-ids",
        GET_DEVICE_GATEWAY: DEVICE_HOST + "getDeviceGateway",
        GET_DATA_DEVICE_GATEWAY: DEVICE_HOST + "getDataDeviceGateway",
        GET_LIST_DEVICE_BY_OBJECT_TYPE: DEVICE_HOST + "list-by-object-type",
        GET_LIST_DEVICE_BY_AREA: DEVICE_HOST + "list-by-area",
        GET_LIST_DEVICE_BY_LOAD_TYPE: DEVICE_HOST + "list-by-load-type",
        LIST_DATA_INSTANCE_FRAME2: DEVICE_HOST + "listDataInstanceFrame2/",
        EXPORT_DATA_INSTANCE: DEVICE_HOST + "exportDataInstance/"
    }
}

export default BASE_API_DEVICE;