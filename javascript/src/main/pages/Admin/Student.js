import React from "react";
import useSWR from "swr";
import { StudentCSVButton } from "./StudentCSVButton";
import { fetchWithToken } from "main/utils/fetch";
import { useAuth0 } from "@auth0/auth0-react";
import Loading from "main/components/Loading/Loading";
import StudentTable from "main/components/Students/StudentTable"
import AddStudent from "main/components/Students/AddStudent"

const Students = () => {
  const { getAccessTokenSilently: getToken } = useAuth0();
  // retrieves data 
  const { data: studentList, error, mutate: mutateStudents } = useSWR(
    ["/api/students", getToken],
    fetchWithToken
  );

  if (error) {
    return (
      <h1>We encountered an error; please reload the page and try again.</h1>
    );
  }
  if (!studentList) {
    return <Loading />;
  }

  const createStudent = async (item, id) => {
    try {
      await fetchWithToken(`/api/students`, getToken, {
        method: "POST",
        headers: {
          "content-type": "application/json",
        },
        body: JSON.stringify(item),
      });
      await mutateStudents();
    } catch (err) {
      console.log("Caught error from create student");
    }
  };

  const updateStudent = async (item, id) => {
    try {
      await fetchWithToken(`/api/students/${id}`, getToken, {
        method: "PUT",
        headers: {
          "content-type": "application/json",
        },
        body: JSON.stringify(item),
      });
      await mutateStudents();
    } catch (err) {
      console.log("Caught error from update students");
    }
  };

  const deleteStudent = async (id) => {
    try {
      await fetchWithToken(`/api/students/${id}`, getToken, {
        method: "DELETE",
        headers: {
          "content-type": "application/json",
        },
        noJSON: true,
      });
      await mutateStudents();
    } catch (err) {
      console.log("Caught error from delete students");
    }
  };

  const uploadCSV = async (file) => {
    const data = new FormData();
    data.append("csv", file);
    await fetchWithToken('/api/students/upload', getToken, {
      method: "POST",
      body: data
    });
    await mutateStudents();
  };

  return (
    <>
        <h1>
            Upload CSV File of Students
        </h1>
        <p>
          Make sure that the uploaded CSV contains a header
        </p>
        <StudentCSVButton addTask={uploadCSV}/>
        {/* <AddStudent addStudent={createStudent}/> */}
        <StudentTable students={studentList} deleteStudent={deleteStudent}/>
    </>
  );
};

export default Students;