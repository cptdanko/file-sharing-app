import { Box, Container, FormControl, FormHelperText, Input, InputLabel, Button } from "@mui/material";
import { useState } from "react";

export const RegistrationForm = (props) => {

    const { closeForm } = props;
    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const signup = (e) => {
        e.preventDefault();
        const jsonSignup = {};
        jsonSignup["name"] = name;
        jsonSignup["username"] = username;
        // TODO: still sending password in plain text, FIX!!!
        jsonSignup["password"] = password;
        fetch("/api/user/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(jsonSignup),
        }).then((resp) => {
            console.log(JSON.stringify(resp));
        });
    };

    return (
        <Container sx={{
            display: 'flex',
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center"
        }}>
            <Box id="signup">
                <form onSubmit={signup} id="signupForm" style={{
                    display: 'flex',
                    flexDirection: 'column'

                }}>
                    <FormControl>
                        <InputLabel htmlFor="name"> Name </InputLabel>
                        <Input autoFocus={true} id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)} />
                        <FormHelperText id="nameText">Please enter your full name</FormHelperText>
                    </FormControl>
                    <FormControl sx={{ marginTop: 2 }}>
                        <InputLabel htmlFor="email"> Email </InputLabel>
                        <Input id="username" value={username}
                            onChange={(e) => setUsername(e.target.value)} />
                        <FormHelperText id="usernameText">Your username should be your email address</FormHelperText>
                    </FormControl>
                    <FormControl sx={{ marginTop: 2 }}>
                        <InputLabel htmlFor="password"> Password </InputLabel>
                        <Input id="password"
                            value={password}
                            type="password"
                            onChange={e => setPassword(e.target.value)} />
                        <FormHelperText id="passwordText">Please choose a strong password</FormHelperText>
                    </FormControl>
                    <Box gap={2} sx={{
                        display: "flex",
                        margin: "auto"
                    }}>
                        <Button size="small"
                            variant="contained"
                            type="submit">
                            Submit
                        </Button>
                        <Button size="small"
                            variant="contained"
                            onClick={closeForm}>
                            Close Form
                        </Button>
                    </Box>
                </form>
            </Box>
        </Container>
    );
}