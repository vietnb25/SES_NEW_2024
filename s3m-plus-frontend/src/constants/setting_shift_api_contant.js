import CONS from "./constant";

const SETTING_HOST = CONS.LOCAL_HOST + "common/setting-shift/";

const SETTING_SHIFT_API = {
    LIST: SETTING_HOST,
    LIST_BY_PROJECT_AND_STATUS: SETTING_HOST + "/list",
    ADD: SETTING_HOST + "add",
    LOCK: SETTING_HOST + "lock/",
    DELETE: SETTING_HOST + "delete/",
    UNLOCK: SETTING_HOST + "unlock/",
    GETBYID: SETTING_HOST,
    GETBYPROJECTID: SETTING_HOST + "getByProjectId",
    GET_BY_ID: SETTING_HOST + "getSettingShiftById",
    EDIT: SETTING_HOST + "update-setting-shift/"
}

export default SETTING_SHIFT_API;