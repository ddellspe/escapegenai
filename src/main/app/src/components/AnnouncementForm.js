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

export default function AnnouncementForm({opened, creds, onClose, announcement}) {
  const [message, setMessage] = useState(announcement.message);
  const [link, setLink] = useState(announcement.link);
  const [linkText, setLinkText] = useState(announcement.linkText);
  const [showError, setShowError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setDataSent(""), 1000);
  }

  const setAnnouncement = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    var object = {};
    data.forEach((value, key) => {
      object[key] = value
    });
    fetch('api/announcements', {
      method: 'POST',
      headers: new Headers({
        'Authorization': 'Basic ' + creds,
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify(object)
    })
    .then((resp) => {
      if (resp.ok) {
        const msg = `Announcement ${announcement.message} created.`;
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
    setMessage(announcement.message)
    setLink(announcement.link)
    setLinkText(announcement.linkText)
  }, [announcement]);

  return (
      <Dialog
          open={opened}
          onClose={onClose}
          component="form"
          onSubmit={setAnnouncement}
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
                Announcement
              </Typography>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <input type="hidden" name="id" value="00000000-0000-0000-0000-000000000000"/>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Message"
                required
                name="message"
                id="message"
                defaultValue={message}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Link"
                name="link"
                id="link"
                defaultValue={link}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Link Text"
                name="linkText"
                id="linkText"
                defaultValue={linkText}
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
              Create
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