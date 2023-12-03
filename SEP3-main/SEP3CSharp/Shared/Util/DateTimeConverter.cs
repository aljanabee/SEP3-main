namespace Shared.Util;

public static class DateTimeConverter {
    public static DateTime? UnixTimeStampToDateTime(long unixTimeStamp) {
        // Unix timestamp 0 will be interpreted as null. 
        if (unixTimeStamp == 0) return null;

        DateTime dateTime = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
        dateTime = dateTime.AddSeconds(unixTimeStamp).ToLocalTime();
        return dateTime;
    }

    public static long DateTimeToUnixTimeStamp(DateTime? dateTime) {
        // DateTime null will be intepreted as unix timestamp 0.
        return dateTime == null ? 0 : ((DateTimeOffset)dateTime).ToUnixTimeSeconds();
    }
}
