import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export const AlertDialog = (props) => {
    const {
        open,
        handleClose,
        title,
        content
    } = props;

    return (
        <Dialog open={open}
            onClose={handleClose} >
            <DialogTitle id="alert-dialog-title" >
                {title}
            </DialogTitle>
            <DialogContent dividers>
                <DialogContentText>
                    {content}
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose}> Close </Button>
            </DialogActions>
        </Dialog>
    );
};