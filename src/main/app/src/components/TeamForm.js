import {useEffect, useState} from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControl from '@mui/material/FormControl';
import Grid from '@mui/material/Grid';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import Snackbar from '@mui/material/Snackbar';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

export default function ScoreForm({opened, creds, onClose, team, quotes}) {
  const [id, setId] = useState(team.id);
  const [name, setName] = useState(team.name);
  const [passwordId, setPasswordId] = useState(team.passwordId);
  const [passwordEntered, setPasswordEntered] = useState(team.passwordEntered);
  const [wordId, setWordId] = useState(team.wordId);
  const [wordEntered, setWordEntered] = useState(team.wordEntered);
  const [quoteId, setQuoteId] = useState(team.quoteId);
  const [quoteEntered, setQuoteEntered] = useState(team.quoteEntered);
  const [funFactType, setFunFactType] = useState(team.funFactType);
  const [funFactEntered, setFunFactEntered] = useState(team.funFactEntered);
  const [showError, setShowError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setDataSent(""), 1000);
  }

  const funFactTypes = [
    {value: "author", label: "Author"},
    {value: "authorAddress", label: "Author Address"},
    {value: "authorTitle", label: "Author Title"},
    {value: "company", label: "Company"},
    {value: "companyAddress", label: "Company Address"},
    {value: "companyIndustry", label: "Company Industry"}
  ];

  const handleChange = (event) => {
    if (event.target.name === 'funFactType') {
      setFunFactType(event.target.value);
    } else {
      setQuoteId(event.target.value);
    }
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
    setPasswordId(team.passwordId)
    setPasswordEntered(team.passwordEntered)
    setWordId(team.wordId)
    setWordEntered(team.wordEntered)
    setQuoteId(team.quoteId)
    setQuoteEntered(team.quoteEntered)
    setFunFactType(team.funFactType)
    setFunFactEntered(team.funFactEntered)
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
            <Grid item>
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
          <input type="hidden" name="passwordId"
                 value={passwordId === null ? undefined : passwordId}/>
          <input type="hidden" name="passwordEntered"
                 value={passwordEntered === null ? undefined
                     : passwordEntered}/>
          <input type="hidden" name="wordId"
                 value={wordId === null ? undefined : wordId}/>
          <input type="hidden" name="wordEntered"
                 value={wordEntered === null ? undefined : wordEntered}/>
          <Box sx={{width: '100%', my: 1}}>
            <FormControl fullWidth>
              <InputLabel id="quoteIdLabel">Quote</InputLabel>
              <Select
                  labelId="quoteIdLabel"
                  id="quoteId"
                  value={quoteId}
                  onChange={handleChange}
                  label="Quote"
                  name="quoteId"
              >
                {quotes.map((quote) => (
                    <MenuItem key={quote.id}
                              value={quote.id}>{quote.quote}</MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>
          <input type="hidden" name="quoteEntered"
                 value={quoteEntered === null ? undefined : quoteEntered}/>
          <Box sx={{width: '100%', my: 1}}>
            <FormControl fullWidth>
              <InputLabel id="funFactTypeLabel">Fun Fact Type</InputLabel>
              <Select
                  labelId="funFactTypeLabel"
                  id="funFactType"
                  value={funFactType}
                  onChange={handleChange}
                  label="Fun Fact Type"
                  name="funFactType"
              >
                {funFactTypes.map((funFactType) => (
                    <MenuItem key={funFactType.value}
                              value={funFactType.value}>{funFactType.label}</MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>
          <input type="hidden" name="funFactEntered"
                 value={funFactEntered === null ? undefined
                     : funFactEntered}/>
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