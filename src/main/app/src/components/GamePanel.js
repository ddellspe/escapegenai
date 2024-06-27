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

export default function GamePanel() {
  const [loading, setLoading] = useState(true);
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState({});
  const [teamId, setTeamId] = useState(undefined);
  const [password, setPassword] = useState(undefined);
  const [teamWord, setTeamWord] = useState(undefined);
  const [quote, setQuote] = useState(undefined);
  const [fact, setFact] = useState(undefined);

  useEffect(() => {
    const getTeams = async () => {
      try {
        const response = await fetch('game/teams');
        const data = await response.json();
        setTeams(data);
        setLoading(false);
        setTimeout(loadState, 500);
      } catch (err) {
      }
    }
    getTeams();
  }, []);

  const loadState = () => {
    let submission = JSON.parse(sessionStorage.getItem("submission"));
    if (submission !== null) {
      updateSubmission(submission);
    }
  }

  const selectTeam = (teamId) => {
    setTeamId(teamId);
    const team = teams.find(team => team.id === teamId);
    setSelectedTeam(team === undefined ? undefined : team);
    if (team === undefined) {
      sessionStorage.removeItem("submission");
    }
  }

  const updateSubmission = (submission) => {
    if (submission.id !== undefined) {
      selectTeam(submission.id);
      setPassword(submission.password === null ? undefined : submission.password);
      setTeamWord(submission.teamWord === null ? undefined : submission.teamWord);
      setQuote(submission.quote === null ? undefined : submission.quote);
      setFact(submission.fact === null ? undefined : submission.fact);
      sessionStorage.setItem('submission', JSON.stringify(submission));
    }
  }

  const handleChange = (event) => {
    if (event.target.name === 'teamId') {
      if (event.target.value !== teamId) {
        updateSubmission({"id": event.target.value, "password": null, "teamWord": null, "quote": null, "fact": null});
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
      updateSubmission(submission);
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
          <Grid item xs={12} alignItems="center" justifyContent="center">
            {teamId === undefined ? (
                    <Box sx={{width: '100%'}}>
                      <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                        Choose your team!
                      </Typography>
                      <Box sx={{width: '50%', my: 1, mx: "auto"}}>
                        <FormControl fullWidth>
                          <InputLabel id="teamLabel">Team</InputLabel>
                          <Select labelId="teamLabel" id="teamId" value={teamId} onChange={handleChange} label="Team" name="teamId">
                            <MenuItem></MenuItem>
                            {teams.map((team) => (
                                <MenuItem key={team.id} value={team.id}>{team.name}</MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Box>
                    </Box>
                )
                :
                (
                    <Grid item xs={12} alignItems="center" justifyContent="center">
                      <Box sx={{width: '100%'}}>
                        <Typography id="teams-modal-title" variant="h4" component="h2" align="center">
                          {selectedTeam === undefined ? "Loading Team Details" : selectedTeam.name}
                        </Typography>
                        <Button variant="contained" onClick={() => selectTeam(undefined)}>
                          Change Team
                        </Button>
                      </Box>
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
                                                  href={"https://escapegenai.com/game/team/" + teamId + "/password"} sx={{mx: 1}}>
                                        {"https://escapegenai.com/game/team/" + teamId + "/password"}
                                      </Typography>
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">Hint: Your password will contain 15-20 characters with
                                      characters from a-z, A-Z, 0-9, and !@#$%^&*()-=+_{}[] and no spaces.
                                    </Typography>
                                    <TextField label="Password" required name="password" id="password" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            ) : (
                                <input type="hidden" name="password" value={password}/>
                            )
                        }
                        {password !== undefined && teamWord === undefined ?
                            (
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
                                                  href={"https://escapegenai.com/game/team/" + teamId + "/word"} sx={{mx: 1}}>
                                        {"https://escapegenai.com/game/team/" + teamId + "/word"}
                                      </Typography>
                                    </Typography>
                                    <Typography paragraph={true} align="center" variant="body2">
                                      Hint: You may need to split your payload into smaller sections and aggregate counts.
                                    </Typography>
                                    <TextField label="Word" required name="teamWord" id="teamWord" sx={{mr: 1, width: '100%'}}/>
                                  </Box>
                                </Grid>
                            ) : (
                                <input type="hidden" name="teamWord" value={teamWord}/>
                            )
                        }
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