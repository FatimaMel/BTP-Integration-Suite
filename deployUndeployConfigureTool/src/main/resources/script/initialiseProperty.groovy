import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {
    // Initialize the property if it doesn't exist
    if (message.getProperty("cumulativeErrorMessages") == null || message.getProperty("ErrorMessage") == null) {
        message.setProperty("cumulativeErrorMessages", []);
        message.setProperty("ErrorMessage", []);
    }
    
    return message;
}
