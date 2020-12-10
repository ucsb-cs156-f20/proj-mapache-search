import React from "react";
import { render, waitFor } from "@testing-library/react";
import { Router, useHistory, useParams } from 'react-router-dom';
import { createMemoryHistory } from 'history';
import EditStudent from "main/pages/Students/EditStudent";
import userEvent from "@testing-library/user-event";

import useSWR from "swr";
jest.mock("swr");

import { useAuth0 } from "@auth0/auth0-react";
jest.mock("@auth0/auth0-react");

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"), // use real functions 
  useParams: jest.fn(), // except for just this one
  useHistory: jest.fn() // and this one too
}));

import { fetchWithToken } from "main/utils/fetch";
jest.mock("main/utils/fetch", () => ({
  fetchWithToken: jest.fn()
}));

// import { useToasts } from 'react-toast-notifications'
// jest.mock('react-toast-notifications', () => ({
//   useToasts: jest.fn()
// }));

describe("Edit Course page test", () => {
  const student =
  {
    email: "jeff@ucsb.edu",
    teamName: "team-7pm-b",
  };
  const user = {
    name: "test user",
  };
  const getAccessTokenSilentlySpy = jest.fn();
  const mutateSpy = jest.fn();
  // const addToast = jest.fn();

  beforeEach(() => {
    useAuth0.mockReturnValue({
      admin: undefined,
      getAccessTokenSilently: getAccessTokenSilentlySpy,
      user: user
    });
    useParams.mockReturnValue({
        studentId: '1'
    });
    // useToasts.mockReturnValue({
    //   addToast: addToast
    // })
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test("Renders student with existing student", async () => {
    useSWR.mockReturnValue({
      data: student,
      error: undefined,
      mutate: mutateSpy,
    });
    const { getByText, getByLabelText } = render(
      <EditStudent />
    );

    await waitFor(() => (
      (expect(getByText("Email")).toBeInTheDocument() &&
      expect(getByLabelText("Email").value).toEqual(student.email)) &&
      expect(getByText("TeamName")).toBeInTheDocument() &&
      expect(getByLabelText("TeamName").value).toEqual(student.teamName)
    ));
    
  });

  // test("Renders spinner with no existing student", () => {
  //   useSWR.mockReturnValue({
  //     data: undefined,
  //     error: undefined,
  //     mutate: mutateSpy,
  //   });
  //   const { getByTestId } = render(
  //     <EditStudent />
  //   );
  //   const spinner = getByTestId("spinner");
  //   expect(spinner).toBeInTheDocument();
  // });

  test("With existing student, pressing submit routes back to students page", async () => {
    useSWR.mockReturnValue({
      data: student,
      error: undefined,
      mutate: mutateSpy,
    });

    const pushSpy = jest.fn();
    useHistory.mockReturnValue({
      push: pushSpy
    });

    const { getByText } = render(
      <EditStudent />
    );

    const submitButton = getByText("Submit");
    expect(submitButton).toBeInTheDocument();
    userEvent.click(submitButton);

    await waitFor(() => expect(pushSpy).toHaveBeenCalledTimes(1));
    expect(pushSpy).toHaveBeenCalledWith("/admin/students");

  });

  test("clicking submit button redirects to home page on error", async () => {

    fetchWithToken.mockImplementation(() => {
      throw new Error();
    });

    useSWR.mockReturnValue({
      data: student,
      error: undefined,
      mutate: mutateSpy,
    });

    const pushSpy = jest.fn();
    useHistory.mockReturnValue({
      push: pushSpy
    });

    const { getByText } = render(
      <EditStudent />
    );

    const submitButton = getByText("Submit");
    expect(submitButton).toBeInTheDocument();
    userEvent.click(submitButton);

    // expect(addToast).toHaveBeenCalledTimes(1);
    // expect(addToast).toHaveBeenCalledWith("Error saving student", { appearance: 'error' });

  });


});