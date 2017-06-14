package eionet.gdem.qa.engines;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FMEApi implements Serializable{
        private fmeStatus executionStatus;
        private String feedbackContent;

        public FMEApi(){
        }

        public fmeStatus getExecutionStatus() {
            return executionStatus;
        }

        public void setExecutionStatus(fmeStatus executionStatus) {
            this.executionStatus = executionStatus;
        }

        public void setFeedbackContent(String feedbackContent) {
            this.feedbackContent = feedbackContent;
        }

        public String getFeedbackContent() {
            return feedbackContent;
        }

    public class fmeStatus implements Serializable{
        private Integer statusId;
        private String statusName;

        public fmeStatus() {
        }

        public Integer getStatusId() {
            return statusId;
        }

        public void setStatusId(Integer statusId) {
            this.statusId = statusId;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }
    }
}
