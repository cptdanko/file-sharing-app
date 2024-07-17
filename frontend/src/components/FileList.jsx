import { Box, Button, Chip, Container, Dialog, DialogActions, DialogContent, DialogTitle, Divider, TextField, Typography } from "@mui/material"
import { useState } from "react";
import { useCookies } from "react-cookie";
import { API_FILE_PATH } from "../Constants";
import { AlertDialog } from "../dialogs/AlertDialog";

export const FileList = (props) => {
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [userFiles, setUserFiles] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [fileToShare, setFileToShare] = useState("");
    const [showFiles, setShowFiles] = useState(false);
    const [alertOpen, setAlertOpen] = useState(false);
    const [notLoginErr, setNotLoginError] = useState(false);
    const [alertHeader, setAlertHeader] = useState("");
    const [alertMessage, setAlertMessage] = useState("");

    const handleAlertClose = () => {
        setAlertOpen(false);
    }
    const handleLoginAlertClose = () => {
        setNotLoginError(false);
    }

    const filesUploaded = async () => {
        console.log("About to fetch user files");
        if (!cookies.user) {
            setNotLoginError(true);
            return;
        }
        // fetching the no of files uploaded
        const urlStr = `/api/file/list?userId=${cookies.user.username}`;
        const resp = await fetch(urlStr, {
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
        });
        const json = await resp.json();
        const fl = json.data;
        setUserFiles(fl);
        setShowFiles(true);
    }

    async function deleteFile(filename) {
        const urlStr = `${API_FILE_PATH}/delete/${filename}?userId=${cookies.user.username}`;
        const resp = await fetch(urlStr, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
        });
        const status = await resp.status;
        console.log(`Result of delete operation -> ${status}`);
        filesUploaded();
    }

    const openDialog = (file) => {
        console.log(`Want to share file ${file}`)
        setFileToShare(file);
        setDialogOpen(true);
    }
    const closeDialog = () => {
        setDialogOpen(false);
    }

    const shareFile = (e) => {
        e.preventDefault();
        const emailAdd = document.getElementById("shareEmailId").value;
        const filename = `${cookies.user.username}/${fileToShare}`;
        console.log("valid email id supplied, calling server");
        // get the logged in user
        const msg = `${cookies.user.username} shared ${fileToShare} with you`;

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
                Authorization: `Bearer ${cookies.user.token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(emailBody),
        })
            .then(resp => resp.json())
            .then(data => {
                setDialogOpen(false);
                // setFileUploading(false);
                if (data.status > 299) {
                    const msg = `${data.message}`;
                    setAlertHeader("File Share Error");
                    setAlertMessage(msg);
                    setAlertOpen(true);
                }
            }).catch(e => {
                console.log(`Error occured ${e}`);
                console.log(JSON.stringify(e));
                setDialogOpen(false);
                // setFileUploading(false);
            });
    }
    const hideFiles = () => {
        setShowFiles(false);
    }
    const downloadFile = (file) => {
        const url = `/api/file/${file}/download`;
        console.log(`Url to download file is ${url}`);
        fetch(url, {
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
        })
        .then(resp => resp.blob())
        .then(blob => {
            const url2 = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url2;
            a.download = file;
            a.click();
            URL.revokeObjectURL(url2);
        }).catch(err => {
            console.log(`Error occured`);
            console.error('File download error', err);
        });
    }

    return (
        <Container sx={{
            marginLeft: "auto",
            marginRight: "auto"
        }}>
            <Box>
                {cookies.user && showFiles && userFiles != null && userFiles.length > 0 ? (
                    <Box p={2} gap={2} sx={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        overflow: "scroll",
                        boxShadow: "3px 1px 5px 5px grey",
                        borderRadius: 8,
                        marginLeft: "auto",
                        marginRight: "auto",
                        marginBottom: 5
                    }}>
                        <Box sx={{ display: 'flex', flexDirection: 'column'}}>
                            <Button variant="contained" onClick={hideFiles} sx={{marginBottom: 2}}>
                                Hide
                            </Button>
                            <Typography component={'span'} >
                                Files Uploaded by <Chip label={cookies.user.username}></Chip>
                            </Typography>
                        </Box>
                        <Divider orientation="horizontal" flexItem />
                        {userFiles.map((file) => (
                            <Box key={file} sx={{
                                display: "flex",
                                justifyContent: "center",
                                flexDirection: "column",
                            }}>
                                <Box>
                                    <Typography component={'span'} variant="body"><b>{file}</b>
                                    </Typography>
                                </Box>
                                <Box>
                                    <Button size="small"
                                        onClick={() => downloadFile(file)}> Download </Button>
                                    <Button size="small"
                                        onClick={() => deleteFile(file)}> Delete </Button>
                                    <Button size="small"
                                        onClick={() => openDialog(file)}> Share </Button>
                                </Box>
                            </Box>

                        ))}
                    </Box>
                ) : (
                    <Box gap={2} sx={{marginBottom: 5}}>
                        {userFiles !== null && userFiles.length === 0 ? (
                            <Typography component={'span'} > No Files Uploaded by
                                <Chip>{cookies.user.username}</Chip>
                            </Typography>
                        ) : (
                            <Box gap={2}>
                                <Button variant="contained" size="small" onClick={filesUploaded}>
                                    Show
                                </Button>
                            </Box>
                        )}
                    </Box>
                )}
            </Box>

            <AlertDialog open={alertOpen}
                handleClose={handleAlertClose}
                title={alertHeader}
                content={alertMessage} />
            
            <AlertDialog open={notLoginErr}
                handleClose={handleLoginAlertClose}
                title="Not Logged in"
                content="You need to login first before using this" />


            <Dialog
                open={dialogOpen}
                onClose={closeDialog}
                PaperProps={{
                    component: 'form',
                    onSubmit: shareFile
                }}>
                <DialogTitle> Share File</DialogTitle>
                <DialogContent>
                    <TextField autoFocus
                        required
                        margin="dense"
                        label="Email address to share with"
                        placeholder="Email address to share with"
                        type="email"
                        id="shareEmailId"
                    />
                </DialogContent>
                <DialogActions>
                    <Button variant="contained"
                        size="small"
                        onClick={closeDialog}>Close</Button>
                    <Button variant="contained"
                        size="small" type="submit">Share</Button>
                </DialogActions>
            </Dialog>
        </Container >
    )
}