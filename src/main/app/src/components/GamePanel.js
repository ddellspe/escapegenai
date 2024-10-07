import React, {useEffect, useState} from 'react';
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import MenuItem from "@mui/material/MenuItem";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

export default function GamePanel() {
  const [loading, setLoading] = useState(true);
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState({});
  const [teamId, setTeamId] = useState("");
  const [started, setStarted] = useState(false);
  const [highQuantity, setHighQuantity] = useState(undefined);
  const [highCost, setHighCost] = useState(undefined);
  const [overpaidInvoiceId, setOverpaidInvoiceId] = useState(undefined);
  const [underpaidInvoiceId, setUnderpaidInvoiceId] = useState(undefined);
  const [overpaidEmail, setOverpaidEmail] = useState(undefined);
  const [underpaidEmail, setUnderpaidEmail] = useState(undefined);
  const [showError, setShowError] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const selectTeam = (teamId) => {
    setTeamId(teamId);
    if (teamId === "" ) {
      setStarted(false);
      sessionStorage.removeItem("submission");
    }
    const team = teams.find(team => team.id === teamId);
    setSelectedTeam(team === "" || team === undefined ? "" : team);
  }

  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setErrorMsg(""), 1000);
  }

  const updateSubmission = (submission, submit) => {
    if (submission.id !== undefined) {
      selectTeam(submission.id);
      if (submit !== undefined) {
      }
      setHighQuantity(submission.highQuantity === null ? undefined : submission.highQuantity);
      setHighCost(submission.highCost === null ? undefined : submission.highCost);
      setOverpaidInvoiceId(submission.overpaidInvoiceId === null ? undefined: submission.overpaidInvoiceId);
      setUnderpaidInvoiceId(submission.underpaidInvoiceId === null ? undefined : submission.underpaidInvoiceId);
      setOverpaidEmail(submission.overpaidEmail === null ? undefined : submission.overpaidEmail);
      setUnderpaidEmail(submission.underpaidEmail === null ? undefined : submission.underpaidEmail);
      sessionStorage.setItem('submission', JSON.stringify(submission));
    }
  }

  useEffect(() => {
    setLoading(true)
    const getTeams = async () => {
      fetch('game/teams')
      .then((res) => res.json())
      .then((json) => {
        setTeams(json)
        setLoading(false);
        let submission = JSON.parse(sessionStorage.getItem("submission"));
        if (submission !== null && submission.id !== undefined) {
          setHighQuantity(submission.highQuantity === null ? undefined : submission.highQuantity);
          setHighCost(submission.highCost === null ? undefined : submission.highCost);
          setOverpaidInvoiceId(submission.overpaidInvoiceId === null ? undefined: submission.overpaidInvoiceId);
          setUnderpaidInvoiceId(submission.underpaidInvoiceId === null ? undefined: submission.underpaidInvoiceId);
          setOverpaidEmail(submission.overpaidEmail === null ? undefined: submission.overpaidEmail);
          setUnderpaidEmail(submission.underpaidEmail === null ? undefined: submission.underpaidEmail);
          setTimeout(() => selectTeam(submission.id), 500);
        }
      });
    }
    getTeams();
    /* eslint-disable react-hooks/exhaustive-deps */
  }, []);
  /* eslint-enable */

  const handleChange = (event) => {
    if (event.target.name === 'teamId') {
      if (event.target.value !== teamId) {
        const submission = {
          "id": event.target.value,
          "highQuantity": undefined,
          "highCost": undefined,
          "overpaidInvoiceId": undefined,
          "underpaidInvoice": undefined,
          "overpaidEmail": undefined,
          "underpaidEmail": undefined
        }
        updateSubmission(submission);
      }
    }
  }

  const submitTeamStart = (event) => {
    if (event.target.name === 'start') {
      const submission = {
        "id": teamId,
        "highQuantity": undefined,
        "highCost": undefined,
        "overpaidInvoiceId": undefined,
        "underpaidInvoice": undefined,
        "overpaidEmail": undefined,
        "underpaidEmail": undefined
      }
      fetch('game/submit', {
        method: 'POST',
        headers: new Headers({
          'Content-Type': 'application/json'
        }),
        body: JSON.stringify(submission)
      })
      .then((resp) => {
        setTimeout(() => setStarted(true), 600);
        setTimeout(() => selectTeam(teamId), 100);
        return resp.json();
      })
      .then();
    }
  }

  const setSubmission = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    var object = {};
    data.forEach((value, key) => {
      object[key] = value
    });
    fetch('game/submit', {
      method: 'POST',
      headers: new Headers({
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify(object)
    })
    .then((resp) => {
      return resp.json();
    })
    .then((submission) => {
      updateSubmission(submission, object);
    })
  };

  if (loading) {
    return (
        <Grid container spacing={2} justifyContent="center" alignItems="center">
          <Grid item>
            <Typography id="teams-modal-title" variant="h4" component="h2">
              Loading Teams
            </Typography>
          </Grid>
        </Grid>
    );
  } else {
    return (
        <Grid container spacing={2} justifyContent="center" alignItems="center">
          <Snackbar
              anchorOrigin={{vertical: 'top', horizontal: 'center'}}
              open={showError}
              autoHideDuration={6000}
              onClose={killAlert}
          >
            <Alert severity="error" sx={{width: '100%'}}>{errorMsg}</Alert>
          </Snackbar>
          <Grid item xs={10} xsoffset={1} alignItems="center" justifyContent="center">
            <Grid item xs={12} alignItems="center" justifyContent="center">
              <Box sx={{width: '100%'}} textAlign="center">
                <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                  Choose your team!
                </Typography>
                <Box sx={{width: '50%', my: 1, mx: "auto"}}>
                  <FormControl fullWidth>
                    <InputLabel id="teamLabel">Team</InputLabel>
                    <Select labelId="teamLabel" id="teamId" value={teamId !== undefined ? teamId : ""} onChange={handleChange} label="Team"
                            name="teamId" disabled={started}>
                      {teams.map((team) => (
                          <MenuItem key={team.id} value={team.id}>{team.name}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  { !started ? (
                      <Button variant="contained" name="start" onClick={(e) => submitTeamStart(e)} disabled={teamId === ""}>
                        Start Escape
                      </Button>
                    )
                    :
                    (
                      <Button variant="contained" onClick={() => selectTeam("")} disabled={!started}>
                        Change Team
                      </Button>
                    )
                  }
                </Box>
              </Box>
            </Grid>
            { !started ? (
                    <Grid item xs={12} alignItems="center" justifyContent="center">
                      <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                        Please select a team and click Start Escape to continue.
                      </Typography>
                    </Grid>
                )
                :
                (
                    <Grid item xs={12} alignItems="center" justifyContent="center">
                      <Grid item xs={12} alignItems="center" justifyContent="center" component="form" onSubmit={setSubmission}>
                        <input type="hidden" name="id" value={teamId}/>
                        {(highQuantity === undefined || highCost === undefined) ?
                            (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 1: Verify product details
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your first task is to use the provided invoice and report the product name with the most items
                                      invoiced, as well as the product name with the highest total cost in the invoice.
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Use the following Invoice to answer these questions:
                                      <Typography align="center" component="a"
                                                  href={"/game/invoice/" + selectedTeam.primaryInvoiceId} sx={{mx: 1}}>
                                        {"Invoice PDF"}
                                      </Typography>
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={6}>
                                        <TextField label="Product with largest item count" required name="highQuantity" id="highQuantity" sx={{mr: 1}} fullWidth/>
                                      </Grid>
                                      <Grid item xs={6}>
                                        <TextField label="Product with highest cost" required name="highCost" id="highCost" sx={{mr: 1}} fullWidth/>
                                      </Grid>
                                    </Grid>
                                  </Box>
                                </Grid>
                            ) : (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 1: Verify product details
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={6}>
                                        <TextField label="Product with largest item count" required name="highQuantity" id="highQuantity" sx={{mr: 1}} fullWidth value={highQuantity} disabled/>
                                      </Grid>
                                      <Grid item xs={6}>
                                        <TextField label="Product with highest cost" required name="highCost" id="highCost" sx={{mr: 1}} fullWidth value={highCost} disabled/>
                                      </Grid>
                                    </Grid>
                                    <input type="hidden" name="highQuantity" value={highQuantity}/>
                                    <input type="hidden" name="highCost" value={highCost}/>
                                  </Box>
                                </Grid>
                            )
                        }
                        {(() => {
                          if (highCost !== undefined && highQuantity !== undefined && (overpaidInvoiceId === undefined || underpaidInvoiceId === undefined)) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 2: Identify revenue leakage
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your second task is to look through all of your invoices to identify the invoices where the total cost of products does
                                      not match the total charged invoice price. You have been overcharged in one invoice and undercharged in one invoice.
                                      You need to identify the invoice where you were overcharged (charged amount was more than the total product cost)
                                      and identify the invoice where you were undercharged (charged amount was less than the total product cost) and submit
                                      the corresponding invoice ID into the appropriate field to continue.
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Use the following invoice PDFs to find your revenue leakage:
                                      {selectedTeam.invoiceIds.map((item, index) => (
                                          <Typography align="center" component="a"
                                                      href={"/game/invoice/" + item} sx={{mx: 1}}>
                                              {"Invoice PDF " + (index + 1)}
                                          </Typography>))
                                      }
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">
                                      Hint: You may want to upload all of your PDFs into your SnapLogic account in order to browse for all of them and process them in series.  You may also need more than Generative AI to validate the data
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={6}>
                                        <TextField label="Overpaid Invoice ID" required name="overpaidInvoiceId" id="overpaidInvoiceId" sx={{mr: 1}} fullWidth/>
                                      </Grid>
                                      <Grid item xs={6}>
                                        <TextField label="Underpaid Invoice ID" required name="underpaidInvoiceId" id="underpaidInvoiceId" sx={{mr: 1}} fullWidth/>
                                      </Grid>
                                    </Grid>
                                  </Box>
                                </Grid>
                            );
                          } else if (overpaidInvoiceId !== undefined && underpaidInvoiceId !== undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 2: Identify revenue leakage
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={6}>
                                        <TextField label="Overpaid Invoice ID" required name="overpaidInvoiceId" id="overpaidInvoiceId" sx={{mr: 1}} fullWidth value={overpaidInvoiceId} disabled/>
                                      </Grid>
                                      <Grid item xs={6}>
                                        <TextField label="Underpaid Invoice ID" required name="underpaidInvoiceId" id="underpaidInvoiceId" sx={{mr: 1}} fullWidth value={underpaidInvoiceId} disabled/>
                                      </Grid>
                                    </Grid>
                                    <input type="hidden" name="overpaidInvoiceId" value={overpaidInvoiceId}/>
                                    <input type="hidden" name="underpaidInvoiceId" value={underpaidInvoiceId}/>
                                  </Box>
                                </Grid>
                            );
                          } else {
                            return (
                              <span>
                                <input type="hidden" name="overpaidInvoiceId" value={overpaidInvoiceId}/>
                                <input type="hidden" name="underpaidInvoiceId" value={underpaidInvoiceId}/>
                              </span>
                            );
                          }
                        })()}
                        {(() => {
                          if (highCost !== undefined && highQuantity !== undefined && overpaidInvoiceId !== undefined && underpaidInvoiceId !== undefined && (overpaidEmail === undefined || underpaidEmail === undefined)) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 3: Contact your suppliers
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your third task is to put together an email to your suppliers (named in the invoices) who you overpaid and underpaid in order
                                      to work through how you will pay them the difference in the product cost and charged amount or how you expect to get reimbursement
                                      for the overpayment.
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Use the following invoice PDFs to obtain the details to contact your suppliers:
                                      {selectedTeam.invoiceIds.map((item, index) => (
                                          <Typography align="center" component="a"
                                                      href={"/game/invoice/" + item} sx={{mx: 1}}>
                                              {"Invoice PDF " + (index + 1)}
                                          </Typography>))
                                      }
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">
                                      Hint: Copy and paste the entire email into the field to ensure the appropriate details are available.
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={12}>
                                        <TextField label="Overpaid Supplier Email" required name="overpaidEmail" id="overpaidEmail" sx={{mr: 1}} fullWidth multiline/>
                                      </Grid>
                                      <Grid item xs={12}>
                                        <TextField label="Underpaid Supplier Email" required name="underpaidEmail" id="underpaidEmail" sx={{mr: 1}} fullWidth multiline/>
                                      </Grid>
                                    </Grid>
                                  </Box>
                                </Grid>
                            );
                          } else if (overpaidEmail !== undefined && underpaidEmail !== undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 3: Contact your suppliers
                                    </Typography>
                                    <Grid container spacing={4} sx={{pt: 2}}>
                                      <Grid item xs={12}>
                                        <TextField label="Overpaid Supplier Email" required name="overpaidEmail" id="overpaidEmail" sx={{mr: 1}} fullWidth multiline value={overpaidEmail} disabled/>
                                      </Grid>
                                      <Grid item xs={12}>
                                        <TextField label="Underpaid Supplier Email" required name="underpaidEmail" id="underpaidEmail" sx={{mr: 1}} fullWidth multiline value={underpaidEmail} disabled/>
                                      </Grid>
                                    </Grid>
                                    <input type="hidden" name="overpaidEmail" value={overpaidEmail}/>
                                    <input type="hidden" name="underpaidEmail" value={underpaidEmail}/>
                                  </Box>
                                </Grid>
                            );
                          } else {
                            return (
                              <span>
                                <input type="hidden" name="overpaidEmail" value={overpaidEmail}/>
                                <input type="hidden" name="underpaidEmail" value={underpaidEmail}/>
                              </span>
                            );
                          }
                        })()}
                        {(highCost === undefined || highQuantity === undefined || overpaidInvoiceId === undefined || underpaidInvoiceId === undefined || overpaidEmail === undefined || underpaidEmail === undefined) ? (
                            <Box sx={{justifyContent: 'space-between', ml: 'auto'}}>
                              <Button type="submit" variant="contained">
                                Submit Guess
                              </Button>
                            </Box>
                        ) : (
                            <Box sx={{width: '100%'}}>
                              <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                                Congratulations, you have completed the GenAI Escape Room!
                              </Typography>
                            </Box>
                        )}
                      </Grid>
                    </Grid>
                )
            }
          </Grid>
        </Grid>
    );
  }
}