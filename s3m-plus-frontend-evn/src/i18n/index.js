import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import locale_vi from "./locales/translation_vi.json";
import locale_en from "./locales/translation_en.json";

const resources = {
    vi: {
        translation: locale_vi
    },
    en: {
        translation: locale_en
    }
}

i18n
.use(initReactI18next)
.init({
    resources,
    lng: "vi"
});

export default i18n;