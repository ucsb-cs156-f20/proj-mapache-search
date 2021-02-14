import React from "react";
import { Jumbotron } from "react-bootstrap";
import {useAuth0} from "@auth0/auth0-react";

const Home = () => {
    const { isAuthenticated: _isAuthenticated } = useAuth0();

    return (
            <Jumbotron>
                <div className="text-left">
                    <h5>Welcome to the Mapache Search!</h5>
                    <p>This is where your home page content goes
                    </p>
                </div>
            </Jumbotron>
    );
};

export default Home;
