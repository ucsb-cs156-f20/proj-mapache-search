import React from "react";
import { render } from "@testing-library/react";
import CountMessagesByUser from "main/pages/AnalyzeMessageData/CountMessagesByUser";

describe("CountMessagesByUser tests", () => {
    test("renders without crashing", () => {
      render(<CountMessagesByUser/>);
    });
});
