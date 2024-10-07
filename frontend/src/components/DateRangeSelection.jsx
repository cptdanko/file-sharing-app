import { Box, Container, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

export const DateTimeRange = (props) => {
    const { fileId } = props;
    const [from, setFrom] = useState(0);
    const [to, setTo] = useState(1); 
    const hourRange = [...Array(24).keys()];
    const handleChange = (e) => {
        const v = e.target.value;
        console.log(v);
    }
    return (
        <Container style={{ display:'flex', flexDirection:'column', marginTop: 8 }}>
            <Box style={{marginTop: 8, marginBottom: 8}}>
                <Typography>Select date time range</Typography>
            </Box>
            <Box>
                <DatePicker />
            </Box>
            <Box style={{marginTop: 8}} >
                <span> Time range </span>
                <select id="to">
                    {hourRange.map((option, idx) => (
                        <option key={"to_"+idx} value={option}>
                            {option}
                        </option>
                    ))}
                </select>
                - 
                <select id="from">
                    {hourRange.map((option, idx) => (
                        <option key={"from_"+idx} value={option}>
                            {option}
                        </option>
                    ))}
                </select>
            </Box>
        </Container>
    );
}