import { Box, Button, TextField, Typography, Container, Avatar, Tooltip, Alert } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { useCookies } from "react-cookie";
import { RegistrationForm } from "../RegistrationForm";

export const UserAuth = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [userLoggedIn, setUserLoggedIn] = useState(false);
    const [loginError, setLoginError] = useState(false);
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [showRegForm, setShowRegForm] = useState(false);

    useEffect(() => {
        cookies.user
            && setUserLoggedIn(true)
            && setUsername(cookies.user.username);
    });

    const login = async () => {
        // call the healthcheck method to know it's valid
        const url = `/api/auth/login`;
        const resp = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
              "username": username,
              "password": password  
            })
        });
        if (resp.status > 299) {
            setLoginError(true);
        } else {
            console.log("Login successful");
            setUserLoggedIn(true);
            const token = await resp.text();;
            console.log(`Received the JSON token ${token}`);
            const userObj = {
                username,
                token,
            };
            setCookie("user", JSON.stringify(userObj), "/");
        }
    }
    const createNewAccount = () => {
        console.log(`About to create new account`);
        setShowRegForm(true);
    }
    const logout = () => {
        removeCookie("user");
        setUserLoggedIn(false);
    }
    const submit = (e) => {
        e.preventDefault();
        console.log("About to submit an event");
    }
    const closeForm = () => {
        setShowRegForm(false);
    }
    function showSigninSignupForm() {
        if (showRegForm) {
            return <RegistrationForm closeForm={closeForm} />
        }
        return <Box id="loginForm"
            onSubmit={submit}
            variant="outlined"
            sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center"
            }}>
            <Typography component="h1" variant="h5">Sign in</Typography>
            <TextField
                margin="normal" required id="email"
                autoComplete="email"
                autoFocus
                name="email"
                label="email"
                onChange={(e) => setUsername(e.target.value)}>
            </TextField>
            <TextField
                margin="normal" required id="password" name="password"
                label="password" type="password" onChange={(e) => {
                    setPassword(e.target.value);
                    setLoginError(false);
                }}
            >
            </TextField>
            {loginError ?
                <Box p={1}>
                    <Alert variant="outlined" severity="error">
                        Error!!! Are you sure you entered the right username and password?
                    </Alert>
                </Box>
                : <>{""}</>}
            <Box gap={1} sx={{ display: "flex", flexDirection: "row" }}>
                <Box>
                    <Button
                        size="small"
                        variant="contained"
                        type="submit"
                        onClick={login}> Login </Button>
                </Box>
                <Box>
                    <Button
                        size="small"
                        variant="contained"
                        type="submit"
                        onClick={createNewAccount}> New Account? </Button>
                </Box>
            </Box>
        </Box>
    }
    return (
        <Container sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center"
        }}>
            {userLoggedIn ?
                <Box sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center"
                }} id="avatar" gap={1} p={1}>
                    <Tooltip title={username}>
                        <Avatar src="#" alt={username} />
                    </Tooltip>
                    <Button size="small"
                        variant="contained"
                        onClick={logout}>
                        Logout
                    </Button>
                </Box> : <></>
            }
            {!userLoggedIn ?
                showSigninSignupForm()
                : <></>}


        </Container>
    )
}
