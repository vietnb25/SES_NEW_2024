import CONS from "./constant";

const SETTING_HOST = CONS.LOCAL_HOST + "common/setting/";

const SETTING_API = {
    LIST: SETTING_HOST,
    LIST_HISTORY: SETTING_HOST + "getSettingHistory",
    GETBYID: SETTING_HOST,
    LIST_SETTING_BY_DEVICES: SETTING_HOST + "settings-by-devices",
    EDIT: SETTING_HOST + "update/",
    LIST_SETTING_BY_DEVICE_TYPE: SETTING_HOST + "settings-by-device-type",
    EDIT_BY_DEVICES: SETTING_HOST + "/update-by-devices"
}

export default SETTING_API;