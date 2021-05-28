import React from 'react';
import { render } from "@testing-library/react";
import MessageTableReaction from "main/components/ChannelMessageReaction/MessageTableReaction.js";
import useSWR from "swr";
jest.mock("swr");
jest.mock("react-router-dom", () => {
    return {
        'useParams': jest.fn(),
    };
});
describe("MessageTableReactions tests", () => {

    test("it renders without crashing", () => {
        useSWR.mockReturnValue({
            data: []
        });
        render(<MessageTableReaction/>);
    });

    test("row.message_reactions is not null", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const testMessages = [{
            user: "test-user",
            text: "test-text",
            message_reactions: [{
                count: 1,
                name: "test-name"
            }]
        }];
        const testReaction = "test-name";
        const { getByText } = render(<MessageTableReaction messages = {testMessages} reaction = {testReaction}/>);
        const testUser = getByText(/test-user/);
        const testText = getByText(/test-text/);
        const testCount = getByText(/1/);
        expect(testUser).toBeInTheDocument();
        expect(testText).toBeInTheDocument();
        expect(testCount).toBeInTheDocument();
    });

    test("user id is replaced with username", () => {
        useSWR.mockReturnValue({
            data: [{
                id: "U017218J9B3",
                real_name: "Test Person"
            }]
        });
        const testMessages = [{
            user: "U017218J9B3",
            text: "<@U017218J9B3> has joined the channel",
            message_reactions: [{
                count: 1,
                name: "test-name"
            }]
        }];
        const testReaction = "test-name";
        const { getByText } = render(<MessageTableReaction messages = {testMessages} reaction = {testReaction}/>);
        const testUser = getByText(/Test Person/);
        const testText = getByText(/@Test Person has joined the channel/);
        expect(testUser).toBeInTheDocument();
        expect(testText).toBeInTheDocument();
    });

    test("Unembedded https links are clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const testMessages = [{
            user: "test-user",
            text: "Office hours at <https://ucsb.zoom.us/j/89220034995?pwd=VTlHNXJpTVgrSEs5QUtlMDdqMC9wQT09>",
            message_reactions: [{
                count: 1,
                name: "test-name"
            }]
        }];
        const testReaction = "test-name";
        const { getByText } = render(<MessageTableReaction messages = {testMessages} reaction = {testReaction}/>);
        const linkElement = getByText(/https:\/\/ucsb.zoom.us\/j\/89220034995\?pwd=VTlHNXJpTVgrSEs5QUtlMDdqMC9wQT09/);
        expect(linkElement.href).toEqual("https://ucsb.zoom.us/j/89220034995?pwd=VTlHNXJpTVgrSEs5QUtlMDdqMC9wQT09");
    }); 
});


