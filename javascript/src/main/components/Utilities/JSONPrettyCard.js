import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { Card } from 'react-bootstrap'
import JSONPretty from 'react-json-pretty';
const JSONPrettyCard = ({expression, value}) => {
    return(
        <Fragment>
            <Card id={`JSONPrettyPanel-${expression}`}>
                <Card.Body>
                    <Card.Title><code>{expression}</code></Card.Title>
                    <JSONPretty data={value} />
                </Card.Body>
            </Card>
        </Fragment>
    ); 
};
export default JSONPrettyCard;