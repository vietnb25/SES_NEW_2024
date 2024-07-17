import CONS from "./constant";

const OBJECT_HOST = CONS.LOCAL_HOST + "common/object/";

const BASE_API_OBJECT = {
    OBJECT: {
        LIST: OBJECT_HOST + "list",
        BY_DEVICE_TYPE : OBJECT_HOST +"getObjectTypeByDeviceType"
    },
    OBJECT_TYPE: {
        LIST: OBJECT_HOST + "listObjectType",
        LIST_AREA: OBJECT_HOST + "listArea",
        LIST_MST: OBJECT_HOST + "getListObjectMst"
    }
}

export default BASE_API_OBJECT;