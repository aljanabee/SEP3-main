namespace BlazorClient.Util;

public interface INotifierService {
    void NotifySuccess(string description, string summary="Success", double duration=-1);
    void NotifyError(string description, string summary="Error", double duration=-1);
    void NotifyInfo(string description, string summary="Info", double duration=-1);
    void NotifyWarning(string description, string summary="Warning", double duration=-1);
}
