import CONS from "../constant";

const PERMISSION_HOST = CONS.LOCAL_HOST + "common/permission";

const PERMISSION_API = {
    ADD: PERMISSION_HOST + "/",
    GET_USER_PERMISSION: PERMISSION_HOST + "/",
    CATEGORY: PERMISSION_HOST + "/category/",
}

export default PERMISSION_API;
