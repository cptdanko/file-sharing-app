import { googleLogout } from "@react-oauth/google";
import "./LeftMenu.css";
import { useCookies } from "react-cookie";
import { Link } from "react-router-dom";

export const LeftMenu = (props) => {
  const { toggleMenuVibility, toggleLoginState, toggleSelected } = props;
  const [cookies, setCookie, removeCookie] = useCookies(['user']);

  const showMenu = () => {
    console.log("show menu clicked");
  };
  const logout = () => {
    console.log("logout clicked");
    toggleLoginState();
    removeCookie("user");
    googleLogout();
  };
  return (
    <div>
      <h2> Welcome Bhuman </h2>
      <p> You are awesome </p>
      <div className="Links" id="menuLinks">
        <h5> 
          <button onClick={() => {
            console.log('Upload clicked');
            toggleSelected('UPLOAD');
          }}>Upload</button>
        </h5>
        <h5> 
          <button onClick={() => toggleSelected('DOCUMENTS')}>Documents</button>
        </h5>
        <h5> 
          <button onClick={() => toggleSelected('SCHEDULE')}>Schedule</button>
        </h5>
        <h5> 
          <button onClick={() => toggleSelected('NOTIFICATIONS')}>Notifications</button>
        </h5>
        <h5> 
          <button onClick={() => toggleSelected('DELETED')}>Deleted</button>
        </h5>
      </div>
      <div className="Button-box">
        <button className="Button" onClick={toggleMenuVibility}>
          {" "}
          Show Menu{" "}
        </button>
        <button className="Button" onClick={logout}>
          {" "}
          Logout{" "}
        </button>
      </div>
    </div>
  );
};
