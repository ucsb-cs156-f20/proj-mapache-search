import React from "react";
import { waitFor, render } from "@testing-library/react";
import useSWR from "swr";
jest.mock("swr");
import { useAuth0 } from "@auth0/auth0-react";
jest.mock("@auth0/auth0-react");
import Students from "main/pages/Students/Students";
import userEvent from "@testing-library/user-event";
import { fetchWithToken } from "main/utils/fetch";
jest.mock("main/utils/fetch");
import { buildCreateCourse, buildDeleteCourse, buildUpdateCourse } from "main/services/Courses/CourseService";

jest.mock("main/services/Courses/CourseService", () => ({
  buildCreateCourse: jest.fn(),
  buildDeleteCourse: jest.fn(),
  buildUpdateCourse: jest.fn()
}) );
import { useHistory } from "react-router-dom";
jest.mock("react-router-dom", () => ({
  useHistory: jest.fn(),
}));

describe("Students page test", () => {
  const students = [
    {
      email: "jeff@ucsb.edu",
      teamName: "team-7pm-b",
    },
    {
      email: "bill@ucsb.edu",
      teamName: "team-7pm-d"
    },
  ];
  const user = {
    name: "test user",
  };
  const getAccessTokenSilentlySpy = jest.fn();
  const mutateSpy = jest.fn();

  beforeEach(() => {
    useAuth0.mockReturnValue({
      admin: undefined,
      getAccessTokenSilently: getAccessTokenSilentlySpy,
      user: user
    });
    useSWR.mockReturnValue({
      data: students,
      error: undefined,
      mutate: mutateSpy,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test("renders without crashing", () => {
    render(<Students />);
  });

  test("renders loading while student list is undefined", () => {
    useSWR.mockReturnValue({
      data: undefined,
      error: undefined,
      mutate: mutateSpy,
    });
    const { getByAltText } = render(<Students />);
    const loading = getByAltText("Loading");
    expect(loading).toBeInTheDocument();
  });

  test("renders an error message when there is an error", () => {
    useSWR.mockReturnValue({
      data: undefined,
      error: new Error("this is an error"),
      mutate: mutateSpy,
    });
    const { getByText } = render(<Students />);
    const error = getByText(/error/);
    expect(error).toBeInTheDocument();
  });

  test("can delete a student", async () => {
    const fakeDeleteFunction = jest.fn();
    buildDeleteCourse.mockReturnValue(fakeDeleteFunction);
    const { getAllByTestId } = render(<Students />);
    const deleteButtons = getAllByTestId("delete-button");
    userEvent.click(deleteButtons[0]);
    await waitFor(() => expect(fakeDeleteFunction).toHaveBeenCalledTimes(1));
  });

  test("can edit a course", async () => {

    const pushSpy = jest.fn();
    useHistory.mockReturnValue({
      push: pushSpy
    });

    const { getAllByTestId } = render(<Students />);
    const editButtons = getAllByTestId("edit-button");
    userEvent.click(editButtons[0]);

    await waitFor(() => expect(pushSpy).toHaveBeenCalledTimes(1));
  });

  test("can click to add a course", async () => {

    const pushSpy = jest.fn();
    useHistory.mockReturnValue({
      push: pushSpy
    });

    const { getByText } = render(<Students />);
    const newCourseButton = getByText("New Course");
    userEvent.click(newCourseButton);

    await waitFor(() => expect(pushSpy).toHaveBeenCalledTimes(1));
  });


});
