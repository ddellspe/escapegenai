import { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControl from '@mui/material/FormControl';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import InputLabel from '@mui/material/InputLabel';
import OutlinedInput from '@mui/material/OutlinedInput';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';

export default function LoginForm({opened, onClose}) {
  const [showPassword, setShowPassword] = useState(false);

  const handleClickShowPassword = () => setShowPassword((show) => !show);

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const setLogin = (event) => {
    event.preventDefault();
    const data = new FormData(event.target);
    const username = data.get('username');
    const password = data.get('password');
    sessionStorage.setItem('auth', btoa(username + ":" + password));
    onClose();
  };

  return (
      <Dialog
          open={opened}
          onClose={onClose}
          component="form"
          onSubmit={setLogin}
          noValidate
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
              <Typography id="games-modal-title" variant="h5" component="h3">
                Login
              </Typography>
            </Grid>
          </Grid>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ width: '100%' }}>
            <TextField
                label="Username"
                required
                name="username"
                id="username"
                sx={{ m: 1, width: '25ch' }}
            />
          </Box>
          <Box sx={{ width: '100%' }}>
            <FormControl sx={{ m: 1, width: '25ch' }} variant="outlined">
              <InputLabel htmlFor="password">Password</InputLabel>
              <OutlinedInput
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  name="password"
                  endAdornment={
                    <InputAdornment position="end">
                      <IconButton
                          aria-label="toggle password visibility"
                          onClick={handleClickShowPassword}
                          onMouseDown={handleMouseDownPassword}
                          edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  }
                  label="Password"
              />
            </FormControl>
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
              Log In
            </Button>
          </Box>
        </DialogActions>
      </Dialog>
  )
}