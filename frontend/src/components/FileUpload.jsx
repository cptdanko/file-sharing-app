import { Alert, Box, Button, Container, styled, Typography } from "@mui/material"
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { AlertDialog } from "../dialogs/AlertDialog";



export const FileUpload = (props) => {
    const { setFileUploaded } = props;
    const [file, setFile] = useState("");
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [username, setUsername] = useState("");
    const [alertMessage, setAlertMessage] = useState("");
    const [alertHeader, setAlertHeader] = useState("");
    const [alertOpen, setAlertOpen] = useState(false);
    const [uploadBtnText, setUploadBtnText] = useState("Upload File");
    const [uploadDisabled, setUploadDisabled] = useState(false);
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
        setUploadDisabled(true);
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
                setUploadBtnText("Upload File");
                setUploadDisabled(false);
                setFileUploaded(true);
            })
            .catch(err => {
                console.error("Error uploading file ", err);
                setUploadBtnText("Upload File");
                setUploadDisabled(false);
            });
    }
    const handleAlertClose = () => {
        setAlertHeader("");
        setAlertMessage("");
        setAlertOpen(false);
    }
    return (
        <Container component='div'>
            <Typography sx={{ marginBottom: 2 }} variant="h5">File Upload</Typography>
            <form onSubmit={uploadFile} id="uploadDocument" style={{
                display: 'flex',
                justifyContent: "flex-start",
                alignContent: "center",
                flexDirection: "column",
                marginTop: 4
            }}>
                <Box sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    marginLeft: 'auto',
                    marginRight: 'auto',
                }}>
                    {cookies.user ?
                        <>
                            <input type="file" name="file" onChange={onFileSelect} />
                            <input type="hidden" value={username} name="username" />
                            <Button sx={{ marginTop: 1 }}
                                size="small"
                                variant="contained"
                                disabled={uploadDisabled}
                                type="submit">
                                {uploadBtnText} </Button>
                        </>
                        :
                        <Button
                            size="small"
                            tabIndex={-1}
                            variant="contained"
                            disabled={uploadDisabled}
                            type="submit">
                            {uploadBtnText} </Button>
                    }
                </Box>
                <Box sx={{
                    marginTop: 2, opacity: 0.5,
                    marginLeft: "auto",
                    marginRight: "auto"
                }}>
                    <Alert variant="outlined" severity="info">
                        Only [".pdf", ".doc", ".docx", ".txt", ".md", ".json"] file types are supported so far
                    </Alert>
                </Box>
            </form>

            <AlertDialog open={alertOpen}
                handleClose={handleAlertClose}
                title={alertHeader}
                content={alertMessage} />

        </Container>
    )
}
