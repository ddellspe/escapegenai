import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CloseIcon from '@mui/icons-material/Close';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import EditIcon from '@mui/icons-material/Edit';
import QuoteForm from './QuoteForm';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import LinearProgress from '@mui/material/LinearProgress';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Typography from '@mui/material/Typography';

export default function QuoteList({opened, creds, onClose}) {
  const defaultQuote = {
    "id": null,
    "quote": "",
    "extendedQuote": "",
    "author": "",
    "authorAddress": "",
    "authorTitle": "",
    "company": "",
    "companyAddress": "",
    "companyIndustry": ""
  };
  const [quotes, setQuotes] = useState([]);
  const [quote, setQuote] = useState(defaultQuote);
  const [loading, setLoading] = useState(true);
  const [quoteDialog, setQuoteDialog] = useState(false);

  const editQuote = (quoteId) => {
    const selectedQuote = quotes.find(quote => quote.id === quoteId);
    setQuote(selectedQuote === undefined ? defaultQuote : selectedQuote);
    setTimeout(() => setQuoteDialog(true), 50)
  }

  const newQuote = () => {
    editQuote(null)
  }

  const closeQuoteModal = (success, message) => {
    if (typeof success === 'boolean') {
      setQuoteDialog(false);
      onClose(success, message);
    } else {
      setQuoteDialog(false);
    }
  }

  useEffect(() => {
    if (!opened || creds === undefined) {
      return;
    }
    setLoading(true);
    const getQuotes = async () => {
      try {
        const response = await fetch('api/quotes',
            {headers: new Headers({'Authorization': 'Basic ' + creds})});
        const data = await response.json();
        setQuotes(data);
        setLoading(false);
      } catch (err) {
      }
    }
    getQuotes();
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
                <Typography id="quotes-modal-title" variant="h4" component="h2">
                  Quotes Listing
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
                <Typography id="quotes-modal-title" variant="h4" component="h2">
                  Quotes Listing
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
                {quotes.map((quote) => {
                      return (
                          <ListItem
                              key={quote.id}
                              secondaryAction={
                                <IconButton edge='end' aria-label='edit'
                                            onClick={() => editQuote(quote.id)}>
                                  <EditIcon/>
                                </IconButton>
                              }
                          >
                            <ListItemText
                                primary={quote.quote}
                                secondary={quote.author}/>
                          </ListItem>
                      )
                    }
                )}
              </List>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button variant="contained" onClick={newQuote}>
              Add Quote
            </Button>
            <QuoteForm opened={quoteDialog} creds={creds} onClose={closeQuoteModal}
                      quoteObj={quote}/>
          </DialogActions>
        </Dialog>
    )
  }
}