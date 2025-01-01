import { Alert, Box, Container, TextField } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import "./components.css";
import { ScheduleContext } from "./ScheduleContext";
import dayjs from "dayjs";

export const DateTimeRange = (props) => {
    const { fileId, savedSchedule } = props;
    const [emailToSendTo, setEmailToSendTo] = useState("");
    const [selDate, setSelDate] = useState(null);
    // const hourRange = [...Array(24).keys()];
    const { schedule, setSchedule } = useContext(ScheduleContext);
    
    useEffect(() => {
        if(savedSchedule) {
            // TODO: alter it to suit schedule send email
            setEmailToSendTo(savedSchedule.receivers[0]);
            schedule.to = savedSchedule.receivers[0];
            const dateStr = savedSchedule.sendDate.split('T')[0];
            
            setSelDate(dayjs(dateStr));
        }
    });

    return (
        <Container style={{ display: 'flex', flexDirection: 'column', marginTop: 8, padding: 0 }}>
            <Box className="Center-text">
                <Alert variant="outlined" severity="info">
                    Date & time when you want to share it
                </Alert>
            </Box>
            <Box className="Date-time">
                <TextField autoFocus
                    fullWidth
                    required
                    value={emailToSendTo}
                    onChange={(e) => {
                        setEmailToSendTo(e.target.value);
                        schedule.to = e.target.value;
                    }}
                    margin="dense"
                    label="Email address to send it to"
                    placeholder="Enter the receiver's email address"
                    type="email"
                    id="scheduleShareEmailId"
                />
                <DatePicker
                    label="Scheduled Date"
                    value={selDate}
                    onChange={newVal => {
                        setSelDate(newVal);
                        schedule.date = newVal;
                    }}
                    minDate={dayjs(new Date())}
                    format="YYYY-MM-DD" />
            </Box>
        </Container>
    );
}