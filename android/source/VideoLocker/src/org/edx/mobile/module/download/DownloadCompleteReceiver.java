package org.edx.mobile.module.download;

import org.edx.mobile.model.IVideoModel;
import org.edx.mobile.model.db.DownloadEntry;
import org.edx.mobile.model.download.NativeDownloadModel;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.analytics.SegmentFactory;
import org.edx.mobile.module.analytics.SegmentTracker;
import org.edx.mobile.module.db.DataCallback;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.module.storage.IStorage;
import org.edx.mobile.module.storage.Storage;
import org.edx.mobile.util.LogUtil;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    protected IStorage storage;

    @Override
    public void onReceive(final Context context, Intent data) {
        try {
            initDB(context);
            if (data != null && data.hasExtra(DownloadManager.EXTRA_DOWNLOAD_ID)) {
                long id = data.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id != -1) {
                    LogUtil.log(getClass().getName(), 
                            "recieved download notification for id: " + id);

                    // check if download was SUCCESSFUL
                    IDownloadManager dm = DownloadFactory.getInstance(context);
                    NativeDownloadModel nm = dm.getDownload(id);

                    if (nm == null || nm.status != DownloadManager.STATUS_SUCCESSFUL) {
                        LogUtil.log(getClass().getName(), 
                                "download seems failed or cancelled for id : " + id);
                        return;
                    } else {
                        LogUtil.log(getClass().getName(), 
                                "download successful for id : " + id);
                    }

                    // mark download as completed
                    storage.markDownloadAsComplete(id, new DataCallback<IVideoModel>() {
                        @Override
                        public void onResult(IVideoModel result) {
                            if(result!=null){
                                DownloadEntry download = (DownloadEntry) result;
                                
                                ISegment segIO = SegmentFactory.getInstance(context, 
                                        new SegmentTracker(context));
                                segIO.trackDownloadComplete(download.videoId, download.eid, 
                                        download.lmsUrl);

                                // update count of downloaded videos
                                // store user's data in his own preference file, so as to keep it unique
                                PrefManager p = new PrefManager(context, download.username);
                                long count = p.getLong(PrefManager.Key.COUNT_OF_VIDEOS_DOWNLOADED);
                                if (count < 0) {
                                    count = 0;
                                }
                                count ++;
                                p.put(PrefManager.Key.COUNT_OF_VIDEOS_DOWNLOADED, count);
                            }
                        }

                        @Override
                        public void onFail(Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initDB(Context context) {
        storage = new Storage(context);
    }


}
