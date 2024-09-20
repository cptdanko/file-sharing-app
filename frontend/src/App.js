import "./App.css";
import { CookiesProvider, useCookies } from "react-cookie";
import { UserAuth } from "./components/UserAuth";
import { Typography, Divider, Container } from "@mui/material";
import { useState } from "react";
import { Dashboard } from "./components/Dashboard";
import { Overview } from "./components/Overview";
import { BrowserRouter as Router, Route, Routes, BrowserRouter } from 'react-router-dom';
import { HomeView } from "./components/Menu/HomeView";
import { FileUpload } from "./components/FileUpload";
import { FileList } from "./components/FileList";

function App() {

  const [fileUploadDone, setFileUploadDone] = useState(false);
  const [userLoggedIn, setUserLoggedIn] = useState(false);
  const [showDetailsMenu, setShowDetailsMenu] = useState(true);


  const toggleState = () => {
    setUserLoggedIn(!userLoggedIn);
  }
  const toggleSideMenuVisibility = () => {
    console.log("toggleSideMenuVisibility called");
    setShowDetailsMenu(!showDetailsMenu);
  };

  return (
    <CookiesProvider>
      <div className="App">
        <Divider />
        <Router>
          <Routes>
            <Route path="/login" element={<UserAuth />} />
            <Route path="/overview" element={<Overview />} />
            <Route path="/upload" element={<FileUpload />} />
            <Route path="/filelist" element={<FileList />} />
          </Routes>

          {userLoggedIn ?
            <Overview toggleLoginState={toggleState} />
            :
            <Container sx={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "flex-start"
            }}>
              <UserAuth userLoggedIn={userLoggedIn} setUserLoggedIn={setUserLoggedIn} />
            </Container>
          }
        </Router>

      </div>
    </CookiesProvider>
  );
}

export default App;
