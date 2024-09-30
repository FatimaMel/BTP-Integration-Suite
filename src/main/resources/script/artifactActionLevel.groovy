import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper;

def Message processData(Message message) {
    // Retrieve the body of the message
    def body = message.getBody(java.io.Reader);

    // Parse the XML
    def xml = new XmlSlurper().parse(body);

    def rootAction = message.getProperty('rootAction');

    // Determine the value of ArtifactAction
    def ArtifactAction = xml.Action.text().trim();

    // Set newArtifactAction based on the value of ArtifactAction
    def newArtifactAction = ArtifactAction ? ArtifactAction : rootAction;

    message.setProperty('newArtifactAction', newArtifactAction.toLowerCase());

    return message;
}
