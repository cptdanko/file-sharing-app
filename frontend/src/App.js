import logo from './logo.svg';
import './App.css';
import { useEffect, useState } from 'react';

function App() {
  const [ping, setPing] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
      doHealthCheck();
  });

  async function doHealthCheck() {
      const resp = await fetch('/ping');
      const txt = await resp.text();
      setPing(txt);
  }
  async function login() {
    console.log("In the login method");
    // call the healthcheck method to know it's valid
    let userNamePasswd = "Basic " + btoa(username + ":" + password);
    const resp = await fetch("/healthcheck", {
      headers: {
        "Authorization": userNamePasswd
      },
    });
    if(resp.status !== 200) {
        alert("Invalid username/password, please try again");
    } else {
      console.log("YAY successfully logged in");
      setLoggedIn(true);
    }
  }
  async function filesUploaded() {
    // fetching the no of files uploaded

  }
  async function uploadFile() {

  }
  async function deleteFile() {

  }
  async function downloadFile() {
    
  }
  
  return (
    <div className="App">
      <h1> Welcome to Document Sharing app </h1>
        <div id="pingCheck">
          <h3>Ping state</h3>
          <p> It is <b>{ping}</b></p>
        </div>
        <div id="loginForm">
          <p>
            Username: <input type="text" onChange={(e) => setUsername(e.target.value)} value={username} />
          </p>
          <p>
            Password: <input type="password" onChange={(e) => setPassword(e.target.value)}  value={password} />
          </p>
          <div>
            <button onClick={() => login()}> Login </button>
          </div>
          <div>
            User logged in? {loggedIn ? <b>Yes</b> : <b>No</b>}
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

        </div>
        <div id="uploadFile"> 
        <h3> Upload new file</h3>
        <hr />
        
        </div>
    </div>
  );
}

export default App;
