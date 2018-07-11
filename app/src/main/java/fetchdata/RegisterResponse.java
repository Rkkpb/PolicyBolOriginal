package fetchdata;

public class RegisterResponse {

    private boolean success = false;

    private String message = "";

    public void setResult(String result) {
        this.result = result;
    }

    private String result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getresult() {
        return result;
    }
}