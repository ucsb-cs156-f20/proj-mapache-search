import React from "react";
import TimeFormatter from "./time"
import {useAuth0} from "@auth0/auth0-react";
import useSWR from "swr";
import {fetchWithToken} from "../../utils/fetch";

const replaceMessage = (text, slackUsers) => {
    return text.replace(/<@([A-Z0-9]{11})>/g, (_,userId) => {
        if(slackUsers != null) {
            for(let i = 0; i < slackUsers.length; i++) {
                if(slackUsers[i].id === userId) {
                    return "@" + slackUsers[i].real_name;
                }
            }
        }
        return "@" + userId;
    })
}

const replaceLink = (text) => {
    var link = /<(.*?)>/g;
    return text.replace(link, '<a href = "$1">$1</a>')
}

const createMarkup = (text, slackUsers) => {
    text = replaceMessage(text, slackUsers)
    text = replaceLink(text)
    return {
        __html: text
    }
}

export default ({ messages, _channel }) => {
    const { getAccessTokenSilently: getToken } = useAuth0();
    const {data: slackUsers} = useSWR([`/api/slackUsers`, getToken], fetchWithToken);
    return (
        <div style={{textAlign: "left", marginTop: 20}}>
            {
                messages.map((el) => {
                    return (
                        <div
                            key={el?.ts}
                            style={{
                            borderRadius: 10,
                            paddingTop: 15,
                            paddingLeft: 10,
                            paddingRight: 10,
                            borderStyle: "solid",
                            borderColor: "#ccc",
                            marginTop: 10
                        }}>
                            <strong>{el?.user_profile?.real_name || el.user}</strong>
                            <label style={{marginLeft: 10}}>{TimeFormatter(el?.ts)}</label>
                            <p dangerouslySetInnerHTML={createMarkup(el?.text, slackUsers)}></p>
                        </div>
                    )
                })
            }
        </div>
    );
};
