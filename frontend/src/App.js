import "./App.css";
import { CookiesProvider } from "react-cookie";
import { UserAuth } from "./components/UserAuth";
import { Typography, Divider } from "@mui/material";
import { FileList } from "./components/FileList";
import { FileUpload } from "./components/FileUpload";

function App() {

  return (
    <CookiesProvider>
      <div className="App" style={{ margin: 2, 
                              padding: 6,
                              maxWidth: "800px",
                              marginLeft: "auto",
                              marginRight: "auto",
                              marginTop: "10px",
                              borderRadius: "10px",
                              boxShadow: "5px 3px 10px 10px lightgray"}}>
        <Typography component="h1" variant="h3">
          File Sharing App
        </Typography>
        
        <UserAuth />
        
        <Divider sx={{ margin: 2 }} flexItem orientation="horizontal"></Divider>

        <FileUpload />

        <Divider sx={{ margin: 2 }} flexItem orientation="horizontal"></Divider>

        <FileList />
      </div>
    </CookiesProvider>
  );
}

export default App;
