import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CloseIcon from '@mui/icons-material/Close';
import DeleteIcon from '@mui/icons-material/Delete';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid2';
import IconButton from '@mui/material/IconButton';
import LinearProgress from '@mui/material/LinearProgress';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Typography from '@mui/material/Typography';
import AnnouncementForm from './AnnouncementForm';

export default function AnnouncementsList({opened, creds, onClose}) {
  const defaultAnnouncement = {
    "id": "00000000-0000-0000-0000-000000000000",
    "message": "",
    "link": null,
    "linkText": null
  };
  const [announcements, setAnnouncements] = useState([]);
  const [announcement, setAnnouncement] = useState(defaultAnnouncement);
  const [loading, setLoading] = useState(true);
  const [announcementDialog, setAnnouncementDialog] = useState(false);

  const editAnnouncement = (announcementId) => {
    const selectedAnnouncement = announcements.find(announcement => announcement.id === announcementId);
    setAnnouncement(selectedAnnouncement === undefined ? defaultAnnouncement : selectedAnnouncement);
    setTimeout(() => setAnnouncementDialog(true), 50)
  }

  const deleteAnnouncement = (announcementId) => {
    const selectedAnnouncement = announcements.find(announcement => announcement.id === announcementId);
    setAnnouncement(selectedAnnouncement === undefined ? defaultAnnouncement : selectedAnnouncement);
    if (selectedAnnouncement !== undefined) {
      fetch('api/announcements/' + selectedAnnouncement.id, {
        method: 'DELETE',
        headers: new Headers({
          'Authorization': 'Basic ' + creds,
          'Content-Type': 'application/json'
        })
      })
      .then((resp) => {
        if (resp.ok) {
          const msg = `Announcement ${announcement.message} deleted'.`;
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

  const newAnnouncement = () => {
    editAnnouncement("00000000-0000-0000-0000-000000000000");
  }

  const closeAnnouncementModal = (success, message) => {
    if (typeof success === 'boolean') {
      setAnnouncementDialog(false);
      onClose(success, message);
    } else {
      setAnnouncementDialog(false);
    }
  }

  useEffect(() => {
    if (!opened || creds === undefined) {
      return;
    }
    setLoading(true)
    const getAnnouncements = async () => {
      try {
        const response = await fetch('api/announcements',
            {headers: new Headers({'Authorization': 'Basic ' + creds})});
        const data = await response.json();
        setAnnouncements(data);
        setLoading(false);
      } catch (err) {
      }
    }
    getAnnouncements();
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
              <Grid>
                <Typography id="announcements-modal-title" variant="h4"
                            component="h2">
                  Announcement Listing
                </Typography>
              </Grid>
              <Grid ml="auto">
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
              <Grid>
                <Typography id="announcements-modal-title" variant="h4"
                            component="h2">
                  Announcement Listing
                </Typography>
              </Grid>
              <Grid ml="auto">
                <IconButton aria-label="close" onClick={onClose}>
                  <CloseIcon/>
                </IconButton>
              </Grid>
            </Grid>
          </DialogTitle>
          <DialogContent>
            <Box sx={{width: '100%'}}>
              <List dense={true}>
                {announcements.map((announcement) => {
                      return (
                          <ListItem
                              key={announcement.id}
                              secondaryAction={
                                <Box>
                                  <IconButton edge='end' aria-label='delete'
                                              onClick={() => deleteAnnouncement(announcement.id)}
                                              color={"warning"}>
                                    <DeleteIcon/>
                                  </IconButton>
                                </Box>
                              }
                          >
                            <ListItemText primary={announcement.message}/>
                          </ListItem>
                      )
                    }
                )}
              </List>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button variant="contained" onClick={newAnnouncement}>
              Add Announcement
            </Button>
            <AnnouncementForm opened={announcementDialog} creds={creds}
                      onClose={closeAnnouncementModal} announcement={announcement}/>
          </DialogActions>
        </Dialog>
    )
  }
}