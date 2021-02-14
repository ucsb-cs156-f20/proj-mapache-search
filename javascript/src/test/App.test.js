import React from "react";
import { render } from "@testing-library/react";
import App from "main/App";
import { createMemoryHistory } from "history";
import { Router } from "react-router-dom";
import { useAuth0 } from "@auth0/auth0-react";
import useSWR from "swr";
jest.mock("@auth0/auth0-react");
jest.mock("swr");

describe("App tests", () => {
  beforeEach(() => {
    useAuth0.mockReturnValue({
      isAuthenticated: true,
      isLoading: false,
      logout: jest.fn(),
      loginWithRedirect: jest.fn(),
      getAccessTokenSilently: jest.fn(),
    });
    useSWR.mockReturnValue({
      data: {
        role: "guest"
      }
    });
  });

  test("renders without crashing", () => {
    const history = createMemoryHistory();
    const { getByTestId } = render(
      <Router history={history}>
        <App />
      </Router>
    );
    const brand = getByTestId("brand");
    expect(brand).toBeInTheDocument();
  });
});
