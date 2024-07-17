import BASE_API_CUSTOMER from "../constants/customer_api_constant";
import commonService from './CommonService';
import CONS from './../constants/constant';

class CustomerService {
    #type;
    #commonService;

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    getListCustomer = async () => {
        let url = BASE_API_CUSTOMER.CUSTOMER.LIST;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    addCustomer = async customer => {
        let url = BASE_API_CUSTOMER.CUSTOMER.ADD;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, customer);
    }

    deleteCustomer = async customerId => {
        let url = BASE_API_CUSTOMER.CUSTOMER.DELETE + customerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    getCustomer = async customerId => {
        let url = BASE_API_CUSTOMER.CUSTOMER.CUSTOMER + customerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    updateCustomer = async (customerId, customer) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.UPDATE + customerId;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, customer);
    }

    searchCustomer = async keyword => {
        let url = BASE_API_CUSTOMER.CUSTOMER.SEARCH + keyword;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    checkCustomerCode = async (information) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.CHECK;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, information);
    }

    getOtp = async (userName, customerId) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.GET_OTP + userName + '/' + customerId;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, null);
    }

    checkOtp = async (data) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.CHECK_OTP;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    expireOtp = async (data) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.EXPIRE_OTP;
        let type = this.#type.POST;
        return await this.#commonService.sendRequest(type, url, data);
    }

    getCustomerIds = async (userName) => {
        let url = BASE_API_CUSTOMER.CUSTOMER.LIST_IDS;
        let type = this.#type.GET;
        return await this.#commonService.sendRequest(type, url, { userName: userName });
    }
}

export default new CustomerService();