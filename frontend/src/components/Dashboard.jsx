import * as React from 'react';
import PropTypes from 'prop-types';
import { useTheme } from '@mui/material/styles';
import AppBar from '@mui/material/AppBar';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import { UserAuth } from './UserAuth';
import { FileUpload } from './FileUpload';
import { Divider } from '@mui/material';
import { FileList } from './FileList';
import { FileBrowser } from './FileBrowser';
import { ScheduleProvider } from './ScheduleContext';


export const Dashboard = () => {

    const [fileUploadDone, setFileUploadDone] = React.useState(false);
    const [userLoggedIn, setUserLoggedIn] = React.useState(false);

    const theme = useTheme();
    const [value, setValue] = React.useState(0);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <Box sx={{ bgcolor: 'background.paper'}}>
        <AppBar position="static">
            <Tabs
            value={value}
            onChange={handleChange}
            indicatorColor="secondary"
            textColor="inherit"
            variant="fullWidth"
            aria-label="full width tabs example"
            >
            <Tab label="Home" {...a11yProps(0)} />
            <Tab label="Documents" {...a11yProps(1)} />
            <Tab label="Schedule" {...a11yProps(2)} />
            </Tabs>
        </AppBar>
        <TabPanel value={value} index={0} dir={theme.direction}>
            <UserAuth userLoggedIn={userLoggedIn} setUserLoggedIn={setUserLoggedIn} />

            <Divider sx={{ margin: 2 }} flexItem orientation="horizontal"></Divider>

            <FileUpload setFileUploaded={setFileUploadDone} />

            <Divider sx={{ margin: 2 }} flexItem orientation="horizontal"></Divider>
            <ScheduleProvider>
              <FileList fileUploadDone={fileUploadDone} setFileUploadDone={setFileUploadDone}  />
            </ScheduleProvider>
        </TabPanel>
        <TabPanel value={value} index={1} dir={theme.direction}>
            <ScheduleProvider>
              <FileBrowser />
            </ScheduleProvider>
        </TabPanel>
        <TabPanel value={value} index={2} dir={theme.direction}>
            <Box sx={{marginTop: 2}}>
              Schedule coming soon....
            </Box>
        </TabPanel>
        
        </Box>
    );
}
function TabPanel(props) {
    const { children, value, index, ...other } = props;
  
    return (
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`full-width-tabpanel-${index}`}
        aria-labelledby={`full-width-tab-${index}`}
        {...other}
      >
        {value === index && (
          <Box>
            <Typography component='div'>{children}</Typography>
          </Box>
        )}
      </div>
    );
  }
  
  TabPanel.propTypes = {
    children: PropTypes.node,
    index: PropTypes.number.isRequired,
    value: PropTypes.number.isRequired,
  };
  
  function a11yProps(index) {
    return {
      id: `full-width-tab-${index}`,
      'aria-controls': `full-width-tabpanel-${index}`,
    };
  }