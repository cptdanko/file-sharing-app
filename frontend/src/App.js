import "./App.css";
import { useEffect, useState } from "react";
import { Button, Container, Form, FormGroup, Input, Label } from "reactstrap";
import { API_FILE_PATH } from "./Constants";
import { CookiesProvider, useCookies, Cookies } from "react-cookie";

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loggedIn, setLoggedIn] = useState(false);
  const [userFiles, setUserFiles] = useState(null);
  const [cookie, setCookie] = useCookies(["user"]);
  const [file, setFile] = useState(null);
  const [shareWithEmailAdd, setShareWithEmailAdd] = useState("");
  const [fileToShare, setFileToShare] = useState("");
  const [fileUploading, setFileUploading] = useState(false);

  useEffect(() => {
    if (cookie.user && cookie.user.username) {
      setLoggedIn(true);
    }
  });
  function getUserNamePassForBasicAuth() {
    return "Basic " + btoa(username + ":" + password);
  }

  const myDialogModal = document.querySelector("#dialog");
  const myCloseModalBtn = document.querySelector("#closeModalBtn");

  if (myDialogModal) {
    myCloseModalBtn && myCloseModalBtn.addEventListener("click", () => myDialogModal.close());
  }
  function openShareDialog(toShare) {
    console.log("about to share file:" + toShare);
    setFileToShare(toShare);
    myDialogModal.showModal();
  }
  async function login() {
    console.log("In the login method");
    // call the healthcheck method to know it's valid
    let userNamePasswd = "Basic " + btoa(username + ":" + password);
    const resp = await fetch("/healthcheck", {
      headers: {
        Authorization: userNamePasswd,
      },
    });
    if (resp.status !== 200) {
      alert("Invalid username/password, please try again");
    } else {
      console.log("YAY successfully logged in");
      setLoggedIn(true);
      const userObj = {
        username,
        userNamePasswd,
      };
      setCookie("user", userObj, "/");
    }
  }
  async function filesUploaded() {
    if (username.length === 0) {
      alert("You need to login before using this");
      return;
    }
    // fetching the no of files uploaded
    const userNamePasswd = getUserNamePassForBasicAuth();
    const urlStr = `/api/file/list?userId=${username}`;
    const resp = await fetch(urlStr, {
      headers: {
        Authorization: userNamePasswd,
      },
    });
    const json = await resp.json();
    setUserFiles(json.data);
  }
  function onFileSelect(event) {
    console.log(event.target.value);
    setFile(event.target.value);
  }

  async function deleteFile(filename) {
    const urlStr = `${API_FILE_PATH}/delete/${filename}?userId=${username}`;
    const userNamePasswd = getUserNamePassForBasicAuth();
    const resp = await fetch(urlStr, {
      method: "DELETE",
      headers: {
        Authorization: userNamePasswd,
      },
    });
    const status = await resp.status;
    console.log(`Result of delete operation -> ${status}`);
    filesUploaded();
  }

  function uploadFile(event) {
    event.preventDefault();
    setFileUploading(true);
    let frm = document.getElementById("uploadDocument");
    const formData = new FormData(frm);
    fetch(`/api/file/upload`, {
      method: "POST",
      headers: {
        Authorization: cookie.user.userNamePasswd,
      },
      body: formData,
    }).then((resp) => {
      console.log(JSON.stringify(resp));
      console.log(resp.status);
      if (resp.status === 403) {
        alert("Max file upload limit reached! Delete files first before uploading now.");
      } else {
        filesUploaded();
      }
    });
  }
  function signup(event) {
    event.preventDefault();
    // const frm = document.getElementById("signupForm");
    // console.log(frm);
    // const formData = new FormData(frm);
    // console.log(formData);
    const jsonSignup = {};
    jsonSignup["name"] = document.getElementsByName("name")[0].value;
    jsonSignup["username"] = document.getElementsByName("username")[0].value;
    jsonSignup["password"] = document.getElementsByName("password")[0].value;
    fetch("/api/user/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(jsonSignup),
    }).then((resp) => {
      console.log(JSON.stringify(resp));
    });
  }

  function logout(event) {
    event.preventDefault();
    setCookie("user", null, "/");
    setLoggedIn(false);
    setUserFiles(null);
    setUsername("");
  }
  function shareFiles() {
    const emailAdd = shareWithEmailAdd.target.value;
    console.log("In the share files function");
    console.log(fileToShare);
    const filename = `${cookie.user.username}/${fileToShare}`;
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const usernamePass = getUserNamePassForBasicAuth();

    if (pattern.test(emailAdd)) {
      console.log("valid email id supplied, calling server");
      // get the logged in user
      const msg = `${cookie.user.username} shared ${fileToShare} with you`;

      const emailBody = {
        to: emailAdd,
        cc: [],
        bcc: [],
        subject: msg,
        body: `Dear ${emailAdd}, kindly download the attachment`,
        filesToAttach: [filename],
      };
      fetch("/api/user/sendMail", {
        method: "POST",
        headers: {
          Authorization: usernamePass,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(emailBody),
      }).then((resp) => {
        console.log(resp);
        myDialogModal.close();
        setFileUploading(false);
      }).catch(e => {
        console.log(`Error occured ${e}`);
        setFileUploading(false);
        myDialogModal.close();
      });
    } else {
      console.log("Invalid email address");
    }
  }
  return (
    <CookiesProvider>
      <div className="App">
        <h1> Welcome to Document Sharing app </h1>
        <hr />
        <div>
          <div id="loginForm">
            <h3> User Details </h3>
            <p>
              Username:{" "}
              <input type="text" onChange={(e) => setUsername(e.target.value)} value={username} />
            </p>
            <p>
              Password:{" "}
              <input
                type="password"
                onChange={(e) => setPassword(e.target.value)}
                value={password}
              />
            </p>
          </div>
          <div>
            {loggedIn ? (
              <div>
                <div>
                  Logged in as
                  <b>
                    {cookie ? (
                      <b>
                        <i> {cookie.user.username}</i>
                      </b>
                    ) : (
                      <span> --- </span>
                    )}
                  </b>{" "}
                </div>
                <div>
                  <button className="btn btn-sm btn-success" onClick={logout}>
                    {" "}
                    Logout{" "}
                  </button>
                </div>
              </div>
            ) : (
              <button className="btn btn-sm btn-success" onClick={() => login()}>
                {" "}
                Login{" "}
              </button>
            )}
          </div>
        </div>
        {loggedIn ? (
          <span> --- </span>
        ) : (
          <div id="signup">
            <h3> Form for new user sign up</h3>
            <hr />
            <div style={{ display: "flex", flexDirection: "column" }}>
              <form onSubmit={signup} id="signupForm">
                <div style={{ margin: 8 }}>
                  {" "}
                  <input type="text" name="name" placeholder="Your name" />{" "}
                </div>
                <div style={{ margin: 8 }}>
                  <input type="text" name="username" placeholder="Your email address" />
                </div>
                <div style={{ margin: 8 }}>
                  <input type="password" name="password" placeholder="Your secret password" />
                </div>
                <div>
                  <button className="btn btn-sm btn-success" type="submit">
                    {" "}
                    Signup{" "}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
        <hr />

        <div id="listFilesUploaded">
          <div>
            {userFiles != null && userFiles.length > 0 ? (
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                }}
              >
                <div style={{ marginBottom: 8 }}>
                  {" "}
                  Files Uploaded by{" "}
                  <i>
                    <b>{username}</b>
                  </i>
                </div>
                {userFiles.map((file) => (
                  <div
                    key={file}
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      marginRight: "5px",
                    }}
                  >
                    <div style={{ marginLeft: 8, marginRight: 8 }}> {file}</div>
                    <div style={{ marginLeft: 8, marginRight: 8 }}>
                      {" "}
                      <a href={`/api/file/${file}/download?userId=${username}`} target="_blank">
                        {" "}
                        Download{" "}
                      </a>
                    </div>
                    <div>
                      <a
                        style={{
                          textDecoration: "underline",
                          color: "#0d6efd",
                          marginRight: "5px",
                        }}
                        onClick={() => deleteFile(file)}
                      >
                        Delete
                      </a>{" "}
                    </div>
                    <div>
                      <a
                        style={{
                          textDecoration: "underline",
                          color: "#0d6efd",
                          marginLeft: "5px",
                        }}
                        id="openModalBtn"
                        onClick={() => openShareDialog(file)}
                      >
                        {" "}
                        Share{" "}
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div>
                {userFiles !== null && userFiles.length == 0 ? (
                  <span>
                    {" "}
                    No Files Uploaded by{" "}
                    <b>
                      <i>{username} </i>
                    </b>{" "}
                  </span>
                ) : (
                  <p>
                    {" "}
                    <button className="btn btn-sm btn-success" onClick={filesUploaded}>
                      Show
                    </button>
                  </p>
                )}
              </div>
            )}
          </div>
        </div>
        <hr />
        <div
          id="uploadFile"
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <div>
            <div style={{ margin: 8 }}>
              <b>Upload a new file</b>
            </div>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <form onSubmit={uploadFile} id="uploadDocument">
                <input type="file" name="file" onChange={onFileSelect} />
                <input type="hidden" value={username} name="userId" />
                <div style={{ marginTop: 8 }}>
                  <button className="btn btn-sm btn-success" type="submit">
                    {" "}
                    Upload{" "}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
        <div>
          <dialog
            id="dialog"
            style={{
              width: 300,
              height: 200,
              borderStyle: 2,
              borderColor: "blue",
            }}
          >
            <h2> Share file</h2>
            <p>
              <input
                name={shareWithEmailAdd}
                onChange={setShareWithEmailAdd}
                placeholder="enter email addresses"
              />
            </p>
            <div>
            {fileUploading ? <h5>Loading file ...</h5> : <b>-</b>}
            </div>
            <div
              style={{
                margin: 8,
                padding: 8,
              }}
            >
              <button style={{ marginRight: 8 }} onClick={shareFiles}>
                {" "}
                Share{" "}
              </button>
              <button id="closeModalBtn"> Cancel </button>
            </div>
          </dialog>
        </div>
      </div>
    </CookiesProvider>
  );
}

export default App;
