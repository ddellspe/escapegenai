import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import LinearProgress from '@mui/material/LinearProgress';

export default function TeamStatus({team}) {
  let progress = 0;
  let startTime = Date.parse(team.firstSelected);
  let endTime = Date.now();
  if (team.productsIdentified !== null) {
    progress += 30;
    endTime = Date.parse(team.productsIdentified);
  }
  if (team.leakageIdentified !== null) {
    progress += 30;
    endTime = Date.parse(team.leakageIdentified);
  }
  if (team.suppliersContacted !== null) {
    progress += 40;
    endTime = Date.parse(team.suppliersContacted);
  }
  let timeTaken = "";
  if (!isNaN(startTime)) {
    let time = endTime - startTime;
    var milliseconds = Math.floor((time % 1000) / 100),
        seconds = Math.floor((time / 1000) % 60),
        minutes = Math.floor((time / (1000 * 60)) % 60),
        hours = Math.floor((time / (1000 * 60 * 60)) % 24);

    hours = (hours > 0) ? (hours + " hour" + ((hours !== 1) ? "s" : "") + " ") : "";
    minutes = (minutes > 0 || hours > 0) ? (minutes + " minute" + ((minutes !== 1) ? "s" : "") + " ") : "";
    seconds = (seconds > 0 || minutes > 0 || hours > 0) ? (seconds + "." + milliseconds + " seconds") : "";

    timeTaken = hours + minutes + seconds;
  }
  if (team.firstSelected !== null) {
    return (
        <Box sx={{my: 1}}>
          <Box sx={{display: 'flex', alignItems: 'center'}}>
            <Typography align="left" variant="h5" component="h3" gutterBottom>
              {team.name}
            </Typography>
          </Box>
          <Box sx={{display: 'flex', alignItems: 'center'}}>
            <Grid container spacing={0}>
              <Grid item xs={12}>
                <LinearProgress
                    sx={{
                      height: 16
                    }}
                    variant="buffer"
                    value={progress}
                    valueBuffer={progress === 100 ? 100 : progress + (progress === 0 || progress === 30 ? 30 : 40)}
                    color='primary'/>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="h6" align="right">
                  {timeTaken}
                </Typography>
              </Grid>
            </Grid>
          </Box>
        </Box>
    );
  }
}
