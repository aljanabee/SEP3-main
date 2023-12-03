using Radzen;

namespace BlazorClient.Util; 

public class NotifierService : INotifierService {
	private readonly NotificationService _notificationService;

    public NotifierService(NotificationService notificationService) {
        _notificationService = notificationService;
    }

    public void NotifyError(string description, string summary = "Error", double duration = -1) {
        NotificationMessage message = new NotificationMessage
        {
            Severity = NotificationSeverity.Error,
            Detail = description,
            Summary = summary,
            Duration = duration
        };

        _notificationService.Notify(message);
    }

    public void NotifyInfo(string description, string summary = "Info", double duration = -1) {
        NotificationMessage message = new NotificationMessage {
            Severity = NotificationSeverity.Info,
            Detail = description,
            Summary = summary,
            Duration = duration
        };

        _notificationService.Notify(message);
    }

    public void NotifySuccess(string description, string summary = "Success", double duration = -1) {
        NotificationMessage message = new NotificationMessage {
            Severity = NotificationSeverity.Success,
            Detail = description,
            Summary = summary,
            Duration = duration
        };

        _notificationService.Notify(message);
    }

    public void NotifyWarning(string description, string summary = "Warning", double duration = -1) {
        NotificationMessage message = new NotificationMessage {
            Severity = NotificationSeverity.Warning,
            Detail = description,
            Summary = summary,
            Duration = duration
        };

        _notificationService.Notify(message);
    }
}
