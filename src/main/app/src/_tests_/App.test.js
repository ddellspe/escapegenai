import { render, screen } from '@testing-library/react';
import App from '../App';

test('renders title on page', () => {
  render(<App />);
  expect(screen.getByText(/GenAI Escape Room/i)).toBeInTheDocument();
});
