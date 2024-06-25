import { useState, useEffect } from 'react';
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

export default function QuoteForm({opened, creds, onClose, quote}) {
  const [id, setId] = useState(quote.id);
  const [quoteText, setQuoteText] = useState(quote.quote);
  const [extendedQuote, setExtendedQuote] = useState(quote.extendedQuote);
  const [author, setAuthor] = useState(quote.author);
  const [authorAddress, setAuthorAddress] = useState(quote.authorAddress);
  const [authorTitle, setAuthorTitle] = useState(quote.authorTitle);
  const [company, setCompany] = useState(quote.company);
  const [companyAddress, setCompanyAddress] = useState(quote.companyAddress);
  const [companyIndustry, setCompanyIndustry] = useState(quote.companyIndustry);
  const [showError, setShowError] = useState(false);
  const [dataSent, setDataSent] = useState("");
  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setDataSent(""), 1000);
  }

  const setScore = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    var object = {};
    data.forEach((value, key) => {object[key] = parseInt(value)});
    fetch('api/quotes', {
      method: quote.id === undefined ? 'POST' : 'PUT',
      headers: new Headers({'Authorization': 'Basic ' + creds, 'Content-Type': 'application/json'}),
      body: JSON.stringify(object)})
    .then((resp) => {
      if (resp.ok) {
        const action = quote.id === undefined ? "created" : "updated"
        const msg = `Quote with id: ${id} ${action} with the quote text of ${quoteText}`;
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
    setId(quote.id);
    setQuoteText(quote.quote);
    setExtendedQuote(quote.extendedQuote);
    setAuthor(quote.author);
    setAuthorAddress(quote.authorAddress);
    setAuthorTitle(quote.authorTitle);
    setCompany(quote.company);
    setCompanyAddress(quote.companyAddress);
    setCompanyIndustry(quote.companyIndustry);
  }, [quote]);

  return (
      <Dialog
          open={opened}
          onClose={onClose}
          component="form"
          onSubmit={setScore}
          PaperProps={{
            sx: {
              position: 'fixed',
              m: '0 auto',
            },
          }}
      >
        <DialogTitle>
          <Grid container spacing={2} justifyContent="center" alignItems="center">
            <Grid item>
              <Typography id="quote-modal-title" variant="h5" component="h3">
                Quote
              </Typography>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <input type="hidden" name="id" value={id} />
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Quote"
                required
                name="quote"
                id="quote"
                defaultValue={quoteText}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Extended Quote (generated)"
                name="extendedQuote"
                id="extendedQuote"
                defaultValue={extendedQuote}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Author (generated)"
                name="author"
                id="author"
                defaultValue={author}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Author Address (generated)"
                name="authorAddress"
                id="authorAddress"
                defaultValue={authorAddress}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Author Title (generated)"
                name="authorTitle"
                id="authorTitle"
                defaultValue={authorTitle}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Company (generated)"
                name="company"
                id="company"
                defaultValue={company}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Company Address (generated)"
                name="companyAddress"
                id="companyAddress"
                defaultValue={companyAddress}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
          <Box sx={{ width: '100%', my: 1 }}>
            <TextField
                label="Company Industry (generated)"
                name="companyIndustry"
                id="companyIndustry"
                defaultValue={companyIndustry}
                aria-readonly={true}
                sx={{ mr: 1, width: '100%' }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Box sx={{ justifyContent: 'space-between' }}>
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
              {quote.id === undefined ? 'Create' : 'Update'}
            </Button>
          </Box>
        </DialogActions>
        <Snackbar
            anchorOrigin={{vertical:'top', horizontal: 'center'}}
            open={showError}
            autoHideDuration={6000}
            onClose={killAlert}
        >
          <Alert severity="error" sx={{width: '100%'}}>{dataSent}</Alert>
        </Snackbar>
      </Dialog>
  )
}