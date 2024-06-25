import { render, screen } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import userEvent from '@testing-library/user-event';
import React from 'react';
import AdminSection from '../components/AdminSection';

beforeEach(() => {
  fetch.resetMocks();
  jest.useFakeTimers();
});

afterEach(() => {
  jest.useRealTimers();
});
const mockLoginForm = jest.fn();

jest.mock("../components/LoginForm", ()  => (props) => {
  mockLoginForm(props);
  return <div>login form</div>;
})

test('when sessionStorage missing, login shows only', () => {
  render(<AdminSection />)

  expect(screen.getByTestId("LoginTwoToneIcon")).toBeInTheDocument();
});

test('when sessionStorage missing, login is clicked, login form shows', async () => {
  render(<AdminSection />)

  const loginButton = await screen.getByTestId("LoginTwoToneIcon");
  expect(loginButton).toBeInTheDocument();
  act(() => {
    userEvent.click(loginButton);
  });

  expect(await screen.getByText(/login form/)).toBeInTheDocument();
  expect(mockLoginForm).toHaveBeenCalledTimes(2);
  expect(mockLoginForm.mock.calls[1][0]['opened']).toBe(true);
  await act(async () => {
    sessionStorage.setItem('auth', 'blah');
    mockLoginForm.mock.calls[1][0]['onClose']();
  })
  expect(await screen.getByTestId("MenuIcon")).toBeVisible();
});

test('when sessionStorage auth does not work, error message shows and login icon returns', async () => {
  const getItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'getItem');
  const removeItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'removeItem');

  sessionStorage.setItem('auth', 'blah');
  fetch.mockResolvedValueOnce({ok: false});

  await act(async () =>{
    render(<AdminSection />);
  });
  expect(await screen.getByText(/Error on login/)).toBeVisible();
  act(() => {
    jest.advanceTimersByTime(5000);
  })
  expect(await screen.getByText(/Error on login/)).not.toBeVisible();

  expect(getItemSpy).toHaveBeenCalledWith('auth');
  expect(removeItemSpy).toHaveBeenCalledWith('auth');
  expect(await screen.getByTestId("LoginTwoToneIcon")).toBeInTheDocument();
});

test('when sessionStorage auth works, speed dial present', async () => {
  const getItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'getItem');

  sessionStorage.setItem('auth', 'blah');
  fetch.mockResolvedValueOnce({ok: true});

  await act(async () =>{
    render(<AdminSection />);
  });

  expect(getItemSpy).toHaveBeenCalledWith('auth');
  expect(await screen.getByTestId("MenuIcon")).toBeInTheDocument();
});

test('when sessionStorage auth works, speed dial clicked, logout button visible', async () => {
  const getItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'getItem');

  sessionStorage.setItem('auth', 'blah');
  fetch.mockResolvedValueOnce({ok: true});

  await act(async () =>{
    render(<AdminSection />);
  });

  expect(getItemSpy).toHaveBeenCalledWith('auth');
  const menuButton = await screen.getByTestId("MenuIcon");
  act(() => {
    userEvent.click(menuButton);
  });

  expect(await screen.getByTestId("LogoutTwoToneIcon")).toBeVisible();
});

test('when sessionStorage auth works, speed dial clicked, close clicked', async () => {
  const getItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'getItem');

  sessionStorage.setItem('auth', 'blah');
  fetch.mockResolvedValueOnce({ok: true});

  await act(async () =>{
    render(<AdminSection />);
  });

  expect(getItemSpy).toHaveBeenCalledWith('auth');
  const menuButton = await screen.getByTestId("MenuIcon");
  act(() => {
    userEvent.click(menuButton);
  });
  const closeIcon = await screen.getByTestId("CloseIcon");
  expect(closeIcon).toBeVisible();
  act(() => {
    userEvent.click(closeIcon);
  });
  expect(await screen.getByTestId("MenuIcon")).toBeVisible();
});

test('when sessionStorage auth works, speed dial clicked, logout clicked', async () => {
  const getItemSpy = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'getItem');

  sessionStorage.setItem('auth', 'blah');
  fetch.mockResolvedValueOnce({ok: true});

  await act(async () =>{
    render(<AdminSection />);
  });

  expect(getItemSpy).toHaveBeenCalledWith('auth');
  const menuButton = await screen.getByTestId("MenuIcon");
  act(() => {
    userEvent.click(menuButton);
  });

  const logoutIcon = await screen.getByTestId("LogoutTwoToneIcon");
  expect(logoutIcon).toBeVisible();
  act(() => {
    userEvent.click(logoutIcon);
  });

  expect(screen.getByTestId("LoginTwoToneIcon")).toBeInTheDocument();
});