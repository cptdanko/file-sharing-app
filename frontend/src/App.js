import "./App.css";
import { CookiesProvider } from "react-cookie";
import { UserAuth } from "./components/UserAuth";
import { Typography, Divider, Container } from "@mui/material";
import { useState } from "react";
import { Dashboard } from "./components/Dashboard";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider/LocalizationProvider";
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';

function App() {

  const [userLoggedIn, setUserLoggedIn] = useState(false);

  return (
    <CookiesProvider>
      
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <div className="App" style={{
          margin: 2,
          padding: 6,
          maxWidth: "1200px",
          marginLeft: "auto",
          marginRight: "auto",
          marginTop: "10px",
          borderRadius: "10px",
          boxShadow: "5px 3px 10px 10px lightgray"
        }}>
          <Typography component="h3" variant="h3">
            Document Sharing App
          </Typography>
          
          <Divider />

          {userLoggedIn ?
            <Dashboard />
            :
            <Container sx={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "flex-start"
            }}>
              <UserAuth userLoggedIn={userLoggedIn} setUserLoggedIn={setUserLoggedIn} />
            </Container>
          }
        </div>
      </LocalizationProvider>
    </CookiesProvider>
  );
}

export default App;
