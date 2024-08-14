import { Alert, Box, Button, Container, styled, Typography } from "@mui/material"
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { AlertDialog } from "../dialogs/AlertDialog";



export const FileUpload = () => {
    const [file, setFile] = useState("");
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [username, setUsername] = useState("");
    const [alertMessage, setAlertMessage] = useState("");
    const [alertHeader, setAlertHeader] = useState("");
    const [alertOpen, setAlertOpen] = useState(false);
    const [uploadBtnText, setUploadBtnText] = useState("Upload File");
    // const [file]

    useEffect(() => {
        setUsername(cookies.user && cookies.user.username);
    });

    const onFileSelect = (event) => {
        setFile(event.target.value);
    }
    const uploadFile = (e) => {
        e.preventDefault();
        setUploadBtnText("Uploading File...");
        let frm = document.getElementById("uploadDocument");
        const formData = new FormData(frm);
        formData["file"] = file;
        fetch(`/api/file/upload`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
            body: formData,
        })
            .then(resp => resp.json())
            .then(data => {
                setFile("");
                if (data.status > 299) {
                    setAlertHeader("Error uploading");
                    setAlertMessage(data.message);
                    setAlertOpen(true);
                }
                setUploadBtnText("Upload File")
            })
            .catch(err => {
                console.error("Error uploading file ", err);
                setUploadBtnText("Upload File");
            });
    }
    const handleAlertClose = () => {
        setAlertHeader("");
        setAlertMessage("");
        setAlertOpen(false);
    }
    return (
        <Container>
            <Box>
                <Typography variant="h5">File Upload</Typography>
                <form onSubmit={uploadFile} id="uploadDocument" style={{
                    display: 'flex',
                    justifyContent: "center",
                    alignContent:"center",
                    flexDirection: "column",
                    marginTop: 4
                }}>
                    <Box sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'center',
                        marginLeft: 'auto',
                        marginRight: 'auto',
                        width: "50%"
                    }}>
                        {cookies.user ?
                            <>
                                <input type="file" name="file" onChange={onFileSelect} />
                                <input type="hidden" value={username} name="username" />
                                <Button sx={{ marginTop: 1 }}
                                    size="small"
                                    variant="contained"
                                    type="submit">
                                    {uploadBtnText} </Button>
                            </>
                            :
                            <Button
                                size="small"
                                tabIndex={-1}
                                variant="contained"
                                disabled
                                type="submit">
                                {uploadBtnText} </Button>
                        }
                    </Box>
                    <Box sx={{marginTop: 2, opacity: 0.5, 
                    width: "80%", 
                    marginLeft: "auto",
                    marginRight: "auto"}}>
                        <Alert variant="outlined" severity="info">
                            Only [".pdf", ".doc", ".docx", ".txt", ".md", ".json"] file types are supported so far
                        </Alert>
                    </Box>
                </form>
            </Box>
            <AlertDialog open={alertOpen}
                handleClose={handleAlertClose}
                title={alertHeader}
                content={alertMessage} />

        </Container>
    )
}
