import { Box, Button, TextField, Typography, Container, Avatar, Tooltip, Alert } from "@mui/material";
import { useEffect, useState } from "react";
import { CookiesProvider, useCookies } from "react-cookie";
import { RegistrationForm } from "../RegistrationForm";
import { useGoogleLogin } from "@react-oauth/google";
import { googleLogout } from '@react-oauth/google';
import GoogleIcon from '@mui/icons-material/Google';

export const UserAuth = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [userLoggedIn, setUserLoggedIn] = useState(false);
    const [loginError, setLoginError] = useState(false);
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [showRegForm, setShowRegForm] = useState(false);
    const [profileImage, setProfileImage] = useState("");

    useEffect(() => {
        cookies.user
            && setUserLoggedIn(true)
            && setUsername(cookies.user.username);
        
        if(cookies.user && cookies.user.google && cookies.user.google.pictureLink) {
            setProfileImage(cookies.user.google.pictureLink);
        }   
    });
    
    const fetchUserGoogleProfile = async (token) => {
        const url = `https://www.googleapis.com/oauth2/v1/userinfo?access_token=${token}`;
        const user = cookies.user ?? {};
        cookies.user = user;
        user.google = {};
        user.google.token = token;
        const request = await fetch(url);
        const data = await request.json();
        // post to the server
        cookies.user.google = {
            "email": data.email,
            "pictureLink": data.picture,
            "name": data.name
        };
        cookies.user.username = data.email;
        setProfileImage(data.picture);
        setCookie("user", JSON.stringify(cookies.user), "/");
        const backendPost = "/api/auth/login/google";
        const bePost = await fetch(backendPost, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cookies.user.google)
        });
        const beData = await bePost.json();
        cookies.user.token = beData.accessToken;
        
        setCookie("user", JSON.stringify(cookies.user), "/");
    }
    const glogin = useGoogleLogin({
        onSuccess: tokenResponse => {
            const token = tokenResponse.access_token;
            fetchUserGoogleProfile(token);
            setUserLoggedIn(true);
        },
        onError: (e) => console.error(e)
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
            setUserLoggedIn(true);
            const token = await resp.text();
            const userObj = {
                username,
                token,
            };
            setProfileImage("");
            setCookie("user", JSON.stringify(userObj), "/");
        }
    }
    const createNewAccount = () => {
        setShowRegForm(true);
    }
    const logout = () => {
        removeCookie("user");
        setUserLoggedIn(false);
        googleLogout();
    }
    const submit = (e) => {
        e.preventDefault();
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
            <Box p={1} m={1}>
                <Box m={1}><Button variant="contained" 
                onClick={glogin}
                startIcon={<GoogleIcon />}> Google Login</Button> </Box>
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
                        <Avatar src={profileImage} 
                            alt={username} 
                            sx={{ width: 64, height: 64 }}/>
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
