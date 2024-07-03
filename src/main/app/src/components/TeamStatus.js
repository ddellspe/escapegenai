import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import LinearProgress from '@mui/material/LinearProgress';

export default function TeamStatus({team}) {
  let progress = 0;
  let startTime = Date.parse(team.firstSelected);
  let endTime = Date.now();
  if (team.passwordEntered !== null) {
    progress += 25;
    endTime = Date.parse(team.passwordEntered);
  }
  if (team.wordEntered !== null) {
    progress += 25;
    endTime = Date.parse(team.wordEntered);
  }
  if (team.quoteEntered !== null) {
    progress += 25;
    endTime = Date.parse(team.quoteEntered);
  }
  if (team.funFactEntered !== null) {
    progress += 25;
    endTime = Date.parse(team.funFactEntered);
  }
  let timeTaken = "";
  if (!isNaN(startTime)) {
    let time = endTime - startTime;
    var milliseconds = Math.floor((time % 1000) / 100),
        seconds = Math.floor((time / 1000) % 60),
        minutes = Math.floor((time / (1000 * 60)) % 60),
        hours = Math.floor((time / (1000 * 60 * 60)) % 24);

    hours = (hours > 0) ? (((hours < 10) ? "0" + hours : hours) + " hour" + ((hours !== 1) ? "s" : "") + " ") : "";
    minutes = (minutes > 0 || hours > 0) ? (((minutes < 10) ? "0" + minutes : minutes) + " minute" + ((minutes !== 1) ? "s" : "") + " ") : "";
    seconds = (seconds > 0 || minutes > 0 || hours > 0) ? (((seconds < 10) ? "0" + seconds : seconds) + "." + milliseconds + " seconds") : "";

    timeTaken = hours + minutes + seconds;
  }
  if (team.firstSelected !== null) {
    return (
        <Box sx={{my: 5}}>
          <Box sx={{display: 'flex', alignItems: 'center'}}>
            <Typography align="left" variant="h5" component="h3" gutterBottom>
              {team.name}
            </Typography>
          </Box>
          <Box sx={{display: 'flex', alignItems: 'center'}}>
            <Box sx={{width: '100%', mr: 1}}>
              <LinearProgress
                  sx={{
                    height: 16
                  }}
                  variant="buffer"
                  value={progress}
                  valueBuffer={progress === 100 ? 100 : progress + 25}
                  color='primary'/>
            </Box>
            <Box sx={{ minWidth: 250 }}>
              <Typography variant="h6" align="right">
                {timeTaken}
              </Typography>
            </Box>
          </Box>
        </Box>
    );
  }
}
