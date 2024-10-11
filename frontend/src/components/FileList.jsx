import { Alert, Box, Button, Chip, Container, Dialog, DialogActions, DialogContent, DialogTitle, Divider, TextField, Typography } from "@mui/material"
import { useContext, useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { API_FILE_PATH } from "../Constants";
import { AlertDialog } from "../dialogs/AlertDialog";
import { DateTimeRange } from "./DateRangeSelection";
import "./components.css";
import { ScheduleContext } from "./ScheduleContext";

export const FileList = (props) => {
    const { fileUploadDone, setFileUploadDone } = props;
    const { schedule } = useContext(ScheduleContext);

    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [userFiles, setUserFiles] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [scheduleDialogOpen, setScheduleDialogOpen] = useState(false);
    const [fileToShare, setFileToShare] = useState("");
    const [showFiles, setShowFiles] = useState(false);
    const [alertOpen, setAlertOpen] = useState(false);
    const [notLoginErr, setNotLoginError] = useState(false);
    const [alertHeader, setAlertHeader] = useState("");
    const [alertMessage, setAlertMessage] = useState("");
    const [shareBtnTxt, setShareBtnTxt] = useState("Share");
    const [shareBtnDisabled, setShareBtnDisabled] = useState(false);
    const scheduleContext = useContext(ScheduleContext);

    useEffect(() => {
        if (fileUploadDone) {
            filesUploaded();
            setFileUploadDone(false);
        }
    });

    const handleAlertClose = () => {
        setAlertHeader("");
        setAlertMessage("");
        setAlertOpen(false);
    }
    const handleLoginAlertClose = () => {
        setNotLoginError(false);
    }

    const filesUploaded = async () => {
        if (!cookies.user) {
            setNotLoginError(true);
            return;
        }
        // fetching the no of files uploaded
        const urlStr = `/api/file/list?userId=${cookies.user.username}`;
        fetch(urlStr, {
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
        })
            .then(resp => resp.json()
                .then(data => {
                    if (data.status === 403 || data.status === 401) {
                        setAlertHeader("Expired Token");
                        setAlertMessage(data.data);
                        setAlertOpen(true);
                    } else {
                        setUserFiles(data.data);
                        setShowFiles(true);
                    }
                }).catch(err => console.log(err)));
    }

    async function deleteFile(filename) {
        const urlStr = `${API_FILE_PATH}/delete/${filename}?userId=${cookies.user.username}`;
        const resp = await fetch(urlStr, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
            },
        });
        filesUploaded();
    }

    const openDialog = (file) => {
        setFileToShare(file);
        setDialogOpen(true);
    }

    const openScheduleDialog = (file) => {
        setFileToShare(file);
        setScheduleDialogOpen(true);
    }

    const scheduleDialogClose = (e) => {
        console.log("In the clopse schedule dialog");
        setScheduleDialogOpen(false);
        const timeWindow = `${scheduleContext.schedule.from}-${scheduleContext.schedule.to}`;
        
        const postObj = {
            "timeWindow": timeWindow,
            "receivers": [scheduleContext.schedule.receiver],
            "sendersEmail": cookies.user.username,
            "sendersName": cookies.user.google ? cookies.user.google.name: cookies.user.name,
            "isRecurring": false
        };
        const url = '/api/schedule/';
        fetch(url, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(postObj)
        }).then(resp => {
            console.log(resp);
        });
    }

    const saveScheduleInDB = (e) => {
        setScheduleDialogOpen(false);
    }

    const closeDialog = () => {
        setDialogOpen(false);
    }

    const shareFile = (e) => {
        setShareBtnDisabled(true);
        setShareBtnTxt("Sharing...");
        e.preventDefault();
        const emailAdd = document.getElementById("shareEmailId").value;
        const filename = `${cookies.user.username}/${fileToShare}`;
        // get the logged in user
        const msg = `${cookies.user.name} shared ${fileToShare} with you`;

        const emailBody = {
            to: emailAdd,
            cc: [],
            bcc: [],
            subject: msg,
            body: `Dear ${emailAdd}, kindly download the attachment`,
            filesToAttach: [filename],
        };
        fetch("/api/social/sendMail", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(emailBody),
        }).then(() => {
            setDialogOpen(false);
            setShareBtnDisabled(false);
            setShareBtnTxt("Share");
        }).catch(e => {
            console.log(`Error occured ${e}`);
            console.log(JSON.stringify(e));
            setDialogOpen(false);
            setShareBtnDisabled(false);
            setShareBtnTxt("Share");
        });
    }
    const setSchedule = (e) => {

    }
    const hideFiles = () => {
        setShowFiles(false);
    }
    const downloadFile = (file) => {
        const url = `/api/file/${file}/download?userId=${cookies.user.username}`;
        console.log(`Url to download file is ${url}`);
        fetch(url, {
            headers: {
                Authorization: `Bearer ${cookies.user.token}`
            },
        })
            .then(resp => resp.blob())
            .then(blob => {
                console.log(JSON.stringify(blob));
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
                        <Box className="Flex-column-layout">
                            <Button variant="contained"
                                onClick={hideFiles}
                                sx={{ marginBottom: 2 }}
                                data-test="hideFiles">
                                Hide
                            </Button>
                            <Typography component={'span'} >
                                Files Uploaded by <Chip label={cookies.user.name ?? cookies.user.username}></Chip>
                            </Typography>
                        </Box>
                        <Divider orientation="horizontal" flexItem />
                        {userFiles.map((file) => (
                            <Box key={file} className="Flex-column">
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

                                    <Button size="small"
                                        onClick={() => openScheduleDialog(file)}> Schedule </Button>
                                </Box>
                            </Box>

                        ))}
                    </Box>
                ) : (
                    <Box gap={2} sx={{ marginBottom: 5 }}>
                        {userFiles !== null && userFiles.length === 0 ? (
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
                                <Typography component={'span'} > No Files Uploaded by
                                    <Chip label={cookies.user.username}></Chip>
                                </Typography>
                                <Button variant="contained" size="small" onClick={filesUploaded}>
                                    Show
                                </Button>
                            </Box>
                        ) : (
                            <Box gap={2}>
                                <Button
                                    data-test="showFiles"
                                    variant="contained"
                                    size="small"
                                    onClick={filesUploaded}
                                    disabled={cookies.user == null}>
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
                <DialogContent className="Flex-column-layout">
                    <Alert variant="outlined" severity="info">
                        Only 1 email address supported
                    </Alert>
                    <TextField autoFocus
                        fullWidth
                        required
                        margin="dense"
                        label="Email address to share with"
                        placeholder="Enter that person's email address"
                        type="email"
                        id="shareEmailId"
                    />
                </DialogContent>

                <DialogActions>
                    <Button variant="contained"
                        size="small"
                        onClick={closeDialog}>Close</Button>
                    <Button variant="contained"
                        disabled={shareBtnDisabled}
                        size="small"
                        type="submit">{shareBtnTxt}</Button>
                </DialogActions>
            </Dialog>
            <Dialog open={scheduleDialogOpen}
                onClose={scheduleDialogClose}
                PaperProps={{
                    component: 'form',
                    onSubmit: scheduleDialogClose
                }}>
                <DialogTitle> Schedule </DialogTitle>
                <DialogContent>
                    <DateTimeRange fileId={fileToShare} />
                </DialogContent>
                <DialogActions>
                    <Button variant="contained"
                        size="small"
                        onClick={scheduleDialogClose}>
                        Close
                    </Button>
                    <Button
                        size="small"
                        variant="contained"
                        type="submit">
                        Set schedule
                    </Button>
                </DialogActions>
            </Dialog>
        </Container >
    )
}