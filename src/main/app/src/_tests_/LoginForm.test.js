import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import LoginForm from '../components/LoginForm';

beforeEach(() => {
  fetch.resetMocks();
  jest.useFakeTimers();
});

afterEach(() => {
  jest.useRealTimers();
});

const onClose = jest.fn(() => true);

test('when opened, dialog shows', () => {
  const opened = true

  render(<LoginForm opened={opened} onClose={onClose} />)

  expect(screen.getByText(/Login/i)).toBeInTheDocument();
});

test('when not opened, dialog does not show', () => {
  const opened = false

  render(<LoginForm opened={opened} onClose={onClose} />)

  expect(screen.queryByText(/Login/i)).not.toBeInTheDocument();
});

test('when visibility icon clicked, no password input', async () => {
  const opened = true
  const user = userEvent.setup({delay: null});

  render(<LoginForm opened={opened} onClose={onClose} />)

  expect(screen.getByText(/Login/i)).toBeInTheDocument();
  const visibilityIcon = screen.getByTestId(/VisibilityIcon/);
  expect(visibilityIcon).toBeInTheDocument();

  await user.click(visibilityIcon);

  expect(screen.getByTestId(/VisibilityOffIcon/)).toBeInTheDocument();
});

test('when username and password set and submitted, session storage updated', async () => {
  const opened = true
  const user = userEvent.setup({delay: null});

  const setItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'setItem');

  render(<LoginForm opened={opened} onClose={onClose} />)

  expect(screen.getByText(/Login/i)).toBeInTheDocument();
  const usernameInput = screen.getByLabelText(/Username/);
  expect(usernameInput).toBeInTheDocument();
  const passwordInput = screen.getByLabelText(/Password/);
  expect(passwordInput).toBeInTheDocument();
  const loginButton = await screen.findByText(/Log In/);
  expect(loginButton).toBeInTheDocument();

  await user.type(usernameInput, "username");
  await user.type(passwordInput, "password");
  await user.click(loginButton);

  expect(setItemSpy).toHaveBeenCalledWith("auth", "dXNlcm5hbWU6cGFzc3dvcmQ=");
});