import React from "react";
import { waitFor, render } from "@testing-library/react";
import HistogramOfMessagesByUser from "main/pages/AnalyzeMessageData/HistogramOfMessagesByUser";
import userEvent from "@testing-library/user-event";
import { fetchWithToken } from "main/utils/fetch";
import { useAuth0 } from "@auth0/auth0-react";


jest.mock("@auth0/auth0-react");

jest.mock("main/utils/fetch", () => ({
  fetchWithToken: jest.fn()
}));

jest.mock('react-chartjs-2', () => ({
  Bar: () => null
}));

const sampleMessageList = [
  {
    "type": "message",
    "subtype": null,
    "ts": "1620786171.027200",
    "user": "U01QP2PTZN2",
    "text": "test message 1",
    "channel": "some channel",
    "user_profile": {
        "email": null,
        "real_name": "Test User",
        "display_name": "",
        "name": "testuser"
    },
    "message_reactions": null
  },
  {
      "type": "message",
      "subtype": null,
      "ts": "1620786396.030900",
      "user": "U01QP2PTZN2",
      "text": "test message 2",
      "channel": "some channel",
      "user_profile": {
          "email": null,
          "real_name": "Test User",
          "display_name": "",
          "name": "testuser"
      },
      "message_reactions": null
  }
];

describe("HistogramOfMessagesByUser tests", () => {
  beforeEach(() => {
    useAuth0.mockReturnValue({
      getAccessTokenSilently: "fakeToken"
    });
  });  
  
  test("renders without crashing", () => {
    render(<HistogramOfMessagesByUser/>);
  });

  test("renders without crashing on search for user", async () => {
    const { getByLabelText } = render(<HistogramOfMessagesByUser />);
    const selectSearchUser = getByLabelText("Enter a user");
    userEvent.type(selectSearchUser, "Test User");
    expect(selectSearchUser.value).toBe("Test User");
  })

  test("Fetch with token is called correctly when user clicks button", async () => {
    const expectedURL = `/api/members/messages/usersearch?searchUser=Test User`;
    const options = {
      method: 'GET'
    }
    fetchWithToken.mockResolvedValue(sampleMessageList);
    const { getByText, getByLabelText } = render(<HistogramOfMessagesByUser />);
    const selectSearchUser = getByLabelText("Enter a user");
    userEvent.type(selectSearchUser, "Test User");
    const goButton = getByText("Go");
    userEvent.click(goButton);
    await waitFor(() => {
      expect(fetchWithToken).toHaveBeenCalledTimes(1);
      expect(fetchWithToken).toHaveBeenCalledWith(expectedURL, "fakeToken", options);
    })
  })

  test("Test the date and time stamp range", async () => {
    fetchWithToken.mockResolvedValue(sampleMessageList);
    const { getByText, getByLabelText } = render(<HistogramOfMessagesByUser />);
    const selectSearchUser = getByLabelText("Enter a user");
    userEvent.type(selectSearchUser, "Test User");
    const goButton = getByText("Go");
    userEvent.click(goButton);
    await waitFor(() => {
      expect(getByText("5/9/2021 - 5/15/2021")).toBeInTheDocument();
    })
  })

  test("test if renders when given empty array", async () => {
    fetchWithToken.mockResolvedValue([]);
    const { getByText, getByLabelText, queryByText } = render(<HistogramOfMessagesByUser />);
    const selectSearchUser = getByLabelText("Enter a user");
    userEvent.type(selectSearchUser, "Test User");
    const goButton = getByText("Go");
    userEvent.click(goButton);
    await waitFor(() => {
      expect(queryByText("Message Count")).toBeInTheDocument();
    })
  })
});
