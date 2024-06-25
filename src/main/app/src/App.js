import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import AdminSection from "./components/AdminSection";

function App() {
  return (
    <Container maxWidth="xl">
      <Box>
        <Typography align="center" variant="h3" component="h1" gutterBottom>
          GenAI Escape Room
        </Typography>
        <Typography align="center" variant="h5" component="h3" gutterBottom>
          Coming Soon!
        </Typography>
        <AdminSection />
      </Box>
    </Container>
  );
}
export default App;