import { Box, Button, TextField, Typography, Container, Avatar, Tooltip, Alert } from "@mui/material";
import Documents from '../documents.jpg';
import { RegistrationForm } from "../RegistrationForm";
import { useState } from "react";

export const HomePage = () => {
    const [loginError, setLoginError] = useState(false);
    const [showRegForm, setShowRegForm] = useState(false);

    const closeForm = () => {
        setShowRegForm(false);
    }

    const submit = (e) => {
        e.preventDefault();
    }

    return (
        <Container>
            <Box sx={{borderRadius: 2, marginTop: 1 }}>
                <img className="RoundedImage" src={Documents} width="300" height="300" />
            </Box>
            <Box>
            </Box>
        </Container>
    )
}