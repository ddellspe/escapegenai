import {useEffect, useState} from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Grid from '@mui/material/Grid';
import Snackbar from '@mui/material/Snackbar';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

export default function QuoteForm({opened, creds, onClose, quoteObj}) {
  const [id, setId] = useState(quoteObj.id);
  const [quote, setQuote] = useState(quoteObj.quote);
  const [extendedQuote, setExtendedQuote] = useState(quoteObj.extendedQuote);
  const [author, setAuthor] = useState(quoteObj.author);
  const [authorAddress, setAuthorAddress] = useState(quoteObj.authorAddress);
  const [authorTitle, setAuthorTitle] = useState(quoteObj.authorTitle);
  const [company, setCompany] = useState(quoteObj.company);
  const [companyAddress, setCompanyAddress] = useState(quoteObj.companyAddress);
  const [companyIndustry, setCompanyIndustry] = useState(
      quoteObj.companyIndustry);
  const [showError, setShowError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setDataSent(""), 1000);
  }

  const setQuoteData = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    var object = {};
    data.forEach((value, key) => {
      object[key] = value;
    });
    fetch('api/quotes', {
      method: quoteObj.id === null ? 'POST' : 'PUT',
      headers: new Headers({
        'Authorization': 'Basic ' + creds,
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify(object)
    })
    .then((resp) => {
      if (resp.ok) {
        const action = object.id === null ? "created" : "updated"
        const msg = `Quote ${action} with the quote text of ${object.quote}`;
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
    setId(quoteObj.id);
    setQuote(quoteObj.quote);
    setExtendedQuote(quoteObj.extendedQuote);
    setAuthor(quoteObj.author);
    setAuthorAddress(quoteObj.authorAddress);
    setAuthorTitle(quoteObj.authorTitle);
    setCompany(quoteObj.company);
    setCompanyAddress(quoteObj.companyAddress);
    setCompanyIndustry(quoteObj.companyIndustry);
  }, [quoteObj]);

  return (
      <Dialog
          open={opened}
          onClose={onClose}
          component="form"
          onSubmit={setQuoteData}
          fullWidth
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
              <Typography id="quote-modal-title" variant="h5" component="h3">
                Quote
              </Typography>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <input type="hidden" name="id" value={id === null ? undefined : id}/>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Quote"
                required
                name="quote"
                id="quote"
                defaultValue={quote}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Extended Quote (generated)"
                name="extendedQuote"
                id="extendedQuote"
                defaultValue={extendedQuote}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Author (generated)"
                name="author"
                id="author"
                defaultValue={author}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Author Address (generated)"
                name="authorAddress"
                id="authorAddress"
                defaultValue={authorAddress}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Author Title (generated)"
                name="authorTitle"
                id="authorTitle"
                defaultValue={authorTitle}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Company (generated)"
                name="company"
                id="company"
                defaultValue={company}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Company Address (generated)"
                name="companyAddress"
                id="companyAddress"
                defaultValue={companyAddress}
                disabled={true}
                sx={{mr: 1, width: '100%'}}
            />
          </Box>
          <Box sx={{width: '100%', my: 1}}>
            <TextField
                label="Company Industry (generated)"
                name="companyIndustry"
                id="companyIndustry"
                defaultValue={companyIndustry}
                disabled={true}
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
              {quoteObj.id === null ? 'Create' : 'Update'}
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