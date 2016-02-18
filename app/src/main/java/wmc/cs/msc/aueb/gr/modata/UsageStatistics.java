package wmc.cs.msc.aueb.gr.modata;

/**
 * Created by ellen on 2/12/16.
 */
public class UsageStatistics {
    String mName;
    String mConnType;
    double mRecvKBPs;
    double mSendKBPs;
    double mRecvBytes;
    double mSendBytes;
    double mMRecvBytes;
    double mMSendBytes;
    double mTotalBytes;
    double mConsumption;
    public long mLastSend;
    public long mLastRcvd;
    long mWifiTime;
    long mMobileTime;
    long mMonitorStart;
    long mMonitorDuration;
    double mCPUTime;
    int mBatteryLevel;

    Object mLock = new Object();

    public UsageStatistics(String appName) {
        mName = appName;
        mRecvKBPs =
         mSendKBPs =
         mRecvBytes =
         mSendBytes =
         mMRecvBytes =
         mMSendBytes =
         mConsumption =
         mTotalBytes =
         mCPUTime = 0.0f;

        mWifiTime = mMobileTime =
         mMonitorStart =
         mLastSend =
         mLastRcvd =
         mMonitorDuration = 0;

        mBatteryLevel = 0;
    }

    @Override
    public String toString() {
        StringBuilder l_Ret = new StringBuilder("");

        synchronized (mLock) {

            l_Ret.append(mName).append("\n");
            l_Ret.append("WBytes Rcvd ").append(mRecvBytes).append("\n");
            l_Ret.append("WBytes Send ").append(mSendBytes).append("\n");
            l_Ret.append("MBytes Rcvd ").append(mMRecvBytes).append("\n");
            l_Ret.append("MBytes Send ").append(mMSendBytes).append("\n");

        }
        return l_Ret.toString();
    }

    public String toFile() {
        StringBuilder l_Ret = new StringBuilder();

        synchronized (mLock) {
            l_Ret.append(mName).append(",");
            l_Ret.append(mRecvBytes).append(",");
            l_Ret.append(mSendBytes).append(",");
            l_Ret.append(mMRecvBytes).append(",");
            l_Ret.append(mMSendBytes).append(",");
            l_Ret.append((double) mMonitorDuration / 1000.0).append(",");
            l_Ret.append(mBatteryLevel).append("");
        }

        return l_Ret.toString();
    }
    @Override
    public int hashCode() { synchronized (mLock) {
            return mName.hashCode();
        }
    }

    public long getmLastRcvd() {
        return mLastRcvd;
    }

    public void setmLastRcvd(long mLastRcvd) {
        this.mLastRcvd = mLastRcvd;
    }

    public long getmLastSend() {
        return mLastSend;
    }

    public void setmLastSend(long mLastSend) {
        this.mLastSend = mLastSend;
    }

    public double getmTotalBytes() {
        return mTotalBytes;
    }

    public void setmTotalBytes(double mTotalBytes) {
        this.mTotalBytes = mTotalBytes;
    }

    public long getmMonitorDuration() { synchronized (mLock) {
            return mMonitorDuration;
        }
    }

    public void setmMonitorDuration(long mMonitorDuration) { synchronized (mLock) {
            this.mMonitorDuration = mMonitorDuration;
        }
    }

    public long getmMonitorStart() { synchronized (mLock) {
            return mMonitorStart;
        }
    }

    public void setmMonitorStart(long mMonitorStart) { synchronized (mLock) {
        this.mMonitorStart = mMonitorStart;
        }
    }

    public String getmName() { synchronized (mLock) {
            return mName;
        }
    }

    public void setmName(String mName) { synchronized (mLock) {
            this.mName = mName;
        }
    }

    public double getmRecvKBPs() { synchronized (mLock) {
            return mRecvKBPs;
        }
    }

    public void setmRecvKBPs(double mRecvKBPs) { synchronized (mLock) {
            this.mRecvKBPs = mRecvKBPs;
        }
    }

    public double getmSendKBPs() { synchronized (mLock) {
            return mSendKBPs;
        }
    }

    public void setmSendKBPs(double mSendKBPs) { synchronized (mLock) {
            this.mSendKBPs = mSendKBPs;
        }
    }

    public double getmRecvBytes() { synchronized (mLock) {
            return mMRecvBytes;
        }
    }

    public void setmRecvBytes(double mRecvBytes) { synchronized (mLock) {
            this.mRecvBytes = mRecvBytes;
        }
    }

    public double getmMRecvBytes() { synchronized (mLock) {
        return mMRecvBytes;
    }
    }

    public void setmMRecvBytes(double mMRecvBytes) { synchronized (mLock) {
        this.mMRecvBytes = mMRecvBytes;
    }
    }

    public double getmSendBytes() { synchronized (mLock) {
            return mSendBytes;
        }
    }

    public void setmSendBytes(double mSendBytes) { synchronized (mLock) {
            this.mSendBytes = mSendBytes;
        }
    }

    public double getmMSendBytes() { synchronized (mLock) {
        return mMSendBytes;
    }
    }

    public void setmMSendBytes(double mMSendBytes) { synchronized (mLock) {
        this.mMSendBytes = mMSendBytes;
    }
    }

    public double getmConsumption() { synchronized (mLock) {
            return mConsumption;
        }
    }

    public void setmConsumption(double mConsumption) { synchronized (mLock) {
            this.mConsumption = mConsumption;
        }
    }

    public long getmWifiTime() { synchronized (mLock) {
            return mWifiTime;
        }
    }

    public void setmWifiTime(long mWifiTime) { synchronized (mLock) {
            this.mWifiTime = mWifiTime;
        }
    }

    public long getmMobileTime() { synchronized (mLock) {
            return mMobileTime;
        }
    }

    public void setmMobileTime(long mMobileTime) { synchronized (mLock) {
            this.mMobileTime = mMobileTime;
        }
    }

    public double getmCPUTime() { synchronized (mLock) {
            return mCPUTime;
        }
    }

    public void setmCPUTime(double mCPUTime) { synchronized (mLock) {
            this.mCPUTime = mCPUTime;
        }
    }

    public String getmConnType() { synchronized (mLock) {
            return mConnType;
        }
    }

    public void setmConnType(String mConnType) { synchronized (mLock) {
            this.mConnType = mConnType;
        }
    }

    public int getmBatteryLevel() { synchronized (mLock) {
            return mBatteryLevel;
        }
    }

    public void setmBatteryLevel(int mBatteryLevel) { synchronized (mLock) {
            this.mBatteryLevel = mBatteryLevel;
        }
    }
}
