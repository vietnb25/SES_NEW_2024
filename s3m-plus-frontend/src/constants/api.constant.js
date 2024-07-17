const HOST = "http://localhost:9999/";
const SUPER_MANAGER_HOST = HOST + "common/super-manager/";
const CABLE_HOST = HOST + "common/cable/";

const BASE_API = {
    LOGIN: HOST + "auth/login",
    USER: {
        ADD: HOST + "/load/user/add"
    },
    PROFILE:{
        LIST: HOST + "common/profile/",
        UPDATE : HOST+ "commom/profile/updateProfile/",
        CHANGEPASSWORD : HOST +"commom/changePassword/"
    },
    
    SUPERMANAGER: {
        DETAILS: SUPER_MANAGER_HOST,
        LIST: SUPER_MANAGER_HOST + "list",
        ADD: SUPER_MANAGER_HOST + "add",
        EDIT: SUPER_MANAGER_HOST + "edit/",
        SEARCH: SUPER_MANAGER_HOST + "search/",
        DELETE: SUPER_MANAGER_HOST + "delete/"
    },

    CABLE : {
        LIST: CABLE_HOST + "list",
        ADD: CABLE_HOST + "add",
        GETBYID: CABLE_HOST + "/",
        EDIT : CABLE_HOST + "update/",
        DELETE: CABLE_HOST + "delete/",
        SEARCH : CABLE_HOST + "list/"
    }
    
}
export default BASE_API;