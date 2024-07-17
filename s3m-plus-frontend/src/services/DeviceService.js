import BASE_API_DEVICE from "../constants/device_api_constant";
import CONS from './../constants/constant';
import commonService from './CommonService';

class DeviceService {
    #type;
    #commonService

    constructor() {
        this.#type = CONS.METHOD_TYPE;
        this.#commonService = commonService;
    }

    listDevice = (projectId, systemType, deviceType) => {
        let url = BASE_API_DEVICE.DEVICE.LIST;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType,
            deviceType: deviceType
        });
    }

    listDeviceType = () => {
        let url = BASE_API_DEVICE.DEVICE.TYPES;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    addDevice = (device) => {
        let url = BASE_API_DEVICE.DEVICE.ADD;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, device);
    }

    addDeviceMst = (device) => {
        let url = BASE_API_DEVICE.DEVICE.ADD_DEVICE;
        let type = this.#type.POST;
        return this.#commonService.sendRequest(type, url, device);
    }

    searchDevice = async (keyword, customerId, projectId) => {
        let url = BASE_API_DEVICE.DEVICE.SEARCH + keyword + "&customerId=" + customerId + "&projectId=" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    detailsDevice = async (deviceId) => {
        let url = BASE_API_DEVICE.DEVICE.DETAILS + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    deleteDevice = async (deviceId) => {
        let url = BASE_API_DEVICE.DEVICE.DELETE + deviceId;
        let type = this.#type.DELETE;
        return this.#commonService.sendRequest(type, url, null);
    }

    updateDevice = async (deviceId, device) => {
        let url = BASE_API_DEVICE.DEVICE.UPDATE + deviceId;
        let type = this.#type.PUT;
        return this.#commonService.sendRequest(type, url, device);
    }

    getDeviceByCustomerId = async (customerId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_BY_CUSTOMER_ID + customerId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDeviceByProjectId = async (customerId, projectId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_BY_PROJECT_ID + customerId + "/" + projectId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getListDeviceOneLevelByCusSys = async (customerId, objectTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_ONE_LEVEL_BY_CUSTOMER_SYS + customerId + "/" + objectTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getListDeviceOneLevelByProSys = async (customerId, projectId, objectTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_ONE_LEVEL_BY_PROJECT_SYS + customerId + "/" + projectId + "/" + objectTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getInforDeviceByDeviceId = async (customerId, deviceId) => {
        let url = BASE_API_DEVICE.DEVICE.INFO_DEVICE_AND_WARNING_INSTANCE + customerId + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDataInstance = async (customerId, systemType, deviceType, deviceId) => {
        let url = BASE_API_DEVICE.DEVICE.INSTANCE_DATA + customerId + "/" + systemType + "/" + deviceType + "/" + deviceId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getListDataInstance = async (customerId, deviceId, fromDate, toDate, optionTime) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DATA_INSTANCE + customerId + "/" + deviceId + "/" + optionTime;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { fromDate: fromDate, toDate: toDate });
    }

    getListDeviceTwoLevelByCusSys = async (customerId, objectTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_TWO_LEVEL_BY_CUSTOMER_SYS + customerId + "/" + objectTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getListDeviceTwoLevelByProSys = async (customerId, projectId, objectTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_TWO_LEVEL_BY_PROJECT_SYS + customerId + "/" + projectId + "/" + objectTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null);
    }

    getDeviceByObjectTypeId = async (customerId, objectTypeId, systemTypeId, objectTypeName, projectId) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_BY_OBJECT + customerId + "/" + objectTypeId + "/" + systemTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { objectTypeName: objectTypeName, projectId: projectId });
    }

    getListDeviceCalculateFlag = async (projectId, systemType, area, load, objectType) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_CACULATE_FLAG;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType,
            objectType: objectType,
            area: area,
            load: load
        });
    }

    getListDeviceAllFlag = async (projectId, systemType, area, load, objectType, deviceType) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_ALL_FLAG;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            projectId: projectId,
            systemType: systemType,
            objectType: objectType,
            area: area,
            load: load,
            deviceType: deviceType,
        });
    }
    getListDeviceByDeviceType = async (customer, projectId, systemType, deviceType) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DEVICE_BY_DEVICE_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            customer: customer,
            project: projectId,
            systemType: systemType,
            deviceType: deviceType,
        });
    }

    getObjectType = async (objectTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.GET_OBJECT_TYPE + objectTypeId;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null)
    }

    getDeviceId = async () => {
        let url = BASE_API_DEVICE.DEVICE.GET_DEVICE_ID;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null)
    }

    getId = async () => {
        let url = BASE_API_DEVICE.DEVICE.GET_ID;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null)
    }
    listDeviceByIds = (ids) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_BY_IDS;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            ids: ids
        });
    }

    getDeviceGateway = async () => {
        let url = BASE_API_DEVICE.DEVICE.GET_DEVICE_GATEWAY;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, null)
    }

    getDataDeviceGateway = async (customerId, projectId, deviceId) => {
        let url = BASE_API_DEVICE.DEVICE.GET_DATA_DEVICE_GATEWAY;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, {
            customerId,
            projectId,
            deviceId
        })
    }

    getListObjectType = async (customerId, projectId, systemTypeId, projectIds) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_OBJECT_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, systemTypeId: systemTypeId, projectIds: projectIds })
    }

    getListObject = async (customerId, projectId, objectTypeId, systemTypeId, projectIds) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_OBJECT;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, objectTypeId: objectTypeId, systemTypeId: systemTypeId, projectIds: projectIds })
    }

    getListDevice = async (customerId, projectId, objectId, systemTypeId) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_DEVICE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { customerId: customerId, projectId: projectId, objectId: objectId, systemTypeId: systemTypeId })
    }
    selectDeviceByObjectType = async (systemType, project, objectType) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_DEVICE_BY_OBJECT_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { systemType, project, objectType })
    }
    getDeviceByLoadTypeSelectDevice = async (systemType, project, loadType) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_DEVICE_BY_LOAD_TYPE;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { systemType, project, loadType })
    }

    getListDataInstanceFrame2 = async (customerId, deviceId, fromDate, toDate, optionTime) => {
        let url = BASE_API_DEVICE.DEVICE.LIST_DATA_INSTANCE_FRAME2 + customerId + "/" + deviceId + "/" + optionTime;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { fromDate: fromDate, toDate: toDate });
    }

    getDeviceByAreaSelectDevice = async (systemType, project, area) => {
        let url = BASE_API_DEVICE.DEVICE.GET_LIST_DEVICE_BY_AREA;
        let type = this.#type.GET;
        return this.#commonService.sendRequest(type, url, { systemType, project, area })
    }

    exportDataInstance = (customerId, deviceId, fromDate, toDate, optionTime, projectName, deviceName, optionName, typeDate, optionNameChild) => {
        let url = BASE_API_DEVICE.DEVICE.EXPORT_DATA_INSTANCE + customerId + "/" + deviceId + "/" + optionTime;
        let type = this.#type.DOWNLOAD_NEW;
        return this.#commonService.sendRequest(type, url, {
            fromDate: fromDate, toDate: toDate, projectName: projectName,
            deviceName: deviceName, optionName: optionName, typeDate: typeDate, optionNameChild: optionNameChild
        });
    }
}

export default new DeviceService();
