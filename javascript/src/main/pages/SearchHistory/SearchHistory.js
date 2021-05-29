import React from "react";
import BootstrapTable from 'react-bootstrap-table-next';
import useSWR from "swr";
import {useAuth0} from "@auth0/auth0-react";
import {fetchWithToken} from "main/utils/fetch";

const SearchHistory = () => {
    const columns = [{
        dataField: 'UserID',
        text: 'UserID',
        sort: true
    } , {
        dataField: "searchTerm",
        text: 'Searchquery',
        sort: true
    },{
       dataField: "timestamp" ,
       text: 'Timestamp' ,
       sort: true
    }];

    const { getAccessTokenSilently: getToken } = useAuth0();
    
    const { data: usersearch } = useSWR(["/api/members/searchhistory/allusersearches", getToken], fetchWithToken);

    return (
        <div>
            <h1>Show Search History</h1>
            <BootstrapTable keyField='id' data={usersearch} columns={columns} />
        </div>
    );
};
export default SearchHistory;
