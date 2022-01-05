import React, { useContext } from 'react'
import { useResource } from 'rest-hooks';
import { GlobalContext } from '../../components/GlobalContextProvider';
import SubmissionsResource from '../../resources/SubmissionsResource';
import { useQuery } from '../../webreceiver-utils';

function SubmissionsDetail() {
    const queryMap = useQuery();
    const taskId = queryMap?.["taskId"] || "";
    // const submission: SubmissionsResource = useResource(
    //     SubmissionsResource.detail(),
    //     { id: taskId }
    // );
    return (
        <div>
            {taskId}
        </div>
    )
}

export default SubmissionsDetail
