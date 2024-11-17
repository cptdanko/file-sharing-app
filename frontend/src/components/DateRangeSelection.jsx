import { Alert, Box, Button, Container, MenuItem, Select, TextField, Typography } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import "./components.css";
import dayjs from "dayjs";
import { ScheduleContext } from "./ScheduleContext";

export const DateTimeRange = (props) => {
    const { fileId, savedSchedule } = props;
    const [emailToSendTo, setEmailToSendTo] = useState("");
    const [selDate, setSelDate] = useState(null);
    const [from, setFrom] = useState(0);
    const [to, setTo] = useState(0);
    const [toRange, setToRange] = useState([]);
    const hourRange = [...Array(24).keys()];
    const { schedule, setSchedule } = useContext(ScheduleContext);
    const [dateSelected, setDateSelected] = useState(false);
    
    useEffect(() => {
        console.log(`In useEffect with ${JSON.stringify(savedSchedule)}`);
        if(savedSchedule) {
            const timeSplit = savedSchedule.timeWindow.split("-");
            console.log(timeSplit);
            setFrom(JSON.parse(timeSplit[0]));
            setTo(JSON.parse(timeSplit[1]));
            // TODO: remove the hard coded index element grab
            setEmailToSendTo(savedSchedule.receivers[0]);
        }

    });
    function getLatestDate() {
        return dayjs(selDate.$d).format('DD/MM/YYYY');
    }
    const handleFromChange = (e) => {
        const v = e.target.value;
        setFrom(v);
        schedule.from = v;
        let range = new Array();
        const startPos = (v == 23) ? 0 : v + 1;
        for (let i = startPos; i < 24; i += 1) {
            range.push(i);
        }
        setToRange(range);
        schedule.date = getLatestDate();
        setSchedule(schedule);
    }
    const handleToChange = (e) => {
        setTo(e.target.value);
        schedule.to = e.target.value;
        schedule.date = getLatestDate();
        schedule.filename = fileId;
        schedule.receiver = emailToSendTo;
        setSchedule(schedule);
    }


    return (
        <Container style={{ display: 'flex', flexDirection: 'column', marginTop: 8, padding: 0 }}>
            <Box className="Center-text">
                <Alert variant="outlined" severity="info">
                    Date & time when you want to share it
                </Alert>
            </Box>
            <Box>
                <TextField autoFocus
                    fullWidth
                    required
                    value={emailToSendTo}
                    onChange={(e) => setEmailToSendTo(e.target.value)}
                    margin="dense"
                    label="Email address to send it to"
                    placeholder="Enter the receiver's email address"
                    type="email"
                    id="scheduleShareEmailId"
                />
            </Box>
            <Box className="Date-time Center-text">
                <Box>
                    <DatePicker
                        label="Scheduled Date"
                        value={selDate}
                        onChange={newVal => {
                            setDateSelected(true);
                            setSelDate(newVal);
                        }}
                        format="DD/MM/YYYY" />
                </Box>
                <Box>
                    <span style={{ marginLeft: 2 }}>&nbsp;Between &nbsp;</span>
                    <Select labelId="from"
                        id="from_time"
                        value={from}
                        label="From"
                        disabled={!dateSelected}
                        onChange={handleFromChange}>
                        {hourRange.map((option, idx) => (
                            <MenuItem key={"from_" + idx} value={option}>
                                {option}
                            </MenuItem>
                        ))}
                    </Select>

                    -
                    <Select labelId="to"
                        id="to_time"
                        value={to}
                        label="To"
                        disabled={!dateSelected}
                        onChange={handleToChange}>
                        {toRange.map((option, idx) => (
                            <MenuItem key={"to_" + idx} value={option}>
                                {option}
                            </MenuItem>
                        ))}
                    </Select>
                    <span>&nbsp; hrs</span>
                </Box>
            </Box>
        </Container>
    );
}