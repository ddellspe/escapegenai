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
  const [password, setPassword] = useState(undefined);
  const [teamWord, setTeamWord] = useState(undefined);
  const [quote, setQuote] = useState(undefined);
  const [fact, setFact] = useState(undefined);
  const [showError, setShowError] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  const currentHost = `${window.location.protocol}//${window.location.hostname}${window.location.port !== "" ? (":" + window.location.port) : ""}`;

  const selectTeam = (teamId) => {
    setTeamId(teamId);
    const team = teams.find(team => team.id === teamId);
    setSelectedTeam(team === "" ? "" : team);
    if (team === undefined) {
      sessionStorage.removeItem("submission");
    }
  }

  const killAlert = () => {
    setShowError(false);
    setTimeout(() => setErrorMsg(""), 1000);
  }

  const updateSubmission = (submission, submit) => {
    if (submission.id !== undefined) {
      selectTeam(submission.id);
      if (submit !== undefined) {
        if (submit.password !== undefined && submit.password !== '' && submit.password !== submission.password) {
          setShowError(true);
          setErrorMsg("Password Incorrect");
        } else if (submit.teamWord !== undefined && submit.teamWord !== '' && submit.teamWord !== submission.teamWord) {
          setShowError(true);
          setErrorMsg("Word Incorrect");
        } else if (submit.quote !== undefined && submit.quote !== '' && submit.quote !== submission.quote) {
          setShowError(true);
          setErrorMsg("Quote Incorrect");
        } else if (submit.fact !== undefined && submit.fact !== '' && submit.fact !== submission.fact) {
          setShowError(true);
          setErrorMsg("Fun Fact Incorrect");
        }
      }
      setPassword(submission.password === null ? undefined : submission.password);
      setTeamWord(submission.teamWord === null ? undefined : submission.teamWord);
      setQuote(submission.quote === null ? undefined : submission.quote);
      setFact(submission.fact === null ? undefined : submission.fact);
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
          setPassword(submission.password === null ? "" : submission.password);
          setTeamWord(submission.teamWord === null ? "" : submission.teamWord);
          setQuote(submission.quote === null ? "" : submission.quote);
          setFact(submission.fact === null ? "" : submission.fact);
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
        const submission = {"id": event.target.value, "password": undefined, "teamWord": undefined, "quote": undefined, "fact": undefined}
        updateSubmission(submission);
        fetch('game/submit', {
          method: 'POST',
          headers: new Headers({
            'Content-Type': 'application/json'
          }),
          body: JSON.stringify(submission)
        })
        .then((resp) => {
          return resp.json();
        })
        .then();
      }
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
                            name="teamId" disabled={teamId
                        !== ""}>
                      {teams.map((team) => (
                          <MenuItem key={team.id} value={team.id}>{team.name}</MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <Button variant="contained" onClick={() => selectTeam("")} disabled={teamId === ""}>
                    Change Team
                  </Button>
                </Box>
              </Box>
            </Grid>
            {teamId === "" ? (
                    <Grid item xs={12} alignItems="center" justifyContent="center">
                      <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                        Please select a team to continue.
                      </Typography>
                    </Grid>
                )
                :
                (
                    <Grid item xs={12} alignItems="center" justifyContent="center">
                      <Grid item xs={12} alignItems="center" justifyContent="center" component="form" onSubmit={setSubmission}>
                        <input type="hidden" name="id" value={teamId}/>
                        {password === undefined ?
                            (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 1: Discover your password
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your first task is to go to the following link and find your password, for some reason, the inventors of this
                                      escape room thought that it would be a brilliant idea to hide passwords within webpages somewhere and we're not
                                      quite sure where it is, but I have a feeling that a Large Language Model would be able to extract your password
                                      from this page. Use a LLM with an appropriate prompt to uncover your password, enter your password into the
                                      following field and then submit your guess!
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Your password can be somewhere in the following page:
                                      <Typography align="center" component="a"
                                                  href={"/game/team/" + teamId + "/password"} sx={{mx: 1}}>
                                        {currentHost + "/game/team/" + teamId + "/password"}
                                      </Typography>
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">Hint: Your password will contain 15-20 characters with
                                      characters from a-z, A-Z, 0-9, and !@#$%^&*()-=+_{}[] and no spaces.
                                    </Typography>
                                    <TextField label="Password" required name="password" id="password" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            ) : (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 1: Discover your password
                                    </Typography>
                                    <TextField label="Password" name="password" id="password" sx={{mr: 1, width: '100%'}} value={password} disabled/>
                                    <input type="hidden" name="password" id="password" value={password}/>
                                  </Box>
                                </Grid>
                            )
                        }
                        {(() => {
                          if (password !== undefined && teamWord === undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 2: Count some words!
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your second task is to go to the following link and determine which word appears the most. For some reason the
                                      escape room organizers got REALLY bored and just wanted to make this task absolutely astronomically difficult,
                                      leading to over 100,000 words being present to have to analyze. Use a Large Language Model to process which
                                      word is the most common in the page and submit that word to the escape room attendant and then we will see what
                                      the next steps really are. Good Luck!
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Your data to count from can be found at:
                                      <Typography align="center" component="a"
                                                  href={"/game/team/" + teamId + "/word"} sx={{mx: 1}}>
                                        {currentHost + "/game/team/" + teamId + "/word"}
                                      </Typography>
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">
                                      Hint: You may need to split your payload into smaller sections and aggregate counts. The most frequent word in
                                      one section of the document is not necessarily the most frequent word across the whole page.
                                    </Typography>
                                    <TextField label="Word" required name="teamWord" id="teamWord" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            );
                          } else if (teamWord !== undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 2: Count some words!
                                    </Typography>
                                    <TextField label="Hidden Word" name="teamWord" id="teamWord" sx={{mr: 1, width: '100%'}} value={teamWord}
                                               disabled/>
                                    <input type="hidden" name="teamWord" value={teamWord}/>
                                  </Box>
                                </Grid>
                            );
                          } else {
                            return (<input type="hidden" name="teamWord" value={teamWord}/>);
                          }
                        })()}
                        {(() => {
                          if (password !== undefined && teamWord !== undefined && quote === undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 3: Unscramble the quote!
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your third task is to find the words in each of the individual links in the provided link below (it is a JSON
                                      response with multiple links). Once you collect the most frequent word in each of the attached links, that forms
                                      a quote when appropriately unscrambled. Your task is to provide that quote to the escape room attendant in order
                                      to unlock your last escape room challenge and FINALLY break free from this torture.
                                    </Typography>
                                    <Typography align="center" component="h6">
                                      Your data to see all of the links can be found at:
                                      <Typography align="center" component="a"
                                                  href={"/game/team/" + teamId + "/quote"} sx={{mx: 1}}>
                                        {currentHost + "/game/team/" + teamId + "/quote"}
                                      </Typography>
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">
                                      Hint: When entering your quote, be sure it only uses the words from your clue (no period is necessary).
                                    </Typography>
                                    <TextField label="Quote" required name="quote" id="quote" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            );
                          } else if (quote !== undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 3: Unscramble the quote!
                                    </Typography>
                                    <TextField label="Unscrambled Quote" name="quote" id="quote" sx={{mr: 1, width: '100%'}} value={quote}
                                               disabled/>
                                    <input type="hidden" name="quote" value={quote}/>
                                  </Box>
                                </Grid>
                            );
                          } else {
                            return (
                                <input type="hidden" name="quote" value={quote}/>
                            );
                          }
                        })()}
                        {(() => {
                          if (password !== undefined && teamWord !== undefined && quote !== undefined && fact === undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 4: Give me the FACTS!
                                    </Typography>
                                    <Typography paragraph={true} align="center">
                                      Your final task is to look up the <b>{selectedTeam === undefined ? "PENDING"
                                        : selectedTeam.funFactType}</b> fact for the quote you provided above, you can find the quote using the
                                      Pinecone instance with the account label <b>Pinecone</b> with the Index: <b>quote-facts</b> and <b>no
                                      namespace</b> set. All embeddings have been created using the <b>amazon.titan-embed-text-v1</b> model. You will
                                      likely need to filter for your fun fact type listed before as your quote may embed to multiple items in the
                                      vector database, the schema of the metadata is as follows:
                                    </Typography>
                                    <Grid container justifyContent="center" sx={{my: 1}}>
                                      <Box sx={{p: 1, border: 2}} style={{backgroundColor: "#EEEEEE", borderColor: "#222222", borderStyle: "solid"}}>
                                        <Typography component="pre" variant="pre">
                                          {`{\n\t"funFactType": "<funFactType>",\n\t"funFact": "<value>"\n}`}
                                        </Typography>
                                      </Box>
                                    </Grid>
                                    <TextField label="Fun Fact" required name="fact" id="fact" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            );
                          } else if (quote !== undefined) {
                            return (
                                <Grid item xs={12} alignItems="center" justifyContent="center">
                                  <Box sx={{width: '100%', my: 1}}>
                                    <Typography variant="h5" component="h3" align="center">
                                      Task 4: Give me the FACTS!
                                    </Typography>
                                    <TextField label="Fun Fact" name="fact" id="fact" sx={{mr: 1, width: '100%'}} value={fact}
                                               disabled/>
                                    <input type="hidden" name="fact" value={fact}/>
                                  </Box>
                                </Grid>
                            );
                          } else {
                            return (
                                <input type="hidden" name="fact" value={fact}/>
                            );
                          }
                        })()}
                        {(password === undefined || teamWord === undefined || quote === undefined || fact === undefined) ? (
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