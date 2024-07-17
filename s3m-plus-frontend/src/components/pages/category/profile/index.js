import React, { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import profileService from '../../../../services/ProfileService';
import $ from 'jquery';
import authService from '../../../../services/AuthService';
import { useFormik } from 'formik';
import './index.css';
import { t } from 'i18next';
const Profile = () => {
  const history = useHistory();
  const [profiledata, setProfiledata] = useState({
    staffName: "",
    email: "",
    img: null,
    id: ""

  });
  const profile = async () => {
    let res = await profileService.detailProfile();
    if (res.status === 200) {
      setProfiledata({
        staffName: res.data.staffName,
        email: res.data.email,
        img: res.data.img,
        id: res.data.id
      });
    }
  }
  function handleClick() {
    history.push("")
  }

  const onInputChange = (e) => {
    setProfiledata({ ...profiledata, [e.target.name]: e.target.value });
  }


  const updateProfile = async (e) => {
    let res = await profileService.updateProfile(profiledata.id, profiledata);
    if (res.status === 200) {
      $.alert({
        title: t('content.title_notify'),
        content: t('content.category.profile.update_account_success')
      });
      setTimeout(() => {
        window.location.reload(false)
      }, 3000);
    }

  }

  function getBase64(event) {
    let file = event.target.files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    if (file.size < 10000000) {
      if (file.type === "image/jpeg" || file.type === "image/jpg" || file.type === "image/png" || file.type === "image/gif" || file.type === "svg" || file.type === "image/tiff" || file.type === "image/bmp" || file.type === "image/webp") {
        reader.onload = (e) => {
          let img = e.target.result;
          setProfiledata({
            ...profiledata,
            img: img
          })
        }
      } else {
        $.alert({
          title: t('content.title_notify'),
          content: 'Định dạng ảnh không hợp lệ (vd: image.jpg, image.png, ...)'

        });
      }

    } else {
      $.alert({
        title: t('content.title_notify'),
        content: 'Size ảnh không được quá 10mb '

      });
    }
  }



  const initialValues = {
    password: "",
    newPassword: "",
    confirmPassword: ""
  }
  const formik = useFormik({
    initialValues,
    onSubmit: async (data) => {
      if (data.newPassword === data.confirmPassword) {
        let res = await profileService.changePassword(profiledata.id, data);
        if (res.status === 200) {
          $("[data-dismiss=modal]").trigger({ type: "click" });
          $.confirm({
            type: 'red',
            typeAnimated: true,
            title: t('content.title_notify'),
            content: t('content.category.profile.reset_pw_success'),
            buttons: {
              confirm: {
                text: 'ok',
                action: function () {
                  authService.logout();
                  history.push("/login");
                }
              }
            }
          });
        } else {
          $.alert({
            title: t('content.title_notify'),
            content: t('content.category.profile.current_pw_not_match'),
          });
        }
      } else {
        $.alert({
          title: t('content.title_notify'),
          content: t('content.category.profile.confirm_pw_not_match')
        });
      }
    }
  })

  useEffect(() => {
    document.title = "Profile";
    profile();
  }, []);

  return (
    <div id="page-body">
      <div id="main-title">
        <h5 className="d-block mb-0 float-left text-uppercase"><i className="far fa-clipboard" /> &nbsp;{t('content.category.profile.account_info')}</h5>
      </div>

      <div id="main-content">

        <table className="table table-input">
          <tbody>
            <tr>
              <th width="200px" className='text-uppercase'>{t('content.staff_name')}</th>
              <td>
                <input type="text" className="form-control" name="staffName" value={profiledata.staffName} onChange={(e) => onInputChange(e)} />
              </td>
            </tr>
            <tr>
              <th width="150px">EMAIL</th>
              <td>
                <input type="text" className="form-control" name="email" value={profiledata.email} onChange={(e) => onInputChange(e)} />
              </td>
            </tr>
            <tr>
              <th width="150px">AVATAR</th>
              <td>
                <input type="file" name="file" id="file" value={profiledata.file} onChange={(e) => getBase64(e)} />
              </td>
            </tr>
            <tr>
              <th width="150px">
                <p className="textSize text-uppercase"> - {t('content.category.profile.img_size')}: {"<"} 10 mb<br></br>
                  - {t('content.category.profile.format')}: jpg, png, ... </p>
              </th>
              <td>
                {
                  profiledata.img == null ?
                    <img id="blah" src="/resources/image/no_avatar.png" alt="avt" className="profileviewImage mt-2 mb-2" /> :
                    <img id="blah" src={profiledata.img} alt="avt" className="profileviewImage mt-2 mb-2" />
                }
              </td>
            </tr>
            {/* password */}
            <tr>
              <th width="150px" className='text-uppercase'>{t('content.password')}</th>
              <td>
                <button className="btn btn-info text-uppercase" data-toggle="modal" data-target="#exampleModal">{t('content.category.profile.reset_password')}</button>
                {/* Modal   */}
                <div className="modal fade" id="exampleModal" tabIndex={-1} aria-labelledby="exampleModalLabel" aria-hidden="true">
                  <div className="modal-dialog">
                    <div className="modal-content">
                      <div className="modal-header">
                        <h5 className="modal-title" id="exampleModalLabel">{t('content.category.profile.reset_password')}</h5>
                        <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </div>
                      <div className="modal-body">
                        {/* form password  */}
                        <form onSubmit={formik.handleSubmit}>

                          <div className="form-group">
                            <label >{t('content.category.profile.current_password')}</label>
                            <input type="password" className="form-control" name="password" onChange={formik.handleChange} required />
                          </div>
                          <div className="form-group">
                            <label >{t('content.category.profile.new_password')}</label>
                            <input type="password" className="form-control" name="newPassword" onChange={formik.handleChange} required />
                            <span className="form-text small text-muted">
                              {/* description  */}
                            </span>
                          </div>
                          <div className="form-group">
                            <label >{t('content.category.profile.confirm_password')}</label>
                            <input type="password" className="form-control" name="confirmPassword" onChange={formik.handleChange} required />
                            <span className="form-text small text-muted">
                              {/* description */}
                            </span>
                          </div>
                          <div className="modal-footer">
                            <button type="submit" className="btn btn-primary" >{t('content.save')}</button>
                          </div>
                        </form>
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            {/* password */}
          </tbody>
        </table>
        <div id="main-button">
          <button type="button" className="btn btn-outline-secondary btn-agree mr-2" onClick={updateProfile} >
            <i className="fa-solid fa-check" />
          </button>
          <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={handleClick}>
            <i className="fa-solid fa-xmark" />
          </button>
        </div>

      </div>
    </div>
  )
}

export default Profile
