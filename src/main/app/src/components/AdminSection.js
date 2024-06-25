import React, { useEffect, useState } from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import CloseIcon from '@mui/icons-material/Close';
import MenuIcon from '@mui/icons-material/Menu';
import Fab from '@mui/material/Fab';
import LoginIcon from '@mui/icons-material/LoginTwoTone';
import LogoutIcon from '@mui/icons-material/LogoutTwoTone';
import QuoteIcon from '@mui/icons-material/FormatQuoteTwoTone';
import Snackbar from '@mui/material/Snackbar';
import SpeedDial from '@mui/material/SpeedDial';
import SpeedDialIcon from '@mui/material/SpeedDialIcon';
import SpeedDialAction from '@mui/material/SpeedDialAction';
import LoginForm from "./LoginForm";
import QuoteList from "./QuoteList";

export default function AdminSection() {
  var creds = sessionStorage.getItem('auth');
  const [auth, setAuth] = useState(false);
  const [openSpeedDialOptions, setOpenSpeedDialOptions] = useState(false);
  const [openLoginModal, setOpenLoginModal] = useState(false);
  const [openQuotesModal, setOpenQuotesModal] = useState(false);
  const [loginError, setLoginError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const toggleDial = () => setOpenSpeedDialOptions((open) => !open);
  const killAlert = () => {
    setDataSent("");
  }
  const handleOpenQuotesModal = () => {
    setOpenSpeedDialOptions(false);
    setOpenQuotesModal(true);
    setOpenLoginModal(false);
  }
  const handleCloseAll = (success, message) => {
    if (typeof success === "boolean") {
      if (success) {
        setDataSent(message);
      }
    }
    setOpenQuotesModal(false);
    setOpenSpeedDialOptions(false);
    setOpenLoginModal(false);
  };
  const handleOpenLoginModal = () => {
    setOpenQuotesModal(false);
    setOpenSpeedDialOptions(false);
    setOpenLoginModal(true);
  };
  const logout = () => {
    sessionStorage.removeItem('auth');
    setAuth(false);
  };
  const handleLogin = () => {
    creds = sessionStorage.getItem('auth');
    handleCloseAll();
  }

  useEffect(() => {
    if (creds !== null) {
      fetch('session', {headers: new Headers({'Authorization': 'Basic ' + creds})})
      .then((response) => {
        if(!response.ok) {
          setAuth(false);
          setLoginError(true);
          setTimeout(() => setLoginError(false), 5000);
          sessionStorage.removeItem('auth');
        } else {
          setAuth(true);
        }
      });
    }
  }, [creds]);

  if (auth === true) {
    return (
        <Box>
          <SpeedDial
              ariaLabel="controlled open manageData"
              sx={{ position: 'absolute', bottom: 16, right: 16 }}
              icon={<SpeedDialIcon icon={<MenuIcon />} openIcon={<CloseIcon />} />}
              onClick={toggleDial}
              open={openSpeedDialOptions}
              hidden={!auth}
              FabProps={{ color:"info" }}
          >
            <SpeedDialAction
                key="quotes"
                icon={<QuoteIcon />}
                tooltipTitle="Manage Quotes"
                onClick={handleOpenQuotesModal}
              />
            <SpeedDialAction
                key="logout"
                icon={<LogoutIcon />}
                tooltipTitle="Logout"
                onClick={logout} />
          </SpeedDial>
          <QuoteList opened={openQuotesModal} creds={creds} onClose={handleCloseAll} />
          <Snackbar
              anchorOrigin={{vertical:'top', horizontal: 'center'}}
              open={dataSent !== ""}
              autoHideDuration={6000}
              onClose={killAlert}
          >
            <Alert severity="success" sx={{width: '100%'}}>{dataSent}</Alert>
          </Snackbar>
        </Box>
    );
  } else {
    return (
        <Box>
          <Fab
              sx={{ position: 'absolute', bottom: 16, right: 16 }}
              onClick={handleOpenLoginModal}
              hidden={auth}
          >
            <LoginIcon />
          </Fab>
          <LoginForm opened={openLoginModal} onClose={handleLogin} />
          <Snackbar
              anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
              open={loginError}
              message="Error on login"
              key="loginError" />
        </Box>
    );
  }
};