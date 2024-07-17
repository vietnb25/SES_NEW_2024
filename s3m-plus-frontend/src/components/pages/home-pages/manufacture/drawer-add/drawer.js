// Drawer.js
import React, { useRef, useState } from 'react';
import './drawer.css';
import { t } from 'i18next';

const Drawer = ({ isOpen, onClose, children, style }) => {
  const drawerRef = useRef();
  const [add, setAdd] = useState(false);

  const handleOutsideClick = (event) => {
    if (isOpen && drawerRef.current && !drawerRef.current.contains(event.target)) {
      onClose();
    }
  };

  const drawerClassName = isOpen ? add == true ? 'drawer openFull' : 'drawer open' : 'drawer';
  return (
    <div ref={drawerRef} className={drawerClassName}>
      <div className="drawer-content" style={style}>
        <button className="close-btn" onClick={() => {
          onClose();
          setAdd(false)
        }
        }>
          {t('content.close')}
        </button>
        {/* <button className="close-btn" onClick={() => setAdd(!add)}>
            add
          </button> */}
        {children}
      </div>
    </div>
  );
};

export default Drawer;
