import commonService from './CommonService';
import CONS from './../constants/constant';

class UserService {

    #type;
    #commonService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getUser = async (userName) => {
        let url = CONS.LOCAL_HOST + "common/user/" + userName;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

}

export default new UserService();