export let DATA = [];

export const setEmpty = () => {
    DATA = []
}

export const setDecriptionByValue = (warningType, value) => {
    var strs = value.split(",");
    if (warningType == 101) {
        return 'Uan || Ubn || Ucn > ' + strs[0]
    }
    if (warningType == 102) {
        return 'Uan || Ubn || Ucn < ' + strs[0]
    }
    if (warningType == 103) {
        return 'Ia || Ib || Ic >= ' + strs[0] + '* Idm'
    }
    if (warningType == 104) {
        return '(((Ia+Ib+Ic)/3)/Idm) * 100  > ' + strs[0] + ' & cosA || cosB || cosC < ' + (strs[1] != undefined ? strs[1] : "-")
    }
    if (warningType == 105) {
        return 'F > ' + strs[0]
    }
    if (warningType == 106) {
        return 'F < ' + strs[0]
    }
    if (warningType == 107) {
        return '(((Ia+Ib+Ic)/3)/Idm) * 100 > ' + strs[0] + ' & (Imax – Imin)/Imin > ' + (strs[1] != undefined ? strs[1] : "-")
    }
    if (warningType == 108) {
        return 'IA_H1 || ... || IA_H10 || IB_H1 || ... || IB_H10 || IC_H1|| ...|| IC_H10 >= ' + strs[0] + ' - IA_H11 || ... || IA_H16 || IB_H11 || ... || IB_H16 || IC_H11|| ...|| IC_H16 >= ' + (strs[1] != undefined ? strs[1] : "-") + '- IA_H17 || ... || IA_H22 || IB_H17 || ... || IB_H22 || IC_H17|| ...|| IC_H22 >= ' + (strs[2] != undefined ? strs[2] : "-") + '- IA_H23 || ... || IA_H34 || IB_H23 || ... || IB_H34 || IC_H23|| ...|| IC_H34 >= ' + (strs[3] != undefined ? strs[3] : "-") + ' - IA_H35 || ... || IA_HN || IB_H34 || ... || IB_HN || IC_H34|| ...|| IC_HN >= ' + (strs[4] != undefined ? strs[4] : "-") + ' & Idm >= ' + (strs[5] != undefined ? strs[5] : "-")
    }
    if (warningType == 109) {
        return 'VAN_H(n) || VBN_H(n) || VCN_H(n) >= ' + strs[0] + ' & Idm >= ' + (strs[1] != undefined ? strs[1] : "-")
    }
    if (warningType == 110) {
        return 'THD_Van || THD_Vbn || THD_Vcn >= ' + strs[0] + ' & Idm >= ' + (strs[1] != undefined ? strs[1] : "-")
    }
    if (warningType == 111) {
        return 'THD_Ia || THD_Ib || THD_Ic >= ' + strs[0] + ' & Idm >= ' + (strs[1] != undefined ? strs[1] : "-")
    }
    if (warningType == 112) {
        return 'PFa|| PFb || PFc < ' + strs[0]
    }
    if (warningType == 113) {
        return 'Ua || Ub || Uc <= ' + strs[0] + '(V)'
    }
    if (warningType == 201) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 202) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 203) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 204) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 205) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 206) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 207) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 208) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 209) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 210) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 211) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 212) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 213) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 214) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 215) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 216) {
        return 'Cảnh báo vận hành'
    }

    if (warningType == 301) {
        return 'Nhiệt độ (T) >= ' + strs[0] + '°C'
    }
    if (warningType == 302) {
        return 'Nhiệt độ (T) <= ' + strs[0] + '°C'
    }
    if (warningType == 303) {
        return 'Độ ẩm (Humidity) >= ' + strs[0] + '(%)'
    }
    if (warningType == 304) {
        return 'Độ ẩm (Humidity) <= ' + strs[0] + '(%)'
    }

    if (warningType == 401) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 402) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 403) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 404) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 405) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 406) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 407) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 408) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 409) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 410) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 411) {
        return 'Cảnh báo vận hành'
    }
    if (warningType == 412) {
        return 'Cảnh báo vận hành'
    }

    if (warningType == 501) {
        return 'Indicator  >=  ' + strs[0]
    }
    if (warningType == 601) {
        return 'Indicator  >=  ' + strs[0]
    }

}