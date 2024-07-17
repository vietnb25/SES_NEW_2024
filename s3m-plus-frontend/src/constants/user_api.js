import CONS from "./constant";

const URL = CONS.LOCAL_HOST + "common/user";

const USER_API = {
    LIST: URL + "/list/",
    ADD: URL + "/add/",
    SEARCH: URL + "/search",
    DELETE: URL + "/delete/",
    UPDATE: URL + "/update/",
    UNLOCK_USER: URL + "/unlock/",
    LOCK_USER: URL + "/lock/",
    USER_BY_CUSTOMER_IDS: URL + "/usersByCustomerIds",
    GET_USER: URL,
    UPDATE_PRIORITY: URL + "/updatePriorityIngredients",
}

export default USER_API;
