import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/object"
const BASE_API_OBJECT_TYPE = CONS.LOCAL_HOST + "common/objectType"

const OBJECT_TYPE_API = {
    GET_OBJECT_TYPE: BASE_API + "/getAllObjectType",
    GET_OBJECT_TYPE_SELECT: CONS.LOCAL_HOST + "common/objectType",
    ADD_OBJECT_TYPE: BASE_API + "/add",
    UPDATE_OBJECT_TYPE: BASE_API + "/update",
    GET_OBJECT_TYPE_BY_ID: BASE_API + "/getObjectById",
    DELETE_OBJECT_TYPE_BY_ID: BASE_API + "/delete",
    CHECK_OBJECT_ID: BASE_API + "/checkObjectIdLinkToDevice?id=",
    GET_OBJECT_TYPE_ID: BASE_API + "/getObjectTypeIdById/",
    GET_OBJECT_ID: BASE_API + "/getObjectId",
    LIST: BASE_API_OBJECT_TYPE + "/listObjectType",
    SEARCH_OBJECT_TYPE :  BASE_API_OBJECT_TYPE + "/searchObjectType?keyword=",
    DELETE_OBJECT_TYPE : BASE_API_OBJECT_TYPE + "/deleteObjectType/",
    INSERT_OBJECT_TYPE: BASE_API_OBJECT_TYPE + "/addObjectTypeMst",
    EDIT_OBJECT_TYPE: BASE_API_OBJECT_TYPE + "/updateObjectType",
    GET_BY_ID : BASE_API_OBJECT_TYPE +"/getObjectTypeById/",
    GET_OBJECT_TYPE_BY_IDS : BASE_API_OBJECT_TYPE + "/getObjectTypeByIds",
    GET_OBJECT_TYPE_SELECT_DEVICE : CONS.LOCAL_HOST + "common/select-device/object-type"
}

export default OBJECT_TYPE_API
