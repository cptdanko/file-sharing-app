import { Alert, Box, Button, CircularProgress, Container, Dialog, DialogActions, DialogContent, DialogTitle, LinearProgress, TextField } from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { API_FILE_PATH } from "../Constants";

export const FileBrowser = () => {
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [userFiles, setUserFiles] = useState([]);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [fileToShare, setFileToShare] = useState("");
    const [shareBtnTxt, setShareBtnTxt] = useState("Share");
    const [shareBtnDisabled, setShareBtnDisabled] = useState(false);
    const [filesLoading, setFilesLoading] = useState(true);

    const [alertHeader, setAlertHeader] = useState("");
    const [alertMessage, setAlertMessage] = useState("");
    const [alertOpen, setAlertOpen] = useState(false);
    const [showFiles, setShowFiles] = useState(false);


    const data = ["file1.docx", "File2.pdf"];

    useEffect(() => {
        filesUploaded();
    }, []);

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

    const filesUploaded = async () => {
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
                        const files = [];
                        data.data.forEach(d => {
                            files.push(d);
                        })

                        setUserFiles(files);
                        setFilesLoading(false);
                    }
                }).catch(err => console.log(err)));
    }

    const openDialog = (file) => {
        setFileToShare(file);
        setDialogOpen(true);
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
    const hideFiles = () => {
        setShowFiles(false);
    }
    const downloadFile = (file) => {
        const url = `/api/file/${file}/download?userId=${cookies.user.username}`;
        console.log(`Url to download file is ${url}`);
        fetch(url, {
            headers: {
                Authorization: `Bearer ${cookies.user.token}`,
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

    const renderList = () => {
        const listItems = userFiles.map(file => {
            return <TableRow
                key={file}
                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
                <TableCell component="th" scope="row">
                    {file}
                    <Box>
                        <Button size="small"
                            onClick={() => downloadFile(file)}> Download </Button>
                        <Button size="small"
                            onClick={() => deleteFile(file)}> Delete </Button>
                        <Button size="small"
                            onClick={() => openDialog(file)}> Share </Button>
                    </Box>
                </TableCell>
                

            </TableRow>
        });
        return listItems;
    }
    return (
        <Container>
            <Box sx={{ marginTop: 2 }}>
                {filesLoading ?
                    <LinearProgress color="success" /> : <></>}
            </Box>
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Filename</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {renderList()}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog
                open={dialogOpen}
                onClose={closeDialog}
                PaperProps={{
                    component: 'form',
                    onSubmit: shareFile
                }}>
                <DialogTitle> Share File</DialogTitle>
                <DialogContent sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}>
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

        </Container>
    );
}