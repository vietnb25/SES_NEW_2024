import BASE_API_PROFILE from '../constants/profile_api_constant';
import authService from './AuthService'
import commonService from './CommonService';
import CONS from './../constants/constant';

class ProfileService {
    #type;
    #auth;
    #commonService;

    constructor(){
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
        this.#auth = authService;
    }

    detailProfile = async () => {
        let url = BASE_API_PROFILE.PROFILE.LIST + this.#auth.getAuth().username;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateProfile = async (id, profile)=>{
        let url = BASE_API_PROFILE.PROFILE.UPDATE+id;
        let type = this.#type.POST;    
        return await this.#commonService.sendRequest(type, url, profile);
    }

    changePassword = async (id, user)=>{
        let url = BASE_API_PROFILE.PROFILE.CHANGEPASSWORD + id;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, user);
    }
}

export default new ProfileService();
