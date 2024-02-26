import "./App.css";
import { useEffect, useState } from "react";
import { Button, Container, Form, FormGroup, Input, Label } from "reactstrap";

function App() {
  const [ping, setPing] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loggedIn, setLoggedIn] = useState(false);
  const [userFiles, setUserFiles] = useState([]);

  useEffect(() => {
    doHealthCheck();
  });
  function getUserNamePassForBasicAuth() {
    return "Basic " + btoa(username + ":" + password);
  }
  async function doHealthCheck() {
    const resp = await fetch("/ping");
    const txt = await resp.text();
    setPing(txt);
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
    }
  }
  async function filesUploaded() {
    // fetching the no of files uploaded
    let userNamePasswd = getUserNamePassForBasicAuth();
    const urlStr = `/api/file/list?userId=${username}`;
    const resp = await fetch(urlStr, {
      headers: {
        Authorization: userNamePasswd,
      },
    });
    const json = await resp.json();
    setUserFiles(json.data);
  }
  async function uploadFile() {}
  async function deleteFile() {}
  async function downloadFile(filename) {
    let userNamePasswd = getUserNamePassForBasicAuth();
    const urlStr = `/api/file/${filename}/download?userId=${username}`;
    const resp = await fetch(urlStr, {
      headers: {
        Authorization: userNamePasswd,
      },
    });
    const json = await resp.json();
  }

  return (
    <div className="App">
      <h1> Welcome to Document Sharing app </h1>
      <div id="pingCheck">
        <h3>Ping state</h3>
        <p>
          {" "}
          It is <b>{ping}</b>
        </p>
      </div>
      <div id="loginForm">
        <Container>
          <p>
            Username:{" "}
            <input
              type="text"
              onChange={(e) => setUsername(e.target.value)}
              value={username}
            />
          </p>
          <p>
            Password:{" "}
            <input
              type="password"
              onChange={(e) => setPassword(e.target.value)}
              value={password}
            />
          </p>
        </Container>
        <div></div>
        <div>
          {loggedIn ? (
            <span>
              Logged in as
              <b>
                <i> {username} </i>
              </b>{" "}
            </span>
          ) : (
            <button onClick={() => login()}> Login </button>
          )}
        </div>
      </div>
      <hr />
      <div id="signupForm">
        <h3> Form for new user sign up</h3>
        <hr />
      </div>
      <div id="listFilesUploaded">
        <h3> File list with download/delete buttons</h3>
        <hr />
        <div>
          {userFiles.length > 0 ? (
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
              }}
            >
              <span>
                {" "}
                Files Uploaded by{" "}
                <i>
                  <b>{username}</b>
                </i>
              </span>
              {userFiles.map((file) => (
                <div
                  style={{
                    display: "flex",
                    flexDirection: "row",
                    marginRight: "5px",
                  }}
                >
                  <div key={file}> {file}</div>
                  <div>
                    {" "}
                    <button>Download</button>{" "}
                  </div>
                  <div>
                    <button>Delete</button>{" "}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <button onClick={filesUploaded}>Show</button>
          )}
        </div>
      </div>
      <hr />
      <div id="uploadFile">
        <h3> Upload new file</h3>
        <hr />
        <Container></Container>
      </div>
    </div>
  );
}

export default App;
