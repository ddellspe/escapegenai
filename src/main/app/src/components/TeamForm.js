import {useEffect, useState} from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid2';
import Snackbar from '@mui/material/Snackbar';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

export default function ScoreForm({opened, creds, onClose, team}) {
  const [id, setId] = useState(team.id);
  const [name, setName] = useState(team.name);
  const [showError, setShowError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setDataSent(""), 1000);
  }

  const setTeam = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    var object = {};
    data.forEach((value, key) => {
      object[key] = value
    });
    fetch('api/teams', {
      method: team.id === null ? 'POST' : 'PUT',
      headers: new Headers({
        'Authorization': 'Basic ' + creds,
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify(object)
    })
    .then((resp) => {
      if (resp.ok) {
        const action = team.id === null ? "created" : "updated"
        const msg = `Team ${team.name} ${action}.`;
        onClose(true, msg);
        return true;
      } else {
        return resp.json();
      }
    })
    .then(data => {
      if (typeof data === "object") {
        setShowError(true);
        setDataSent(data.errors.message);
      }
    })
  };

  useEffect(() => {
    setId(team.id)
    setName(team.name)
  }, [team]);

  return (
      <Dialog
          open={opened}
          onClose={onClose}
          component="form"
          onSubmit={setTeam}
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
            <Grid>
              <Typography id="team-modal-title" variant="h5" component="h3">
                Team
              </Typography>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <input type="hidden" name="id" value={id === null ? undefined : id}/>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Name"
                required
                name="name"
                id="name"
                defaultValue={name}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Box sx={{justifyContent: 'space-between'}}>
            <Button
                variant="outlined"
                onClick={onClose}
                sx={{mr: 1}}
            >
              Cancel
            </Button>
            <Button
                type="submit"
                variant="contained"
            >
              {team.id === null ? 'Create' : 'Update'}
            </Button>
          </Box>
        </DialogActions>
        <Snackbar
            anchorOrigin={{vertical: 'top', horizontal: 'center'}}
            open={showError}
            autoHideDuration={6000}
            onClose={killAlert}
        >
          <Alert severity="error" sx={{width: '100%'}}>{dataSent}</Alert>
        </Snackbar>
      </Dialog>
  )
}