import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import AdminSection from "./components/AdminSection";
import GamePanel from "./components/GamePanel";

function App() {
  return (
    <Container maxWidth="xl">
      <Box>
        <Typography align="center" variant="h3" component="h1" gutterBottom>
          GenAI Escape Room
        </Typography>
        <GamePanel />
        <AdminSection />
      </Box>
    </Container>
  );
}
export default App;