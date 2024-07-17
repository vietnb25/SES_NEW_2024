import CONS from "./constant";

const DEVICE_TYPE_HOST = CONS.LOCAL_HOST + "common/device-type/";

const BASE_API_DEVICE_TYPE = {
    DEVICETYPE: {
        LIST: DEVICE_TYPE_HOST + "list",
        SYSTEMTYPE: DEVICE_TYPE_HOST + "listDeviceType/"
    }
}

export default BASE_API_DEVICE_TYPE;