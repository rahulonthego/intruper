/**
 * @author rahul
 * @created 03/10/2018
 */
package com.zomasystems.persistence.google;

import org.springframework.context.annotation.Configuration;

@Configuration("google.bigtable")
public class BigTableConfiguration {

    private String projectId;
    private String instanceId;
    private String membersTable;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getMembersTable() {
        return membersTable;
    }

    public void setMembersTable(String membersTable) {
        this.membersTable = membersTable;
    }
}
