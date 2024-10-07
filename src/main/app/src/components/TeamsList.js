import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
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
import RestoreIcon from '@mui/icons-material/Restore';
import Typography from '@mui/material/Typography';
import TeamForm from './TeamForm';

export default function TeamsList({opened, creds, onClose}) {
  const defaultTeam = {
    "id": null,
    "name": "",
  };
  const [teams, setTeams] = useState([]);
  const [team, setTeam] = useState(defaultTeam);
  const [loading, setLoading] = useState(true);
  const [teamDialog, setTeamDialog] = useState(false);

  const editTeam = (teamId) => {
    const selectedTeam = teams.find(team => team.id === teamId);
    setTeam(selectedTeam === undefined ? defaultTeam : selectedTeam);
    setTimeout(() => setTeamDialog(true), 50)
  }

  const deleteTeam = (teamId) => {
    const selectedTeam = teams.find(team => team.id === teamId);
    setTeam(selectedTeam === undefined ? defaultTeam : selectedTeam);
    if (selectedTeam !== undefined) {
      fetch('api/teams/' + selectedTeam.id, {
        method: 'DELETE',
        headers: new Headers({
          'Authorization': 'Basic ' + creds,
          'Content-Type': 'application/json'
        })
      })
      .then((resp) => {
        if (resp.ok) {
          const msg = `Team ${team.name} deleted'.`;
          onClose(true, msg);
          return true;
        } else {
          return resp.json();
        }
      })
      .then(data => {
      })
    }
  }

  const resetTeam = (teamId) => {
    const selectedTeam = teams.find(team => team.id === teamId);
    setTeam(selectedTeam === undefined ? defaultTeam : selectedTeam);
    if (selectedTeam !== undefined) {
      fetch('api/teams/' + selectedTeam.id + '/reset', {
        method: 'DELETE',
        headers: new Headers({
          'Authorization': 'Basic ' + creds,
          'Content-Type': 'application/json'
        })
      })
      .then((resp) => {
        if (resp.ok) {
          const msg = `Team ${team.name} reset'.`;
          onClose(true, msg);
          return true;
        } else {
          return resp.json();
        }
      })
      .then(data => {
      })
    }
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
                <Typography id="teams-modal-title" variant="h4"
                            component="h2">
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
                <Typography id="teams-modal-title" variant="h4"
                            component="h2">
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
                                <Box>
                                  <IconButton edge='end' aria-label='edit'
                                              onClick={() => editTeam(team.id)}>
                                    <EditIcon/>
                                  </IconButton>
                                  <IconButton edge='end' aria-label='reset'
                                              onClick={() => resetTeam(team.id)}
                                              color={"secondary"}>
                                    <RestoreIcon/>
                                  </IconButton>
                                  <IconButton edge='end' aria-label='delete'
                                              onClick={() => deleteTeam(team.id)}
                                              color={"warning"}>
                                    <DeleteIcon/>
                                  </IconButton>
                                </Box>
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
                      onClose={closeTeamModal} team={team}/>
          </DialogActions>
        </Dialog>
    )
  }
}