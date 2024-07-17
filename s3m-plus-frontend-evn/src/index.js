import React from 'react';
import  { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';

import { BrowserRouter as Router } from 'react-router-dom';

import i18n from './i18n/index';
import { I18nextProvider } from 'react-i18next';

//theme
import "primereact/resources/themes/lara-light-indigo/theme.css";     
    
//core
import "primereact/resources/primereact.min.css";

//icons
import "primeicons/primeicons.css"; 


const container = document.getElementById('root');

const root = createRoot(container);

root.render(
  <I18nextProvider i18n={i18n}>
        <Router>
          <App />
        </Router>
      </I18nextProvider>
);
