package com.itsolution.smsapp.models;

public class SMS {
    String time;
    String GroupName;
    int SmsCount;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public int getSmsCount() {
        return SmsCount;
    }

    public void setSmsCount(int smsCount) {
        SmsCount = smsCount;
    }

    @Override
    public String toString() {
        return "SMS{" +
                "time='" + time + '\'' +
                ", GroupName='" + GroupName + '\'' +
                ", SmsCount=" + SmsCount +
                '}';
    }
}
