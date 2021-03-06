import React from 'react';
import { MemoryRouter } from "react-router-dom";

import { PureNavbar } from "main/components/Nav/AppNavbar";

export default {
  title: 'components/Nav/AppNavbar',
  component: PureNavbar
};

const Template = (args) => (
  <MemoryRouter>
    <PureNavbar {...args} />
  </MemoryRouter>
);

export const Guest = Template.bind({});
Guest.args = {
  channelPages: [
    { link: "/member/channels", name: "List Channels" },
  ],

  adminPages: [
    { link: "/admin", name: "Maintain Admins", },
    { link: "/admin/slackUsers", name: "Slack Users", },
    { link: "/admin/teams", name: "Manage Teams", },
    { link: "/admin/students", name: "Manage Students", },
    { link: "/admin/searchInfo", name: "Search Information", },
  ],

  searchPages: [
    { link: "/member/messages/search", name: "Slack Search" },
  ],

  dataPages: [
    { link: "/member/analyzemessages/reactions", name: "Analyze Reactions", },
    { link: "/member/analyzemessages/countmessages", name: "Count Messages By User", },
    { link: "/member/analyzemessages/messagehistogram", name: "Histogram of Messages for a User", },
    { link: "/member/analyzemessages/searchmessages", name: "Search Messages By User", },
  ],
};

export const Admin = Template.bind({});
Admin.args = {
  isAdmin: true,
  ...Guest.args
};

export const Member = Template.bind({});
Member.args = {
  isMember: true,
  ...Guest.args
};
