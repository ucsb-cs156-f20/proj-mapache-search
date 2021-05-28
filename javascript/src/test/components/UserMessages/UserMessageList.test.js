import React from "react";
import { render } from "@testing-library/react";
import UserMessageList from "main/components/UserMessages/UserMessageList";
import useSWR from "swr";
jest.mock("swr");
jest.mock("react-router-dom", () => {
    return {
        'useParams': jest.fn(),
    };
});

describe("UserMessageList tests", () => {
    test("it renders without crashing", () => {
        useSWR.mockReturnValue({
            data: []
        });
        render(<UserMessageList messages={[]} channel={""} />);
    });

    test("Default message display", () => {
        useSWR.mockReturnValue({
            data: [{
                id: "U017218J9B3",
                real_name: "Test Person"
            }]
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "Someone said U017218J9B3",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const nameElement = getByText(/Someone said U017218J9B3/);
        expect(nameElement).toBeInTheDocument();
    });

    test("Username not found", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<@U017218J9B3> has joined the channel",
            "channel": "section-6pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        setTimeout(function (){
            const nameElement = getByText(/@U017218J9B3/);
            expect(nameElement).toBeInTheDocument();
        }, 500)
    });

    test("Channel tags begin with @ and are bolded", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<!channel> Hello channel",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const bracketElement = getByText(/@channel/);
        expect(bracketElement).toHaveStyle("font-weight: bold");
        
    });

    test("Channel links are clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "Please post in <#C01K1CR63MX|help-jpa02>",
            "channel": "section-6pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const linkElement = getByText   (/#help-jpa02/);
        expect(linkElement.href).toEqual("http://localhost/member/listViewChannels/help-jpa02");
        
    });  

    test("Bracketed text that is not an http or mailto link is not clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<!channel> This is an announcement",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const bracketElement = getByText(/@channel/);
        expect(bracketElement.getAttribute("href")).toEqual(null);
        
    });

    test("Brackets removed from elements that are not links", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<!channel> This is an announcement <testing>",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {queryByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        let bracketElement = queryByText(/<testing>/);
        expect(bracketElement).toEqual(null);
        bracketElement = queryByText(/testing/);
        expect(bracketElement).toBeInTheDocument();
        
    });

    test("Displays username in message", () => {
        useSWR.mockReturnValue({
            data: [{
                id: "U017218J9B3",
                real_name: "Test Person"
            }]
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<@U017218J9B3> has joined the channel",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        setTimeout(function (){
            const nameElement = getByText(/Test Person has joined the channel/);
            expect(nameElement).toBeInTheDocument();
        }, 500)

    });

    test("Embedded email links are clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "My email is <mailto:test@ucsb.edu|this email>",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const linkElement = getByText(/this email/);
        expect(linkElement.href).toEqual("mailto:test@ucsb.edu");
        
    });

    test("Unembedded https links are clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "Office hours at <https://www.youtube.com/watch?v=Rgx8dpiPwpA>",
            "channel": "section-7pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const linkElement = getByText(/https:\/\/www.youtube.com\/watch\?v=Rgx8dpiPwpA/);
        expect(linkElement.href).toEqual("https://www.youtube.com/watch?v=Rgx8dpiPwpA");
        
    });

    test("Unembedded phone links are clickable", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "Call me at <tel:+01234567890>",
            "channel": "section-6pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        const linkElement = getByText("tel:+01234567890");
        expect(linkElement.href).toEqual("tel:+01234567890");
        
    });

    test("User tags are styled using the correct css class", () => {
        useSWR.mockReturnValue({
            data: []
        });
        const exampleMessage = {
            "type": "message",
            "subtype": "channel_join",
            "ts": "1594143066.000200",
            "user": "U017218J9B3",
            "text": "<@U017218J9B3> has joined the channel",
            "channel": "section-6pm",
            "user_profile": {
                "real_name": "Test Person"
            }
        }
        const {getByText} = render(<UserMessageList messages={[exampleMessage]}/>);
        setTimeout(function () {
            const userTag = getByText(/@Test Person/);
            expect(userTag).toHaveClass("user-tag");
        }, 500)
    });
});