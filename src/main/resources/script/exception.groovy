import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    def body = message.getBody(String);
    def artifactId = message.getProperty("Id");
    def responseCode = message.getHeader("CamelHttpResponseCode", Integer);
    
    def errorMessages = message.getProperty("ErrorMessage") as List<String>;

    if (responseCode == 404) {
        def response = [
            status: "Completed",
            message: "Artifact ${artifactId} is already undeployed or 404 - Not found."
        ];
        errorMessages << response;
    } else if (responseCode != 200) {
        def response = [
            status: "Failed",
            message: "Failed to undeploy artifact ${artifactId}: ${responseCode.message}"
        ];
        errorMessages << response;
    }
    
    message.setBody(JsonOutput.toJson(errorMessages));
    message.setProperty("ErrorMessage", errorMessages);
    
    return message;
}
