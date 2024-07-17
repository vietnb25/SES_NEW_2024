import CONS from '../constants/constant';

/*
 * Hàm dùng chung cho hệ thống
 *
*/

class Converter {
    VIEW_TYPE = CONS.VIEW_TYPE_ELECTRIC_POWER;
    DEFAULT_VALUE = CONS.ELECTRIC_POWER_NUMBER;


    convertElectricPower = (viewType, value) => {
        if(viewType === this.VIEW_TYPE.G){
            return Math.ceil((value / CONS.ELECTRIC_POWER_NUMBER.BILLION) * 100) / 100;
        }else if(viewType === this.VIEW_TYPE.M){
            return Math.ceil((value / CONS.ELECTRIC_POWER_NUMBER.MILLION) * 100) / 100;
        }
        else {
            return Math.ceil(value / (CONS.ELECTRIC_POWER_NUMBER.THOUSAND) * 100) / 100;
        }
    }
    
    convertLabelElectricPower = (viewType, typeValue) => {
        if(viewType === this.VIEW_TYPE.G){
            return `[G${typeValue}]`;
        }else if(viewType === this.VIEW_TYPE.M){
            return `[M${typeValue}]`;
        }
        else {
            return `[k${typeValue}]`;
        }
    }
    
    setViewType =  (value) => {
        if (value >= this.DEFAULT_VALUE.BILLION) {
            return  this.VIEW_TYPE.G;
        }else if(value >= this.DEFAULT_VALUE.MILLION){
            return  this.VIEW_TYPE.M;
        }else{
            return  this.VIEW_TYPE.K;
        }
    }

   
}

export default new Converter();



