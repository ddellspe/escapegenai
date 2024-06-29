import React, {useEffect, useState} from "react";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import DialogContent from "@mui/material/DialogContent";
import Box from "@mui/material/Box";
import LinearProgress from "@mui/material/LinearProgress";
import TeamStatus from './TeamStatus';

export default function Scoreboard({opened, onClose}) {
  const [loading, setLoading] = useState(true);
  const [timer, setTimer] = useState("");
  const [teams, setTeams] = useState([]);

  const handleClose = () => {
    console.log(timer);
    clearTimeout(timer);
    setTimer("");
    onClose();
  }
  const getTeams = () => {
    if (!opened) {
      return;
    }
    fetch('game/teams').then((resp) => resp.json()).then((data) => {
      setTeams(data);
      setLoading(false);
    });
    setTimer(setTimeout(() => getTeams(), 10000));
  }

  useEffect(() => {
    if (!opened) {
      return;
    }
    setLoading(true)
    getTeams();
    /* eslint-disable react-hooks/exhaustive-deps */
  }, [opened]);
  /* eslint-enable */
  if (loading) {
    return (
        <Dialog
            fullScreen
            open={opened}
            onClose={handleClose}
            PaperProps={{
              sx: {
                position: 'fixed',
                m: '0 auto',
              },
            }}
        >
          <DialogTitle>
            <Grid container spacing={2} justifyContent="center"
                  alignItems="center">
              <Grid item>
                <Typography id="scoreboard-modal-title" variant="h4"
                            component="h2">
                  Scoreboard
                </Typography>
              </Grid>
              <Grid ml="auto" item>
                <IconButton aria-label="close" onClick={handleClose}>
                  <CloseIcon/>
                </IconButton>
              </Grid>
            </Grid>
          </DialogTitle>
          <DialogContent>
            <Box sx={{width: '100%'}}>
              <LinearProgress/>
            </Box>
          </DialogContent>
        </Dialog>
    );
  } else {
    return (
        <Dialog
            fullScreen
            open={opened}
            onClose={handleClose}
            PaperProps={{
              sx: {
                position: 'fixed',
                m: '0 auto',
              },
            }}
        >
          <DialogTitle>
            <Grid container spacing={2} justifyContent="center"
                  alignItems="center">
              <Grid item>
                <Typography id="scoreboard-modal-title" variant="h4"
                            component="h2">
                  Scoreboard
                </Typography>
              </Grid>
              <Grid ml="auto" item>
                <IconButton aria-label="close" onClick={handleClose}>
                  <CloseIcon/>
                </IconButton>
              </Grid>
            </Grid>
          </DialogTitle>
          <DialogContent>
            <Box sx={{width: '100%'}}>
              {teams.map((team) => (<TeamStatus key={team.id} team={team}/>))}
            </Box>
          </DialogContent>
        </Dialog>
    )
  }
}