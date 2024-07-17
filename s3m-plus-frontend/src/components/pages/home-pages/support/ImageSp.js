import React from "react";

const ImageSp = (props) => {
  const clickImage = () => {
    window.open(props.src);
  };
  return (
    <>
      <div style={{ width: "70%" }} className="text-center">
        <img
          // onClick={clickImage}
          style={{ width: "90%" }}
          src={props.src}
          alt={props.text}
        />
        <p style={{ width: "90%", marginTop: "5px" }}>
          <i style={{ fontSize: "12px" }}>{props.text}</i>
        </p>
      </div>
    </>
  );
};

export default ImageSp;
