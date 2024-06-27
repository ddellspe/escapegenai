import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CloseIcon from '@mui/icons-material/Close';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import EditIcon from '@mui/icons-material/Edit';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import LinearProgress from '@mui/material/LinearProgress';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Typography from '@mui/material/Typography';
import TeamForm from './TeamForm';

export default function TeamsList({opened, creds, onClose}) {
  const defaultTeam = {
    "id": null,
    "name": "",
    "passwordId": null,
    "passwordEntered": null,
    "wordId": null,
    "wordEntered": null,
    "quoteId": null,
    "quoteEntered": null,
    "funFactType": null,
    "funFactEntered": null
  };
  const [teams, setTeams] = useState([]);
  const [quotes, setQuotes] = useState(
      [{"00000000-0000-0000-0000-000000000000": "Nothing"}]);
  const [team, setTeam] = useState(defaultTeam);
  const [loading, setLoading] = useState(true);
  const [teamDialog, setTeamDialog] = useState(false);

  const editTeam = (teamId) => {
    const selectedTeam = teams.find(team => team.id === teamId);
    setTeam(selectedTeam === undefined ? defaultTeam : selectedTeam);
    setTimeout(() => setTeamDialog(true), 50)
  }

  const newTeam = () => {
    editTeam("00000000-0000-0000-0000-000000000000");
  }

  const closeTeamModal = (success, message) => {
    if (typeof success === 'boolean') {
      setTeamDialog(false);
      onClose(success, message);
    } else {
      setTeamDialog(false);
    }
  }

  useEffect(() => {
    if (!opened || creds === undefined) {
      return;
    }
    setLoading(true)
    const getTeams = async () => {
      try {
        const response = await fetch('api/teams',
            {headers: new Headers({'Authorization': 'Basic ' + creds})});
        const data = await response.json();
        setTeams(data);
        setLoading(false);
      } catch (err) {
      }
    }
    const getQuotes = async () => {
      try {
        const response = await fetch('api/quotes',
            {headers: new Headers({'Authorization': 'Basic ' + creds})});
        const data = await response.json();
        setQuotes(data)
      } catch (err) {
      }
    }
    getQuotes();
    getTeams();
  }, [opened, creds]);
  if (loading) {
    return (
        <Dialog
            open={opened}
            onClose={onClose}
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
                <Typography id="teams-modal-title" variant="h4" component="h2">
                  Teams Listing
                </Typography>
              </Grid>
              <Grid ml="auto" item>
                <IconButton aria-label="close" onClick={onClose}>
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
            open={opened}
            onClose={onClose}
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
                <Typography id="teams-modal-title" variant="h4" component="h2">
                  Teams Listing
                </Typography>
              </Grid>
              <Grid ml="auto" item>
                <IconButton aria-label="close" onClick={onClose}>
                  <CloseIcon/>
                </IconButton>
              </Grid>
            </Grid>
          </DialogTitle>
          <DialogContent>
            <Box sx={{width: '100%'}}>
              <List dense={true}>
                {teams.map((team) => {
                      return (
                          <ListItem
                              key={team.id}
                              secondaryAction={
                                <IconButton edge='end' aria-label='edit'
                                            onClick={() => editTeam(team.id)}>
                                  <EditIcon/>
                                </IconButton>
                              }
                          >
                            <ListItemText primary={team.name}/>
                          </ListItem>
                      )
                    }
                )}
              </List>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button variant="contained" onClick={newTeam}>
              Add Team
            </Button>
            <TeamForm opened={teamDialog} creds={creds}
                      onClose={closeTeamModal} team={team} quotes={quotes}/>
          </DialogActions>
        </Dialog>
    )
  }
}